package net.sourceforge.squirrel_sql.client.session.properties;
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
import net.sourceforge.squirrel_sql.client.plugin.SessionPluginInfo;
import net.sourceforge.squirrel_sql.client.session.ISession;

public class SessionPropertiesDialog extends JDialog {
	/**
	 * This interface defines locale specific strings. This should be
	 * replaced with a property file.
	 */
	private interface i18n {
		String TITLE = "Session Properties";
	}

	/** Logger for this class. */
	private static ILogger s_log = LoggerController.createLogger(SessionPropertiesDialog.class);

	private ISession _session;
	private List _panels = new ArrayList();

	public SessionPropertiesDialog(Frame frame, ISession session) {
		super(frame, i18n.TITLE);
		if (session == null) {
			throw new IllegalArgumentException("Null ISession passed");
		}
		_session = session;
		createUserInterface();
	}

	private void performCancel() {
		setVisible(false);
	}

	/**
	 * OK button pressed. Edit data and if ok save to aliases model
	 * and then close dialog.
	 */
	private void performOk() {
		final boolean isDebug = s_log.isDebugEnabled();
		long start = 0;
		for (Iterator it = _panels.iterator(); it.hasNext();) {
			ISessionPropertiesPanel pnl = (ISessionPropertiesPanel)it.next();
			if (isDebug) {
				start = System.currentTimeMillis();
			}
			pnl.applyChanges();
			if (isDebug) {
				s_log.debug("Panel " + pnl.getTitle() + " applied changes in "
							+ (System.currentTimeMillis() - start) + "ms");
			}
		}

		dispose();
	}

	private void createUserInterface() {
		final IApplication app = _session.getApplication();

		_panels.add(new SQLPropertiesPanel(app));
		_panels.add(new OutputPropertiesPanel());

		final Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		// Go thru all plugins attached to this session asking for panels.
		SessionPluginInfo[] plugins = app.getPluginManager().getPluginInformation(_session);
		for (int i = 0; i < plugins.length; ++i) {
			SessionPluginInfo spi = plugins[i];
			if (spi.isLoaded()) {
				ISessionPropertiesPanel[] pnls = spi.getSessionPlugin().getSessionPropertiesPanels(_session);
				if (pnls != null && pnls.length > 0) {
					for (int pnlIdx = 0; pnlIdx < pnls.length; ++pnlIdx) {
						_panels.add(pnls[pnlIdx]);
					}
				}
			}
		}

		// Initialize all panels and add them to the dialog.
		JTabbedPane tabPane = new JTabbedPane();
		for (Iterator it = _panels.iterator(); it.hasNext();) {
			ISessionPropertiesPanel pnl = (ISessionPropertiesPanel)it.next();
			pnl.initialize(_session.getApplication(), _session);
			String title = pnl.getTitle();
			String hint = pnl.getHint();
			tabPane.addTab(title, null, pnl.getPanelComponent(), hint);
		}

		contentPane.add(tabPane, BorderLayout.NORTH);
		contentPane.add(createButtonsPanel(), BorderLayout.CENTER);

		setResizable(false);
		setModal(true);
		pack();

		GUIUtils.centerWithinParent(this);
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
}
