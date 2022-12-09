package net.sourceforge.squirrel_sql.client.session.mainpanel;

import net.sourceforge.squirrel_sql.client.session.SQLExecutionInfo;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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

   QueryInfoPanel()
   {
      createGUI();
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

   private void createGUI()
   {
      setLayout(new GridBagLayout());
      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(15, 10, 0, 10), 0, 0);
      add(new JLabel(s_stringMgr.getString("ResultTab.executedLabel"), SwingConstants.RIGHT), gbc);

      // If GridBagConstraints.HORIZONTAL is changes to GridBagConstraints.NONE the controls won't be displayed when SQL are long.
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

      gbc = new GridBagConstraints(1, 2, 1, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(10, 0, 0, 10), 0, 0);
      _queryScrollPane = new JScrollPane(_queryTxt);
      add(_queryScrollPane, gbc);


      gbc = new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(10, 10, 30, 10), 0, 0);
      add(new JLabel(s_stringMgr.getString("ResultTab.elapsedTimeLabel"), SwingConstants.RIGHT), gbc);

      gbc = new GridBagConstraints(1, 3, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 30, 10), 0, 0);
      add(_elapsedLbl, gbc);
   }
}
