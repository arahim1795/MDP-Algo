package search;

import map.*;
import robot.*;
import utility.Utility;

public class Explore {
	
	private Map mapRobot = new Map();
	private int x, y;
	
	// Simulator
	private Map mapActual;
	
	// Only for Simulator
	public void explore(Map map) {
		
		mapActual = map;
		
		int[] coor = insertCoor();
		this.x = coor[0]; 
		this.y = coor[1];
		
		while (true) {
			
			
			
		}
		
	}
	
	private static String[] insertStartInfo() {
		String[] input = new String[2];
		System.out.print("Start (X): ");
		do {
			input[0] = Utility.scanner.nextLine();
		} while (!input[0].)
		System.out.print("Start (Y): ");
		input[1] = Utility.scanner.nextLine();
		return input;
	}

}
