package net.sourceforge.squirrel_sql.plugins.laf;
/*
 * Copyright (C) 2003 Colin Bell
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
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.LookAndFeel;
import javax.swing.SwingConstants;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalTheme;

import net.sourceforge.squirrel_sql.fw.id.IHasIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IntegerIdentifier;
import net.sourceforge.squirrel_sql.fw.util.DuplicateObjectException;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.xml.XMLObjectCache;
/**
 * Behaviour for the jGoodies Plastic Look and Feel. It also takes
 * responsibility for the metal Look and Feel.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class PlasticLookAndFeelController extends DefaultLookAndFeelController
{
	/** Logger for this class. */
	private static ILogger s_log =
		LoggerController.createLogger(PlasticLookAndFeelController.class);

	private final LAFRegister _lafRegister;

	/**
	 * Look and Feel class names that this controller is responsible for. All the
	 * Plastic LAFs as well as Metal.
	 */
	static final String[] LAF_CLASS_NAMES = new String[]
	{
		"com.jgoodies.plaf.plastic.PlasticLookAndFeel",
		"com.jgoodies.plaf.plastic.Plastic3DLookAndFeel",
		"com.jgoodies.plaf.plastic.PlasticXPLookAndFeel",
		javax.swing.plaf.metal.MetalLookAndFeel.class.getName(),
	};

	/** Name of package that contains Plastic Themes. */
	private static final String THEME_PACKAGE = "com.jgoodies.plaf.plastic.theme";

	/** Base class for all Plastic themes. */
	private static final String THEME_BASE_CLASS = "com.jgoodies.plaf.plastic.PlasticTheme";

	/** Preferences for this LAF. */
	private ThemePreferences _prefs;

	/** Collection of all the themes keyed by the theme name. */
	private Map _themes = new TreeMap();

	/** Class names of all the themes. */
	private static final String[] THEME_CLASS_NAMES = new String[]
	{
		"com.jgoodies.plaf.plastic.theme.BrownSugar",
		"com.jgoodies.plaf.plastic.theme.DarkStar",
		"com.jgoodies.plaf.plastic.theme.DesertBlue",
		"com.jgoodies.plaf.plastic.theme.DesertBluer",
		"com.jgoodies.plaf.plastic.theme.DesertGreen",
		"com.jgoodies.plaf.plastic.theme.DesertRed",
		"com.jgoodies.plaf.plastic.theme.DesertYellow",
		"com.jgoodies.plaf.plastic.theme.ExperienceBlue",
		"com.jgoodies.plaf.plastic.theme.ExperienceGreen",
		"com.jgoodies.plaf.plastic.theme.Silver",
		"com.jgoodies.plaf.plastic.theme.SkyBlue",
		"com.jgoodies.plaf.plastic.theme.SkyBluer",
		"com.jgoodies.plaf.plastic.theme.SkyBluerTahoma",
		"com.jgoodies.plaf.plastic.theme.SkyGreen",
		"com.jgoodies.plaf.plastic.theme.SkyKrupp",
		"com.jgoodies.plaf.plastic.theme.SkyPink",
		"com.jgoodies.plaf.plastic.theme.SkyRed",
		"com.jgoodies.plaf.plastic.theme.SkyYellow",
	};

	/**
	 * Ctor specifying the Look and Feel plugin.
	 * 
	 * @param	plugin	The plugin that this controller is a part of.
	 */
	PlasticLookAndFeelController(LAFPlugin plugin,
								LAFRegister lafRegister) throws IOException
	{
		super();

		_lafRegister = lafRegister;

		// Load themes.
		loadThemes();

		XMLObjectCache cache = plugin.getSettingsCache();
		Iterator it = cache.getAllForClass(ThemePreferences.class);
		if (it.hasNext())
		{
			_prefs = (ThemePreferences)it.next();
		}
		else
		{
			_prefs = new ThemePreferences();
			try
			{
				cache.add(_prefs);
			}
			catch (DuplicateObjectException ex)
			{
				s_log.error("ThemePreferences object already in XMLObjectCache", ex);
			}
		}
	}

	/**
	 * This Look and Feel is about to be installed. Apply the selected
	 * theme.
	 */
	public void aboutToBeInstalled(LAFRegister lafRegister, LookAndFeel laf)
	{
		try
		{
			final String name = _prefs.getThemeName();
			if (name != null)
			{
				Object theme = _themes.get(name);
				if (theme != null)
				{
					ClassLoader cl = lafRegister.getLookAndFeelClassLoader();
					Class themeBaseClass;
					try
					{
						themeBaseClass = cl.loadClass(THEME_BASE_CLASS);
					}
					catch (Throwable th)
					{
						s_log.error("Error loading theme base class " + THEME_BASE_CLASS, th);
						return;
					}

					Class lafClazz = laf.getClass();
					Method method;
					if (lafClazz == MetalLookAndFeel.class)
					{
						method = lafClazz.getMethod("setCurrentTheme",
														new Class[] { MetalTheme.class });
					}
					else
					{
						method = lafClazz.getMethod("setMyCurrentTheme",
														new Class[] { themeBaseClass });
					}
					Object[] parms = new Object[] { theme };
					method.invoke(laf, parms);
				}
			}
		}
		catch (Throwable th)
		{
			s_log.error("Error installing a PlasticTheme", th);
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
		return new PlasticPrefsPanel(this);
	}

	private void loadThemes()
	{
		_themes.clear();
		ClassLoader cl = _lafRegister.getLookAndFeelClassLoader();
		for (int i = 0; i < THEME_CLASS_NAMES.length; ++i)
		{
			try
			{
				Class clazz = cl.loadClass(THEME_CLASS_NAMES[i]);
				Object theme = clazz.newInstance();

				Method getName = clazz.getMethod("getName", new Class[0]);
				Object name = getName.invoke(theme, new Object[0]);

				_themes.put(name.toString(), theme); 
			}
			catch (Throwable th)
			{
				s_log.error("Error loading theme " + THEME_CLASS_NAMES[i], th);
			}
		}
	}

	private static final class PlasticPrefsPanel extends BaseLAFPreferencesPanelComponent
	{
		/**
		 * This interface defines locale specific strings. This should be
		 * replaced with a property file.
		 */
		interface i18n
		{
			String THEME = "Theme:";
		}

		private PlasticLookAndFeelController _ctrl;
		private JComboBox _themeCmb;
		private int _origSelThemeIdx;

		PlasticPrefsPanel(PlasticLookAndFeelController ctrl)
		{
			super();
			_ctrl = ctrl;
			createUserInterface();
		}

		private void createUserInterface()
		{
			setLayout(new GridBagLayout());
			_themeCmb = new JComboBox();

			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.anchor = GridBagConstraints.WEST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(4, 4, 4, 4);

			gbc.gridx = 0;
			gbc.gridy = 0;
			add(new JLabel(i18n.THEME, SwingConstants.RIGHT), gbc);

			++gbc.gridx;
			add(_themeCmb, gbc);
		}
	
		/**
		 * @see BaseLAFPreferencesPanelComponent#loadPreferencesPanel()
		 */
		public void loadPreferencesPanel()
		{
			super.loadPreferencesPanel();

			_themeCmb.removeAllItems();
			final Iterator it = _ctrl._themes.keySet().iterator();
			while (it.hasNext())
			{
				_themeCmb.addItem((String)it.next());
			}

			if (_themeCmb.getModel().getSize() > 0)
			{
				String selThemeName = _ctrl._prefs.getThemeName();
				if (selThemeName != null && selThemeName.length() > 0)
				{
					_themeCmb.setSelectedItem(selThemeName);
				}
				if (_themeCmb.getSelectedIndex() == -1)
				{
					_themeCmb.setSelectedIndex(0);
				}
			}
			_origSelThemeIdx = _themeCmb.getSelectedIndex();
		}

		/**
		 * @see BaseLAFPreferencesPanelComponent#applyChanges()
		 */
		public boolean applyChanges()
		{
			super.applyChanges();
			if (_origSelThemeIdx != _themeCmb.getSelectedIndex())
			{
				_ctrl._prefs.setThemeName((String)_themeCmb.getSelectedItem());
				return true;
			}
			return false;
		}
	}

	public static final class ThemePreferences implements IHasIdentifier
	{
		private String _themeName;
		private IntegerIdentifier _id = new IntegerIdentifier(1);

		public String getThemeName()
		{
			return _themeName;
		}

		public void setThemeName(String value)
		{
			_themeName = value;
		}

		/**
		 * @return	The unique identifier for this object.
		 */
		public IIdentifier getIdentifier()
		{
			return _id;
		}
	}
}

