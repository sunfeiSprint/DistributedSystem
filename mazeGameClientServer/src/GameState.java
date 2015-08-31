import java.io.Serializable;

/**
 * Created by Benze on 8/31/15.
 */
public class GameState implements Serializable {

    String map = "map";

    @Override
    public String toString() {
        return "GameState{" +
                "map='" + map + '\'' +
                '}';
    }
}
