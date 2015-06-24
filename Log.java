//package personCounter;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

/**
 * A basic JTextArea at the right of the window you can use to show
 * debug messages. The textArea is lying within a JScrollPane which
 * autmatically scrolls to the bottom, so you always see the most
 * recent debug messages.
 * 
 * This class consists only out of static members and methods. 
 * Instead of creating an instance of this class, just call the
 * Log.initialize() at the beginning of your main method. And
 * after that add text to the log with Log.add(string).
 * 
 * @author jan
 *
 */
public final class Log {
	
	private static JScrollPane scrollPane;
	private static JTextArea logTextArea;
	
	private Log(){}
	
	/**
	 * Sets some design values for the GUI-Elements and
	 * puts some text in the textArea.
	 */
	public static void initialize()
	{
		logTextArea = new JTextArea();
		logTextArea.setBackground(Color.gray);
		logTextArea.setEditable(false);
		logTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 10));
		logTextArea.setForeground(Color.white);
		logTextArea.setLineWrap(true);
		
		scrollPane = new JScrollPane(logTextArea);
	    scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setPreferredSize(new Dimension(250, 500));
		
		Log.add("--------------DEBUG-----------------");
		Log.add("Log now running...");
		Log.addSeperator();
	}
	
	/**
	 * Getter used to get the JScrollPane. Use this method
	 * to put the Log into your window.
	 * @return
	 */
	public static JScrollPane getComponent() {  return scrollPane; }
	
	/**
	 * Adds the given string at the end of the string in the
	 * JTextArea.
	 * @param string The string you want to show in the Log.
	 */
	public static void add(String string)
	{
		logTextArea.setText(logTextArea.getText()+"\r\n"+string);
	}
	/**
	 * Adds a seperating string to the JTextArea to make it more
	 * readable.
	 */
	public static void addSeperator()
	{
		Log.add("------------------------------------");
	}
} 
