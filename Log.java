//package personCounter;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

public final class Log {
	
	private static JScrollPane scrollPane;
	private static JTextArea logTextArea;
	
	private Log(){}
	
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
	
	public static JScrollPane getComponent() {  return scrollPane; }
	
	public static void add(String string)
	{
		logTextArea.setText(logTextArea.getText()+"\r\n"+string);
	}
	
	public static void addSeperator()
	{
		Log.add("------------------------------------");
	}
} 
