import java.util.Map;

/**
 * Created by Benze on 8/31/15.
 */
public class Game {

    private GameState gameState;

    private Map<Integer, Player> players;

    public Game(Map<Integer, Player> players, int numOfTreasure, int dimension) {
        this.players = players;
        gameState = new GameState(dimension, numOfTreasure, players.size());
    }

    public GameState getGameState() {
        return gameState;
    }

    public GameState getGameStateForPlayer(Player player) {
    	GameState  gameSnapshot;
		try {
			gameSnapshot = gameState.clone();
		    int X = player.getCoordinate().getX();
		    int Y = player.getCoordinate().getY();
			gameSnapshot.map[X][Y] = gameState.PLAYER;
	    	return gameSnapshot;
		} catch (CloneNotSupportedException e) {			
			e.printStackTrace();
			return null;
		}       
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
        int newX = 0;
        int newY = 0;
    	int oldX = origin.getX();
    	int oldY = origin.getY();
    	Coordinate newCoordinate = new Coordinate(oldX,oldY);
    	switch(dir){
    		case 'S':
    			newY = oldY + 1;
    		case 'N':
    			newY = oldY - 1;
    		case 'E':
    			newX = oldX - 1;
    		case 'W':
    			newX = oldX + 1;
    	}
        newCoordinate.setX(newX);
        newCoordinate.setY(newY);
        return newCoordinate;
    }
}
