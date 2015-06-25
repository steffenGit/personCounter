package personCounter;

import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JSlider;

/**
 * A combination of a JSlider and a JLabel. This class is entirely
 * used to avoid code redundancy in the main method.
 * (Just GUI-stuff, nothing image processing related)
 * 
 * @author jan
 *
 */
public class LabeldSlider 
{
	/**
	 * The text label of the slider. Put your slider description in this label. 
	 */
	JLabel label;
	/**
	 * The slider used.
	 */
	JSlider slider;
	/**
	 * The text of the label used.
	 */
	String labelText = "LabelText";
	/**
	 * Minimum value of the slider.
	 */
	int min = 0;
	/**
	 * Maximum value of the slider.
	 */
	int max = 100;
	/**
	 * The start value of the slider.
	 */
	int defaultValue = 10;
	/**
	 * The labeled steps of the slider.
	 */
	int majorTickSpacing = 10;
	/**
	 * The small tick spaces represented by small strokes.
	 */
	int minorTickSpacing = 1;
	/**
	 * The font size of the majorTickSpacing.
	 */
	int fontSize = 9;
	
	/**
	 * Saves the values of in class member variables, creates a JLabel and a JSlider.
	 * Sets some default presentational options of the label and slider.
	 * 
	 * @param labelText The text of the label.
	 * @param min The minimum value of the slider.
	 * @param max The maximum value of the slider.
	 * @param defaultValue The starting value of the slider.
	 * @param minorTickSpacing The small tick spacing of the slider represented by strokes.
	 * @param majorTickSpacing The big tick spacing of the slider represented by labeld numbers.
	 */
	public LabeldSlider(String labelText, int min, int max, int defaultValue, int minorTickSpacing, int majorTickSpacing)
	{
		this.labelText = labelText;
		this.min = min;
		this.max = max;
		this.defaultValue = defaultValue;
		this.majorTickSpacing = majorTickSpacing;
		this.minorTickSpacing = minorTickSpacing;
		
		label = new JLabel(labelText);
		slider = new JSlider(JSlider.HORIZONTAL, min, max, defaultValue);
		slider.setMajorTickSpacing(majorTickSpacing);
		slider.setMinorTickSpacing(minorTickSpacing);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		slider.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
        Font font = new Font("Serif", Font.ITALIC, 9);
        slider.setFont(font);
	}
	
	/**
	 * Getter for the JLabel used in this class.
	 * @return The JLabel of the slider.
	 */
	public JLabel getLabel() { return this.label; }
	/**
	 * Getter for the JSlider used in this class.
	 * @return The JSlider.
	 */
	public JSlider getSlider() { return this.slider; }
}
