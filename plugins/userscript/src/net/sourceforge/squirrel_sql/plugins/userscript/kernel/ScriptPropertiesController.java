package net.sourceforge.squirrel_sql.plugins.userscript.kernel;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.plugins.userscript.UserScriptPlugin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

public class ScriptPropertiesController
{
	private ScriptPropsListener m_scriptPropsListener;

	private Script m_script;
	private boolean m_isNewScript;
	private ScriptPropertiesDialog m_dlg;
	private Frame m_owner;
	private UserScriptPlugin m_plugin;


	public ScriptPropertiesController(Frame owner, UserScriptPlugin plugin)
	{
      this(owner, null, plugin);
	}

	public ScriptPropertiesController(Frame owner, Script script, UserScriptPlugin plugin)
	{
		m_owner = owner;
		m_plugin = plugin;
		m_dlg = new ScriptPropertiesDialog(owner);
		GUIUtils.centerWithinParent(m_dlg);
		m_dlg.setVisible(true);

		if(null == script)
		{
			m_script = new Script();
			m_isNewScript = true;
		}
		else
		{
			m_script = script;
			m_dlg.txtName.setText(m_script.getName());
			m_dlg.txtScriptClass.setText(m_script.getScriptClass());
			m_dlg.chkShowInStandard.setSelected(m_script.isShowInStandard());
			m_isNewScript = false;
		}

		m_dlg.btnCheck.addActionListener
		(
			new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{onCheck();}
			}
		);


		m_dlg.btnOk.addActionListener
		(
			new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{onOK();}
			}
		);

		m_dlg.btnCancel.addActionListener
		(
			new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{close();}
			}
		);

	}

	private void onCheck()
	{
      URL classUrl = findClass(m_dlg.txtScriptClass.getText());

		String msg;
		if(null == classUrl)
		{
			msg = "Class " + m_dlg.txtScriptClass.getText() + " not found";
		}
		else
		{
			msg = "Class " + m_dlg.txtScriptClass.getText() + " found in\n" + classUrl.getFile();
		}

		JOptionPane.showMessageDialog(m_owner, msg);
	}

	public URL findClass(final String className)
	{
		return m_plugin.getUserScriptClassLoader().getResource(asResourceName(className));
	}

	protected static String asResourceName(String resource)
	{
		resource = resource.replace('.', '/');
		resource = resource + ".class";
		return resource;
	}


	private void onOK()
	{
      if(null == m_dlg.txtName.getText() || "".equals(m_dlg.txtName.getText().trim()))
		{
			JOptionPane.showMessageDialog(m_owner, "Please enter a script name");
			return;
		}
		if(null == m_dlg.txtScriptClass.getText() || "".equals(m_dlg.txtScriptClass.getText().trim()))
		{
			JOptionPane.showMessageDialog(m_owner, "Please enter a script class");
			return;
		}

		m_script.setName(m_dlg.txtName.getText());
		m_script.setScriptClass(m_dlg.txtScriptClass.getText());
		m_script.setShowInStandard(m_dlg.chkShowInStandard.isSelected());

		close();

		if(m_isNewScript)
		{
			m_scriptPropsListener.newScript(m_script);
		}
		else
		{
			m_scriptPropsListener.scriptEdited(m_script);
		}
	}

	private void close()
	{
		m_dlg.setVisible(false);
		m_dlg.dispose();
	}

	public void setScriptPropsListener(ScriptPropsListener scriptPropsListener)
	{
		m_scriptPropsListener = scriptPropsListener;
	}
}
