import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;



public class MazeGamePeerImpl implements MazeGamePeer {
	
    private static Registry rmiRegistry;

    private final int GAME_INIT = 0;

    private final int GAME_PENDING_START = 1;

    private final int GAME_START = 2;

    private final int GAME_END = 3;

    private volatile int gameStatus = GAME_INIT;

    private Map<Integer, MazeGamePeer> peers = new HashMap<>();
	
    private int playerNum = 0;

    private static final int REGISTRY_PORT = 8888;

    private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    private Game game;
    
    private int playerId;
    
    private Thread ioThread;

    private ServerMsg serverMsg;
    
    private static boolean isFirstPlayer;

    /** if the current peer is primary, rmi request is not needed */
	private boolean isPrimaryServer;

    /** if the current peer is backup, promote itself to primary when primary dies */
    private boolean isBackupServer;

	
    private class GameInitializeTask implements Runnable {
        @Override
        public void run() {
            System.out.println("game start");
            gameStatus = GAME_START;
            // notifyGameStart is non-blocking.
            for(Integer id : peers.keySet()) {
                MazeGamePeer peer = peers.get(id);
                try {
                	peer.p2pNotifyStart(id, game.createMsgForPlayer(id));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    private class GameEndTask implements Runnable {
        @Override
        public void run() {
            System.out.println("game end");
            // notifyGameEnd
            for(Integer id : peers.keySet()) {
                MazeGamePeer peer = peers.get(id);
                try {
                    peer.p2pNotifyEnd(game.createMsgForPlayer(id));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            // TODO: revisit exit logic
            try {
                //rmiRegistry.unbind(MazeGameServer.NAME);
                UnicastRemoteObject.unexportObject(MazeGamePeerImpl.this, false);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            executor.shutdown();
        }
    }

	public MazeGamePeerImpl(int dimension, int numOfTreasure) {
        game = new Game(numOfTreasure, dimension);
    }
	
	public MazeGamePeerImpl(Thread thread) {
		this.ioThread = thread;
	}

	@Override
	public boolean joinP2PGame(MazeGamePeer peer) throws RemoteException {
        if(gameStatus == GAME_INIT) {
            // The first player join in, notify game start in 20 seconds
            peers.put(playerNum, peer);
            game.addPlayer(playerNum, new Player());
            playerNum++;
            // TODO: change back to 20
            executor.schedule(new GameInitializeTask(), 10, TimeUnit.SECONDS);
            gameStatus = GAME_PENDING_START;
            System.out.println("first client");
            return true;
        } else if (gameStatus == GAME_PENDING_START) {
            peers.put(playerNum, peer);
            game.addPlayer(playerNum, new Player());
            playerNum++;
            System.out.println("new client");
            return true;
        } else {
            // game started, can't join anymore
            System.out.println("join refuse");
            return false;
        }
	}

	@Override
	public ServerMsg p2pMove(int playerID, char dir) throws RemoteException {
        if(gameStatus == GAME_START) {
            if(game.playerMove(playerID, dir)) {
                if (game.isGameOver()) {
                    // game is over, send back game_over response and remove player
                    gameStatus = GAME_END;
                    ServerMsg endMsg = game.createGameOverMsgForPlayer(playerID);
                    // notify other players
                    executor.execute(new GameEndTask());
                    return endMsg;
                }
            }
            return game.createMsgForPlayer(playerID);
        } else {
            // receive move request when game is over, send back game over message
            return game.createGameOverMsgForPlayer(playerID);
        }
	}

    @Override
    public void updateBackupState(GameState state) throws RemoteException {

    }

    @Override
    public boolean setAsBackupServer(GameState state, Map<Integer, MazeGamePeer> peerRefs) throws RemoteException {
        return false;
    }

    @Override
    public void broadcastNewPrimary(MazeGamePeer primary, GameState updateState) {

    }

	@Override
	public synchronized void p2pNotifyStart(int id, ServerMsg msg) throws RemoteException {
		gameStatus = GAME_START;
        this.playerId = id;
        this.serverMsg = msg;

		System.err.println("Notification from server. ID = " + id
				+ " isPrimaryServer: " + isPrimaryServer);
        
        this.notifyAll();
	}

	@Override
	public synchronized void p2pNotifyEnd(ServerMsg msg) throws RemoteException {
        gameStatus = GAME_END;
        serverMsg = msg;
        // interrupt the io thread
        ioThread.interrupt();
	}

//
//	@Override
//	public void heartBeat() throws RemoteException {
//		// TODO Auto-generated method stub
//
//	}

    private boolean isValidInput(String input) {
        // TODO: check if input is valid
        if(input.length() != 1)
            return false;
        char ch = input.toUpperCase().charAt(0);
        if(ch == 'W' || ch == 'S' || ch == 'A' || ch == 'D' || ch == ' ')
            return true;
        else return false;
    }
    
    public void gaming(MazeGamePeer server) {
        // when game is not end, print out game states and wait for user input
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        while(!isGameEnd()) {
            System.out.println(serverMsg.toString());
            try {
                while(!br.ready()) {
                    TimeUnit.MILLISECONDS.sleep(200);
                }
                String input = br.readLine();
                if(input != null && isValidInput(input)) {
                    char dir = Character.toUpperCase(input.charAt(0));
                    serverMsg = server.p2pMove(playerId, dir);  // a blocking operation
                    if(serverMsg.isGameOver()) {
                        gameStatus = GAME_END;
                    }
                } else {
                    System.out.println("error input.");
                }
            } catch (IOException e) {
                System.err.println("io error.");
            } catch (InterruptedException e) {
                System.err.println("received interruption, game end.");
            }
        }
        System.out.println("********Game End********");
        System.out.println(serverMsg.toString());
    }
    
    public void shutDown() throws NoSuchObjectException {
        UnicastRemoteObject.unexportObject(this, true);
    }
    
    public synchronized boolean isGameStarted() {
        return (GAME_START == gameStatus);
    }

    public synchronized boolean isGameEnd() {
        return (GAME_END == gameStatus);
    }

	private void becomeBackupServer() {
		System.err.println("Player " + playerId + " is now Backup Server");
	}

    public static void startAsHost(int dimension, int numOfTreasure) {
        try {
            MazeGamePeerImpl server = new MazeGamePeerImpl(dimension, numOfTreasure);
            MazeGamePeer stub = (MazeGamePeer) UnicastRemoteObject.exportObject(server, 0);
            // Bind the remote object's stub in the registry
            rmiRegistry = LocateRegistry.createRegistry(REGISTRY_PORT);
            rmiRegistry.bind(MazeGamePeer.NAME, stub);
            System.out.println("Server ready");
        } catch (AccessException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (AlreadyBoundException e) {
            e.printStackTrace();
        }
    }

    public static void startAsClient(String host) {
        try {
            Registry registry = LocateRegistry.getRegistry(host, REGISTRY_PORT);
            MazeGamePeer server = (MazeGamePeer) registry.lookup(MazeGamePeer.NAME);
            MazeGamePeerImpl player = new MazeGamePeerImpl(Thread.currentThread());
            UnicastRemoteObject.exportObject(player, 0);
            boolean success = server.joinP2PGame(player);
            if(success) {
                System.out.println("join game successfully, waiting for game start...");
                while(!player.isGameStarted()) {
                    synchronized (player) {
                        player.wait();
                        System.out.println("player weak up");
                    }
                }
                System.out.println("game start!");
                player.gaming(server);
                // clean exit
                player.shutDown();
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

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage note:");
            System.err.println("java mazeGameP2PImpl host [N] [numTreasure] [hostAddr] for the first player");
            System.err.println("java mazeGameP2PImpl player [hostAddr] for the other players");
        }

        isFirstPlayer = args[0].equals("host") ? true : false;

        if (isFirstPlayer) {//if it is the host, create server
            startAsHost(Integer.valueOf(args[1]), Integer.valueOf(args[2]));
        } else {
            startAsClient(args[1]);
        }

    }
}
