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
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.sourceforge.squirrel_sql.BaseSQuirreLTestCase;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;

/**
 * Base class for various data type class JUnit tests.
 * 
 * @author manningr
 */
public abstract class AbstractDataType extends BaseSQuirreLTestCase {

	protected IDataTypeComponent iut = null;
		
	public abstract void testTextComponents();

	public AbstractDataType() {
		super();
	}

	protected ColumnDisplayDefinition getColDef() {
		return new ColumnDisplayDefinition(10, "aLabel");
	}
	
	protected void testTextComponents(IDataTypeComponent dtc) {
		JTextField tf = dtc.getJTextField();
		tf.setText("111111111111");
		testKeyListener(tf);
		JTextArea ta = dtc.getJTextArea(null);
		ta.setText("111111111111");
		testKeyListener(ta);		
	}

	protected void testKeyListener(Component c) {
		KeyListener[] listeners = c.getKeyListeners();
		if (listeners.length > 0) {
			KeyListener listener = listeners[0];
			KeyEvent e = new KeyEvent(c, -1, 1111111111l, -1, -1, (char)KeyEvent.VK_ENTER);
			// Test for bug 1541154 (ArrayIndexOutOfBoundsException in DataTypeDouble)				
			listener.keyTyped(e);
		}
	}

}