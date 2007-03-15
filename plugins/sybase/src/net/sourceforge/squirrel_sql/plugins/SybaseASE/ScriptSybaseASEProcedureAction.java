package net.sourceforge.squirrel_sql.plugins.SybaseASE;

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

public class ScriptSybaseASEProcedureAction extends SquirrelAction
{
   private ISession _session;


   public ScriptSybaseASEProcedureAction(IApplication app, Resources rsrc, ISession session)
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
            // SybaseASE specific code to read procedure definitions.
            String sql =
                "Select text " +
                "from sysobjects inner join syscomments on syscomments.id = sysobjects.id " +
                "where name = '" + pi.getSimpleName().replace(";1", "") + "'";

             ResultSet res = stat.executeQuery(sql);

             while(res.next())
             {
                 script.append(res.getString("text"));            
             }
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
