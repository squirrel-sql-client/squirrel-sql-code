package net.sourceforge.squirrel_sql.fw.util.log;

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

import net.sourceforge.squirrel_sql.client.ApplicationArguments;
import net.sourceforge.squirrel_sql.client.Version;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.LogManager;

public class SquirrelLoggerFactory extends SQLoggerFactoryBase
{
	public SquirrelLoggerFactory()
	{
		SQLoggerKernel.init();
		doStartupLogging();
	}

	private void doStartupLogging()
	{
		setAllLoggingFrameworkLoggersToWarning();

		final ILogger log = createLogger(getClass());
		log.info("#############################################################################################################");
		log.info("# Starting " + Version.getVersion() + " at " + DateFormat.getInstance().format(new Date()));
		log.info("#############################################################################################################");
		log.info(Version.getVersion() + " started: " + Calendar.getInstance().getTime());
		log.info(Version.getCopyrightStatement().replace('\n', ' '));
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

		if(false == StringUtilities.isEmpty(ApplicationArguments.getInstance().getLoggingConfigFileName(), true))
		{
			log.warn("The logging configuration file command line argument " +
						"(short option:" + ApplicationArguments.IOptions.LOG_FILE[0] + " / long option: " + ApplicationArguments.IOptions.LOG_FILE[1] + ") " +
						"is unused since Log4J was removed from SQuirreL. Please remove the argument from your SQuirreL start script.");
		}
	}

	/**
	 * Prevents logs from libs
	 */
	private void setAllLoggingFrameworkLoggersToWarning()
	{
		final Enumeration<String> loggerNames = LogManager.getLogManager().getLoggerNames();
		while (loggerNames.hasMoreElements())
		{
			LogManager.getLogManager().getLogger(loggerNames.nextElement()).setLevel(Level.WARNING);
		}
	}
}