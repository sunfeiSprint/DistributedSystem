import org.junit.Test;

import static org.junit.Assert.*;

public class GameTest {

    @org.junit.Test
    public void testGetTargetCoordinateUp() throws Exception {
        Coordinate src = new Coordinate(2, 4);
        Coordinate target = Game.getTargetCoordinate(src, 'W');
        assertTrue(target.getX() == 2);
        assertTrue(target.getY() == 3);
    }

    @org.junit.Test
    public void testGetTargetCoordinateDown() throws Exception {
        Coordinate src = new Coordinate(2, 4);
        Coordinate target = Game.getTargetCoordinate(src, 'S');
        assertTrue(target.getX() == 2);
        assertTrue(target.getY() == 5);
    }

    @org.junit.Test
    public void testGetTargetCoordinateLeft() throws Exception {
        Coordinate src = new Coordinate(2, 4);
        Coordinate target = Game.getTargetCoordinate(src, 'A');
        assertTrue(target.getX() == 1);
        assertTrue(target.getY() == 4);
    }

    @org.junit.Test
    public void testGetTargetCoordinateRight() throws Exception {
        Coordinate src = new Coordinate(2, 4);
        Coordinate target = Game.getTargetCoordinate(src, 'D');
        assertTrue(target.getX() == 3);
        assertTrue(target.getY() == 4);
    }

}