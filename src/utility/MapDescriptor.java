package utility;

import map.Map;
import map.MapConstant;
import java.io.*;

public class MapDescriptor {
	public static void 	loadMapfromFile (Map map, String bin){
		//		System.out.println(bin);

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
	public static String generateMDFHex1(Map m){
		String s = generateMDFString1(m);
		return binaryStringtoHexString(s);
	}

	public static String generateMDFHex2(Map m){
		String s = generateMDFString2(m);
		return binaryStringtoHexString(s);
	}

	public static String generateMDFString1(Map map){
		StringBuilder mapString = new StringBuilder("");
		mapString.append("11");
		for(int row = 0; row<MapConstant.MAP_ROWS;row++){
			for(int col = 0;col<MapConstant.MAP_COLS;col++){
				//				 System.out.println(getMDFrow(row)+","+col);
				if(map.getTile(getMDFrow(row), col).isExplored())
					mapString.append("1");
				else
					mapString.append("0");
			}
		}
		mapString.append("11");
		return mapString.toString();
	}

	public static String generateMDFString2(Map map){
		StringBuilder mapString = new StringBuilder("");
		for(int row = 0; row<MapConstant.MAP_ROWS;row++){
			for(int col = 0;col<MapConstant.MAP_COLS;col++){
				//				 System.out.println(getMDFrow(row)+","+col);
				if(map.getTile(getMDFrow(row), col).isExplored()){
					if(map.getTile(getMDFrow(row), col).isObstacle())
						mapString.append("1");
					else
						mapString.append("0");
				}


				else
					mapString.append("");
			}
		}

		if(mapString.length()%8 != 0){
			int padding = 8- mapString.length()%8;

			for(int i=0;i<padding;i++)
				mapString.append("1");
		}
		String result = mapString.toString();
//		System.out.println(result);
//		System.out.println(result.length());
		return result;
	}

	//map -> mdf
	public static int getMDFrow(int r){
		return (MapConstant.MAP_ROWS-1) -r;
	}

	public static int getMDFcol(int c){
		return c;
	}

	//mdf -> map
	public static int getMapRow(int r){
		return getMDFrow(r);
	}

	public static int getMapCol(int c){
		return getMDFcol(c);
	}

	private static String binaryStringtoHexString(String s){
		StringBuilder sb = new StringBuilder("");
		StringBuilder hex = new StringBuilder("");
		for(int i=0;i<s.length();i+=4){
			for(int j=0;j<4;j++){
				hex.append(s.charAt(i+j));
			}
			//			 System.out.println(hex.toString());
			switch(hex.toString()){
			case "0000":sb.append("0");
			break;
			case "0001": sb.append("1");
			break;
			case "0010": sb.append("2");
			break;
			case "0011": sb.append("3");
			break;
			case "0100": sb.append("4");
			break;
			case "0101": sb.append("5");
			break;
			case "0110": sb.append("6");
			break;
			case "0111": sb.append("7");
			break;
			case "1000": sb.append("8");
			break;
			case "1001": sb.append("9");
			break;
			case "1010": sb.append("A");
			break;
			case "1011": sb.append("B");
			break;
			case "1100": sb.append("C");
			break;
			case "1101": sb.append("D");
			break;
			case "1110": sb.append("E");
			break;
			case "1111": sb.append("F");
			break;
			default: sb.append("X");
			break;

			}
			hex = new StringBuilder("");
		}
		return sb.toString();
	}

}