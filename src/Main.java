import java.io.IOException;

import map.Map;
import utility.Utility;

public class Main {
	
	public static void main(String[] args) throws IOException {
		
		// example 
		Map map = new Map();
		
		String filename = "./map/test_1.txt";
		
		map.parseMap(Utility.getmap(filename));
		map.printMap();
		
	}

}
