package de.ixdb.squirrel_sql.plugins.cache;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.IProcedureInfo;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.StringTokenizer;
import java.util.Vector;

public class ScriptCdlCommand
{
   private ISession _session;
   private CachePlugin _plugin;

   private static ILogger s_log;

   public ScriptCdlCommand(ISession session, CachePlugin plugin)
   {
      _session = session;
      _plugin = plugin;
      if (ScriptCdlCommand.s_log == null)
      {
         ScriptCdlCommand.s_log = LoggerController.createLogger(getClass());
      }
   }

   public void execute()
   {
      String names = getSelectedNames();

      if(null == names || 0 == names.trim().length())
      {
         return;
      }

      String cdl = CdlAccessor.getDefinition(names, _session.getSQLConnection().getConnection());

      _session.getSessionInternalFrame().getSQLPanelAPI().appendSQLScript("\n" + cdl + "\n");
      _session.selectMainTab(ISession.IMainPanelTabIndexes.SQL_TAB);

   }


   private String getSelectedNames()
   {
      IDatabaseObjectInfo[] dbObjs = _session.getSessionInternalFrame().getObjectTreeAPI().getSelectedDatabaseObjects();

      return CdlAccessor.getSearchStringForCdlAccess(dbObjs);
   }

}
