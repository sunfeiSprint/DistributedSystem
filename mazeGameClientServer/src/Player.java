
import java.rmi.RemoteException;

/**
 * @author Sun Fei
 *
 */
public class Player {

    private MazeGameClient clientRef;
    
    private P2PMazeGameServerClient p2pClientRef;

	private int playerID;

    private Coordinate coordinate = new Coordinate(0, 0);

    public int getNumOfTreasure() {
        return numOfTreasure;
    }

    public void collectTreasure() {
        numOfTreasure++;
    }

    private int numOfTreasure = 0;
	
	public Player(int playerID, MazeGameClient clientRef) {
		this.playerID = playerID;
        this.clientRef = clientRef;
	}
	
	public Player(int playerID, P2PMazeGameServerClient p2pClientRef) {
		this.playerID = playerID;
        this.p2pClientRef = p2pClientRef;
	}

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public void notifyGameStart(ServerMsg msg) throws RemoteException {
        clientRef.notifyStart(playerID, msg);
    }

    public void notifyGameEnd(ServerMsg msg) throws RemoteException {
        clientRef.notifyEnd(msg);
    }

//	@Override
//	public String toString() {
//		return "Player [playerID=" + playerID + ", locationX=" + locationX + ", locationY=" + locationY
//				+ ", numCollectedTreasure=" + numCollectedTreasure + "]";
//	}
}
