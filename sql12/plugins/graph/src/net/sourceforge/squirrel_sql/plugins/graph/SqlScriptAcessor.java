package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;

import javax.swing.*;


public class SqlScriptAcessor
{
   public static void scriptTablesToSQLEntryArea(ISession session, ITableInfo[] tableInfos)
   {
      ScriptInterface si = (ScriptInterface) session.getApplication().getPluginManager().bindExternalPluginService("sqlscript", ScriptInterface.class);
      if (null == si)
      {
         String msg = "Scripting is only available with the SQL Scripts Plugin.\n Get the plugin from www.squirrelsql.org. It's free.";
         JOptionPane.showMessageDialog(session.getApplication().getMainFrame(), msg);
         return;
      }

      si.scriptTablesToSQLEntryArea(session, tableInfos);
   }
}
