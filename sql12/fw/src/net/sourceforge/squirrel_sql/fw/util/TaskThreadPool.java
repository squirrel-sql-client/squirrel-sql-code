package net.sourceforge.squirrel_sql.fw.util;
/*
 * Copyright (C) 2001 Johan Companger
 * jcompagner@j-com.nl
 *
 * Modification copyright (C) 2001 Colin Bell
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
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class TaskThreadPool {
	/** Logger for this class. */
	private static ILogger s_log = LoggerController.createLogger(TaskThreadPool.class);

	// Count of available or free threads.
	private int _iFree;

	// Total number of threads.
	private int _threadCount;

	private List _tasks = new ArrayList();

	private MyCallback _callback = new MyCallback();

	/**
	 * Default ctor.
	 */
	public TaskThreadPool() {
		this(0);
	}

	/**
	 * Ctor specifying the max number of threads.
	 *
	 * @param	maxThreads	Maximum number of threads. Zero means nomax.
	 *
	 * @throws	IllegalArgumentException
	 * 			If maxThreads < 0
	 */
	public TaskThreadPool(int maxThreads) throws IllegalArgumentException {
		super();
		if (maxThreads < 0) {
			throw new IllegalArgumentException("Negative maxThreads passed");
		}
	}

	/**
	 * Add a task to be executed by the next available thread
	 * in this thread pool. If there is no free thread available
	 * but the maximum number of threads hasn't been reached then
	 * create a new thread.
	 */
	public synchronized void addTask(Runnable task) throws IllegalArgumentException {
		if (task == null) {
			throw new IllegalArgumentException("Null Runnable passed");
		}
		_tasks.add(task);
		// Should there me a Max Number of threads?
		if (_iFree == 0) {
			Thread th = new Thread(new TaskExecuter(_callback));
			th.setPriority(Thread.MIN_PRIORITY);//??
			th.setDaemon(true);
			th.start();
			++_threadCount;
			s_log.debug("Creating thread nbr: " + _threadCount);
		} else {
			synchronized (_callback) {
				s_log.debug("Reusing existing thread");
				_callback.notify();
			}
		}
	}

	private final class MyCallback implements ITaskThreadPoolCallback {
		public void incrementFreeThreadCount() {
			++_iFree;
			s_log.debug("Returning thread. " + _iFree + " threads available");
		}

		public void decrementFreeThreadCount() {
			--_iFree;
			s_log.debug("Using a thread. " + _iFree + " threads available");
		}

		public synchronized Runnable nextTask() {
			if (_tasks.size() > 0) {
				return (Runnable)_tasks.remove(0);
			}
			return null;
		}

		//??Show to user
		public void showMessage(Throwable th) {
			s_log.error("Error", th);
		}
	}
}

