package net.sourceforge.squirrel_sql.fw.util;
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
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import net.sourceforge.squirrel_sql.fw.util.Logger;

public abstract class Resources {

    private interface ActionProperties {
        String IMAGE = "image";
        String NAME = "name";
        String TOOLTIP = "tooltip";
    }

    private interface MenuProperties {
        String TITLE = "title";
        String MNEMONIC = "mnemonic";
    }

    private interface MenuItemProperties extends MenuProperties {
        String ACCELERATOR = "accelerator";
    }

    private interface Keys {
        String ACTION = "action";
        String MENU = "menu";
        String MENU_ITEM = "menuitem";
    }

    /** Applications resource bundle. */
    private final ResourceBundle _bundle;

    /** Path to images. */
    private final String _imagePath;

    protected Resources(String rsrcBundleBaseName, ClassLoader cl)
            throws IllegalArgumentException {
        super();
        if (rsrcBundleBaseName == null || rsrcBundleBaseName.trim().length() == 0) {
            throw new IllegalArgumentException("Null or empty rsrcBundleBaseName passed");
        }

//      _app = app;
        _bundle = ResourceBundle.getBundle(rsrcBundleBaseName,
                    Locale.getDefault(), cl);
        _imagePath = _bundle.getString("path.images");
    }

    public JMenuItem addToMenu(Action action, JMenu menu)
            throws MissingResourceException {
        JMenuItem item = menu.add(action);
        final String fullKey = Keys.MENU_ITEM +  "." + getClassName(action.getClass());

        String mn = getResourceString(fullKey, MenuItemProperties.MNEMONIC);
        if (mn.length() > 0) {
            item.setMnemonic(mn.charAt(0));
        }

        String accel = getResourceString(fullKey, MenuItemProperties.ACCELERATOR);
        if (accel.length() > 0) {
            //try {
                item.setAccelerator(KeyStroke.getKeyStroke(accel));
            //} catch (Exception ex) {
            //  _app.getLogger().showMessage(Logger.ILogTypes.ERROR, "Invalid accelerator "
            //                                          + accel + " for fullKey");
            //}
        }

        item.setToolTipText((String)action.getValue(Action.SHORT_DESCRIPTION));

        return item;
    }

    public JMenu createMenu(String menuKey) throws MissingResourceException {
        JMenu menu = new JMenu();
        final String fullKey = Keys.MENU +  "." + menuKey;
        menu.setText(getResourceString(fullKey, MenuProperties.TITLE));
        String mn = getResourceString(fullKey, MenuProperties.MNEMONIC);
        if (mn.length() >= 1) {
            menu.setMnemonic(mn.charAt(0));
        }
        return menu;
    }

    public void setupAction(Action action) {
        String key = Keys.ACTION + "." + getClassName(action.getClass());
        action.putValue(Action.NAME, getResourceString(key, ActionProperties.NAME));
        action.putValue(Action.SHORT_DESCRIPTION, getResourceString(key, ActionProperties.TOOLTIP));
        setIconForAction(action, key);
    }

    public Icon getIcon(String keyName) {
        return getIcon(keyName, "image");
    }

    public Icon getIcon(String keyName, String propName) {
        return privateGetIcon(getResourceString(keyName, propName));
    }

    public Icon getIcon(Class objClass, String propName) {
        return getIcon(getClassName(objClass), propName);
    }

    protected ResourceBundle getBundle() {
        return _bundle;
    }

    private Icon privateGetIcon(String iconName) {
        if (iconName != null && iconName.length() > 0) {
            return new ImageIcon(getClass().getResource(_imagePath + iconName));
        }
        return null;
    }

    private void setIconForAction(Action action, String actionClassName) {
        Icon icon = getIcon(actionClassName, ActionProperties.IMAGE);
        if (icon != null) {
            action.putValue(Action.SMALL_ICON, icon);
        }
    }

    private String getResourceString(String keyName, String propName)
            throws MissingResourceException {
        return _bundle.getString(keyName + "." + propName);
    }

    private String getClassName(Class objClass) {
        // Retrieve class name of the passed Action minus the package name.
        String className = objClass.getName();
        int pos = className.lastIndexOf(".");
        if (pos != -1) {
            className = className.substring(pos + 1);
        }
        return className;
    }
}
