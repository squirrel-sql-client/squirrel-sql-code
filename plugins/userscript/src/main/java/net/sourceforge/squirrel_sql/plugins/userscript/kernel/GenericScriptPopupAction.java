package net.sourceforge.squirrel_sql.plugins.userscript.kernel;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class GenericScriptPopupAction extends AbstractAction
{
	private Script m_script;
	private UserScriptAdmin m_admin;
	private boolean m_targetType;

	public GenericScriptPopupAction(Script script, UserScriptAdmin admin, boolean targetType)
	{
		super(script.getName());
		m_script = script;
		m_admin = admin;
		m_targetType = targetType;
	}

	public void actionPerformed(ActionEvent e)
	{
		m_admin.executeScript(m_admin.getSession().getApplication().getMainFrame(), m_script, m_admin.getTargets(m_targetType));
	}
}
