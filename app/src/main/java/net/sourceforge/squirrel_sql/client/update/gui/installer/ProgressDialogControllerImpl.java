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

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * Progress dialog controller that shows, updates and hides a single progress bar dialog.
 */
public class ProgressDialogControllerImpl implements ProgressDialogController
{
	/** the dialog being displayed */
	private JDialog currentDialog = null;

	/** The message that appears in the dialog just above the detail message */
	private JLabel currentMessage = null;

	/** The message that appears in the dialog just above the progress bar */
	private JLabel detailMessage = null;
	
	/** the progress bar */
	private JProgressBar currentProgressBar = null;

	/** Logger for this class. */
	private static ILogger s_log = LoggerController.createLogger(ProgressDialogControllerImpl.class);
	
	/**
	 * @see net.sourceforge.squirrel_sql.client.update.gui.installer.ProgressDialogController#hideProgressDialog()
	 */
	public void hideProgressDialog()
	{
		s_log.info("Hiding dialog");
		GUIUtils.processOnSwingEventThread(new Runnable()
		{
			public void run()
			{
				currentDialog.setVisible(false);
			}
		}, true);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.update.gui.installer.ProgressDialogController#incrementProgress()
	 */
	public void incrementProgress()
	{
		s_log.info("incrementing progress");
		GUIUtils.processOnSwingEventThread(new Runnable()
		{
			public void run()
			{
				int currentValue = currentProgressBar.getValue();
				currentProgressBar.setValue(currentValue + 1);
			}
		}, true);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.update.gui.installer.ProgressDialogController#setDetailMessage(java.lang.String)
	 */
	public void setDetailMessage(final String msg)
	{
		s_log.info("Setting detail message: "+msg);
		GUIUtils.processOnSwingEventThread(new Runnable()
		{
			public void run()
			{
				detailMessage.setText(msg);
			}
		}, true);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.update.gui.installer.ProgressDialogController#showProgressDialog(java.lang.String,
	 *      java.lang.String, int)
	 */
	public void showProgressDialog(final String title, final String msg, final int total)
	{
		s_log.info("showing progress dialog");
		GUIUtils.processOnSwingEventThread(new Runnable()
		{
			public void run()
			{
				currentDialog = new JDialog((Frame) null, title);
				currentMessage = new JLabel(msg);
				detailMessage = new JLabel("...");
				currentProgressBar = new JProgressBar(0, total - 1);
				
				JPanel panel = new JPanel(new BorderLayout());
				JPanel messagePanel = new JPanel(new GridLayout(2,1));
				messagePanel.add(currentMessage);
				messagePanel.add(detailMessage);
				panel.add(messagePanel, BorderLayout.CENTER);
				panel.add(currentProgressBar, BorderLayout.SOUTH);
				
				currentDialog.getContentPane().add(panel);
				currentDialog.setSize(300, 100);
				GUIUtils.centerWithinScreen(currentDialog);
				currentDialog.setVisible(true);
			}
		}, true);

	}

	public void resetProgressDialog(final String title, final String msg, final int total)
	{
		GUIUtils.processOnSwingEventThread(new Runnable() {
			public void run() {
				currentDialog.setTitle(title);
				currentMessage.setText(msg);
				currentProgressBar.setValue(0);
				currentProgressBar.setMinimum(0);
				currentProgressBar.setMaximum(total);
			}
		});
		
	}

}
