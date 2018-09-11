import java.io.IOException;

import map.Field;
import utility.Utility;

public class Main {
	
	public static void main(String[] args) throws IOException {
		
		// example 
		Field map = new Field();
		
		String filename = "./map/test_1.txt";
		
		map.parseMap(Utility.getmap(filename));
		map.printMap();
		
	}

}
