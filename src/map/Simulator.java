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
	
	public static JFrame frame = null;
	private static JPanel panel = null;
	private static JPanel buttonPanel = null;
	private static Robot bot;
	private static MapUI mapUI = null;
	private static JPanel mapViews = null;

	public static void main(String[] args) {
		
		bot = new Robot(1,1,DIRECTION.UP, false);
		System.out.println("clear");
		/*
		//main display frame
		 frame = new JFrame();
	     frame.setTitle("Map Simulator");
	     frame.setSize(new Dimension(690, 700));
	     frame.setVisible(true);
	     frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	     
	     panel = new JPanel(new CardLayout());
	     buttonPanel = new JPanel();
	     
	     // Create the CardLayout for storing the different maps
	     mapViews = new JPanel(new CardLayout());
	     
	     Container contentPane = frame.getContentPane();
	     contentPane.add(mapViews, BorderLayout.CENTER);
	     contentPane.add(buttonPanel, BorderLayout.PAGE_END);
	        
	     //add panels to frame
	     frame.add(panel);
	     frame.add(buttonPanel);
	     
	     mapUI = new MapUI();
	     //panel.add(mapUI);
		*/
	}
	
	private static void addButtons() {
		JButton btn_LoadMap = new JButton ("Load Map");
		btn_LoadMap.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {

                // Load map from a map description string
                final JFileChooser fileDialog = new JFileChooser(System.getProperty("user.dir"));
                int returnVal = fileDialog.showOpenDialog(frame);
                
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fileDialog.getSelectedFile();
                    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                        mapUI.loadFromMapString(br.readLine());
                    } 
                    catch (IOException e1) {
                        e1.printStackTrace();
                    } 
                    catch (Exception e2) {
                        e2.printStackTrace();
                    }
                    
                    JOptionPane.showInternalMessageDialog(frame, "Loading map from file:" + file.getName(), 
                    		"",JOptionPane.INFORMATION_MESSAGE);
                }
                else {
                    System.out.println("Open command cancelled by user.");
                }
            }
        });
		
		JButton btn_SaveMap = new JButton ("Save Map");
		btn_SaveMap.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {

                // Save current map layout to a map descriptor file
                final JFileChooser fileDialog = new JFileChooser(System.getProperty("user.dir"));

                int returnVal = fileDialog.showSaveDialog(frame);
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

                        JOptionPane.showMessageDialog(frame, "Saving map to " + fileName, "", JOptionPane.PLAIN_MESSAGE);
                    } 
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } 
                else {
                    System.out.println("cancelled");
                }
            }
        });
		
		JButton btn_ClearMap = new JButton ("Clear Map"); //reset map
		btn_ClearMap.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                // Clear the current map
                System.out.println("Clearing Obstacles..");
                mapUI.clearMap();
            }
        });
		JButton btn_Explore = new JButton ("Explore");
		JButton btn_FastestPath = new JButton ("Fastest Path");
	}
	
}
