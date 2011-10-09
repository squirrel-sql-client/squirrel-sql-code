package net.sourceforge.squirrel_sql.plugins.hibernate.util;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.util.ExceptionFormatter;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.hibernate.HQLPanelController;

public class HqlQueryErrorUtil
{
   private static ILogger s_log = LoggerController.createLogger(HqlQueryErrorUtil.class);


   public static String handleHqlQueryError(Throwable e, ISession sess, boolean showInMessagePanel)
   {
      String ret = null;

      Throwable t = Utilities.getDeepestThrowable(e);
      ExceptionFormatter formatter = sess.getExceptionFormatter();
      try
      {
         ret = formatter.format(t);
         if (showInMessagePanel)
         {
            sess.showErrorMessage(ret);
         }
      }
      catch (Exception e1)
      {
         sess.showErrorMessage(e1);
         sess.showErrorMessage(t);
      }

      if (sess.getProperties().getWriteSQLErrorsToLog() ||
            (-1 == t.getClass().getName().toLowerCase().indexOf("hibernate") && -1 == t.getClass().getName().toLowerCase().indexOf("antlr")))
      {
         // If this is not a hibernate error we write a log entry
         s_log.error(t);
      }

      return ret;
   }
}
