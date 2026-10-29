package net.sourceforge.squirrel_sql.fw.util.log;

public class LogCountLimit
{
   private final ILogger _logger;
   private final int _maxLogCount;

   private int _logCount;

   public LogCountLimit(ILogger logger, int maxLogCount)
   {
      _logger = logger;
      _maxLogCount = maxLogCount;
   }

   public void warn(String logString)
   {
      ++_logCount;
      if(_logCount < _maxLogCount)
      {
         _logger.warn(logString);
      }
   }
}
