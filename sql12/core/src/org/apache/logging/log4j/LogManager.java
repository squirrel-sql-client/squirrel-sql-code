package org.apache.logging.log4j;

public class LogManager
{
   public static Logger getLogger(final Class<?> clazz)
   {
      return new LoggerImpl();
   }
}
