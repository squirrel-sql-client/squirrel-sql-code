package net.sourceforge.squirrel_sql.plugins.userscript.kernel;

import javax.swing.*;
import java.awt.*;

public class ScriptPropertiesDialog extends JDialog
{
	JTextField txtName;
	JTextField txtScriptClass;
	JCheckBox chkShowInStandard;
	JButton btnOk;
	JButton btnCancel;
	JButton btnCheck;


	public ScriptPropertiesDialog(Frame owner)
	{
		super(owner, "Script properties", false);

      getContentPane().setLayout(new GridLayout(4,1));

		JPanel pnl1 = new JPanel(new BorderLayout());
		JLabel lblName = new JLabel("Name");
		pnl1.add(lblName, BorderLayout.WEST);
		txtName = new JTextField();
		pnl1.add(txtName, BorderLayout.CENTER);
		getContentPane().add(pnl1);


		JPanel pnl2 = new JPanel(new BorderLayout());
		JLabel lblScriptClass = new JLabel("Script class");
		pnl2.add(lblScriptClass, BorderLayout.WEST);
		txtScriptClass = new JTextField();
		pnl2.add(txtScriptClass, BorderLayout.CENTER);
		getContentPane().add(pnl2);

		chkShowInStandard = new JCheckBox("Show in standard menues");
		getContentPane().add(chkShowInStandard);

		JPanel pnl3 = new JPanel();
		pnl3.setLayout(new GridLayout(1,3));
		btnCheck = new JButton("Check");
		pnl3.add(btnCheck);
		btnOk = new JButton("OK");
		pnl3.add(btnOk);
		btnCancel = new JButton("Cancel");
		pnl3.add(btnCancel);
		getContentPane().add(pnl3);

		lblScriptClass.setPreferredSize(new Dimension(lblScriptClass.getPreferredSize().width + 5, lblScriptClass.getPreferredSize().height));
		lblName.setPreferredSize(new Dimension(lblScriptClass.getPreferredSize().width, lblScriptClass.getPreferredSize().height));


		setSize(300, 120);
	}
}
