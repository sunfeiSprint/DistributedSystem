//Author:Tang Bengze

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MazeGameClient extends Remote {
    //server notify client that game started
	public abstract void notifyStart(int playerId, GameState state) throws RemoteException;

    //server notify client that game ended
    public abstract void notifyEnd(GameState state) throws RemoteException;
}