package net.sourceforge.squirrel_sql.plugins.hibernate.server;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * NOTE: There is code here copied from somewhere else. Still don't call this functions from somewhere else. The Hibernate Server must remain independent of other libs.
 */
public class HibernateServerExceptionUtil
{
   public static Throwable prepareTransport(Throwable t)
   {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);

      Throwable deepestThrowable = getDeepestThrowable(t);
      deepestThrowable.printStackTrace(pw);

      pw.flush();
      sw.flush();

      String messageIncludingOriginalStackTrace = "Exception occurred on Hibernate Server Process: " + deepestThrowable.getMessage() + "\n";

      String stackTraceString = sw.toString();
      String deepestToString = deepestThrowable.toString();
      if(("" + deepestThrowable.getMessage()).equals(deepestToString) || stackTraceString.startsWith(deepestToString))
      {
         messageIncludingOriginalStackTrace += stackTraceString;
      }
      else
      {
         messageIncludingOriginalStackTrace +=  ( deepestToString + "\n" + stackTraceString);
      }


      return new SquirrelHibernateServerException(messageIncludingOriginalStackTrace, deepestThrowable.getMessage(), deepestToString, deepestThrowable.getClass().getName());
   }

   public static Throwable getDeepestThrowable(Throwable t)
   {
      Throwable parent = t;
      Throwable child = t.getCause();
      while(null != child)
      {
         parent = child;
         child = parent.getCause();
      }

      return parent;

   }
}
