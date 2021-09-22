package net.sourceforge.squirrel_sql.fw.timeoutproxy;

import net.sourceforge.squirrel_sql.client.Main;

public class TimeOutUtil
{
   public static long getMetaDataLoadingTimeOutOfActiveSession()
   {
      long metaDataLoadingTimeOut = Main.getApplication().getSquirrelPreferences().getSessionProperties().getMetaDataLoadingTimeOut();

      if(   false == Main.getApplication().getSessionManager().isInCreateSession()
            && null != Main.getApplication().getSessionManager().getActiveSession()) // Happens when testing or loading Schema table for a new Alias that has not yet been saved.
      {
         metaDataLoadingTimeOut = Main.getApplication().getSessionManager().getActiveSession().getProperties().getMetaDataLoadingTimeOut();
      }
      return metaDataLoadingTimeOut;
   }
}
