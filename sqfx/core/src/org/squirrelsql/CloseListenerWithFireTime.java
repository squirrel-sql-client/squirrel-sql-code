package org.squirrelsql;

/**
 * Created by gerd on 07.06.14.
 */
public class CloseListenerWithFireTime
{
   private final ApplicationCloseListener _listener;
   private final ApplicationCloseListener.FireTime _fireTime;

   public CloseListenerWithFireTime(ApplicationCloseListener listener, ApplicationCloseListener.FireTime fireTime)
   {
      _listener = listener;
      _fireTime = fireTime;
   }

   public ApplicationCloseListener getListener()
   {
      return _listener;
   }

   public ApplicationCloseListener.FireTime getFireTime()
   {
      return _fireTime;
   }
}
