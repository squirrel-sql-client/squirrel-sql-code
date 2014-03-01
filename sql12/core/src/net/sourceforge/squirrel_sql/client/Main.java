package net.sourceforge.squirrel_sql.client;

import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.util.log.SystemOutToLog;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.PrintStream;
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
/**
 * Application entry point.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class Main
{
   /** Logger for this class. */
   private static ILogger s_log;

   private static Application application;
   
	/**
	 * Default ctor. private as class should never be instantiated.
	 */
	private Main()
	{
		super();
	}

	/**
	 * Application entry point.
	 *
	 * @param	args	Arguments passed on command line.
	 */
	public static void main(String[] args) throws IOException
   {
		if (ApplicationArguments.initialize(args))
		{

         if(false == Version.supportsUsedJDK())
         {
            JOptionPane.showMessageDialog(null, Version.getUnsupportedJDKMessage());
            System.exit(-1);
         }

			final ApplicationArguments appArgs = ApplicationArguments.getInstance();
			if (appArgs.getShowHelp())
			{
				appArgs.printHelp();
			}
			else
			{
            startApp();
         }
		}
	}

   private static void startApp() throws IOException
   {
      LoggerController.registerLoggerFactory(new SquirrelLoggerFactory(true));
      s_log = LoggerController.createLogger(Main.class);

      System.setErr(new PrintStream(new SystemOutToLog(System.err)));
      System.setOut(new PrintStream(new SystemOutToLog(System.out)));




      EventQueue q = Toolkit.getDefaultToolkit().getSystemEventQueue();

      q.push(new EventQueue()
      {
         OutOfMemoryErrorHandler oumErrorHandler = new OutOfMemoryErrorHandler();

         protected void dispatchEvent(AWTEvent event)
         {
            try
            {
               super.dispatchEvent(event);
            }
            catch (Throwable e)
            {
               doLogging(event, e);
               doOutOfMemory(event, e, oumErrorHandler);
            }
         }

      });


      Runnable runnable = new Runnable()
      {
         

		public void run()
         {
            application = new Application();
            application.startup();
         }
      };

      SwingUtilities.invokeLater(runnable);
   }

   private static void doLogging(AWTEvent event, Throwable t)
   {
      if (s_log.isDebugEnabled())
      {
         t.printStackTrace();
      }
      s_log.error("Exception occured dispatching Event " + event, t);
   }

   private static void doOutOfMemory(AWTEvent event, Throwable e, OutOfMemoryErrorHandler oumErrorHandler)
   {
      if (Utilities.getDeepestThrowable(e) instanceof OutOfMemoryError)
      {
         try
         {
        	// We have to set the application by a lazy way, because it is created in a runnable. 
            oumErrorHandler.setApplication(application);
            oumErrorHandler.handleOutOfMemoryError();
         }
         catch (Throwable t)
         {
            doLogging(event, t);
         }
      }
   }

}