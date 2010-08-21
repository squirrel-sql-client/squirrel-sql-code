package net.sourceforge.squirrel_sql.plugins.userscript.kernel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;

public class GenerateTemplateController
{
	private GenerateTemplateDialog m_dlg;
	GenerateTemplateController(JFrame owner)
	{
		m_dlg = new GenerateTemplateDialog(owner);
		GUIUtils.centerWithinParent(m_dlg);
		m_dlg.setVisible(true);

		m_dlg.txtCodeTemplate.setText(TemplateCode.CODE);

		m_dlg.btnSave.addActionListener
		(
			new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{onSave();}
			}
		);
	}

	private void onSave()
	{
		try
		{
			JFileChooser fc = new JFileChooser();
			if(JFileChooser.APPROVE_OPTION == fc.showSaveDialog(m_dlg))
			{
				FileOutputStream fos = new FileOutputStream(fc.getSelectedFile());
				fos.write(m_dlg.txtCodeTemplate.getText().getBytes());
				fos.flush();
				fos.close();
			}
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
}
