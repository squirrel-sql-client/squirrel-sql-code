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

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.BaseSheet;
import net.sourceforge.squirrel_sql.client.gui.SquirrelTabbedPane;
import net.sourceforge.squirrel_sql.client.plugin.PluginInfo;
import net.sourceforge.squirrel_sql.client.session.properties.GeneralSessionPropertiesPanel;
import net.sourceforge.squirrel_sql.client.session.properties.SessionSQLPropertiesPanel;

public class NewSessionPropertiesSheet extends BaseSheet
{
	/**
	 * This interface defines locale specific strings. This should be
	 * replaced with a property file.
	 */
	private interface NewSessionPropertiesSheetI18n
	{
		String TITLE = "New Session Properties";
	}

	/** Logger for this class. */
	private static final ILogger s_log =
		LoggerController.createLogger(NewSessionPropertiesSheet.class);

	/** Singleton instance of this class. */
	private static NewSessionPropertiesSheet s_instance;

	/** Frame title. */
	private JLabel _titleLbl = new JLabel();

	private IApplication _app;
	private List _panels = new ArrayList();

	private NewSessionPropertiesSheet(IApplication app)
	{
		super(NewSessionPropertiesSheetI18n.TITLE, true);
		_app = app;
		setDefaultCloseOperation(HIDE_ON_CLOSE);
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
			s_instance = new NewSessionPropertiesSheet(app);
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

	public void setVisible(boolean show)
	{
		if (show)
		{
			if (!isVisible())
			{
				final boolean isDebug = s_log.isDebugEnabled();
				long start = 0;
				for (Iterator it = _panels.iterator(); it.hasNext();)
				{
					INewSessionPropertiesPanel pnl = (INewSessionPropertiesPanel) it.next();
					if (isDebug)
					{
						start = System.currentTimeMillis();
					}
					pnl.initialize(_app);
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

	private void performClose()
	{
		dispose();
	}

	/**
	 * OK button pressed so save changes.
	 */
	private void performOk()
	{
		final boolean isDebug = s_log.isDebugEnabled();
		long start = 0;
		for (Iterator it = _panels.iterator(); it.hasNext();)
		{
			if (isDebug)
			{
				start = System.currentTimeMillis();
			}
			INewSessionPropertiesPanel pnl = (INewSessionPropertiesPanel) it.next();
			pnl.applyChanges();
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

		dispose();
	}

	private void createGUI()
	{
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		// This is a tool window.
		GUIUtils.makeToolWindow(this, true);

		// Add panels for core Squirrel functionality.
		_panels.add(new GeneralSessionPropertiesPanel());
		_panels.add(new SessionSQLPropertiesPanel(_app));

		// Go thru all loaded plugins asking for panels.
		PluginInfo[] plugins = _app.getPluginManager().getPluginInformation();
		for (int plugIdx = 0; plugIdx < plugins.length; ++plugIdx)
		{
			PluginInfo pi = plugins[plugIdx];
			if (pi.isLoaded())
			{
				INewSessionPropertiesPanel[] pnls =
					pi.getPlugin().getNewSessionPropertiesPanels();
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
		final SquirrelTabbedPane tabPane =
			new SquirrelTabbedPane(_app.getSquirrelPreferences());
		for (Iterator it = _panels.iterator(); it.hasNext();)
		{
			INewSessionPropertiesPanel pnl = (INewSessionPropertiesPanel)it.next();
			String title = pnl.getTitle();
			String hint = pnl.getHint();
			final JScrollPane sp = new JScrollPane(pnl.getPanelComponent());
			sp.setBorder(BorderFactory.createEmptyBorder());
			tabPane.addTab(title, null, sp/*pnl.getPanelComponent()*/, hint);
		}

		final JPanel contentPane = new JPanel(new GridBagLayout());
		contentPane.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		setContentPane(contentPane);

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridwidth = 1;
		gbc.fill = gbc.BOTH;
		gbc.weightx = 1;

		gbc.gridx = 0;
		gbc.gridy = 0;
		contentPane.add(_titleLbl, gbc);
		++gbc.gridy;
		gbc.weighty = 1;
		contentPane.add(tabPane, gbc);

		++gbc.gridy;
		gbc.weighty = 0;
		contentPane.add(createButtonsPanel(), gbc);
	}

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

		pnl.add(okBtn);
		pnl.add(closeBtn);

		GUIUtils.setJButtonSizesTheSame(new JButton[] { okBtn, closeBtn });
		getRootPane().setDefaultButton(okBtn);

		return pnl;
	}
}