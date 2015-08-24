
import java.util.Map;

public class Player {
	private String playerID;
	private int locationX;
	private int locationY;
	private int numCollectedTreasure;
	
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
		switch (direction){
			case "S":
				if (canMove(locationX,locationY,direction,game)){
					this.setLocationY(this.locationY+1);
				}
				break;
			case "N":
				this.setLocationY(this.locationY-1);
				break;
			case "E":
				this.setLocationX(this.locationX+1);
				break;
			case "W":
				this.setLocationX(this.locationX-1);
				break;
			default:
				break;
			
		}			
		return game;
	}
	
	private boolean canMove(int X,int Y,String direction, GameStates game){
		boolean playerCanMove = false;
		
		//will it hit wall?
		
		//new postion is player?
		
		//new position is treasure? collect treasure <hanle in move>
		

		return playerCanMove; 
	}

	@Override
	public String toString() {
		return "Player [playerID=" + playerID + ", locationX=" + locationX + ", locationY=" + locationY
				+ ", numCollectedTreasure=" + numCollectedTreasure + "]";
	}
}
