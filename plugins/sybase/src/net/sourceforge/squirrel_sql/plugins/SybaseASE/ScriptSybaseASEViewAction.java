package net.sourceforge.squirrel_sql.plugins.SybaseASE;

import java.awt.event.ActionEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.gui.session.SessionInternalFrame;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;



public class ScriptSybaseASEViewAction extends SquirrelAction
{
    private static final long serialVersionUID = 1L;

    transient private ISession _session;
   
    /** Logger for this class. */
    transient private final ILogger s_log =
        LoggerController.createLogger(ScriptSybaseASEViewAction.class);
    
   ///////////////////////////////////////////////////////////
   // Sybase ASE specific code to read view definitions.
   private static final String sql =
       "Select text " +
       "from sysobjects inner join syscomments on syscomments.id = sysobjects.id " +
       "where name = ?";


   public ScriptSybaseASEViewAction(IApplication app, Resources rsrc, ISession session)
	{
		super(app, rsrc);
      _session = session;
   }


	public void actionPerformed(ActionEvent evt)
	{
      try
      {
         PreparedStatement stat = _session.getSQLConnection().prepareStatement(sql);

         SessionInternalFrame sessMainFrm = _session.getSessionInternalFrame();
         IDatabaseObjectInfo[] dbObjs = sessMainFrm.getObjectTreeAPI().getSelectedDatabaseObjects();


         StringBuffer script = new StringBuffer();
         for (int i = 0; i < dbObjs.length; i++)
         {
            ITableInfo ti = (ITableInfo) dbObjs[i];

            stat.setString(1, ti.getSimpleName());
            if (s_log.isDebugEnabled()) {
                s_log.debug("Running SQL: "+sql);
                s_log.debug("Bind var value is: "+ti.getSimpleName());
            }
            ResultSet res = stat.executeQuery();

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

         sessMainFrm.getSQLPanelAPI().appendSQLScript(script.toString());
         sessMainFrm.getSessionPanel().selectMainTab(ISession.IMainPanelTabIndexes.SQL_TAB);
      }
      catch (Exception e)
      {
         s_log.error(
             "Unexpected exception while attempting to get source for view: "+
             e.getMessage(), e);
         
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
