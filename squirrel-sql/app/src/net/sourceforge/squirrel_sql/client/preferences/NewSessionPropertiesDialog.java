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
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.plugin.PluginInfo;
import net.sourceforge.squirrel_sql.client.session.properties.OutputPropertiesPanel;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.client.session.properties.SQLPropertiesPanel;

public class NewSessionPropertiesDialog extends JDialog {
	/**
	 * This interface defines locale specific strings. This should be
	 * replaced with a property file.
	 */
	private interface i18n {
		String TITLE = "New Session Properties";
	}

	/** Logger for this class. */
	private static ILogger s_log = LoggerController.createLogger(NewSessionPropertiesDialog.class);

	/** Singleton instance of this class. */
	private static NewSessionPropertiesDialog s_instance;

	private IApplication _app;
	private List _panels = new ArrayList();

	/**
	 * Default properties for new sessions.
	 */
	private SessionProperties _sessionProperties;

	private NewSessionPropertiesDialog(IApplication app)
			throws IllegalArgumentException {
		super(getFrame(app), i18n.TITLE);
		_app = app;
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
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
	public static synchronized void showDialog(IApplication app)
			throws IllegalArgumentException {
		if (s_instance == null) {
			s_instance = new NewSessionPropertiesDialog(app);
		}
		s_instance.setVisible(true);
	}

	public void setVisible(boolean show) {
		if (show) {
			final boolean isDebug = s_log.isDebugEnabled();
			long start = 0;
			for (Iterator it = _panels.iterator(); it.hasNext();) {
				INewSessionPropertiesPanel pnl = (INewSessionPropertiesPanel)it.next();
				if (isDebug) {
					start = System.currentTimeMillis();
				}
				pnl.initialize(_app);
				if (isDebug) {
					s_log.debug("Panel " + pnl.getTitle() + " initialized in "
								+ (System.currentTimeMillis() - start) + "ms");
				}
			}
		}
		super.setVisible(show);
	}

	private void performCancel() {
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
			INewSessionPropertiesPanel pnl = (INewSessionPropertiesPanel)it.next();
			pnl.applyChanges();
			if (isDebug) {
				s_log.debug("Panel " + pnl.getTitle() + " applied changes in "
							+ (System.currentTimeMillis() - start) + "ms");
			}
		}

		setVisible(false);
	}

	private void createUserInterface() {
		// Add panels for core Squirrel functionality.
		_panels.add(new SQLPropertiesPanel(_app));
		_panels.add(new OutputPropertiesPanel());

		// Go thru all loaded plugins asking for panels.
		PluginInfo[] plugins = _app.getPluginManager().getPluginInformation();
		for (int plugIdx = 0; plugIdx < plugins.length; ++plugIdx) {
			PluginInfo pi = plugins[plugIdx];
			if (pi.isLoaded()) {
				INewSessionPropertiesPanel[] pnls = pi.getPlugin().getNewSessionPropertiesPanels();
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
			INewSessionPropertiesPanel pnl = (INewSessionPropertiesPanel)it.next();
			String title = pnl.getTitle();
			String hint = pnl.getHint();
			tabPane.addTab(title, null, pnl.getPanelComponent(), hint);
		}

		final Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		contentPane.add(tabPane, BorderLayout.NORTH);
		contentPane.add(createButtonsPanel(), BorderLayout.CENTER);

		pack();
		GUIUtils.centerWithinParent(this);
		setResizable(false);
		setModal(true);
	}

	private JPanel createButtonsPanel() {
		JPanel pnl = new JPanel();

		JButton okBtn = new JButton("OK");
		okBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				performOk();			
			}
		});
		JButton cancelBtn = new JButton("Cancel");
		cancelBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				performCancel();			
			}
		});

		pnl.add(okBtn);
		pnl.add(cancelBtn);		

		GUIUtils.setJButtonSizesTheSame(new JButton[] {okBtn, cancelBtn});
		getRootPane().setDefaultButton(okBtn);

		return pnl;
	}

	private static Frame getFrame(IApplication app)
			throws IllegalArgumentException {
		if (app == null) {
			throw new IllegalArgumentException("Null IApplication passed");
		}
		return app.getMainFrame();
	}
}
