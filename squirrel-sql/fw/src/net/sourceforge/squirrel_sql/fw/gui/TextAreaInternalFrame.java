/*
 * Copyright (C) 2002 Johan Compagner
 * jcompagner@j-com.nl
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
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
