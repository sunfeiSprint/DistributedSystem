package mazeGameLocalVersion;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

public class GameStates {
	
	int N, numPlayers, numTreasuresLeft;//N is mapSize	
	Random rand;
	String[][] gameStates;
	Map<String,Player> PlayerList;
	Map<Coordinates,String> Locations;//record location of each player and treasure
			
	public GameStates(int n, int numPlayers, int numTreasuresLeft) {
		super();
		N = n;
		this.numPlayers = numPlayers;
		this.numTreasuresLeft = numTreasuresLeft;	
		PlayerList = new HashMap<String, Player>();
		Locations = new HashMap<Coordinates, String>();
		rand = new Random(System.currentTimeMillis());	
		placePlayers();
		placeTreasures();
	}
	
	/* method to init player locaiton of the game*/
	private void placePlayers() {
		Coordinates pos;
		for (int i = 0; i < numPlayers; i++) {
			String playerID = "player"+i;
			int x;
			int y;
			do{			
				x = rand.nextInt(N);
				y = rand.nextInt(N);
				pos = new Coordinates(x, y);
			}while(Locations.get(pos)  != null);
			Locations.put(pos,playerID);
//			Player player = new Player(playerID,x,y);
//			PlayerList.put(playerID, player);
		}
	}
	
	/* method to init treasure locaiton of the game*/
	private void placeTreasures(){		
		Coordinates pos;
		for (int i = 0; i < numPlayers; i++) {
			String treasureID = "treasure"+i;
			do{			
				int x = rand.nextInt(N);
				int y = rand.nextInt(N);
				pos = new Coordinates(x, y);
			}while(Locations.get(pos) != null);
			Locations.put(pos,treasureID);

		}		
	}

	@Override
	public String toString() {
		Iterator entries = this.Locations.entrySet().iterator();
		String stringValue ="GameStates" +"\n";
		
		while (entries.hasNext()) {
			  Entry thisEntry = (Entry) entries.next();
			  Coordinates key = (Coordinates)thisEntry.getKey();
			  String value = (String)thisEntry.getValue();
			  stringValue = stringValue + key.toString() + value+ "\n";
			}
		
		return stringValue;
	}
}
