package search;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import java.util.concurrent.TimeUnit;
import java.lang.Math;
import robot.Robot;
import robot.RobotConstant;
import robot.RobotConstant.DIRECTION;
import robot.RobotConstant.MOVEMENT;
import utility.Comms;
import map.Map;
import map.Tile;
import map.MapConstant;
import map.Simulator;


/**
 * @author 
 */
public class FastestPath {

	private ArrayList<Tile> toVisit;
	private ArrayList<Tile> visited;
	private HashMap<Tile,Tile>parents;//child -> parent

	private Tile current;
	private Tile[] neighbours;
	private Map exploredMap;
	private DIRECTION curDir;
	private Robot dummyBot;
	private double[][] gCosts;
	private Robot bot; //KIV
	private int loopCount;
	private boolean exploreMode = false;


	private StringBuilder log;
	private StringBuilder Hlog;

	/**
	 * 
	 * @param mapExplore
	 * @param bot
	 */
	public FastestPath(Map exploredMap, Robot bot) {
		init(exploredMap, bot);
	}

	/**
	 * 
	 * @param map
	 * @param bot
	 */
	public void init(Map map, Robot bot){
		//TODO dummy debug
		System.out.println("FP init");

		//init log file
		this.log = new StringBuilder();
		this.Hlog = new StringBuilder();
		//initialize realtime variables
		this.bot = bot;
		this.exploredMap = map;

		//init dummy
		this.dummyBot = new Robot(false, bot.getRow(), bot.getCol(), bot.getDir(), null);
		//init arrays
		initArrays();

		//initialize robot orientation
		initCurrent(bot.getRow(),bot.getCol(),bot.getDir());

		initGCosts(this.current);

		this.loopCount = 0;
		//TODO
		//System.out.println(bot.getRobotRow() + "." +bot.getRobotCol());
		//printGCosts();
	}	

	//diagnostic
	public void fpDiag_Init(){
		System.out.println("==========FP DIAG==============");
		System.out.println("Robot");
		System.out.println(bot.getRow()+","+bot.getCol()+","+bot.getDir());
		System.out.println("Current");
		System.out.println(current.getRow()+","+current.getCol());
		System.out.println(curDir);
		System.out.println("==========FP DIAG END===========");
	}
	public void fpDiag_Search(int goalRow,int goalCol){
		System.out.println("==========FPsearch DIAG========");
		System.out.println("Goal");
		System.out.println(goalRow + "," +goalCol);
		System.out.println("Current");
		System.out.println(current.getRow()+","+current.getCol());
		System.out.println(curDir);
		System.out.println("=======FPsearch DIAG END========");
	}
	/*Private methods*/
	private void initArrays() {
		this.toVisit = new ArrayList<>();
		this.visited = new ArrayList<>();
		this.parents = new HashMap<>();		
		this.neighbours = new Tile[4];

	}
	private void initCurrent(int row, int col, DIRECTION dir){
		this.current = exploredMap.getTile(row, col);
		this.curDir = dir;
		System.out.println(current.getRow()+"," + current.getCol());
	}
	private void initGCosts(Tile curPos){
		//initialize gCost array
		this.gCosts = new double[MapConstant.MAP_ROWS][MapConstant.MAP_COLS];
		for(int i=0;i<MapConstant.MAP_ROWS;i++){
			for(int j=0;j<MapConstant.MAP_COLS;j++){
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
		gCosts[bot.getRow()][bot.getCol()] = 0;

	}

	//checks if tile can be visited
	/**
	 * 
	 * @param t
	 * @return
	 */
	private boolean canBeVisited(Tile t) {
		if(t.isObstacle()||t.isVirtualWall())
			return false;
		for(int i =-1;i<=1;i++){
			for(int j=-1;j<=1;j++){
				if(exploredMap.isObstacleTile(t.getRow()+i,t.getCol()+ j)||!exploredMap.isExploredTile(t.getRow()+i,t.getCol()+ j))
					return false;
			}
		}
		return  true;
	}

	/**
	 * 
	 * @param goalRow
	 * @param col
	 * @return
	 */
	private Tile minimumCostTile(int goalRow,int goalCol){
		int size = toVisit.size();
		int minCost = RobotConstant.INFINITE_COST;	
		Tile output = null;
		for (int i=size-1;i>=0;i--){
			int gCost = (int) gCosts[(toVisit.get(i).getRow())][toVisit.get(i).getCol()];
			int hCost = (int)getHCost(toVisit.get(i),goalRow,goalCol);
			int tCost = gCost + hCost;
			if(minCost > tCost){
				minCost = tCost;
				output = toVisit.get(i);
			}
		}
		return output;
	}

	//return heuristic cost calculation
	/**
	 * 
	 * @param T
	 * @param goalRow
	 * @param goalCol
	 * @return
	 */
	private double getHCost(Tile T, int goalRow, int goalCol){
		//no of rows and column moves needed to get to goal
		int movementCost = Math.abs(T.getRow()-goalRow)+Math.abs(T.getCol()-goalCol);
		//factor 1 turn if not on same row or col as goal
		int turnCost = 0;
		if(T.getRow() != goalRow || T.getCol() != goalCol){turnCost += RobotConstant.TURN_COST;}

		return movementCost + turnCost;
	}

	//return target direction from robot to specific tile
	/**
	 * 
	 * @param botRow
	 * @param botCol
	 * @param botDir
	 * @param target
	 * @return
	 */
	private DIRECTION getTargetDirection(int botRow,int botCol, DIRECTION botDir, Tile target){
		//robot column > cell column
		if (botCol - target.getCol() >0){return DIRECTION.LEFT;}
		//robot column < cell column
		else if (botCol - target.getCol() <0){return DIRECTION.RIGHT;}
		else{if(botRow-target.getRow()>0){return DIRECTION.UP;}
		else{return DIRECTION.DOWN;}}
	}

	//calculate ACTUAL g-cost when travelling from one node to another
	/**
	 * 
	 * @param a
	 * @param b
	 * @param aDir
	 * @return
	 */
	private double getGCost(Tile a, Tile b, DIRECTION aDir) {
		double moveCost = robot.RobotConstant.MOVE_COST; // one movement to neighbor

		double turnCost;
		DIRECTION targetDir = getTargetDirection(a.getRow(), a.getCol(), aDir, b);
		turnCost = getTurnCost(aDir, targetDir);

		return moveCost + turnCost;
	}

	//calcuate ACTUAL turn cost 
	/**
	 * 
	 * @param cDir
	 * @param aDir
	 * @return
	 */
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
	//
	public void moveBotfromString(String s, boolean realRun, boolean cal){
		if(!realRun)
			moveBotfromChars(s);
		else
			moveBotfromString(s, cal);
	}
	public void moveBotfromChars(String movementString){
		ArrayList<MOVEMENT> movementList = new ArrayList<>();
		char c;
		for(int i=0;i<movementString.length();i++){
			c = movementString.charAt(i);
			//System.out.print(c+"\n");
			movementList.add(MOVEMENT.get(c));
			//System.out.print(MOVEMENT.get(c)+"\n");
		}

		if (Simulator.realRun) {
			for(MOVEMENT m : movementList){
				System.out.println("Move: " + MOVEMENT.print(m));
				bot.move(m, true, true);
			}
		}
		else{
			for(MOVEMENT m : movementList){
				System.out.println("Move: " + MOVEMENT.print(m));
				bot.moveDigital(m);
			}
		}

	}

	private void moveBotfromString(String s, boolean cal) {
		MOVEMENT m;
		@SuppressWarnings("unused")
		String msg;
		try{
			System.out.println("Attempting moveBotfromString");
			System.out.println(s);

			if (Simulator.realRun) {
				if (cal) {
					s = 'M' + s + "T/";
				} else {
					s = 'M' + s + '/';
				}
				Comms.sendMsg(Comms.ARDnAND, Comms.MULTI, s);
			}
			System.out.println("Message sent to Android");



		}catch(Exception e){
			System.out.println("moveBotfromString did not send");
			e.printStackTrace();
		}

		for(int i=0;i<s.length();i++){
			// while(true){
			//	msg = Comms.receiveMsg();
			//	if(msg.equals(Comms.ACK))
			//	break;		
			//	}
			try {
				TimeUnit.MILLISECONDS.sleep(350);
			} catch (Exception e){
				System.out.println("moveBotfromString : Sleep did not work");
			}
			m=MOVEMENT.get(s.charAt(i));
			try {
				TimeUnit.MILLISECONDS.sleep(100);
				bot.moveDigital(m);
			} catch (Exception e){
				System.out.println("moveBotfromString : Sleep did not work");
			}
			System.out.println("Move: " + MOVEMENT.print(m));
		}

		System.out.println("moveBotfromString successful!");

	}

	//overloaded method 
	public String searchFastestPath(int startRow, int startCol, int goalRow, int goalCol){
		initArrays();
		initCurrent(startRow,startCol,dummyBot.getDir());
		initGCosts(exploredMap.getTile(startRow, startCol));
		dummyBot.setBotPos(startRow, startCol);
		return searchFastestPath(goalRow,goalCol);
	}

	//aStarSearch
	/**
	 * 
	 * @param targetRow
	 * @param targetCol
	 * @return
	 */
	public String searchFastestPath(int goalRow, int goalCol){
		Stack<Tile> path;
		//fpDiag_Search(goalRow,goalCol);
		//fpDiag_Init();

		printHCosts(goalRow,goalCol);
		log.append(utility.MapDescriptor.generateMapStringAligned(exploredMap));
		log.append(System.lineSeparator());
		do{
			loopCount++;
			//get next Tile (with minimum cost) to expand 

			current = 	minimumCostTile(goalRow,goalCol);

			//TODO dummy text
			String visitlog = new String("visiting" + "(" + current.getRow() + "," + current.getCol()+")");
			System.out.println(visitlog);

			log.append(visitlog);
			log.append(System.lineSeparator());
			log.append(System.lineSeparator());

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
				printPath(path);
				writeGCosts(log);
				return executeFastestPath(path, goalRow,goalCol, false);
			}

			//get list of neighbours (4 cardinal directions)
			//down
			if(Map.isValidTile(current.getRow()+1, current.getCol())){
				neighbours[0] = exploredMap.getTile(current.getRow()+1, current.getCol());
				if (!canBeVisited(neighbours[0])) {
					neighbours[0] = null;
				}
			}			
			//up
			if (Map.isValidTile(current.getRow() - 1, current.getCol())) {
				neighbours[1] = exploredMap.getTile(current.getRow() - 1, current.getCol());
				if (!canBeVisited(neighbours[1])) {
					neighbours[1] = null;
				}
			}			
			//left
			if (Map.isValidTile(current.getRow(), current.getCol() - 1)) {
				neighbours[2] = exploredMap.getTile(current.getRow(), current.getCol() - 1);
				if (!canBeVisited(neighbours[2])) {
					neighbours[2] = null;
				}
			}
			//right
			if (Map.isValidTile(current.getRow(), current.getCol() + 1)) {
				neighbours[3] = exploredMap.getTile(current.getRow(), current.getCol() + 1);
				if (!canBeVisited(neighbours[3])) {
					neighbours[3] = null;
				}
			}


			//iterate and update G values for each neighbour
			log.append("canVisit: ");
			for(int i=0;i<4;i++){
				if(neighbours[i] != null){
					//check if node is already visited
					if(visited.contains(neighbours[i])){continue;}
					else{
						log.append("("+neighbours[i].getRow()+","+neighbours[i].getCol()+")");


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
				log.append(System.lineSeparator());}
			log.append("toVisit : ");
			for(Tile t : toVisit){
				log.append("("+t.getRow()+","+t.getCol()+") ["+(int)gCosts[t.getRow()][t.getCol()]+"]");
				log.append(" ; ");
			}

			log.append(System.lineSeparator());
			log.append(System.lineSeparator());
			printGCosts();
			//System.out.println();

		}while(!toVisit.isEmpty());
		return null;
	}


	//executes the fastest path and returns a path String
	/**
	 * 
	 * @param path
	 * @param goalRow
	 * @param goalCol
	 * @return
	 */
	private String executeFastestPath(Stack<Tile> path, int goalRow, int goalCol, boolean moveBot){
		//TODO
		System.out.println("executing");

		StringBuilder outputString = new StringBuilder();

		Tile temp = path.pop();
		DIRECTION targetDir;

		ArrayList<MOVEMENT> movementList = new ArrayList<>();
		Robot tempBot = new Robot(dummyBot.isRealBot(), dummyBot.getRow(), dummyBot.getCol(), dummyBot.getDir(), dummyBot.getSpeed());
		System.out.println(dummyBot.getRow() + "," + dummyBot.getCol());
		//tempBot.setSpeed(0);
		//while robot position not on goal tile
		
		MOVEMENT cal = MOVEMENT.CALIBRATE, left = MOVEMENT.TURNLEFT, right = MOVEMENT.TURNRIGHT;
		
		int calibrateCounter = 0;
		while ( (tempBot.getRow() != goalRow) || (tempBot.getCol() != goalCol) ) {
			// if robot on path tile
			if (tempBot.getRow() == temp.getRow() && tempBot.getCol() == temp.getCol()) {
				temp = path.pop();
			}
			
			if (calibrateCounter >= 6) {
				if (Explore.canCalibrate(1, tempBot, exploredMap)) {
					outputString.append(MOVEMENT.print(cal));
					calibrateCounter = 0;
				} else if (Explore.canCalibrate(2, tempBot, exploredMap)) {
					outputString.append(MOVEMENT.print(left));
					outputString.append(MOVEMENT.print(cal));
					outputString.append(MOVEMENT.print(right));
					calibrateCounter = 0;
				} else if (Explore.canCalibrate(3, tempBot, exploredMap)){
					outputString.append(MOVEMENT.print(right));
					outputString.append(MOVEMENT.print(cal));
					outputString.append(MOVEMENT.print(left));
					calibrateCounter = 0;
				}
			}
			
			
			targetDir = getTargetDirection(tempBot.getRow(),tempBot.getCol(),tempBot.getDir(),temp);

			MOVEMENT m;

			//if robot not facing correct direction, orientate robot
			if (tempBot.getDir() != targetDir) {
				System.out.println("Target Direction: "+targetDir+", Bot Direction"+tempBot.getDir());
				m = getTargetMove(tempBot.getDir(),targetDir);
			} else {
				m = MOVEMENT.FORWARD;
			}

			System.out.println("Movement " + MOVEMENT.print(m) + " from (" + tempBot.getRow()+ ", " + tempBot.getCol() + ") to (" + temp.getRow() + ", " + temp.getCol() + ")");

			tempBot.move(m, true, true);
			movementList.add(m);
			calibrateCounter++;
			outputString.append(MOVEMENT.print(m));
		}
		
		//store current direction
		System.out.println(tempBot.getDir());
		dummyBot.setRobotDir(tempBot.getDir());

		if(!bot.isRealBot()||this.exploreMode){
			for (MOVEMENT n : movementList){
				if(n==MOVEMENT.FORWARD){
					if(!this.canMoveForward()){
						System.out.println("Early termination of fastest path execution.");
						return "T";
					}
				}
				if(moveBot){
					bot.move(n, true, true);
					exploredMap.repaint();
				}

				if (this.exploreMode) {
					bot.moveSensor();
					// TODO correct sensing to do correct stuff
					// bot.sense(this.exploredMap, this.realMap);
					this.exploredMap.repaint();
				}


			}
		}

		//		else if(moveBot){
		//			for(MOVEMENT x : movementList){
		//				bot.move(x);
		//			}
		//
		//			
		//			int fCount = 0; //forwardCount
		//
		//            for (MOVEMENT x : movementList) {
		//                if (x == MOVEMENT.FORWARD) {
		//                    fCount++;
		//                    if (fCount == 10) {
		//                        bot.moveForwardMultiple(fCount);
		//                        fCount = 0;
		//                        exploredMap.repaint();
		//                    }
		//                } else if (x == MOVEMENT.TURNRIGHT || x == MOVEMENT.TURNLEFT) {
		//                    if (fCount > 0) {
		//                        bot.moveForwardMultiple(fCount);
		//                        fCount = 0;
		//                        exploredMap.repaint();
		//                    }
		//
		//                    bot.move(x);
		//                    exploredMap.repaint();
		//                }
		//            }
		//
		//            if (fCount > 0) {
		//                bot.moveForwardMultiple(fCount);
		//                exploredMap.repaint();
		//            }
		//		}
		//TODO : exploration code?

		System.out.println("\nMovements: " + outputString.toString());
		return outputString.toString();

	}

	//return a movement given target direction and current direction
	/**
	 * 
	 * @param current
	 * @param goal
	 * @return
	 */
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

	//determines if path ahead can be traversed
	private boolean canMoveForward() {
		int row = bot.getRow();
		int col = bot.getCol();
		int rowMIN=-5,rowMAX=-5,colMIN=-5,colMAX=-5;
		switch (bot.getDir()) {

		case UP:
			rowMIN=-2;rowMAX=-2;colMIN=-1;colMAX=1;break;          
		case RIGHT:
			rowMIN=-1;rowMAX=1;colMIN=2;colMAX=2;break;
		case DOWN:
			rowMIN=2;rowMAX=2;colMIN=0;colMAX=0;break;
		case LEFT:
			rowMIN=-1;rowMAX=1;colMIN=-2;colMAX=-2;break;
		}
		for(int x=rowMIN;x<=rowMAX;x++){
			for(int y=colMIN;y<=colMAX;y++){
				if(Map.isValidTile(row+x, col+y)&&this.exploredMap.getTile(row+x, col+y).isObstacle()){return false;}        		
			}
		}
		//if no obstacles found
		return true;
	}
	private void printPath(Stack<Tile> path) {
		System.out.println("\nLooped " + loopCount + " times.");
		System.out.println("The number of steps is: " + (path.size() - 1) + "\n");

		@SuppressWarnings("unchecked")
		Stack<Tile> pathForPrint = (Stack<Tile>) path.clone();
		Tile temp;
		System.out.println("Path:");
		while (!pathForPrint.isEmpty()) {
			temp = pathForPrint.pop();
			if (!pathForPrint.isEmpty()) System.out.print("(" + temp.getRow() + ", " + temp.getCol() + ") --> ");
			else System.out.print("(" + temp.getRow() + ", " + temp.getCol() + ")");
		}

		System.out.println("\n");
	}

	private void printHCosts(int goalRow, int goalCol){
		int n;
		String line;
		String header = new String("   ");
		for(int k=0;k<MapConstant.MAP_COLS;k++){
			header += k;
			if(k<10){header += " ";}
			header+="|";
		}
		Hlog.append(header);
		Hlog.append(System.lineSeparator());
		Hlog.append(System.lineSeparator());
		for (int i = 0; i < MapConstant.MAP_ROWS; i++) {
			line = new String(i + " ");
			if(i<10){ line+=" ";}
			for (int j = 0; j < MapConstant.MAP_COLS; j++) {
				n = (int)this.getHCost(this.exploredMap.getTile(i, j), goalRow, goalCol);
				if(n<10){
					line+=" ";
				}

				line+= n+"|";
			}

			line+= "\n";

			Hlog.append(line);
			Hlog.append(System.lineSeparator());

		}
		Hlog.append(System.lineSeparator());
		writeHCosts(Hlog);
	}
	private void printGCosts() {
		String header = new String("   ");
		for(int k=0;k<MapConstant.MAP_COLS;k++){
			header += k;
			if(k<10){header += " ";}
			header+="|";
		}
		log.append(header);
		log.append(System.lineSeparator());
		log.append(System.lineSeparator());
		for (int i = 0; i < MapConstant.MAP_ROWS; i++) {
			String line = new String(i + " ");
			if(i<10){ line+=" ";}
			for (int j = 0; j < MapConstant.MAP_COLS; j++) {
				int n = (int) gCosts[i][j];
				if(n == 9999){ n = -1;}
				if(n < 10 && n>=0){           		
					line+=" ";
				}

				line+=n+"|";
			}
			line+= "\n";
			//System.out.println(line);
			log.append(line);
			log.append(System.lineSeparator());

		}
		log.append(System.lineSeparator());
	}

	private static void writeGCosts(StringBuilder s){
		String fileName = "FPlog";
		String outStr = s.toString();

		if (!fileName.endsWith(".txt")) {
			fileName += ".txt";
		}
		try{

			FileWriter fw = new FileWriter(fileName);
			fw.write(outStr);
			fw.flush();
			fw.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	private static void writeHCosts(StringBuilder s){
		String fileName = "HCostlog";
		String outStr = s.toString();
		// Allows overriding of existing text files
		if (!fileName.endsWith(".txt")) {
			fileName += ".txt";
		}
		try{
			// Change file writing part to a better implementation
			FileWriter fw = new FileWriter(fileName);
			//TODO debug dummy
			fw.write(outStr);
			fw.flush();
			fw.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	public void printDecisionlog(Tile g){
		for(Tile t : toVisit){
			System.out.println("toVisit: "+t.getRow()+","+t.getCol()+" : "+getGCost(t, g, bot.getDir()));
		}

	}
}
