package personCounter;

import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JSlider;


public class LabeldSlider 
{
	JLabel label;
	JSlider slider;
	String labelText = "LabelText";
	int min = 0;
	int max = 100;
	int defaultValue = 10;
	int majorTickSpacing = 10;
	int minorTickSpacing = 1;
	int fontSize = 9;
	
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
	
	public JLabel getLabel() { return this.label; }
	public JSlider getSlider() { return this.slider; }
}
