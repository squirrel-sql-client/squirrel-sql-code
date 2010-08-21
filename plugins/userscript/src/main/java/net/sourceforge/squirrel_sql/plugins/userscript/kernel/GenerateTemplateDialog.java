package net.sourceforge.squirrel_sql.plugins.userscript.kernel;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;

public class GenerateTemplateDialog extends JDialog
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(GenerateTemplateDialog.class);

	JTextArea txtCodeTemplate;
	JButton btnSave;

	GenerateTemplateDialog(JFrame owner)
	{
		// i18n[userscript.codeTemplate=Script code template]
		super(owner, s_stringMgr.getString("userscript.codeTemplate"), false);

		getContentPane().setLayout(new BorderLayout());
		txtCodeTemplate = new JTextArea();
		txtCodeTemplate.setTabSize(3);
		getContentPane().add(new JScrollPane(txtCodeTemplate), BorderLayout.CENTER);
		// i18n[userscript.codeTemplateSave=Save]
		btnSave = new JButton(s_stringMgr.getString("userscript.codeTemplateSave"));
		getContentPane().add(btnSave, BorderLayout.SOUTH);

		setSize(400, 400);
	}
}
