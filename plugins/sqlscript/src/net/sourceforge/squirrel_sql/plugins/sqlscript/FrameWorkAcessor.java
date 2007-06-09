package net.sourceforge.squirrel_sql.plugins.sqlscript;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.gui.session.BaseSessionInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SessionInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.ObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringManager;


/**
 * This class was introduced to make the plugin compilable for the time it takes
 * to completely introduce the multible session windows framework.
 * It may be removed after that.
 */
public class FrameWorkAcessor
{

	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(FrameWorkAcessor.class);

	public static ISQLPanelAPI getSQLPanelAPI(ISession session, SQLScriptPlugin plugin)
	{
		// old version before multible sesssion windows
		//return session.getSQLPanelAPI(plugin);

		if(session.getActiveSessionWindow() instanceof ObjectTreeInternalFrame)
		{
			// i18n[sqlscript.scriptWritten=Script was written to the SQL editor of the main session window.]
			session.showMessage(s_stringMgr.getString("sqlscript.scriptWritten"));
			return session.getSessionSheet().getSQLPaneAPI();
		}
		else
		{
			return session.getSQLPanelAPIOfActiveSessionWindow();
		}
	}

	public static IObjectTreeAPI getObjectTreeAPI(ISession session, SQLScriptPlugin sqlScriptPlugin)
	{
		// old version
		//return session.getObjectTreeAPI(sqlScriptPlugin);

		return session.getObjectTreeAPIOfActiveSessionWindow();
	}
}
