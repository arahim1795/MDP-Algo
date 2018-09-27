package utility;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import map.Constants;

/**
 * @author 18/19 S1 G3
 */
public class Utility {
	
	public static Scanner scanner = new Scanner(System.in);
	
	/**
	 * Orientations the Robot can face
	 * @see robot.Robot
	 */
	public enum Orientation {
		/**
		 * Border in contact with End Zone
		 */
		UP, 
		/**
		 * Border in contact with Start Zone
		 */
		DOWN,
		/**
		 * Border in contact with End Zone
		 */
		RIGHT,
		/**
		 * Border in contact with Start Zone
		 */
		LEFT
	}
	
	/**
	 * Reads map (in List of Strings form) from a manually written file
	 * @param name Name of file to be read as Map
	 * @return a String-List of the Map
	 * @throws IOException - If an I/O error occurs
	 */
	public static List<String> getmap(String name) throws IOException {
		// Construct BufferedReader from FileReader
		BufferedReader reader = new BufferedReader(new FileReader(name));
		List<String> mapcomp = new ArrayList<String>();
		String line = null;
		while ((line = reader.readLine()) != null) {
			mapcomp.add(line);
		}
		reader.close();
		return mapcomp;
	}
	
	/**
	 * Returns a list of valid adjacent coordinates
	 * @param ref Grid coordinate
	 * @return list of valid adjacent coordinates to the passed-in coordinates
	 */
	public static List<int[]> getAdjCoor(int[] ref) {
		List<int[]> listCoor = new ArrayList<int[]>();		
		int refC = ref[0], refR = ref[1];
		int[] adj;
		for (int r = -1; r <= 1; r++) {
			for (int c = -1; c <= 1; c++) {
				adj = new int[2];
				adj[0] = refC + c;
				adj[1] = refR + r;
				if ((!(adj[0] == ref[0]) || !(adj[1] == ref[1])) && isValid(adj)) {
					listCoor.add(adj);
				}
			}
		}
		return listCoor;
	}
	
	/**
	 * Returns true if coordinates are within map
	 * @param coor x and y-coordinates
	 * @return true if x and y-coordinates are valid, false otherwise 
	 */
	public static boolean isValid(int[] coor){
		return coor[0] > 0 && coor[0] < Constants.MAP_ROWS && coor[1] > 0 && coor[1] < Constants.MAP_COLS;
	}

}
