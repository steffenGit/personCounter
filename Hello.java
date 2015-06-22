package personCounter;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

public class Hello
{
	private static Thread videoThread;
	private static VideoRunnable videoRunnable;
	private static double currThreshold = 15;
	private static double currMinArea = 100;
	private static double currMaxDistance = 50;
	private static int currFilterSize = 3;
	private static int currAdaptionFactor = 4;
	
	private static JPanel container;
	private static JPanel containerLeft;
	private static JPanel containerBottom;
	private static Panel panel;
	
	private static LabeldSlider thresholdGui;
	private static LabeldSlider minAreaGui;
	private static LabeldSlider maxDistanceGui;
	private static LabeldSlider filterSizeGui;
	private static LabeldSlider adaptionFactorGui;
	private static JTextField frameRateTextField;
	private static JButton playButton;
	private static JButton restartButton;
	
	public static void main( String[] args )
	{
		
		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
		
		videoRunnable = null;
		
		JFrame window = new JFrame("window");
		window.setSize(800, 500);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
		
		container = new JPanel();
		container.setLayout(new BorderLayout());
		panel = new Panel();
		
		
		containerLeft = new JPanel();
		containerBottom = new JPanel();
		containerLeft.setLayout(new BoxLayout(containerLeft, BoxLayout.PAGE_AXIS));
		containerBottom.setLayout(new BoxLayout(containerBottom, BoxLayout.LINE_AXIS));
		JLabel personCounterLabel = new JLabel("Persons in Picture: ");
		JTextField personCounterTextField = new JTextField(4);
		personCounterTextField.setEditable(false);
		JLabel frameRateLabel = new JLabel("Frames/sec: ");
		frameRateTextField = new JTextField(4);
		frameRateTextField.setEditable(false);
		
		thresholdGui = new LabeldSlider("Set Threshold", 0, 50, 15, 1, 5);
		thresholdGui.getSlider().addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) {
				currThreshold = thresholdGui.getSlider().getValue();
			}
		});
		minAreaGui = new LabeldSlider("Set Minimum Area", 0, 500, 100, 10, 100);
		minAreaGui.getSlider().addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) {
				currMinArea = minAreaGui.getSlider().getValue();
			}
		});
		maxDistanceGui = new LabeldSlider("Set Maximum Distance", 0, 200, 50, 10, 25);
		maxDistanceGui.getSlider().addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) {
				currMaxDistance = maxDistanceGui.getSlider().getValue();
			}
		});
		filterSizeGui = new LabeldSlider("Set Filter Size", 0, 50, 3, 1, 5);
		filterSizeGui.getSlider().addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) {
				currFilterSize = filterSizeGui.getSlider().getValue();
			}
		});
		adaptionFactorGui = new LabeldSlider("Set Adaption Factor", 0, 50, 4, 1, 5);
		adaptionFactorGui.getSlider().addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) {
				currAdaptionFactor = adaptionFactorGui.getSlider().getValue();
				System.out.println("Setting adaptionFactor to: "+currAdaptionFactor);
			}
		});
		playButton = new JButton("Play");
		playButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if(videoRunnable == null) {
					System.out.println("Starting Video for the first time...");
					videoRunnable = new VideoRunnable(panel, frameRateTextField, currThreshold, currMinArea, currMaxDistance, currFilterSize, currAdaptionFactor);
					videoThread = new Thread(videoRunnable);
					videoThread.start();
					playButton.setText("Pause");
				} else {
					if(videoRunnable.videoIsRunning()) {
						System.out.println("Pausing video...");
						videoRunnable.pauseVideo();
						playButton.setText("Play");
					} else {
						System.out.println("Resuming video...");
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
					System.out.println("Restarting video...");
					videoRunnable.stopRunning();
					videoThread.join();
					videoRunnable = new VideoRunnable(panel, frameRateTextField, currThreshold, currMinArea, currMaxDistance, currFilterSize, currAdaptionFactor);
					videoThread = new Thread(videoRunnable);
					videoThread.start();
					playButton.setText("Pause");
				} catch (InterruptedException e1) {
					System.out.println("Failed to close video. Try again!");
				}
			}
		});
		
		
		containerBottom.add(personCounterLabel);
		containerBottom.add(personCounterTextField);
		containerBottom.add(frameRateLabel);
		containerBottom.add(frameRateTextField);
		
		
		containerLeft.add(thresholdGui.getLabel());
		containerLeft.add(thresholdGui.getSlider());
		containerLeft.add(new JSeparator(SwingConstants.HORIZONTAL));
		containerLeft.add(minAreaGui.getLabel());
		containerLeft.add(minAreaGui.getSlider());
		containerLeft.add(new JSeparator(SwingConstants.HORIZONTAL));
		containerLeft.add(maxDistanceGui.getLabel());
		containerLeft.add(maxDistanceGui.getSlider());
		containerLeft.add(new JSeparator(SwingConstants.HORIZONTAL));
		containerLeft.add(filterSizeGui.getLabel());
		containerLeft.add(filterSizeGui.getSlider());
		containerLeft.add(new JSeparator(SwingConstants.HORIZONTAL));
		containerLeft.add(adaptionFactorGui.getLabel());
		containerLeft.add(adaptionFactorGui.getSlider());
		containerLeft.add(new JSeparator(SwingConstants.HORIZONTAL));
		containerLeft.add(playButton);
		containerLeft.add(restartButton);
		containerLeft.add(new JSeparator(SwingConstants.HORIZONTAL));
		
		container.add(containerLeft, BorderLayout.LINE_START);
		container.add(panel, BorderLayout.CENTER);
		container.add(containerBottom, BorderLayout.SOUTH);
		
		window.add(container);
	    
	    window.setExtendedState(JFrame.MAXIMIZED_BOTH); 
	    window.setVisible(true);
	    window.repaint();
	    //PersonCounter(40, 500, 90, 3, .001);
	}

   
}