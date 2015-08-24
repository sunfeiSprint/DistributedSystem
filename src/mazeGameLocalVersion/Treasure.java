package mazeGameLocalVersion;

public class Treasure {
	private String treasureID;//treasureID starts with 200
	private int locationX;
	private int locationY;
	private boolean collected;
	
	public Treasure(String treasureID, int locationX, int locationY, boolean collected) {
		super();
		this.treasureID = treasureID;
		this.locationX = locationX;
		this.locationY = locationY;
		this.collected = collected;
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
	public boolean isCollected() {
		return collected;
	}
	public void setCollected(boolean collected) {
		this.collected = collected;
	}
	
}
