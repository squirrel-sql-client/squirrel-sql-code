package net.sourceforge.squirrel_sql.fw.util.log;
/*
 * Copyright (C) 2001-2002 Colin Bell
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
import org.apache.log4j.Logger;

/**
 * This is a logger that logs using the Apache log4j package.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class Log4jLogger implements ILogger
{
	/** Log4j logger to log to. */
	private Logger _log;

	/**
	 * Ctor specifying the object requesting the logger. A
	 * Log4J <TT>Logger</TT> is created using as a name the
	 * fully qualified class of <TT>requester</TT>.
	 *
	 * @param	clazz	The class requesting a logger.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>clazz</TT> is <TT>null</TT>.
	 */
	public Log4jLogger(Class clazz)
	{
		if (clazz == null)
		{
			throw new IllegalArgumentException("Empty requesterClass passed");
		}

		_log = Logger.getLogger(clazz);
	}

	/**
	 * @see ILogger#debug(Object)
	 */
	public void debug(Object message)
	{
		_log.debug(message);
	}

	/**
	 * @see ILogger#debug(Object, Throwable)
	 */
	public void debug(Object message, Throwable th)
	{
		_log.debug(message, th);
	}

	/**
	 * @see ILogger#info(Object)
	 */
	public void info(Object message)
	{
		_log.info(message);
	}

	/**
	 * @see ILogger#info(Object, Throwable)
	 */
	public void info(Object message, Throwable th)
	{
		_log.info(message, th);
	}

	/**
	 * @see ILogger#warn(Object)
	 */
	public void warn(Object message)
	{
		_log.warn(message);
	}

	/**
	 * @see ILogger#warn(Object, Throwable)
	 */
	public void warn(Object message, Throwable th)
	{
		_log.warn(message, th);
	}

	/**
	 * @see ILogger#error(Object)
	 */
	public void error(Object message)
	{
		_log.error(message);
	}

	/**
	 * @see ILogger#error(Object, Throwable)
	 */
	public void error(Object message, Throwable th)
	{
		_log.error(message, th);
	}

	/**
	 * @see ILogger#isDebugEnabled()
	 */
	public boolean isDebugEnabled()
	{
		return _log.isDebugEnabled();
	}

	/**
	 * @see ILogger#isInfoEnabled()
	 */
	public boolean isInfoEnabled()
	{
		return _log.isInfoEnabled();
	}
}