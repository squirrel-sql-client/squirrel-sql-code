package net.sourceforge.squirrel_sql.plugins.userscript.kernel;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;

public class ScriptPropertiesDialog extends JDialog
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(ScriptPropertiesDialog.class);


	JTextField txtName;
	JTextField txtScriptClass;
	JCheckBox chkShowInStandard;
	JButton btnOk;
	JButton btnCancel;
	JButton btnCheck;


	public ScriptPropertiesDialog(Frame owner)
	{
		// i18n[userscript.scriptProps=Script properties]
		super(owner, s_stringMgr.getString("userscript.scriptProps"), false);

      getContentPane().setLayout(new GridLayout(4,1));

		JPanel pnl1 = new JPanel(new BorderLayout());
		// i18n[userscript.scriptPropsName=Name]
		JLabel lblName = new JLabel(s_stringMgr.getString("userscript.scriptPropsName"));
		pnl1.add(lblName, BorderLayout.WEST);
		txtName = new JTextField();
		pnl1.add(txtName, BorderLayout.CENTER);
		getContentPane().add(pnl1);


		JPanel pnl2 = new JPanel(new BorderLayout());
		// i18n[userscript1.scriptClass=Script class]
		JLabel lblScriptClass = new JLabel(s_stringMgr.getString("userscript.scriptClass1"));
		pnl2.add(lblScriptClass, BorderLayout.WEST);
		txtScriptClass = new JTextField();
		pnl2.add(txtScriptClass, BorderLayout.CENTER);
		getContentPane().add(pnl2);

		// i18n[userscript.showInStandardMenues1=Show in standard menues]
		chkShowInStandard = new JCheckBox(s_stringMgr.getString("userscript.showInStandardMenues1"));
		getContentPane().add(chkShowInStandard);

		JPanel pnl3 = new JPanel();
		pnl3.setLayout(new GridLayout(1,3));
		// i18n[userscript.propsDlgCheck=Check]
		btnCheck = new JButton(s_stringMgr.getString("userscript.propsDlgCheck"));
		pnl3.add(btnCheck);
		// i18n[userscript.propsDlgOk=OK]
		btnOk = new JButton(s_stringMgr.getString("userscript.propsDlgOk"));
		pnl3.add(btnOk);
		// i18n[userscript.propsDlgCancel=Cancel]
		btnCancel = new JButton(s_stringMgr.getString("userscript.propsDlgCancel"));
		pnl3.add(btnCancel);
		getContentPane().add(pnl3);

		lblScriptClass.setPreferredSize(new Dimension(lblScriptClass.getPreferredSize().width + 5, lblScriptClass.getPreferredSize().height));
		lblName.setPreferredSize(new Dimension(lblScriptClass.getPreferredSize().width, lblScriptClass.getPreferredSize().height));


		setSize(300, 120);
	}
}
