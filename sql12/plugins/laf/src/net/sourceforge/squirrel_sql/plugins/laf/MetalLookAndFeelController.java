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
import java.util.Iterator;
import java.util.Vector;
import java.util.HashMap;

import javax.swing.*;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalTheme;

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
class MetalLookAndFeelController extends AbstractPlasticController
{
	/** Logger for this class. */
	private static ILogger s_log =
		LoggerController.createLogger(MetalLookAndFeelController.class);

	static final String METAL_LAF_CLASS_NAME = MetalLookAndFeel.class.getName();

	private String[] _extraThemeClassNames = new String[0];



	/** Preferences for this LAF. */
	private MetalThemePreferences _currentThemePrefs;

	private MetalTheme _defaultMetalTheme;
   private static final String DEFAULT_METAL_THEME = "javax.swing.plaf.metal.OceanTheme";
   private HashMap<String, MetalTheme> _themesByName = new HashMap<String, MetalTheme>();

   /**
	 * Ctor specifying the Look and Feel plugin and register.
	 * 
	 * @param	plugin		The plugin that this controller is a part of.
	 * @param	lafRegister	LAF register.
	 */
	MetalLookAndFeelController(LAFPlugin plugin,
										LAFRegister lafRegister)
	{

      super(plugin, lafRegister);

      try
      {
         _extraThemeClassNames = new String[]
            {
               DEFAULT_METAL_THEME,
               ////////////////////////////////////////////////////////////////////
               // These classes have no package see swingsetthemes.jar
               "AquaTheme",
               "CharcoalTheme",
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

         XMLObjectCache cache = plugin.getSettingsCache();
         Iterator<?> it = cache.getAllForClass(MetalThemePreferences.class);
         if (it.hasNext())
         {
            _currentThemePrefs = (MetalThemePreferences) it.next();
         }
         else
         {
            _currentThemePrefs = new MetalThemePreferences();

            ClassLoader cl = getLAFRegister().getLookAndFeelClassLoader();
            Class<?> clazz = Class.forName(MetalLookAndFeelController.DEFAULT_METAL_THEME, false, cl);
            MetalTheme theme = (MetalTheme) clazz.newInstance();
            _currentThemePrefs.setThemeName(theme.getName());

            try
            {
               cache.add(_currentThemePrefs);
            }
            catch (DuplicateObjectException ex)
            {
               s_log.error("MetalThemePreferences object already in XMLObjectCache", ex);
            }
         }
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

	/**
	 * Retrieve extra themes for this Look and Feel.
	 * 
	 * @return	The default metal theme. 
	 */
	MetalTheme[] getExtraThemes()
	{
		ClassLoader cl = getLAFRegister().getLookAndFeelClassLoader();

		Vector<MetalTheme> ret = new Vector<MetalTheme>();

		boolean defaultThemeIsIncluded = false;

		for (int i = 0; i < _extraThemeClassNames.length; ++i)
		{
			try
			{
				Class<?> clazz = Class.forName(_extraThemeClassNames[i], false, cl);

            MetalTheme metalTheme = (MetalTheme) clazz.newInstance();
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


	void installCurrentTheme(LookAndFeel laf, MetalTheme theme)
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


	/**
	 * Preferences for the Metal LAF. Subclassed purely to give it a
	 * different data type when stored in the plugin preferences so that it
	 * doesn't get mixed up with preferences for other subclasses of
	 * AbstractPlasticController
	 */
	public static final class MetalThemePreferences
		extends AbstractPlasticController.ThemePreferences
	{
	}
}

