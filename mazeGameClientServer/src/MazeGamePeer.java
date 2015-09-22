import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

/**
 * @author Sun Fei
 *
 */

public interface MazeGamePeer extends Remote {

    public static final int GAME_INIT = 0;

    public static final int GAME_PENDING_START = 1;

    public static final int GAME_START = 2;

    public static final int GAME_END = 3;


	final static String NAME = "P2PMazeGameServer";
		
	public boolean joinGame(MazeGamePeer client) throws RemoteException;

	public ServerMsg move(int id, char dir) throws RemoteException;

	public void updateBackupState(GameState state) throws RemoteException;

    // TODO: if new backup server is down, re-select a new one (blocking)
    public boolean setAsBackupServer(int primaryId, GameState state, Map<Integer, MazeGamePeer> peerRefs) throws RemoteException;

    public void notifyNewPrimary(MazeGamePeer primary, ServerMsg serverMsg) throws RemoteException;

	public void notifyStart(int id, ServerMsg msg) throws RemoteException;
	
	public void notifyEnd(ServerMsg msg) throws RemoteException;
    
	public boolean heartBeat() throws RemoteException;//if more 1 sec, backup become primary
}
