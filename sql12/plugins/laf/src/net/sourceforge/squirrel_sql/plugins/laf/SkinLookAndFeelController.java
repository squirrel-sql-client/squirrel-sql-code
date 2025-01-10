package net.sourceforge.squirrel_sql.plugins.laf;

/*
 * Copyright (C) 2002-2003 Colin Bell
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

import net.sourceforge.squirrel_sql.fw.util.DuplicateObjectException;
import net.sourceforge.squirrel_sql.fw.util.FileWrapper;
import net.sourceforge.squirrel_sql.fw.util.FileWrapperFactory;
import net.sourceforge.squirrel_sql.fw.util.FileWrapperFactoryImpl;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.xml.XMLObjectCache;

import javax.swing.LookAndFeel;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Iterator;

/**
 * Behaviour for the Skin Look and Feel.
 * 
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SkinLookAndFeelController extends DefaultLookAndFeelController
{
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SkinLookAndFeelController.class);

	/** Logger for this class. */
	private static final ILogger s_log = LoggerController.createLogger(SkinLookAndFeelController.class);

	/** Class name of the Skin Look and Feel. */
	public static final String SKINNABLE_LAF_CLASS_NAME = "com.l2fprod.gui.plaf.skin.SkinLookAndFeel";

	/** Class name of the Skin class. */
	public static final String SKIN_CLASS_NAME = "com.l2fprod.gui.plaf.skin.Skin";

	/** Preferences for this LAF. */
	private SkinPreferences _prefs;

	/** factory for creating FileWrappers which insulate the application from direct reference to File */
	private FileWrapperFactory fileWrapperFactory = new FileWrapperFactoryImpl();

	/**
	 * Ctor specifying the Look and Feel plugin.
	 * 
	 * @param plugin
	 *           The plugin that this controller is a part of.
	 */
	SkinLookAndFeelController(LAFPlugin plugin) throws IOException
	{
		super();

		XMLObjectCache cache = plugin.getSettingsCache();
		Iterator<?> it = cache.getAllForClass(SkinPreferences.class);
		if (it.hasNext())
		{
			_prefs = (SkinPreferences) it.next();
		}
		else
		{
			_prefs = new SkinPreferences();
			try
			{
				cache.add(_prefs);
			}
			catch (DuplicateObjectException ex)
			{
				s_log.error("SkinPreferences object already in XMLObjectCache", ex);
			}
		}

		// Folder that stores themepacks for this LAF.
		FileWrapper themePackDir =
			fileWrapperFactory.create(plugin.getPluginAppSettingsFolder(), "skinlf-theme-packs");
		_prefs.setThemePackDirectory(themePackDir.getAbsolutePath());
		if (!themePackDir.exists())
		{
			themePackDir.mkdirs();
		}
	}

	/**
	 * This Look and Feel is about to be installed. Load the selected themepack.
	 */
	public void aboutToBeInstalled(LAFRegister lafRegister, LookAndFeel laf)
	{
		try
		{
			final String dir = _prefs.getThemePackDirectory();
			final String name = _prefs.getThemePackName();
			if (dir != null && name != null)
			{
				File themePackFile = new File(dir, name);
				if (themePackFile.exists())
				{
					ClassLoader cl = lafRegister.getLookAndFeelClassLoader();
					Class<?> skinLafClass = Class.forName(SKINNABLE_LAF_CLASS_NAME, false, cl);
					Class<?> skinClass = Class.forName(SKIN_CLASS_NAME, false, cl);

					Method loadThemePack = skinLafClass.getMethod("loadThemePack", new Class[] { String.class });
					Method setSkin = skinLafClass.getMethod("setSkin", new Class[] { skinClass });

					Object[] parms = new Object[] { dir + "/" + name };
					Object skin = loadThemePack.invoke(skinLafClass, parms);
					setSkin.invoke(skinLafClass, new Object[] { skin });
				}
			}
		}
		catch (Throwable th)
		{
			s_log.error("Error loading a Skinnable Look and Feel", th);
		}

	}

	/**
	 * This Look and Feel has just been installed.
	 */
	public void hasBeenInstalled(LAFRegister lafRegister, LookAndFeel laf)
	{
	}

	/**
	 * @see ILookAndFeelController#getPreferencesComponent()
	 */
	public BaseLAFPreferencesPanelComponent getPreferencesComponent()
	{
		return new SkinPrefsPanel(this);
	}

	/**
	 * @param fileWrapperFactory
	 *           the fileWrapperFactory to set
	 */
	public void setFileWrapperFactory(FileWrapperFactory fileWrapperFactory)
	{
		Utilities.checkNull("setFileWrapperFactory", "fileWrapperFactory", fileWrapperFactory);
		this.fileWrapperFactory = fileWrapperFactory;
	}

	public SkinPreferences getSkinPreferences()
	{
		return _prefs;
	}
}
