package net.sourceforge.squirrel_sql.plugins.sqlscript.table_script;

import javax.swing.*;
import java.awt.*;

public class CreateTableOfCurrentSQLDialog extends JDialog
{
   JButton btnOK;
   JButton btnCancel;
   JTextField txtTableName;
   JCheckBox chkScriptOnly;
   JCheckBox chkDropTable;

   public CreateTableOfCurrentSQLDialog(JFrame parentFrame)
   {
      super(parentFrame, "Create table of SQL", true);

      getContentPane().setLayout(new GridLayout(5,1,5,0));

      getContentPane().add(new JLabel("Enter name of table:"));

      txtTableName = new JTextField();
      getContentPane().add(txtTableName);

      chkDropTable = new JCheckBox("Drop table if exists");
      getContentPane().add(chkDropTable);

      chkScriptOnly = new JCheckBox("Generate script only");
      getContentPane().add(chkScriptOnly);

      JPanel pnlButtons = new JPanel();
      pnlButtons.setLayout(new GridLayout(1,2,0,5));

      btnOK = new JButton("OK");
      pnlButtons.add(btnOK);
      btnCancel = new JButton("Cancel");
      pnlButtons.add(btnCancel);

      getContentPane().add(pnlButtons);
   }
}
