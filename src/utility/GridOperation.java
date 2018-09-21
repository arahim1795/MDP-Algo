package utility;
import map.Constants;
public class GridOperation {
	//miscellanous
	public static int[] populate(int[] neighbours){
		int[] output = neighbours;
		for (int i = 0; i < output.length;i++){
			if(output[i] == 0){output[i] = -1;}
		}
		return output;
	}
	//positioning
	public static int getRow(int gridNum){
		return gridNum/Constants.MAP_COLS+1;
	}
	public static int getCol(int gridNum){
		return (gridNum%Constants.MAP_COLS)+1;
	}
	public static int index(int row, int col){
		return ((row-1)*Constants.MAP_COLS+col);
	}
	//navigation
	public static int incRow(int gridNum){
		//+15
		return gridNum+Constants.MAP_COLS;
	}
	public static int decRow(int gridNum){
		//-15
		return gridNum-Constants.MAP_COLS;
	}
	public static int incCol(int gridNum){
		return gridNum+1;
	}
	public static int decCol(int gridNum){
		return gridNum-1;
	}
	
	//get grid neighbours for searching
	public static int[] getNeighbour(int gridNum){
		int[] output = new int[4];
		for(int i=0;i<4;i++){
			int j = 0;
			switch(i){
			//up
			case 1 : if(getRow(gridNum)-1>0){
				output[j] = decRow(gridNum);
				j++;
			};
			//down
			case 2 : if(getRow(gridNum)+1<=Constants.MAP_ROWS){
				output[j] = incRow(gridNum);
				j++;
			};
			//left
			case 3 : if((getCol(gridNum)-1)<=0){
				output[j] = decCol(gridNum);
				j++;
			}
			//right
			case 4 : if((getCol(gridNum)+1)<=15){
				output[j] = incCol(gridNum);
				j++;
			}
			}
		}
		return output;
	}
	
	//get grids in radius 
	public static int[] getRadius(int gridNum){
		int[] output = new int[8];
		int i = getRow(gridNum);
		int j = getCol(gridNum);
		int n = 0; //placeholder
		int z = 0; //array position
		
		for(int x = i-1; x<= (i+1);x++){
			for(int y = j-1;y<=(j+1);y++){
				n = index(x,y);
				if(n == gridNum||n<0||n>299){;}
				else{output[z] = n;z++;}
			}
		}
		output = populate(output);
		return output;
	}
	
	//prevent instantiation
	private GridOperation(){};
}
