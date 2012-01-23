package net.sourceforge.squirrel_sql.plugins.hibernate.server;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: gerd
 * Date: 22.01.12
 * Time: 20:19
 */
public class SquirrelHibernateServerException extends RuntimeException implements Serializable
{
   private String _originalMessage;
   private String _originalExceptionClassName;

   public SquirrelHibernateServerException(String messageIncludingOriginalStackTrace, String originalMessage, String originalExceptionClassName)
   {
      super(messageIncludingOriginalStackTrace);
      _originalMessage = originalMessage;
      _originalExceptionClassName = originalExceptionClassName;
   }

   @Override
   public String getMessage()
   {
      return _originalMessage;
   }

   public String getOriginalExceptionClassName()
   {
      return _originalExceptionClassName;
   }
}
