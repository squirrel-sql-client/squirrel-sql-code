package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.sourceforge.squirrel_sql.client.ApplicationArguments;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;

import junit.framework.TestCase;

public abstract class AbstractDataTypeTest extends TestCase {

	protected IDataTypeComponent iut = null;
	
	private static boolean appArgsInitialized = false;
	
	public abstract void testTextComponents();

	public AbstractDataTypeTest() {
		super();
	}

	public void setUp() throws Exception {
		super.setUp();
		if (!appArgsInitialized) {
			appArgsInitialized = true;
			ApplicationArguments.initialize(new String[0]);
		}		
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