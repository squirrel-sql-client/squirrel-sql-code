package net.sourceforge.squirrel_sql.client.gui;
/*
 * Copyright (C) 2001-2006 Colin Bell
 * colbell@users.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.fw.util.ClassLoaderListener;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import java.awt.*;

public class SquirrelSplashScreen
{
   private final static ILogger s_log = LoggerController.createLogger(SquirrelSplashScreen.class);


   private SplashStringWriter _splashStringWriter;

   public SquirrelSplashScreen(SquirrelPreferences prefs, int numberOffCallsToindicateNewTask)
   {
      SplashScreen splashScreen = SplashScreen.getSplashScreen();

      if(null == splashScreen)
      {
         s_log.error("No SplashScreen availabe", new NullPointerException("No SplashScreen availabe. Please check VM parameter -splash:"));
         return;
      }
      _splashStringWriter = new SplashStringWriter(splashScreen, prefs.getShowPluginFilesInSplashScreen(), numberOffCallsToindicateNewTask);
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

   public ClassLoaderListener getClassLoaderListener()
   {
      return new ClassLoaderListener()
      {
         public void loadedZipFile(String filename)
         {
            indicateLoadingFile(filename);
         }

         public void finishedLoadingZipFiles()
         {
            indicateLoadingFile(null);
         }
      };
   }
}
