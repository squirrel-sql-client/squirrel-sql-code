package net.sourceforge.squirrel_sql.fw.util;
/*
 * Copyright (C) 2001 Johan Companger
 * jcompagner@j-com.nl
 *
 * Modifications copyright (C) 2001 Colin Bell
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
 * This is the code called by a thread in the thead pool that
 * handles the executing of tasks.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 * @author <A HREF="mailto:jcompagner@j-com.nl">Johan Companger</A>
 */
class TaskExecuter implements Runnable
{
	private boolean _bStopThread = false;
//	private boolean _bStopExecution = false;
	private ITaskThreadPoolCallback _callback;

	TaskExecuter(ITaskThreadPoolCallback callback)
		throws IllegalArgumentException
	{
		super();
		if (callback == null)
		{
			throw new IllegalArgumentException("Null IGUIExecutionControllerCallback passed");
		}
		_callback = callback;
	}

	public void run()
	{
		while (!_bStopThread)
		{
			Runnable task = null;
			synchronized (_callback)
			{
				_callback.incrementFreeThreadCount();
				while (!_bStopThread)
				{
//					_bStopExecution = false;
					task = _callback.nextTask();
					if (task != null)
					{
						_callback.decrementFreeThreadCount();
						break;
					}
					else
					{
						try
						{
							_callback.wait();
						}
						catch (InterruptedException ignore)
						{
							// Ignore
						}
					}
				}
			}
			if (task != null)
			{
				try
				{
					task.run();
				}
				catch (Throwable th)
				{
					_callback.showMessage(th);
				}
			}
		}
	}
}
