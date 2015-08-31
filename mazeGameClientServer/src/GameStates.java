/**
 * @author Sun Fei
 *
 */

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GameStates implements Serializable {

	private static final long serialVersionUID = -4077101802232614756L;
	
	int N, numPlayers, numTreasuresLeft;//N is mapSize	
	String[][] Locations;
	Random rand;
	Map<String,Coordinates> playerlocations;//record initial location of each player
	public int getMapSize() {
		return N;
	}

	public void setMapSize(int n) {
		N = n;
	}

	public int getNumPlayers() {
		return numPlayers;
	}

	public void setNumPlayers(int numPlayers) {
		this.numPlayers = numPlayers;
	}

	public int getNumTreasuresLeft() {
		return numTreasuresLeft;
	}

	public void setNumTreasuresLeft(int numTreasuresLeft) {
		this.numTreasuresLeft = numTreasuresLeft;
	}

	public String[][] getLocations() {
		return Locations;
	}

	public void setLocations(String[][] locations) {
		Locations = locations;
	}

	public GameStates(int n, int numPlayers, int numTreasuresLeft) {
		super();
		N = n;
		this.numPlayers = numPlayers;
		this.numTreasuresLeft = numTreasuresLeft;	
		rand = new Random(System.currentTimeMillis());	
		
		this.Locations = new String[n][n];
		
		for (int i=0;i<n;i++){
			for (int j=0;j<n;j++){
				Locations[i][j] = "()";
			}
		}
		playerlocations = new HashMap<String, Coordinates>();
		placePlayers();
		placeTreasures();
	}
	
	/* method to init player locaiton of the game*/
	private void placePlayers() {
		Coordinates pos;
		for (int i = 0; i < numPlayers; i++) {
			String playerID = "p"+i;
			int x;
			int y;
			do{			
				x = rand.nextInt(N);
				y = rand.nextInt(N);
				pos = new Coordinates(x, y);
			}while(Locations[x][y] != "()");
			Locations[x][y] = playerID;
			playerlocations.put(playerID,pos);
		}
	}
	
	/* method to init player locaiton of the game*/
	private void placeTreasures() {
		for (int i = 0; i < numTreasuresLeft; i++) {
			String trasureID = "t"+i;
			int x;
			int y;
			do{			
				x = rand.nextInt(N);
				y = rand.nextInt(N);
			}while(Locations[x][y] != "()");
			Locations[x][y] = trasureID;
		}
	}
	
	
	public boolean isGameOver() {
		if (numTreasuresLeft == 0) {
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		String game = "Mapzie N = " + N;
		game = game + "\n";
		for (int i=0;i<N;i++){
			for (int j=0;j<N;j++){
				game = game + Locations[i][j];
			}
			game = game + "\n";
		}
		return game;
	}

}
