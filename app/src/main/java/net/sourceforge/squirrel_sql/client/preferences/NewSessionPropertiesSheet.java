package net.sourceforge.squirrel_sql.client.preferences;
/*
 * Copyright (C) 2001-2003 Colin Bell
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

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.builders.UIFactory;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.DialogWidget;
import net.sourceforge.squirrel_sql.client.plugin.PluginInfo;
import net.sourceforge.squirrel_sql.client.session.properties.GeneralSessionPropertiesPanel;
import net.sourceforge.squirrel_sql.client.session.properties.SessionObjectTreePropertiesPanel;
import net.sourceforge.squirrel_sql.client.session.properties.SessionSQLPropertiesPanel;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.prefs.Preferences;

// JASON: Rename to NewSessionPropertiesInternalFrame
public class NewSessionPropertiesSheet extends DialogWidget
{
    private static final long serialVersionUID = 1L;

    /** Internationalized strings for this class. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(NewSessionPropertiesSheet.class);

	/** Logger for this class. */
	private static final ILogger s_log =
		LoggerController.createLogger(NewSessionPropertiesSheet.class);

	/** Singleton instance of this class. */
	private static NewSessionPropertiesSheet s_instance;

	/** Frame title. */
	private JLabel _titleLbl = new JLabel();

	private IApplication _app;
	private List<INewSessionPropertiesPanel> _panels = new ArrayList<INewSessionPropertiesPanel>();

	public static final String PREF_KEY_NEW_SESSION_PROPS_SHEET_WIDTH = "Squirrel.newSessionPropsSheetWidth";
	public static final String PREF_KEY_NEW_SESSION_PROPS_SHEET_HEIGHT = "Squirrel.newSessionPropsSheetHeight";


	private NewSessionPropertiesSheet(IApplication app)
	{
		super(s_stringMgr.getString("NewSessionPropertiesSheet.title"), true, app);
		_app = app;
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

		createGUI();
		for (Iterator<INewSessionPropertiesPanel> it = _panels.iterator(); it.hasNext();)
		{
			INewSessionPropertiesPanel pnl = it.next();
			pnl.initialize(_app);
		}

		setSize(getDimension());
		app.getMainFrame().addWidget(this);
		DialogWidget.centerWithinDesktop(this);
		setVisible(true);
	}

	private Dimension getDimension()
	{
		return new Dimension(
			Preferences.userRoot().getInt(PREF_KEY_NEW_SESSION_PROPS_SHEET_WIDTH, 500),
			Preferences.userRoot().getInt(PREF_KEY_NEW_SESSION_PROPS_SHEET_HEIGHT, 600)
		);
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
		}
		else
		{
			s_instance.moveToFront();
		}
	}

	public void dispose()
	{
		Dimension size = getSize();
		Preferences.userRoot().putInt(PREF_KEY_NEW_SESSION_PROPS_SHEET_WIDTH, size.width);
		Preferences.userRoot().putInt(PREF_KEY_NEW_SESSION_PROPS_SHEET_HEIGHT, size.height);

		synchronized (getClass())
		{
			s_instance = null;
		}
		super.dispose();
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
		for (Iterator<INewSessionPropertiesPanel> it = _panels.iterator(); it.hasNext();)
		{
			if (isDebug)
			{
				start = System.currentTimeMillis();
			}
			INewSessionPropertiesPanel pnl = it.next();
			pnl.applyChanges();
			if (isDebug)
			{
				s_log.debug("Panel " + pnl.getTitle() + " applied changes in "
						+ (System.currentTimeMillis() - start) + "ms");
			}
		}

		dispose();
	}

	private void createGUI()
	{
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		// This is a tool window.
		makeToolWindow(true);

		// Add panels for core Squirrel functionality.
		_panels.add(new GeneralSessionPropertiesPanel());
		_panels.add(new SessionObjectTreePropertiesPanel(_app));
		_panels.add(new SessionSQLPropertiesPanel(_app, null));

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
		final JTabbedPane tabPane = UIFactory.getInstance().createTabbedPane();
		for (Iterator<INewSessionPropertiesPanel> it = _panels.iterator(); it.hasNext();)
		{
			INewSessionPropertiesPanel pnl = it.next();
			String winTitle = pnl.getTitle();
			String hint = pnl.getHint();
			tabPane.addTab(winTitle, null, pnl.getPanelComponent(), hint);
		}

		final JPanel contentPane = new JPanel(new GridBagLayout());
		contentPane.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		setContentPane(contentPane);

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.BOTH;
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

      AbstractAction closeAction = new AbstractAction()
      {
        private static final long serialVersionUID = 1L;

        public void actionPerformed(ActionEvent actionEvent)
         {
            performClose();
         }
      };
      KeyStroke escapeStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
      getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(escapeStroke, "CloseAction");
      getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeStroke, "CloseAction");
      getRootPane().getInputMap(JComponent.WHEN_FOCUSED).put(escapeStroke, "CloseAction");
      getRootPane().getActionMap().put("CloseAction", closeAction);
   }

	private JPanel createButtonsPanel()
	{
		JPanel pnl = new JPanel();

		JButton okBtn = new JButton(s_stringMgr.getString("NewSessionPropertiesSheet.ok"));
		okBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				performOk();
			}
		});
		JButton closeBtn = new JButton(s_stringMgr.getString("NewSessionPropertiesSheet.close"));
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
