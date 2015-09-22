
import java.io.Serializable;
import java.rmi.RemoteException;

/**
 * @author Sun Fei
 *
 */
public class Player implements Serializable{

    private Coordinate coordinate = new Coordinate(0, 0);

    public int getNumOfTreasure() {
        return numOfTreasure;
    }

    public void collectTreasure(int num) {
        numOfTreasure += num;
    }

    private int numOfTreasure = 0;

    public Player() {}

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }


}
