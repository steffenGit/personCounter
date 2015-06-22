//package personCounter;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.opencv.core.Core;

public class Hello
{
	private static Thread videoThread;
	private static VideoRunnable videoRunnable;
	private static double currThreshold = 15;
	private static double currMinArea = 100;
	private static double currMaxDistance = 50;
	private static int currFilterSize = 3;
	private static int currAdaptionFactor = 4;
	
	private static final String videoPathDefault = "/home/jan/opencv_workspace/personCounter/leute.mp4";
	
	private static JFrame window;
	
	private static JPanel container;
	private static JPanel containerTop;
	private static JPanel containerLeft;
	private static JPanel containerBottom;
	private static JPanel containerRight;
	private static Panel panel;
	
	
	private static JLabel videoPathLabel;
	private static JTextField videoPathTextField;
	private static JFileChooser videoPathFileChooser;
	private static JButton videoPathButton;
	
	private static LabeldSlider thresholdGui;
	private static LabeldSlider minAreaGui;
	private static LabeldSlider maxDistanceGui;
	private static LabeldSlider filterSizeGui;
	private static LabeldSlider adaptionFactorGui;
	private static JTextField frameRateTextField;
	private static JButton playButton;
	private static JButton restartButton;
	
	
	private static JLabel personCounterLabel;
	private static JTextField personCounterTextField;;
	private static JLabel frameRateLabel;
	
	public static void main( String[] args )
	{
		
		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
		
		try { 
		    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		    e.printStackTrace();
		}
		
		videoRunnable = null;
		
		window = new JFrame("window");
		window.setSize(800, 500);
		window.setTitle("OpenCV 3.0 - Background Subtraction");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
		Panel panel = new Panel();
		window.add(panel);
	   
	   
		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
		Mat m = new Mat();
		  
		VideoCapture vid = new VideoCapture("/home/stfn/dev/eclipse/labor.mp4");	  
		if (vid.isOpened())
			System.out.println("file loaded");		
		PersonCounter pc = new PersonCounter(40, 1200, 90, 1, .00001);

		
//		VideoCapture vid = new VideoCapture(0);
//		Thread.sleep(1000);
//		if (vid.isOpened())
//			System.out.println("file loaded");		
//		PersonCounter pc = new PersonCounter(40, 1200, 90, 1, .00001);		
		
//		VideoCapture vid = new VideoCapture("/home/stfn/dev/eclipse/leute.mp4");
//		if (vid.isOpened())
//			System.out.println("file loaded");		
//		PersonCounter pc = new PersonCounter(40, 500, 90, 3, .001);
		
		container = new JPanel();
		container.setLayout(new BorderLayout());
		panel = new Panel();
		
		Log.initialize();
		
		containerTop = new JPanel();
		containerLeft = new JPanel();
		containerRight = new JPanel();
		containerBottom = new JPanel();
		containerLeft.setLayout(new BoxLayout(containerLeft, BoxLayout.PAGE_AXIS));
		containerBottom.setLayout(new BoxLayout(containerBottom, BoxLayout.LINE_AXIS));
		
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
		
		personCounterLabel = new JLabel("Persons in Picture: ");
		personCounterTextField = new JTextField(4);
		personCounterTextField.setEditable(false);
		frameRateLabel = new JLabel("Frames/sec: ");
		frameRateTextField = new JTextField(4);
		frameRateTextField.setEditable(false);
		
		
		thresholdGui = new LabeldSlider("Set Threshold", 0, 50, 15, 1, 5);
		thresholdGui.getSlider().addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) {
				currThreshold = thresholdGui.getSlider().getValue();
				Log.add("Setting threshold to: "+currThreshold);
			}
		});
		minAreaGui = new LabeldSlider("Set Minimum Area", 0, 500, 100, 10, 100);
		minAreaGui.getSlider().addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) {
				currMinArea = minAreaGui.getSlider().getValue();
				Log.add("Setting minArea to: "+currMinArea);
			}
		});
		maxDistanceGui = new LabeldSlider("Set Maximum Distance", 0, 200, 50, 10, 25);
		maxDistanceGui.getSlider().addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) {
				currMaxDistance = maxDistanceGui.getSlider().getValue();
				Log.add("Setting maxDistance to: "+currMaxDistance);
			}
		});
		filterSizeGui = new LabeldSlider("Set Filter Size", 0, 50, 3, 1, 5);
		filterSizeGui.getSlider().addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) {
				currFilterSize = filterSizeGui.getSlider().getValue();
				Log.add("Setting filterSize to: "+currFilterSize);
			}
		});
		adaptionFactorGui = new LabeldSlider("Set Adaption Factor", 0, 50, 4, 1, 5);
		adaptionFactorGui.getSlider().addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) {
				currAdaptionFactor = adaptionFactorGui.getSlider().getValue();
				Log.add("Setting adaptionFactor to: "+currAdaptionFactor);
			}
		});
		playButton = new JButton("Play");
		playButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if(videoRunnable == null) {
					Log.addSeperator();
					Log.add("Starting Video \""+videoPathTextField.getText()+"\"");
					videoRunnable = new VideoRunnable(panel, videoPathTextField.getText(), frameRateTextField, currThreshold, currMinArea, currMaxDistance, currFilterSize, currAdaptionFactor);
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
					videoRunnable = new VideoRunnable(panel, videoPathTextField.getText(), frameRateTextField, currThreshold, currMinArea, currMaxDistance, currFilterSize, currAdaptionFactor);
					videoThread = new Thread(videoRunnable);
					videoThread.start();
					playButton.setText("Pause");
				} catch (InterruptedException e1) {
					Log.add("Failed to close video. Try again!");
				}
			}
		});
		
		
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
		containerLeft.add(playButton);
		containerLeft.add(restartButton);
		
		containerRight.add(Log.getComponent());
		
		container.add(containerTop, BorderLayout.NORTH);
		container.add(containerLeft, BorderLayout.LINE_START);
		container.add(containerRight, BorderLayout.LINE_END);
		container.add(panel, BorderLayout.CENTER);
		container.add(containerBottom, BorderLayout.SOUTH);
		
		window.add(container);
	    
	    window.setExtendedState(JFrame.MAXIMIZED_BOTH); 
	    window.setVisible(true);
	    window.repaint();
	    //PersonCounter(40, 500, 90, 3, .001);
	}

   
}