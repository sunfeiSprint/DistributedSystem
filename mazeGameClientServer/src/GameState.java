import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by Benze on 8/31/15.
 */
public class GameState implements Serializable {

    public static final char TREASURE = '$';

    public static final char PLAYER = '@';

    public static final char CUR_PLAYER = 'P';

    public static final char EMPTY = ' ';

    char[][] map;

    /** number of treasure */
    private int numOfTreasure;

    /** the dimension of map */
    private int dimension;

    public GameState(int n) {
        this.dimension = n;
        this.map = new char[dimension][dimension];
        initializeMap();
    }

    private void initializeMap() {
        // TODO: randomly initialize the map
    }

    public int getDimension() {
        return dimension;
    }

    public boolean isTargetReachable(Coordinate target) {
        boolean reachable = true;
        int locationX = target.getX();
        int locationY = target.getY();
        //check out of boundary
        if (locationX<0||locationY<0||locationX>=dimension||locationY>=dimension){
        	reachable = false;
        }
        return reachable;
    }

    public boolean isTreasure(Coordinate target) {
        // check if the target position has treasure
        return (map[target.getY()][target.getX()] == TREASURE);
    }

    public void treasureCollected() {
        numOfTreasure--;
    }

    public void playerMove(Coordinate origin, Coordinate target) {
        // update game state after player playerMove
        map[origin.getY()][origin.getX()] = EMPTY;
        map[target.getY()][target.getX()] = PLAYER;
    }

    public int getNumOfTreasure() {
        return numOfTreasure;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int numOfRows = map.length;
        int numOfColumns = map[0].length;
        sb.append(System.lineSeparator());
        for(int i = 0; i < numOfRows; i++) {
            for(int j = 0; j < numOfColumns; j++) {
                sb.append('|');
                sb.append(map[i][j]);
            }
            sb.append('|');
            sb.append(System.lineSeparator());
        }
        sb.append(System.lineSeparator());
        return sb.toString();
    }

    public static void main(String[] args) {
//        char[][] map = new char[10][10];
//        for(char[] row : map) {
//            Arrays.fill(row, GameState.EMPTY);
//        }
//        map[0][3] = TREASURE;
//        map[2][5] = TREASURE;
//        map[2][2] = OTHER_PLAYER;
//        map[3][3] = OTHER_PLAYER;
//        map[0][0] = PLAYER;
        GameState state = new GameState(5);
        System.out.println(state);
    }
}
