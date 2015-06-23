package personCounter;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.RepaintManager;
import javax.swing.Scrollable;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;


public class Panel  extends JPanel{
	boolean draw = false;
	ArrayList<BufferedImage> videos = new ArrayList<BufferedImage>();
	int defaultImageWidth = 640;
	int defaultImageHeight = 320;
	
	public Panel()
	{
		super();
		//prevent automatic repaint
		RepaintManager.currentManager(this).markCompletelyClean(this);
	}
	
	public BufferedImage matrixToBufferedImage(Mat m, BufferedImage image)
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
		image = new BufferedImage(m.cols(),m.rows(), type);
		image.getRaster().setDataElements(0, 0, m.cols(),m.rows(), b);
		return image;
	}
	
	public BufferedImage rescaleImage(BufferedImage img, int width, int height)
	{
		Image tmp = img.getScaledInstance(width, height, Image.SCALE_REPLICATE);
	    BufferedImage rescaledImage = new BufferedImage(width, height, BufferedImage.SCALE_REPLICATE);

	    Graphics2D g2d = rescaledImage.createGraphics();
	    g2d.drawImage(tmp, 0, 0, null);
	    g2d.dispose();
	    return rescaledImage;
	}
	
	public void addMatrix(Mat videoMatrix)
	{
		BufferedImage img = new BufferedImage(videoMatrix.cols(), videoMatrix.rows(), BufferedImage.TYPE_3BYTE_BGR);
		img = matrixToBufferedImage(videoMatrix, img);
		img = rescaleImage(img, defaultImageWidth, defaultImageHeight);
		videos.add(img);
	}
	
	public void draw()
	{
		//this.getParent().getParent().repaint();
		draw = true;
		//this.getParent().getParent().repaint();
		this.repaint();
	}
	
	@Override
	public void paint(Graphics g)
	{
		super.paint(g);
		if(draw) {
			for(int i=0; i<videos.size(); i++) {
				g.drawImage(videos.get(i), 0, defaultImageHeight*i + i*20, this);
			}
			videos.clear();
			draw = false;
		}
	}
	
	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension(700, 2000);
	}
}
