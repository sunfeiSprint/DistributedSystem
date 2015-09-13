import java.io.Serializable;

/**
 * @author Sun Fei
 *
 */
public class Coordinate implements Serializable {

	int x, y;

	public Coordinate(int x, int y) {
		super();
		this.x = x;
		this.y = y;
	}		
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public boolean equals(Object other) {
		return x == ((Coordinate) other).x && y == ((Coordinate) other).y;
	}

	@Override
	public String toString() {
		return "[x=" + x + ", y=" + y + "]";
	}
}