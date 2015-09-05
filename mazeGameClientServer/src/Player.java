
import java.rmi.RemoteException;

/**
 * @author Sun Fei
 *
 */
public class Player {

    private MazeGameClient clientRef;

	private int playerID;

    private Coordinate coordinate;

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

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public void notifyGameStart(GameState gameState) throws RemoteException {
        clientRef.notifyStart(playerID, gameState);
    }

//	public Player(int playerID,int locationX, int locationY) {
//		this.playerID = playerID;
//		this.locationX = locationX;
//		this.locationY = locationY;
//		this.numCollectedTreasure = 0;
//	}
//	public GameStates playerMove(String direction, GameStates game){
//		int numberTresureLeft = game.getNumTreasuresLeft();
//		switch (direction){
//			case "S":
//				if (canMove(locationX,locationY,direction,game)){
//					game.getLocations()[this.locationX][this.locationY] = "()";
//					this.setLocationY(this.locationY+1);
//				}
//				break;
//			case "N":
//				if (canMove(locationX,locationY,direction,game)){
//					game.getLocations()[this.locationX][this.locationY] = "()";
//					this.setLocationY(this.locationY-1);
//				}
//				break;
//			case "E":
//				if (canMove(locationX,locationY,direction,game)){
//					game.getLocations()[this.locationX][this.locationY] = "()";
//					this.setLocationX(this.locationX+1);
//				}
//				break;
//			case "W":
//				if (canMove(locationX,locationY,direction,game)){
//					game.getLocations()[this.locationX][this.locationY] = "()";
//					this.setLocationX(this.locationX-1);
//				}
//				break;
//			default:
//				return game;
//		}
//		if (game.getLocations()[locationX][locationY].startsWith("t")){//collect treasure
//			this.numCollectedTreasure = this.numCollectedTreasure + 1;
//			numberTresureLeft = numberTresureLeft -1;
//			game.setNumTreasuresLeft(numberTresureLeft);
//			game.getLocations()[locationX][locationY] = Integer.toString(this.playerID);
//		}else{
//			game.getLocations()[locationX][locationY] = Integer.toString(this.playerID);
//		}
//		return game;
//	}
//
//	private boolean canMove(int X,int Y,String direction, GameStates game){
//		String[][] locations = game.getLocations();
//		switch (direction){//Check if the player is already at boundary
//			case "S":
//				if (Y == (game.getMapSize()-1)){
//					return false;
//				}else{
//					int newY = Y + 1;
//					if (locations[X][newY].startsWith("p")){
//						return false;
//					}
//				}
//				break;
//			case "N":
//				if (Y == 0){
//					return false;
//				}else{
//					int newY = Y - 1;
//					if (locations[X][newY].startsWith("p")){
//						return false;
//					}
//				}
//				break;
//			case "E":
//				if (X == (game.getMapSize()-1)){
//					return false;
//				}else{
//					int newX = X - 1;
//					if (locations[newX][Y].startsWith("p")){
//						return false;
//					}
//				}
//				break;
//			case "W":
//				if (X == (game.getMapSize()-1)){
//					return false;
//				}else{
//					int newX = X + 1;
//					if (locations[newX][Y].startsWith("p")){
//						return false;
//					}
//				}
//				break;
//			default:
//				return false;
//		}
//		return true;
//	}
//
//	public boolean equals(Object other) {
//		return this.playerID == ((Player) other).playerID;
//	}

//	@Override
//	public String toString() {
//		return "Player [playerID=" + playerID + ", locationX=" + locationX + ", locationY=" + locationY
//				+ ", numCollectedTreasure=" + numCollectedTreasure + "]";
//	}
}
