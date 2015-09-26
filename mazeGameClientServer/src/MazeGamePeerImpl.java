import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MazeGamePeerImpl implements MazeGamePeer {
	
    private volatile int gameStatus = GAME_INIT;

    private Map<Integer, MazeGamePeer> peers;
	
    private int playerNum = 0;

    // test registry
//    private static final int REGISTRY_PORT = 8888;

    private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    private Game game;
    
    private int playerId;
    
    private Thread ioThread;

    private GameMessage gameMessage;
    
    /** if the current peer is primary, rmi request is not needed */
	private boolean isPrimaryServer = false;

    /** if the current peer is backup, promote itself to primary when primary dies */
    //private boolean isBackupServer;

    private MazeGamePeer primaryServer;
    
    private MazeGamePeer backupServer = null;
    
    int primarySeverId = -1, backupServerId = -1;
	
    private class GameInitializeTask implements Runnable {
        @Override
        public void run() {
//            System.out.println("game start");
            game.initializeGameState();
            gameStatus = GAME_START;
            primarySeverId = playerId;
            backupServerId = playerId + 1;
            backupServer = peers.get(backupServerId);
            try {
                backupServer.setAsBackupServer(primarySeverId, game.getGameState(), peers);
                // notifyGameStart is non-blocking.
                for(Integer id : peers.keySet()) {
                    if(id != playerId) {
                        peers.get(id).notifyStart(id, game.createMsgForPlayer(id));
                    }
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            // starts a thread to periodically ping backup server
            executor.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    if(backupServer != null) {
                        try {
                            backupServer.heartBeat();
                        } catch (RemoteException e) {
                            // backup server has died
                            onBackupServerDie();
                        }
                    }
                }
            }, 2, 2, TimeUnit.SECONDS);
            // wake up the current primary server
            synchronized (MazeGamePeerImpl.this) {
                MazeGamePeerImpl.this.notifyAll();
            }
        }
    }
    
    private class GameEndTask implements Runnable {
        @Override
        public void run() {
            for(Integer id : peers.keySet()) {
                if(id != playerId) {
                    try {
                        peers.get(id).notifyEnd(game.createGameOverMsgForPlayer(id));
                    } catch (RemoteException e) {
                        System.err.println("Peer " + id + " has died");
//                        e.printStackTrace();
                    }
                }
            }
            // interrupt the current io thread on primary server
            ioThread.interrupt();
        }
    }

	public MazeGamePeerImpl(int dimension, int numOfTreasure, Thread ioThread) {
        game = new Game(numOfTreasure, dimension);
        this.ioThread = ioThread;
    }
	
	public MazeGamePeerImpl(Thread ioThread) {
		this.ioThread = ioThread;
	}

    public void setAsPrimaryServer() {
        peers = new HashMap<>();
        playerId = playerNum;
        isPrimaryServer = true;
        addPeer(this);
    }

    public void setPrimaryServer(MazeGamePeer peer) {
        primaryServer = peer;
    }

    private void addPeer(MazeGamePeer peer) {
        peers.put(playerNum, peer);
        game.addPlayer(playerNum, new Player());
        playerNum++;
    }

    private boolean isValidInput(String input) {
        // check if input is valid
        if(input.length() != 1)
            return false;
        char ch = input.toUpperCase().charAt(0);
        return (ch == 'W' || ch == 'S' || ch == 'A' || ch == 'D' || ch == ' ');
    }
    
    public void gaming() {
        System.out.println("************Game Start**************");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while(!isGameEnd()) {
            // when game is not end, print out game states and wait for user input
            if(isPrimaryServer) {
                System.out.println(game.createMsgForPlayer(playerId));
            } else {
                System.out.println(gameMessage.toString());
            }
            try {
                while(!br.ready()) {
                    TimeUnit.MILLISECONDS.sleep(200);
                }
                String input = br.readLine();
                if(input != null && isValidInput(input)) {
                    char dir = Character.toUpperCase(input.charAt(0));
                    if(isPrimaryServer) {
                        game.playerMove(playerId, dir);
                        backupServer.updateBackupState(game.getGameState());
                        if(game.isGameOver()) {
                            gameStatus = GAME_END;
                            executor.execute(new GameEndTask());
                        }
                    } else {
                        gameMessage = primaryServer.move(playerId, dir);  // a blocking operation
                        if(gameMessage.isGameOver())
                            gameStatus = GAME_END;
                    }
                } else {
                    System.out.println("error input.");
                }
            } catch (RemoteException e) {
                System.err.println("primary server has crashed, try again");
            } catch (InterruptedException e) {
                System.err.println("received interruption, game end.");
            } catch (IOException e) {
                System.err.println("io error.");
            }
        }
        System.out.println("********Game End**********");
        if(isPrimaryServer) {
            System.out.println(game.createGameOverMsgForPlayer(playerId));
        } else {
            System.out.println(gameMessage.toString());
        }
    }
    
    public void shutDown() throws NoSuchObjectException {
        // properly shutdown program
//        System.out.println("Shut down");
        UnicastRemoteObject.unexportObject(this, true);
        executor.shutdownNow();
    }
    
    public synchronized boolean isGameStarted() {
        return (GAME_START == gameStatus);
    }

    public synchronized boolean isGameEnd() {
        return (GAME_END == gameStatus);
    }

    private boolean createNewBackupServer() {
        List<Integer> diedPeers = new ArrayList<>();
        boolean success = false;
        for(Integer id : peers.keySet()) {
            if(id != primarySeverId) {
                try {
                    if (peers.get(id).setAsBackupServer(primarySeverId, game.getGameState(), peers)) {
                        backupServerId = id;
                        backupServer = peers.get(backupServerId);
                        success = true;
                        break;
                    }
                } catch (RemoteException e) {
                    System.err.println("pick up a backup server that has died (peer: " + id + ")");
                    diedPeers.add(id);
                }
            }
        }
        // remove peers that have died
        for(Integer diedPeer : diedPeers) {
            peers.remove(diedPeer);
        }
        // TODO: synchronize the removed peers to backup server
        return success;
    }

    public void onPrimaryServerDie()  {//this method should only executed by backup
        System.out.println("Become Primary Server");
        //promote itself to primary
        peers.remove(primarySeverId);
        primarySeverId = playerId;
        isPrimaryServer = true;

        // set backupServer
        createNewBackupServer();

        //broadCast new primary
        for(Integer id : peers.keySet()) {
            if (id != playerId){
                try {
                    peers.get(id).notifyNewPrimary(this, game.createMsgForPlayer(id));
                } catch (RemoteException e) {
                    System.err.println("Peer :" + id + "has died");
                }
            }
        }

        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if(backupServer != null) {
                    try {
                        backupServer.heartBeat();
                    } catch (RemoteException e) {
                        // backup server has died
                        onBackupServerDie();
                    }
                }
            }
        }, 2, 2, TimeUnit.SECONDS);
    }

    public void onBackupServerDie() {
        //this method should only executed by primary
        // remove died backup server
        peers.remove(backupServerId);
        createNewBackupServer();
    }

    @Override
    public boolean joinGame(MazeGamePeer peer) throws RemoteException {
        if(gameStatus == GAME_INIT) {
            // The first player join in, notify game start in 20 seconds
            System.out.println("Peer " + playerNum + " join in");
            addPeer(peer);
            // TODO: change back to 20
            executor.schedule(new GameInitializeTask(), 20, TimeUnit.SECONDS);
            gameStatus = GAME_PENDING_START;
            return true;
        } else if (gameStatus == GAME_PENDING_START) {
            System.out.println("Peer " + playerNum + " join in");
            addPeer(peer);
            return true;
        } else {
            // game started, can't join anymore
            System.out.println("Game has started, refuse joining request.");
            return false;
        }
    }

    @Override
    public GameMessage move(int playerID, char dir) throws RemoteException {
        if(gameStatus == GAME_START) {
            if(game.playerMove(playerID, dir)) {
                if (game.isGameOver()) {
                    // game is over, send back game_over response and remove player
                    gameStatus = GAME_END;
                    GameMessage endMsg = game.createGameOverMsgForPlayer(playerID);
                    // notify other players
                    executor.execute(new GameEndTask());
                    return endMsg;
                }
                backupServer.updateBackupState(this.game.getGameState());//if player successfully moved, updated backup
            }
            return game.createMsgForPlayer(playerID);
        } else {
            // receive move request when game is over, send back game over message
            return game.createGameOverMsgForPlayer(playerID);
        }
    }

    @Override
    public void updateBackupState(GameState state) throws RemoteException {
        if (this.game == null){
            this.game = new Game(state);
        } else {
            this.game.setGameState(state);
        }
    }

    @Override
    public boolean setAsBackupServer(int primarySeverId, GameState state, Map<Integer, MazeGamePeer> peerRefs) throws RemoteException {
        //set client reference
        this.peers = peerRefs;
        game = new Game(state);
//        this.isBackupServer = true;
        this.primarySeverId = primarySeverId;
        System.err.println("become Backup Server");
        // start a new thread to call heartbeat
        executor.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    while(true) {
                        primaryServer.heartBeat();
                        Thread.sleep(2000);
                    }
                } catch (RemoteException e) {
                    // receive exception, the primary server has died
                    onPrimaryServerDie();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, 2, TimeUnit.SECONDS);
        return true;
    }

    @Override
    public void notifyNewPrimary(MazeGamePeer primary, GameMessage gameMessage) {
        this.primaryServer = primary;
        this.gameMessage = gameMessage;
        // TODO: may need to interrupt current blocked request
//    	System.out.println(serverMsg.toString());
    }

    @Override
    public synchronized void notifyStart(int id, GameMessage msg) throws RemoteException {
        gameStatus = GAME_START;
        this.playerId = id;
        this.gameMessage = msg;
        this.notifyAll();
    }

    @Override
    public synchronized void notifyEnd(GameMessage msg) throws RemoteException {
        gameStatus = GAME_END;
        gameMessage = msg;
        // interrupt the io thread
        ioThread.interrupt();
    }

    @Override
    public boolean heartBeat() throws RemoteException {
        return true;
    }

    public static void startAsHost(int dimension, int numOfTreasure, int port) {
        try {
            MazeGamePeerImpl maze = new MazeGamePeerImpl(dimension, numOfTreasure, Thread.currentThread());
            maze.setAsPrimaryServer();
            MazeGamePeer stub = (MazeGamePeer) UnicastRemoteObject.exportObject(maze, 0);
            // Bind the remote object's stub in the registry
            Registry rmiRegistry = LocateRegistry.createRegistry(port);
            rmiRegistry.rebind(MazeGamePeer.NAME, stub);
            System.out.println("Create new game, waiting for other players to join");
            //maze.setPlayer
            while(!maze.isGameStarted()) {
                synchronized (maze) {
                    maze.wait();
                }
            }
            maze.gaming();
            maze.shutDown();
        }  catch (RemoteException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void startAsClient(String host, int port) {
        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            MazeGamePeer server = (MazeGamePeer) registry.lookup(MazeGamePeer.NAME);
            MazeGamePeerImpl player = new MazeGamePeerImpl(Thread.currentThread());
            UnicastRemoteObject.exportObject(player, 0);
            boolean success = server.joinGame(player);
            if(success) {
                System.out.println("join game successfully, waiting for game start...");
                player.setPrimaryServer(server);
                while(!player.isGameStarted()) {
                    synchronized (player) {
                        player.wait();
                    }
                }
                player.gaming();
                // clean exit
                player.shutDown();
            } else {
                System.out.println("joining game failed.");
            }
        } catch (RemoteException e) {
            // join game failed
            e.printStackTrace();
        } catch (NotBoundException e) {
            // look up MazeServer failed
            e.printStackTrace();
        } catch (InterruptedException e) {
            // interrupted when waiting for game start
            e.printStackTrace();
        }
    }

    public static void errorArgs() {
        System.out.println("Usage note:");
        System.out.println("java MazeGamePeerImpl host <dimension> <numOfTreasure> <RMI port> for the first player");
        System.out.println("java MazeGamePeerImpl player <hostname> <RMI port> for the other players");
        System.exit(0);
    }

    public static void main(String[] args) {
        // set the rmi invocation timeout value
        System.setProperty("sun.rmi.transport.tcp.responseTimeout", "2000");
        if (args.length < 1) {
            errorArgs();
        }

        if (args[0].equals("host")) {
            //if it is the host, create primaryServer
            if(args.length != 4) {
                errorArgs();
            }
            startAsHost(Integer.valueOf(args[1]), Integer.valueOf(args[2]), Integer.valueOf(args[3]));
        } else {
            if(args.length != 3) {
                errorArgs();
            }
            startAsClient(args[1], Integer.valueOf(args[2]));
        }

    }
}
