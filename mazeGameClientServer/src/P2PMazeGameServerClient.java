import java.rmi.Remote;
import java.rmi.RemoteException;
/**
 * @author Sun Fei
 *
 */

public interface P2PMazeGameServerClient extends Remote {
	final static String NAME = "P2PMazeGameServer";
		
	public boolean joinP2PGame(P2PMazeGameServerClient client) throws RemoteException;

	public GameState p2pMove(int id, char dir, int playerClock) throws RemoteException;

	public void update(int id, char dir, int playerClock) throws RemoteException;

	public void notifyStart(int id, GameState state) throws RemoteException;
	
	public void notifyEnd(GameState state) throws RemoteException;
}
