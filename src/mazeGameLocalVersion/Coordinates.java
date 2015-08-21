package mazeGameLocalVersion;

public class Coordinates {
	int x, y;

	public Coordinates(int x, int y) {
		super();
		this.x = x;
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
