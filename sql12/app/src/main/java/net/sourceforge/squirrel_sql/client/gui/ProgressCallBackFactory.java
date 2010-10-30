package net.sourceforge.squirrel_sql.client.gui;

import java.awt.Frame;

import javax.swing.JDialog;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.ProgressCallBack;

/*
 * Copyright (C) 2010 Rob Manning
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
 * A factory that creates a ProgressCallBack on the Swing Event Dispatch Thread. 
 */
public class ProgressCallBackFactory implements IProgressCallBackFactory
{
	/**
	 * @see net.sourceforge.squirrel_sql.client.gui.IProgressCallBackFactory#create(java.awt.Frame,
	 *      java.lang.String, int)
	 */
	@Override
	public ProgressCallBack create(Frame owner, String title, int totalItems)
	{
		ProgressCallBackCreationTask task = new ProgressCallBackCreationTask(owner, title, totalItems);
		GUIUtils.processOnSwingEventThread(task, true);
		return task.getResult();
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.gui.IProgressCallBackFactory#
	 * create(javax.swing.JDialog, java.lang.String, int)
	 */
	@Override
	public ProgressCallBack create(JDialog owner, String title, int totalItems)
	{
		ProgressCallBackCreationTask task = new ProgressCallBackCreationTask(owner, title, totalItems);
		GUIUtils.processOnSwingEventThread(task, true);
		return task.getResult();
	}
	
	
	/**
	 * This runnable is used by the factory methods above to create the ProgressCallBackDialog.  This allows
	 * this task to be performed on the event dispatch thread, and afterwards the ProgressCallBackDialog can 
	 * be obtained using getResult().
	 */
	private class ProgressCallBackCreationTask implements Runnable
	{

		private ProgressCallBack result = null;
		
		private final String title;

		private final int totalItems;

		private Frame frameOwner;

		private JDialog dialogOwner;
		
		public ProgressCallBackCreationTask(Frame owner, String title, int totalItems)
		{
			this.frameOwner = owner;
			this.title = title;
			this.totalItems = totalItems;
		}

		public ProgressCallBackCreationTask(JDialog owner, String title, int totalItems)
		{
			this.dialogOwner = owner;
			this.title = title;
			this.totalItems = totalItems;
		}

		@Override
		public void run()
		{
			if (frameOwner != null) {
				result = new ProgressCallBackDialog(frameOwner, title, totalItems);
			} else {
				result = new ProgressCallBackDialog(dialogOwner, title, totalItems);
			}
		}

		/**
		 * @return the result
		 */
		public ProgressCallBack getResult()
		{
			return result;
		}

	}
}
