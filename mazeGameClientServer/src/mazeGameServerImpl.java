import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * @author Sun Fei
 *
 */
public class mazeGameServerImpl implements MazeGameServer,Runnable  {
	
	private List<MazeGameClient> Clientlist;
	private Map<String,Player> Playerlist;
	private int gameStarted;//0 game Waiting to start, 1 game started, 2 game ended.
	private long startTime;
	
	private GameStates gameState;
	
	private int mapSize = 20;
	private int numTresures = 10;
	
	
	public mazeGameServerImpl() throws RemoteException {
		Clientlist = new ArrayList<MazeGameClient>();
		Playerlist = new HashMap<String,Player>();
		startTime = -1;
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
		this.gameState=Playerlist.get(playerID).move(dir, this.gameState);
		return this.gameState;
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
		
		//Create list of player		
		//notify start of the game to the client
		for (int i=0;i<Clientlist.size();i++){
			//construct player list 
			String playerID = "p"+i;
			Player player = new Player(playerID);
			Coordinates location = gameState.playerlocations.get(player);
			player.setLocationX(location.getX());
			player.setLocationY(location.getY());		
			Playerlist.put(playerID, player);		
			try {
			Clientlist.get(i).notifyStart(playerID, gameState);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		while (!gameState.isGameOver()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}
		
		//notify end of the game to the client
		for (int i=0;i<Clientlist.size();i++){
			String playerID = "p"+i;
			try {
			Clientlist.get(i).notifyEnd(gameState);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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
	}
}
