package net.sourceforge.squirrel_sql.client.session.mainpanel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.NumberFormat;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import net.sourceforge.squirrel_sql.client.session.SQLExecutionInfo;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class QueryInfoPanel extends JPanel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(QueryInfoPanel.class);

   private JTextArea _queryTxt = new JTextArea();
   private JLabel _rowCountLbl = new JLabel();
   private JLabel _executedLbl = new JLabel();
   private JLabel _elapsedLbl = new JLabel();
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

         // i18n[ResultTab.elapsedTime=Total: {0}, SQL query: {1}, Building output: {2}]
         String elapsedTime =
             s_stringMgr.getString("ResultTab.elapsedTime",
                                   new String[] { totalTime,
                                                  queryTime,
                                                  outputTime});
      return elapsedTime;
   }

   private void createGUI()
   {
      setLayout(new GridBagLayout());
      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(15, 10, 0, 10), 0,0);
      String label = s_stringMgr.getString("ResultTab.executedLabel");
      add(new JLabel(label, SwingConstants.RIGHT), gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(15, 0, 0, 10), 0,0);
      add(_executedLbl, gbc);


      //++gbc.gridy;
      gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(10, 10, 0, 10), 0,0);
      label = s_stringMgr.getString("ResultTab.rowCountLabel");
      add(new JLabel(label, SwingConstants.RIGHT), gbc);

      gbc = new GridBagConstraints(1,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 10), 0,0);
      add(_rowCountLbl, gbc);



      //++gbc.gridy;
      gbc = new GridBagConstraints(0,2,1,1,0,0,GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(10, 10, 0, 10), 0,0);
      label = s_stringMgr.getString("ResultTab.statementLabel");
      add(new JLabel(label, SwingConstants.RIGHT), gbc);

      gbc = new GridBagConstraints(1,2,1,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(10, 0, 0, 10), 0,0);
      _queryScrollPane = new JScrollPane(_queryTxt);
      add(_queryScrollPane, gbc);



      gbc = new GridBagConstraints(0,3,1,1,0,0,GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(10, 10, 30, 10), 0,0);
      label = s_stringMgr.getString("ResultTab.elapsedTimeLabel");
      add(new JLabel(label, SwingConstants.RIGHT), gbc);

      gbc = new GridBagConstraints(1,3,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 0, 30, 10), 0,0);
      add(_elapsedLbl, gbc);
   }
}
