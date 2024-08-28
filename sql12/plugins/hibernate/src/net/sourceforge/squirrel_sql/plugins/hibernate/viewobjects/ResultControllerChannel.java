package net.sourceforge.squirrel_sql.plugins.hibernate.viewobjects;

public class ResultControllerChannel
{
   private ResultControllerChannelListener _resultControllerChannelListener;

   public void typedValuesDisplayModeChanged()
   {
      if(null != _resultControllerChannelListener)
      {
         _resultControllerChannelListener.typedValuesDisplayModeChanged();
      }
   }

   public void setActiveControllersListener(ResultControllerChannelListener resultControllerChannelListener)
   {
      _resultControllerChannelListener = resultControllerChannelListener;
   }
}
