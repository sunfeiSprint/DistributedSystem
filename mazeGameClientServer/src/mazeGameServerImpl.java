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
		GameStates game = new GameStates(10, 2,3);
		System.out.println(game.toString());
	}

}
