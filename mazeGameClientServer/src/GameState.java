import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by Benze on 8/31/15.
 */
public class GameState implements Serializable {

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

    private  Map<Integer, Player> players = new HashMap<>();

    public GameState(int dimension, int numOfTreasure) {
        this.dimension = dimension;
        this.numOfTreasure = numOfTreasure;
        this.map = new char[dimension][dimension];
        for(char[] row : map) {
            Arrays.fill(row, GameState.EMPTY);
        }
    }

    public void addPlayer(int id, Player player) {
        players.put(id, player);
    }

    public Map<Integer, Player> getPlayers() {
        return players;
    }

    public Player getPlayer(int id) {
        return players.get(id);
    }

    public char[][] getMap() { return map; }

    private int hashKeyFromCoordinate(int x, int y) {
        return y * dimension + x;
    }

    public int getDimension() {
        return dimension;
    }

    public boolean isTargetReachable(Coordinate target) {
        int locationX = target.getX();
        int locationY = target.getY();
        //check out of boundary
        if (locationX<0||locationY<0||locationX>=dimension||locationY>=dimension){
        	return false;
        }
        if (map[locationY][locationX] == PLAYER){
            return false;
        }
        return true;
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

//
//    public GameState clone() throws CloneNotSupportedException {
//        GameState newState = new GameState(dimension, numOfTreasure, numOfPlayer);
//        char[][] newMap = new char[map.length][];
//        for (int i = 0; i < map.length; i++) {
//            newMap[i] = Arrays.copyOf(map[i], map[i].length);
//        }
//        newState.map = newMap;
//        // TODO: replace to clone?
//        newState.numOfTreasurePerBlock = this.numOfTreasurePerBlock;
//        return newState;
//    }

//    public static void main(String[] args) {
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
//    }
}
