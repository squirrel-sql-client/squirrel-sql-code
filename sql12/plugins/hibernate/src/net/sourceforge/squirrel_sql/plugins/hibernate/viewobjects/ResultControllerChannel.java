package net.sourceforge.squirrel_sql.plugins.hibernate.viewobjects;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class ResultControllerChannel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ResultControllerChannel.class);

   private ResultControllerChannelListener _resultControllerChannelListener;

   public void projectionDisplayModeChanged()
   {
      if(null != _resultControllerChannelListener)
      {
         _resultControllerChannelListener.projectionDisplayModeChanged();
      }
   }

   public void setActiveControllersListener(ResultControllerChannelListener resultControllerChannelListener)
   {
      _resultControllerChannelListener = resultControllerChannelListener;
   }

   public void findIfTableDisplay()
   {
      boolean tableFindSupported = false;
      if(null != _resultControllerChannelListener)
      {
         tableFindSupported = _resultControllerChannelListener.findIfTableDisplay();
      }

      if(false == tableFindSupported)
      {
         Main.getApplication().getMessageHandler().showWarningMessage(s_stringMgr.getString("ResultControllerChannel.no.table.find.supported"));
      }
   }
}
