import java.rmi.RemoteException;

/**
 * @author Sun Fei
 *
 */
public class mazeGameServerImpl implements MazeGameServer {

	@Override
	public boolean joinGame(MazeGameClient client) throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public GameStates move(String playerID, String dir) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public static void main(String[] args) {
		System.out.println("Game Start");
		int numPlayers = 2;
		int mapSize = 10;
		int numTresures = 3;
		
//		for (int i=0;i<numPlayers;i++){
//			String playerID = "p"+i;
//			Player player = new Player(playerID);
//			
//		}
		GameStates game = new GameStates(mapSize, numPlayers,numTresures);
		
		Player playerZero = new Player("p0");
		
		playerZero.setLocationX(game.playerlocations.get("p0").getX());
		playerZero.setLocationY(game.playerlocations.get("p0").getY());
		
		System.out.println(game.toString());
		
		game = playerZero.move("S", game);
		
		System.out.println(game.toString());
		
		game = playerZero.move("E", game);
		System.out.println(game.toString());
		
		game = playerZero.move("W", game);
		
		System.out.println(game.toString());
		game = playerZero.move("N", game);
		
		System.out.println(game.toString());
		
	}
	
}
