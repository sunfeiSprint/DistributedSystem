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



public class mazeGameP2PImpl implements P2PMazeGameServerClient{
	
    private static Registry rmiRegistry;

    private final int GAME_INIT = 0;

    private final int GAME_PENDING_START = 1;

    private final int GAME_START = 2;

    private final int GAME_END = 3;

    private volatile int gameStatus = GAME_INIT;
	
	private Map<Integer, Player> players = new HashMap<Integer, Player>();
	
    private int playerNum = 0;

    private int numOfTreasure;

    private int dimension;

    private static final int REGISTRY_PORT = 8888;

    private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    private Game game;
    
    private int playerId;
    
    private Thread ioThread;

    private ServerMsg serverMsg;
    
    private static boolean isFirstPlayer;

	private boolean isPrimaryServer;
	
	private boolean isBackupServer;
	
    private class GameInitializeTask implements Runnable {
        @Override
        public void run() {
            System.out.println("game start");
            gameStatus = GAME_START;
            game = new Game(players, numOfTreasure, dimension);
            // notifyGameStart is non-blocking.
            for(Integer key : players.keySet()) {
                Player player = players.get(key);
                try {
                    player.notifyGameStart(game.createMsgForPlayer(player));
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
            for(Integer key : players.keySet()) {
                Player player = players.get(key);
                try {
                    player.notifyGameEnd(game.createMsgForPlayer(player));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            try {
                rmiRegistry.unbind(MazeGameServer.NAME);
                UnicastRemoteObject.unexportObject(mazeGameP2PImpl.this, false);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (NotBoundException e) {
                e.printStackTrace();
            }
            executor.shutdown();
        }
    }

	public mazeGameP2PImpl(int dimension, int numOfTreasure) {
        this.numOfTreasure = numOfTreasure;
        this.dimension = dimension;
	}
	
	public mazeGameP2PImpl(Thread thread) {
		this.ioThread = thread;
	}
	

	public static void main(String[] args) {
		if (args.length < 1) {
			System.err.println("Usage note:");
			System.err.println("java MGP2PImpl host [N] [hostAddr] for the first player");
			System.err.println("java MGP2PImpl player [hostAddr] for the other players");
		}
		
		isFirstPlayer = args[0].equals("host") ? true : false;

		P2PMazeGameServerClient serverClient = null;
		
		if (isFirstPlayer) {//if it is the host, create server
			int dimension = Integer.valueOf(args[0]);
	        int numOfTreasure = Integer.valueOf(args[1]);
	        try {
	        	mazeGameP2PImpl server = new mazeGameP2PImpl(dimension, numOfTreasure);
	        	P2PMazeGameServerClient stub = (P2PMazeGameServerClient) UnicastRemoteObject.exportObject(server, 0);
	            // Bind the remote object's stub in the registry
	            rmiRegistry = LocateRegistry.createRegistry(REGISTRY_PORT);
	            rmiRegistry.bind("MazeGameServer", stub);
	            System.out.println("Server ready");
	        } catch (AccessException e) {
	            e.printStackTrace();
	        } catch (RemoteException e) {
	            e.printStackTrace();
	        } catch (AlreadyBoundException e) {
	            e.printStackTrace();
	        }
		}
		
		try {
			String host;//read host from keyboard input
			if (isFirstPlayer){
				 host = args[2];
			}else{
				 host = args[1];
			}		
            Registry registry = LocateRegistry.getRegistry(host, REGISTRY_PORT);
            P2PMazeGameServerClient server = (P2PMazeGameServerClient) registry.lookup(P2PMazeGameServerClient.NAME);
            mazeGameP2PImpl player = new mazeGameP2PImpl(Thread.currentThread());
            UnicastRemoteObject.exportObject(player, 0);
            boolean success = server.joinP2PGame(player);
            if(success) {
                System.out.println("join game successfully, waiting for game start...");
                while(!player.isGameStarted()) {
                    synchronized (player) {
                        player.wait();
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

	@Override
	public boolean joinP2PGame(P2PMazeGameServerClient client) throws RemoteException {
        if(gameStatus == GAME_INIT) {
            // The first player join in, notify game start in 20 seconds
            players.put(playerNum, new Player(playerNum, client));
            playerNum++;
            //TODO: change back to 20
            executor.schedule(new GameInitializeTask(), 5, TimeUnit.SECONDS);
            gameStatus = GAME_PENDING_START;
            System.out.println("first client");
            return true;
        } else if (gameStatus == GAME_PENDING_START) {
            players.put(playerNum, new Player(playerNum, client));
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
                    ServerMsg endMsg = game.createGameOverMsgForPlayer(players.get(playerID));
                    // notify other players
                    executor.execute(new GameEndTask());
                    return endMsg;
                }
            }
            return game.createMsgForPlayer(players.get(playerID));
        } else {
            // receive move request when game is over, send back game over message
            return game.createGameOverMsgForPlayer(players.get(playerID));
        }
	}

	@Override
	public void update(int playerID, char dir) throws RemoteException {
        if(gameStatus == GAME_START) {
            if(game.playerMove(playerID, dir)) {
                if (game.isGameOver()) {
                    // game is over, send back game_over response and remove player
                    gameStatus = GAME_END;
                }
            }
        }	
	}

	@Override
	public void notifyStart(int id, ServerMsg msg) throws RemoteException {
		gameStatus = GAME_START;
        this.playerId = playerId;
        this.serverMsg = msg;
        
        serverMsg.getGameState();
		
		System.err.println("Notification from server. ID = " + id
				+ " isPrimaryServer: " + isPrimaryServer + " isBackupServer: "
				+ isBackupServer);
        
        this.notifyAll();		
	}

	@Override
	public void notifyEnd(ServerMsg msg) throws RemoteException {
        gameStatus = GAME_END;
        serverMsg = msg;
        // interrupt the io thread
        ioThread.interrupt();
		
	}
	
	@Override
	public void heartBeat() throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	
    private boolean isValidInput(String input) {
        // TODO: check if input is valid
        if(input.length() != 1)
            return false;
        char ch = input.toUpperCase().charAt(0);
        if(ch == 'W' || ch == 'S' || ch == 'A' || ch == 'D' || ch == ' ')
            return true;
        else return false;
    }
    
    public void gaming(P2PMazeGameServerClient server) {
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
		isBackupServer = true;
		System.err.println("Player " + playerId + " is now Backup Server");
	}
	
	
}
