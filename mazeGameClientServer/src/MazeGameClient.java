//Author:Tang Bengze

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MazeGameClient extends Remote {
	final static String NAME = "MazeGameClient";
	
	public void notifyStart(int playerId, GameStates state) throws RemoteException;//server notify client that game started
	public void notifyEnd(GameStates state) throws RemoteException;//server notify client that game ended
}