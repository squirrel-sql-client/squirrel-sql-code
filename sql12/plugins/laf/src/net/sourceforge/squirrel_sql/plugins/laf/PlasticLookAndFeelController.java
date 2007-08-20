package net.sourceforge.squirrel_sql.plugins.laf;
/*
 * Copyright (C) 2003-2006 Colin Bell
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

import javax.swing.LookAndFeel;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalTheme;

import net.sourceforge.squirrel_sql.fw.util.BaseException;
import net.sourceforge.squirrel_sql.fw.util.DuplicateObjectException;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.xml.XMLObjectCache;
/**
 * Behaviour for the jGoodies Plastic Look and Feel.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
class PlasticLookAndFeelController extends AbstractPlasticController
{
	/** Logger for this class. */
	private static ILogger s_log =
		LoggerController.createLogger(PlasticLookAndFeelController.class);

	/**
	 * Look and Feel class names that this controller is responsible for.
	 */
	static final String[] LAF_CLASS_NAMES = new String[]
	{
		"com.jgoodies.looks.plastic.PlasticLookAndFeel",
		"com.jgoodies.looks.plastic.Plastic3DLookAndFeel",
		"com.jgoodies.looks.plastic.PlasticXPLookAndFeel",
	};

	public static final String DEFAULT_LOOK_AND_FEEL_CLASS_NAME = LAF_CLASS_NAMES[1];

	/** Base class for all Plastic themes. */
	private static final String THEME_BASE_CLASS = "com.jgoodies.looks.plastic.PlasticTheme";

	/** Preferences for this LAF. */
	private PlasticThemePreferences _prefs;

   /**
	 * Ctor specifying the Look and Feel plugin and register.
	 * 
	 * @param	plugin	The plugin that this controller is a part of.
	 * @param	lafRegister	LAF register.
	 */
	PlasticLookAndFeelController(LAFPlugin plugin,
								LAFRegister lafRegister)
	{
      super(plugin, lafRegister);
      try
      {

         XMLObjectCache cache = plugin.getSettingsCache();
         Iterator<?> it = cache.getAllForClass(PlasticThemePreferences.class);
         if (it.hasNext())
         {
            _prefs = (PlasticThemePreferences) it.next();
         }
         else
         {
            _prefs = new PlasticThemePreferences();

            ClassLoader cl = getLAFRegister().getLookAndFeelClassLoader();
            Class<?> clazz = 
            	Class.forName(AbstractPlasticController.DEFAULT_PLASTIC_THEME_CLASS_NAME, false, cl);
            MetalTheme theme = (MetalTheme) clazz.newInstance();
            _prefs.setThemeName(theme.getName());

            try
            {
               cache.add(_prefs);
            }
            catch (DuplicateObjectException ex)
            {
               s_log.error("PlasticThemePreferences object already in XMLObjectCache", ex);
            }
         }
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

	/**
	 * Retrieve the name of the current theme.
	 * 
	 * @return	Name of the current theme.
	 */
	String getCurrentThemeName()
	{
		return _prefs.getThemeName();
	}

	/**
	 * Set the current theme name.
	 * 
	 * @param name		name of the current theme.
	 */
	void setCurrentThemeName(String name)
	{
		_prefs.setThemeName(name);
	}

	void installCurrentTheme(LookAndFeel laf, MetalTheme theme)
		throws BaseException
	{
		try
		{
			ClassLoader cl = getLAFRegister().getLookAndFeelClassLoader();
			Class<?> themeBaseClass;
			try
			{
				themeBaseClass = Class.forName(THEME_BASE_CLASS, false, cl);
			}
			catch (Throwable th)
			{
				s_log.error("Error loading theme base class " + THEME_BASE_CLASS, th);
				throw new BaseException(th);
			}

			// Ensure that this is a Plastic Theme.
			if (!themeBaseClass.isAssignableFrom(theme.getClass()))
			{
				throw new BaseException("NonPlastic Theme passed in");
			}


         // Note: which jar is used is specified in LAFPluginResources.properties
//         try
//         {
//            // This works for the old looks-1.3.1.jar
//            Method method = laf.getClass().getMethod("setMyCurrentTheme", new Class[] { themeBaseClass });
//            Object[] parms = new Object[] { theme };
//            method.invoke(laf, parms);
//         }
//         catch (NoSuchMethodException e)
//         {
//            // This works for the newer looks.jar
//            MetalLookAndFeel.setCurrentTheme(theme);
//         }
          MetalLookAndFeel.setCurrentTheme(theme);
      }
		catch (Throwable th)
		{
			throw new BaseException(th);
		}
	}

	/**
	 * Preferences for the Plastic LAFs. Subclassed purely to give it a
	 * different data type when stored in the plugin preferences so that it
	 * doesn't get mixed up with preferences for other subclasses of
	 * AbstractPlasticController
	 */
	public static final class PlasticThemePreferences
		extends AbstractPlasticController.ThemePreferences
	{
	}
}

