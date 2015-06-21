package personCounter;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;


public class Panel  extends JPanel{
	
	BufferedImage img;
	
	public void draw(Mat m)
	{
		
		
		int type = BufferedImage.TYPE_BYTE_GRAY;
		if ( m.channels() > 1 ) {
			Mat m2 = new Mat();
			Imgproc.cvtColor(m,m2,Imgproc.COLOR_BGR2RGB);
			type = BufferedImage.TYPE_3BYTE_BGR;
			m = m2;
		}
		
		byte [] b = new byte[m.channels()*m.cols()*m.rows()];
		m.get(0,0,b); // get all the pixels
		this.img = new BufferedImage(m.cols(),m.rows(), type);
		this.img.getRaster().setDataElements(0, 0, m.cols(),m.rows(), b);
		this.getParent().repaint();
	}
	
	@Override
	public void paint(Graphics g)
	{
		super.paint(g);
		g.drawImage(img, 0,0, this);
	}
	

}
