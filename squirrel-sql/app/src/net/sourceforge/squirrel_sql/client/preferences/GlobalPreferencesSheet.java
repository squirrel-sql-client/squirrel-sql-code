package net.sourceforge.squirrel_sql.client.preferences;
/*
 * Copyright (C) 2001-2002 Colin Bell
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
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.sourceforge.squirrel_sql.fw.gui.CursorChanger;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.BaseSheet;
import net.sourceforge.squirrel_sql.client.gui.SquirrelTabbedPane;
import net.sourceforge.squirrel_sql.client.plugin.PluginInfo;

/**
 * This sheet allows the user to maintain global preferences.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class GlobalPreferencesSheet extends BaseSheet
{
	/**
	 * This interface defines locale specific strings. This should be
	 * replaced with a property file.
	 */
	private interface i18n
	{
		String TITLE = "Global Preferences";
	}

	/** Logger for this class. */
	private static ILogger s_log =
		LoggerController.createLogger(GlobalPreferencesSheet.class);

	/** Singleton instance of this class. */
	private static GlobalPreferencesSheet s_instance;

	/** Application API. */
	private IApplication _app;

	/**
	 * List of all the panels (instances of
	 * <TT>IGlobalPreferencesPanel</TT> objects in shhet.
	 */
	private List _panels = new ArrayList();

	/** Sheet title. */
	private JLabel _titleLbl = new JLabel();

	/**
	 * Ctor specifying the application API.
	 *
	 * @param	app		Application API.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if a <TT>null</TT> <TT>IApplication passed.
	 */
	private GlobalPreferencesSheet(IApplication app)
	{
		super(i18n.TITLE, true);
		if (app == null)
		{
			throw new IllegalArgumentException("IApplication == null");
		}

		_app = app;
		createGUI();
	}

	/**
	 * Show the Preferences dialog
	 *
	 * @param	app		Application API.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if a <TT>null</TT> <TT>IApplication</TT> object passed.
	 */
	public static synchronized void showSheet(IApplication app)
	{
		if (s_instance == null)
		{
			s_instance = new GlobalPreferencesSheet(app);
			app.getMainFrame().addInternalFrame(s_instance, true, null);
		}
		s_instance.setVisible(true);
	}

	public void dispose()
	{
		synchronized (getClass())
		{
			s_instance = null;
		}
		super.dispose();
	}

	public synchronized void setVisible(boolean show)
	{
		if (show)
		{
			if (!isVisible())
			{
				final boolean isDebug = s_log.isDebugEnabled();
				long start = 0;
				for (Iterator it = _panels.iterator(); it.hasNext();)
				{
					IGlobalPreferencesPanel pnl = (IGlobalPreferencesPanel) it.next();
					if (isDebug)
					{
						start = System.currentTimeMillis();
					}
					try
					{
						pnl.initialize(_app);
					}
					catch (Throwable th)
					{
						final String msg = "Error occured loading " + pnl.getTitle();
						s_log.error(msg, th);
						_app.showErrorDialog(msg, th);
					}
					if (isDebug)
					{
						s_log.debug(
							"Panel "
								+ pnl.getTitle()
								+ " initialized in "
								+ (System.currentTimeMillis() - start)
								+ "ms");
					}
				}
				pack();
				GUIUtils.centerWithinDesktop(this);
			}
			moveToFront();
		}

		super.setVisible(show);
	}

	/**
	 * Set title of this frame. Ensure that the title label
	 * matches the frame title.
	 *
	 * @param	title	New title text.
	 */
	public void setTitle(String title)
	{
		super.setTitle(title);
		_titleLbl.setText(title);
	}

	/**
	 * Close this sheet.
	 */
	private void performClose()
	{
		dispose();
	}

	/**
	 * OK button pressed so save changes.
	 */
	private void performOk()
	{
		CursorChanger cursorChg = new CursorChanger(_app.getMainFrame());
		cursorChg.show();
		try
		{
			final boolean isDebug = s_log.isDebugEnabled();
			long start = 0;
			for (Iterator it = _panels.iterator(); it.hasNext();)
			{
				if (isDebug)
				{
					start = System.currentTimeMillis();
				}
				IGlobalPreferencesPanel pnl = (IGlobalPreferencesPanel) it.next();
				try
				{
					pnl.applyChanges();
				}
				catch (Throwable th)
				{
					final String msg = "Error occured saving " + pnl.getTitle();
					s_log.error(msg, th);
					_app.showErrorDialog(msg, th);
				}
				if (isDebug)
				{
					s_log.debug(
						"Panel "
							+ pnl.getTitle()
							+ " applied changes in "
							+ (System.currentTimeMillis() - start)
							+ "ms");
				}
			}
		}
		finally
		{
			cursorChg.restore();
		}

		dispose();
	}

	/**
	 * Create user interface.
	 */
	private void createGUI()
	{
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		// This is a tool window.
		GUIUtils.makeToolWindow(this, true);

		// Add panels for core Squirrel functionality.
		_panels.add(new GeneralPreferencesPanel());
		_panels.add(new SQLPreferencesPanel());
		_panels.add(new ProxyPreferencesPanel());

		// Go thru all loaded plugins asking for panels.
		PluginInfo[] plugins = _app.getPluginManager().getPluginInformation();
		for (int plugIdx = 0; plugIdx < plugins.length; ++plugIdx)
		{
			PluginInfo pi = plugins[plugIdx];
			if (pi.isLoaded())
			{
				IGlobalPreferencesPanel[] pnls = pi.getPlugin().getGlobalPreferencePanels();
				if (pnls != null && pnls.length > 0)
				{
					for (int pnlIdx = 0; pnlIdx < pnls.length; ++pnlIdx)
					{
						_panels.add(pnls[pnlIdx]);
					}
				}
			}
		}

		// Add all panels to the tabbed pane.
		SquirrelTabbedPane tabPane =
			new SquirrelTabbedPane(_app.getSquirrelPreferences());
		for (Iterator it = _panels.iterator(); it.hasNext();)
		{
			IGlobalPreferencesPanel pnl = (IGlobalPreferencesPanel) it.next();
			String title = pnl.getTitle();
			String hint = pnl.getHint();
			final JScrollPane sp = new JScrollPane(pnl.getPanelComponent());
			sp.setBorder(BorderFactory.createEmptyBorder());
			tabPane.addTab(title, null, sp/*pnl.getPanelComponent()*/, hint);
		}

		// This seems to be necessary to get background colours
		// correct. Without it labels added to the content pane
		// have a dark background while those added to a JPanel
		// in the content pane have a light background under
		// the java look and feel. Similar effects occur for other
		// look and feels.
		final JPanel contentPane = new JPanel();
		contentPane.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		setContentPane(contentPane);

		GridBagConstraints gbc = new GridBagConstraints();
		contentPane.setLayout(new GridBagLayout());

		gbc.gridwidth = 1;
		gbc.fill = gbc.BOTH;

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		contentPane.add(_titleLbl, gbc);

		++gbc.gridy;
		gbc.weighty = 1;
		contentPane.add(tabPane, gbc);

		++gbc.gridy;
		gbc.weighty = 0;
		contentPane.add(createButtonsPanel(), gbc);
	}

	/**
	 * Create panel at bottom containing the buttons.
	 */
	private JPanel createButtonsPanel()
	{
		JPanel pnl = new JPanel();

		JButton okBtn = new JButton("OK");
		okBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				performOk();
			}
		});
		JButton closeBtn = new JButton("Close");
		closeBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				performClose();
			}
		});

		GUIUtils.setJButtonSizesTheSame(new JButton[] { okBtn, closeBtn });

		pnl.add(okBtn);
		pnl.add(closeBtn);

		getRootPane().setDefaultButton(okBtn);

		return pnl;
	}

	/**
	 * Get the main frame from the passed <TT>IApplication</TT> object.
	 *
	 * @return	The main frame.
	 *
	 * @throws	IllegalArgumentException
	 *			If <TT>null</TT> <TT>IApplication</TT> passed.
	 */
	private static Frame getFrame(IApplication app)
	{
		if (app == null)
		{
			throw new IllegalArgumentException("Null IApplication passed");
		}
		return app.getMainFrame();
	}
}