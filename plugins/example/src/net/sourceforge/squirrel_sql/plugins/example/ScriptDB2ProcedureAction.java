package net.sourceforge.squirrel_sql.plugins.example;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.gui.session.SessionInternalFrame;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.IProcedureInfo;
import net.sourceforge.squirrel_sql.fw.util.Resources;

import java.awt.event.ActionEvent;
import java.sql.ResultSet;
import java.sql.Statement;

public class ScriptDB2ProcedureAction extends SquirrelAction
{
   private ISession _session;


   public ScriptDB2ProcedureAction(IApplication app, Resources rsrc, ISession session)
	{
		super(app, rsrc);
      _session = session;
   }


	public void actionPerformed(ActionEvent evt)
	{
      try
      {

         IDatabaseObjectInfo[] dbObjs = _session.getSessionInternalFrame().getObjectTreeAPI().getSelectedDatabaseObjects();

         Statement stat = _session.getSQLConnection().createStatement();


         StringBuffer script = new StringBuffer();
         for (int i = 0; i < dbObjs.length; i++)
         {
            IProcedureInfo pi = (IProcedureInfo) dbObjs[i];

            ///////////////////////////////////////////////////////////
            // IBM DB 2 specific code to read procedure definitions.
            String sql =
               "SELECT TEXT " +
               "FROM SYSIBM.SYSPROCEDURES " +
               "WHERE PROCNAME = '" + pi.getSimpleName() + "'";


            ResultSet res = stat.executeQuery(sql);
            res.next();
            res.getString("TEXT");


            script.append(res.getString("TEXT"));
            script.append(getStatementSeparator());
            res.close();
            //
            ///////////////////////////////////////////////////////////
         }
         stat.close();

         SessionInternalFrame sessMainFrm = _session.getSessionInternalFrame();
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
