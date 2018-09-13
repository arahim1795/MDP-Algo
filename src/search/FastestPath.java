package search;

import java.util.ArrayList;

import robot.Robot;
import robot.RobotConstant;
import robot.RobotConstant.DIRECTION;
import robot.RobotConstant.MOVEMENT;
import utility.GridOperation;
import map.Map;
import map.Tile;
import map.Constants;
import java.util.HashMap;
import java.util.ArrayList;
public class FastestPath {

	
	private ArrayList<Tile> toVisit;
	private ArrayList<Tile> visited;
	private HashMap<Tile,Tile>parents;
	
	private Tile current;
	private Tile[] neighbours;
	private Map exploredMap;
	private DIRECTION curDir;
	private double[][] gCosts;
	private Robot bot; //KIV
	private int loopcount;
	
	/*
    public FastestPathAlgo(Map exploredMap, Robot bot) {
        this.realMap = null;
        initObject(exploredMap, bot);
    }

    public FastestPathAlgo(Map exploredMap, Robot bot, Map realMap) {
        this.realMap = realMap;
        this.explorationMode = true;
        initObject(exploredMap, bot);
    }*/
	//future uses???
    
	public void init(Map map, Robot bot){
		//initialize variables
		this.bot = bot;
		this.exploredMap = map;
		this.toVisit = new ArrayList<>();
		this.visited = new ArrayList<>();
		this.parents = new HashMap<>();
		this.neighbours = new Tile[4];
		this.current = map.getTile(bot.getRobotRow(), bot.getRobotCol());
		
	}	
	
	
	
	
	
	
	
	
	
	
	
	
	
/*	public void aStarSearch(int start, int goal, int[] mapBit, DIRECTION sDirection){
		int[] gScore = new int[300]; //real cost matrix
		int[] fscore = new int[300]; //heuristic cost matrix
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
		
		
	}*/
}

