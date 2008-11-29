package net.sourceforge.squirrel_sql;

import net.sourceforge.squirrel_sql.client.ApplicationManager;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import org.apache.log4j.Level;

import utils.EasyMockHelper;

public class BaseSQuirreLJUnit4TestCase
{

	protected EasyMockHelper mockHelper = new EasyMockHelper();

	static {
		LoggerController.setForceDebug(true);
	}
	
	public BaseSQuirreLJUnit4TestCase()
	{
		ApplicationManager.initApplication();
		StringManager.setTestMode(true);
	}

	@SuppressWarnings("unchecked")
	protected static void disableLogging(Class c)
	{
		ILogger s_log = LoggerController.createLogger(c);
		s_log.setLevel(Level.OFF);
	}

	@SuppressWarnings("unchecked")
	protected static void debugLogging(Class c)
	{
		ILogger s_log = LoggerController.createLogger(c);
		s_log.setLevel(Level.DEBUG);
	}

}
