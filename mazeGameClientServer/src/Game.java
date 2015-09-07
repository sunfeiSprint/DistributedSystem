import java.util.Map;
import java.util.Random;

/**
 * Created by Benze on 8/31/15.
 */
public class Game {

    private GameState gameState;

    private Map<Integer, Player> players;

    public Game(Map<Integer, Player> players, int numOfTreasure, int dimension) {
        this.players = players;
        gameState = new GameState(dimension, numOfTreasure, players.size());
        initializeGameState(numOfTreasure, dimension);
    }

    private void initializeGameState(int numOfTreasure, int dimension) {
        // randomly set the treasure location
        Random random = new Random();
        int limit = gameState.getDimension() * gameState.getDimension();
        for(int i = 0; i < numOfTreasure; i++) {
            int r = random.nextInt(limit);
            int x = r % dimension;
            int y = r / dimension;
            gameState.setBlockToTreasure(x, y);
        }
        // randomly set the initial location of players
        for(Integer key : players.keySet()) {
            Player player = players.get(key);
            while(true) {
                int r = random.nextInt(limit);
                int x = r % dimension;
                int y = r / dimension;
                if(gameState.isEmptyBlock(x, y)) {
                    gameState.setBlockToPlayer(x, y);
                    player.setCoordinate(new Coordinate(x, y));
                    break;
                }
            }
        }
//        while(count < players.size()) {
//            int r = random.nextInt(limit);
//            int x = r % dimension;
//            int y = r / dimension;
//            if(!gameState.isEmptyBlock(x, y)) {
//                continue;
//            } else {
//                count++;
//                gameState.setBlockToPlayer(x, y);
//
//            }
//        }

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
//			gameSnapshot.setMapLocation(X, Y.CUR_PLAYER);
            gameSnapshot.setBlockToCurPlayer(X, Y);
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
        //TODO: replace to getGameStateForPlayer
//        return getGameState();
        return getGameStateForPlayer(player);
    }

    public boolean isGameOver() {
        return (gameState.getNumOfTreasure() == 0);
    }

    public static Coordinate getTargetCoordinate(Coordinate origin, char dir) {
//        int newX = 0;
//        int newY = 0;
//    	int oldX = origin.getX();
//    	int oldY = origin.getY();
//    	Coordinate newCoordinate = new Coordinate(oldX, oldY);
//    	switch(dir){
//    		case 'S':
//    			newY = oldY + 1;
//    		case 'N':
//    			newY = oldY - 1;
//    		case 'E':
//    			newX = oldX - 1;
//    		case 'W':
//    			newX = oldX + 1;
//    	}
//        newCoordinate.setX(newX);
//        newCoordinate.setY(newY);
        int newX = origin.getX();
        int newY = origin.getY();
        switch (dir) {
            case 'W':
                newY--;
                break;
            case 'S':
                newY++;
                break;
            case 'A':
                newX--;
                break;
            case 'D':
                newX++;
                break;
        }
        return new Coordinate(newX, newY);
    }
}
