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
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.squirrel_sql.fw.gui.FontChooser;
import net.sourceforge.squirrel_sql.fw.gui.FontInfo;
import net.sourceforge.squirrel_sql.fw.gui.IntegerField;
import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.sql.TokenizerSessPropsInteractions;
import net.sourceforge.squirrel_sql.fw.sql.IQueryTokenizer;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.preferences.INewSessionPropertiesPanel;
import net.sourceforge.squirrel_sql.client.session.ISession;
/**
 * This panel allows the user to tailor SQL settings for a session.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SessionSQLPropertiesPanel
	implements INewSessionPropertiesPanel, ISessionPropertiesPanel
{
	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(SessionSQLPropertiesPanel.class);

	/** Application API. */
	private final IApplication _app;

	/** The actual GUI panel that allows user to do the maintenance. */
	private final SQLPropertiesPanel _myPanel;

   private final JScrollPane _scrolledMyPanel;

	/** Session properties object being maintained. */
	private SessionProperties _props;

   /**
	 * ctor specifying the Application API.
	 *
	 * @param	app		Application API.
	 *
	 * @param session
    * @throws	IllegalArgumentException
	 * 			Thrown if <tt>null</tt> <tt>IApplication</tt>
	 * 			passed.
	 */
	public SessionSQLPropertiesPanel(IApplication app, ISession session) throws IllegalArgumentException
	{
		super();
      if (app == null)
		{
			throw new IllegalArgumentException("Null IApplication passed");
		}
		_app = app;
		_myPanel = new SQLPropertiesPanel(app, session);
      _scrolledMyPanel = new JScrollPane(_myPanel);
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
		return _scrolledMyPanel;
	}

	public String getTitle()
	{
		return s_stringMgr.getString("SessionSQLPropertiesPanel.sql");
	}

	public String getHint()
	{
		return getTitle();
	}

	public void applyChanges()
	{
		_myPanel.applyChanges(_props);
	}

	private static final class SQLPropertiesPanel extends JPanel
	{
		private JCheckBox _abortOnErrorChk = new JCheckBox(s_stringMgr.getString("SessionSQLPropertiesPanel.abortonerror"));
		private JCheckBox _showSQLErrorsInTabChk = new JCheckBox(s_stringMgr.getString("SessionSQLPropertiesPanel.showSQLErrorsInTab"));
		private JCheckBox _writeSQLErrorsToLogChk = new JCheckBox(s_stringMgr.getString("SessionSQLPropertiesPanel.writesqlerrorstolog"));
		private JCheckBox _loadColumsInBackgroundChk = new JCheckBox(s_stringMgr.getString("SessionSQLPropertiesPanel.loadColumsInBackground"));
		private JCheckBox _autoCommitChk = new JCheckBox(s_stringMgr.getString("SessionSQLPropertiesPanel.autocommit"));
		private JCheckBox _commitOnClose = new JCheckBox(s_stringMgr.getString("SessionSQLPropertiesPanel.commitonclose"));
		private IntegerField _sqlNbrRowsToShowField = new IntegerField(5);
		private JCheckBox _sqlLimitRowsChk = new JCheckBox(s_stringMgr.getString("SessionSQLPropertiesPanel.limitrows"));
		private JCheckBox _sqlFetchSizeChk = new JCheckBox(s_stringMgr.getString("SessionSQLPropertiesPanel.fetchSize"));
		private IntegerField _sqlFetchSizeField = new IntegerField(5);
		private JTextField _stmtSepField = new JTextField(5);
		private JTextField _solCommentField = new JTextField(2);
      // i18n[SessionSQLPropertiesPanel.removeMultiLineComment=Remove multi line comment (/*...*/) from SQL before sending to database]
      private JCheckBox _removeMultiLineComment = new JCheckBox(s_stringMgr.getString("SessionSQLPropertiesPanel.removeMultiLineComment"));

      private JCheckBox _limitSQLResultTabsChk = new JCheckBox(s_stringMgr.getString("SessionSQLPropertiesPanel.limitsqlresulttabs"));
      private IntegerField _limitSQLResultTabsField = new IntegerField(5);

		/** Label displaying the selected font. */
		private JLabel _fontLbl = new JLabel();

		/** Button to select font. */
		private FontButton _fontBtn = new FontButton(s_stringMgr.getString("SessionSQLPropertiesPanel.font"), _fontLbl);

		private JCheckBox _shareSQLHistoryChk = new JCheckBox(s_stringMgr.getString("SessionSQLPropertiesPanel.sharesqlhistory"));
		private JCheckBox _limitSQLHistoryComboSizeChk = new JCheckBox(s_stringMgr.getString("SessionSQLPropertiesPanel.limitsqlhistorysize"));
		private IntegerField _limitSQLHistoryComboSizeField = new IntegerField(5);
		private JCheckBox _showResultsMetaChk = new JCheckBox(s_stringMgr.getString("SessionSQLPropertiesPanel.showresultsmd"));

		/**
		 * This object will update the status of the GUI controls as the user
		 * makes changes.
		 */
		private final ControlMediator _controlMediator = new ControlMediator();
      private ISession _session;

      SQLPropertiesPanel(IApplication app, ISession session)
		{
			super();
         _session = session;
         createGUI();
		}

		void loadData(SessionProperties props)
		{
			_abortOnErrorChk.setSelected(props.getAbortOnError());
         _showSQLErrorsInTabChk.setSelected(props.getShowSQLErrorsInTab());
			_writeSQLErrorsToLogChk.setSelected(props.getWriteSQLErrorsToLog());
			_loadColumsInBackgroundChk.setSelected(props.getLoadColumnsInBackground());

			_autoCommitChk.setSelected(props.getAutoCommit());
			_commitOnClose.setSelected(props.getCommitOnClosingConnection());
			_sqlNbrRowsToShowField.setInt(props.getSQLNbrRowsToShow());
			_sqlLimitRowsChk.setSelected(props.getSQLLimitRows());
			_sqlFetchSizeChk.setSelected(props.getSQLUseFetchSize());
			_sqlFetchSizeField.setInt(props.getSQLFetchSize());

         if(null != _session)
         {
            IQueryTokenizer queryTokenizer = _session.getQueryTokenizer();
            TokenizerSessPropsInteractions qtp = queryTokenizer.getTokenizerSessPropsInteractions();
            if(qtp.isTokenizerDefinesStatementSeparator())
            {
               _stmtSepField.setText(queryTokenizer.getSQLStatementSeparator());
               _stmtSepField.setEditable(false);
            }
            else
            {
               _stmtSepField.setText(props.getSQLStatementSeparator());
               _stmtSepField.setEditable(true);
            }

            if (qtp.isTokenizerDefinesStartOfLineComment())
            {
               _solCommentField.setText(queryTokenizer.getLineCommentBegin());
               _solCommentField.setEditable(false);
            }
            else
            {
               _solCommentField.setText(props.getStartOfLineComment());
               _solCommentField.setEditable(true);
            }

            if (qtp.isTokenizerDefinesStatementSeparator())
            {
               _removeMultiLineComment.setSelected(queryTokenizer.isRemoveMultiLineComment());
               _removeMultiLineComment.setEnabled(false);
            }
            else
            {
               _removeMultiLineComment.setSelected(props.getRemoveMultiLineComment());
               _removeMultiLineComment.setEnabled(true);
            }
         }
         else
         {
            _stmtSepField.setText(props.getSQLStatementSeparator());
            _solCommentField.setText(props.getStartOfLineComment());
            _removeMultiLineComment.setSelected(props.getRemoveMultiLineComment());
         }

         _shareSQLHistoryChk.setSelected(props.getSQLShareHistory());
			_limitSQLHistoryComboSizeChk.setSelected(props.getLimitSQLEntryHistorySize());
			_limitSQLHistoryComboSizeField.setInt(props.getSQLEntryHistorySize());

			_limitSQLResultTabsChk.setSelected(props.getLimitSQLResultTabs());
			_limitSQLResultTabsField.setInt(props.getSqlResultTabLimit());

			_showResultsMetaChk.setSelected(props.getShowResultsMetaData());

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
			props.setShowSQLErrorsInTab(_showSQLErrorsInTabChk.isSelected());
			props.setWriteSQLErrorsToLog(_writeSQLErrorsToLogChk.isSelected());
			props.setLoadColumnsInBackground(_loadColumsInBackgroundChk.isSelected());
			props.setAutoCommit(_autoCommitChk.isSelected());
			props.setCommitOnClosingConnection(_commitOnClose.isSelected());
			props.setSQLNbrRowsToShow(_sqlNbrRowsToShowField.getInt());
			props.setSQLLimitRows(_sqlLimitRowsChk.isSelected());
			props.setSQLFetchSize(_sqlFetchSizeField.getInt());
			props.setSQLUseFetchSize(_sqlFetchSizeChk.isSelected());
			
			props.setSQLStatementSeparator(_stmtSepField.getText());
			props.setStartOfLineComment(_solCommentField.getText());
         props.setRemoveMultiLineComment(_removeMultiLineComment.isSelected());

			props.setFontInfo(_fontBtn.getFontInfo());

			props.setSQLShareHistory(_shareSQLHistoryChk.isSelected());
			props.setLimitSQLEntryHistorySize(_limitSQLHistoryComboSizeChk.isSelected());
			props.setSQLEntryHistorySize(_limitSQLHistoryComboSizeField.getInt());

			props.setLimitSQLResultTabs(_limitSQLResultTabsChk.isSelected());

         if(0 >= _limitSQLResultTabsField.getInt())
         {
            props.setSqlResultTabLimit(15);
         }
         else
         {
            props.setSqlResultTabLimit(_limitSQLResultTabsField.getInt());
         }

			props.setShowResultsMetaData(_showResultsMetaChk.isSelected());
		}

		private void updateControlStatus()
		{
			_commitOnClose.setEnabled(!_autoCommitChk.isSelected());

			_sqlNbrRowsToShowField.setEnabled(_sqlLimitRowsChk.isSelected());

         _sqlFetchSizeField.setEnabled(_sqlFetchSizeChk.isSelected());

         _limitSQLResultTabsField.setEnabled(_limitSQLResultTabsChk.isSelected());

			// If this session doesn't share SQL history with other sessions
			// then disable the controls that relate to SQL History.
			final boolean shareSQLHistory = _shareSQLHistoryChk.isSelected();

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
			pnl.setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("SessionSQLPropertiesPanel.sql")));
			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(4, 4, 4, 4);
			gbc.anchor = GridBagConstraints.CENTER;

			_autoCommitChk.addChangeListener(_controlMediator);
			_sqlLimitRowsChk.addChangeListener(_controlMediator);
			_sqlFetchSizeChk.addChangeListener(_controlMediator);
			_sqlNbrRowsToShowField.setColumns(5);
			_stmtSepField.setColumns(5);

         _limitSQLResultTabsChk.addChangeListener(_controlMediator);
         _limitSQLResultTabsField.setColumns(5);


			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.gridwidth = 2;
			pnl.add(_autoCommitChk, gbc);

			gbc.gridx+=2;
			gbc.gridwidth = GridBagConstraints.REMAINDER;
			pnl.add(_commitOnClose, gbc);

			++gbc.gridy; // new line
			gbc.gridx = 0;
			gbc.gridwidth = 3;
			pnl.add(_showResultsMetaChk, gbc);

			++gbc.gridy; // new line
			gbc.gridx = 0;
			gbc.gridwidth = 2;
			pnl.add(_sqlLimitRowsChk, gbc);
			gbc.gridwidth = 1;
			gbc.gridx+=2;
			pnl.add(_sqlNbrRowsToShowField, gbc);
			++gbc.gridx;
			gbc.gridwidth = GridBagConstraints.REMAINDER;
			pnl.add(new JLabel(s_stringMgr.getString("SessionSQLPropertiesPanel.rows")), gbc);
			
			
			// Show fetchSize-Option
			++gbc.gridy; // new line
			gbc.gridx = 0;
			gbc.gridwidth = 2;
			pnl.add(_sqlFetchSizeChk, gbc);
			gbc.gridwidth = 1;
			gbc.gridx+=2;
			pnl.add(_sqlFetchSizeField, gbc);
			++gbc.gridx;
			gbc.gridwidth = GridBagConstraints.REMAINDER;
			pnl.add(new JLabel(s_stringMgr.getString("SessionSQLPropertiesPanel.rows")), gbc);
			
			
			
			
			
			
			
			

         ++gbc.gridy; // new line
         gbc.gridx = 0;
         gbc.gridwidth = 2;
         pnl.add(_limitSQLResultTabsChk, gbc);
         gbc.gridwidth = 1;
         gbc.gridx+=2;
         pnl.add(_limitSQLResultTabsField, gbc);
         ++gbc.gridx;
         gbc.gridwidth = GridBagConstraints.REMAINDER;
         pnl.add(new JLabel(s_stringMgr.getString("SessionSQLPropertiesPanel.tabs")), gbc);


			++gbc.gridy; // new line
			gbc.gridx = 0;
			gbc.gridwidth = GridBagConstraints.REMAINDER;
			pnl.add(_abortOnErrorChk, gbc);

			++gbc.gridy; // new line
			gbc.gridx = 0;
			gbc.gridwidth = GridBagConstraints.REMAINDER;
			pnl.add(_showSQLErrorsInTabChk, gbc);

         ++gbc.gridy; // new line
         gbc.gridx = 0;
         gbc.gridwidth = GridBagConstraints.REMAINDER;
         pnl.add(_writeSQLErrorsToLogChk, gbc);


         ++gbc.gridy; // new line
         gbc.gridx = 0;
         gbc.gridwidth = GridBagConstraints.REMAINDER;
         pnl.add(createLoadColumnsInBackkgroundPanel(), gbc);


         if (null != _session)
         {
            TokenizerSessPropsInteractions tep = _session.getQueryTokenizer().getTokenizerSessPropsInteractions();

            if(   tep.isTokenizerDefinesRemoveMultiLineComment() ||
                  tep.isTokenizerDefinesStartOfLineComment() ||
                  tep.isTokenizerDefinesStatementSeparator())
            {
               ++gbc.gridy;
               gbc.gridwidth = 4;
               MultipleLineLabel lbl = new MultipleLineLabel(s_stringMgr.getString("SessionSQLPropertiesPanel.tokenizerNotEditableMsg"));
               lbl.setForeground(Color.red);
               pnl.add(lbl, gbc);

               gbc.gridwidth = 1;
            }
         }
         

			++gbc.gridy; // new line
			gbc.gridx = 0;
			gbc.gridwidth = 1;
			pnl.add(new JLabel(s_stringMgr.getString("SessionSQLPropertiesPanel.stmtsep")), gbc);
			++gbc.gridx;
			pnl.add(_stmtSepField, gbc);
			++gbc.gridx;
			pnl.add(new RightLabel(s_stringMgr.getString("SessionSQLPropertiesPanel.solcomment")), gbc);
			++gbc.gridx;
			pnl.add(_solCommentField, gbc);

         ++gbc.gridy; // new line
         gbc.gridx = 0;
         gbc.gridwidth = 4;
         pnl.add(_removeMultiLineComment, gbc);


         return pnl;
		}

      private JPanel createLoadColumnsInBackkgroundPanel()
      {
         JPanel ret = new JPanel(new GridBagLayout());

         GridBagConstraints gbc;

         gbc = new GridBagConstraints(0,0,1,1,1,1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0,0,5,0), 0,0);
         String loadColsInBackgroundDescription = s_stringMgr.getString("SessionSQLPropertiesPanel.loadColsInBackgroundDescription");
         ret.add(new MultipleLineLabel(loadColsInBackgroundDescription), gbc);

         gbc = new GridBagConstraints(0,1,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0);
         ret.add(_loadColumsInBackgroundChk, gbc);

         ret.setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("SessionSQLPropertiesPanel.columnLoading")));

         return ret;
      }

      private JPanel createFontPanel()
		{
			JPanel pnl = new JPanel();
			pnl.setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("SessionSQLPropertiesPanel.sqlentryarea")));
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
			pnl.setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("SessionSQLPropertiesPanel.sqlhistory")));
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
