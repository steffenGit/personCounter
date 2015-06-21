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
		  
		VideoCapture vid = new VideoCapture("/home/stfn/dev/eclipse/leute.mp4");
		  
		if (vid.isOpened())
		System.out.println("file loaded");
		
		PersonCounter pc = new PersonCounter(15, 100, 50, 3, 4);

		while(vid.read(m))
		{
			  
			int c = pc.count(m);
			//Mat img = pc.getResultColor();
			//Mat img = pc.getForegroundBW();
			Mat img = pc.test;
			panel.draw(img);

			
		}
	      
	}

   
}