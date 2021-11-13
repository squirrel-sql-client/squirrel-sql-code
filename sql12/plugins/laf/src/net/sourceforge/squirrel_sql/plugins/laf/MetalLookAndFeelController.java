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

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.UIManager;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalTheme;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Behaviour for the jGoodies Plastic Look and Feel. It also takes
 * responsibility for the metal Look and Feel.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class MetalLookAndFeelController extends AbstractPlasticController
{
	private static ILogger s_log = LoggerController.createLogger(MetalLookAndFeelController.class);

	public static final String METAL_LAF_CLASS_NAME = MetalLookAndFeel.class.getName();

	private String[] _extraThemeClassNames = new String[0];

	private MetalThemePreferences _currentThemePrefs;

	private MetalTheme _defaultMetalTheme;
	private HashMap<String, MetalTheme> _themesByName = new HashMap<>();

   /**
	 * Ctor specifying the Look and Feel plugin and register.
	 * 
	 * @param	plugin		The plugin that this controller is a part of.
	 * @param	lafRegister	LAF register.
	 */
	MetalLookAndFeelController(LAFPlugin plugin, LAFRegister lafRegister)
	{
      super(plugin, lafRegister);

		_extraThemeClassNames = new String[]
				{
						MetalThemePreferencesUtil.DEFAULT_METAL_THEME_CLASS_NAME,
						////////////////////////////////////////////////////////////////////
						// These classes have no package see swingsetthemes.jar
						"AquaTheme",
						MetalThemePreferencesUtil.CHARCOAL_THEME_CLASS_NAME,
						"ContrastTheme",
						"EmeraldTheme",
						"RubyTheme",
						//
						///////////////////////////////////////////////////////////////////

						/////////////////////////////////////////////////////////////////////////////
						// This theme was presented to SQuirreL by Karsten Lentzsch of jgoodies.com.
						// It is SQuirreL's default theme if the LAF plugin is not used.
						// Here we make the AllBluesBoldMetalTheme also available within the LAF plugin.
						// Thanks a lot Karsten.
						"net.sourceforge.squirrel_sql.client.gui.laf.AllBluesBoldMetalTheme",
						//
						////////////////////////////////////////////////////////////////////////////
				};

		_defaultMetalTheme = new DefaultMetalTheme();

		_currentThemePrefs = MetalThemePreferencesUtil.getMetalThemePreferences(plugin, getLAFRegister());
   }

	/**
	 * Retrieve extra themes for this Look and Feel.
	 * 
	 * @return	The default metal theme. 
	 */
	MetalTheme[] getExtraThemes()
	{
		ClassLoader cl = getLAFRegister().getLookAndFeelClassLoader();

		ArrayList<MetalTheme> ret = new ArrayList<>();

		boolean defaultThemeIsIncluded = false;

		for (int i = 0; i < _extraThemeClassNames.length; ++i)
		{
			try
			{
				Class<?> clazz = Class.forName(_extraThemeClassNames[i], false, cl);

            MetalTheme metalTheme = (MetalTheme) clazz.getDeclaredConstructor().newInstance();
            _themesByName.put(metalTheme.getName(), metalTheme);
            ret.add(metalTheme);

				if(null != _defaultMetalTheme && _extraThemeClassNames[i].equals(_defaultMetalTheme.getClass().getName()))
				{
					defaultThemeIsIncluded = true;
				}
			}
			catch (Throwable th)
			{
				s_log.error("Error loading theme " + _extraThemeClassNames[i], th);
			}
		}

		if(false == defaultThemeIsIncluded)
		{
			ret.add(_defaultMetalTheme);
		}


		return ret.toArray(new MetalTheme[ret.size()]);
	}


   MetalTheme getThemeForName(String name)
   {
      MetalTheme ret = super.getThemeForName(name);

      if(null == ret)
      {
         ret = _themesByName.get(name);
      }

      return ret;
   }


	void installMetalTheme(MetalTheme theme)
	{
		// This works only on JDK 1.5
		// With JDK 1.4.x fonts will be bold for all SwingSet themes.
		// See also SwingSet2 demos in JDK 1.4 and JDK 1.5
		UIManager.put("swing.boldMetal", Boolean.FALSE);

		MetalLookAndFeel.setCurrentTheme(theme);
	}

	/**
	 * Retrieve the name of the current theme.
	 * 
	 * @return	Name of the current theme.
	 */
	String getCurrentThemeName()
	{
		return _currentThemePrefs.getThemeName();
	}

	/**
	 * Set the current theme name.
	 * 
	 * @param name		name of the current theme.
	 */
	void setCurrentThemeName(String name)
	{
		_currentThemePrefs.setThemeName(name);
	}

	public void applyTheme(String metalThemeClassName)
	{
		final MetalTheme theme = super.getMetalThemeForClassName(metalThemeClassName);
		_currentThemePrefs.setThemeName(theme.getName());
		getPlasticPrefsPanel().loadPreferencesPanel();
		installMetalTheme(theme);
	}


	/**
	 * Preferences for the Metal LAF. Subclassed purely to give it a
	 * different data type when stored in the plugin preferences so that it
	 * doesn't get mixed up with preferences for other subclasses of
	 * AbstractPlasticController
	 */
	public static final class MetalThemePreferences extends ThemePreferences
	{
	}
}

