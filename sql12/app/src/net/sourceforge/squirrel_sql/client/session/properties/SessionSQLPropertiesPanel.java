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
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.preferences.INewSessionPropertiesPanel;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.FontChooser;
import net.sourceforge.squirrel_sql.fw.gui.FontInfo;
import net.sourceforge.squirrel_sql.fw.gui.IntegerField;
/**
 * This panel allows the user to tailor SQL settings for a session.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
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
			String ABORT_ON_ERROR = "Abort On Error";
			String AUTO_COMMIT = "Auto Commit SQL";
			String COMMIT_ON_CLOSE = "Commit On Closing Session";
			String LIMIT_ROWS_SQL = "SQL results - Limit rows";
			String SHARE_SQL_HISTORY = "Share SQL History";
			String SOL_COMENT = "Start of Line Comment";
			String STATEMENT_SEPARATOR = "Statement Separator:";
			String SQL = "SQL";
			String SQL_HISTORY = "SQL History";
			String LIMIT_SQL_HISTORY_COMBO_SIZE = "Limit SQL History Combo Size";
		}

		private JCheckBox _abortOnErrorChk = new JCheckBox(i18n.ABORT_ON_ERROR);
		private JCheckBox _autoCommitChk = new JCheckBox(i18n.AUTO_COMMIT);
		private JCheckBox _commitOnClose = new JCheckBox(i18n.COMMIT_ON_CLOSE);
		private IntegerField _sqlNbrRowsToShowField = new IntegerField(5);
		private JCheckBox _sqlLimitRowsChk = new JCheckBox(i18n.LIMIT_ROWS_SQL);
		private JTextField _stmtSepField = new JTextField(5);
		private JTextField _solCommentField = new JTextField(2);

		/** Label displaying the selected font. */
		private JLabel _fontLbl = new JLabel();

		/** Button to select font. */
		private FontButton _fontBtn = new FontButton("Font", _fontLbl);

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
			_abortOnErrorChk.setSelected(props.getAbortOnError());
			_autoCommitChk.setSelected(props.getAutoCommit());
			_commitOnClose.setSelected(props.getCommitOnClosingConnection());
			_sqlNbrRowsToShowField.setInt(props.getSQLNbrRowsToShow());
			_sqlLimitRowsChk.setSelected(props.getSQLLimitRows());
			_stmtSepField.setText(props.getSQLStatementSeparator());
			_solCommentField.setText(props.getStartOfLineComment());

			_shareSQLHistoryChk.setSelected(props.getSQLShareHistory());
			_limitSQLHistoryComboSizeChk.setSelected(props.getLimitSQLEntryHistorySize());
			_limitSQLHistoryComboSizeField.setInt(props.getSQLEntryHistorySize());

			FontInfo fi = props.getFontInfo();
			if (fi == null)
			{
				fi = new FontInfo(UIManager.getFont("TextArea.font"));
			}
			_fontLbl.setText(fi.toString());
			_fontBtn.setSelectedFont(fi.createFont());

			updateControlStatus();
		}

		void applyChanges(SessionProperties props)
		{
			props.setAbortOnError(_abortOnErrorChk.isSelected());
			props.setAutoCommit(_autoCommitChk.isSelected());
			props.setCommitOnClosingConnection(_commitOnClose.isSelected());
			props.setSQLNbrRowsToShow(_sqlNbrRowsToShowField.getInt());
			props.setSQLLimitRows(_sqlLimitRowsChk.isSelected());
			props.setSQLStatementSeparator(_stmtSepField.getText());
			props.setStartOfLineComment(_solCommentField.getText());

			props.setFontInfo(_fontBtn.getFontInfo());

			props.setSQLShareHistory(_shareSQLHistoryChk.isSelected());
			props.setLimitSQLEntryHistorySize(_limitSQLHistoryComboSizeChk.isSelected());
			props.setSQLEntryHistorySize(_limitSQLHistoryComboSizeField.getInt());
		}

		private void updateControlStatus()
		{
			_commitOnClose.setEnabled(!_autoCommitChk.isSelected());

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
			add(createFontPanel(), gbc);

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
			_sqlLimitRowsChk.addChangeListener(_controlMediator);

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
			gbc.gridwidth = GridBagConstraints.REMAINDER;
			pnl.add(_abortOnErrorChk, gbc);

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
		private JPanel createFontPanel()
		{
			JPanel pnl = new JPanel();
			pnl.setBorder(BorderFactory.createTitledBorder("SQL Entry Area"));
			pnl.setLayout(new GridBagLayout());
			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(4, 4, 4, 4);

			_fontBtn.addActionListener(new FontButtonListener());

			gbc.gridx = 0;
			gbc.gridy = 0;
			pnl.add(_fontBtn, gbc);

			++gbc.gridx;
			gbc.weightx = 1.0;
			pnl.add(_fontLbl, gbc);

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

		private static final class FontButton extends JButton
		{
			private FontInfo _fi;
			private JLabel _lbl;
			private Font _font;

			FontButton(String text, JLabel lbl)
			{
				super(text);
				_lbl = lbl;
			}

			FontInfo getFontInfo()
			{
				return _fi;
			}

			Font getSelectedFont()
			{
				return _font;
			}

			void setSelectedFont(Font font)
			{
				_font = font;
				if (_fi == null)
				{
					_fi = new FontInfo(font);
				}
				else
				{
					_fi.setFont(font);
				}
			}
		}

		private static final class FontButtonListener implements ActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				if (evt.getSource() instanceof FontButton)
				{
					FontButton btn = (FontButton) evt.getSource();
					FontInfo fi = btn.getFontInfo();
					Font font = null;
					if (fi != null)
					{
						font = fi.createFont();
					}
					font = new FontChooser().showDialog(font);
					if (font != null)
					{
						btn.setSelectedFont(font);
						btn._lbl.setText(new FontInfo(font).toString());
					}
				}
			}
		}

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
