package org.squirrelsql.splash;

import org.squirrelsql.services.MessageHandler;
import org.squirrelsql.services.MessageHandlerDestination;

import java.awt.*;

public class SquirrelSplashScreen
{

   private SplashStringWriter _splashStringWriter;

   public SquirrelSplashScreen(int numberOffCallsToindicateNewTask)
   {
      SplashScreen splashScreen = SplashScreen.getSplashScreen();

      if(null == splashScreen)
      {
         new MessageHandler(getClass(), MessageHandlerDestination.MESSAGE_LOG).error("No SplashScreen available", new NullPointerException("No SplashScreen available. Please check VM parameter -splash:"));
         return;
      }
      _splashStringWriter = new SplashStringWriter(splashScreen, numberOffCallsToindicateNewTask);
   }


   private void indicateLoadingFile(final String filename)
   {
      if(null == _splashStringWriter)
      {
         return;
      }

      _splashStringWriter.writeLowerProgressLine(filename);
   }

   public void indicateNewTask(final String text)
   {
      if(null == _splashStringWriter)
      {
         return;
      }

      _splashStringWriter.writeUpperProgressLine(text);
   }

   public void close()
   {
      SplashScreen.getSplashScreen().close();
   }
}
