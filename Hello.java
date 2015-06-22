import javax.swing.JFrame;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

public class Hello
{
	
	public static void main( String[] args )
	{
		
		JFrame window = new JFrame("window");
		window.setSize(800, 500);
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
		
		
		while(vid.read(m))
		{
			  
			int c = pc.count(m);
			Mat img = pc.getResultColor();
			//Mat img = pc.getForegroundBW();
			//Mat img = pc.test;
			panel.draw(img);

			
		}
	      
	}

   
}