import java.rmi.Remote;
import java.rmi.RemoteException;
/**
 * @author Sun Fei
 *
 */

public interface P2PMazeGameServerClient extends Remote {
	final static String NAME = "P2PMazeGameServer";
		
	public boolean joinP2PGame(P2PMazeGameServerClient client) throws RemoteException;

	public ServerMsg p2pMove(int id, char dir) throws RemoteException;

	public void update(int id, char dir) throws RemoteException;

	public void p2pNotifyStart(int id, ServerMsg msg) throws RemoteException;
	
	public void p2pNotifyEnd(ServerMsg msg) throws RemoteException;
    
	public void heartBeat() throws RemoteException;//if more 1 sec, backup become primary
}
