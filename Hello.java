package personCounter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.opencv.core.Core;

/**
 * About this program:
 * 
 * This program was finished on the 24.06.2015 and was written
 * for the subject of study "Bild- und Videoverarbeitung" at the
 * University of Applied Sciences Flensburg.
 * 
 * What this program does:
 * 
 * The program uses a video processing technique to find moving
 * objects in videos. The provided GUI is built to help finding
 * the correct thresholds to successfully differentiate between
 * foreground and background of the video.
 * 
 * The GUI:
 * 
 * The program uses a BorderLayout. On the top of the screen you
 * can choose the video you want to run the algorithm on.
 * On the left you see different sliders for the thresholds used
 * by the algorithm. Change these values to fit the video.
 * In the center the videos will be shown after clicking play.
 * On the right you see a log with debug messages.
 * On the bottom you can see the frames of the video.
 * 
 * Program structure:
 * 
 * 		- PersonCounter: The class that holds information about the
 * 		algorithm and performs the algorithm.
 * 		- Person: A class representing a person inside the video.
 * 		- Panel: The GUI-Element that paints the video.
 * 		- Frame: A class representing a single frame of the video.
 * 		- LabeldSlider: A class for a slider with a label. (GUI)
 * 		- VideoRunnable: A class implementing Runnable to show the
 * 		video in a different thread. After starting, this thread
 * 		will manage to show the video frame by frame.
 * 		- Log: A GUI-Element to show debug messages.
 * 
 * Dependencies:
 * 
 * This program needs the OpenCV 3.0 .jar file to compile. How
 * to add this to your IDE can be found in the OpenCV documentation.
 * 
 * Created by:
 * Steffen Peleikis
 * Ole Quedens
 * Jan Behrens
 * Marek Michels
 * 
 * 
 * The class that holds the main()-method.
 * 
 * This class holds most of the information about the different
 * GUI elements used in the program.
 * This class defines all the eventHandlers for the different GUI
 * elements.
 * 
 * @author jan
 *
 */
public class Hello
{
	/**
	 * The thread the video runs in.
	 */
	private static Thread videoThread;
	/**
	 * The class that implements Runnable and will be started by the videoThread.
	 */
	private static VideoRunnable videoRunnable;
	/**
	 * The default threshold value. This value will be changed by moving the slider
	 * labeld with "threshold". This value is used by the image processing algorithm.
	 */
	private static double currThreshold = 40;
	/**
	 * The default value for minArea. This value will be changed by moving the slider
	 * labeld with "Minimum Area". This value is used by the image processing algorithm.
	 */
	private static double currMinArea = 700;
	/**
	 * The default value for maxDistance. This value will be changed by moving the slider
	 * labeld with "Maximum Distance". This value is used by the image processing algorithm.
	 */
	private static double currMaxDistance = 90;
	/**
	 * The default value for filterSite. This value will be changed by moving the slider
	 * labeld with "Filter Size". This value is used by the image processing algorithm.
	 */
	private static int currFilterSize = 1;
	/**
	 * The default value for adaptionFactor. This value will be changed by moving the slider
	 * labeld with "Adaption Factor". This value is used by the image processing algorithm.
	 */
	private static int currAdaptionFactor = 0;
	
	
	private static int currMinBBsize = 2200;

	/**
	 * Default path to video you want to run the algorithm with. The video can be changed
	 * at runtime by using the fileChooser. This path will set to chosen file.
	 */
	//private static final String videoPathDefault = "/home/jan/opencv_workspace/personCounter/leute.mp4";
	private static final String videoPathDefault = "/home/stfn/dev/eclipse/labor.mp4";

	/**
	 * The JFrame used to show the program in.
	 */
	private static JFrame window;
	/**
	 * The top-level container of the program. This container uses the BorderLayout. All
	 * other containers will be added to this container. The other containers are called
	 * after their position, i.e. containerTop for the top area of the BorderLayout.
	 */
	private static JPanel container;
	/**
	 * The container for all elements at the BorderLayout.NORTH position of the top-level
	 * container.
	 */
	private static JPanel containerTop;
	/**
	 * The container for all elements at the BorderLayout.LINE_START position of the
	 * top-level container.
	 */
	private static JPanel containerLeft;
	/**
	 * The container for all elements at the BorderLayout.SOUTH position of the
	 * top-level container.
	 */
	private static JPanel containerBottom;
	/**
	 * The container for all elements at the BorderLayout.LINE_END position of the
	 * top-level container.
	 */
	private static JPanel containerRight;
	/**
	 * The container for all elements at the BorderLayout.CENTER position of the
	 * top-level container.
	 */
	private static JScrollPane containerCenter;
	
	/**
	 * The panel that will draw the videos.
	 */
	private static Panel panel;
	
	/**
	 * Label for the textBox to choose the video file.
	 */
	private static JLabel videoPathLabel;
	/**
	 * TextField that shows the currently selected video.
	 */
	private static JTextField videoPathTextField;
	/**
	 * JFileChooser to select a different video at runtime.
	 */
	private static JFileChooser videoPathFileChooser;
	/**
	 * The button that starts the dialog of videoPathFileChooser to choose
	 * a video.
	 */
	private static JButton videoPathButton;
	
	/**
	 * A slider to change the value of the class member currThreshold.
	 */
	private static LabeldSlider thresholdGui;
	/**
	 * A slider to change the value of the class member currMinArea.
	 */
	private static LabeldSlider minAreaGui;
	/**
	 * A slider to change the value of the class member currMaxDistance.
	 */
	private static LabeldSlider maxDistanceGui;
	/**
	 * A slider to change the value of the class member currFilterSize.
	 */
	private static LabeldSlider filterSizeGui;
	/**
	 * A slider to change the value of the class member currAdaptionFactor.
	 */
	private static LabeldSlider adaptionFactorGui;
	
	private static LabeldSlider minBBsizeGui;

	/**
	 * A JTextField that shows the current framerate of the video.
	 */
	private static JTextField frameRateTextField;
	/**
	 * A button that can be used to play and pause the video.
	 */
	private static JButton playButton;
	/**
	 * A button to restart the video.
	 */
	private static JButton restartButton;
	
	/**
	 * A label for the class member personCounterTextField.
	 */
	private static JLabel personCounterLabel;
	/**
	 * A JTextField that shows the current count of people in the
	 * current frame of the video.
	 */
	private static JTextField personCounterTextField;
	/**
	 * A label for the class member frameRateTextField.
	 */
	private static JLabel frameRateLabel;
	
	/**
	 * The main()-method of this program. This method creates the
	 * GUI of the program and holds all the eventHandlers. The interesting
	 * part and the algorithm will be triggered by the events of the
	 * different GUI-Elements.
	 * 
	 * @param args Unused
	 */
	public static void main( String[] args )
	{
		// Load the native library for OpenCV.
		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
		
		
		//Change javas standard look and feel to the OS specific look and feel
		/*try { 
		    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		    e.printStackTrace();
		}*/
		
		//initialize log so every component can write debug messages into log
		Log.initialize();
		
		videoRunnable = null;
		
		/*
		 * Create the window and set some basic window defaults.
		 */
		window = new JFrame("window");
		window.setSize(800, 500);
		window.setTitle("OpenCV 3.0 - Background Subtraction");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
	   
		
		//create a top-level container for the window
		container = new JPanel();
		container.setLayout(new BorderLayout());
		
		//create the wrapper for elements in the center.
		containerCenter = new JScrollPane();
		containerCenter.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		// the panel in the center of the screen (shows video)
		panel = new Panel(); 
		panel.setBorder(BorderFactory.createLineBorder(Color.red));
		panel.setSize(700, 2000);
		//panel.setPreferredSize(new Dimension(700, 2000));
		containerCenter.setPreferredSize(new Dimension(700, 2000));
		
		
		//just some container for the different parts of the borderlayout
		containerTop = new JPanel();
		containerLeft = new JPanel();
		containerRight = new JPanel();
		containerBottom = new JPanel();
		containerLeft.setLayout(new BoxLayout(containerLeft, BoxLayout.PAGE_AXIS));
		containerBottom.setLayout(new BoxLayout(containerBottom, BoxLayout.LINE_AXIS));
		
		//the file chooser to get a video path from the file system
		videoPathLabel = new JLabel("Set Path to video");
		videoPathTextField = new JTextField(videoPathDefault);
		videoPathButton = new JButton("Choose File");
		videoPathFileChooser = new JFileChooser();
		videoPathButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				videoPathFileChooser.setDialogTitle("Choose a video");
				int returnVal = videoPathFileChooser.showOpenDialog(containerTop);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File file = videoPathFileChooser.getSelectedFile();
		            videoPathTextField.setText(file.getPath());
		        }
			}
		});
		
		//the information at the bottom of the window
		personCounterLabel = new JLabel("Persons in Picture: ");
		personCounterTextField = new JTextField(4);
		personCounterTextField.setEditable(false);
		frameRateLabel = new JLabel("Frames/sec: ");
		frameRateTextField = new JTextField(4);
		frameRateTextField.setEditable(false);
		
		
		/********************************************************************
		 * Add event listener to the different components
		 *******************************************************************/
		
		thresholdGui = new LabeldSlider("Set Threshold", 0, 150, (int)currThreshold, 5, 30);
		thresholdGui.getSlider().addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) {
				currThreshold = thresholdGui.getSlider().getValue();
				videoRunnable.setThreshold(currThreshold);
				Log.add("Setting threshold to: "+currThreshold);
			}
		});
		minAreaGui = new LabeldSlider("Set Minimum Area", 0, 1500, (int)currMinArea, 50, 300);
		minAreaGui.getSlider().addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) {
				currMinArea = minAreaGui.getSlider().getValue();
				videoRunnable.setMinArea(currMinArea);
				Log.add("Setting minArea to: "+currMinArea);
			}
		});
		maxDistanceGui = new LabeldSlider("Set Maximum Distance", 0, 200, (int)currMaxDistance, 10, 25);
		maxDistanceGui.getSlider().addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) {
				currMaxDistance = maxDistanceGui.getSlider().getValue();
				videoRunnable.setMaxDistance(currMaxDistance);
				Log.add("Setting maxDistance to: "+currMaxDistance);
			}
		});
		filterSizeGui = new LabeldSlider("Set Filter Size", 0, 20, currFilterSize, 1, 5);
		filterSizeGui.getSlider().addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) {
				if(filterSizeGui.getSlider().getValue() % 2 == 1)
				{
					currFilterSize = filterSizeGui.getSlider().getValue();
					videoRunnable.setFilterSize(currFilterSize);
					Log.add("Setting filterSize to: "+currFilterSize);	
				}
				
			}
		});
		adaptionFactorGui = new LabeldSlider("Set Adaption Factor", 0, 50, currAdaptionFactor, 1, 5);
		adaptionFactorGui.getSlider().addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) {
				currAdaptionFactor = adaptionFactorGui.getSlider().getValue();
				videoRunnable.setAdaptionFactor(currAdaptionFactor);
				Log.add("Setting adaptionFactor to: "+currAdaptionFactor);
			}
		});
		
		minBBsizeGui = new LabeldSlider("Set min BB size", 0, 5000, currMinBBsize, 50, 1000);
		minBBsizeGui.getSlider().addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) {
				currMinBBsize = minBBsizeGui.getSlider().getValue();
				videoRunnable.setMinBBsize(currMinBBsize);
				Log.add("Setting currMinBBsize to: "+currMinBBsize);
			}
		});
		playButton = new JButton("Play");
		playButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if(videoRunnable == null) {
					Log.addSeperator();
					Log.add("Starting Video \""+videoPathTextField.getText()+"\"");
					videoRunnable = new VideoRunnable(panel, videoPathTextField.getText(), frameRateTextField, currThreshold, currMinArea, currMaxDistance, currFilterSize, currAdaptionFactor, currMinBBsize);
					videoThread = new Thread(videoRunnable);
					videoThread.start();
					playButton.setText("Pause");
				} else {
					if(videoRunnable.videoIsRunning()) {
						Log.add("Pausing video...");
						videoRunnable.pauseVideo();
						playButton.setText("Play");
					} else {
						Log.add("Resuming video...");
						videoRunnable.playVideo();
						playButton.setText("Pause");
					}
				}
			}
		});
		restartButton = new JButton("Restart");
		restartButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					Log.addSeperator();
					Log.add("Starting Video \""+videoPathTextField.getText()+"\"");
					videoRunnable.stopRunning();
					videoThread.join();
					videoRunnable = new VideoRunnable(panel, videoPathTextField.getText(), frameRateTextField, currThreshold, currMinArea, currMaxDistance, currFilterSize, currAdaptionFactor, currMinBBsize);
					videoThread = new Thread(videoRunnable);
					videoThread.start();
					playButton.setText("Pause");
				} catch (InterruptedException e1) {
					Log.add("Failed to close video. Try again!");
				}
			}
		});
		
		/********************************************************************
		 * Add all the components to the top level containers and then
		 * add them to the window itself.
		 *******************************************************************/
		
		containerTop.add(videoPathLabel);
		containerTop.add(videoPathTextField);
		containerTop.add(videoPathButton);
		
		containerBottom.add(personCounterLabel);
		containerBottom.add(personCounterTextField);
		containerBottom.add(frameRateLabel);
		containerBottom.add(frameRateTextField);
		
		
		containerLeft.add(thresholdGui.getLabel());
		containerLeft.add(thresholdGui.getSlider());
		containerLeft.add(minAreaGui.getLabel());
		containerLeft.add(minAreaGui.getSlider());
		containerLeft.add(maxDistanceGui.getLabel());
		containerLeft.add(maxDistanceGui.getSlider());
		containerLeft.add(filterSizeGui.getLabel());
		containerLeft.add(filterSizeGui.getSlider());
		containerLeft.add(adaptionFactorGui.getLabel());
		containerLeft.add(adaptionFactorGui.getSlider());
		containerLeft.add(minBBsizeGui.getLabel());
		containerLeft.add(minBBsizeGui.getSlider());
		containerLeft.add(playButton);
		containerLeft.add(restartButton);
		
		containerRight.add(Log.getComponent());
		
		containerCenter.add(panel);
		
		container.add(containerTop, BorderLayout.NORTH);
		container.add(containerLeft, BorderLayout.LINE_START);
		container.add(containerRight, BorderLayout.LINE_END);
		container.add(containerCenter, BorderLayout.CENTER);
		container.add(containerBottom, BorderLayout.SOUTH);
		
		window.add(container);
		
		//maximize window and repaint it
	    window.setExtendedState(JFrame.MAXIMIZED_BOTH); 
	    window.setVisible(true);
	    window.pack();
	    window.validate();
	    window.repaint();
	    //PersonCounter(40, 500, 90, 3, .001);
	}

   
}