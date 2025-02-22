package net.sourceforge.squirrel_sql.client.session.mainpanel;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLExecutionInfo;
import net.sourceforge.squirrel_sql.fw.gui.ClipboardUtil;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.text.NumberFormat;

public class QueryInfoPanel extends JPanel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(QueryInfoPanel.class);

   private JTextArea _queryTxt = new JTextArea();
   private JTextField _rowCountLbl = GUIUtils.styleTextFieldToCopyableLabel(new JTextField());
   private JTextField _executedLbl = GUIUtils.styleTextFieldToCopyableLabel(new JTextField());
   private JTextField _elapsedLbl = GUIUtils.styleTextFieldToCopyableLabel(new JTextField());
   private JScrollPane _queryScrollPane;
   private JButton _btnCopySql;
   private JButton _btnCopySqlToCaret;
   private JButton _btnCopySqlToBottom;

   public QueryInfoPanel(ISession session)
   {
      createUI();

      _btnCopySql.addActionListener(e -> ClipboardUtil.copyToClip(_queryTxt.getText()));
      _btnCopySqlToCaret.addActionListener(e -> onCopySqlToCaret(session));
      _btnCopySqlToBottom.addActionListener(e -> onCopySqlToBottom(session));
   }

   private void onCopySqlToBottom(ISession session)
   {
      ISQLPanelAPI panelAPI = session.getSQLPanelAPIOfActiveSessionWindow(true);
      if(null == panelAPI)
      {
         return;
      }

      int beginLen = panelAPI.getSQLEntryPanel().getText().length();
      if(0 == beginLen)
      {
         panelAPI.getSQLEntryPanel().appendText(_queryTxt.getText());
      }
      else
      {
         panelAPI.getSQLEntryPanel().appendText("\n\n" + _queryTxt.getText());
         panelAPI.setCaretPosition(beginLen + 2);
      }
   }

   private void onCopySqlToCaret(ISession session)
   {
      ISQLPanelAPI panelAPI = session.getSQLPanelAPIOfActiveSessionWindow(true);
      if(null == panelAPI)
      {
         return;
      }

      int caretPosition = panelAPI.getSQLEntryPanel().getCaretPosition();
      panelAPI.getSQLEntryPanel().setSelectionStart(caretPosition);
      panelAPI.getSQLEntryPanel().setSelectionEnd(caretPosition);
      if(0 == caretPosition)
      {
         panelAPI.getSQLEntryPanel().replaceSelection(_queryTxt.getText() + "\n\n");
      }
      else
      {
         panelAPI.getSQLEntryPanel().replaceSelection("\n\n" +_queryTxt.getText() + "\n\n");
      }
      panelAPI.setCaretPosition(panelAPI.getCaretPosition() - 2);

   }


   private void createUI()
   {
      setLayout(new GridBagLayout());
      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(15, 10, 0, 10), 0, 0);
      add(new JLabel(s_stringMgr.getString("ResultTab.executedLabel"), SwingConstants.RIGHT), gbc);

      gbc = new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(15, 0, 0, 10), 0, 0);
      add(_executedLbl, gbc);


      //++gbc.gridy;
      gbc = new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(10, 10, 0, 10), 0, 0);
      add(new JLabel(s_stringMgr.getString("ResultTab.rowCountLabel"), SwingConstants.RIGHT), gbc);

      gbc = new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 10), 0, 0);
      add(_rowCountLbl, gbc);


      //++gbc.gridy;
      gbc = new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(10, 10, 0, 10), 0, 0);
      add(new JLabel(s_stringMgr.getString("ResultTab.statementLabel"), SwingConstants.RIGHT), gbc);

      gbc = new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(5, 0, 0, 10), 0, 0);
      add(createCopyButtonsPanel(), gbc);

      gbc = new GridBagConstraints(1, 2, 1, 2, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(10, 0, 0, 10), 0, 0);
      _queryScrollPane = new JScrollPane(_queryTxt);
      add(_queryScrollPane, gbc);


      gbc = new GridBagConstraints(0, 4, 1, 1, 0, 0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(10, 10, 30, 10), 0, 0);
      add(new JLabel(s_stringMgr.getString("ResultTab.elapsedTimeLabel"), SwingConstants.RIGHT), gbc);

      gbc = new GridBagConstraints(1, 4, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 30, 10), 0, 0);
      add(_elapsedLbl, gbc);
   }


   void load(int rowCount, SQLExecutionInfo exInfo)
   {
      _queryTxt.setEditable(false);
      _queryTxt.setText(exInfo.getQueryHolder().getOriginalQuery());

      displayRowCount(rowCount);
      _executedLbl.setText(exInfo.getSQLExecutionStartTime().toString());
      _elapsedLbl.setText(formatElapsedTime(exInfo));

      GUIUtils.forceScrollToBegin(_queryScrollPane);
   }

   public void displayRowCount(int rowCount)
   {
      _rowCountLbl.setText(String.valueOf(rowCount));
   }

   private String formatElapsedTime(SQLExecutionInfo exInfo)
   {
      final NumberFormat nbrFmt = NumberFormat.getNumberInstance();
      double executionLength = exInfo.getSQLExecutionElapsedMillis() / 1000.0;
      double outputLength = exInfo.getResultsProcessingElapsedMillis() / 1000.0;

      String totalTime = nbrFmt.format(executionLength + outputLength);
      String queryTime = nbrFmt.format(executionLength);
      String outputTime = nbrFmt.format(outputLength);

      String elapsedTime = s_stringMgr.getString("ResultTab.elapsedTime", new String[]{totalTime, queryTime, outputTime});
      return elapsedTime;
   }

   private JPanel createCopyButtonsPanel()
   {
      JPanel pnl = new JPanel(new GridLayout(3,1, 0,3));
      _btnCopySql = GUIUtils.styleAsToolbarButton(new JButton(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.COPY)));
      _btnCopySql.setToolTipText(s_stringMgr.getString("ResultTab.copy.sql.tooltip"));
      pnl.add(_btnCopySql);

      _btnCopySqlToCaret = GUIUtils.styleAsToolbarButton(new JButton(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.COPY_2_CARET)));
      _btnCopySqlToCaret.setToolTipText(s_stringMgr.getString("ResultTab.copy.to.caret.tooltip"));
      pnl.add(_btnCopySqlToCaret);

      _btnCopySqlToBottom = GUIUtils.styleAsToolbarButton(new JButton(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.COPY_2_BOTTOM)));
      _btnCopySqlToBottom.setToolTipText(s_stringMgr.getString("ResultTab.copy.to.bottom.tooltip"));
      pnl.add(_btnCopySqlToBottom);

      return pnl;
   }
}
