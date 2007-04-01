package net.sourceforge.squirrel_sql.client.session.properties;
/*
 * Copyright (C) 2001-2004 Colin Bell
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
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.WindowManager;
import net.sourceforge.squirrel_sql.client.gui.builders.UIFactory;
import net.sourceforge.squirrel_sql.client.gui.session.BaseSessionInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SessionInternalFrame;
import net.sourceforge.squirrel_sql.client.plugin.SessionPluginInfo;
import net.sourceforge.squirrel_sql.client.preferences.NewSessionPropertiesSheet;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class SessionPropertiesSheet extends BaseSessionInternalFrame
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(SessionPropertiesSheet.class);

    
	/**
    * This interface defines locale specific strings. This should be
    * replaced with a property file.
    */
   private interface i18n
   {

		// i18n[sessionPropertiesSheet.sessionProperties=- Session Properties]
		String TITLE = s_stringMgr.getString("sessionPropertiesSheet.sessionProperties");
   }

	/** Logger for this class. */
	private static final ILogger s_log =
		LoggerController.createLogger(SessionPropertiesSheet.class);

	private final List<ISessionPropertiesPanel> _panels = 
        new ArrayList<ISessionPropertiesPanel>();

   private JTabbedPane _tabbedPane;

   /** Frame title. */
	private JLabel _titleLbl = new JLabel();

	public SessionPropertiesSheet(ISession session)
	{
		super(session, session.getTitle() + " " + i18n.TITLE, true);
		createGUI();
        for (ISessionPropertiesPanel pnl : _panels)
		{
			pnl.initialize(getSession().getApplication(), getSession());
		}

		setSize(getDimension());
	}


	private Dimension getDimension()
	{
		return new Dimension(
			Preferences.userRoot().getInt(NewSessionPropertiesSheet.PREF_KEY_NEW_SESSION_PROPS_SHEET_WIDTH, 500),
			Preferences.userRoot().getInt(NewSessionPropertiesSheet.PREF_KEY_NEW_SESSION_PROPS_SHEET_HEIGHT, 600)
		);
	}


	public void selectTabIndex(int index)
   {
		int tabCount = _tabbedPane.getTabCount();

      if(0 <= index && index < tabCount)
      {
			_tabbedPane.setSelectedIndex(index);
      }
   }


   /**
	 * Set title of this frame. Ensure that the title label
	 * matches the frame title.
	 *
	 * @param	newTitle	New title text.
	 */
	public void setTitle(String newTitle)
	{
		super.setTitle(newTitle);
		if (_titleLbl != null)
		{
			_titleLbl.setText(newTitle);
		}
	}

	private void performClose()
	{
		dispose();
	}

	/**
	 * OK button pressed. Edit data and if ok save to aliases model
	 * and then close dialog.
	 */
	private void performOk()
	{
		final boolean isDebug = s_log.isDebugEnabled();
		long start = 0;
        for (ISessionPropertiesPanel pnl : _panels)
		{
			if (isDebug)
			{
				start = System.currentTimeMillis();
			}
			pnl.applyChanges();
            if (pnl instanceof SessionObjectTreePropertiesPanel) {
                SessionObjectTreePropertiesPanel otPanel = 
                    (SessionObjectTreePropertiesPanel) pnl;
                if (otPanel.isObjectTreeRefreshNeeded()) {
                    WindowManager wm = 
                        _session.getApplication().getWindowManager();
                    BaseSessionInternalFrame[] frames = 
                        wm.getAllFramesOfSession(_session.getIdentifier());
                    for (int i = 0; i < frames.length; i++) {
                        BaseSessionInternalFrame frame = frames[i];
                        try {
                            if (frame instanceof SessionInternalFrame) {
                                SessionInternalFrame sif = 
                                    (SessionInternalFrame)frame;
                                sif.getObjectTreeAPI().refreshSelectedNodes();
                            }
                        } catch (Exception e) {
                            s_log.error(
                                "Unexpected exception while attempting to " +
                                "refresh object tree: "+e.getMessage(), e);
                        }
                    }
                }
            }
			if (isDebug)
			{
				s_log.debug("Panel " + pnl.getTitle() + " applied changes in "
						+ (System.currentTimeMillis() - start) + "ms");
			}
		}

		dispose();
	}

	public void dispose()
	{
		Dimension size = getSize();
		Preferences.userRoot().putInt(NewSessionPropertiesSheet.PREF_KEY_NEW_SESSION_PROPS_SHEET_WIDTH, size.width);
		Preferences.userRoot().putInt(NewSessionPropertiesSheet.PREF_KEY_NEW_SESSION_PROPS_SHEET_HEIGHT, size.height);

		super.dispose();	 //To change body of overridden methods use File | Settings | File Templates.
	}

	private void createGUI()
	{
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

// TODO: Setup title correctly.
//		setTitle(getTitle() + ": " + _session.getSessionSheet().getTitle());

		// This is a tool window.
		GUIUtils.makeToolWindow(this, true);

		final IApplication app = getSession().getApplication();

		// Property panels for SQuirreL.
		_panels.add(new GeneralSessionPropertiesPanel());
		_panels.add(new SessionObjectTreePropertiesPanel(app));
		_panels.add(new SessionSQLPropertiesPanel(app, false));

		// Go thru all plugins attached to this session asking for panels.
		SessionPluginInfo[] plugins = app.getPluginManager().getPluginInformation(getSession());
		for (int i = 0; i < plugins.length; ++i)
		{
			SessionPluginInfo spi = plugins[i];
			if (spi.isLoaded())
			{
				ISessionPropertiesPanel[] pnls = spi.getSessionPlugin().getSessionPropertiesPanels(getSession());
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
		_tabbedPane = UIFactory.getInstance().createTabbedPane();
        for (ISessionPropertiesPanel pnl : _panels) 
		{
			String pnlTitle = pnl.getTitle();
			String hint = pnl.getHint();
			_tabbedPane.addTab(pnlTitle, null, pnl.getPanelComponent(), hint);
		}

		final JPanel contentPane = new JPanel(new GridBagLayout());
		contentPane.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		setContentPane(contentPane);

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridwidth = 1;

		gbc.gridx = 0;
		gbc.gridy = 0;

		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1;
		contentPane.add(_titleLbl, gbc);

		++gbc.gridy;
		gbc.weighty = 1;
		contentPane.add(_tabbedPane, gbc);

		++gbc.gridy;
		gbc.weighty = 0;
		contentPane.add(createButtonsPanel(), gbc);

      AbstractAction closeAction = new AbstractAction()
      {
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

		// i18n[sessionPropertiesSheet.ok=OK]
		JButton okBtn = new JButton(s_stringMgr.getString("sessionPropertiesSheet.ok"));
		okBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				performOk();
			}
		});

		// i18n[sessionPropertiesSheet.close=Close]
		JButton closeBtn = new JButton(s_stringMgr.getString("sessionPropertiesSheet.close"));
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

