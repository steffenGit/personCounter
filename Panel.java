//package personCounter;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;


public class Panel  extends JPanel{
	BufferedImage coloredImage;
	BufferedImage backgroundImage;
	BufferedImage foregroundImage;
	BufferedImage differenceImage;
	
	int defaultImageWidth = 300;
	int defaultImageHeight = 300;
	
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
		
		//image = (BufferedImage)image.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
		
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
	
	public void draw(Mat coloredMatrix, Mat foregroundMatrix, Mat differenceMatrix, Mat backgroundMatrix)
	{
		coloredImage = matrixToBufferedImage(coloredMatrix, coloredImage);
		coloredImage = rescaleImage(coloredImage, defaultImageWidth, defaultImageHeight);
		foregroundImage = matrixToBufferedImage(foregroundMatrix, foregroundImage);
		foregroundImage = rescaleImage(foregroundImage, defaultImageWidth, defaultImageHeight);
		differenceImage = matrixToBufferedImage(differenceMatrix, differenceImage);
		differenceImage = rescaleImage(differenceImage, defaultImageWidth, defaultImageHeight);
		backgroundImage = matrixToBufferedImage(backgroundMatrix, backgroundImage);
		backgroundImage = rescaleImage(backgroundImage, defaultImageWidth, defaultImageHeight);
		getParent().repaint();
	}
	
	@Override
	public void paint(Graphics g)
	{
		super.paint(g);
		g.drawImage(coloredImage, 0,0, this);
		g.drawImage(backgroundImage, defaultImageWidth+20, 0, this);
		g.drawImage(foregroundImage, 0, defaultImageHeight+20, this);
		g.drawImage(differenceImage, defaultImageWidth+20, defaultImageHeight+20, this);
	}
	

}
