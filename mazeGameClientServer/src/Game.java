/**
 * Created by Benze on 8/31/15.
 */
public class Game {
    private GameState gameState;

    public boolean move(Player player, char dir) {
        //Coordinates coordinates = player.getCoordinate();
        Coordinates coordinates = player.getCoordinates();
        Coordinates target = getTargetCoordinate(coordinates, dir);
        if(isTargetReachable(target)) {
            // update GameState and player coordinate
            return true;
        } else {
            return false;
        }
    }

    private Coordinates getTargetCoordinate(Coordinates origin, char dir) {
        return null;
    }

    private boolean isTargetReachable(Coordinates coordinates) {
        return true;
    }

}
