package net.sourceforge.squirrel_sql.client.gui.db.aliasproperties;

/*
 * Copyright (C) 2009 Rob Manning
 * manningr@users.sourceforge.net
 * 
 * Based on initial work from Colin Bell
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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;

import net.sourceforge.squirrel_sql.client.gui.db.SQLAliasConnectionProperties;
import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

/**
 * This panel allows the user to configure Session connection keep-alive properties
 */
public class ConnectionPropertiesPanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(ConnectionPropertiesPanel.class);

	private JCheckBox enableConnectionKeepAliveChk = null;

	private JLabel sleepForLabel = null;

	private JSpinner sleepTime = null;

	private JLabel secondsLabel = null;

	private JLabel executeLabel = null;

	private JTextArea sqlTextArea = new JTextArea();

	public interface i18n
	{
		// i18n[ConnectionPropertiesPanel.enableKeepAliveMsg=Enable Keep-Alive]
		String ENABLE_KEEP_ALIVE_MSG = s_stringMgr.getString("ConnectionPropertiesPanel.enableKeepAliveMsg");		
		
		// i18n[ConnectionPropertiesPanel.instructions=These settings control whether or not...]
		String INSTRUCTIONS = s_stringMgr.getString("ConnectionPropertiesPanel.instructions");

		// i18n[ConnectionPropertiesPanel.secondsLabel=seconds]
		String SECONDS_LABEL = s_stringMgr.getString("ConnectionPropertiesPanel.secondsLabel");

		// i18n[ConnectionPropertiesPanel.executeLabel=Execute the following SQL statement:]
		String EXECUTE_LABEL = s_stringMgr.getString("ConnectionPropertiesPanel.executeLabel");

		// i18n[ConnectionPropertiesPanel.sleepForLabel=Sleep for]
		String SLEEP_FOR_LABEL = s_stringMgr.getString("ConnectionPropertiesPanel.sleepForLabel");

	}

	private SQLAliasConnectionProperties _props = null;

	public ConnectionPropertiesPanel(SQLAliasConnectionProperties props)
	{
		Utilities.checkNull("ConnectionPropertiesPanel.init", "props", props);

		this._props = props;

		createUserInterface();
		
		setSQLAliasConnectionProperties(props);
	}

	/**
	 * Retrieve the database properties.
	 * 
	 * @return the database properties.
	 */
	public SQLAliasConnectionProperties getSQLAliasConnectionProperties()
	{
		if (enableConnectionKeepAliveChk.isSelected())
		{
			_props.setEnableConnectionKeepAlive(true);
			int sleepTimeInt = (Integer)sleepTime.getValue();
			_props.setKeepAliveSleepTimeSeconds(sleepTimeInt);
			_props.setKeepAliveSqlStatement(sqlTextArea.getText());
		}
		else
		{
			_props.setEnableConnectionKeepAlive(false);
		}
		return _props;
	}

	public void setSQLAliasConnectionProperties(SQLAliasConnectionProperties props) {
		this._props = props;
		enableConnectionKeepAliveChk.setSelected(_props.isEnableConnectionKeepAlive());
		sleepForLabel.setEnabled(_props.isEnableConnectionKeepAlive());
		sleepTime.setEnabled(_props.isEnableConnectionKeepAlive());
		sleepTime.setValue(props.getKeepAliveSleepTimeSeconds());
		secondsLabel.setEnabled(_props.isEnableConnectionKeepAlive());
		executeLabel.setEnabled(_props.isEnableConnectionKeepAlive());
		sqlTextArea.setEnabled(_props.isEnableConnectionKeepAlive());
		sqlTextArea.setText(props.getKeepAliveSqlStatement());
	
	}
	
	private void createUserInterface()
	{
		setLayout(new GridBagLayout());

		final GridBagConstraints gbc = new GridBagConstraints();

		setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = .75;
		gbc.weighty = .3;

		addInfoPanel(gbc);

		prepareNewRow(gbc);

		addMiddle(gbc);

		prepareNewRow(gbc);

		addExecuteLabel(gbc);

		prepareNewRow(gbc);

		addSqlTextAreaPanel(gbc);
		
		
	}

	private void addExecuteLabel(GridBagConstraints gbc)
	{
		executeLabel = new JLabel(i18n.EXECUTE_LABEL);
		// We want the text to line up with the text of the enable checkbox to give a hint that the
		// the checkbox controls enabling this component.
		Insets orig = gbc.insets;
		gbc.insets = new Insets(0, 25, 0, 0);
		add(executeLabel, gbc);
		gbc.insets = orig;
	}

	private void addInfoPanel(final GridBagConstraints gbc)
	{
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.BOTH;

		MultipleLineLabel label = new MultipleLineLabel(i18n.INSTRUCTIONS);

		// Instructions
		final JPanel pnl = new JPanel();
		pnl.add(new MultipleLineLabel(i18n.INSTRUCTIONS));

		add(label, gbc);

	}

	private void addMiddle(final GridBagConstraints gbc)
	{
		JPanel middlePanel = new JPanel();
		middlePanel.setLayout(new GridLayout(2, 1));
		
		setupEnabledCheckbox();
		middlePanel.add(enableConnectionKeepAliveChk);
		middlePanel.add(getSleepTimePanel());

		gbc.weightx = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;

		add(middlePanel, gbc);
	}

	private JPanel getSleepTimePanel()
	{
		JPanel sleepTimePanel = new JPanel();
		sleepTimePanel.setLayout(new GridBagLayout());
		
		SpinnerNumberModel spinnerNumberModel = 
			new SpinnerNumberModel(_props.getKeepAliveSleepTimeSeconds(), 10, Integer.MAX_VALUE, 10);
		sleepTime = new JSpinner(spinnerNumberModel);
		sleepTime.setPreferredSize(new Dimension(75, 25));

		sleepForLabel = new JLabel(i18n.SLEEP_FOR_LABEL);
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.EAST;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = .05;
		gbc.insets = new Insets(0, 5, 0, 0);
		sleepTimePanel.add(sleepForLabel, gbc);

		gbc.anchor = GridBagConstraints.CENTER;
		gbc.gridx++;
		gbc.weightx = .05;
		sleepTimePanel.add(sleepTime, gbc);

		secondsLabel = new JLabel(i18n.SECONDS_LABEL);
		
		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridx++;
		gbc.weightx = .9;
		sleepTimePanel.add(secondsLabel, gbc);

		return sleepTimePanel;
	}

	private void addSqlTextAreaPanel(final GridBagConstraints gbc)
	{
		gbc.weighty = .7;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(10, 25, 10, 10);
		JPanel textPanel = new JPanel();
		textPanel.setLayout(new BorderLayout());
		textPanel.add(sqlTextArea, BorderLayout.CENTER);
		add(textPanel, gbc);
	}

	private void prepareNewRow(final GridBagConstraints gbc)
	{
		gbc.gridx = 0;
		++gbc.gridy;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridwidth = 1;
		gbc.weighty = 0;
	}
	
	private void setupEnabledCheckbox()
	{		
		enableConnectionKeepAliveChk = new JCheckBox(i18n.ENABLE_KEEP_ALIVE_MSG);
		enableConnectionKeepAliveChk.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				sleepForLabel.setEnabled(enableConnectionKeepAliveChk.isSelected());
				sleepTime.setEnabled(enableConnectionKeepAliveChk.isSelected());	
				secondsLabel.setEnabled(enableConnectionKeepAliveChk.isSelected());
				executeLabel.setEnabled(enableConnectionKeepAliveChk.isSelected());
				sqlTextArea.setEnabled(enableConnectionKeepAliveChk.isSelected());
			}
		});
	}

}
