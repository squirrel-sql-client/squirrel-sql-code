package net.sourceforge.squirrel_sql.plugins.laf;
/*
 * Copyright (C) 2003-20066 Colin Bell
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

import javax.swing.LookAndFeel;
import javax.swing.plaf.metal.MetalTheme;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
/**
 * Base class for the LAF controllers for Plastic and Metal.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
abstract class AbstractPlasticController extends DefaultLookAndFeelController
{
	private static ILogger s_log = LoggerController.createLogger(AbstractPlasticController.class);

	/** Class names of all the Plastic themes. */
	protected static final String[] PLASTIC_THEME_CLASS_NAMES = new String[]
	{
			"com.jgoodies.looks.plastic.theme.BrownSugar",
			"com.jgoodies.looks.plastic.theme.DarkStar",
			"com.jgoodies.looks.plastic.theme.DesertBlue",
			"com.jgoodies.looks.plastic.theme.DesertBluer",
			"com.jgoodies.looks.plastic.theme.DesertGreen",
			"com.jgoodies.looks.plastic.theme.DesertRed",
			"com.jgoodies.looks.plastic.theme.DesertYellow",
			"com.jgoodies.looks.plastic.theme.ExperienceBlue",
			"com.jgoodies.looks.plastic.theme.ExperienceGreen",
			"com.jgoodies.looks.plastic.theme.ExperienceRoyale",
			"com.jgoodies.looks.plastic.theme.LightGray",
			"com.jgoodies.looks.plastic.theme.Silver",
			"com.jgoodies.looks.plastic.theme.SkyBlue",
			"com.jgoodies.looks.plastic.theme.SkyBluer",
			"com.jgoodies.looks.plastic.theme.SkyGreen",
			"com.jgoodies.looks.plastic.theme.SkyKrupp",
			"com.jgoodies.looks.plastic.theme.SkyPink",
			"com.jgoodies.looks.plastic.theme.SkyRed",
			"com.jgoodies.looks.plastic.theme.SkyYellow",
	};

   public static final String DEFAULT_PLASTIC_THEME_CLASS_NAME = PLASTIC_THEME_CLASS_NAMES[11]; // SkyBluer

	/** Look and Feel Plugin. */
	private final LAFPlugin _lafPlugin;

	/** Look and Feel Register. */
	private final LAFRegister _lafRegister;

	/** The Plastic themes keyed by the theme name. */
	private Map<String, MetalTheme> _themes = new TreeMap<>();

	private PlasticPrefsPanel _plasticPrefsPanel;

	/**
	 * Ctor specifying the Look and Feel plugin and register.
	 * 
	 * @param	plugin		The plugin that this controller is a part of.
	 * @param	lafRegister	LAF register.
	 */
	AbstractPlasticController(LAFPlugin plugin, LAFRegister lafRegister)
	{
		_lafPlugin = plugin;
		_lafRegister = lafRegister;
	}

	/**
	 * Initialization.
	 */
	public void initialize()
	{
		_themes.clear();

		ClassLoader cl = getLAFRegister().getLookAndFeelClassLoader();
		MetalTheme[] extras = getExtraThemes();
		if (extras == null)
		{
			extras = new MetalTheme[0];
		}
		
		for (int i = 0; i < extras.length; ++i)
		{
			_themes.put(extras[i].getName(), extras[i]);
		}

		for (int i = 0; i < PLASTIC_THEME_CLASS_NAMES.length; ++i)
		{
			try
			{

				Class<?> clazz = Class.forName(PLASTIC_THEME_CLASS_NAMES[i], false, cl);
				MetalTheme theme = (MetalTheme) clazz.getDeclaredConstructor().newInstance();
				_themes.put(theme.getName(), theme);
			}
			catch (Throwable th)
			{
				s_log.error("Error loading theme " + PLASTIC_THEME_CLASS_NAMES[i], th);
			}
		}
	}

	/**
	 * A Look and Feel is about to be installed. Apply the selected
	 * theme.
	 * 
	 * @param	lafRegister		The LAF Register.
	 * @param	laf				The Look and Feel being installed.
	 */
	public void aboutToBeInstalled(LAFRegister lafRegister, LookAndFeel laf)
	{
		final String name = getCurrentThemeName();
		if (name != null)
		{
			MetalTheme theme = getThemeForName(name);
			if (theme != null)
			{
				try
				{
					installMetalTheme(theme);
				}
				catch (Throwable ex)
				{
					s_log.error("Error installing a Theme", ex);
				}
			}
		}
	}

	/**
	 * Retrieve the extra panel to be displayed in the LAF preferences
	 * panel for the current LAF.
	 * 
	 * @return	LAF preferences extra panel
	 */
	public BaseLAFPreferencesPanelComponent getPreferencesComponent()
	{
		return getPlasticPrefsPanel();
	}

	public final PlasticPrefsPanel getPlasticPrefsPanel()
	{
		if(null == _plasticPrefsPanel)
		{
			_plasticPrefsPanel = new PlasticPrefsPanel(this);
		}

		return _plasticPrefsPanel;
	}


	/**
	 * Retrieve the name of the current theme.
	 * 
	 * @return	Name of the current theme.
	 */
	abstract String getCurrentThemeName();

	/**
	 * Set the current theme name.
	 * 
	 * @param name		name of the current theme.
	 */
	abstract void setCurrentThemeName(String name);

	/**
	 * Override in subclasses to actually set the current theme.
	 * 
	 * @param name
	 * @return
	 */
	abstract void installMetalTheme(MetalTheme theme);

	MetalTheme getThemeForName(String name)
	{
		return _themes.get(name);
	}

	MetalTheme getMetalThemeForClassName(String className)
	{
		for (MetalTheme metalTheme : _themes.values())
		{
			if(metalTheme.getClass().getName().equals(className))
			{
				return metalTheme;
			}
		}

		return null;
	}

	/**
	 * Override in subclasses to retrieve the extra themes for the Look and
	 * Feel classes that the subclass is responsible for.
	 * @return
	 */
	MetalTheme[] getExtraThemes()
	{
		return new MetalTheme[0];
	}

	/**
	 * Retrieve the LAF register.
	 * 
	 * @return	<TT>LAFRegister</TT>.
	 */
	LAFRegister getLAFRegister()
	{
		return _lafRegister;
	}

	/**
	 * Retrieve the LAF plugin.
	 * 
	 * @return	<TT>LAFPlugin</TT>.
	 */
	LAFPlugin getLAFPlugin()
	{
		return _lafPlugin;
	}

	Iterator<MetalTheme> themesIterator()
	{
		return _themes.values().iterator();
	}

}
