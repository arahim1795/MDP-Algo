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
import java.util.Stack;
import java.util.ArrayList;
public class FastestPath {

	
	private ArrayList<Tile> toVisit;
	private ArrayList<Tile> visited;
	private HashMap<Tile,Tile>parents;//child -> parent
	
	private Tile current;
	private Tile[] neighbours;
	private Map exploredMap;
	private DIRECTION curDir;
	private double[][] gCosts;
	private Robot bot; //KIV
	private int loopCount;
	
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
	
	//constructor for object/algo initialization
	public void init(Map map, Robot bot){
		//initialize variables
		this.bot = bot;
		this.exploredMap = map;
		this.toVisit = new ArrayList<>();
		this.visited = new ArrayList<>();
		this.parents = new HashMap<>();
		this.neighbours = new Tile[4];
		//initialize robot orientation
		this.current = map.getTile(bot.getRobotRow(), bot.getRobotCol());
		this.curDir = bot.getRobotOrientation();
		
		//initialize gCost array
		this.gCosts = new double[Constants.WID][Constants.LEN];
		for(int i=0;i<Constants.LEN;i++){
			for(int j=0;j<Constants.WID;j++){
				if(!canBeVisited(this.exploredMap.getTile(i, j))){
					gCosts[i][j] = RobotConstant.INFINITE_COST;
			}
				else{
					gCosts[i][j] = 1;
				}
		}
		}
		
		//
		toVisit.add(current);
		
		//initialize starting point
		gCosts[bot.getRobotRow()][bot.getRobotCol()] = 0;
		this.loopCount = 0;
	}	
	
	/*Private methods
	 */
	
	//checks if tile can be visited
	private boolean canBeVisited(Tile t) {
        return  !t.isObstacle() && !t.isVirtualWall();
    }
	
	//
	
	private Tile minimumCostTile(int goalRow,int col){
		int size = toVisit.size();
		int minCost = RobotConstant.INFINITE_COST;	
		Tile output = null;
		for (int i=size-1;i>=0;i++){
			int gCost = (int) gCosts[(toVisit.get(i).getRow())][toVisit.get(i).getCol()];
			if(minCost > gCost){
				minCost = gCost;
				output = toVisit.get(i);
			}
		}
		return output;
	}
	
	//return heuristic cost calculation
	private double getHcost(Tile T, int goalRow, int goalCol){
		//no of rows and column moves needed to get to goal
		int movementCost = (T.getRow()-goalRow)+(T.getCol()-goalCol);
		//factor 1 turn if not on same row or col as goal
		int turnCost = 0;
		if(T.getRow() != goalRow || T.getCol() != goalCol){turnCost += RobotConstant.TURN_COST;}
		
		return movementCost + turnCost;
	}
	
	//return target direction from robot to specific tile
	
	private DIRECTION getTargetDirection(int botRow,int botCol, DIRECTION botDir, Tile target){
		//robot column > cell column
		if (botCol - target.getCol() >0){return DIRECTION.LEFT;}
		//robot column < cell column
		else if (botCol - target.getCol() <0){return DIRECTION.LEFT;}
		else{if(botRow-target.getRow()>0){return DIRECTION.UP;}
		else{return DIRECTION.DOWN;}}
		}
	
	//calculate ACTUAL g-cost when travelling from one node to another
	private double getGCost(Tile a, Tile b, DIRECTION aDir) {
        double moveCost = robot.RobotConstant.MOVE_COST; // one movement to neighbor

        double turnCost;
        DIRECTION targetDir = getTargetDirection(a.getRow(), a.getCol(), aDir, b);
        turnCost = getTurnCost(aDir, targetDir);

        return moveCost + turnCost;
	}
    //calcuate ACTUAL turn cost 
    private double getTurnCost(DIRECTION cDir, DIRECTION aDir){
    		int turns = Math.abs(aDir.ordinal()-cDir.ordinal());
    		//turn the other direction if turns >2
    		if(turns >2){turns = turns%2;}
    		return (turns*RobotConstant.TURN_COST);
    	}

    //generates path in reverse from HashMap parents
    private Stack<Tile> getPath(int goalRow, int goalCol){
    	Stack<Tile> actualPath = new Stack<>();
    	Tile temp = this.exploredMap.getTile(goalRow, goalCol);
    	
    	do{
    		actualPath.push(temp);
    		temp = parents.get(temp);
    		if(temp==null){break;}
    	}while(true);
    	
    	return actualPath;
    }
	
	//aStarSearch
	public String searchFastestPath(int goalRow, int goalCol){
		Stack<Tile> path;
		do{
			loopCount++;
			//get next Tile (with minimum cost) to expand 
			current = 	minimumCostTile(goalRow,goalCol);
			
			//point robot 
			if(parents.containsKey(current)){
				curDir = getTargetDirection(parents.get(current).getRow(),parents.get(current).getCol(),curDir,current);
			}
			
			//add current tile to visited
			visited.add(current);
			toVisit.remove(current);
			
			//if goal is found
			if(visited.contains(exploredMap.getTile(goalRow, goalCol))){
				//message : path found
				path = getPath(goalRow,goalCol);
				//printFastestPath(path);
				//return executePath(path, goalRow,goalCol);
				return "toDO" ;
			}
			
			//get list of neighbours (4 cardinal directions)
			//down
			if(exploredMap.isValid(current.getRow()+1, current.getCol())){
				neighbours[0] = exploredMap.getTile(current.getRow()+1, current.getCol());
			}			
			//up
			if (exploredMap.isValid(current.getRow() - 1, current.getCol())) {
                neighbours[1] = exploredMap.getTile(current.getRow() - 1, current.getCol());
                if (!canBeVisited(neighbours[1])) {
                    neighbours[1] = null;
                }
            }			
			//left
            if (exploredMap.isValid(current.getRow(), current.getCol() - 1)) {
                neighbours[2] = exploredMap.getTile(current.getRow(), current.getCol() - 1);
                if (!canBeVisited(neighbours[2])) {
                    neighbours[2] = null;
                }
            }
            //right
            if (exploredMap.isValid(current.getRow(), current.getCol() + 1)) {
                neighbours[3] = exploredMap.getTile(current.getRow(), current.getCol() + 1);
                if (!canBeVisited(neighbours[3])) {
                    neighbours[3] = null;
                }
            }
            
            //iterate and update G values for each neighbour
            for(int i=0;i<4;i++){
            	if(neighbours[i] != null){
            		//check if node is already visited
            		if(visited.contains(neighbours[i])){continue;}
            		//if node is not already in toVisit list
            		if(!toVisit.contains(neighbours[i])){
            			parents.put(neighbours[i], current);
            			this.gCosts[neighbours[i].getRow()][neighbours[i].getCol()]=gCosts[current.getRow()][current.getCol()] + getGCost(current, neighbours[i],curDir);
            			toVisit.add(neighbours[i]);
            		
            		}else{
            			//calculate and update gCost if path to neighbour is shorter
            			double currentGScore = gCosts[neighbours[i].getRow()][neighbours[i].getCol()];
            			double newGScore = gCosts[current.getRow()][current.getCol()] + getGCost(current, neighbours[i], curDir);
            			if(newGScore < currentGScore){
            				gCosts[neighbours[i].getRow()][neighbours[i].getCol()] = newGScore;
            				parents.put(neighbours[i], current); //change parent of neighbour node to current node
            			}
            		}
            	}
            }
			
		}while(!toVisit.isEmpty());
		return null;
	}
	
	
	//executes the fastest path and returns a path String
	
	private String executeFastestPath(Stack<Tile> path, int goalRow, int goalCol){
		StringBuilder outputString = new StringBuilder();
		
		Tile temp = path.pop();
		DIRECTION targetDir;
		
		ArrayList<MOVEMENT> movementList = new ArrayList<>();
		Robot tempBot = new Robot(1,1);
		//tempBot.setSpeed(0);
		//while robot position not on goal tile
		while((tempBot.getRobotRow()!= goalRow) || (tempBot.getRobotCol()!= goalCol)){
			//if robot on path tile
			if(tempBot.getRobotRow()==temp.getRow() && tempBot.getRobotCol()==temp.getCol()){
				temp = path.pop();
				
			}
			targetDir = getTargetDirection(tempBot.getRobotRow(),tempBot.getRobotCol(),tempBot.getRobotOrientation(),temp);
			
			MOVEMENT m;
			
			//if robot not facing correct direction, orientate robot
			if(tempBot.getRobotOrientation() != targetDir){
				m = getTargetMove(tempBot.getRobotOrientation(),targetDir);
			}else{
				m = MOVEMENT.FORWARD;
			}
			System.out.println("Movement " + MOVEMENT.print(m) + " from (" + tempBot.getRobotRow()+ ", " + tempBot.getRobotCol() + ") to (" + temp.getRow() + ", " + temp.getCol() + ")");

            tempBot.move(m);
            movements.add(m);
            outputString.append(MOVEMENT.print(m));
		}
		
	}

	//return a movement given target direction and current direction
	private MOVEMENT getTargetMove(DIRECTION current, DIRECTION goal) {
        switch (current) {
            case UP:
                switch (goal) {
                    case UP:
                        return MOVEMENT.ERROR;
                    case DOWN: 
                        return MOVEMENT.TURNLEFT;
                    case LEFT:
                        return MOVEMENT.TURNLEFT;
                    case RIGHT:
                        return MOVEMENT.TURNRIGHT;
                }
                break;
            case DOWN:
                switch (goal) {
                    case UP:
                        return MOVEMENT.TURNLEFT;
                    case DOWN:
                        return MOVEMENT.ERROR;
                    case LEFT:
                        return MOVEMENT.TURNRIGHT;
                    case RIGHT:
                        return MOVEMENT.TURNLEFT;
                }
                break;
            case LEFT:
                switch (goal) {
                    case UP:
                        return MOVEMENT.TURNRIGHT;
                    case DOWN:
                        return MOVEMENT.TURNLEFT;
                    case LEFT:
                        return MOVEMENT.ERROR;
                    case RIGHT:
                        return MOVEMENT.TURNLEFT;
                }
                break;
            case RIGHT:
                switch (goal) {
                    case UP:
                        return MOVEMENT.TURNLEFT;
                    case DOWN:
                        return MOVEMENT.TURNRIGHT;
                    case LEFT:
                        return MOVEMENT.TURNLEFT;
                    case RIGHT:
                        return MOVEMENT.ERROR;
                }
        }
        return MOVEMENT.ERROR;
    }
}

