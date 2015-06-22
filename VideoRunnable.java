package personCounter;

import javax.swing.JTextField;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

public class VideoRunnable implements Runnable{

	private String videoPath = "/home/jan/opencv_workspace/personCounter/leute.mp4";
	
	private double fps;
	private boolean running = false;
	private boolean stopped = false;
	
	private Mat m;
	private VideoCapture vid;
	private PersonCounter pc;
	private Panel panel;
	private JTextField frameRateTextField;
	
	public VideoRunnable(Panel panel, JTextField frameRateTextField, double threshold, double minArea, double maxDistance, int filterSize, int adaptionFactor)
	{
		this.frameRateTextField = frameRateTextField;
		this.panel = panel;
		m = new Mat();
		vid = new VideoCapture(videoPath);
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
				Mat img = pc.getResultColor();
				//Mat img = pc.getForegroundBW();
				//Mat img = pc.test;
				panel.draw(img);
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
