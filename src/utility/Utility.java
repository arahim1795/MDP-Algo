package utility;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import map.MapConstant;
import robot.RobotConstant.DIRECTION;

/**
 * @author 18/19 S1 G3
 */
public class Utility {
	
	public static Scanner scanner = new Scanner(System.in);
	
	/**
	 * Reads map (in List of Strings form) from a manually written file
	 * @param name Name of file to be read as Map
	 * @return a String-List of the Map
	 * @throws IOException - If an I/O error occurs
	 */ 
	public static List<String> getmap(String name) throws IOException {
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
