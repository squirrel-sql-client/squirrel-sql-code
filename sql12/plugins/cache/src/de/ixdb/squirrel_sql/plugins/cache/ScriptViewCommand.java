package de.ixdb.squirrel_sql.plugins.cache;

import com.intersys.cache.jbind.JBindDatabase;
import com.intersys.objects.CacheDatabase;
import com.intersys.objects.CacheQuery;
import com.intersys.objects.Database;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;

import java.awt.event.ActionEvent;
import java.sql.ResultSet;
import java.util.Vector;

public class ScriptViewCommand
{
   private ISession _session;

   private static final String PREFIX_SQLUSER = "SQLUser.";

   public ScriptViewCommand(ISession session)
	{
      _session = session;
   }



	public void execute()
	{
      try
      {

         String[] selectedViews = getSelectedViews();


         Database cacDb =  (JBindDatabase) CacheDatabase.getDatabase(_session.getSQLConnection().getConnection());
         StringBuffer script = new StringBuffer();

         for (int i = 0; i < selectedViews.length; i++)
         {
            CacheQuery qry = new CacheQuery(cacDb, "%Library.SQLCatalog", "SQLViewInfo");
            ResultSet viewInfo = qry.execute(selectedViews[i]);

            viewInfo.next();
            script.append("CREATE VIEW ").append(selectedViews[i].substring(PREFIX_SQLUSER.length())).append(" AS\n");
            script.append(viewInfo.getString(1)).append(getStatementSeparator()).append("\n");
            viewInfo.close();

         }

         _session.getSessionInternalFrame().getSQLPanelAPI().appendSQLScript(script.toString());
         _session.selectMainTab(ISession.IMainPanelTabIndexes.SQL_TAB);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   private String[] getSelectedViews()
   {
      IDatabaseObjectInfo[] dbObjs = _session.getSessionInternalFrame().getObjectTreeAPI().getSelectedDatabaseObjects();

      Vector ret = new Vector();
      for (int i = 0; i < dbObjs.length; i++)
      {
         if (dbObjs[i] instanceof ITableInfo)
         {
            ITableInfo ti = (ITableInfo) dbObjs[i];
            String sTable = PREFIX_SQLUSER + ti.getSimpleName();
            ret.add(sTable);
         }
      }
      return (String[]) ret.toArray(new String[0]);
   }

   private String getStatementSeparator()
   {
      String statementSeparator = _session.getProperties().getSQLStatementSeparator();

      if (1 < statementSeparator.length())
      {
         statementSeparator = "\n" + statementSeparator + "\n";
      }

      return statementSeparator;
   }


}
