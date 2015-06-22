import org.opencv.core.Rect;


public class Person {
	int id;
	Rect boundingbox;
	int oldX, oldY;

	
	public boolean collidesWith(Rect r2)
	{
		Rect r1 = this.boundingbox;
		if (r1.x < r2.x + r2.width &&
				r1.x + r1.width > r2.x /*&&
				r1.y < r2.y + r2.height &&
				r1.height + r1.y > r2.y*/)
			return true;
		else
			return false;
				
	}
	
}
