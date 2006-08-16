package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

/*
 * Copyright (C) 2006 Rob Manning
 * manningr@users.sourceforge.net
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

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextArea;
import javax.swing.JTextField;

import junit.framework.TestCase;
import net.sourceforge.squirrel_sql.client.ApplicationArguments;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;

/**
 * JUnit test for DataTypeDouble class.
 * 
 * @author manningr
 */
public class DataTypeDoubleTest extends TestCase {

	private DataTypeDouble iut = null;
	
	protected void setUp() throws Exception {
		super.setUp();
		ApplicationArguments.initialize(new String[0]);
		ColumnDisplayDefinition columnDef = new ColumnDisplayDefinition(10, "aLabel");
		iut = new DataTypeDouble(null, columnDef);
	}

	public void testGetJTextField() {
		JTextField tf = iut.getJTextField();
		tf.setText("111111111111");
		KeyListener[] listeners = tf.getKeyListeners();
		if (listeners.length > 0) {
			KeyListener listener = listeners[0];
			KeyEvent e = new KeyEvent(tf, -1, 1111111111l, -1, -1, (char)KeyEvent.VK_ENTER);
			try {
				// Test for bug 1541154 (ArrayIndexOutOfBoundsException in DataTypeDouble)
				listener.keyTyped(e);
			} catch (RuntimeException ex) {
				ex.printStackTrace();
				fail("Unexpected exception: "+ex.getMessage());
			}
		}
	}

	public void testGetJTextArea() {
		JTextArea ta = iut.getJTextArea(null);
		ta.setText("111111111111");
		KeyListener[] listeners = ta.getKeyListeners();
		if (listeners.length > 0) {
			KeyListener listener = listeners[0];
			KeyEvent e = new KeyEvent(ta, -1, 1111111111l, -1, -1, (char)KeyEvent.VK_ENTER);
			try {
				// Test for bug 1541154 (ArrayIndexOutOfBoundsException in DataTypeDouble)				
				listener.keyTyped(e);
			} catch (RuntimeException ex) {
				ex.printStackTrace();
				fail("Unexpected exception: "+ex.getMessage());
			}
		}
	}

}
