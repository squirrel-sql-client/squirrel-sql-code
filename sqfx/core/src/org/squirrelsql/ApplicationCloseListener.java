package org.squirrelsql;

public interface ApplicationCloseListener
{
   public enum FireTime
   {
      WITHIN_SESSION_FIRE_TIME,
      AFTER_SESSION_FIRE_TIME
   }


   public void applicationClosing();
}
