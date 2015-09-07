import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by Benze on 8/31/15.
 */
public class GameState implements Serializable, Cloneable {

    public static final char TREASURE = '$';

    public static final char PLAYER = '@';

    public static final char CUR_PLAYER = 'P';

    public static final char EMPTY = ' ';

    private char[][] map;

    /** number of treasure */
    private int numOfTreasure;

    /** number of treasure on each treasure block */
    private Map<Integer, Integer> numOfTreasurePerBlock = new HashMap<Integer, Integer>();

    /** the dimension of map */
    private int dimension;

    /** number of players */
    private int numOfPlayer;

    public GameState(int dimension, int numOfTreasure, int numOfPlayer) {
        this.dimension = dimension;
        this.numOfTreasure = numOfTreasure;
        this.numOfPlayer = numOfPlayer;
        this.map = new char[dimension][dimension];
        for(char[] row : map) {
            Arrays.fill(row, GameState.EMPTY);
        }
    }

    public void setMap(char[][] map) {
        this.map = map;
    }

    private int hashKeyFromCoordinate(int x, int y) {
        return y * dimension + x;
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
        if (reachable){
        	if (map[locationY][locationX] == PLAYER){
        		reachable = false;
        	}
        }
        return reachable;
    }

    public void setBlockToCurPlayer(int x, int y) {
        map[y][x] = CUR_PLAYER;
    }

    public void setBlockToPlayer(int x, int y) {
        map[y][x] = PLAYER;
    }

    public boolean isEmptyBlock(int x, int y) {
        return (map[y][x] == EMPTY);
    }

    public void setBlockToTreasure(int x, int y) {
        map[y][x] = TREASURE;
        int key = hashKeyFromCoordinate(x, y);
        if(numOfTreasurePerBlock.containsKey(key)) {
            Integer value = numOfTreasurePerBlock.get(key);
            value++;
            numOfTreasurePerBlock.put(key, value);
        } else {
            numOfTreasurePerBlock.put(key, new Integer(1));
        }
    }

    public boolean isTreasure(Coordinate target) {
        // check if the target position has treasure
        return (map[target.getY()][target.getX()] == TREASURE);
    }

    public void treasureCollected() {
        // TODO: assuming player collect only one treasure each time
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
    
    public GameState clone() throws CloneNotSupportedException {
        GameState newState = new GameState(dimension, numOfTreasure, numOfPlayer);
        char[][] newMap = new char[map.length][];
        for (int i = 0; i < map.length; i++) {
            newMap[i] = Arrays.copyOf(map[i], map[i].length);
        }
        newState.map = newMap;
        // TODO: replace to clone?
        newState.numOfTreasurePerBlock = this.numOfTreasurePerBlock;
        return newState;
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
//        GameState state = new GameState(10, 10);
//        System.out.println(state);
    }
}
