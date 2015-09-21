import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

/**
 * @author Sun Fei
 *
 */

public interface MazeGamePeer extends Remote {
	final static String NAME = "P2PMazeGameServer";
		
	public boolean joinP2PGame(MazeGamePeer client) throws RemoteException;

	public ServerMsg p2pMove(int id, char dir) throws RemoteException;

	public void updateBackupState(GameState state) throws RemoteException;

    // TODO: if new backup server is down, re-select a new one
    public boolean setAsBackupServer(GameState state, Map<Integer, MazeGamePeer> peerRefs) throws RemoteException;

    public void broadcastNewPrimary(MazeGamePeer primary, ServerMsg serverMsg);

	public void p2pNotifyStart(int id, ServerMsg msg) throws RemoteException;
	
	public void p2pNotifyEnd(ServerMsg msg) throws RemoteException;
    
//	public void heartBeat() throws RemoteException;//if more 1 sec, backup become primary
}
