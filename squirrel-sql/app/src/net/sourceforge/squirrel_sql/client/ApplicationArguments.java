package net.sourceforge.squirrel_sql.client;
/*
 * Copyright (C) 2001 Colin Bell
 * colbell@users.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
import java.util.StringTokenizer;

/**
 * Application arguments.
 *
 * <B>Note:</B> <EM>This class <B>cannot</B> use the logging package as this
 * class is used to initialize the logging package.</EM>
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ApplicationArguments
{
	/** Only instance of this class. */
	private static ApplicationArguments s_instance;

	/** &quot;Raw&quot; arguments straight from the command line. */
	private String[] _rawArgs;

	/** Squirrels home directory. */
	private String _squirrelHome = null;

	/** <TT>true</TT> if splashscreen should be shown. */
	private boolean _showSplashScreen = true;

	/**
	 * If not <TT>null</TT> then is an override for the users .squirrel-sql
	 * settings directory.
	 */
	private String _userSettingsDir = null;

	/** Path for logging configuration file */
	private String _loggingConfigFile = null;

	/** If <TT>true</TT> plugins are to be loaded. */
	private boolean _loadPlugins = true;

	/** If <TT>true</TT> use default metal theme rther than the SQuirreL theme. */
	private boolean _userDefaultMetalTheme = false;

	/**
	 * Ctor specifying arguments from command line.
	 *
	 * @param   args	Arguments passed on command line.
	 */
	private ApplicationArguments(String[] args)
	{
		super();
		_rawArgs = args;
		for (int i = 0; i < args.length; ++i)
		{
			StringTokenizer strTok = new StringTokenizer(args[i], "=");
			String parm = null;
			String value = null;
			while (strTok.hasMoreTokens())
			{
				String token = strTok.nextToken();
				if (parm == null)
				{
					parm = token;
				}
				else
				{
					value = token;
				}
			}
			if (parm.equalsIgnoreCase("-nosplash"))
			{
				_showSplashScreen = false;
			}
			else if (parm.equalsIgnoreCase("-squirrelHome"))
			{
				_squirrelHome = value;
			}
			else if (parm.equalsIgnoreCase("-settingsdir"))
			{
				_userSettingsDir = value;
			}
			else if (parm.equalsIgnoreCase("-loggingconfigfile"))
			{
				_loggingConfigFile = value;
			}
			else if (parm.equalsIgnoreCase("-noplugins"))
			{
				_loadPlugins = false;
			}
			else if (parm.equalsIgnoreCase("-usedefaultmetaltheme"))
			{
				_userDefaultMetalTheme = true;
			}
		}
	}

	/**
	 * Initialize application arguments.
	 *
	 * @param   args	Arguments passed on command line.
	 */
	public synchronized static void initialize(String[] args)
	{
		if (s_instance == null)
		{
			s_instance = new ApplicationArguments(args);
		}
		else
		{
			System.out.println("ApplicationArguments.initialize() called twice");
		}
	}

	/**
	 * Return the single instance of this class.
	 * 
	 * @return the single instance of this class.
	 *
	 * @throws	IllegalStateException
	 * 			Thrown if ApplicationArguments.getInstance() called
	 *			before ApplicationArguments.initialize()
	 */
	public static ApplicationArguments getInstance()
	{
		if (s_instance == null)
		{
			throw new IllegalStateException("ApplicationArguments.getInstance() called before ApplicationArguments.initialize()");
		}
		return s_instance;
	}

	/**
	 *  @return The raw arguments passed on the command line.
	 */
	public String[] getRawArguments()
	{
		return _rawArgs;
	}

	/**
	 *  @return	override for the user settings directory. Will be
	 * 				<TT>null</TT> if not overridden.
	 */
	public String getSquirrelHomeDirectory()
	{
		return _squirrelHome;
	}

	/**
	 *  @return The name of the directory that Squirrel is installed into.
	 */
	public String getUserSettingsDirectoryOverride()
	{
		return _userSettingsDir;
	}

	/**
	 *  @return <TT>true</TT> if splashscreen should be shown.
	 */
	public boolean getShowSplashScreen()
	{
		return _showSplashScreen;
	}

	/**
	 *  @return	the logging configuration file name. Will be
	 * 			<TT>null</TT> if not passed.
	 */
	public String getLoggingConfigFileName()
	{
		return _loggingConfigFile;
	}

	/**
	 *  @return	<TT>true</TT> if the plugins should be loaded.
	 */
	public boolean getLoadPlugins()
	{
		return _loadPlugins;
	}

	/**
	 *  @return		<TT>true</TT> if the default metal theme should be used
	 *				rather than the SQuirreL metal theme.
	 */
	public boolean useDefaultMetalTheme()
	{
		return _userDefaultMetalTheme;
	}

}