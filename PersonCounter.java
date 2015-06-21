import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.*;

public class PersonCounter {
	ArrayList<Person> people;
	double threshold;
	double minArea;
	double maxDistance;
	int filterSize;
	int adaptionFactor;
	Frame current;
	Frame last;
	Mat test;
	
	public PersonCounter(double threshold, 
			double minArea, 
			double maxDistance, 
			int filterSize, 
			int adaptionFactor)
	{
		this.threshold = threshold;
		this.minArea = minArea;
		this.maxDistance = maxDistance;
		this.filterSize = filterSize;
		this.adaptionFactor = adaptionFactor;
		
		this.current = new Frame();
		this.last = null;
		this.test = new Mat();
		
		this.current.grey = new Mat();
		this.current.backgroundGrey = null;
		this.current.differenceGrey = null;
		this.current.foregroundBW = null;
		this.current.resultColor = null;
		
	}
	public Mat getGrey(){
		return this.current.grey;
	}	
	public Mat getBackgroundGrey(){
		return this.current.backgroundGrey;
	}
	public Mat getDifferenceGrey(){
		return this.current.differenceGrey;
	}
	public Mat getForegroundBW(){
		return this.current.foregroundBW;
	}
	public Mat getResultColor(){
		return this.current.resultColor;
	}
	
	
	
	public int count(Mat img)
	{
		// process the image, getting our ROIs
		this.processImage(img);


		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();    
	    Imgproc.findContours(this.current.foregroundBW.clone(), contours, new Mat(), Imgproc.RETR_LIST,Imgproc.CHAIN_APPROX_SIMPLE);
	    for (int i = 0; i < contours.size(); i++)
	    {
	    	if (Imgproc.contourArea(contours.get(i)) > 300)
	    	{
	    	    Imgproc.drawContours(this.current.resultColor, contours, i, new Scalar(0,0,255), 2);
	    	}
	    }
		
		this.last = this.current.clone();
		return 0;
	}
	
	
	public int processImage(Mat img)
	{
		this.current.orig = img.clone();
		this.current.height = this.current.orig.rows();
		this.current.width = this.current.orig.cols();
		
		this.current.resultColor = img.clone();
		
		// grab current frame
		Imgproc.cvtColor(img, this.current.grey, Imgproc.COLOR_RGB2GRAY);
		
		// init images
		if (this.current.backgroundGrey == null)
			this.current.backgroundGrey = this.current.grey.clone();

		if (this.current.differenceGrey == null)
			this.current.differenceGrey = this.current.grey.clone();
		
		if (this.current.foregroundBW == null)
			this.current.foregroundBW = this.current.grey.clone();
		

		
		// blur the image
		Imgproc.GaussianBlur(this.current.grey, this.current.grey, new Size(this.filterSize, this.filterSize), 0);		
		
		// adapt background		
		for(int r = 0; r < this.current.grey.rows(); r++)
		{
			for(int c = 0; c < this.current.grey.cols(); c++)
			{
				if(this.current.grey.get(r,  c)[0] > this.current.backgroundGrey.get(r, c)[0])
					this.current.backgroundGrey.put(r, c, this.current.backgroundGrey.get(r, c)[0]+this.adaptionFactor);
				else
					this.current.backgroundGrey.put(r, c, this.current.backgroundGrey.get(r, c)[0]-this.adaptionFactor);
			}
		}
		
		//get difference			
		Core.absdiff(this.current.grey, this.current.backgroundGrey, this.current.differenceGrey);
		
		//get thresholded-image
		Imgproc.threshold(this.current.differenceGrey, this.current.foregroundBW, this.threshold, 255, Imgproc.THRESH_BINARY);
		
		
		
		try
		{
			Core.bitwise_or(this.current.foregroundBW, this.last.foregroundBW, this.test);
		}
		catch(Exception e)
		{
			this.test = this.current.foregroundBW.clone();
		}

		return 0;
	}
	

}
