//package personCounter;

import java.awt.Dimension;

import javax.swing.JTextField;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;



/**
 * This class is used to show one or more videos in a window.
 * 
 * GUI:
 * There are two GUI-Elements needed for this class:
 * 		- An object of type Panel
 *      - An object of type JTextField
 * The video will be drawn onto the Panel and the current fps
 * will be shown in the JTextField.
 * 
 * This class implements Runnable so the videos can run in a 
 * different thread than the main-program. In case of exceptions
 * or errors only this thread is likely to die or freeze but the
 * main thread will run as smooth as always.
 * 
 * By starting this thread with thread.start() the video will run
 * in an endless loop. To pause the video set the boolean flag
 * "stopped" to true. To continue the video set this to false.
 * 
 * The boolean "running" is used to kill the thread. If you want
 * to kill the thread, set this value to false and then join the
 * thread or whatever else you can do to kill a thread.
 * 
 * @author jan
 *
 */
public class VideoRunnable implements Runnable{
	
	/**
	 * Wheter this thread is still running or not.
	 */
	private boolean running = false;
	/**
	 * Wheter the video is paused or not.
	 */
	private boolean stopped = false;
	/**
	 * Matrix used to step through the video.
	 */
	private Mat m;
	/**
	 * Object to read and save the video in.
	 */
	private VideoCapture vid;
	/**
	 * The main image processing class object.
	 */
	private PersonCounter pc;
	/**
	 * GUI-Element that shows the video(s).
	 */
	private Panel panel;
	/**
	 * GUI-Element to show the current frameRate.
	 */
	private JTextField frameRateTextField;
	
	/**
	 * The constructor reads the videofile and creates an object
	 * of PersonCounter to initialize the image processing process.
	 * 
	 * @param panel The panel that shows the video.
	 * @param videoPath Path to the video file you want to use.
	 * @param frameRateTextField The gui element you want to show the frame rate in.
	 * @param threshold The threshold for the algorithm. - Not used in this class
	 * @param minArea The minArea for the algorithm. - Not used in this class
	 * @param maxDistance The maxDistance for the algorithm. - Not used in this class
	 * @param filterSize The filterSize for the algorithm. - Not used in this class
	 * @param adaptionFactor The adaptionFactor for the algorithm. - Not used in this class
	 */
	public VideoRunnable(Panel panel, String videoPath, JTextField frameRateTextField, double threshold, double minArea, double maxDistance, int filterSize, int adaptionFactor)
	{
		this.frameRateTextField = frameRateTextField;
		this.panel = panel;
		m = new Mat();
		vid = new VideoCapture(videoPath);
//		vid = new VideoCapture(0);
//		try {
//			Thread.sleep(5000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		if (vid.isOpened())
		System.out.println("file loaded");
		pc = new PersonCounter(threshold, minArea, maxDistance, filterSize, adaptionFactor);
	}
	
	/**
	 * The run method for the videoThread. Use the standard thread.start() to 
	 * start this method. 
	 * 
	 * This method runs an endless loop to show videos.
	 * Use boolean stopped to pause and resume the video.
	 * Use boolean running to kill the video.
	 * 
	 * This method also measures the current fps which will be shown in the
	 * JTextField you specified in the constructor.
	 */
	@Override
	public void run() {
		this.running = true;
		double lastTime = System.nanoTime();
		int fpsCounter = 0;
		panel.getParent().getParent().repaint();
		while(running)
		{
			if(!stopped && vid.read(m))
			{
				int c = pc.count(m);
				Mat img    = pc.getResultColor();
				Mat imgFBW = pc.getForegroundBW();
				Mat diff   = pc.getDifferenceGrey();
				Mat grey   = pc.getGrey();
				//Mat img = pc.test;
				
				panel.addMatrix(img);
				//panel.addMatrix(imgFBW);
				panel.addMatrix(imgFBW);
				//panel.addMatrix(grey);
				panel.draw();
				
				fpsCounter++;
			}
			
			if(((System.nanoTime() - lastTime) / 1000000.0) > 1000)
			{
				frameRateTextField.setText(""+fpsCounter);
				lastTime = System.nanoTime();
				fpsCounter = 0;
			}
		}
	}
	
	/**
	 * Pauses the video.
	 */
	public void pauseVideo() {
		this.stopped = true;
	}
	/**
	 * Plays the video if it is stopped.
	 */
	public void playVideo() {
		this.stopped = false;
	}
	/**
	 * Returns a value that indicates if the video is playing or paused.
	 * @return The status of the video: running or stopped.
	 */
	public boolean videoIsRunning() {
		return !this.stopped;
	}
	/**
	 * stopRunning ends the endless loop the video runs in. Call this
	 * method if you want to kill the thread.
	 */
	public void stopRunning() {
		this.running = false;
	}
	/**
	 * Gives you the object of PersonCounter this class is holding. This
	 * object is the main part of the image processing and so holds
	 * much information about it, i.e. the thresholds etc.
	 * @return The PersonCounter object that was used in the constructor
	 * of this class.
	 */
	public PersonCounter getPersonCounter()
	{
		return this.pc;
	}
	
}
