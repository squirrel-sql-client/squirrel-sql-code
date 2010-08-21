/*
 * Copyright (C) 2008 Rob Manning
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
package net.sourceforge.squirrel_sql.client.update.gui.installer;

/**
 * Interface for separating the display of progress for a long running task from the task that is updating it.
 * This helps keeps UI code out of the business logic code.
 */
public interface ProgressDialogController
{

	/**
	 * Shows a progress bar with the specified title and message, with the number of increments set to total.
	 * 
	 * @param title
	 *           the title of the dialog
	 * @param msg
	 *           the message to display in the body of the dialog
	 * @param total
	 *           the number of increments the bar is made of
	 */
	void showProgressDialog(String title, String msg, int total);

	/**
	 * Updates the detail message in the progress bar dialog
	 * 
	 * @param msg a new detail message for the body of the dialog 
	 */
	void setDetailMessage(String msg);

	/**
	 * Increments the currently displayed progress bar
	 */
	void incrementProgress();

	void resetProgressDialog(String title, String msg, int total);
	
	/**
	 * Hides the currently displayed progress bar
	 */
	void hideProgressDialog();
}
