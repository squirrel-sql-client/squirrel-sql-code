package net.sourceforge.squirrel_sql.plugins.example;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.gui.session.SessionInternalFrame;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.util.Resources;

import java.awt.event.ActionEvent;
import java.sql.ResultSet;
import java.sql.Statement;

public class ScriptDB2ViewAction extends SquirrelAction
{
   private ISession _session;


   public ScriptDB2ViewAction(IApplication app, Resources rsrc, ISession session)
	{
		super(app, rsrc);
      _session = session;
   }


	public void actionPerformed(ActionEvent evt)
	{
      try
      {
         Statement stat = _session.getSQLConnection().createStatement();

         SessionInternalFrame sessMainFrm = _session.getSessionInternalFrame();
         IDatabaseObjectInfo[] dbObjs = sessMainFrm.getObjectTreeAPI().getSelectedDatabaseObjects();


         StringBuffer script = new StringBuffer();
         for (int i = 0; i < dbObjs.length; i++)
         {
            ITableInfo ti = (ITableInfo) dbObjs[i];

            ///////////////////////////////////////////////////////////
            // IBM DB 2 specific code to read view definitions.
            String sql =
               "SELECT TEXT " +
               "FROM SYSIBM.SYSVIEWS " +
               "WHERE NAME = '" + ti.getSimpleName() + "'";

            ResultSet res = stat.executeQuery(sql);
            res.next();

            script.append(res.getString("TEXT"));
            script.append(getStatementSeparator());
            res.close();
            //
            ///////////////////////////////////////////////////////////
         }

         stat.close();

         sessMainFrm.getSQLPanelAPI().appendSQLScript(script.toString());
         sessMainFrm.getSessionPanel().selectMainTab(ISession.IMainPanelTabIndexes.SQL_TAB);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   private String getStatementSeparator()
   {
      String statementSeparator = _session.getQueryTokenizer().getSQLStatementSeparator();

      if (1 < statementSeparator.length())
      {
         statementSeparator = "\n" + statementSeparator + "\n";
      }

      return statementSeparator;
   }


}
