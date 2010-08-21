package net.sourceforge.squirrel_sql.fw.util.log;
/*
 * Copyright (C) 2001 Colin Bell
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
import org.apache.log4j.BasicConfigurator;

import java.util.Vector;

public class Log4jLoggerFactory implements ILoggerFactory
{
	private ILoggerListener _listenerOfAllLoggers;
	private Vector<ILoggerListener> _listeners =new Vector<ILoggerListener>();

	public Log4jLoggerFactory()
	{
		this(true);
	}

	public void addLoggerListener(ILoggerListener l)
	{
		_listeners.add(l);
	}

	public void removeLoggerListener(ILoggerListener l)
	{
		_listeners.remove(l);
	}



	public Log4jLoggerFactory(boolean doBasicConfig)
	{
		_listenerOfAllLoggers = new ILoggerListener()
		{
			public void info(Class<?> source, Object message)
			{
				try
				{
					ILoggerListener[] listeners = _listeners.toArray(new ILoggerListener[_listeners.size()]);
					for (int i = 0; i < listeners.length; i++)
					{
						listeners[i].info(source, message);
					}
				}
				catch (Throwable t)
				{
					// No exceptions during logging
				}
			}

			public void info(Class<?> source, Object message, Throwable th)
			{
				try
				{
					ILoggerListener[] listeners = _listeners.toArray(new ILoggerListener[_listeners.size()]);
					for (int i = 0; i < listeners.length; i++)
					{
						listeners[i].info(source, message, th);
					}
				}
				catch (Throwable t)
				{
					// No exceptions during logging
				}
			}

			public void warn(Class<?> source, Object message)
			{
				try
				{
					ILoggerListener[] listeners = _listeners.toArray(new ILoggerListener[_listeners.size()]);
					for (int i = 0; i < listeners.length; i++)
					{
						listeners[i].warn(source, message);
					}
				}
				catch (Throwable t)
				{
					// No exceptions during logging
				}
			}

			public void warn(Class<?> source, Object message, Throwable th)
			{
				try
				{
					ILoggerListener[] listeners = _listeners.toArray(new ILoggerListener[_listeners.size()]);
					for (int i = 0; i < listeners.length; i++)
					{
						listeners[i].warn(source, message, th);
					}
				}
				catch (Throwable t)
				{
					// No exceptions during logging
				}
			}

			public void error(Class<?> source, Object message)
			{
				try
				{
					ILoggerListener[] listeners = _listeners.toArray(new ILoggerListener[_listeners.size()]);
					for (int i = 0; i < listeners.length; i++)
					{
						listeners[i].error(source, message);
					}
				}
				catch (Throwable t)
				{
					// No exceptions during logging
				}
			}

			public void error(Class<?> source, Object message, Throwable th)
			{
				try
				{
					ILoggerListener[] listeners = _listeners.toArray(new ILoggerListener[_listeners.size()]);
					for (int i = 0; i < listeners.length; i++)
					{
						listeners[i].error(source, message, th);
					}
				}
				catch (Throwable t)
				{
					// No exceptions during logging
				}
			}
		};

		if (doBasicConfig)
		{
			BasicConfigurator.configure();
		}
	}

	public ILogger createLogger(Class<?> clazz)
	{
		return new Log4jLogger(clazz, _listenerOfAllLoggers);
	}

	public void shutdown()
	{
	}
}
