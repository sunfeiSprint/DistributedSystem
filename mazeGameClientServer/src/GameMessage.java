import java.io.Serializable;

/**
 * Created by Benze on 8/31/15.
 */
public class GameMessage implements Serializable {

    /** the current game state */
    private GameState gameState;

    /** the current position of the player */
    private Coordinate playerPos;

    /** is game over */
    private boolean isGameOver = false;

    /** treasure collected */
    private String statistics;

    public GameMessage(GameState gameState) {
        this.gameState = gameState;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public void setStatistics(String statistics) {
        this.statistics = statistics;
    }

    public void setGameOver(boolean isGameOver) {
        this.isGameOver = isGameOver;
    }

    public void setPlayerPos(Coordinate pos) {
        this.playerPos = pos;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        char[][] map = gameState.getMap();
        int numOfRows = map.length;
        int numOfColumns = map[0].length;
        sb.append(System.lineSeparator());
        for(int i = 0; i < numOfRows; i++) {
            for(int j = 0; j < numOfColumns; j++) {
                sb.append('|');
                switch(map[i][j]) {
                    case GameState.TREASURE:
                        sb.append(gameState.getNumOfTreasureAt(j, i));
                        break;
                    case GameState.PLAYER:
                        if(i == playerPos.getY() && j == playerPos.getX()) {
                            sb.append(GameState.CUR_PLAYER);
                        } else {
                            sb.append(GameState.PLAYER);
                        }
                        break;
                    default:
                        sb.append(map[i][j]);
                        break;
                }

            }
            sb.append('|');
            sb.append(System.lineSeparator());
        }
        sb.append(System.lineSeparator());
        if(isGameOver) {
            sb.append("************ Treasure collected ************")
                    .append(System.lineSeparator())
                    .append(statistics)
                    .append(System.lineSeparator());
        }
        return sb.toString();
    }
}
