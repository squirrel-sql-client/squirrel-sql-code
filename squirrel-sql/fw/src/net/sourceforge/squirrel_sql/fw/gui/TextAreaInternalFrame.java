/*
 * (c) Copyright 2001 MyCorporation.
 * All Rights Reserved.
 */
package net.sourceforge.squirrel_sql.fw.gui;

import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import java.awt.BorderLayout;
import java.awt.Container;

/**
 * @version 	1.0
 * @author
 */
public class TextAreaInternalFrame extends JInternalFrame {

	/**
	 * Constructor for TextAreaInternalFrame.
	 */
	public TextAreaInternalFrame(String column, String text) 
	{
		super("Value of column " + column,true,true,true,true);
		Container con = getContentPane();
		con.setLayout(new BorderLayout());
		JTextArea area = new JTextArea(text);
		area.setEditable(false);
		JScrollPane pane = new JScrollPane(area);
		con.add(pane,BorderLayout.CENTER);
	}
}
