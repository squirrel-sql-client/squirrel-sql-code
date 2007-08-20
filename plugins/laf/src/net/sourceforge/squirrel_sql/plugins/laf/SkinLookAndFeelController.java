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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Iterator;

import javax.swing.JLabel;
import javax.swing.LookAndFeel;
import javax.swing.SwingConstants;

import net.sourceforge.squirrel_sql.fw.gui.DirectoryListComboBox;
import net.sourceforge.squirrel_sql.fw.gui.OutputLabel;
import net.sourceforge.squirrel_sql.fw.id.IHasIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IntegerIdentifier;
import net.sourceforge.squirrel_sql.fw.util.DuplicateObjectException;
import net.sourceforge.squirrel_sql.fw.util.FileExtensionFilter;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.xml.XMLObjectCache;
/**
 * Behaviour for the Skin Look and Feel.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SkinLookAndFeelController extends DefaultLookAndFeelController
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(SkinLookAndFeelController.class);


	/** Logger for this class. */
	private static final ILogger s_log =
		LoggerController.createLogger(SkinLookAndFeelController.class);

	/** Class name of the Skin Look and Feel. */
	public static final String SKINNABLE_LAF_CLASS_NAME =
		"com.l2fprod.gui.plaf.skin.SkinLookAndFeel";

	/** Class name of the Skin class. */
	public static final String SKIN_CLASS_NAME = "com.l2fprod.gui.plaf.skin.Skin";

	/** Preferences for this LAF. */
	private SkinPreferences _prefs;

	/**
	 * Ctor specifying the Look and Feel plugin.
	 * 
	 * @param	plugin	The plugin that this controller is a part of.
	 */
	SkinLookAndFeelController(LAFPlugin plugin) throws IOException
	{
		super();

		XMLObjectCache cache = plugin.getSettingsCache();
		Iterator<?> it = cache.getAllForClass(SkinPreferences.class);
		if (it.hasNext())
		{
			_prefs = (SkinPreferences)it.next();
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
		File themePackDir = new File(plugin.getPluginAppSettingsFolder(), "skinlf-theme-packs");
		_prefs.setThemePackDirectory(themePackDir.getAbsolutePath());
		if (!themePackDir.exists())
		{
			themePackDir.mkdirs();
		}
	}

	/**
	 * This Look and Feel is about to be installed. Load the selected
	 * themepack.
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
					Class<?> skinLafClass = 
						Class.forName(SKINNABLE_LAF_CLASS_NAME, false, cl);
					Class<?> skinClass = 
						Class.forName(SKIN_CLASS_NAME, false, cl);

					Method loadThemePack =
						skinLafClass.getMethod("loadThemePack",
											new Class[] { String.class });
					Method setSkin =
						skinLafClass.getMethod(
							"setSkin", new Class[] { skinClass });

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

	private static final class SkinPrefsPanel extends BaseLAFPreferencesPanelComponent
	{
        private static final long serialVersionUID = 1L;

        /**
		 * This interface defines locale specific strings. This should be
		 * replaced with a property file.
		 */
		interface SkinPrefsPanelI18n
		{
			// i18n[laf.skinThemPack=Theme Pack:]
			String THEME_PACK = s_stringMgr.getString("laf.skinThemPack");
			// i18n[laf.skinThemePackDir=Theme Pack Directory:]
			String THEMEPACK_LOC = s_stringMgr.getString("laf.skinThemePackDir");
		}

		private SkinLookAndFeelController _ctrl;
		private DirectoryListComboBox _themePackCmb = new DirectoryListComboBox();

		SkinPrefsPanel(SkinLookAndFeelController ctrl)
		{
			super(new GridBagLayout());
			_ctrl = ctrl;
			createUserInterface();
		}

		private void createUserInterface()
		{
			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.anchor = GridBagConstraints.WEST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(4, 4, 4, 4);

			gbc.gridx = 0;
			gbc.gridy = 0;
			add(new JLabel(SkinPrefsPanelI18n.THEME_PACK, SwingConstants.RIGHT), gbc);

			++gbc.gridx;
			add(_themePackCmb, gbc);

			gbc.gridx = 0;
			++gbc.gridy;
			add(new JLabel(SkinPrefsPanelI18n.THEMEPACK_LOC, SwingConstants.RIGHT), gbc);

			++gbc.gridx;
			final String themePackDir = _ctrl._prefs.getThemePackDirectory();
			add(new OutputLabel(themePackDir), gbc);
		}
	
		/**
		 * @see BaseLAFPreferencesPanelComponent#loadPreferencesPanel()
		 */
		public void loadPreferencesPanel()
		{
			super.loadPreferencesPanel();
			final String themePackDir = _ctrl._prefs.getThemePackDirectory();
			// i18n[laf.jarZip=JAR/Zip files]
			final FileExtensionFilter filter = new FileExtensionFilter(s_stringMgr.getString("laf.jarZip"), new String[] { ".jar", ".zip" });
			_themePackCmb.load(new File(themePackDir), filter);
			_themePackCmb.setSelectedItem(_ctrl._prefs.getThemePackName());
			if (_themePackCmb.getSelectedIndex() == -1 &&
					_themePackCmb.getModel().getSize() > 0)
			{
				_themePackCmb.setSelectedIndex(0);
			}
		}

		/**
		 * @see BaseLAFPreferencesPanelComponent#applyChanges()
		 */
		public boolean applyChanges()
		{
			super.applyChanges();
			_ctrl._prefs.setThemePackName((String)_themePackCmb.getSelectedItem());
			
			// Force the LAF to be set even if Skin is the current one. This
			// allows a change in theme to take affect.
			return true;
		}
	}

	public static final class SkinPreferences implements IHasIdentifier
	{
		private String _themePackDir;
		private String _themePackName;
		private IntegerIdentifier _id = new IntegerIdentifier(1);

		public String getThemePackDirectory()
		{
			return _themePackDir;
		}

		public void setThemePackDirectory(String value)
		{
			_themePackDir = value;
		}

		public String getThemePackName()
		{
			return _themePackName;
		}

		public void setThemePackName(String value)
		{
			_themePackName = value;
		}

		/**
		 * @return		The unique identifier for this object.
		 */
		public IIdentifier getIdentifier()
		{
			return _id;
		}
	}
}

