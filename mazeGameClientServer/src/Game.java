import java.util.Map;

/**
 * Created by Benze on 8/31/15.
 */
public class Game {

    private GameState gameState;

    private Map<Integer, Player> players;

    public Game(Map<Integer, Player> players, int numOfTreasure, int dimension) {
        this.players = players;
        // TODO: change to random number
        gameState = new GameState(numOfTreasure, dimension);
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
        int newX,newY;
    	int oldX = origin.getX();
    	int oldY = origin.getY();
    	Coordinate newCoordinate = new Coordinate(oldX,oldY);
//    	swith(direction){
//    		case "S":
//    			newY = Y + 1;
//    		case "N":
//    			newY = Y - 1;
//    		case "E":
//    			newX = X - 1;
//    		case "W":
//    			newX = X + 1;
//    	}
//        newCoordinate.setX(newX);
//        newCoordinate.setY(newY);
        return newCoordinate;
    }
}
