package net.sourceforge.squirrel_sql.client.session.properties;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.FontChooser;
import net.sourceforge.squirrel_sql.fw.gui.FontInfo;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.IntegerField;
import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.sql.IQueryTokenizer;
import net.sourceforge.squirrel_sql.fw.sql.TokenizerSessPropsInteractions;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

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
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class SQLPropertiesPanel extends JPanel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SQLPropertiesPanel.class);


   private JCheckBox _abortOnErrorChk = new JCheckBox(s_stringMgr.getString("SessionSQLPropertiesPanel.abortonerror"));
   private JCheckBox _showSQLErrorsInTabChk = new JCheckBox(s_stringMgr.getString("SessionSQLPropertiesPanel.showSQLErrorsInTab"));
   private JCheckBox _writeSQLErrorsToLogChk = new JCheckBox(s_stringMgr.getString("SessionSQLPropertiesPanel.writesqlerrorstolog"));
   private JCheckBox _loadColumsInBackgroundChk = new JCheckBox(s_stringMgr.getString("SessionSQLPropertiesPanel.loadColumsInBackground"));

   private IntegerField _metaDataLoadingTimeOutTxt = new IntegerField(8, 0);

   private JCheckBox _autoCommitChk = new JCheckBox(s_stringMgr.getString("SessionSQLPropertiesPanel.autocommit"));
   private JCheckBox _commitOnClose = new JCheckBox(s_stringMgr.getString("SessionSQLPropertiesPanel.commitonclose"));

   private SQLResultConfigCtrl _sqlResultConfigCtrl = new SQLResultConfigCtrl();

   private JumpToObjectTreeConfigCtrl _jumpToObjectTreeConfigCtrl = new JumpToObjectTreeConfigCtrl();

   private JTextField _stmtSepField = new JTextField(5);
   private JTextField _solCommentField = new JTextField(2);
   // i18n[SessionSQLPropertiesPanel.removeMultiLineComment=Remove multi line comment (/*...*/) from SQL before sending to database]
   private JCheckBox _removeMultiLineComment = new JCheckBox(s_stringMgr.getString("SessionSQLPropertiesPanel.removeMultiLineComment"));

   private JCheckBox _limitSQLResultTabsChk = new JCheckBox(s_stringMgr.getString("SessionSQLPropertiesPanel.limitsqlresulttabs"));
   private IntegerField _limitSQLResultTabsField = new IntegerField(5);

   /**
    * Label displaying the selected font.
    */
   private JLabel _fontLbl = new JLabel();

   /**
    * Button to select font.
    */
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
      _metaDataLoadingTimeOutTxt.setInt((int)props.getMetaDataLoadingTimeOut());

      _autoCommitChk.setSelected(props.getAutoCommit());
      _commitOnClose.setSelected(props.getCommitOnClosingConnection());
      _sqlResultConfigCtrl.loadData(props);
      _jumpToObjectTreeConfigCtrl.loadData(props);

      if (null != _session)
      {
         IQueryTokenizer queryTokenizer = _session.getQueryTokenizer();
         TokenizerSessPropsInteractions qtp = queryTokenizer.getTokenizerSessPropsInteractions();
         if (qtp.isTokenizerDefinesStatementSeparator())
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
      props.setMetaDataLoadingTimeOut(_metaDataLoadingTimeOutTxt.getInt());
      props.setAutoCommit(_autoCommitChk.isSelected());
      props.setCommitOnClosingConnection(_commitOnClose.isSelected());

      props.setSQLLimitRows(_sqlResultConfigCtrl.isLimitRows());
      props.setSQLNbrRowsToShow(_sqlResultConfigCtrl.getNbrRowsToShow());

      props.setSQLReadOn(_sqlResultConfigCtrl.isReadOn());
      props.setSQLReadOnBlockSize(_sqlResultConfigCtrl.getReadOnBlockSize());

      props.setSQLUseFetchSize(_sqlResultConfigCtrl.isUseFetchSize());
      props.setSQLFetchSize(_sqlResultConfigCtrl.getFetchSize());

      props.setAllowCtrlBJumpToObjectTree(_jumpToObjectTreeConfigCtrl.isAllowCtrlBJumpToObjectTree());
      props.setAllowCtrlMouseClickJumpToObjectTree(_jumpToObjectTreeConfigCtrl.isAllowCtrlMouseClickJumpToObjectTree());

      props.setSQLStatementSeparator(_stmtSepField.getText());
      props.setStartOfLineComment(_solCommentField.getText());
      props.setRemoveMultiLineComment(_removeMultiLineComment.isSelected());

      props.setFontInfo(_fontBtn.getFontInfo());

      props.setSQLShareHistory(_shareSQLHistoryChk.isSelected());
      props.setLimitSQLEntryHistorySize(_limitSQLHistoryComboSizeChk.isSelected());
      props.setSQLEntryHistorySize(_limitSQLHistoryComboSizeField.getInt());

      props.setLimitSQLResultTabs(_limitSQLResultTabsChk.isSelected());

      if (0 >= _limitSQLResultTabsField.getInt())
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

      _stmtSepField.setColumns(5);

      _limitSQLResultTabsChk.addChangeListener(_controlMediator);
      _limitSQLResultTabsField.setColumns(5);


      gbc.gridx = 0;
      gbc.gridy = 0;
      gbc.gridwidth = 2;
      pnl.add(_autoCommitChk, gbc);

      gbc.gridx += 2;
      gbc.gridwidth = GridBagConstraints.REMAINDER;
      pnl.add(_commitOnClose, gbc);

      ++gbc.gridy; // new line
      gbc.gridx = 0;
      gbc.gridwidth = 3;
      pnl.add(_showResultsMetaChk, gbc);


      Insets oldInsets;

      ++gbc.gridy;
      gbc.gridwidth = GridBagConstraints.REMAINDER;
      oldInsets = gbc.insets;
      gbc.insets = new Insets(oldInsets.top + 10, oldInsets.left, oldInsets.bottom + 10, oldInsets.right);
      pnl.add(_jumpToObjectTreeConfigCtrl.createJumpToObjectTreeConfigPanel(), gbc);
      gbc.insets = oldInsets;

      ++gbc.gridy;
      gbc.gridwidth = GridBagConstraints.REMAINDER;
      oldInsets = gbc.insets;
      gbc.insets = new Insets(oldInsets.top + 10, oldInsets.left, oldInsets.bottom + 10, oldInsets.right);
      pnl.add(_sqlResultConfigCtrl.createResultLimitAndReadOnPanel(), gbc);
      gbc.insets = oldInsets;


      ++gbc.gridy; // new line
      gbc.gridx = 0;
      gbc.gridwidth = 2;
      pnl.add(_limitSQLResultTabsChk, gbc);
      gbc.gridwidth = 1;
      gbc.gridx += 2;
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
      pnl.add(createMetdDataHandlingPanel(), gbc);


      if (null != _session)
      {
         TokenizerSessPropsInteractions tep = _session.getQueryTokenizer().getTokenizerSessPropsInteractions();

         if (tep.isTokenizerDefinesRemoveMultiLineComment() ||
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

   private JPanel createMetdDataHandlingPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      JPanel columnLoading = new JPanel(new GridBagLayout());

      gbc = new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0);
      String loadColsInBackgroundDescription = s_stringMgr.getString("SessionSQLPropertiesPanel.loadColsInBackgroundDescription");
      columnLoading.add(new MultipleLineLabel(loadColsInBackgroundDescription), gbc);

      gbc = new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
      columnLoading.add(_loadColumsInBackgroundChk, gbc);

      columnLoading.setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("SessionSQLPropertiesPanel.columnLoading")));

      gbc = new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0);
      ret.add(columnLoading, gbc);



      JPanel metaDataTimeOut = new JPanel(new GridBagLayout());

      gbc = new GridBagConstraints(0, 0, 2, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0);
      String metaDataTimeOutDescription = s_stringMgr.getString("SessionSQLPropertiesPanel.metaDataTimeOutDescription");
      metaDataTimeOut.add(new MultipleLineLabel(metaDataTimeOutDescription), gbc);

      gbc = new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
      metaDataTimeOut.add(new JLabel(s_stringMgr.getString("SessionSQLPropertiesPanel.metaDataTimeOutMillis")), gbc);

      gbc = new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 0), 0, 0);
      metaDataTimeOut.add(_metaDataLoadingTimeOutTxt, gbc);

      metaDataTimeOut.setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("SessionSQLPropertiesPanel.metaDataLoading")));


      gbc = new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0);
      ret.add(metaDataTimeOut, gbc);

      ret.setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("SessionSQLPropertiesPanel.metaDataHandling")));

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

      _fontBtn.addActionListener(e -> onFontButtonClicked());

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

   private void onFontButtonClicked()
   {
      FontInfo fi = _fontBtn.getFontInfo();
      Font font = null;
      if (fi != null)
      {
         font = fi.createFont();
      }
      font = new FontChooser(GUIUtils.getOwningWindow(this)).showDialog(font);
      if (font != null)
      {
         _fontBtn.setSelectedFont(font);
         _fontBtn._lbl.setText(new FontInfo(font).toString());
      }
   }

   /**
    * This class will update the status of the GUI controls as the user
    * makes changes.
    */
   private final class ControlMediator implements ChangeListener, ActionListener
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
