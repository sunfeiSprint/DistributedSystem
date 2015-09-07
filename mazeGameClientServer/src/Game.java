import java.util.Map;

/**
 * Created by Benze on 8/31/15.
 */
public class Game {

    private GameState gameState;

    private Map<Integer, Player> players;

    public Game(Map<Integer, Player> players) {
        this.players = players;
        // TODO: change to random number
        gameState = new GameState(10);
    }

    public GameState getGameState() {
        return gameState;
    }

    public GameState getGameStateForPlayer(Player player) {
        // TODO: return a snapshot of game state for player
        return gameState;
    }

    public GameState playerMove(int playerId, char dir) {
        //Coordinates coordinates = player.getCoordinate();
        Player player = players.get(playerId);
        Coordinate playerPos = player.getCoordinate();
        Coordinate target = getTargetCoordinate(playerPos, dir);
        if(gameState.isTargetReachable(target)) {
            if (gameState.isTreasure(target)) {
                gameState.treasureCollected();
                player.collectTreasure();
            }
            // update GameState and player coordinate
            gameState.playerMove(playerPos, target);
            player.setCoordinate(target);
        }
        return getGameStateForPlayer(player);
    }

    public boolean isGameOver() {
        return (gameState.getNumOfTreasure() == 0);
    }

    public static Coordinate getTargetCoordinate(Coordinate origin, char dir) {
        // TODO: calculate the target coordinate from the origin and dir
        return null;
    }
}
