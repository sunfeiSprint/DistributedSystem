//Author:Tang Bengze

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MazeGameServer extends Remote {
	final static String NAME = "MazeGameServer";
	
	public boolean joinGame(MazeGameClient client) throws RemoteException;//client join game
	public ServerMsg move(int playerID, char dir) throws RemoteException;//player playerMove
	//startgame
}