package net.sourceforge.squirrel_sql.fw.util.log;
/*
 * Copyright (C) 2001 Colin Bell
 * colbell@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

public class LoggerController {

	private static ILoggerFactory s_factory = new Log4jLoggerFactory();

	public static void registerLoggerFactory(ILoggerFactory factory) {
		s_factory = factory != null ? factory : new Log4jLoggerFactory();
	}

/*
	public LoggerController(IApplication app) {
		String logFileName = app.getApplicationFiles().getExecutionLogFile().getPath();
		try {
			FileAppender fa = new FileAppender(new PatternLayout(PatternLayout.TTCC_CONVERSION_PATTERN), logFileName);
			fa.setFile(logFileName);
			BasicConfigurator.configure(fa);
		} catch (IOException ex) {
			BasicConfigurator.configure();
		}
*/
/*
		ILogger log = createLogger(LoggerFactory.class);
		log.info("=======================================================");
		log.info("=======================================================");
		log.info("=======================================================");
		log.info(Version.getVersion() + " started: " + Calendar.getInstance().getTime());
		log.info(Version.getCopyrightStatement());
		log.info("java.vendor:       " + System.getProperty("java.vendor"));
		log.info("java.version:      " + System.getProperty("java.version"));
		log.info("java.runtime.name: " + System.getProperty("java.runtime.name"));
		log.info("os.name:           " + System.getProperty("os.name"));
		log.info("os.version:        " + System.getProperty("os.version"));
		log.info("os.arch:           " + System.getProperty("os.arch"));
		log.info("user.dir:          " + System.getProperty("user.dir"));
		log.info("user.home:         " + System.getProperty("user.home"));
		log.info("java.home:         " + System.getProperty("java.home"));
		log.info("java.class.path:   " + System.getProperty("java.class.path"));
*/
	//}

	public static ILogger createLogger(Class clazz) {
		return s_factory.createLogger(clazz);
	}
}

