package org.squirrelsql.services;

import org.squirrelsql.AppState;

import java.sql.SQLException;

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
      GuiUtils.executeOnEDT(() -> _warning(s, t));
   }

   private void _warning(String s, Throwable t)
   {
      try
      {
         if (MessageHandlerDestination.MESSAGE_LOG == _dest)
         {
            AppState.get().getStatusBarCtrl().warning(s, t);
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
      catch (Throwable tmh)
      {
         handlErrorInMessaging(s, t, tmh);
      }
   }

   public void info(String s)
   {
      GuiUtils.executeOnEDT(() -> _info(s));
   }

   private  void _info(String s)
   {
      try
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
      catch (Throwable tmh)
      {
         handlErrorInMessaging(s, null, tmh);
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
      GuiUtils.executeOnEDT(() -> _error(s, t));
   }

   private void _error(String s, Throwable t)
   {
      try
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
      catch (Throwable tmh)
      {
         handlErrorInMessaging(s, t, tmh);
      }
   }

   private void handlErrorInMessaging(String msg, Throwable originalError, Throwable errorFromMessageHandler)
   {
      System.err.println("### Error occurred in message/error handling. We provide the following information in the following order:");
      System.err.println("### 1. The original message that failed to be handled");
      System.err.println("### 2. The original error that failed to be handled");
      System.err.println("### 3. The error that occurred during message/error handling");
      System.err.println("### Here we go:");
      System.err.println("### ");
      System.err.println("### 1. The original message that failed to be handled:");

      if(Utils.isEmptyString(msg))
      {
         System.err.println("### <MESSAGE WAS NULL>");
      }
      else
      {
         System.err.println(msg);
      }

      System.err.println("### ");
      System.err.println("### 2. The original error that failed to be handled:");

      if(null == originalError)
      {
         System.err.println("### <ERROR WAS NULL>");
      }
      else
      {
         originalError.printStackTrace(System.err);
      }

      System.err.println("### ");
      System.err.println("### 3. The error that occurred during message/error handling");

      errorFromMessageHandler.printStackTrace(System.err);


   }

   public String errorSQLNoStack(SQLException e)
   {
      return AppState.get().getMessagePanelCtrl().errorSQLNoStack(e);
   }
}
