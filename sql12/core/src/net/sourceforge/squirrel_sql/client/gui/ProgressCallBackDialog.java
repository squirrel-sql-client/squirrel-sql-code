package net.sourceforge.squirrel_sql.client.gui;

/*
 * Copyright (C) 2007 Rob Manning
 * manningr@user.sourceforge.net
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
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.ProgressCallBack;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * A simple class that can be used to show the user a dialog to indicate the progress of some task using the
 * ProgressCallBack interface. Since certain classes in fw module interact with the database and certain
 * operations can take quite a long time, letting the user know how it's going is nice. However, fw module
 * classes don't (and shouldn't) know anything about the UI as this is the responsibility of the app module
 * classes. So, this class can be passed in by app classes to certain fw long-running methods to bridge the
 * gap and provide feedback to the user.
 * 
 * @author manningr
 */
public class ProgressCallBackDialog extends JDialog implements ProgressCallBack
{

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/** Logger for this class. */
	public final static ILogger s_log = LoggerController.createLogger(ProgressCallBackDialog.class);

	/** Internationalized strings for this class */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(ProgressCallBackDialog.class);

	static interface i18n
	{
		// i18n[ProgressCallBackDialog.defaultLoadingPrefix=Loading:]
		String DEFAULT_LOADING_PREFIX = s_stringMgr.getString("ProgressCallBackDialog.defaultLoadingPrefix");

		// i18n[ProgressCallBackDialog.initialLoadingPrefix=Loading...]
		String INITIAL_LOADING_PREFIX = s_stringMgr.getString("ProgressCallBackDialog.initialLoadingPrefix");
	}

	private int itemCount = 0;

	private JProgressBar progressBar = null;

	private JLabel statusLabel = null;

	private String _loadingPrefix = i18n.DEFAULT_LOADING_PREFIX;

	/**
	 * Constructor which accepts a Dialog owner
	 * 
	 * @param owner
	 *           the owner Dialog from which the dialog is displayed or null if this dialog has no owner
	 * @param title
	 *           the String to display in the dialog's title bar
	 * @param totalItems
	 *           the total number of items at which point progress will indicate complete
	 */
	public ProgressCallBackDialog(Dialog owner, String title, int totalItems)
	{
		super(owner, title);
		init(totalItems);
	}

	/**
	 * Constructor which accepts a Frame owner
	 * 
	 * @param owner
	 *           the owner Frame from which the dialog is displayed or null if this dialog has no owner
	 * @param title
	 *           the String to display in the dialog's title bar
	 * @param totalItems
	 *           the total number of items at which point progress will indicate complete
	 */
	public ProgressCallBackDialog(Frame owner, String title, int totalItems)
	{
		super(owner, title);
		setLocationRelativeTo(owner);
		init(totalItems);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.sql.ProgressCallBack#setTotalItems(int)
	 */
	@Override
	public void setTotalItems(int totalItems)
	{
		itemCount = totalItems;
		progressBar.setMaximum(totalItems);
	}

	/**
	 * Sets the text that is displayed before each thing being loaded. By default this is the string
	 * "Loading:".
	 * 
	 * @param loadingPrefix
	 */
	@Override
	public void setLoadingPrefix(String loadingPrefix)
	{
		if (loadingPrefix != null)
		{
			_loadingPrefix = loadingPrefix;
		}
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.sql.ProgressCallBack#currentlyLoading(java.lang.String)
	 */
	@Override
	public void currentlyLoading(final String simpleName)
	{
		final StringBuilder statusText = new StringBuilder();
		statusText.append(_loadingPrefix);
		statusText.append(" ");
		statusText.append(simpleName);
		try
		{
			GUIUtils.processOnSwingEventThread(new Runnable()
			{
				public void run()
				{
					statusLabel.setText(statusText.toString());
					progressBar.setValue(progressBar.getValue() + 1);

					if (finishedLoading())
					{
						ProgressCallBackDialog.this.setVisible(false);
						return;
					}
				}
			});
		}
		catch (Exception e)
		{
			s_log.error("Unexpected exception: " + e.getMessage(), e);
		}
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.sql.ProgressCallBack#finishedLoading()
	 */
	@Override
	public boolean finishedLoading()
	{
		return progressBar.getValue() == itemCount;
	}
	
	/**
	 * @see java.awt.Window#dispose()
	 */	
	@Override
	public void dispose()
	{
		GUIUtils.processOnSwingEventThread(new Runnable()
		{
			@Override
			public void run()
			{
				callDisposeFromSuperClass();
			}
		});
	}
	
	/**
	 * Since {@link #dispose()} uses an {@link Runnable}, we needs an
	 * delegate to call the overridden dispose method.
	 */
	private void callDisposeFromSuperClass(){
		super.dispose();
	}

	/**
	 * @see java.awt.Dialog#setVisible(boolean)
	 */
	@Override
	public void setVisible(final boolean b)
	{
		GUIUtils.processOnSwingEventThread(new Runnable()
		{
			@Override
			public void run()
			{
				callSetVisibleFromSuperClass(b);
			}
		});
	}	
	
	/**
	 * Since {@link #setVisible(boolean)} uses an {@link Runnable}, we needs an
	 * delegate to call the overridden setVisible method.
	 */
	private void callSetVisibleFromSuperClass(final boolean b){
		super.setVisible(b);
	}
	

	private void init(int totalItems)
	{
		itemCount = totalItems;
		final Window owner = super.getOwner();
		final ProgressCallBackDialog dialog = this;
		createGUI();
		setLocationRelativeTo(owner);
		dialog.setVisible(true);
	}

	private void createGUI()
	{
		JPanel dialogPanel = new JPanel(new GridBagLayout());
		dialogPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
		GridBagConstraints c;

		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;

		statusLabel = new JLabel(i18n.INITIAL_LOADING_PREFIX);
		dialogPanel.add(statusLabel, c);

		progressBar = new JProgressBar(0, itemCount);
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0, 0, 10, 0);
		c.weightx = 1.0;

		dialogPanel.add(progressBar, c);
		super.getContentPane().add(dialogPanel);
		super.pack();
		super.setSize(new Dimension(400, 100));
	}


}
