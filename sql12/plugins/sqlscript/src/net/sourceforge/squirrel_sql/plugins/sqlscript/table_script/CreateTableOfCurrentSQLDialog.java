package net.sourceforge.squirrel_sql.plugins.sqlscript.table_script;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.Frame;
import java.awt.GridLayout;

public class CreateTableOfCurrentSQLDialog extends JDialog
{
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(CreateTableOfCurrentSQLDialog.class);


	JButton btnOK;
	JButton btnCancel;
	JTextField txtTableName;
	JCheckBox chkScriptOnly;
	JCheckBox chkDropTable;

	public CreateTableOfCurrentSQLDialog(Frame parentFrame)
	{
		// i18n[sqlscript.dlgCreatTableOfSql=Create table of SQL]
		super(parentFrame, s_stringMgr.getString("sqlscript.dlgCreatTableOfSql"), true);

		getContentPane().setLayout(new GridLayout(5,1,5,0));

		// i18n[sqlscript.enterNameOfTable=Enter name of table:]
		getContentPane().add(new JLabel(s_stringMgr.getString("sqlscript.enterNameOfTable")));

		txtTableName = new JTextField();
		getContentPane().add(txtTableName);

		// i18n[sqlscript.dropIfExists=Drop table if exists]
		chkDropTable = new JCheckBox(s_stringMgr.getString("sqlscript.dropIfExists"));
		getContentPane().add(chkDropTable);

		// i18n[sqlscript.scriptOnly=Generate script only]
		chkScriptOnly = new JCheckBox(s_stringMgr.getString("sqlscript.scriptOnly"));
		getContentPane().add(chkScriptOnly);

		JPanel pnlButtons = new JPanel();
		pnlButtons.setLayout(new GridLayout(1,2,0,5));

		// i18n[sqlscript.tableScriptOk=OK]
		btnOK = new JButton(s_stringMgr.getString("sqlscript.tableScriptOk"));
		pnlButtons.add(btnOK);
		// i18n[sqlscript.tableScriptCancel=Cancel]
		btnCancel = new JButton(s_stringMgr.getString("sqlscript.tableScriptCancel"));
		pnlButtons.add(btnCancel);

		getContentPane().add(pnlButtons);
		
		getRootPane().setDefaultButton(btnOK);

      GUIUtils.enableCloseByEscape(this);

      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            txtTableName.requestFocus();
         }
      });
   }
}
