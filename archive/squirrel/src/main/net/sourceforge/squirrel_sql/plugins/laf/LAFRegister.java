package net.sourceforge.squirrel_sql.plugins.laf;
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
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import net.sourceforge.squirrel_sql.fw.util.Logger;
import net.sourceforge.squirrel_sql.fw.util.MyURLClassLoader;

import net.sourceforge.squirrel_sql.client.IApplication;

/**
 * Register of Look and Feels.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class LAFRegister implements LAFConstants {
    /** Application API. */
    private IApplication _app;

    /** Look and Feel plugin. */
    LAFPlugin _plugin;

    /** Classloader for Look and Feel classes. */
    private MyURLClassLoader _lafClassLoader;

    /** Name of the Skin Look and Feel. */
    private String _skinLookAndFeelName = "";

    /**
     * Ctor. Load all Look and Feels from the Look and Feel folder. Set the
     * current Look and Feel to that specified in the application preferences.
     *
     * @param   app     Application API.
     * @param   plugin  The LAF plugin.
     *
     * @throws  IllegalArgumentException
     *              If <TT>IApplication</TT>, or <TT>LAFPlugin</TT> are <TT>null</TT>.
     *
     * @throws  IllegalStateException
     *              If no <TT>Logger</TT> object exists in the passed<TT>IApplication</TT>.
     */
    public LAFRegister(IApplication app, LAFPlugin plugin)
        throws IllegalArgumentException {
        super();
        if (app == null) {
            throw new IllegalArgumentException("Null IApplication passed");
        }
        if (plugin == null) {
            throw new IllegalArgumentException("Null LAFPlugin passed");
        }
        if (app.getLogger() == null) {
            throw new IllegalStateException("No Logger object in IApplication");
        }

        _app = app;
        _plugin = plugin;

        installLookAndFeels();

        try {
            setLookAndFeel();
        } catch (Throwable ex) {
            _app.getLogger().showMessage(Logger.ILogTypes.ERROR, ex);
        }
    }

    /**
     * Return the name of the Skin Look and Feel.
     *
     * @return  name of he Skin Look and Feel.
     */
    public String getSkinnableLookAndFeelName() {
        return _skinLookAndFeelName;
    }

    /**
     * Set the current Look and Feel to that specified in the app preferences.
     */
    void setLookAndFeel()
        throws
            ClassNotFoundException,
            IllegalAccessException,
            InstantiationException,
            UnsupportedLookAndFeelException {
        LAFPreferences lafPrefs = _plugin.getLAFPreferences();
        String lafClassName = lafPrefs.getLookAndFeelClassName();

        // If this is the Skin Look and Feel then load the current theme pack
        // and set the current skin.
        if (lafClassName.equals(SKINNABLE_LAF_CLASS_NAME)) {
            try {
                Class skinLafClass = _lafClassLoader.loadClass(lafClassName);
                Class skinClass = _lafClassLoader.loadClass(SKIN_CLASS_NAME);
                Method loadThemePack =
                    skinLafClass.getMethod("loadThemePack", new Class[] { String.class });
                Method setSkin = skinLafClass.getMethod("setSkin", new Class[] { skinClass });
                Object skin =
                    loadThemePack.invoke(
                        skinLafClass,
                        new Object[] {
                             _plugin.getSkinThemePackFolder() + "/" + lafPrefs.getThemePackName()});
                setSkin.invoke(skinLafClass, new Object[] { skin });
            } catch (Exception ex) {
                Logger logger = _app.getLogger();
                logger.showMessage(
                    Logger.ILogTypes.ERROR,
                    "Error loading a Skinnable Look and Feel");
                logger.showMessage(Logger.ILogTypes.ERROR, ex.toString());
            }
        }

        // Set Look and Feel and update the main frame to use it.
        if (_lafClassLoader != null) {
            Class cls = Class.forName(lafClassName, true, _lafClassLoader);
            UIManager.setLookAndFeel((LookAndFeel) cls.newInstance());
            UIManager.getLookAndFeelDefaults().put("ClassLoader", _lafClassLoader);
        } else {
            Class cls = Class.forName(lafClassName);
            UIManager.setLookAndFeel((LookAndFeel) cls.newInstance());
        }
        Frame frame = _app.getMainFrame();
        if (frame != null) {
            SwingUtilities.updateComponentTreeUI(frame);
        }
    }

    /**
     * Install Look and Feels from their jars.
     */
    private void installLookAndFeels() {
        Logger log = _app.getLogger();

        // Retrieve URLs of all the Look and Feel jars and store in lafUrls.
        List lafUrls = new ArrayList();
        File dir = _plugin.getLookAndFeelFolder();
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            for (int i = 0; i < files.length; ++i) {
                File jarFile = files[i];
                String jarFileName = jarFile.getAbsolutePath();
                if (jarFile.isFile()
                    && (jarFileName.toLowerCase().endsWith(".zip")
                        || jarFileName.toLowerCase().endsWith(".jar"))) {
                    try {
                        lafUrls.add(jarFile.toURL());
                    } catch (IOException ex) {
                        log.showMessage(
                            Logger.ILogTypes.ERROR,
                            "Error occured reading Look and Feel jar: " + jarFileName);
                        log.showMessage(ex);
                    }
                }
            }
        }

        // Add the skin LAF jar to the list.
        try {
            File skinLafFile =
                new File(_plugin.getPluginAppFolder(), LAFConstants.SKINNABLE_LAF_JAR_NAME);
            lafUrls.add(skinLafFile.toURL());
        } catch (IOException ex) {
            log.showMessage(
                Logger.ILogTypes.ERROR,
                "Error occured reading Skin Look and Feel jar: "
                    + LAFConstants.SKINNABLE_LAF_JAR_NAME);
            log.showMessage(ex);
        }

        // Create a ClassLoader for all the LAF jars. Install all Look and Feels
        // into the UIManager.
        try {
            URL[] urls = ((URL[]) lafUrls.toArray(new URL[lafUrls.size()]));
            _lafClassLoader =
                new MyURLClassLoader((URL[]) lafUrls.toArray(new URL[lafUrls.size()]));
            Class[] lafClasses =
                _lafClassLoader.getAssignableClasses(LookAndFeel.class, log);
            List lafNames = new ArrayList();
            for (int i = 0; i < lafClasses.length; ++i) {
                Class lafClass = lafClasses[i];
                try {
                    LookAndFeel laf = (LookAndFeel) lafClass.newInstance();
                    LookAndFeelInfo info = new LookAndFeelInfo(laf.getName(), lafClass.getName());
                    UIManager.installLookAndFeel(info);
                    lafNames.add(lafClass.getName());
                    if (lafClass.getName().equals(this.SKINNABLE_LAF_CLASS_NAME)) {
                        _skinLookAndFeelName = laf.getName();
                    }
                } catch (Throwable th) {
                    log.showMessage(
                        Logger.ILogTypes.ERROR,
                        "Error occured loading Look and Feel: " + lafClass.getName());
                    log.showMessage(Logger.ILogTypes.ERROR, th);
                }
            }
        } catch (Throwable th) {
            log.showMessage(
                Logger.ILogTypes.ERROR,
                "Error occured trying to load Look and Feel classes");
            log.showMessage(Logger.ILogTypes.ERROR, th);
        }

    }
}