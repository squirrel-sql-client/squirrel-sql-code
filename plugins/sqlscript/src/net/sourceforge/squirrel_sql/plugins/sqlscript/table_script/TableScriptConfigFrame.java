package net.sourceforge.squirrel_sql.plugins.sqlscript.table_script;

import net.sourceforge.squirrel_sql.client.mainframe.MainFrame;

import javax.swing.*;
import java.awt.*;


public class TableScriptConfigFrame extends JDialog
{
   JRadioButton optConstAndIndAtEnd;
   JRadioButton optConstAndIndAfterTable;
   JCheckBox constToTablesNotInScript;
   JButton btnOk;

   public TableScriptConfigFrame(MainFrame mainFrame)
   {
      super(mainFrame, "Configuration of multi table scripts", true);

      JPanel pnl = new JPanel(new GridLayout(5,1,5,0));

      Label lbl = new Label("Configure your multi table script:");
      pnl.add(lbl);

      optConstAndIndAtEnd = new JRadioButton("Constraints and indexes at end of script");
      pnl.add(optConstAndIndAtEnd);

      optConstAndIndAfterTable = new JRadioButton("Constraints and indexes after each table");
      pnl.add(optConstAndIndAfterTable);

      constToTablesNotInScript = new JCheckBox("Include constraints to tables not in selection");
      pnl.add(constToTablesNotInScript);

      btnOk = new JButton("OK");
      pnl.add(btnOk);


      ButtonGroup buttonGroup = new ButtonGroup();
      buttonGroup.add(optConstAndIndAfterTable);
      buttonGroup.add(optConstAndIndAtEnd);

      getContentPane().setLayout(new BorderLayout());
      getContentPane().add(pnl, BorderLayout.NORTH);
      getContentPane().add(new JPanel(), BorderLayout.CENTER);


      setSize(326,178);
   }
}
