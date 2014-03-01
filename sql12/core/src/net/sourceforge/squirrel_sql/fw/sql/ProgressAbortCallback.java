/*
 * Copyright (C) 2011 Stefan Willinger
 * wis775@users.sourceforge.net
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
package net.sourceforge.squirrel_sql.fw.sql;

/**
 * A monitor for long running operations.
 * The scope of this monitor is to give feedback to the user and provide the opportunity to abort the operation.
 * This interface is a conjunction of {@link ProgressCallBack} and {@link IAbortController}
 * @author Stefan Willinger
 * @see ProgressCallBack
 * @see IAbortController
 */
public interface ProgressAbortCallback extends ProgressCallBack, IAbortController {
	/**
	 * Display the status of the current running sub-task.
	 * @param status status to display.
	 */
	void setTaskStatus(String status);

	/**
	 * Tells the progress bar, that the operation is finished.
	 * Unnoticed, how many tasks are realy done. 
	 */
	void setFinished();

}
