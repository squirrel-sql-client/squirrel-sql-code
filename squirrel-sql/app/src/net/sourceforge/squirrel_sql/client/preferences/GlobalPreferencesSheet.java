package net.sourceforge.squirrel_sql.client.preferences;
/*
 * Copyright (C) 2001 Colin Bell
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
import javax.swing.JTabbedPane;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.BaseSheet;
import net.sourceforge.squirrel_sql.client.plugin.PluginInfo;

public class GlobalPreferencesSheet extends BaseSheet {
	/**
	 * This interface defines locale specific strings. This should be
	 * replaced with a property file.
	 */
	private interface i18n {
		String TITLE = "Global Preferences";
	}

	/** Logger for this class. */
	private static ILogger s_log = LoggerController.createLogger(GlobalPreferencesSheet.class);

	/** Singleton instance of this class. */
	private static GlobalPreferencesSheet s_instance;

	/** Application API. */
	private IApplication _app;

	private List _panels = new ArrayList();

	/** Frame title. */
	private JLabel _titleLbl = new JLabel();

	private GlobalPreferencesSheet(IApplication app) {
		super(i18n.TITLE);
		if (app == null) {
			throw new IllegalArgumentException("IApplication == null");
		}

		_app = app;
		createUserInterface();
	}

	/**
	 * Show the Preferences dialog
	 * 
	 * @param	app		Application API.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if a <TT>null</TT> <TT>IApplication</TT> object passed.
	 */
	public static synchronized void showSheet(IApplication app) {
		if (s_instance == null) {
			s_instance = new GlobalPreferencesSheet(app);
			app.getMainFrame().addInternalFrame(s_instance, true, null);
		}
		s_instance.setVisible(true);
	}

	public synchronized void setVisible(boolean show) {
		if (show) {
			if (!isVisible()) {
				final boolean isDebug = s_log.isDebugEnabled();
				long start = 0;
				for (Iterator it = _panels.iterator(); it.hasNext();) {
					IGlobalPreferencesPanel pnl = (IGlobalPreferencesPanel)it.next();
					if (isDebug) {
						start = System.currentTimeMillis();
					}
					pnl.initialize(_app);
					if (isDebug) {
						s_log.debug("Panel " + pnl.getTitle() + " initialized in "
									+ (System.currentTimeMillis() - start) + "ms");
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
	public void setTitle(String title) {
		super.setTitle(title);
		_titleLbl.setText(title);
	}

	private void performClose() {
		setVisible(false);
	}

	/**
	 * OK button pressed so save changes.
	 */
	private void performOk() {
		final boolean isDebug = s_log.isDebugEnabled();
		long start = 0;
		for (Iterator it = _panels.iterator(); it.hasNext();) {
			if (isDebug) {
				start = System.currentTimeMillis();
			}
			IGlobalPreferencesPanel pnl = (IGlobalPreferencesPanel)it.next();
			pnl.applyChanges();
			if (isDebug) {
				s_log.debug("Panel " + pnl.getTitle() + " applied changes in "
							+ (System.currentTimeMillis() - start) + "ms");
			}
		}

		setVisible(false);
	}

	private void createUserInterface() {
		setDefaultCloseOperation(HIDE_ON_CLOSE);

        // This is a tool window.
        GUIUtils.makeToolWindow(this, true);

		// Add panels for core Squirrel functionality.
		_panels.add(new GeneralPreferencesPanel());

		// Go thru all loaded plugins asking for panels.
		PluginInfo[] plugins = _app.getPluginManager().getPluginInformation();
		for (int plugIdx = 0; plugIdx < plugins.length; ++plugIdx) {
			PluginInfo pi = plugins[plugIdx];
			if (pi.isLoaded()) {
				IGlobalPreferencesPanel[] pnls = pi.getPlugin().getGlobalPreferencePanels();
				if (pnls != null && pnls.length > 0) {
					for (int pnlIdx = 0; pnlIdx < pnls.length; ++pnlIdx) {
						_panels.add(pnls[pnlIdx]);
					}
				}
			}
		}

		// Add all panels to the tabbed pane.
		JTabbedPane tabPane = new JTabbedPane();
		for (Iterator it = _panels.iterator(); it.hasNext();) {
			IGlobalPreferencesPanel pnl = (IGlobalPreferencesPanel)it.next();
			String title = pnl.getTitle();
			String hint = pnl.getHint();
			tabPane.addTab(title, null, pnl.getPanelComponent(), hint);
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

		gbc.gridx = 0;
		gbc.gridy = 0;
		contentPane.add(_titleLbl, gbc);

		++gbc.gridy;
		contentPane.add(tabPane, gbc);

		++gbc.gridy;
		contentPane.add(createButtonsPanel(), gbc);
	}

	private JPanel createButtonsPanel() {
		JPanel pnl = new JPanel();

		JButton okBtn = new JButton("OK");
		okBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				performOk();			
			}
		});
		JButton closeBtn = new JButton("Close");
		closeBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				performClose();			
			}
		});

		GUIUtils.setJButtonSizesTheSame(new JButton[] {okBtn, closeBtn});

		pnl.add(okBtn);
		pnl.add(closeBtn);		

		getRootPane().setDefaultButton(okBtn);

		return pnl;
	}

	private static Frame getFrame(IApplication app) {
		if (app == null) {
			throw new IllegalArgumentException("Null IApplication passed");
		}
		return app.getMainFrame();
	}
}
