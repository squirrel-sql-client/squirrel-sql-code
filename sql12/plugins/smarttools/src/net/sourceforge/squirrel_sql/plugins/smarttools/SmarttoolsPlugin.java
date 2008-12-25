/*
 * Copyright (C) 2008 Michael Romankiewicz
 * microm at users.sourceforge.net
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
package net.sourceforge.squirrel_sql.plugins.smarttools;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;
import java.beans.PropertyVetoException;

import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.session.ObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.DialogWidget;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.IWidget;
import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallback;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.smarttools.gui.ISmarttoolFrame;
import net.sourceforge.squirrel_sql.plugins.smarttools.gui.SmarttoolChangeValuesFrame;
import net.sourceforge.squirrel_sql.plugins.smarttools.gui.SmarttoolFindBadNullValuesFrame;
import net.sourceforge.squirrel_sql.plugins.smarttools.gui.SmarttoolMissingIndicesFrame;

/**
 * Plugin start class for smarttools
 * 
 * @author Michael Romankiewicz
 */
public class SmarttoolsPlugin extends DefaultSessionPlugin {
	// variables
	// ========================================================================
	// non visible
	// Logger for this class
	private final static ILogger log = LoggerController
			.createLogger(SmarttoolsPlugin.class);

	private static final StringManager stringManager = StringManagerFactory
			.getStringManager(SmarttoolsPlugin.class);

	// sheet types
	private final int ST_SHEET_TYPE_FIND_VALUES = 1;
	private final int ST_SHEET_TYPE_CHANGE_VALUES = 2;
	private final int ST_SHEET_TYPE_MISSING_INICES = 3;

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.client.plugin.IPlugin#getAuthor()
     */
	public String getAuthor() {
		return "Michael Romankiewicz";
	}

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.client.plugin.IPlugin#getDescriptiveName()
     */
	public String getDescriptiveName() {
		return "Smarttools Plugin";
	}

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.client.plugin.IPlugin#getInternalName()
     */
	public String getInternalName() {
		return "smarttools";
	}

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.client.plugin.IPlugin#getVersion()
     */
	public String getVersion() {
		return "1.0";
	}
	
    /**
     * Returns the name of the Help file for the plugin. This should
     * be a text or HTML file residing in the <TT>getPluginAppSettingsFolder</TT>
     * directory.
     *
     * @return  the Help file name or <TT>null</TT> if plugin doesn't have
     * a help file.
     */
    public String getHelpFileName()
    {
       return "readme.html";
    }    
    
    /**
     * Returns the name of the change log for the plugin. This should
     * be a text or HTML file residing in the <TT>getPluginAppSettingsFolder</TT>
     * directory.
     *
     * @return  the changelog file name or <TT>null</TT> if plugin doesn't have
     *          a change log.
     */
    public String getChangeLogFileName()
    {
        return "changes.txt";
    }

    /**
     * @see net.sourceforge.squirrel_sql.client.plugin.IPlugin#getLicenceFileName()
     */
    public String getLicenceFileName()
    {
        return "licence.txt";
    }

	/**
	 * Initialize this plugin.
	 */
	public synchronized void initialize() throws PluginException {
//		IApplication application = getApplication();
//		application.addToMenu(IMenuIDs.PLUGINS_MENU,
//				getSmarttoolsMenu(getApplication(), null, false));
	}

	public void unload() {
		super.unload();
	}

	/**
	 * Called when a session started. Add commands to popup menu in object tree.
	 * 
	 * @param session
	 *            The session that is starting.
	 * @return An implementation of PluginSessionCallback or null to indicate
	 *         the plugin does not work with this session
	 */
	public PluginSessionCallback sessionStarted(final ISession session) {
			try {
			// Add context menu items to the object tree's session node.
			// as popup menu
			IObjectTreeAPI objectTreeApi = session.getSessionInternalFrame()
					.getObjectTreeAPI();
			objectTreeApi.addToPopup(DatabaseObjectType.SESSION,
					getSmarttoolsMenu(getApplication(), session, true));

			return new PluginSessionCallback() {
				public void sqlInternalFrameOpened(
						SQLInternalFrame sqlInternalFrame, ISession sess) {
					// plugin supports Session main window only
				}

				public void objectTreeInternalFrameOpened(
						ObjectTreeInternalFrame objectTreeInternalFrame,
						ISession sess) {
					// plugin supports Session main window only
				}
			};
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Interface with the text resources of I18NStrings.properties
	 */
	private interface i18n {
		String MENU_TITLE_SMARTTOOLS = stringManager
				.getString("smarttools.menu.title");
		String MENU_TITLE_SMARTTOOLS_FIND_VALUES = stringManager
				.getString("smarttools.menu.findvalues.title");
		String MENU_TITLE_SMARTTOOLS_CHANGE_VALUES = stringManager
				.getString("smarttools.menu.changevalues.title");
		String MENU_TITLE_SMARTTOOLS_MISSING_INDICES = stringManager
				.getString("smarttools.menu.missingindices.title");
	}

	/**
	 * Return the menu for the plugin
	 * 
	 * @param session session
	 * @return menu
	 */
	private JMenu getSmarttoolsMenu(final IApplication application,
			ISession session, boolean forPopupMenu) {
		JMenu menu = new JMenu(i18n.MENU_TITLE_SMARTTOOLS);

		if (forPopupMenu) {
			menu.add(addMenuItem(application,
					i18n.MENU_TITLE_SMARTTOOLS_FIND_VALUES,
					ST_SHEET_TYPE_FIND_VALUES, session));

			menu.add(addMenuItem(application,
					i18n.MENU_TITLE_SMARTTOOLS_CHANGE_VALUES,
					ST_SHEET_TYPE_CHANGE_VALUES, session));

			menu.add(addMenuItem(application,
					i18n.MENU_TITLE_SMARTTOOLS_MISSING_INDICES,
					ST_SHEET_TYPE_MISSING_INICES, session));
		}
		
		return menu;
	}
	
	/**
	 * Creates a menuitem for a new smarttools function
	 * 
	 * @param application application
	 * @param title title of the menu item
	 * @param sheetType type to identifier the frame which will be created
	 * @param session session
	 * @return menu item
	 */
	private JMenuItem addMenuItem(final IApplication application, final String title,
			final int sheetType, final ISession session) {
		JMenuItem menuItem = new JMenuItem(title);
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				DialogWidget frame = (DialogWidget) isInternalFrameUsed(application, sheetType);
				if (frame == null) {
					if (sheetType == ST_SHEET_TYPE_FIND_VALUES) {
						frame = new SmarttoolFindBadNullValuesFrame(session, title);
					} else if (sheetType == ST_SHEET_TYPE_CHANGE_VALUES) {
						frame = new SmarttoolChangeValuesFrame(session, title);
					} else if (sheetType == ST_SHEET_TYPE_MISSING_INICES) {
						frame = new SmarttoolMissingIndicesFrame(session, title);
					}
					application.getMainFrame().addWidget(frame);
					frame.pack();
					if (frame instanceof SmarttoolFindBadNullValuesFrame) {
						frame.setSize(new Dimension(frame.getWidth(), 500));
					} else if (frame instanceof SmarttoolChangeValuesFrame) {
						frame.setSize(new Dimension(frame.getWidth(), 500));
					} else if (frame instanceof SmarttoolMissingIndicesFrame) {
						frame.setSize(new Dimension(frame.getWidth(), 500));
					} 
					DialogWidget.centerWithinDesktop(frame);
				} else {
					frame.setVisible(true);
					frame.moveToFront();
				}


				try {
					frame.setSelected(true);
					if (frame instanceof ISmarttoolFrame) {
						((ISmarttoolFrame)frame).setFocusToFirstEmptyInputField();
					}
				} catch (PropertyVetoException e) {
					log.error(e.getLocalizedMessage());
				}
			}
		});
		return menuItem;
	}

	private IWidget isInternalFrameUsed(IApplication application,
			int sheetType) {
		IWidget[] frames = application.getMainFrame().getDesktopContainer().getAllWidgets();
		for (int i = 0; i < frames.length; i++) {
			if ((sheetType == ST_SHEET_TYPE_FIND_VALUES && frames[i] instanceof SmarttoolFindBadNullValuesFrame)
					|| (sheetType == ST_SHEET_TYPE_CHANGE_VALUES && frames[i] instanceof SmarttoolChangeValuesFrame)
					|| (sheetType == ST_SHEET_TYPE_MISSING_INICES && frames[i] instanceof SmarttoolMissingIndicesFrame)
					) {
				return frames[i];
			}
		}
		return null;
	}

	@Override
	public void sessionEnding(ISession session) {
		super.sessionEnding(session);
		IWidget[] frames = session.getApplication().getMainFrame().getDesktopContainer().getAllWidgets();
		for (int i = 0; i < frames.length; i++) {
			if (frames[i] instanceof SmarttoolFindBadNullValuesFrame
					|| frames[i] instanceof SmarttoolChangeValuesFrame
					|| frames[i] instanceof SmarttoolMissingIndicesFrame
				) {
				frames[i].dispose();
			}
		}
	}
}
