package mazeGameLocalVersion;

import java.util.Random;

public class Main {

	public static void main(String[] args) {
		System.out.println("This is a maze game local version");		
		
		System.out.println("Start Game");
		System.out.println("Init Game State");
//		System.out.println("Player start to collect treasures");
//		System.out.println("All treasures have been collected Game end");
		
		GameStates newGame = new GameStates(5,2,2);
		
		System.out.println(newGame.toString());

//		System.out.println(a.toString());
//		a.move("W");
//		System.out.println("move W"+ a.toString());
//		a.move("E");
//		System.out.println("move E"+ a.toString());
//		a.move("N");
//		System.out.println("move N"+ a.toString());
//		a.move("S");
//		System.out.println("move S"+ a.toString());
//		a.move(" ");
//		System.out.println("No move"+ a.toString());
	}

}
