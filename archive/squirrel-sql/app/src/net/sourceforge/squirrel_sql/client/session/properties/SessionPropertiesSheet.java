package net.sourceforge.squirrel_sql.client.session.properties;
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
import net.sourceforge.squirrel_sql.client.plugin.SessionPluginInfo;
import net.sourceforge.squirrel_sql.client.session.ISession;

public class SessionPropertiesSheet extends BaseSheet
{
	/**
	 * This interface defines locale specific strings. This should be
	 * replaced with a property file.
	 */
	private interface i18n
	{
		String TITLE = "Session Properties";
	}

	/** Logger for this class. */
	private static final ILogger s_log =
		LoggerController.createLogger(SessionPropertiesSheet.class);

	private ISession _session;
	private List _panels = new ArrayList();

	/** Frame title. */
	private JLabel _titleLbl = new JLabel();

	SessionPropertiesSheet(ISession session)
	{
		super(i18n.TITLE, true);
		if (session == null)
		{
			throw new IllegalArgumentException("Null ISession passed");
		}
		_session = session;
		createGUI();
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
					ISessionPropertiesPanel pnl = (ISessionPropertiesPanel) it.next();
					if (isDebug)
					{
						start = System.currentTimeMillis();
					}
					pnl.initialize(_session.getApplication(), _session);
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

	ISession getSession()
	{
		return _session;
	}

	/**
	 * OK button pressed. Edit data and if ok save to aliases model
	 * and then close dialog.
	 */
	private void performOk()
	{
		final boolean isDebug = s_log.isDebugEnabled();
		long start = 0;
		for (Iterator it = _panels.iterator(); it.hasNext();)
		{
			ISessionPropertiesPanel pnl = (ISessionPropertiesPanel) it.next();
			if (isDebug)
			{
				start = System.currentTimeMillis();
			}
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

		setTitle(getTitle() + ": " + _session.getSessionSheet().getTitle());

		// This is a tool window.
		GUIUtils.makeToolWindow(this, true);

		final IApplication app = _session.getApplication();

		_panels.add(new GeneralSessionPropertiesPanel());
		_panels.add(new SessionSQLPropertiesPanel(app));

		// Go thru all plugins attached to this session asking for panels.
		SessionPluginInfo[] plugins =
			app.getPluginManager().getPluginInformation(_session);
		for (int i = 0; i < plugins.length; ++i)
		{
			SessionPluginInfo spi = plugins[i];
			if (spi.isLoaded())
			{
				ISessionPropertiesPanel[] pnls =
					spi.getSessionPlugin().getSessionPropertiesPanels(_session);
				if (pnls != null && pnls.length > 0)
				{
					for (int pnlIdx = 0; pnlIdx < pnls.length; ++pnlIdx)
					{
						_panels.add(pnls[pnlIdx]);
					}
				}
			}
		}

		// Add all panels to the tabbed panel.
		SquirrelTabbedPane tabPane =
			new SquirrelTabbedPane(app.getSquirrelPreferences());
		for (Iterator it = _panels.iterator(); it.hasNext();)
		{
			ISessionPropertiesPanel pnl = (ISessionPropertiesPanel) it.next();
			String title = pnl.getTitle();
			String hint = pnl.getHint();
			final JScrollPane sp = new JScrollPane(pnl.getPanelComponent());
			sp.setBorder(BorderFactory.createEmptyBorder());
			tabPane.addTab(title, null, sp, hint);
		}

		final JPanel contentPane = new JPanel(new GridBagLayout());
		contentPane.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		setContentPane(contentPane);

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridwidth = 1;

		gbc.gridx = 0;
		gbc.gridy = 0;

		gbc.fill = gbc.BOTH;
		gbc.weightx = 1;
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