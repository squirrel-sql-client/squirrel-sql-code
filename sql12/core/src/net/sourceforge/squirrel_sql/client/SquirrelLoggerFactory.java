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
import java.io.File;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.Log4jLoggerFactory;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class SquirrelLoggerFactory extends Log4jLoggerFactory
{

	public SquirrelLoggerFactory(FileAppender fa, boolean doStartupLogging)
	{
		super(false);
		initialize(fa, doStartupLogging);
	}

	public SquirrelLoggerFactory(boolean doStartupLogging)
	{
		try
		{ 
			SquirrelAppender fa = new SquirrelAppender();
			initialize(fa, doStartupLogging);
		}
		catch (Exception e)
		{
			final ILogger log = createLogger(getClass());
			log.error("Error occurred configuring logging. Now logging to standard output", e);
			BasicConfigurator.configure();
		}

	}

	private void initialize(FileAppender fa, boolean doStartupLogging)
	{
		String configFileName = ApplicationArguments.getInstance().getLoggingConfigFileName();
		if (configFileName != null && isFileAccessible(new File(configFileName)))
		{
			PropertyConfigurator.configure(configFileName);
		}
		else
		{
			File squirrelHomeDir = new ApplicationFiles().getSquirrelHomeDir();
			File defaultLogConfigFile = new File(squirrelHomeDir, "log4j.properties");
			if (isFileAccessible(defaultLogConfigFile))
			{
				PropertyConfigurator.configure(defaultLogConfigFile.getAbsolutePath());
			}
			else
			{
				Properties props = new Properties();
				props.setProperty("log4j.rootLogger", "debug, SquirrelAppender");
				props.setProperty("log4j.appender.SquirrelAppender", "net.sourceforge.squirrel_sql.client.SquirrelFileSizeRollingAppender");
				props.setProperty("log4j.appender.SquirrelAppender.layout", "org.apache.log4j.PatternLayout");
				props.setProperty("log4j.appender.SquirrelAppender.layout.ConversionPattern", "%d{ISO8601} [%t] %-5p %c %x - %m%n");

				PropertyConfigurator.configure(props);
			}
		}
		if (doStartupLogging)
		{
			doStartupLogging();
		}
	}

	private static boolean isFileAccessible(File configFile)
	{
		return configFile != null && configFile.exists() && configFile.isFile() && configFile.canRead();
	}

	private void doStartupLogging()
	{
		final ILogger log = createLogger(getClass());
		log.info("#############################################################################################################");
		log.info("# Starting " + Version.getVersion() + " at " + DateFormat.getInstance().format(new Date()));
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