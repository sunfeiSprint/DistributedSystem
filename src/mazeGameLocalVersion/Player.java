package mazeGameLocalVersion;



public class Player {
	private int playerID;
	private int locationX;
	private int locationY;
	private int numCollectedTreasure;
	
	public Player(int playerID,int locationX, int locationY) {
		this.playerID = playerID;
		this.locationX = locationX;
		this.locationY = locationY;
		this.numCollectedTreasure = 0;
	}

	public int getPlayerID() {
		return playerID;
	}

	public void setPlayerID(int playerID) {
		this.playerID = playerID;
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
	
	public Player move(String direction){
		switch (direction){
			case "S":
				this.setLocationY(this.locationY+1);
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
		return this;
	}

	@Override
	public String toString() {
		return "Player [playerID=" + playerID + ", locationX=" + locationX + ", locationY=" + locationY
				+ ", numCollectedTreasure=" + numCollectedTreasure + "]";
	}
}
