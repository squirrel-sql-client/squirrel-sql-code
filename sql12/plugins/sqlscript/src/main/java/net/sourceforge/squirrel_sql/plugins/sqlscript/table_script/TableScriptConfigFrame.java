package net.sourceforge.squirrel_sql.plugins.sqlscript.table_script;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;


public class TableScriptConfigFrame extends JDialog
{

	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(TableScriptConfigFrame.class);

	JRadioButton optConstAndIndAtEnd;
	JRadioButton optConstAndIndAfterTable;
	JCheckBox constToTablesNotInScript;
	JButton btnOk;
	JButton btnCancel;

	public TableScriptConfigFrame(JFrame mainFrame)
	{
		// i18n[sqlscript.configMultiTableScript=Configuration of multi table scripts]
		super(mainFrame, s_stringMgr.getString("sqlscript.configMultiTableScript"), true);

		JPanel pnl = new JPanel(new GridBagLayout());
		
		GridBagConstraints gbc;

		
		// i18n[sqlscript.configYourMultiTableScript=Configure your multi table script:]
		Label lbl = new Label(s_stringMgr.getString("sqlscript.configYourMultiTableScript"));
		gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
		pnl.add(lbl, gbc);

		// i18n[sqlscript.configYourMultiTableScriptIxAtEnd=Constraints and indexes at end of script]
		optConstAndIndAtEnd = new JRadioButton(s_stringMgr.getString("sqlscript.configYourMultiTableScriptIxAtEnd"));
		gbc = new GridBagConstraints(0,1,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,5,5,5),0,0);
		pnl.add(optConstAndIndAtEnd,gbc);

		// i18n[sqlscript.configYourMultiTableScriptIxAfterEach=Constraints and indexes after each table]
		optConstAndIndAfterTable = new JRadioButton(s_stringMgr.getString("sqlscript.configYourMultiTableScriptIxAfterEach"));
		gbc = new GridBagConstraints(0,2,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,5,5,5),0,0);
		pnl.add(optConstAndIndAfterTable,gbc);

		// i18n[sqlscript.configYourMultiTableScriptConstr=Include constraints to tables not in selection]
		constToTablesNotInScript = new JCheckBox(s_stringMgr.getString("sqlscript.configYourMultiTableScriptConstr"));
		gbc = new GridBagConstraints(0,3,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,5,5,5),0,0);
		pnl.add(constToTablesNotInScript,gbc);


		JPanel pnlButtons = new JPanel(new GridLayout(1,2,5,0));

      // i18n[sqlscript.configYourMultiTableScriptOk=OK]
      btnOk = new JButton(s_stringMgr.getString("sqlscript.configYourMultiTableScriptOk"));
      pnlButtons.add(btnOk);
      
		// i18n[sqlscript.configYourMultiTableScriptCancel=Cancel]
		btnCancel = new JButton(s_stringMgr.getString("sqlscript.configYourMultiTableScriptCancel"));
		pnlButtons.add(btnCancel);

		
		gbc = new GridBagConstraints(0,4,1,1,0,0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0,5,5,5),0,0);
		pnl.add(pnlButtons, gbc);
		

		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(optConstAndIndAfterTable);
		buttonGroup.add(optConstAndIndAtEnd);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(pnl, BorderLayout.NORTH);
		getContentPane().add(new JPanel(), BorderLayout.CENTER);


		setSize(326,178);

		AbstractAction closeAction = new AbstractAction()
					{
						public void actionPerformed(ActionEvent actionEvent)
						{
							setVisible(false);
							dispose();
						}
					};
		KeyStroke escapeStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(escapeStroke, "CloseAction");
		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeStroke, "CloseAction");
		getRootPane().getInputMap(JComponent.WHEN_FOCUSED).put(escapeStroke, "CloseAction");
		getRootPane().getActionMap().put("CloseAction", closeAction);

		getRootPane().setDefaultButton(btnOk);

	}
}
