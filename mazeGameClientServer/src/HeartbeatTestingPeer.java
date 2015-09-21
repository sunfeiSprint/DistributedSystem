import java.net.Inet4Address;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by Benze on 9/21/15.
 */
public interface HeartbeatTestingPeer extends Remote {

    public abstract HeartbeatConfiguration heartbeatConfig() throws RemoteException;
}
