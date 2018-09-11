package search;

import java.util.ArrayList;

import robot.RobotConstant;
import robot.RobotConstant.DIRECTION;
import utility.GridOperation;
public class FastestPath {
	//
	public void aStarSearch(int start, int goal, int[] mapBit, DIRECTION sDirection){
		int[] gScore = new int[300]; //rea
		int[] fscore = new int[300];
		int[] visibilityG = new int[300];
		ArrayList<Integer> expandedNodes = new ArrayList<Integer>();
		
		//render visibility graph
		int[] neighbours = new int[8];
		for(int i = 0;i<300;i++){
			//mark danger area for obstacle
			if(mapBit[i]>0){
				visibilityG[i] = 999;
				neighbours = GridOperation.getRadius(i);
				//marking danger area for radius
				for(int j=0;j<8;j++){
					visibilityG[neighbours[j]] += 15;
				}
			}
		}
		
		
	}
}
