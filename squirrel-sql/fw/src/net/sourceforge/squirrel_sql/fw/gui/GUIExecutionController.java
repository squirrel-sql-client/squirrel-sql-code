package net.sourceforge.squirrel_sql.fw.gui;
/*
 * Copyright (C) 2001 Johan Companger
 * jcompagner@j-com.nl
 *
 * Modification copyright (C) 2001 Colin Bell
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
import java.util.ArrayList;
import java.util.List;

public class GUIExecutionController {
	// Count of available or free threads.
	private int _iFree;

	// Total number of threads.
	private int _threadCount;

	private List _tasks = new ArrayList();

	private MyCallback _callback = new MyCallback();
	
	/**
	 * Default ctor.
	 */
	public GUIExecutionController() {
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
	public GUIExecutionController(int maxThreads) throws IllegalArgumentException {
		super();
		if (maxThreads < 0) {
			throw new IllegalArgumentException("Negative maxThreads passed");
		}
	}

	public synchronized void addTask(IGUIExecutionTask task) throws IllegalArgumentException {
		if (task == null) {
			throw new IllegalArgumentException("Null GUIExecuterTask passed");
		}
		_tasks.add(task);
		// Should there me a Max Number of threads?
		if (_iFree == 0) {
			Thread th = new Thread(new GUIExecuter(_callback));
			th.setPriority(Thread.MIN_PRIORITY);//??
			th.setDaemon(true);
			th.start();
			++_threadCount;
System.out.println("Creating thread nbr: " + _threadCount);
		} else {
			synchronized (_callback) {
System.out.println("Reusing existing thread");
				_callback.notify();
			}
		}
	}
	
	private final class MyCallback implements IGUIExecutionControllerCallback {
		public void incrementFreeThreadCount() {
			++_iFree;
System.out.println("++_iFree: " + _iFree);
		}

		public void decrementFreeThreadCount() {
			--_iFree;
System.out.println("--_iFree: " + _iFree);
		}
	
		public synchronized IGUIExecutionTask nextTask() {
			if (_tasks.size() > 0) {
				return (IGUIExecutionTask)_tasks.remove(0);
			}
			return null;
		}

		public void showMessage(Exception ex) {
			ex.printStackTrace(System.out);	//??Show to user
		}
	}
}

