package net.sourceforge.squirrel_sql.fw.util.log;

public class SQLLogCounts
{
   long _errorCount;
   long _warningCount;
   long _infoCount;

   public long getErrorCount()
   {
      return _errorCount;
   }

   public long getWarningCount()
   {
      return _warningCount;
   }

   public long getInfoCount()
   {
      return _infoCount;
   }

   public void incInfo()
   {
      ++_infoCount;
   }

   public void incWarning()
   {
      ++_warningCount;
   }

   public void incError()
   {
      ++_errorCount;
   }
}
