//package personCounter;

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
	 * @param threshold 
	 * @param minArea
	 * @param maxDistance
	 * @param filterSize
	 * @param adaptionFactor
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
	
	@Override
	public void run() {
		this.running = true;
		double lastTime = System.nanoTime();
		int fpsCounter = 0;
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
				
				panel.draw(img, imgFBW, diff, grey);
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
	
	public void pauseVideo() {
		this.stopped = true;
	}
	
	public void playVideo() {
		this.stopped = false;
	}
	
	public boolean videoIsRunning() {
		return !this.stopped;
	}
	
	public void stopRunning() {
		this.running = false;
	}
	
	public PersonCounter getPersonCounter()
	{
		return this.pc;
	}
	
}
