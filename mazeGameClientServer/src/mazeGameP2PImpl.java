import java.rmi.NotBoundException;
import java.rmi.RemoteException;
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

	public mazeGameP2PImpl() {
		isPrimaryServer = false;
		isBackupServer = false;
	}

	public static void main(String[] args) {
		if (args.length < 1) {
			System.err.println("Usage note:");
			System.err.println("java MGP2PImpl host [N M] [hostAddr] for the first player");
			System.err.println("java MGP2PImpl player [hostAddr] for the other players");
		}
		
		isFirstPlayer = args[0].equals("host") ? true : false;

		P2PMazeGameServerClient serverClient = null;

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
	public GameState p2pMove(int id, char dir, int playerClock) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void update(int id, char dir, int playerClock) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyStart(int id, ServerMsg msg) throws RemoteException {
		gameStatus = GAME_START;
        this.playerId = playerId;
        this.serverMsg = msg;
        this.notifyAll();
        
        
		
	}

	@Override
	public void notifyEnd(ServerMsg msg) throws RemoteException {
		// TODO Auto-generated method stub
		
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
    
    public synchronized boolean isGameStarted() {
        return (GAME_START == gameStatus);
    }

    public synchronized boolean isGameEnd() {
        return (GAME_END == gameStatus);
    }



}
