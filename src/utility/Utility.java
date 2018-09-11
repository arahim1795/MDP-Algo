package utility;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 18/19 S1 G3
 */
public class Utility {
	
	public final int portnumber = 1224;
	
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

}
