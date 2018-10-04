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
	
	
	
	// TODO implement integration to Commms
	/**
	 * 
	 * @return
	 */
	private static String[] insertStartInfo() {
		String regex = "\\d+";
		Pattern p = Pattern.compile(regex);
		String[] input = new String[3];
		
		do {
			System.out.print("Start (X): ");
			input[0] = Utility.scanner.nextLine();
		} while (p.matcher(input[0]).matches());
		
		do {
			System.out.print("Start (Y): ");
			input[1] = Utility.scanner.nextLine();
		} while (p.matcher(input[1]).matches());
		do {
			System.out.print("Direction: ");
			input[2] = Utility.scanner.nextLine();
		} while (validateDir(input[2].toLowerCase()));
		
		return input;
	}
	
	/**
	 * 
	 * @param str
	 * @return
	 */
	private static boolean validateDir(String str) {
		boolean bool;
		
		switch(str) {
			case "up":
			case "down":
			case "left":
			case "right":
				bool = false;
				break;
			default:
				bool = true;
				break;
		}
		
		return bool;
	}
	
	/**
	 * 
	 * @param str
	 * @return
	 */
	private static DIRECTION convertDir(String str) {
		DIRECTION dir;
		
		switch(str) {
			case "up":
				dir = DIRECTION.UP;
				break;
			case "down":
				dir = DIRECTION.DOWN;
				break;
			case "left":
				dir = DIRECTION.LEFT;
				break;
			default:
				dir = DIRECTION.RIGHT;
				break;
		}
		
		return dir;
	}

}
