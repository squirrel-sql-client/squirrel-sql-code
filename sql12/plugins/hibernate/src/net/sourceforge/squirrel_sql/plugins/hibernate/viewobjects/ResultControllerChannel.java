package net.sourceforge.squirrel_sql.plugins.hibernate.viewobjects;

public class ResultControllerChannel
{
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
}
