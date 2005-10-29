package net.sourceforge.squirrel_sql.plugins.userscript.kernel;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.plugins.userscript.UserScriptPlugin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

public class ScriptPropertiesController
{

	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(ScriptPropertiesController.class);

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
			// i18n[userscript.classNotFound=Class {0} not found]
			msg = s_stringMgr.getString("userscript.classNotFound", m_dlg.txtScriptClass.getText());
		}
		else
		{
			String params[] = new String[]{m_dlg.txtScriptClass.getText(), classUrl.getFile()};
			// i18n[userscript.classNotFoundIn=Class {0} found in\n{1}]
			msg = s_stringMgr.getString("userscript.classNotFoundIn", params);
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
			// i18n[userscript.enterScriptName=Please enter a script name]
			JOptionPane.showMessageDialog(m_owner, s_stringMgr.getString("userscript.enterScriptName"));
			return;
		}
		if(null == m_dlg.txtScriptClass.getText() || "".equals(m_dlg.txtScriptClass.getText().trim()))
		{
			// i18n[userscript.enterScriptClass=Please enter a script class]
			JOptionPane.showMessageDialog(m_owner, s_stringMgr.getString("userscript.enterScriptClass"));
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
