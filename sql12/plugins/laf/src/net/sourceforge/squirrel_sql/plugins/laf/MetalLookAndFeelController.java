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

import javax.swing.LookAndFeel;
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

	/** Preferences for this LAF. */
	private MetalThemePreferences _prefs;

	private MetalTheme _defaultMetalTheme;

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

		_defaultMetalTheme = new DefaultMetalTheme();

		XMLObjectCache cache = plugin.getSettingsCache();
		Iterator it = cache.getAllForClass(MetalThemePreferences.class);
		if (it.hasNext())
		{
			_prefs = (MetalThemePreferences)it.next();
		}
		else
		{
			_prefs = new MetalThemePreferences();
			try
			{
				cache.add(_prefs);
			}
			catch (DuplicateObjectException ex)
			{
				s_log.error("MetalThemePreferences object already in XMLObjectCache", ex);
			}
		}
	}
	
	/**
	 * Retrieve extra themes for this Look and Feel.
	 * 
	 * @return	The default metal theme. 
	 */
	MetalTheme[] getExtraThemes()
	{
		return new MetalTheme[] {_defaultMetalTheme};
	}

	void installCurrentTheme(LookAndFeel laf, MetalTheme theme)
	{
		MetalLookAndFeel.setCurrentTheme(theme);
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

