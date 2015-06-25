//package personCounter;

import java.util.ArrayList;
import java.util.List;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.*;

public class PersonCounter {
	List<Person> people;
	public double threshold;
	public double minArea;
	public double maxDistance;
	public int filterSize;
	public double adaptionFactor;
	public int minBBsize;
	Frame current;
	Frame last;
	boolean printed = false;
	int id = 0;
	int cnt = 0;
	
	public PersonCounter(double threshold, 
			double minArea, 
			double maxDistance, 
			int filterSize, 
			double adaptionFactor,
			int minBBsize)
	{
		this.threshold = threshold;
		this.minArea = minArea;
		this.maxDistance = maxDistance;
		this.filterSize = filterSize;
		this.adaptionFactor = adaptionFactor;
		this.minBBsize = minBBsize;
		
		this.current = new Frame();
		this.last = null;
		
		this.current.grey = new Mat();
		this.current.backgroundGrey = null;
		this.current.differenceGrey = null;
		this.current.foregroundBW = null;
		this.current.resultColor = null;
		
		people = new ArrayList<Person>();
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

		List<MatOfPoint> contours;
		List<Rect> bbs;
		List<Rect> bbsIntersected;
		List<Person> lonely = new ArrayList<Person>();
		// get the contours of all shapes, with threshold
		contours = findContours(this.current, this.minArea);
		Imgproc.drawContours(this.current.resultColor, contours, -1, new Scalar(255,128,128), 1);
		
		// get their boundinboxes, with threshold
		bbs = findBoundingBoxes(contours, this.minBBsize);
				
		for(int i = 0; i < bbs.size(); i++)
		{
			Imgproc.rectangle(this.current.foregroundBW, 
					bbs.get(i).tl(), 
					bbs.get(i).br(), 
					new Scalar(255, 255,255),2);
		}
		
		bbsIntersected = mergeBoundingBoxes(bbs);
		for(int i = 0; i < bbsIntersected.size(); i++)
		{
			Imgproc.rectangle(this.current.resultColor, 
					bbsIntersected.get(i).tl(), 
					bbsIntersected.get(i).br(), 
					new Scalar(255, 0,0),1);
		}
		
		attachBBStoPeopleList(bbsIntersected, lonely);
		attachLonelyToLonely(bbsIntersected, lonely);
		removePeople(lonely);
		createNewPeople(bbsIntersected);
				
		Imgproc.putText(this.current.resultColor, "Total: " + Integer.toString(this.people.size()) + 
				" lonely " + Integer.toString(lonely.size()) + 
				" bbs left " + Integer.toString(bbsIntersected.size()), 
				new Point(15,25),
				Core.FONT_HERSHEY_SIMPLEX, 1, 
				new Scalar(255,255,255),3,1, false);
		
		for(int i = 0; i < this.people.size(); i++)
		{
			Imgproc.putText(this.current.resultColor, 
					Integer.toString(people.get(i).id), 
					new Point(people.get(i).boundingbox.tl().x, 
							this.current.height - 20), 
					Core.FONT_HERSHEY_SIMPLEX, 1, 
					new Scalar(255,255,255),2,1, false);
			Imgproc.rectangle(this.current.resultColor, 
					people.get(i).boundingbox.tl(), 
					people.get(i).boundingbox.br(), 
					new Scalar(0, 0,255),2);
		}
		

		this.last = this.current.clone();
		return 0;
	}
	
	
	void attachBBStoPeopleList(List<Rect> bbs, List<Person> lonely) {
		
		
		// loop over known people and attach them to bbs
		for(int i = 0; i < people.size(); i++)	
		{
			boolean found = false;
			int minD = (int) (2*this.maxDistance);
			int minJ = -1;
			
			for (int j = 0; j < bbs.size(); j++)
			{
				int dx1 = (int) Math.abs(bbs.get(j).tl().x - people.get(i).boundingbox.tl().x);
				int dx2 = (int) Math.abs(bbs.get(j).br().x - people.get(i).boundingbox.br().x);

				int dy = Math.abs(bbs.get(j).y - people.get(i).boundingbox.y);
				
				double d1 = Math.sqrt(dx1*dx1 + dy*dy);
				double d2 = Math.sqrt(dx2*dx2 + dy*dy);

				double d = d1+d2;		
				

				if(d < minD)
				{
					minD = (int)d;
					minJ = j;
					
					found = true;
				}
			}
			if(found)
			{
				people.get(i).oldX = people.get(i).boundingbox.x;
				people.get(i).oldY = people.get(i).boundingbox.y;				
				people.get(i).boundingbox = bbs.get(minJ).clone();
				bbs.remove(minJ);
			}
			else
			{
				lonely.add(people.get(i));
			}
		}
	}


	public void attachLonelyToLonely(List<Rect> bbs, List<Person> lonely)
	{
		// try to attach lonely people to lonely bss 
		for (int i = 0; i < lonely.size(); i++)
		{
			boolean found = false;
			
			int minD = (int) (3* this.maxDistance);
			int minJ = -1;
			
			for(int j = 0; j < bbs.size(); j++)
			{
				int dx1 = (int) Math.abs((lonely.get(i).boundingbox.tl().x - bbs.get(j).tl().x));
				int dx2 = (int) Math.abs((lonely.get(i).boundingbox.br().x - bbs.get(j).br().x));

				int dy = lonely.get(i).boundingbox.y - bbs.get(j).y;
				double d1 = Math.sqrt(dx1*dx1 + dy*dy);
				double d2 = Math.sqrt(dx2*dx2 + dy*dy);

				double d = d1+d2;
				

				if(d < minD )
				{
					minD = (int)d2;
					minJ = j;
					found = true;
				}
			}
			if (found)
			{
				lonely.get(i).boundingbox = bbs.get(minJ).clone();
				bbs.remove(minJ);
				lonely.remove(i);
			    Log.add("created");
			}
		}
	}
	
	
	public void removePeople(List<Person> lonely)
	{
		//remove people
		for(int j = 0; j < lonely.size(); j++)
		{
			Log.add("remove");
			
			int dx1 = lonely.get(j).boundingbox.x;
			Log.add(""+dx1);
			int dx2 = this.current.width - (lonely.get(j).boundingbox.x + lonely.get(j).boundingbox.width);
			Log.add(""+dx2);

			if((dx1 < 2 || dx2 < 2) && lonely.get(j).boundingbox.width < 200)
			{
				people.remove(lonely.get(j));
				lonely.remove(j);
				Log.add("removed");

			}
		}
	}
	
	public void createNewPeople(List<Rect> bbs)
	{
		// create new people
		for (int i = 0; i < bbs.size(); i++)
		{
			int dx1 = bbs.get(i).x;
			int dx2 = this.current.width - (bbs.get(i).x + bbs.get(i).width);
			if(dx1 < 3 || dx2 < 3)
			{
			    System.out.println("created");
				System.out.println(dx1);
				System.out.println(dx2);

				Person p = new Person();
				p.id = this.id;
				this.id++;
				
				p.boundingbox = bbs.get(i).clone();
				people.add(p);
				bbs.remove(i);
			}	
		}
	}	

	
	
	public void processImage(Mat img)
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
		//Imgproc.GaussianBlur(this.current.grey, this.current.grey, new Size(this.filterSize, this.filterSize), 0);		
		Imgproc.medianBlur(this.current.grey, this.current.grey, this.filterSize);
		
//		if(cnt++ % 10 == 0)
//		{
//			// adapt background		
//			for(int r = 0; r < this.current.grey.rows(); r++)
//			{
//				for(int c = 0; c < this.current.grey.cols(); c++)
//				{
//					if(this.current.grey.get(r,  c)[0] > this.current.backgroundGrey.get(r, c)[0])
//						this.current.backgroundGrey.put(r, c, this.current.backgroundGrey.get(r, c)[0]+this.adaptionFactor/10);
//					else
//						this.current.backgroundGrey.put(r, c, this.current.backgroundGrey.get(r, c)[0]-this.adaptionFactor/10);
//				}
//			}	
//		}
		
		
		//get difference			
		Core.absdiff(this.current.grey, this.current.backgroundGrey, this.current.differenceGrey);
		
		//get thresholded-image
		Imgproc.threshold(this.current.differenceGrey, this.current.foregroundBW, this.threshold, 255, Imgproc.THRESH_BINARY);
	}
	
	
	List<MatOfPoint> findContours(Frame f, double minArea)
	{
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();    
	    Imgproc.findContours(f.foregroundBW.clone(), contours, new Mat(), Imgproc.RETR_EXTERNAL,Imgproc.CHAIN_APPROX_TC89_L1);
	    for (int i = 0; i < contours.size(); i++)
	    {
	    	if (Imgproc.contourArea(contours.get(i)) < minArea)
	    	{
	    	    contours.remove(i);
	    	}
	    }
		return contours;
	}
	
	List<Rect> findBoundingBoxes(List<MatOfPoint> contours, double minSize)
	{
		List<Rect> bbs = new ArrayList<Rect>();
		for(int i = 0; i < contours.size(); i++)
		{
			Rect bb = Imgproc.boundingRect(contours.get(i));
			if (bb.area() > minSize)
			{
				bbs.add(bb);
			}
		}
		return bbs;
	}
	
	List<Rect> mergeBoundingBoxes(List<Rect> bbs)
	{
		List<Rect> bbs2 = new ArrayList<Rect>();
		for(int i = 0; i < bbs.size(); i++)
		{
			
			Rect r1 = bbs.get(i);
			Rect r = new Rect();
			bbs2.add(r);
			
			r.x = r1.x;
			r.y = r1.y;
			r.width = r1.width;
			r.height = r1.height;
			
			for(int j = 0; j < bbs.size(); j++)
			{
				
				Rect r2 = bbs.get(j);
				if(r1.equals(r2)) 
				{
					continue;
				}

				if (r1.x < r2.x + r2.width &&
						r1.x + r1.width > r2.x /*&&
						r1.y < r2.y + r2.height &&
						r1.height + r1.y > r2.y*/) 
				{		
					
					double thresh = .80;
					double A1 = r1.area();
					double A2 = r2.area();
					
					if(A1 > A2)
					{
						if(A2/A1 > thresh)
							continue;
					}
					else
					{
						if(A1/A2 > thresh)
							continue;
					}
					
					if(r2.tl().x < r1.tl().x)
						r.x = (int)r2.tl().x;
					else
						r.x = (int)r1.tl().x;
					
					if(r2.tl().y < r1.tl().y)
						r.y = (int)r2.tl().y;
					else
						r.y = (int)r1.tl().y;
					
					
					if(r2.br().x > r1.br().x)
						r.width = (int)r2.br().x - r.x;
					else
						r.width = (int)r1.br().x - r.x;
					
					if(r2.br().y > r1.br().y)
						r.height = (int)r2.br().y - r.y;
					else
						r.height = (int)r1.br().y - r.y;
					
					bbs.remove(j);
				}
	
			}			
		}
		return bbs2;
	}
	
	public void setCurrentFrameAsReference()
	{
		Imgproc.cvtColor(this.current.orig, this.current.grey, Imgproc.COLOR_RGB2GRAY);
		
		// init images
		this.current.backgroundGrey = this.current.grey.clone();
	}
}
