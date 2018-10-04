package utility;

import map.Map;
import map.MapConstant;
import java.io.*;

public class MapDescriptor {
	public static void 	loadMapfromFile (Map map, String bin){
		System.out.println(bin);
		try {
			/*
			System.out.println(filename);
            InputStream inputStream = new FileInputStream(filename + ".txt");
            BufferedReader buf = new BufferedReader(new InputStreamReader(inputStream));

            String line = buf.readLine();
            StringBuilder sb = new StringBuilder();
            while (line != null) {
                sb.append(line);
                line = buf.readLine();
            }

            String bin = sb.toString();
            */
            int binPtr = 0;
            for (int row = 0 ; row < MapConstant.MAP_ROWS ; row++) {
                for (int col = 0; col < MapConstant.MAP_COLS; col++) {
                	
                    if (bin.charAt(binPtr) == '1') map.setObstacleTile(row, col, true);
                    binPtr++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
		return;
	}
	
	 public static String generateMapString(Map map) {

	        String mapString = "";

	        for (int row = 0; row < MapConstant.MAP_ROWS ; row++) 
	            {
	                for (int col = 0; col < MapConstant.MAP_COLS; col++) {
	                // Obstacle - Border walls
	                if (!map.getTile(row, col).isObstacle()) {
	                    mapString += "0";
	                } else {
	                    mapString += "1";
	                }
	            }
	        }

	        return mapString;
	    }
	
}
