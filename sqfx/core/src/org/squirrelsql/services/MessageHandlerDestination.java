package org.squirrelsql.services;

public enum MessageHandlerDestination
{
   MESSAGE_LOG, MESSAGE_PANEL, MESSAGE_LOG_AND_PANEL;

   public boolean matches(MessageHandlerDestination dest)
   {
      return dest == MESSAGE_LOG_AND_PANEL || dest == this;
   }
}
