package org.squirrelsql.services;

import org.squirrelsql.AppState;

public class MessageHandler
{
   private Class _clazz;
   private MessageHandlerDestination _dest;

   public MessageHandler(Class clazz, MessageHandlerDestination dest)
   {
      _clazz = clazz;
      _dest = dest;
   }

   public void error(String s)
   {
      if (MessageHandlerDestination.MESSAGE_LOG == _dest)
      {
         AppState.get().getStatusBarCtrl().error(s);
      }
      else if (MessageHandlerDestination.MESSAGE_PANEL == _dest)
      {
         AppState.get().getMessagePanelCtrl().error(s);
      }
      else
      {
         throw new UnsupportedOperationException("Unkonwn destination: " + _dest);
      }
   }

   public void warning(String s)
   {
      if (MessageHandlerDestination.MESSAGE_LOG == _dest)
      {
         AppState.get().getStatusBarCtrl().warning(s);
      }
      else if (MessageHandlerDestination.MESSAGE_PANEL == _dest)
      {
         AppState.get().getMessagePanelCtrl().warning(s);
      }
      else
      {
         throw new UnsupportedOperationException("Unkonwn destination: " + _dest);
      }
   }

   public void info(String s)
   {
      if (MessageHandlerDestination.MESSAGE_LOG == _dest)
      {
         AppState.get().getStatusBarCtrl().info(s);
      }
      else if (MessageHandlerDestination.MESSAGE_PANEL == _dest)
      {
         AppState.get().getMessagePanelCtrl().info(s);
      }
      else
      {
         throw new UnsupportedOperationException("Unkonwn destination: " + _dest);
      }
   }

   public void error(Throwable t)
   {
      if (MessageHandlerDestination.MESSAGE_LOG == _dest)
      {
         AppState.get().getStatusBarCtrl().error(t);
      }
      else if (MessageHandlerDestination.MESSAGE_PANEL == _dest)
      {
         AppState.get().getMessagePanelCtrl().error(t);
      }
      else
      {
         throw new UnsupportedOperationException("Unkonwn destination: " + _dest);
      }
   }
}
