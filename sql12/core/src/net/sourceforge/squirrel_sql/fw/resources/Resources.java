package net.sourceforge.squirrel_sql.fw.resources;

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

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.shortcut.ShortCutReader;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.JScrollMenu;
import net.sourceforge.squirrel_sql.fw.gui.action.BaseAction;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import java.net.URL;
import java.util.MissingResourceException;

public abstract class Resources implements IResources
{
	private static final ILogger s_log = LoggerController.createLogger(Resources.class);

	private final ResourceBundleHandler _bundleHandler;

	private final ShortCutReader _shortCutReader;

	private ClassLoader _classLoader;

	protected Resources(String rsrcBundleBaseName, ClassLoader cl)
	{
		if (StringUtilities.isEmpty(rsrcBundleBaseName, true))
		{
			throw new IllegalArgumentException("Null or empty rsrcBundleBaseName passed");
		}

		_classLoader = cl;
		_bundleHandler = new ResourceBundleHandler(rsrcBundleBaseName, cl);

		_shortCutReader = new ShortCutReader(_bundleHandler);
	}

	/**
	 * @see IResources#getKeyStroke(javax.swing.Action)
	 */
	public KeyStroke getKeyStroke(Action action)
	{
		Utilities.checkNull("getKeyStroke", "action", action);
		return _shortCutReader.getShortcutAsKeyStroke(getFullMenuItemKey(action.getClass()), action);
	}

	public String getFullMenuItemKey(Class<? extends Action> actionClass)
	{
		return Keys.MENU_ITEM + "." + actionClass.getName();
	}


	public String getAcceleratorString(Action action)
	{
		String fullKey = getFullMenuItemKey(action.getClass());
		return getAcceleratorString(action, fullKey, false);
	}


	private String getAcceleratorString(Action action, String fullKey, boolean forAppend)
	{

		String buf = _shortCutReader.getShortcutAsString(fullKey, action);

		if(null == buf)
		{
			if (forAppend)
			{
				return "";
			}
			else
			{
				return null;
			}
		}
		else
		{
			if (forAppend)
			{
				return "  (" + buf + ")";
			}
			else
			{
				return buf;
			}

		}
	}



	/**
	 * @see IResources#addToPopupMenu(javax.swing.Action, javax.swing.JPopupMenu)
	 */
	public JMenuItem addToPopupMenu(Action action, javax.swing.JPopupMenu menu) throws MissingResourceException
	{
		Utilities.checkNull("addToPopupMenu", "action", action, "menu", menu);
		final String fullKey = getFullMenuItemKey(action.getClass());
		final JMenuItem item = menu.add(action);

		if (action.getValue(Action.MNEMONIC_KEY) == null)
		{
			String mn = getResourceString(fullKey, MenuItemProperties.MNEMONIC);
			if (mn.length() > 0)
			{
				item.setMnemonic(mn.charAt(0));
			}
		}

		item.setAccelerator(_shortCutReader.getShortcutAsKeyStroke(fullKey, action));

		String toolTipText = getToolTipText(action, fullKey, true);

		item.setToolTipText(toolTipText);

		return item;
	}

	/**
	 * @see IResources#addToMenuAsCheckBoxMenuItem(javax.swing.Action, javax.swing.JMenu)
	 */
	public JCheckBoxMenuItem addToMenuAsCheckBoxMenuItem(Action action, JMenu menu)
	      throws MissingResourceException
	{
		Utilities.checkNull("addToMenuAsCheckBoxMenuItem", "action", action, "menu", menu);
		final JCheckBoxMenuItem item = new JCheckBoxMenuItem(action);
		menu.add(item);
		configureMenuItem(action, item);
		return item;
	}

	/**
	 * @see IResources#addToMenuAsCheckBoxMenuItem(javax.swing.Action, javax.swing.JPopupMenu)
	 */
	public JCheckBoxMenuItem addToMenuAsCheckBoxMenuItem(Action action, JPopupMenu popupMenu)
	{
		final JCheckBoxMenuItem item = new JCheckBoxMenuItem(action);
		popupMenu.add(item);
		configureMenuItem(action, item);
		return item;
	}

	/**
	 * @see IResources#addToMenu(javax.swing.Action, javax.swing.JMenu)
	 */
	public JMenuItem addToMenu(Action action, JMenu menu) throws MissingResourceException
	{
		final JMenuItem item = menu.add(action);
		configureMenuItem(action, item);
		return item;
	}

	/**
	 * @see IResources#createMenu(java.lang.String)
	 */
	public JMenu createMenu(String menuKey) throws MissingResourceException
	{
		JMenu menu = new JMenu();
		initMenu(menuKey, menu);
		return menu;
	}

	public JScrollMenu createScrollMenu(String menuKey) throws MissingResourceException
	{
		JScrollMenu menu = new JScrollMenu();
		initMenu(menuKey, menu);

		menu.setMaximumVisibleRows(getMaxVisibleMenuRowsCount());

		return menu;
	}

	private int getMaxVisibleMenuRowsCount()
	{
		JMenuItem menuItem = new JMenuItem("Test");

		/////////////////////////////////////////////////////////////////////////////////////////////////////
		// To fix bug #1384 (NullPointer in TinyL&F) we add the menuItem to a menu so that it has a parent.
		JMenu menu = new JMenu();
		menu.add(menuItem);
		//
		//////////////////////////////////////////////////////////////////////////////////////////////////////

		double maxVisibleRowsCount = ((double)GUIUtils.getMinHeightOfAllScreens()) / ((double)menuItem.getPreferredSize().height) - 3;

		return (int) maxVisibleRowsCount;
	}

	private void initMenu(String menuKey, JMenu menu)
	{
		final String fullKey = Keys.MENU + "." + menuKey;
		menu.setText(getResourceString(fullKey, MenuProperties.TITLE));
		String mn = getResourceString(fullKey, MenuProperties.MNEMONIC);
		if (mn.length() >= 1)
		{
			menu.setMnemonic(mn.charAt(0));
		}
	}


	/**
	 * @see #setupAction(Action)
	 * @return
	 */
	public Action setupAction(Action action)
	{
		if (action == null)
		{
			throw new IllegalArgumentException("Action == null");
		}

		boolean showColorIcons = Main.getApplication().getSquirrelPreferences().getShowColoriconsInToolbar();

		final String key = getActionKey(action.getClass());
		String actionName = getActionName(action.getClass());

		action.putValue(Action.NAME, actionName);

		String shortDescription = getResourceString(key, ActionProperties.TOOLTIP);

		String acceleratorString = getAcceleratorString(action);
		if (null != acceleratorString && 0 < acceleratorString.trim().length())
		{
			shortDescription += "  (" + acceleratorString + ")";

		}

		action.putValue(Action.SHORT_DESCRIPTION, shortDescription);

		String accelerator = getAcceleratorString(action);
		if (null != accelerator)
		{
			action.putValue(ACCELERATOR_STRING, accelerator);
		}

		Icon icon = null;
		try
		{
			if (showColorIcons == true)
			{
				icon = getIcon(key, ActionProperties.ROLLOVER_IMAGE);
				action.putValue(Action.SMALL_ICON, icon);
			} else
			{
				icon = getIcon(key, ActionProperties.IMAGE);
				action.putValue(Action.SMALL_ICON, icon);
			}
		} catch (MissingResourceException ex)
		{
			try
			{
				icon = getIcon(key, ActionProperties.IMAGE);
				action.putValue(Action.SMALL_ICON, icon);
			} catch (MissingResourceException ignore)
			{
				// Ignore
			}
		}

		try
		{
			icon = getIcon(key, ActionProperties.ROLLOVER_IMAGE);
			action.putValue(BaseAction.IBaseActionPropertyNames.ROLLOVER_ICON, icon);
		} catch (MissingResourceException ignore)
		{
			// Ignore
		}

		try
		{
			icon = getIcon(key, ActionProperties.DISABLED_IMAGE);
			action.putValue(BaseAction.IBaseActionPropertyNames.DISABLED_ICON, icon);
		} catch (MissingResourceException ignore)
		{
			// Ignore
		}

		return action;
	}

	public String getActionName(Class<? extends Action> actionClass)
	{
		return getResourceString(getActionKey(actionClass), ActionProperties.NAME);
	}

	private String getActionKey(Class<? extends Action> actionClass)
	{
		final String actionClassName = actionClass.getName();
		return Keys.ACTION + "." + actionClassName;
	}

	/**
	 * @see IResources#getIcon(java.lang.String)
	 */
	public ImageIcon getIcon(String keyName)
	{
		return getIcon(keyName, "image");
	}

	/**
	 * @see IResources#getIcon(java.lang.Class, java.lang.String)
	 */
	public ImageIcon getIcon(Class<?> objClass, String propName)
	{
		return getIcon(objClass.getName(), propName);
	}

	/**
	 * @see IResources#getIcon(java.lang.String, java.lang.String)
	 */
	public ImageIcon getIcon(String keyName, String propName)
	{
		if (keyName == null)
		{
			throw new IllegalArgumentException("keyName == null");
		}
		if (propName == null)
		{
			throw new IllegalArgumentException("propName == null");
		}

		ImageIcon icon = null;

		String rsrcName = getResourceString(keyName, propName);

		if (rsrcName != null && rsrcName.length() > 0)
		{
			icon = privateGetIcon(rsrcName);
			if (icon == null)
			{
				s_log.error("can't load image: " + rsrcName);
			}
		} else
		{
			if (s_log.isDebugEnabled())
			{
				s_log.debug("No resource found for " + keyName + " : " + propName);
			}
		}

		return icon;
	}

	/**
	 * @see IResources#getString(java.lang.String)
	 */
	public String getString(String key)
	{
		return _bundleHandler.getString(key);
	}

	public void configureMenuItem(Action action, JMenuItem item) throws MissingResourceException
	{
		configureMenuItem(action, item, false);
	}

	public void configureMenuItem(Action action, JMenuItem item, boolean appendAccelereatorToToolTip) throws MissingResourceException
	{
		Utilities.checkNull("configureMenuItem", "action", action, "item", item);
		final String fullKey = getFullMenuItemKey(action.getClass());

		if (action.getValue(Action.MNEMONIC_KEY) == null)
		{
			String mn = getResourceString(fullKey, MenuItemProperties.MNEMONIC);
			if (mn.length() > 0)
			{
				item.setMnemonic(mn.charAt(0));
			}
		}

		item.setAccelerator(_shortCutReader.getShortcutAsKeyStroke(fullKey, action));

		String toolTipText = getToolTipText(action, fullKey, appendAccelereatorToToolTip);

		item.setToolTipText(toolTipText);

		// item.setIcon(null);
	}

	private String getToolTipText(Action action, String fullKey, boolean withAccelerator)
	{
		String toolTipText = (String) action.getValue(Action.SHORT_DESCRIPTION);

		if (null == toolTipText)
		{
			toolTipText = "";
		}

		if (withAccelerator)
		{
			toolTipText += getAcceleratorString(action, fullKey, true);
		}


		return toolTipText;
	}


	protected ResourceBundleHandler getBundleHandler()
	{
		return _bundleHandler;
	}

	private ImageIcon privateGetIcon(String iconName)
	{
		URL url = getIconUrl(iconName);

		if (url == null)
		{
			return null;
		}

		return Main.getApplication().getIconHandler().createImageIcon(url);
	}

	private URL getIconUrl(String iconName)
	{
		if (StringUtilities.isEmpty(iconName, true))
		{
			return null;
		}

		URL url;
		String imagePathName = getImagePathName(iconName);

		if (null == _classLoader)
		{
			url = getClass().getResource(imagePathName);

			// This slash stuff is a ...
			if (null == url && imagePathName.startsWith("/"))
			{
				url = getClass().getResource(imagePathName.substring(1));
			} else if (null == url && false == imagePathName.startsWith("/"))
			{
				url = getClass().getResource("/" + imagePathName);
			}

		}
		else
		{
			url = _classLoader.getResource(imagePathName);

			if (null == url && imagePathName.startsWith("/"))
			{
				url = _classLoader.getResource(imagePathName.substring(1));
			} else if (null == url && false == imagePathName.startsWith("/"))
			{
				url = _classLoader.getResource("/" + imagePathName);
			}
		}
		return url;
	}

	private String getResourceString(String keyName, String propName) throws MissingResourceException
	{
		return _bundleHandler.getResourceString(keyName, propName);
	}

	private String getImagePathName(String iconName)
	{
		return _bundleHandler.getImagePath() + iconName;
	}

	public ShortCutReader getShortCutReader()
	{
		return _shortCutReader;
	}
}
