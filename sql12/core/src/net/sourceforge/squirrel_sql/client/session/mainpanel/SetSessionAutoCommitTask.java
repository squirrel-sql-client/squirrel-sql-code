package net.sourceforge.squirrel_sql.client.session.mainpanel;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;


/**
 * TODO: This task may be made a direct, i.e. non threaded call.
 */
public class SetSessionAutoCommitTask implements Runnable
{
   private static final ILogger s_log = LoggerController.createLogger(SetSessionAutoCommitTask.class);

   private ISession _session;

   public SetSessionAutoCommitTask(ISession session)
   {
      _session = session;
   }

   public void run()
   {
      final ISQLConnection conn = _session.getSQLConnection();
      final SessionProperties props = _session.getProperties();
      if (conn != null)
      {
         boolean auto = true;
         try
         {
            auto = conn.getAutoCommit();
         }
         catch (Exception ex)
         {
            s_log.error("Error with transaction control", ex);
            _session.showErrorMessage(ex);
         }
         try
         {
            //conn.setAutoCommit(props.getAutoCommit());
            _session.getConnectionPool().setSessionAutoCommit(props.getAutoCommit());
         }
         catch (Exception ex)
         {
            props.setAutoCommit(auto);
            _session.showErrorMessage(ex);
         }
      }
   }
}
