
import java.util.Map;
/**
 * @author Sun Fei
 *
 */
public class Player {
	private String playerID;
	private int locationX;
	private int locationY;
	private int numCollectedTreasure;
	
	public Player(String playerID){
		this.playerID = playerID;
	}
	
	public Player(String playerID,int locationX, int locationY) {
		this.playerID = playerID;
		this.locationX = locationX;
		this.locationY = locationY;
		this.numCollectedTreasure = 0;
	}


	public int getLocationX() {
		return locationX;
	}

	public void setLocationX(int locationX) {
		this.locationX = locationX;
	}

	public int getLocationY() {
		return locationY;
	}

	public void setLocationY(int locationY) {
		this.locationY = locationY;
	}

	public int getNumCollectedTreasure() {
		return numCollectedTreasure;
	}

	public void setNumCollectedTreasure(int numCollectedTreasure) {
		this.numCollectedTreasure = numCollectedTreasure;
	}	
	
	public GameStates move(String direction,GameStates game){
		String[][] locations = game.getLocations();
		int numberTresureLeft = game.getNumTreasuresLeft();
		switch (direction){
			case "S":
				if (canMove(locationX,locationY,direction,game)){
					locations[this.locationX][this.locationY] = "()";
					this.setLocationY(this.locationY+1);
				}
				break;
			case "N":
				if (canMove(locationX,locationY,direction,game)){
					locations[this.locationX][this.locationY] = "()";
					this.setLocationY(this.locationY-1);
				}				
				break;
			case "E":
				if (canMove(locationX,locationY,direction,game)){
					locations[this.locationX][this.locationY] = "()";
					this.setLocationX(this.locationX+1);
				}						
				break;
			case "W":
				if (canMove(locationX,locationY,direction,game)){
					locations[this.locationX][this.locationY] = "()";
					this.setLocationX(this.locationX-1);
				}				
				break;
			default:
				return game;	
		}
		if (locations[locationX][locationY].startsWith("t")){//collect treasure
			this.numCollectedTreasure = this.numCollectedTreasure + 1;
			numberTresureLeft = numberTresureLeft -1;
			game.setNumTreasuresLeft(numberTresureLeft);
			locations[locationX][locationY] = this.playerID;
		}else{
			locations[locationX][locationY] = this.playerID;
		}
		return game;
	}
	
	private boolean canMove(int X,int Y,String direction, GameStates game){
		String[][] locations = game.getLocations();
		switch (direction){//Check if the player is already at boundary
			case "S":
				if (Y == (game.getMapSize()-1)){
					return false;
				}else{
					int newY = Y + 1;
					if (locations[X][newY].startsWith("p")){
						return false;
					} 
				}
				break;
			case "N":
				if (Y == 0){
					return false;
				}else{
					int newY = Y - 1;
					if (locations[X][newY].startsWith("p")){
						return false;
					} 
				}
				break;
			case "E":
				if (X == (game.getMapSize()-1)){
					return false;
				}else{
					int newX = X - 1;
					if (locations[newX][Y].startsWith("p")){
						return false;
					} 
				}
				break;
			case "W":
				if (X == (game.getMapSize()-1)){
					return false;
				}else{
					int newX = X + 1;
					if (locations[newX][Y].startsWith("p")){
						return false;
					} 
				}
				break;
			default:
				return false;		
		}
		return true;				
	}

	@Override
	public String toString() {
		return "Player [playerID=" + playerID + ", locationX=" + locationX + ", locationY=" + locationY
				+ ", numCollectedTreasure=" + numCollectedTreasure + "]";
	}
}
