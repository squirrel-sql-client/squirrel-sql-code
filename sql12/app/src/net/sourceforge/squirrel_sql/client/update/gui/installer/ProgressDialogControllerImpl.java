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

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;

/**
 * Progress dialog controller that shows, updates and hides a single progress bar dialog.
 */
public class ProgressDialogControllerImpl implements ProgressDialogController
{
	/** the dialog being displayed */
	private JDialog currentDialog = null;
	
	/** The message that appears in the dialog above the progress bar */
	private JLabel currentMessage = null;
	
	/** the progress bar */
	private JProgressBar currentProgressBar = null;
	
	/**
	 * @see net.sourceforge.squirrel_sql.client.update.gui.installer.ProgressDialogController#hideProgressDialog()
	 */
	public void hideProgressDialog()
	{
		currentDialog.setVisible(false);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.update.gui.installer.ProgressDialogController#incrementProgress()
	 */
	public void incrementProgress()
	{
		GUIUtils.processOnSwingEventThread(new Runnable() {
			public void run()
			{
				int currentValue = currentProgressBar.getValue();
				currentProgressBar.setValue(currentValue+1);
			}
		});		
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.update.gui.installer.ProgressDialogController#setMessage(java.lang.String)
	 */
	public void setMessage(final String msg)
	{
		GUIUtils.processOnSwingEventThread(new Runnable() {
			public void run()
			{
				currentMessage.setText(msg);
				currentProgressBar.setString(msg);
			}
		});
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.update.gui.installer.ProgressDialogController#showProgressDialog(java.lang.String, java.lang.String, int)
	 */
	public void showProgressDialog(final String title, final String msg, final int total)
	{
		currentDialog = new JDialog((Frame)null, title);
		currentMessage = new JLabel(msg);
		
		currentProgressBar = new JProgressBar(0, total-1);
		
		
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(5, 0, 5, 0);
		panel.add(currentMessage,gbc);
		
		gbc.gridy = 1;
		panel.add(currentProgressBar,gbc);
		
		currentDialog.getContentPane().add(panel);
		currentDialog.setSize(300, 150);
		currentDialog.setVisible(true);
	}

	
}
