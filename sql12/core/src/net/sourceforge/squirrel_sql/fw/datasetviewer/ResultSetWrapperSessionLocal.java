package net.sourceforge.squirrel_sql.fw.datasetviewer;

public class ResultSetWrapperSessionLocal
{
   private boolean _callingIsLastFailed;

   public void setCallingIsLastFailed()
   {
      _callingIsLastFailed = true;
   }

   public boolean isCallingIsLastFailed()
   {
      return _callingIsLastFailed;
   }
}
