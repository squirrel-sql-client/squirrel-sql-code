package net.sourceforge.squirrel_sql.client.session.properties;
/*
 * Copyright (C) 2001-2003 Colin Bell
 * colbell@users.sourceforge.net
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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.squirrel_sql.fw.gui.IntegerField;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.preferences.INewSessionPropertiesPanel;
import net.sourceforge.squirrel_sql.client.session.ISession;
/**
 * This panel allows the user to tailor SQL settings for a session.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SessionSQLPropertiesPanel
	implements INewSessionPropertiesPanel, ISessionPropertiesPanel
{
	/** Application API. */
	private final IApplication _app;

	/** The actual GUI panel that allows user to do the maintenance. */
	private final SQLPropertiesPanel _myPanel;

	/** Session properties object being maintained. */
	private SessionProperties _props;

	/**
	 * ctor specifying the Application API.
	 *
	 * @param	app		Application API.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if <tt>null</tt> <tt>IApplication</tt>
	 * 			passed.
	 */
	public SessionSQLPropertiesPanel(IApplication app) throws IllegalArgumentException
	{
		super();
		if (app == null)
		{
			throw new IllegalArgumentException("Null IApplication passed");
		}
		_app = app;
		_myPanel = new SQLPropertiesPanel(app);
	}


	public void initialize(IApplication app)
	{
		_props = _app.getSquirrelPreferences().getSessionProperties();
		_myPanel.loadData(_props);
	}

	public void initialize(IApplication app, ISession session)
		throws IllegalArgumentException
	{
		if (session == null)
		{
			throw new IllegalArgumentException("Null ISession passed");
		}
		_props = session.getProperties();
		_myPanel.loadData(_props);
	}

	public Component getPanelComponent()
	{
		return _myPanel;
	}

	public String getTitle()
	{
		return SQLPropertiesPanel.i18n.SQL;
	}

	public String getHint()
	{
		return SQLPropertiesPanel.i18n.SQL;
	}

	public void applyChanges()
	{
		_myPanel.applyChanges(_props);
	}

	private static final class SQLPropertiesPanel extends JPanel
	{
		/**
		 * This interface defines locale specific strings. This should be
		 * replaced with a property file.
		 */
		interface i18n
		{
			String AUTO_COMMIT = "Auto Commit SQL";
			String COMMIT_ON_CLOSE = "Commit On Closing Session";
			String LIMIT_ROWS_CONTENTS = "Contents - Limit rows";
			String LIMIT_ROWS_SQL = "SQL results - Limit rows";
			String SHARE_SQL_HISTORY = "Share SQL History";
			String SHOW_ROW_COUNT = "Show Row Count for Tables (can slow application)";
			String SOL_COMENT = "Start of Line Comment";
			String TABLE = "Table";
			String TEXT = "Text";
			String STATEMENT_SEPARATOR = "Statement Separator:";
			String SQL = "SQL";
			String SQL_HISTORY = "SQL History";
			String LIMIT_SQL_HISTORY_COMBO_SIZE = "Limit SQL History Combo Size";
		}

		private JCheckBox _autoCommitChk = new JCheckBox(i18n.AUTO_COMMIT);
		private JCheckBox _commitOnClose = new JCheckBox(i18n.COMMIT_ON_CLOSE);
		private IntegerField _contentsNbrRowsToShowField = new IntegerField(5);
		private JCheckBox _contentsLimitRowsChk = new JCheckBox(i18n.LIMIT_ROWS_CONTENTS);
		private JCheckBox _showRowCountChk = new JCheckBox(i18n.SHOW_ROW_COUNT);
		private IntegerField _sqlNbrRowsToShowField = new IntegerField(5);
		private JCheckBox _sqlLimitRowsChk = new JCheckBox(i18n.LIMIT_ROWS_SQL);
		private JTextField _stmtSepField = new JTextField(5);
		private JTextField _solCommentField = new JTextField(2);

		private JCheckBox _shareSQLHistoryChk = new JCheckBox(i18n.SHARE_SQL_HISTORY);
		private JCheckBox _limitSQLHistoryComboSizeChk = new JCheckBox(i18n.LIMIT_SQL_HISTORY_COMBO_SIZE);
		private IntegerField _limitSQLHistoryComboSizeField = new IntegerField(5);

		/**
		 * This object will update the status of the GUI controls as the user
		 * makes changes.
		 */
		private final ControlMediator _controlMediator = new ControlMediator();

		SQLPropertiesPanel(IApplication app)
		{
			super();
			createGUI();
		}

		void loadData(SessionProperties props)
		{
			_autoCommitChk.setSelected(props.getAutoCommit());
			_commitOnClose.setSelected(props.getCommitOnClosingConnection());
			_contentsNbrRowsToShowField.setInt(props.getContentsNbrRowsToShow());
			_contentsLimitRowsChk.setSelected(props.getContentsLimitRows());
			_sqlNbrRowsToShowField.setInt(props.getSQLNbrRowsToShow());
			_sqlLimitRowsChk.setSelected(props.getSQLLimitRows());
			_showRowCountChk.setSelected(props.getShowRowCount());
			_stmtSepField.setText(props.getSQLStatementSeparator());
			_solCommentField.setText(props.getStartOfLineComment());

			_shareSQLHistoryChk.setSelected(props.getSQLShareHistory());
			_limitSQLHistoryComboSizeChk.setSelected(props.getLimitSQLEntryHistorySize());
			_limitSQLHistoryComboSizeField.setInt(props.getSQLEntryHistorySize());

			updateControlStatus();
		}

		void applyChanges(SessionProperties props)
		{
			props.setAutoCommit(_autoCommitChk.isSelected());
			props.setCommitOnClosingConnection(_commitOnClose.isSelected());
			props.setContentsNbrRowsToShow(_contentsNbrRowsToShowField.getInt());
			props.setContentsLimitRows(_contentsLimitRowsChk.isSelected());
			props.setSQLNbrRowsToShow(_sqlNbrRowsToShowField.getInt());
			props.setSQLLimitRows(_sqlLimitRowsChk.isSelected());
			props.setShowRowCount(_showRowCountChk.isSelected());
			props.setSQLStatementSeparator(_stmtSepField.getText());
			props.setStartOfLineComment(_solCommentField.getText());

			props.setSQLShareHistory(_shareSQLHistoryChk.isSelected());
			props.setLimitSQLEntryHistorySize(_limitSQLHistoryComboSizeChk.isSelected());
			props.setSQLEntryHistorySize(_limitSQLHistoryComboSizeField.getInt());
		}

		private void updateControlStatus()
		{
			_commitOnClose.setEnabled(!_autoCommitChk.isSelected());

			_contentsNbrRowsToShowField.setEnabled(_contentsLimitRowsChk.isSelected());
			_sqlNbrRowsToShowField.setEnabled(_sqlLimitRowsChk.isSelected());

			// If this session doesn't share SQL history with other sessions
			// then disable the controls that relate to SQL History.
			final boolean shareSQLHistory = _shareSQLHistoryChk.isSelected();
			_limitSQLHistoryComboSizeChk.setEnabled(!shareSQLHistory);
			_limitSQLHistoryComboSizeField.setEnabled(!shareSQLHistory &&
								_limitSQLHistoryComboSizeChk.isSelected());	
		}

		private void createGUI()
		{
			setLayout(new GridBagLayout());
			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.anchor = GridBagConstraints.WEST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(4, 4, 4, 4);

			gbc.gridx = 0;
			gbc.gridy = 0;
			add(createSQLPanel(), gbc);

			++gbc.gridy;
			add(createSQLHistoryPanel(), gbc);
		}

		private JPanel createSQLPanel()
		{
			final JPanel pnl = new JPanel(new GridBagLayout());
			pnl.setBorder(BorderFactory.createTitledBorder("SQL"));
			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(4, 4, 4, 4);
			gbc.anchor = GridBagConstraints.CENTER;

			_autoCommitChk.addChangeListener(_controlMediator);
			_contentsLimitRowsChk.addChangeListener(_controlMediator);
			_sqlLimitRowsChk.addChangeListener(_controlMediator);

			_contentsNbrRowsToShowField.setColumns(5);
			_sqlNbrRowsToShowField.setColumns(5);
			_stmtSepField.setColumns(5);

			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.gridwidth = 2;
			pnl.add(_autoCommitChk, gbc);

			gbc.gridx+=2;
			gbc.gridwidth = GridBagConstraints.REMAINDER;
			pnl.add(_commitOnClose, gbc);

			++gbc.gridy; // new line
			gbc.gridx = 0;
			gbc.gridwidth = GridBagConstraints.REMAINDER;
			pnl.add(_showRowCountChk, gbc);

			++gbc.gridy; // new line
			gbc.gridx = 0;
			gbc.gridwidth = 2;
			pnl.add(_contentsLimitRowsChk, gbc);
			gbc.gridwidth = 1;
			gbc.gridx+=2;
			pnl.add(_contentsNbrRowsToShowField, gbc);
			++gbc.gridx;
			gbc.gridwidth = GridBagConstraints.REMAINDER;
			pnl.add(new JLabel("rows"), gbc);

			++gbc.gridy; // new line
			gbc.gridx = 0;
			gbc.gridwidth = 2;
			pnl.add(_sqlLimitRowsChk, gbc);
			gbc.gridwidth = 1;
			gbc.gridx+=2;
			pnl.add(_sqlNbrRowsToShowField, gbc);
			++gbc.gridx;
			gbc.gridwidth = GridBagConstraints.REMAINDER;
			pnl.add(new JLabel("rows"), gbc);

			++gbc.gridy; // new line
			gbc.gridx = 0;
			gbc.gridwidth = 1;
			pnl.add(new JLabel(i18n.STATEMENT_SEPARATOR), gbc);
			++gbc.gridx;
			pnl.add(_stmtSepField, gbc);
			++gbc.gridx;
			pnl.add(new RightLabel(i18n.SOL_COMENT), gbc);
			++gbc.gridx;
			pnl.add(_solCommentField, gbc);

			return pnl;
		}

		private JPanel createSQLHistoryPanel()
		{
			_shareSQLHistoryChk.addChangeListener(_controlMediator);
			_limitSQLHistoryComboSizeChk.addChangeListener(_controlMediator);

			JPanel pnl = new JPanel(new GridBagLayout());
			pnl.setBorder(BorderFactory.createTitledBorder(i18n.SQL_HISTORY));
			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(4, 4, 4, 4);
			gbc.anchor = GridBagConstraints.WEST;

			gbc.gridx = 0;
			gbc.gridy = 0;
			pnl.add(_shareSQLHistoryChk, gbc);

			++gbc.gridy;
			pnl.add(_limitSQLHistoryComboSizeChk, gbc);
			
			++gbc.gridx;
			pnl.add(_limitSQLHistoryComboSizeField, gbc);

			return pnl;
		}

		private static final class RightLabel extends JLabel
		{
			RightLabel(String title)
			{
				super(title, SwingConstants.RIGHT);
			}
		}

//		private static final class ReadTypeCombo extends JComboBox
//		{
//			static final int READ_ALL_IDX = 1;
//			static final int READ_PARTIAL_IDX = 0;
//			
//			ReadTypeCombo()
//			{
//				addItem("only the first");
//				addItem("all");
//			}
//		}

		/**
		 * This class will update the status of the GUI controls as the user
		 * makes changes.
		 */
		private final class ControlMediator implements ChangeListener,
															ActionListener
		{
			public void stateChanged(ChangeEvent evt)
			{
				updateControlStatus();
			}

			public void actionPerformed(ActionEvent evt)
			{
				updateControlStatus();
			}
		}
	}
}
