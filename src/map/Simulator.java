package map;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import java.io.*;
import java.util.Stack;


import map.MapConstant;




import robot.RobotConstant;
import robot.RobotConstant.DIRECTION;
import robot.Robot;

import search.FastestPath;
import search.Explore;
import utility.Comms;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class Simulator {
	
	//mainFrame for the application
	public static JFrame mainFrame = null;
	
	//mainPanel for laying out different views
	private static JPanel mapCards = null; //for viewing the different maps
	private static JPanel mainButtons = null;
	
	private static Map realMap = null;
	private static Map exploredMap = null;
	
	// Robot's starting position and direction
	private static int robotSize = RobotConstant.ROBOT_SIZE;
    private static int startPosRow = RobotConstant.DEFAULT_START_ROW;
    private static int startPosCol = RobotConstant.DEFAULT_START_COL;
    //private static DIRECTION startDir = RobotConstant.DEFAULT_START_DIR;
    private static DIRECTION startDir = DIRECTION.RIGHT;
    
    // The robot
    private static robot.Robot roboCop = null;
    private static final boolean realRun = false;
    private static boolean ready = false;

    // Map width & length used to render real & robot map
    private static int mapWidth;
    private static int mapLength;

    // File name of the loaded map
    private static String _loadedMapFilename = null;

	public static void main(String[] args) {
		
		
		roboCop = new Robot(RobotConstant.DEFAULT_START_ROW, RobotConstant.DEFAULT_START_COL, false);

		
		/*if (!realRun) {
			realMap = new MapUI (roboCop);
			//TODO debug
			//realMap.reset();
		}*/
		
		realMap = new Map(roboCop);
		exploredMap = new Map (roboCop);
		//TODO debug
		//exploredMap.reset();
		
		// Calculate map width & length based on grid size
        mapWidth = MapConstant.MAP_COLS * GraphicConstant.TILE_SIZE;
        mapLength = MapConstant.MAP_ROWS * GraphicConstant.TILE_SIZE;
        
		//init main display mainFrame
		 mainFrame = new JFrame("Map Simulator");
	     mainFrame.setLocation(120,80);
	     mainFrame.setSize(new Dimension(960, 657));
	     mainFrame.setLayout(new GridLayout(1,2,3,3));
	     
	     //JPanel for map views
	     mapCards = new JPanel(new CardLayout());
	     mapCards.add(realMap, "Main");
	     
	    addButtons();

	    mainFrame.add(mapCards);
	    mainFrame.add(mainButtons);
	    
	    initMainLayout();
	     
	     //add map panel and button panel to main frame
	     Container contentPane = mainFrame.getContentPane();
	     contentPane.add(mapCards, BorderLayout.CENTER); //add mapCards to main frame's content pane
	     contentPane.add(mainButtons, BorderLayout.EAST);
	     
	     mainFrame.setVisible(true);
	     mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
	}
	
	private static void initMainLayout() {
        if (!realRun) {
            mapCards.add(realMap, "REAL_MAP");
        }
        //mapCards.add(exploredMap, "EXPLORATION");

        CardLayout cl = ((CardLayout) mapCards.getLayout());
        if (!realRun) {
            cl.show(mapCards, "REAL_MAP");
        } else {
            cl.show(mapCards, "EXPLORATION");
        }
    }
	
	
    private static void formatButton(JButton btn) {
        btn.setFont(new Font("Arial", Font.BOLD, 18));
        btn.setMargin(new Insets(10, 15, 10, 15));
        btn.setFocusPainted(false);
    }
	
	private static void addButtons() {
		
		//init classes
		
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
		        
		        while (true) {
		            
		            System.out.println("waiting");
		            if(ready){		            	
	    	            break;
		            }
		        }
		        //
            	System.out.println("running FP");
            	//
		        FastestPath fastestPathAlgo;
	            fastestPathAlgo = new FastestPath(exploredMap, roboCop);
	            fastestPathAlgo.searchFastestPath(MapConstant.GOAL_GRID_ROW, MapConstant.GOAL_GRID_COL);	
	            //
		        firePropertyChange(prReallyDone, false, true);
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
		JButton btn_printMapDesc = new JButton("Print MapDesc");
		formatButton(btn_printMapDesc);
		btn_printMapDesc.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                // print descriptor string on console
                System.out.println("Print MapDesc");
                System.out.println(utility.MapDescriptor.generateMapString(realMap));
            }
        });		
		
		mainButtons.add(btn_printMapDesc);
		//TODO ready button
				JButton btn_ready = new JButton("READY");
		        formatButton(btn_ready);

		        btn_ready.addMouseListener(new MouseAdapter() {
		            public void mousePressed(MouseEvent e) {

		                ready = true;
		            }
		        });
		        mainButtons.add(btn_ready);
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
                    	realMap.clearMap();
                    	utility.MapDescriptor.loadMapfromFile(realMap, br.readLine());
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }

                    _loadedMapFilename = file.getName();
                    JOptionPane.showMessageDialog(mainFrame, "Loaded map information from " + file.getName(),
                    		"Loaded Map Information", JOptionPane.PLAIN_MESSAGE);
                } else {
                    System.out.println("Open command cancelled by user.");
                }
            }
        });
        mainButtons.add(btn_loadMap);
		
		//save map to string
        JButton btn_saveMap = new JButton("Save Map");
        btn_saveMap.setFont(new Font("Arial", Font.BOLD, 18));
        btn_saveMap.setMargin(new Insets(10, 15, 10, 15));
        btn_saveMap.setFocusPainted(false);

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
                        String outStr = utility.MapDescriptor.generateMapString(realMap);
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
        mainButtons.add(btn_saveMap);
		
		//reset map
		JButton btn_ClearMap = new JButton ("Clear Map");
		btn_ClearMap.setFont(new Font("Arial", Font.BOLD, 18));
        btn_ClearMap.setMargin(new Insets(10, 15, 10, 15));
        btn_ClearMap.setFocusPainted(false);
        
		btn_ClearMap.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                // Clear the current map
                System.out.println("Clearing Obstacles..");
                realMap.reset();
                exploredMap.clearMap();
            }
        });
		mainButtons.add(btn_ClearMap);
		
		//clear obstacles
		JButton btn_clearObs = new JButton("Clear Obstacles");
        btn_clearObs.setFont(new Font("Arial", Font.BOLD, 18));
        btn_clearObs.setMargin(new Insets(10, 15, 10, 15));
        btn_clearObs.setFocusPainted(false);

        btn_clearObs.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                // Clear the current map
                System.out.println("Clearing Obstacles..");
                realMap.clearMap();
                exploredMap.clearMap();
            }
        });
        mainButtons.add(btn_clearObs);
        
        JButton btn_SetMid = new JButton("Set Mid Point");
        btn_SetMid.setFont(new Font("Arial", Font.BOLD, 18));
        btn_SetMid.setMargin(new Insets(10, 15, 10, 15));
        btn_SetMid.setFocusPainted(false);
        
        JButton btn_Explore = new JButton("Explore");
        btn_Explore.setFont(new Font("Arial", Font.BOLD, 18));
        btn_Explore.setMargin(new Insets(10, 15, 10, 15));
        btn_Explore.setFocusPainted(false);
        
        btn_Explore.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
//                roboCop.setRobotRow(RobotConstant.DEFAULT_START_ROW); 
//            	  roboCop.setRobotCol(RobotConstant.DEFAULT_START_COL);
                exploredMap.repaint();
                Explore exploration;
                exploration = new Explore (roboCop, realMap, 3);
                exploration.explore();
            }
        });
        mainButtons.add(btn_Explore);

        JButton btn_FastestPath = new JButton("Fastest Path");
        btn_FastestPath.setFont(new Font("Arial", Font.BOLD, 18));
        btn_FastestPath.setMargin(new Insets(10, 15, 10, 15));
        btn_FastestPath.setFocusPainted(false);
        
        btn_FastestPath.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
//                roboCop.setRobotRow(RobotConstant.DEFAULT_START_ROW); 
  //              roboCop.setRobotCol(RobotConstant.DEFAULT_START_COL);
                realMap.repaint();
                new fastestPathThread().execute();
               
                
            }
        });
        mainButtons.add(btn_FastestPath);

	}
	
	
}
