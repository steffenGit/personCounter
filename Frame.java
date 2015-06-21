import org.opencv.core.Mat;


public class Frame {
	Mat orig;
	Mat grey;
	Mat backgroundGrey;
	Mat differenceGrey;
	Mat foregroundBW;
	Mat resultColor;
	int width, height;
	
	public Frame clone()
	{
		
		Frame fb = new Frame();
		fb.orig = this.orig.clone();
		fb.grey = this.grey.clone();
		fb.backgroundGrey = this.grey.clone();
		fb.differenceGrey = this.differenceGrey.clone();
		fb.foregroundBW = this.foregroundBW.clone();
		fb.resultColor = this.resultColor.clone();
		fb.width = this.width;
		fb.height = this.height;
		return fb;
		
	}
	
}
