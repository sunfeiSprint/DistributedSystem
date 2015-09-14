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

    public Coordinate getPlayerPos() {
        return playerPos;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setPlayerPos(Coordinate pos) {
        this.playerPos = pos;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        gameState.setBlockToCurPlayer(playerPos.getX(), playerPos.getY());
        sb.append(gameState.toString());
        // recover state
        gameState.setBlockToPlayer(playerPos.getX(), playerPos.getY());
        return sb.toString();
    }
}
