package robot;

import java.util.ArrayList;
import robot.RobotConstant.DIRECTION;
import utility.GridOperation;
public class Search {
	//
	public void aStarSearch(int start, int goal, int[] mapBit, DIRECTION sDirection){
		int[] gScore = new int[300];
		int[] fscore = new int[300];
		int[] visibilityG = new int[300];
		ArrayList<Integer> expandedNodes = new ArrayList<Integer>();
		
		//render visibility graph
		int[] neighbours = new int[4];
		for(int i = 0;i<300;i++){
			visibilityG[i] = mapBit[i];
			//mark danger area for obstacle
			if(mapBit[i]>0){
				neighbours = GridOperation.getNeighbour(i);
			}
		}
		
		
	}
}
