package net.sourceforge.squirrel_sql.plugins.userscript.kernel;

import javax.swing.*;
import java.awt.*;

public class GenerateTemplateDialog extends JDialog
{
	JTextArea txtCodeTemplate;
	JButton btnSave;

	GenerateTemplateDialog(JFrame owner)
	{
		super(owner, "Script code template", false);

		getContentPane().setLayout(new BorderLayout());
		txtCodeTemplate = new JTextArea();
		txtCodeTemplate.setTabSize(3);
		getContentPane().add(new JScrollPane(txtCodeTemplate), BorderLayout.CENTER);
		btnSave = new JButton("Save");
		getContentPane().add(btnSave, BorderLayout.SOUTH);

		setSize(400, 400);
	}
}
