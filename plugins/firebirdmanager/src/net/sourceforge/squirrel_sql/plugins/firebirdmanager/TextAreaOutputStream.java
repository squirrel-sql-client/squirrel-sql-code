/*
 * Copyright (C) 2008 Michael Romankiewicz
 * mirommail(at)web.de
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package net.sourceforge.squirrel_sql.plugins.firebirdmanager;

import java.io.OutputStream;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * OutputStream into a textarea
 * @author michael Romankiewicz
 *
 */
public class TextAreaOutputStream extends OutputStream {
	private JTextArea textArea;
	private JScrollPane scrollPane;

	/**
	 * Constructor with textarea to use
	 * @param textArea textarea to use
	 */
	public TextAreaOutputStream(JTextArea textArea, JScrollPane scrollPane) {
		setTextArea(textArea);
		setScrollPane(scrollPane);
	}
	
	@Override
	public void write(int b) {
		this.textArea.append((char)b + "");
    	refreshDisplay();
	}
	
	@Override
    public void write(byte[] b) {
        this.textArea.append(new String(b));
    	refreshDisplay();
    }
    
	@Override
    public void write(byte[] b, int off, int len) {
    	this.textArea.append(new String(b, off, len));
    	refreshDisplay();
    }	
	
	/**
	 * Refresh the textarea and scroll to the end
	 */
	private void refreshDisplay() {
        this.textArea.setCaretPosition(this.textArea.getDocument().getLength());
        this.textArea.scrollRectToVisible(this.textArea.getVisibleRect());
    	this.scrollPane.getVerticalScrollBar().setValue(this.scrollPane.getVerticalScrollBar().getMaximum());
    	this.textArea.paintImmediately(0,0,this.textArea.getWidth(), this.textArea.getHeight());
	}

	
	/**
	 * Getter for using textarea
	 * @return using textarea
	 */
	public JTextArea getTextArea() {
		return textArea;
	}

	/**
	 * Setter for using textarea
	 * @param textArea textarea to use
	 */
	public void setTextArea(JTextArea textArea) {
		this.textArea = textArea;
	}

	/**
	 * Getter for the scrollpane of the textarea
	 * @return scrollpane of the textarea
	 */
	public JScrollPane getScrollPane() {
		return scrollPane;
	}

	/**
	 * Setter for the scrollpane of the textarea
	 * @param scrollPane scrollpane of the textarea
	 */
	public void setScrollPane(JScrollPane scrollPane) {
		this.scrollPane = scrollPane;
	}
}
