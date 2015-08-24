public class Coordinates {

	int x, y;

	public Coordinates(int x, int y) {
		super();
		this.x = x;
		this.y = y;
	}		
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	public boolean equals(Object other) {
		return x == ((Coordinates) other).x && y == ((Coordinates) other).y;
	}
	@Override
	public String toString() {
		return "[x=" + x + ", y=" + y + "]";
	}
}