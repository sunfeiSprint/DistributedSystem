package mazegame.maze;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by Benze on 8/23/15.
 */
public interface MazeServer extends Remote {

    public abstract String helloServer(MazePlayer player, String msg) throws RemoteException;
}
