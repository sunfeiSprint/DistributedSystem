package mazeGameLocalVersion;

import java.io.Serializable;
import java.util.Map;
import java.util.Random;

public class GameStates {
	
	enum GameElement {
		EMPTY, TREASURE, WALL, PLAYER;
	}	
	int N, numPlayers, numTreasuresLeft;//N is mapSize	
	Random rand;
	Map<Coordinates,GameElement> Game;
		
	public GameStates(int n, int numPlayers, int numTreasuresLeft) {
		super();
		N = n;
		this.numPlayers = numPlayers;
		this.numTreasuresLeft = numTreasuresLeft;	
		rand = new Random(System.currentTimeMillis());		
		//placePlayers();
		//placeTreasures();
	}
}
