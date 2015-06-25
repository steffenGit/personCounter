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
	/**
	 * @return the current frame as greyscale image.
	 */
	public Mat getGrey(){
		return this.current.grey;
	}	
	
	/**
	 * @return the current background reference-image
	 */
	public Mat getBackgroundGrey(){
		return this.current.backgroundGrey;
	}
	
	/**
	 * @return the current difference image
	 */
	public Mat getDifferenceGrey(){
		return this.current.differenceGrey;
	}
	
	/**
	 * @return the current thresholded difference image
	 */
	public Mat getForegroundBW(){
		return this.current.foregroundBW;
	}
	
	/**
	 * @return get the final result image with boxes
	 */
	public Mat getResultColor(){
		return this.current.resultColor;
	}
	
	
	/**
	 * The method serves as main counting method.
	 * Provide a single frame and the programm will take it from there.
	 * Call this method every frame and provide the current Mat.
	 * Afterwards, use the getters above to retrieve the images.
	 * 
	 * @param img
	 * @return
	 */
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
		
		// do the tracking
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
	
	/**
	 * Attaches ROIs to known people.
	 * All ROIs used here will be removed from the bbs-list.
	 * All People that can not be attached to any ROI will be put into the lonely-list.
	 * 
	 * We loop over all known people and try to find the closest ROI within reach.
	 * 
	 * @param bbs the List of ROIS
	 * @param lonely the list of lonely people. Is probably empty at this point.
	 */
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

	/**
	 * This method tries to attach lonely people to left over ROIS.
	 * It basically increases the maxDistance for peopleattaching. 
	 * @param bbs the List of leftover ROIS
	 * @param lonely the list of lonely people
	 */
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
	
	
	/**
	 * Removes lonely people if they are touching the boundaries of the image.
	 * 
	 * @param lonely
	 */
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
	
	/**
	 * create new people and add them to the list, 
	 * if we have a ROI at the boundaries of the image and no corresponding Person.
	 * @param bbs
	 */
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

	
	/**
	 * Processes the image, does the background substraction and creates 
	 * the foregroundBW image needed for the counting mechanisms.
	 *  
	 * @param img the original image
	 */
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
	
	/**
	 * Finds the contours of ROIs within the foregroundBW image.
	 * Stores and returns them if, if their area() is larger than minArea. 
	 * @param f the current Frame.
	 * @param minArea the minArea of the Contours.
	 * @return the List of contours.
	 */
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
	
	/**
	 * Creates the Boundingboxes of the contours, if thei area() is larger than minSize.
	 * @param contours List of the contours found by findContours(...)
	 * @param minSize the minSize of the BoundingBoxes
	 * @return the List of Boundingboxes
	 */
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
	
	/**
	 * Merges all boundingboxes that are touching. If so, merge them
	 * If their Sizedifference is less than 20%, dont merge them, to make sure people dont merge.
	 * BUT: doesnt seem to be very practicle at the moment.
	 * @param bbs List of Boundingboxes to merge
	 * @return the List of Merged Boxes.
	 */
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

				// merge them, if they colide on the x-axis 
				if (r1.x < r2.x + r2.width &&
						r1.x + r1.width > r2.x /*&&
						r1.y < r2.y + r2.height &&
						r1.height + r1.y > r2.y*/) 
				{		
					
					// step out of the loop, if the sizedifference is less that 20%.
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
	
	/**
	 * Sets the current Frame as reference-frame.
	 * Usefull for the android-app to make adaption possible.
	 * 
	 */
	public void setCurrentFrameAsReference()
	{
		Imgproc.cvtColor(this.current.orig, this.current.grey, Imgproc.COLOR_RGB2GRAY);		
		// init images
		this.current.backgroundGrey = this.current.grey.clone();
	}
	
	
	public void resetCounter()
	{
		this.people = new ArrayList<Person>();
	}
}
