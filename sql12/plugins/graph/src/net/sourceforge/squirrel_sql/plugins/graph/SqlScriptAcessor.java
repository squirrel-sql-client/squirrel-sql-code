package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;


public class SqlScriptAcessor
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(SqlScriptAcessor.class);

	public static void scriptTablesToSQLEntryArea(ISession session, ITableInfo[] tableInfos)
   {
      ScriptInterface si = (ScriptInterface) session.getApplication().getPluginManager().bindExternalPluginService("sqlscript", ScriptInterface.class);
      if (null == si)
      {
			// i18n[graph.scriptPlugNeeded=Scripting is only available with the SQL Scripts Plugin.\nGet the plugin from www.squirrelsql.org. It's free.]
			String msg = s_stringMgr.getString("graph.scriptPlugNeeded");
         JOptionPane.showMessageDialog(session.getApplication().getMainFrame(), msg);
         return;
      }

      si.scriptTablesToSQLEntryArea(session, tableInfos);
   }
}
