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
	
	public static String generateExploredMapString(Map map){
		 StringBuilder mapString = new StringBuilder();
		 	mapString.append("11");
		 	mapString.append(System.lineSeparator());
		 	
	        for (int row = 0; row < MapConstant.MAP_ROWS ; row++) 
	            {
	                for (int col = 0; col < MapConstant.MAP_COLS; col++) {
	                // Obstacle - Border walls
	                if (map.getTile(row, col).isExplored()) {
	                    mapString.append("1");
	                } else {
	                	mapString.append("0");
	                }
	            }
	                mapString.append(System.lineSeparator());
	        }
	        
		 	mapString.append("11");
		 	mapString.append(System.lineSeparator());

	        return mapString.toString();
	 }
	
	public static String generateObstacleMapString(Map map){
		 StringBuilder mapString = new StringBuilder();
		 	
	        for (int row = 0; row < MapConstant.MAP_ROWS ; row++) 
	            {
	                for (int col = 0; col < MapConstant.MAP_COLS; col++) {
	                // Obstacle - Border walls
	                if (!map.getTile(row, col).isExplored()){
	                	mapString.append("");
	                }
	                else if (map.getTile(row, col).isObstacle()) {
	                    mapString.append("1");
	                } else {
	                	mapString.append("0");
	                }
	            }
	                mapString.append(System.lineSeparator());
	        }
	        

	        return mapString.toString();
	 }
	
	
	
	public static String generateMapStringAligned(Map map){
		 StringBuilder mapString = new StringBuilder();

	        for (int row = 0; row < MapConstant.MAP_ROWS ; row++) 
	            {
	                for (int col = 0; col < MapConstant.MAP_COLS; col++) {
	                // Obstacle - Border walls
	                if (!map.getTile(row, col).isObstacle()) {
	                    mapString.append("0");
	                } else {
	                	mapString.append("1");
	                }
	            }
	                mapString.append(System.lineSeparator());
	        }

	        return mapString.toString();
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
	 
	 public static String generateMDFSTring1(Map map){
		 StringBuilder mapString = new StringBuilder("");
		 for(int row = MapConstant.MAP_ROWS -1; row==0;row--){
			 for(int col = 0;col<MapConstant.MAP_COLS;col--){
				 if(map.getTile(MapConstant.MAP_ROWS-row, col).isExplored())
					 mapString.append("1");
				 else
					 mapString.append("0");
			 }
		 }
		 return mapString.toString();
	 }
	 
	 public static String generateMDFSTring2(Map map){
		 StringBuilder mapString = new StringBuilder("");
		 for(int row = MapConstant.MAP_ROWS -1; row==0;row--){
			 for(int col = 0;col<MapConstant.MAP_COLS;col--){
				 if(map.getTile(MapConstant.MAP_ROWS-row, col).isExplored()){
					 if(map.getTile(MapConstant.MAP_ROWS-row, col).isObstacle())
						 mapString.append("1");
					 else
						 mapString.append("0");
				 }
					 
					 
				 else
					 mapString.append("");
			 }
		 }
		 return mapString.toString();
	 }
	
}
