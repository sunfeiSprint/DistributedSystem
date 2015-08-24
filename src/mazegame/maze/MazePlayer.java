package mazegame.maze;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by Benze on 8/23/15.
 */
public interface MazePlayer extends Remote {

    public abstract String helloPlayer(String msg) throws RemoteException;
}
