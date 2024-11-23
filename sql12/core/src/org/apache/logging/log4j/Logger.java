package org.apache.logging.log4j;

public interface Logger
{
   LogBuilder atTrace();

   LogBuilder atDebug();

   LogBuilder atInfo();

   LogBuilder atWarn();

   LogBuilder atError();

   LogBuilder atFatal();

   LogBuilder always();

   LogBuilder atLevel(Level level);


}
