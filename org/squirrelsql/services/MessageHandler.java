package org.squirrelsql.services;

import org.squirrelsql.AppState;

import java.net.URISyntaxException;

public class MessageHandler
{
   private Class _clazz;
   private MessageHandlerDestination _dest;

   public MessageHandler(Class clazz, MessageHandlerDestination dest)
   {
      _clazz = clazz;
      _dest = dest;
   }

   public void warning(String s)
   {
      warning(s, null);
   }

   public void warning(String s, Throwable t)
   {
      if (MessageHandlerDestination.MESSAGE_LOG == _dest)
      {
         AppState.get().getStatusBarCtrl().warning(s);
      }
      else if (MessageHandlerDestination.MESSAGE_PANEL == _dest)
      {
         AppState.get().getMessagePanelCtrl().warning(s, t);
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

   public void error(String s)
   {
      error(s, null);
   }


   public void error(Throwable t)
   {
      error(null, t);
   }

   public void error(String s, Throwable t)
   {
      if (MessageHandlerDestination.MESSAGE_LOG == _dest)
      {
         AppState.get().getStatusBarCtrl().error(s, t);
      }
      else if (MessageHandlerDestination.MESSAGE_PANEL == _dest)
      {
         AppState.get().getMessagePanelCtrl().error(s, t);
      }
      else
      {
         throw new UnsupportedOperationException("Unkonwn destination: " + _dest);
      }
   }
}
