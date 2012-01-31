package net.sourceforge.squirrel_sql.plugins.hibernate.util;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.util.ExceptionFormatter;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.hibernate.HQLPanelController;
import net.sourceforge.squirrel_sql.plugins.hibernate.server.SquirrelHibernateServerException;

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
         if (t instanceof SquirrelHibernateServerException)
         {
            SquirrelHibernateServerException serverException = (SquirrelHibernateServerException) t;

            ret = serverException.getOriginalMessage();

            if(false == serverException.getOriginalMessage().equals(serverException.getExceptionToString()))
            {
               ret += "\n" + serverException.getExceptionToString();
            }
            
         }
         else
         {
            ret = formatter.format(t);
         }

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
            isHibernateException(t))
      {
         // If this is not a hibernate error we write a log entry
         s_log.error(t);
      }

      return ret;
   }

   private static boolean isHibernateException(Throwable t)
   {
      String className;
      if (t instanceof SquirrelHibernateServerException)
      {
         className = ((SquirrelHibernateServerException)t).getOriginalExceptionClassName();
      }
      else
      {
         className = t.getClass().getName();
      }

      return (-1 == className.toLowerCase().indexOf("hibernate") && -1 == className.toLowerCase().indexOf("antlr"));
   }
}
