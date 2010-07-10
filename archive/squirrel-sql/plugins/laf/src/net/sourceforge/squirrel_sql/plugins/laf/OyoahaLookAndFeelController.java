package net.sourceforge.squirrel_sql.plugins.laf;
/*
 * Copyright (C) 2002 Colin Bell
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
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.xml.XMLObjectCache;

/**
 * Behaviour for the Oyoaha Look and Feel.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class OyoahaLookAndFeelController extends DefaultLookAndFeelController
{
	/** Logger for this class. */
	private static ILogger s_log =
		LoggerController.createLogger(OyoahaLookAndFeelController.class);

	/** Class name of the Oyoaha Look and Feel. */
	public static final String OA_LAF_CLASS_NAME =
		"com.oyoaha.swing.plaf.oyoaha.OyoahaLookAndFeel";

	/** Preferences for this LAF. */
	private OyoahaPreferences _prefs;

	/**
	 * Ctor specifying the Look and Feel plugin.
	 * 
	 * @param	plugin	The plugin that this controller is a part of.
	 */
	OyoahaLookAndFeelController(LAFPlugin plugin) throws IOException
	{
		super();

		XMLObjectCache cache = plugin.getSettingsCache();
		Iterator it = cache.getAllForClass(OyoahaPreferences.class);
		if (it.hasNext())
		{
			_prefs = (OyoahaPreferences)it.next();
		}
		else
		{
			_prefs = new OyoahaPreferences();
			try
			{
				cache.add(_prefs);
			}
			catch (DuplicateObjectException ex)
			{
				s_log.error("OyoahaPreferences object already in XMLObjectCache", ex);
			}
		}

		// Folder that stores themepacks for this LAF.
		File themePackDir = new File(plugin.getPluginAppSettingsFolder(), "oyoaha-theme-packs");
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
	public void aboutToBeInstalled(LAFRegister lafRegister, LookAndFeel laf) {
		try {
			final String dir = _prefs.getThemePackDirectory();
			final String name = _prefs.getThemePackName();
			if (dir != null && name != null)
			{
				File themePackFile = new File(dir, name);
				if (themePackFile.exists())
				{
					ClassLoader cl = lafRegister.getLookAndFeelClassLoader();
					Class oyLafClass = cl.loadClass(OA_LAF_CLASS_NAME);
					Method setTheme = oyLafClass.getMethod("setOyoahaTheme", new Class[] {File.class});
					Object[] parms = new Object[] { themePackFile };
					setTheme.invoke(laf, parms);
				}
			}
		} catch (Throwable th) {
			s_log.error("Error loading an Oyoaha theme", th);
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
		return new OyoahaPrefsPanel(this);
	}

	private static final class OyoahaPrefsPanel extends BaseLAFPreferencesPanelComponent
	{
		/**
		 * This interface defines locale specific strings. This should be
		 * replaced with a property file.
		 */
		interface OyoahaPrefsPanelI18n
		{
			String THEME_PACK = "Theme Pack:";
			String THEMEPACK_LOC = "Theme Pack Directory:";
		}

		private OyoahaLookAndFeelController _ctrl;
		private DirectoryListComboBox _themePackCmb = new DirectoryListComboBox();

		OyoahaPrefsPanel(OyoahaLookAndFeelController ctrl)
		{
			super(new GridBagLayout());
			_ctrl = ctrl;
			createUserInterface();
		}

		private void createUserInterface()
		{
			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.anchor = gbc.WEST;
			gbc.fill = gbc.HORIZONTAL;
			gbc.insets = new Insets(4, 4, 4, 4);

			gbc.gridx = 0;
			gbc.gridy = 0;
			add(new JLabel(OyoahaPrefsPanelI18n.THEME_PACK, SwingConstants.RIGHT), gbc);

			++gbc.gridx;
			add(_themePackCmb, gbc);

			gbc.gridx = 0;
			++gbc.gridy;
			add(new JLabel(OyoahaPrefsPanelI18n.THEMEPACK_LOC, SwingConstants.RIGHT), gbc);

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
			final FileExtensionFilter filter = new FileExtensionFilter("OTM files", new String[] {".otm"});
			_themePackCmb.load(new File(themePackDir), filter);
			_themePackCmb.setSelectedItem(_ctrl._prefs.getThemePackName());
		}

		/**
		 * @see BaseLAFPreferencesPanelComponent#applyChanges()
		 */
		public void applyChanges()
		{
			super.applyChanges();
			_ctrl._prefs.setThemePackName((String)_themePackCmb.getSelectedItem());
		}
	}

	public static final class OyoahaPreferences implements IHasIdentifier
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

