package net.sourceforge.squirrel_sql.client;
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
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.text.DateFormat;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Category;
import org.apache.log4j.PropertyConfigurator;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.Log4jLoggerFactory;

public class SquirrelLoggerFactory extends Log4jLoggerFactory
{
	public SquirrelLoggerFactory(boolean doStartupLogging) throws IllegalArgumentException
	{
		super(false);
		String configFileName = ApplicationArguments.getInstance().getLoggingConfigFileName();
		if (configFileName != null)
		{
			PropertyConfigurator.configure(configFileName);
		}
		else
		{
			Category.getRoot().removeAllAppenders();
			try
			{
//				final String logFileName = new ApplicationFiles().getExecutionLogFile().getPath();
//				final PatternLayout layout = new PatternLayout("%-4r [%t] %-5p %c %x - %m%n");
//				FileAppender fa = new FileAppender(layout, logFileName);
//				fa.setFile(logFileName);
				SquirrelAppender fa = new SquirrelAppender();

				BasicConfigurator.configure(fa);
				final ILogger log = createLogger(getClass());
				log.info("No logger configuration file passed on command line arguments. Using default log file: "	+ fa.getFile() );
			}
			catch (IOException ex)
			{
				final ILogger log = createLogger(getClass());
				log.error("Error occured configuring logging. Now logging to standard output", ex);
				BasicConfigurator.configure();
			}
		}
		if (doStartupLogging) {
		   doStartupLogging();
		}
	}

	private void doStartupLogging()
	{
		final ILogger log = createLogger(getClass());
		log.info("#############################################################################################################");
		log.info("# Starting " + Version.getVersion()  + " at " + DateFormat.getInstance().format(new Date()));
		log.info("#############################################################################################################");
		log.info(Version.getVersion() + " started: " + Calendar.getInstance().getTime());
		log.info(Version.getCopyrightStatement());
		log.info("java.vendor: " + System.getProperty("java.vendor"));
		log.info("java.version: " + System.getProperty("java.version"));
		log.info("java.runtime.name: " + System.getProperty("java.runtime.name"));
		log.info("os.name: " + System.getProperty("os.name"));
		log.info("os.version: " + System.getProperty("os.version"));
		log.info("os.arch: " + System.getProperty("os.arch"));
		log.info("user.dir: " + System.getProperty("user.dir"));
		log.info("user.home: " + System.getProperty("user.home"));
		log.info("java.home: " + System.getProperty("java.home"));
		log.info("java.class.path: " + System.getProperty("java.class.path"));
	}
}