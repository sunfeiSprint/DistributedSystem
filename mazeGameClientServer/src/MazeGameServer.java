//Author:Tang Bengze

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MazeGameServer extends Remote {
	final static String NAME = "MazeGameServer";
	final static boolean SUCCESS = true;
	final static boolean FAILED = false;
	
	public boolean joinGame(MazeGameClient client) throws RemoteException;//client join game
	public GameStates move(String playerID, String dir) throws RemoteException;//player move
	//startgame
}