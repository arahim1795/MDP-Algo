package map;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import java.io.*;
import map.MapConstant;
import map.MapUI;
import robot.RobotConstant;
import robot.RobotConstant.DIRECTION;
import robot.Robot;


public class Simulator {
	
	//mainFrame for the application
	public static JFrame mainFrame = null;
	
	//mainPanel for laying out different views
	private static JPanel mainPanel = null;
	private static JPanel buttonPanel = null;
	
	private static MapUI mapUI = null;
	private static JPanel mainButtons = null;
	
	//robot map??
	private static Map map = null;
	
	// Robot's starting position and direction
	private static int robotSize = RobotConstant.ROBOT_SIZE;
    private static int startPosRow = RobotConstant.DEFAULT_START_ROW;
    private static int startPosCol = RobotConstant.DEFAULT_START_COL;
    private static DIRECTION startDir = RobotConstant.DEFAULT_START_DIR;
    
    // The robot
    private static robot.Robot roboCop = null;

    // Map width & length used to render real & robot map
    private static int mapWidth;
    private static int mapLength;

    // File name of the loaded map
    private static String _loadedMapFilename = null;

	public static void main(String[] args) {
		
		roboCop = new Robot(startPosRow, startPosCol, startDir, false);
		
		// Calculate map width & length based on grid size
        mapWidth = MapConstant.MAP_COLS * GraphicConstant.TILE_SIZE;
        mapLength = MapConstant.MAP_ROWS * GraphicConstant.TILE_SIZE;
		
		//init main display mainFrame
		 mainFrame = new JFrame("Map Simulator");
	     mainFrame.setLocation(120,80);
	    // mainFrame.setSize(new Dimension(690, 700));
	     mainFrame.setLayout(new GridLayout(1,2,3,3));
	     mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	    
	     //init map mainPanel
	     mainPanel = new JPanel(new CardLayout());
	     //init button mainPanel
	     
	     mapUI = new MapUI();
	     mainPanel.add(mapUI, "Main");
	     
	     map = new Map();
	     mainPanel.add(map, "Map");
	     
	     // Show the real map (main menu) by default
	     CardLayout cl = ((CardLayout) mainPanel.getLayout());
	     cl.show(mainPanel, "MAIN");
	     
	     addButtons();
	     
	     //add cardLayouts to main frame
	     Container contentPane = mainFrame.getContentPane();
	     contentPane.add(mainPanel, BorderLayout.WEST);
	     contentPane.add(buttonPanel, BorderLayout.EAST); 
	     
	     mainFrame.setSize(new Dimension(920, 648));
	     mainFrame.setVisible(true);
	     mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	private static void addButtons() {
		mainButtons = new JPanel();
		
		//load map from string
		JButton btn_loadMap = new JButton("Load Map");
        btn_loadMap.setFont(new Font("Arial", Font.BOLD, 18));
        btn_loadMap.setMargin(new Insets(10, 15, 10, 15));
        btn_loadMap.setFocusPainted(false);

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
                        mapUI.loadFromMapString(br.readLine());
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
                        fw.write(mapUI.generateMapString());
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
                mapUI.clearMap();
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
                mapUI.clearMap();
            }
        });
        mainButtons.add(btn_clearObs);
        
        JButton btn_SetMid = new JButton("Set Mid Point");
        btn_SetMid.setFont(new Font("Arial", Font.BOLD, 18));
        btn_SetMid.setMargin(new Insets(10, 15, 10, 15));
        btn_SetMid.setFocusPainted(false);
        
		JButton btn_Explore = new JButton ("Explore");
		 btn_Explore.setFont(new Font("Arial", Font.BOLD, 18));
	     btn_Explore.setMargin(new Insets(10, 15, 10, 15));
	     btn_Explore.setFocusPainted(false);
	        
		JButton btn_FastestPath = new JButton ("Fastest Path");
		 btn_FastestPath.setFont(new Font("Arial", Font.BOLD, 18));
	     btn_FastestPath.setMargin(new Insets(10, 15, 10, 15));
	     btn_FastestPath.setFocusPainted(false);
		
		buttonPanel.add(mainButtons, "BUTTONS");
        CardLayout cl = ((CardLayout) buttonPanel.getLayout());
        cl.show(buttonPanel, "BUTTONS");
	}
	
}
