/*
 * Copyright (C) 2011 Stefan Willinger
 * wis775@users.sourceforge.net
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
package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import static org.easymock.EasyMock.expect;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.sql.Types;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;

import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JTextComponentFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * An abstract test case, that provide some generic tests for the text components
 * of the different numeric data types.
 * This test case was written, to ensure the same behavior of different numeric data
 * types when editing the value through the {@link JTextField}
 * @author Stefan Willinger
 * 
 */
public abstract class AbstractNumericDataTypeUITest extends BaseSQuirreLJUnit4TestCase {

	/**
	 * 
	 */
	private static final String TA_UN_SIGNED = "taUnSigned";
	/**
	 * 
	 */
	private static final String TA_SIGNED = "taSigned";
	/**
	 * 
	 */
	private static final String TF_UN_SIGNED = "tfUnSigned";
	/**
	 * 
	 */
	private static final String TF_SIGNED = "tfSigned";
	protected FrameFixture fixture = null;
	
	
	/**
	 * Sets up the test environment.
	 * It's important, that swing components and mock objects are created within the event dispatcher thread.
	 */
	@Before
	public void setUp() throws Exception {
		JFrame frame = constructTestFrameInEDT();

		fixture = new FrameFixture(frame);
		fixture.show();
		
	}
	
	@After
	public void tearDown() {
		fixture.cleanUp();
	}

	/**
	 * Set up the environment within the event dispatcher thread
	 * @return JFrame, which contains the testable components.
	 */
	protected JFrame constructTestFrameInEDT() {
		 return GuiActionRunner.execute(new GuiQuery<JFrame>() {
		      protected JFrame executeInEDT() {
		    	  return constructTestFrame();
		      }
		  });
	}
	
	/**
	 * Create a simple JFrame, which contains the {@link JTextField} and a {@link JTextArea} of the {@link IDataTypeComponent} for testing.
	 * One for signed and unsigned data types.
	 * @see #TF_SIGNED
	 * @see #TF_UN_SIGNED
	 * @see #TA_SIGNED
	 * @see #TA_UN_SIGNED
	 */
	private JFrame constructTestFrame() {
		
		ColumnDisplayDefinition columnDisplayDefinitionUnsigned = getMockColumnDisplayDefinition(false);
		ColumnDisplayDefinition columnDisplayDefinitionSigned = getMockColumnDisplayDefinition(true);
		mockHelper.replayAll();
		
	
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, 1));
		panel.setPreferredSize(new Dimension(100, 50));

		IDataTypeComponent dataType;
		
		// text field signed
		dataType = initClassUnderTest(columnDisplayDefinitionSigned);
		addTextComponentToPanel(panel, dataType.getJTextField(), TF_SIGNED);
		
		// text field unSigned
		dataType = initClassUnderTest(columnDisplayDefinitionUnsigned);
		addTextComponentToPanel(panel, dataType.getJTextField(), TF_UN_SIGNED);

		// text area signed
		dataType = initClassUnderTest(columnDisplayDefinitionSigned);
		addTextComponentToPanel(panel, dataType.getJTextArea(null), TA_SIGNED);
		
		// text area unSigned
		dataType = initClassUnderTest(columnDisplayDefinitionUnsigned);
		addTextComponentToPanel(panel, dataType.getJTextArea(null), TA_UN_SIGNED);
		
		final JFrame frame = new JFrame("Test edit Numeric DataTypes");
		frame.getContentPane().add(panel);
		frame.setSize(new Dimension(300, 300));
		frame.setPreferredSize(new Dimension(300, 300));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocation(10, 10);
		return frame;
		
		
	}

	private void addTextComponentToPanel(JPanel panel, JTextComponent textComponent, String name){
		JPanel subPanel = new JPanel();
		subPanel.add(new JLabel(name));
		textComponent.setName(name);
		textComponent.setPreferredSize(new Dimension(100, 20));
		subPanel.add(textComponent);
		panel.add(subPanel);
	}
	

	/**
	 * Create the concrete {@link IDataTypeComponent} that should be tested.
	 * @param columnDisplayDefinition Display definition to use.
	 */
	protected abstract IDataTypeComponent initClassUnderTest(ColumnDisplayDefinition columnDisplayDefinition);

	

	/**
	 * Some DataTypeComponents require a ColumnDisplayDefinition in their
	 * constructor. This returns a mock which can be passed into the
	 * constructor.
	 */
	protected ColumnDisplayDefinition getMockColumnDisplayDefinition(boolean signed) {
		ColumnDisplayDefinition columnDisplayDefinition = mockHelper.createMock(
				"testColumnDisplayDefinition", ColumnDisplayDefinition.class);
		expect(columnDisplayDefinition.isNullable()).andStubReturn(true);
		expect(columnDisplayDefinition.isSigned()).andStubReturn(signed);
		expect(columnDisplayDefinition.getPrecision()).andStubReturn(10);
		expect(columnDisplayDefinition.getScale()).andStubReturn(3);
		expect(columnDisplayDefinition.getColumnSize()).andStubReturn(10);
		expect(columnDisplayDefinition.getColumnName()).andStubReturn("testLabel");
		expect(columnDisplayDefinition.getSqlType()).andStubReturn(Types.NUMERIC);
		expect(columnDisplayDefinition.getSqlTypeName()).andStubReturn("NUMERIC");
		return columnDisplayDefinition;
	}



	@Test
	public void testInsertMinusSign() throws Exception {
		JTextComponentFixture textBox = fixture.textBox(TF_SIGNED);
		textBox.enterText("-");
		textBox.requireText("-");
		
		textBox = fixture.textBox(TF_UN_SIGNED);
		textBox.enterText("-");
		textBox.requireText("");
		
		textBox = fixture.textBox(TA_SIGNED);
		textBox.setText(""); 
		textBox.enterText("-");
		textBox.requireText("-");
		
		textBox = fixture.textBox(TA_UN_SIGNED);
		textBox.setText(""); 
		textBox.enterText("-");
		textBox.requireText("");
		
		
	}
	
	@Test
	public void testInsertMinusSignIfANumberIsPresent() throws Exception {
		JTextComponentFixture textBox = fixture.textBox(TF_SIGNED);
		textBox.setText("0");
		// go to the first position
		textBox.pressAndReleaseKeys(KeyEvent.VK_HOME);
		textBox.enterText("-");
		textBox.requireText("-0");
				
		textBox = fixture.textBox(TF_UN_SIGNED);
		textBox.setText("0");
		// go to the first position
		textBox.pressAndReleaseKeys(KeyEvent.VK_HOME);
		textBox.enterText("-");
		textBox.requireText("0");
		
		textBox = fixture.textBox(TA_SIGNED);
		textBox.setText("0");
		// go to the first position
		textBox.pressAndReleaseKeys(KeyEvent.VK_HOME);
		textBox.enterText("-");
		textBox.requireText("-0");
				
		textBox = fixture.textBox(TA_UN_SIGNED);
		textBox.setText("0");
		// go to the first position
		textBox.pressAndReleaseKeys(KeyEvent.VK_HOME);
		textBox.enterText("-");
		textBox.requireText("0");
	}
	
	/**
	 * A <code>+</code> character is not allowed as sign.
	 */
	@Test
	public void testInsertPlusSign() throws Exception {
		JTextComponentFixture textBox = fixture.textBox(TF_SIGNED);
		textBox.enterText("+");
		textBox.requireText("");
		
		fixture.textBox(TF_UN_SIGNED);
		textBox.enterText("+");
		textBox.requireText("");
		
		fixture.textBox(TA_SIGNED);
		textBox.enterText("+");
		textBox.requireText("");
		
		fixture.textBox(TA_UN_SIGNED);
		textBox.enterText("+");
		textBox.requireText("");
	}
	
	

	/**
	 * A <code>+</code> character is not allowed as sign.
	 */
	@Test
	public void testInsertPlusSignIfANumberIsPresent() throws Exception {
		JTextComponentFixture textBox = fixture.textBox(TF_SIGNED);
		textBox.setText("0");
		// go to the first position
		textBox.pressAndReleaseKeys(KeyEvent.VK_HOME);
		textBox.enterText("+");
		textBox.requireText("0");
		
		textBox = fixture.textBox(TF_UN_SIGNED);
		textBox.setText("0");
		// go to the first position
		textBox.pressAndReleaseKeys(KeyEvent.VK_HOME);
		textBox.enterText("+");
		textBox.requireText("0");
		
		textBox = fixture.textBox(TA_SIGNED);
		textBox.setText("0");
		// go to the first position
		textBox.pressAndReleaseKeys(KeyEvent.VK_HOME);
		textBox.enterText("+");
		textBox.requireText("0");
		
		textBox = fixture.textBox(TA_UN_SIGNED);
		textBox.setText("0");
		// go to the first position
		textBox.pressAndReleaseKeys(KeyEvent.VK_HOME);
		textBox.enterText("+");
		textBox.requireText("0");
	}
	
	
	
	@Test
	public void testInsertMinusSignWhenSignAllreadyPresent() throws Exception {
		JTextComponentFixture textBox = fixture.textBox(TF_SIGNED);
		textBox.setText("-0");
		textBox.pressAndReleaseKeys(KeyEvent.VK_HOME);
		textBox.enterText("-");
		textBox.requireText("-0");
		
		textBox = fixture.textBox(TA_SIGNED);
		textBox.setText("-0");
		textBox.pressAndReleaseKeys(KeyEvent.VK_HOME);
		textBox.enterText("-");
		textBox.requireText("-0");
	}
	
	@Test
	public void testInsertMinusSignAtBadLocation() throws Exception {
		JTextComponentFixture textBox = fixture.textBox(TF_SIGNED);
		textBox.setText("0");
		textBox.enterText("-");
		textBox.requireText("0");
		
		textBox = fixture.textBox(TF_UN_SIGNED);
		textBox.setText("0");
		textBox.enterText("-");
		textBox.requireText("0");
		
		textBox = fixture.textBox(TA_SIGNED);
		textBox.setText("");
		textBox.enterText("0");
		textBox.enterText("-");
		textBox.requireText("0");
		
		textBox = fixture.textBox(TA_UN_SIGNED);
		textBox.setText("");
		textBox.enterText("0");
		textBox.enterText("-");
		textBox.requireText("0");
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testInsertMinusSignWhenNullValue() throws Exception {
		JTextComponentFixture textBox = fixture.textBox(TA_SIGNED);
		textBox.setText("<null>");
		textBox.pressAndReleaseKeys(KeyEvent.VK_END);
		textBox.enterText("-");
		textBox.requireText("-");
		
		textBox.setText("<null>");
		textBox.pressAndReleaseKeys(KeyEvent.VK_HOME);
		textBox.enterText("-");
		textBox.requireText("-");
			
		
		textBox = fixture.textBox(TA_UN_SIGNED);
		textBox.setText("<null>");
		textBox.pressAndReleaseKeys(KeyEvent.VK_END);
		textBox.enterText("-");
		textBox.requireText("");
		
		textBox.setText("<null>");
		textBox.pressAndReleaseKeys(KeyEvent.VK_HOME);
		textBox.enterText("-");
		textBox.requireText("");
		
	}

}
