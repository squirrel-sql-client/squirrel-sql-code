/*
 * Copyright (C) 2005 Rob Manning
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
package net.sourceforge.squirrel_sql.plugins.oracle.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.sourceforge.squirrel_sql.client.ApplicationArguments;
import net.sourceforge.squirrel_sql.client.plugin.PluginQueryTokenizerPreferencesManager;
import net.sourceforge.squirrel_sql.client.plugin.gui.PluginQueryTokenizerPreferencesPanel;
import net.sourceforge.squirrel_sql.plugins.oracle.prefs.OraclePluginPreferencesPanel;
import net.sourceforge.squirrel_sql.plugins.oracle.prefs.OraclePreferenceBean;

import org.fest.swing.annotation.GUITest;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JCheckBoxFixture;
import org.fest.swing.fixture.JTextComponentFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * This is a FEST UI test for OraclePluginPreferencesPanel
 */
@GUITest
public class TestOraclePluginPreferencesPanel
{

	private FrameFixture fixture;
	
	@Before 
	public void setUp() throws Exception {
		JFrame f = constructTestFrame();		
		fixture = new FrameFixture(f);
		fixture.show();
	}
	
	@Test
	public void testCheckbox() {
		JCheckBoxFixture cbFixture = fixture.checkBox("useCustomQTCheckBox");
		JTextComponentFixture lineCommentTextField = fixture.textBox("lineCommentTextField");
		JTextComponentFixture statementSeparatorTextField = fixture.textBox("statementSeparatorTextField");
		
		cbFixture.uncheck();
		cbFixture.check();
		cbFixture.uncheck();
		
		lineCommentTextField.requireDisabled();
		statementSeparatorTextField.requireDisabled();
		
		cbFixture.check();
		lineCommentTextField.requireEnabled();
		statementSeparatorTextField.requireEnabled();
		
	}
	
	@After
	public void tearDown() {
		fixture.cleanUp();
	}
	
	
	
	/**
	 * The main method is not used at all in the test - it is just here to allow for user interaction testing
	 * with the graphical component, which doesn't require launching SQuirreL.
	 * 
	 * @param args
	 */
	public static void main(String[] args) throws Exception
	{
		constructTestFrame().setVisible(true);
	}

	/**
	 * Builds the frame that will be used to display the panel.
	 * 
	 * @return
	 * @throws Exception
	 */
	private static JFrame constructTestFrame() throws Exception {
		JFrame f = new JFrame();
		f.getContentPane().setLayout(new BorderLayout());
		ApplicationArguments.initialize(new String[0]);
		final PluginQueryTokenizerPreferencesManager prefsManager =
			new PluginQueryTokenizerPreferencesManager();
		prefsManager.initialize(new DummyPlugin(), new OraclePreferenceBean());
		final PluginQueryTokenizerPreferencesPanel p = new OraclePluginPreferencesPanel(prefsManager);
		JScrollPane sp = new JScrollPane(p);
		f.getContentPane().add(sp, BorderLayout.CENTER);
		JButton button = new JButton("Save");
		button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				p.applyChanges();
				prefsManager.unload();
			}
		});
		JButton exitButton = new JButton("Exit");
		exitButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				System.exit(0);
			}
		});
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(button);
		buttonPanel.add(exitButton);
		f.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		f.setBounds(200, 50, 700, 700);
		return f;
	}
}
