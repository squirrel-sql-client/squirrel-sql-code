package net.sourceforge.squirrel_sql.fw.gui;
/*
 * Copyright (C) 2001 Johan Companger
 * jcompagner@j-com.nl
 *
 * Modifications copyright (C) 2001 Colin Bell
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
public class GUIExecuter implements Runnable {
	
	private boolean _bStopThread = false;
	private boolean _bStopExecution = false;
	private IGUIExecutionControllerCallback _callback;
	
	public GUIExecuter(IGUIExecutionControllerCallback callback) throws IllegalArgumentException {
		super();
		if (callback == null) {
			throw new IllegalArgumentException("Null IGUIExecutionControllerCallback passed");
		}
		_callback = callback;
	}
	
	public void run() {
		while (!_bStopThread) {
			IGUIExecutionTask task = null;
			synchronized (_callback) {
				_callback.incrementFreeThreadCount();
				while(!_bStopThread) {
					_bStopExecution = false;
					task = _callback.nextTask();
					if(task != null) {
						_callback.decrementFreeThreadCount();
						break;
					} else {
						try {
							_callback.wait();
						} catch(InterruptedException ignore){
						}
					}
				}
			}
			if(task != null) {
				try {
					task.execute();
				} catch (Exception ex) {
					_callback.showMessage(ex);
				}
			}
		}
	}
}

