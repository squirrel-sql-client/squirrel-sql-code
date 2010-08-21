/*
 * Copyright (C) 2007 Rob Manning
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
package net.sourceforge.squirrel_sql.client.plugin.gui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import net.sourceforge.squirrel_sql.client.plugin.PluginQueryTokenizerPreferencesManager;
import net.sourceforge.squirrel_sql.fw.preferences.IQueryTokenizerPreferenceBean;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

/**
 * This can be used (or subclassed) to provide a plugin-specific preference panel geared toward storing custom
 * QueryTokenizer preferences. A plugin can instantiate this class giving it an IQueryTokenizerPreferenceBean
 * with which to load and store preference information. The databaseName is used to tailor some of the
 * tooltips for a specific database.
 * 
 * @author manningr
 */
public class PluginQueryTokenizerPreferencesPanel extends JPanel
{

	protected static final int LEFT_INDENT_INSET_SIZE = 35;

	private static final long serialVersionUID = 1L;

	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(PluginQueryTokenizerPreferencesPanel.class);

	static interface i18n
	{

		// i18n[PreferencesPanel.useCustomQTLabel=Use Custom Query Tokenizer]
		String USE_CUSTOM_QT_LABEL = s_stringMgr.getString("PreferencesPanel.useCustomQTLabel");

		// i18n[PreferencesPanel.removeMultiLineCommentLabel=Remove multi-line
		// comments (/*...*/) from SQL before executing
		String REMOVE_ML_COMMENT_LABEL = s_stringMgr.getString("PreferencesPanel.removeMultiLineCommentLabel");

		// i18n[PreferencesPanel.removeMultiLineCommentLabelTipText=]
		String REMOVE_ML_COMMENT_LABEL_TT =
			s_stringMgr.getString("PreferencesPanel.removeMultiLineCommentLabelTipText");

		// i18n[PreferencesPanel.statementSeparatorLabel=Statement Separator]
		String STMT_SEP_LABEL = s_stringMgr.getString("PreferencesPanel.statementSeparatorLabel");

		// i18n[PreferencesPanel.statementSeparatorToolTip=When multiple
		// statements are selected, use this to separate them into single
		// statements.]
		String STMT_SEP_LABEL_TT = s_stringMgr.getString("PreferencesPanel.statementSeparatorToolTip");

		// i18n[PreferencesPanel.lineCommentLabel=Start of line comment]
		String LINE_COMMENT_LABEL = s_stringMgr.getString("PreferencesPanel.lineCommentLabel");

		// i18n[PreferencesPanel.lineCommentToolTip=Sequence of characters at
		// the beginning of a line to indicate that whole line is a comment]
		String LINE_COMMENT_LABEL_TT = s_stringMgr.getString("PreferencesPanel.lineCommentToolTip");

		// i18n[PreferencesPanel.procedureSeparatorLabel=Procedure/Function
		// Separator]
		String PROC_SEP_LABEL = s_stringMgr.getString("PreferencesPanel.procedureSeparatorLabel");

		// i18n[PreferencesPanel.procedureSeparatorToolTip=Multiple statements
		// within a procedure or function can be terminated with this.]
		String PROC_SEP_LABEL_TT = s_stringMgr.getString("PreferencesPanel.procedureSeparatorToolTip");

	}

	protected PluginQueryTokenizerPreferencesManager _prefsManager = null;

	protected JCheckBox useCustomQTCheckBox = null;

	protected JLabel useCustomQTLabel = null;

	protected JCheckBox removeMultiLineCommentCheckBox = null;

	protected JTextField lineCommentTextField = null;

	protected JLabel lineCommentLabel = null;

	protected JLabel procedureSeparatorLabel = null;

	protected JTextField procedureSeparatorTextField = null;

	protected JLabel statementSeparatorLabel = null;

	protected JTextField statementSeparatorTextField = null;

	/** the name of the database we are storing preferences for. */
	protected String _databaseName = null;

	protected boolean _showProcSep = true;

	/** the last row of widgets that was added to the grid */
	protected int lastY = 0;

	/**
	 * Construct a new PreferencesPanel.
	 * 
	 * @param databaseName
	 */
	public PluginQueryTokenizerPreferencesPanel(PluginQueryTokenizerPreferencesManager prefsMgr,
		String databaseName)
	{
		this(prefsMgr, databaseName, true);
	}

	/**
	 * Construct a new PreferencesPanel.
	 * 
	 * @param prefsMgr
	 *           the query tokenizer preferences manager.
	 * @param databaseName
	 *           the name of the database to use as the panel title.
	 * @param showProcedureSeparator
	 *           whether or not the custom tokenizer needs procedure separator preference.
	 */
	public PluginQueryTokenizerPreferencesPanel(PluginQueryTokenizerPreferencesManager prefsMgr,
		String databaseName, boolean showProcedureSeparator)
	{
		super();
		_prefsManager = prefsMgr;
		_databaseName = databaseName;
		_showProcSep = showProcedureSeparator;
		createGUI();
		loadData();
	}

	private void createGUI()
	{
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0; // Column 0
		c.gridy = 0; // Row 0
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = .40;
		add(createTopPanel(), c);
	}

	protected JPanel createTopPanel()
	{
		JPanel result = new JPanel(new GridBagLayout());
		// i18n[PreferencesPanel.borderLabel={0} Script Settings]
		String borderLabel = s_stringMgr.getString("PreferencesPanel.borderLabel", _databaseName);
		result.setBorder(getTitledBorder(borderLabel));

		addUseCustomQTCheckBox(result, 0, lastY++);

		addLineCommentLabel(result, 0, lastY);
		addLineCommentTextField(result, 1, lastY++);

		addStatementSeparatorLabel(result, 0, lastY);
		addStatementSeparatorTextField(result, 1, lastY++);

		if (_showProcSep)
		{
			addProcedureSeparatorLabel(result, 0, lastY);
			addProcedureSeparatorTextField(result, 1, lastY++);
		}

		addRemoveMultiLineCommentCheckBox(result, 0, lastY++);

		return result;
	}

	private void addUseCustomQTCheckBox(JPanel panel, int col, int row)
	{
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = col;
		c.gridy = row;
		c.insets = new Insets(5, 5, 0, 0);
		c.anchor = GridBagConstraints.WEST;
		c.gridwidth = 2; // Span across two columns
		useCustomQTCheckBox = new JCheckBox(i18n.USE_CUSTOM_QT_LABEL);
		useCustomQTCheckBox.setName("useCustomQTCheckBox");

		// i18n[PreferencesPanel.useCustomQTToolTip=Gives enhanced capabilities
		// over the default query tokenizer for handling {0} scripts]
		String USE_CUSTOM_QT_TOOLTIP =
			s_stringMgr.getString("PreferencesPanel.useCustomQTToolTip", _databaseName);

		useCustomQTCheckBox.setToolTipText(USE_CUSTOM_QT_TOOLTIP);
		useCustomQTCheckBox.addActionListener(new UseQTHandler());
		panel.add(useCustomQTCheckBox, c);
	}

	private void addRemoveMultiLineCommentCheckBox(JPanel panel, int col, int row)
	{
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = col;
		c.gridy = row;
		c.gridwidth = 2; // Span across two columns
		c.insets = new Insets(5, 30, 0, 0);
		String cbLabel = i18n.REMOVE_ML_COMMENT_LABEL;
		removeMultiLineCommentCheckBox = new JCheckBox(cbLabel);
		removeMultiLineCommentCheckBox.setName("removeMultiLineCommentCheckBox");
		removeMultiLineCommentCheckBox.setToolTipText(i18n.REMOVE_ML_COMMENT_LABEL_TT);
		panel.add(removeMultiLineCommentCheckBox, c);
	}

	private void addStatementSeparatorLabel(JPanel panel, int col, int row)
	{
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = col;
		c.gridy = row;
		c.insets = new Insets(5, LEFT_INDENT_INSET_SIZE, 0, 0);
		c.anchor = GridBagConstraints.WEST;
		statementSeparatorLabel = new JLabel(i18n.STMT_SEP_LABEL);
		statementSeparatorLabel.setHorizontalAlignment(JLabel.LEFT);
		statementSeparatorLabel.setToolTipText(i18n.STMT_SEP_LABEL_TT);
		panel.add(statementSeparatorLabel, c);
	}

	private void addStatementSeparatorTextField(JPanel panel, int col, int row)
	{
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = col;
		c.gridy = row;
		c.ipadx = 40; // Increases component width by 40 pixels
		c.insets = new Insets(5, 5, 0, 0);
		c.anchor = GridBagConstraints.WEST;
		statementSeparatorTextField = new JTextField(10);
		statementSeparatorTextField.setName("statementSeparatorTextField");

		statementSeparatorTextField.setHorizontalAlignment(JTextField.RIGHT);
		statementSeparatorTextField.setToolTipText(i18n.STMT_SEP_LABEL_TT);
		panel.add(statementSeparatorTextField, c);
	}

	private void addLineCommentLabel(JPanel panel, int col, int row)
	{
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = col;
		c.gridy = row;
		c.insets = new Insets(5, LEFT_INDENT_INSET_SIZE, 0, 0);
		c.anchor = GridBagConstraints.WEST;
		lineCommentLabel = new JLabel(i18n.LINE_COMMENT_LABEL);
		lineCommentLabel.setHorizontalAlignment(JLabel.LEFT);
		lineCommentLabel.setToolTipText(i18n.LINE_COMMENT_LABEL_TT);
		panel.add(lineCommentLabel, c);
	}

	private void addLineCommentTextField(JPanel panel, int col, int row)
	{
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = col;
		c.gridy = row;
		c.ipadx = 40; // Increases component width by 40 pixels
		c.insets = new Insets(5, 5, 0, 0);
		c.anchor = GridBagConstraints.WEST;
		lineCommentTextField = new JTextField(10);
		lineCommentTextField.setName("lineCommentTextField");
		lineCommentTextField.setHorizontalAlignment(JTextField.RIGHT);
		lineCommentTextField.setToolTipText(i18n.LINE_COMMENT_LABEL_TT);
		panel.add(lineCommentTextField, c);
	}

	private void addProcedureSeparatorLabel(JPanel panel, int col, int row)
	{
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = col;
		c.gridy = row;
		c.insets = new Insets(5, LEFT_INDENT_INSET_SIZE, 0, 0);
		procedureSeparatorLabel = new JLabel(i18n.PROC_SEP_LABEL);
		procedureSeparatorLabel.setHorizontalAlignment(JLabel.RIGHT);
		procedureSeparatorLabel.setToolTipText(i18n.PROC_SEP_LABEL_TT);
		panel.add(procedureSeparatorLabel, c);
	}

	private void addProcedureSeparatorTextField(JPanel panel, int col, int row)
	{
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = col;
		c.gridy = row;
		c.ipadx = 40; // Increases component width by 20 pixels
		c.insets = new Insets(5, 5, 0, 0);
		c.anchor = GridBagConstraints.WEST;
		procedureSeparatorTextField = new JTextField(10);
		procedureSeparatorTextField.setHorizontalAlignment(JTextField.RIGHT);
		procedureSeparatorTextField.setToolTipText(i18n.PROC_SEP_LABEL_TT);
		panel.add(procedureSeparatorTextField, c);
	}

	private Border getTitledBorder(String title)
	{
		CompoundBorder border = new CompoundBorder(new EmptyBorder(10, 10, 10, 10), new TitledBorder(title));
		return border;
	}

	protected void loadData()
	{
		IQueryTokenizerPreferenceBean _prefs = _prefsManager.getPreferences();
		removeMultiLineCommentCheckBox.setSelected(_prefs.isRemoveMultiLineComments());
		lineCommentTextField.setText(_prefs.getLineComment());
		statementSeparatorTextField.setText(_prefs.getStatementSeparator());
		if (_showProcSep)
		{
			procedureSeparatorTextField.setText(_prefs.getProcedureSeparator());
		}
		useCustomQTCheckBox.setSelected(_prefs.isInstallCustomQueryTokenizer());
		updatePreferenceState();
	}

	protected void save()
	{
		IQueryTokenizerPreferenceBean _prefs = _prefsManager.getPreferences();
		_prefs.setRemoveMultiLineComments(removeMultiLineCommentCheckBox.isSelected());
		_prefs.setLineComment(lineCommentTextField.getText());
		_prefs.setStatementSeparator(statementSeparatorTextField.getText());
		if (_showProcSep)
		{
			_prefs.setProcedureSeparator(procedureSeparatorTextField.getText());
		}
		_prefs.setInstallCustomQueryTokenizer(useCustomQTCheckBox.isSelected());
		_prefsManager.savePrefs();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.squirrel_sql.client.util.IOptionPanel#applyChanges()
	 */
	public void applyChanges()
	{
		save();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.squirrel_sql.client.util.IOptionPanel#getPanelComponent()
	 */
	public Component getPanelComponent()
	{
		return this;
	}

	private void updatePreferenceState()
	{
		if (useCustomQTCheckBox.isSelected())
		{
			removeMultiLineCommentCheckBox.setEnabled(true);
			lineCommentTextField.setEnabled(true);
			lineCommentLabel.setEnabled(true);
			statementSeparatorTextField.setEnabled(true);
			statementSeparatorLabel.setEnabled(true);
			if (_showProcSep)
			{
				procedureSeparatorLabel.setEnabled(true);
				procedureSeparatorTextField.setEnabled(true);
			}
		}
		else
		{
			removeMultiLineCommentCheckBox.setEnabled(false);
			lineCommentTextField.setEnabled(false);
			lineCommentLabel.setEnabled(false);
			statementSeparatorTextField.setEnabled(false);
			statementSeparatorLabel.setEnabled(false);
			if (_showProcSep)
			{
				procedureSeparatorLabel.setEnabled(false);
				procedureSeparatorTextField.setEnabled(false);
			}
		}
	}

	private class UseQTHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			updatePreferenceState();
		}
	}

}
