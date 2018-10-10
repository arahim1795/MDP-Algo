package map;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import java.io.*;

import map.MapConstant;

import robot.RobotConstant;
import robot.RobotConstant.DIRECTION;
import robot.Robot;

import search.FastestPath;
import search.Explore;
import utility.Comms;
import utility.MapDescriptor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.TimeUnit;

public class Simulator {

	//mainFrame for the application
	public static JFrame mainFrame = null;

	//mainPanel for laying out different views
	private static JPanel mapCards = null; //for viewing the different maps
	private static JPanel mainButtons = null;
	private static JPanel mapButtons = null;

	private static Map realMap = null;
	private static Map exploredMap = null;

	// The robot
	private static robot.Robot roboCop = null;
	private static final boolean realRun = false;
	private static boolean ready = false;
	private static boolean exploredDone = false;
	private static boolean noInterrupt = true;

	// File name of the loaded map
	private static int timeLimit;
	private static int coverageValue;

	public static void main(String[] args) {

    	System.out.println("Starting Simulator...");
		roboCop = new Robot(RobotConstant.DEFAULT_START_ROW, RobotConstant.DEFAULT_START_COL, realRun);

		realMap = new Map(roboCop);
		realMap.setAllExplored();
		exploredMap = new Map (roboCop);


		// Calculate map width & length based on grid size
		// mapWidth = MapConstant.MAP_COLS * GraphicConstant.TILE_SIZE;
		// mapLength = MapConstant.MAP_ROWS * GraphicConstant.TILE_SIZE;

		//init main display mainFrame
		mainFrame = new JFrame("Map Simulator");
		mainFrame.setLocation(120,80);
		mainFrame.setSize(new Dimension(1382, 648));
		mainFrame.setLayout(new GridLayout(1,2,3,3));

		//JPanel for map views
		mapCards = new JPanel(new CardLayout());
		mapCards.add(exploredMap, "Main");

		addMainButtons();
		addMapButtons();

		mainFrame.add(mapCards);
		mainFrame.add(mainButtons);
		mainFrame.add(mapButtons);

		initMainLayout();

		//add map panel and button panel to main frame
		Container contentPane = mainFrame.getContentPane();
		contentPane.add(mapCards, BorderLayout.CENTER); //add mapCards to main frame's content pane
		contentPane.add(mainButtons, BorderLayout.EAST);
		contentPane.add(mapButtons, BorderLayout.SOUTH);
		
		if(realRun){
		Comms.openSocket();
		System.out.println("Comms Open");
		}

		mainFrame.setVisible(true);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
	}

	private static void initMainLayout() {
		if (!realRun) {
			mapCards.add(realMap, "REAL_MAP");
		}
		mapCards.add(exploredMap, "EXPLORATION");


		CardLayout cl = ((CardLayout) mapCards.getLayout());
		if (!realRun) {
			cl.show(mapCards, "REAL_MAP");
		} else {
			cl.show(mapCards, "EXPLORATION");
		}
	}

	private static void switchMap(){
		CardLayout cl = ((CardLayout) mapCards.getLayout());
		cl.show(mapCards, "EXPLORATION");
	}


	private static void formatButton(JButton btn) {
		btn.setFont(new Font("Arial", Font.BOLD, 18));
		btn.setMargin(new Insets(10, 15, 10, 15));
		btn.setFocusPainted(false);
	}

	private static void addMainButtons() {

		//init classes
		
//		class receiveMidPoint extends SwingWorker<Void, Void> {
//
//			//private Simulator simulator = null;
//			private final String prReallyDone = "ReallyDone";
//
//			private void whenReallyDone() {
//				//simulator.afterWorkerFinishes();
//				System.out.println("FP done");
//			}
//
//			public receiveMidPoint() {
//
//				getPropertyChangeSupport().addPropertyChangeListener(prReallyDone,
//						new PropertyChangeListener() {
//					public void propertyChange(PropertyChangeEvent e) {
//						if (e.getNewValue().equals(true)) {
//							whenReallyDone();
//						}
//					}
//				});
//			}
//
//			protected Void doInBackground() throws Exception {
//				
//				String msg;
//
//				while (true) {
//					System.out.println("Waiting for Mid Point");
//					msg = Comms.receiveMsg();
////					if(Comms.receiveMsg()=="startFP"){
//					if(Comms.isMidPointCoor(msg)){		            	
//						break;
//					}
//				}
//				//
//				System.out.println("Mid Point received!");
//				
//
//
//				firePropertyChange(prReallyDone, false, true);
//				return null;
//			}
//
//		}

		class fastestPathThread extends SwingWorker<Void, Void> {

			//private Simulator simulator = null;
			private final String prReallyDone = "ReallyDone";

			private void whenReallyDone() {
				//simulator.afterWorkerFinishes();
				System.out.println("FP done");
			}

			public fastestPathThread() {
				//this.simulator = sim;

				getPropertyChangeSupport().addPropertyChangeListener(prReallyDone,
						new PropertyChangeListener() {
					public void propertyChange(PropertyChangeEvent e) {
						if (e.getNewValue().equals(true)) {
							whenReallyDone();
						}
					}
				});
			}

			protected Void doInBackground() throws Exception {
				long idleTime = System.currentTimeMillis();
				boolean fpReady = false;
				
				System.out.println("FP Ready");
				while (true) {
					if(!realRun){
						fpReady = ready;
					}
					else if(Comms.receiveMsg()=="fp"){
						fpReady = true;
					}
					if(System.currentTimeMillis()-idleTime >7000){
						idleTime = System.currentTimeMillis();
						System.out.println("FP Waiting...");
					}
					else
						System.out.print("");	
					if(fpReady){		            	
						break;
					}
				}
				//
				System.out.println("FP Running");
				Map FPMap;
				if(exploredDone || realRun){
					FPMap = exploredMap;            		
				}
				else
					FPMap = realMap;
				//
				FastestPath fastestPathAlgo = new FastestPath(FPMap, roboCop);
				String outStr;
				if(FPMap.hasMidPoint()){	        	
					outStr = fastestPathAlgo.searchFastestPath(FPMap.getMidPointRow(),FPMap.getMidPointCol());
					outStr +=fastestPathAlgo.searchFastestPath(FPMap.getMidPointRow(),FPMap.getMidPointCol(),MapConstant.GOAL_GRID_ROW, MapConstant.GOAL_GRID_COL);
				} 
				else{
					outStr = fastestPathAlgo.searchFastestPath(MapConstant.GOAL_GRID_ROW, MapConstant.GOAL_GRID_COL);
				}
				fastestPathAlgo.moveBotfromString(outStr,realRun);

				firePropertyChange(prReallyDone, false, true);
				return null;
			}

		}

		class explorationThread extends SwingWorker<Void, Void> {
			private final String exploreComplete = "Complete";
			private void whenReallyDone() {
				System.out.println("Exploration Complete");
			}

			public explorationThread() {
				getPropertyChangeSupport().addPropertyChangeListener(exploreComplete, new PropertyChangeListener() {
					public void propertyChange(PropertyChangeEvent e) { 
						if (e.getNewValue().equals(true)) whenReallyDone();
					}
				});
			}
			//TODO main code 
			protected Void doInBackground() throws Exception {
				long idleTime = System.currentTimeMillis();
				boolean exReady = false;
				String msg;
				System.out.println("Explore Ready");
				if(realRun)
					Comms.sendMsg(Comms.ARDUINO, Comms.SET, null);
				switchMap();
				while (true) {
					
					if(!realRun){
						exReady = ready;
					}
					
					else{
						msg = Comms.receiveMsg();
						if(msg.equals(Comms.EX)){
							exReady = true;
						}
						else if(msg.startsWith(Comms.MP)||msg.startsWith(Comms.SP)){
							if(msg.startsWith(Comms.MP))
								exploredMap.setMidPoint(Comms.readCoor("ROW", msg), Comms.readCoor("COL", msg));
							else if(msg.startsWith(Comms.SP)){
								roboCop.setBotPos(Comms.readCoor("ROW", msg), Comms.readCoor("COL", msg));
							}
						}
					}
					if(System.currentTimeMillis()-idleTime >7000){
						idleTime = System.currentTimeMillis();
						System.out.println("Explore Waiting");
					}
					else
						System.out.print("");
					if (exReady) break;
				}

				System.out.println("Exploration Running");

				Explore explore;
				explore = new Explore(roboCop, exploredMap, realMap, 20, 100);
				explore.setupExplore();	
				while(noInterrupt && !explore.runFinished()){
					explore.exploreSim();
				} 
				explore.goToStart();
				Comms.sendMsg(Comms.ARDUINO, "END", null);

				exploredMap.repaint();

				ready = false;
				new fastestPathThread().execute();
				//
				firePropertyChange(exploreComplete, false, true);
				return null;
			}
		}

		class explorationCoverageThread extends SwingWorker<Void, Void> {
			private final String exploreComplete = "Complete";
			private void whenReallyDone() {
				System.out.println("Exploration Complete");
			}

			public explorationCoverageThread() {
				getPropertyChangeSupport().addPropertyChangeListener(exploreComplete, new PropertyChangeListener() {
					public void propertyChange(PropertyChangeEvent e) { 
						if (e.getNewValue().equals(true)) whenReallyDone();
					}
				});
			}

			protected Void doInBackground() throws Exception {
				while (true) {
					switchMap();
					System.out.println("Can Run!");
					if (ready) break;
				}

				System.out.println("Exploration Running");

				Explore explore;
				explore = new Explore(roboCop, exploredMap, realMap, 20, coverageValue);
				explore.setupExplore();	
				while(noInterrupt && !explore.runFinished()){
					explore.exploreSim();
				} 
				explore.goToStart();

				exploredMap.repaint();

				ready = false;
				new fastestPathThread().execute();
				//
				firePropertyChange(exploreComplete, false, true);
				return null;
			}
		}

		class explorationTimeThread extends SwingWorker<Void, Void> {
			private final String exploreComplete = "Complete";
			private void whenReallyDone() {
				System.out.println("Exploration Complete");
			}

			public explorationTimeThread() {
				getPropertyChangeSupport().addPropertyChangeListener(exploreComplete, new PropertyChangeListener() {
					public void propertyChange(PropertyChangeEvent e) { 
						if (e.getNewValue().equals(true)) whenReallyDone();
					}
				});
			}

			protected Void doInBackground() throws Exception {
				while (true) {
					switchMap();
					System.out.println("Can Run!");
					if (ready) break;
				}

				System.out.println("Exploration Running");

				Explore explore;
				explore = new Explore(roboCop, exploredMap, realMap, timeLimit, 100);
				explore.setupExplore();	
				while(noInterrupt && !explore.runFinished()){
					explore.exploreSim();
				} 
				explore.goToStart();

				exploredMap.repaint();

				ready = false;
				new fastestPathThread().execute();
				//
				firePropertyChange(exploreComplete, false, true);
				return null;
			}
		}


		/*
		 * 
		 * 
		 * 
		 */
		mainButtons = new JPanel();

		//print map descriptor

		JButton btn_interrupt = new JButton("TERMINATE");
		formatButton(btn_interrupt);

		btn_interrupt.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {

				noInterrupt = false;
			}
		});
		mainButtons.add(btn_interrupt);



		//TODO ready button
		JButton btn_ready = new JButton("READY");
		formatButton(btn_ready);

		btn_ready.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if(!realRun)
					ready = true;
			}
		});
		mainButtons.add(btn_ready);



		/* JButton btn_SetMid = new JButton("Set Mid Point");
        btn_SetMid.setFont(new Font("Arial", Font.BOLD, 18));
        btn_SetMid.setMargin(new Insets(10, 15, 10, 15));
        btn_SetMid.setFocusPainted(false); */

		JButton btn_Explore = new JButton("Explore");
		formatButton(btn_Explore);

		btn_Explore.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				new explorationThread().execute();
			}
		});
		mainButtons.add(btn_Explore);

		JButton btn_FastestPath = new JButton("Fastest Path");
		formatButton(btn_FastestPath);

		btn_FastestPath.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				// roboCop.setRobotRow(RobotConstant.DEFAULT_START_ROW); 
				// roboCop.setRobotCol(RobotConstant.DEFAULT_START_COL);
				exploredMap.repaint();
				new fastestPathThread().execute();
			}
		});
		mainButtons.add(btn_FastestPath);

		JButton btn_ExploreCoverage = new JButton("Explore Coverage");
		formatButton(btn_ExploreCoverage);
		btn_ExploreCoverage.addMouseListener(new MouseAdapter() {
			public void mousePressed (MouseEvent e) {
				String coverage;
				coverage=JOptionPane.showInputDialog("Input coverage value between 0 - 100");
				try {
					coverageValue = Integer.parseInt(coverage);
					if (coverageValue>0 && coverageValue<=100) {
						JOptionPane.showMessageDialog(null, "entered coverage value:" + coverage);
						new explorationCoverageThread().execute();
					}
					else {
						JOptionPane.showMessageDialog(null, "Invalid integer.\n Pls enter a value between  0- 100");
					}
				}
				catch (Exception e1) {
					JOptionPane.showMessageDialog(null, "Pls enter an integer value only.");
				}
			}
		});
		mainButtons.add(btn_ExploreCoverage);

		JButton btn_ExploreTime = new JButton("Explore Time");
		formatButton(btn_ExploreTime);
		btn_ExploreTime.addMouseListener(new MouseAdapter() {
			public void mousePressed (MouseEvent e) {
				String timeInput;
				timeInput = JOptionPane.showInputDialog("Input time in MM:SS format \n max : 99:59");
				if (timeInput.matches("[0-9]{1,2}:[0-9]{1,2}")) {
					String[] timeInputArr = timeInput.split(":");
					timeLimit = (Integer.parseInt(timeInputArr[0]) * 60) + Integer.parseInt(timeInputArr[1]);
					JOptionPane.showMessageDialog(null, "entered time limit: \n" + timeLimit + " seconds \n click ok to continue");
					new explorationTimeThread().execute();
				}
				else {
					JOptionPane.showMessageDialog(null, "Wrong format. Try again.");
				}
			}
		});
		mainButtons.add(btn_ExploreTime);

		JButton btn_EnterSpeed = new JButton ("Enter Speed");
		formatButton(btn_EnterSpeed);
		btn_EnterSpeed.addMouseListener(new MouseAdapter() {
			public void mousePressed (MouseEvent e) {
				String speedInput;
				speedInput = JOptionPane.showInputDialog("Input speed (steps per second)");
				try {
					int speedValue = Integer.parseInt(speedInput);
					roboCop.setRobotSpeed(speedValue);
				}
				catch (Exception e2) {
					JOptionPane.showMessageDialog(null, "Pls enter an integer value only.");
				}
			}
		});
		mainButtons.add(btn_EnterSpeed);
		
//		JButton btn_setMid = new JButton ("recv Mid Point");
//		formatButton(btn_setMid);
//		btn_setMid.addMouseListener(new MouseAdapter() {
//			public void mousePressed (MouseEvent e) {
//				new receiveMidPoint().execute();
//			}
//		});
//		mainButtons.add(btn_setMid);


	}



	private static void addMapButtons(){
		mapButtons = new JPanel();

		JButton btn_printMapDesc = new JButton("Print MapDesc");
		formatButton(btn_printMapDesc);
		btn_printMapDesc.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
//				System.out.println(MapDescriptor.generateMDFString2(realMap));
				System.out.println(MapDescriptor.generateMDFHex2(realMap));
				// print descriptor string on console
//				System.out.println("Print MapDesc");
//				System.out.println(utility.MapDescriptor.generateMapString(realMap));
			}
		});		

		mapButtons.add(btn_printMapDesc);

		JButton btn_setFog = new JButton("Set Fog");
		formatButton(btn_setFog);
		btn_setFog.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				// print descriptor string on console
				realMap.setAllUnExplored();
				realMap.repaint();
			}
		});		

		mapButtons.add(btn_setFog);

		JButton btn_clearFog = new JButton("Clear Fog");
		formatButton(btn_clearFog);
		btn_clearFog.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				// print descriptor string on console
				realMap.setAllExplored();
				realMap.repaint();
			}
		});		

		mapButtons.add(btn_clearFog);

		//load map from string
		JButton btn_loadMap = new JButton("Load Map");

		formatButton(btn_loadMap);

		btn_loadMap.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {

				// Load map from a map description string
				final JFileChooser fileDialog = new JFileChooser(System
						.getProperty("user.dir"));
				int returnVal = fileDialog.showOpenDialog(mainFrame);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fileDialog.getSelectedFile();

					try (BufferedReader br = new BufferedReader(new FileReader(
							file))) {
						realMap.reset();
						MapDescriptor.loadMapfromFile(realMap, br.readLine());
						realMap.setAllExplored();
					} catch (IOException e1) {
						e1.printStackTrace();
					} catch (Exception e2) {
						e2.printStackTrace();
					}
					
					System.out.println(MapDescriptor.generateMDFString2(realMap));
					// _loadedMapFilename = file.getName();
					JOptionPane.showMessageDialog(mainFrame, "Loaded map information from " + file.getName(),
							"Loaded Map Information", JOptionPane.PLAIN_MESSAGE);
				} else {
					System.out.println("Open command cancelled by user.");
				}
			}
		});
		mapButtons.add(btn_loadMap);

		//save map to string
		JButton btn_saveMap = new JButton("Save Map");
		formatButton(btn_saveMap);

		btn_saveMap.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {

				// Save current map layout to a map descriptor file
				final JFileChooser fileDialog = new JFileChooser(System
						.getProperty("user.dir"));

				int returnVal = fileDialog.showSaveDialog(mainFrame);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					try {
						String fileName = fileDialog.getSelectedFile() + "";

						// Allows overriding of existing text files
						if (!fileName.endsWith(".txt")) {
							fileName += ".txt";
						}

						// Change file writing part to a better implementation
						FileWriter fw = new FileWriter(fileName);
						//TODO debug dummy
						String outStr = MapDescriptor.generateMapString(realMap);
						System.out.println(outStr);
						fw.write(outStr);
						fw.flush();
						fw.close();

						JOptionPane.showMessageDialog(mainFrame,
								"Map information saved to " + fileName,
								"Saved Map Information",
								JOptionPane.PLAIN_MESSAGE);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				} else {
					System.out.println("Save command cancelled by user.");
				}
			}
		});
		mapButtons.add(btn_saveMap);





		//reset map
		JButton btn_ClearMap = new JButton ("Clear Map");
		formatButton(btn_ClearMap);

		btn_ClearMap.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				// Clear the current map
				System.out.println("Clearing Obstacles..");
				realMap.reset();
				exploredMap.reset();
			}
		});
		mapButtons.add(btn_ClearMap);


		//clear obstacles
		JButton btn_clearObs = new JButton("Clear Obstacles");
		formatButton(btn_clearObs);

		btn_clearObs.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				
				roboCop.reAlign();
				// Clear the current map
//				System.out.println("Clearing Obstacles..");
//				realMap.reset();
//				exploredMap.reset();
			}
		});
		mapButtons.add(btn_clearObs);
	}
	
	public static boolean returnRealRun(){
		return realRun;
	}

}
