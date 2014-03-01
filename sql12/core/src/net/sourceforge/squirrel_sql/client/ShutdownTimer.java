package net.sourceforge.squirrel_sql.client;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/*
 * Copyright (C) 2011 Rob Manning
 * manningr@users.sourceforge.net
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
 * Shutdown thread that can be used to shutdown the application after a certain number of seconds.
 */
public class ShutdownTimer implements Runnable, IShutdownTimer
{ 
	
	/** Logger for this class. */
	private static ILogger s_log = LoggerController.createLogger(ShutdownTimer.class);	
	
	/** Number of seconds to wait before initiating shutdown */
	private int _shutdownSeconds;
	
	/** The app implementation that can perform a clean shutdown */
	private IApplication _app;
	
	public ShutdownTimer() {
	}
	
	/**
	 * @see net.sourceforge.squirrel_sql.client.IShutdownTimer#start()
	 */
	public void start() {
		Thread t = new Thread(this, "ShutdownTimerThread");
		t.start();
	}

	@Override
	public void run()
	{
		try
		{
			Thread.sleep(_shutdownSeconds * 1000);
		}
		catch (InterruptedException e)
		{
			s_log.error("Shutdown timer thread was interrupted unexpectedly: "+e.getMessage(), e);
		}
		
		if (s_log.isInfoEnabled()) {
			s_log.info("ShutdownTimer is shutting down the application");
		} 
		
		System.exit(_app.shutdown(false) ? 0 : 1);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.IShutdownTimer#setShutdownSeconds(int)
	 */
	public void setShutdownSeconds(int shutdownSeconds)
	{
		_shutdownSeconds = shutdownSeconds;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.IShutdownTimer#setApplication(net.sourceforge.squirrel_sql.client.IApplication)
	 */
	public void setApplication(IApplication app)
	{
		_app = app;
	}
}
