package net.sourceforge.squirrel_sql.client.session.action.sqlscript.table_script;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;

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
		super(parentFrame, s_stringMgr.getString("sqlscript.dlgCreatTableOfSql"), true);

		getContentPane().setLayout(new GridBagLayout());

		GridBagConstraints gbc;

		gbc =  new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0);
		getContentPane().add(new JLabel(s_stringMgr.getString("sqlscript.enterNameOfTable")), gbc);

		gbc =  new GridBagConstraints(0, 1, 1, 1, 1, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0);
		txtTableName = new JTextField();
		getContentPane().add(txtTableName, gbc);

		gbc =  new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0);
		chkDropTable = new JCheckBox(s_stringMgr.getString("sqlscript.dropIfExists"));
		getContentPane().add(chkDropTable, gbc);

		gbc =  new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0);
		chkScriptOnly = new JCheckBox(s_stringMgr.getString("sqlscript.scriptOnly"));
		getContentPane().add(chkScriptOnly, gbc);

		gbc =  new GridBagConstraints(0, 4, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0);
		getContentPane().add(createButtonsPanel(), gbc);
		
		getRootPane().setDefaultButton(btnOK);

      GUIUtils.enableCloseByEscape(this);

      SwingUtilities.invokeLater(() -> txtTableName.requestFocus());
   }

	private JPanel createButtonsPanel()
	{
		GridBagConstraints gbc;
		JPanel pnlButtons = new JPanel();
		pnlButtons.setLayout(new GridBagLayout());

		gbc =  new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
		btnOK = new JButton(s_stringMgr.getString("sqlscript.tableScriptOk"));
		pnlButtons.add(btnOK, gbc);

		gbc =  new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 0), 0, 0);
		btnCancel = new JButton(s_stringMgr.getString("sqlscript.tableScriptCancel"));
		pnlButtons.add(btnCancel, gbc);
		return pnlButtons;
	}
}
