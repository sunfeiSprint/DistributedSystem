import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by Benze on 8/31/15.
 */
public class GameState implements Serializable {

    //String map = "map";

    private static final char TREASURE = '$';

    private static final char OTHER_PLAYER = '@';

    private static final char PLAYER = 'P';

    public GameState(char[][] map) {
        this.map = map;
    }

    char[][] map = new char[10][10];

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
        char[][] map = new char[10][10];
        for(char[] row : map) {
            Arrays.fill(row, ' ');
        }
        map[0][3] = TREASURE;
        map[2][5] = TREASURE;
        map[2][2] = OTHER_PLAYER;
        map[3][3] = OTHER_PLAYER;
        map[0][0] = PLAYER;
        GameState state = new GameState(map);
        System.out.println(state);
    }
}
