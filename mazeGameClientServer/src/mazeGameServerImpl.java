import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author Sun Fei
 *
 */
public class mazeGameServerImpl implements MazeGameServer,Runnable  {
	
	private List<MazeGameClient> Clientlist;
	private int gameStarted;//0 game Waiting to start, 1 game started, 2 game ended.
	private long startTime;
	
	private GameStates gameState;
	private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
	
	private int mapSize = 20;
	private int numTresures = 10;
	
	
	public mazeGameServerImpl() throws RemoteException {
		Clientlist = new ArrayList<MazeGameClient>();
		startTime = -1;
	}
	
	private class DelayNotify implements Runnable {

        public DelayNotify() {
        }

        @Override
        public void run() {
            try {
            	gameState = new GameStates(mapSize,Clientlist.size(),numTresures);
            	
            	for (int i = 0; i < Clientlist.size(); i++) {
        			String playerID = "p" + i;
        			Clientlist.get(i).notifyStart(playerID, gameState);
        		}
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

	@Override
	public synchronized boolean joinGame(MazeGameClient client) throws RemoteException {
		if (startTime == -1) { // first player
			startTime = System.nanoTime() + 20 * (long)1000000000;
		} else {
			if (System.nanoTime() > startTime) {//already game started
				return FAILED;
			}
		}
		System.err.println("Player " + client + " joined");
		
		Clientlist.add(client);
		return SUCCESS;
	}

	@Override
	public GameStates move(String playerID, String dir) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("Awaiting players...");
		
		while (startTime == -1) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}
		}
		
		System.out.println("First player joined.");
		while (System.nanoTime() < startTime) {
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
			}
		}
		
		System.out.println("Starting game...");
		
		gameState = new GameStates(mapSize,Clientlist.size(),numTresures);
		
		//notify start
		
		while (!gameState.isGameOver()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}
		
		//nofigy end
	}
	
	public static void main(String[] args) {
	
		String host = "localhost";

		try {
			mazeGameServerImpl gameServer = new mazeGameServerImpl();
			String registration = "rmi://" + host + "/" + MazeGameServer.NAME;

			Naming.rebind(registration, gameServer);

			System.out.println("Server ready");

			Thread thread = new Thread((mazeGameServerImpl) gameServer);
			thread.start();
		} catch (Exception e) {
			System.out.println("Server exception: " + e.toString());
			e.printStackTrace();
			System.exit(1);
		}
		
//		System.out.println("Game Start");
//		int numPlayers = 2;
//		int mapSize = 10;
//		int numTresures = 3;		
//		GameStates game = new GameStates(mapSize, numPlayers,numTresures);
//		
//		Player playerZero = new Player("p0");
//		
//		playerZero.setLocationX(game.playerlocations.get("p0").getX());
//		playerZero.setLocationY(game.playerlocations.get("p0").getY());
//		
//		System.out.println(game.toString());
//		
//		game = playerZero.move("S", game);
//		
//		System.out.println(game.toString());
//		
//		game = playerZero.move("E", game);
//		System.out.println(game.toString());
//		
//		game = playerZero.move("W", game);
//		
//		System.out.println(game.toString());
//		game = playerZero.move("N", game);
//		
//		System.out.println(game.toString());
		
	}
}
