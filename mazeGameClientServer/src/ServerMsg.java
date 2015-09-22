import java.io.Serializable;

/**
 * Created by Benze on 8/31/15.
 */
public class ServerMsg implements Serializable {

    /** the current game state */
    private GameState gameState;

    /** the current position of the player */
    private Coordinate playerPos;

    /** is game over */
    private boolean isGameOver = false;

    public ServerMsg(GameState gameState) {
        this.gameState = gameState;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public void setGameOver(boolean isGameOver) {
        this.isGameOver = isGameOver;
    }

//    public Coordinate getPlayerPos() {
//        return playerPos;
//    }
//
//    public GameState getGameState() {
//        return gameState;
//    }

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
                if(i == playerPos.getY() && j == playerPos.getX()) {
                    sb.append(GameState.CUR_PLAYER);
                } else {
                    sb.append(map[i][j]);
                }
            }
            sb.append('|');
            sb.append(System.lineSeparator());
        }
        sb.append(System.lineSeparator());
        return sb.toString();
    }
}
