package net.sourceforge.squirrel_sql.client.session.mainpanel;

import net.sourceforge.squirrel_sql.client.session.SQLExecutionInfo;
import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;

public class QueryInfoPanel extends JPanel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(QueryInfoPanel.class);

   private MultipleLineLabel _queryLbl = new MultipleLineLabel();
   private JLabel _rowCountLbl = new JLabel();
   private JLabel _executedLbl = new JLabel();
   private JLabel _elapsedLbl = new JLabel();

   QueryInfoPanel()
   {
      createGUI();
   }

   void load(int rowCount, SQLExecutionInfo exInfo)
   {
      _queryLbl.setText(StringUtilities.cleanString(exInfo.getSQL()));
      displayRowCount(rowCount);
      _executedLbl.setText(exInfo.getSQLExecutionStartTime().toString());
      _elapsedLbl.setText(formatElapsedTime(exInfo));
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
      GridBagConstraints gbc = new GridBagConstraints();

      gbc.anchor = GridBagConstraints.NORTHWEST;
      gbc.gridwidth = 1;
      gbc.weightx = 0;

      gbc.gridx = 0;
      gbc.gridy = 0;
      gbc.insets = new Insets(5, 10, 5, 10);
      gbc.fill = GridBagConstraints.HORIZONTAL;
         // i18n[ResultTab.executedLabel=Executed:]
         String label = s_stringMgr.getString("ResultTab.executedLabel");
      add(new JLabel(label, SwingConstants.RIGHT), gbc);

      ++gbc.gridy;
         // i18n[ResultTab.rowCountLabel=Row Count:]
         label = s_stringMgr.getString("ResultTab.rowCountLabel");
      add(new JLabel(label, SwingConstants.RIGHT), gbc);

      ++gbc.gridy;
         // i18n[ResultTab.statementLabel=SQL:]
         label = s_stringMgr.getString("ResultTab.statementLabel");
      add(new JLabel(label, SwingConstants.RIGHT), gbc);

      ++gbc.gridy;
         // i18n[ResultTab.elapsedTimeLabel=Elapsed Time (seconds):]
         label = s_stringMgr.getString("ResultTab.elapsedTimeLabel");
         add(new JLabel(label, SwingConstants.RIGHT), gbc);

      gbc.gridwidth = GridBagConstraints.REMAINDER;
      gbc.weightx = 1;

      gbc.gridx = 1;
      gbc.gridy = 0;
      add(_executedLbl, gbc);

      ++gbc.gridy;
      add(_rowCountLbl, gbc);

      ++gbc.gridy;
      add(_queryLbl, gbc);

      ++gbc.gridy;
      add(_elapsedLbl, gbc);
   }
}
