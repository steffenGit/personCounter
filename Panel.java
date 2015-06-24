//package personCounter;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.RepaintManager;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

/**
 * The class that shows the videos in the center of the screen.
 * 
 * How it works:
 * 1. Add a matrix by calling "addMatrix(yourMatrix)".
 * 2. The matrix will then be parsed into a BufferedImage and rescaled.
 * 3. The BufferedImage will be saved in the videos-ArrayList.
 * 4. Call the "draw()" method to trigger the paint(Graphics g) method.
 * 5. The paint()-method will draw all the images into this panel.
 * 6. The paint()-method will empty the videos-ArrayList.
 * 7. Continue at step 1 with the next frame.
 * 
 * IMPORTANT: Actually this class doesn't show a video. It only shows
 * one image of the video. The class that makes the actual video out of
 * the different single images is the VideoRunnable class which calls
 * the paint() method of Panel in an endless loop.
 * 
 * @author jan
 *
 */
public class Panel  extends JPanel{
	/**
	 * The list of images that will be painted by paint(Graphics g).
	 */
	ArrayList<BufferedImage> videos = new ArrayList<BufferedImage>();
	/**
	 * The width of the images.
	 */
	int defaultImageWidth = 640;
	/**
	 * The height of the images.
	 */
	int defaultImageHeight = 320;
	
	/**
	 * Calls the parents constructor and sets the automated repaint() of
	 * java components to stop so java won't repaint this component when
	 * a parent gets repainted.
	 */
	public Panel()
	{
		super();
		//prevent automatic repaint
		RepaintManager.currentManager(this).markCompletelyClean(this);
	}
	/**
	 * Parses a matrix of the openCV Mat to a BufferedImage so it can
	 * be drawn onto a JPanel.
	 * 
	 * @param m The matrix that should be parsed.
	 * @param image The image that the matrix will be parsed in.
	 * @return The image representing the matrix.
	 */
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
	/**
	 * Rescales the Buffered image to the given size.
	 * 
	 * The image parameter will be parsed from BufferedImage to an object
	 * of type Image with the size given by the width and height parameters.
	 * 
	 * After that a Graphics2D object paints the image into a new 
	 * BufferedImage.
	 * 
	 * 
	 * @param img The image that should be scaled to width and height.
	 * @param width The new width of the image.
	 * @param height The new height of the image.
	 * @return A BufferedImage with the new size.
	 */
	public BufferedImage rescaleImage(BufferedImage img, int width, int height)
	{
		Image tmp = img.getScaledInstance(width, height, Image.SCALE_REPLICATE);
	    BufferedImage rescaledImage = new BufferedImage(width, height, BufferedImage.SCALE_REPLICATE);

	    Graphics2D g2d = rescaledImage.createGraphics();
	    g2d.drawImage(tmp, 0, 0, null);
	    g2d.dispose();
	    return rescaledImage;
	}
	/**
	 * Adds a matrix to the list of images and scales it to the default
	 * size.
	 * 
	 * @param videoMatrix The matrix that should be shown the next time draw gets called.
	 */
	public void addMatrix(Mat videoMatrix)
	{
		BufferedImage img = new BufferedImage(videoMatrix.cols(), videoMatrix.rows(), BufferedImage.TYPE_3BYTE_BGR);
		img = matrixToBufferedImage(videoMatrix, img);
		img = rescaleImage(img, defaultImageWidth, defaultImageHeight);
		videos.add(img);
	}
	/**
	 * Triggers a repaint which will draw all the images in the
	 * arrayList.
	 */
	public void draw()
	{
		this.repaint();
	}
	/**
	 * Draws all the images in the arrayList and afterwards emptys
	 * the arrayList.
	 * 
	 * @param g The graphics object that paints the images.
	 */
	@Override
	public void paint(Graphics g)
	{
		super.paint(g);
		for(int i=0; i<videos.size(); i++) {
			g.drawImage(videos.get(i), 0, defaultImageHeight*i + i*20, this);
		}
		videos.clear();
	}
}
