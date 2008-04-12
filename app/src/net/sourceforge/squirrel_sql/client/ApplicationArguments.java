package net.sourceforge.squirrel_sql.client;
/*
 * TODO: i18n
 */

/*
 * Copyright (C) 2001-2006 Colin Bell
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
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
/**
 * Application arguments.
 *
 * <B>Note:</B> <EM>This class <B>cannot</B> use the logging package as this
 * class is used to initialize the logging package. Nor can it use any classes
 * that themselves use the logging package.</EM>
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ApplicationArguments
{
	/**
	 * Option descriptions.
	 *
	 * <UL>
	 * <LI>element 0 - short option
	 * <LI>element 1 - long option (null if none)
	 * <LI>element 2 - option description
	 * </UL>
	 */
	private interface IOptions
	{
		String[] HELP = { "h", "help", "Display Help and exit"};
		String[] SQUIRREL_HOME = { "home", "squirrel-home",
									"SQuirreL home directory"};
		String[] LOG_FILE = { "l", "log-config-file",
											"Logging configuration file"};
		String[] USE_DEFAULT_METAL_THEME = { "m", "use-default-metal-theme",
											"Use default metal theme"};
		String[] USE_NATIVE_LAF = { "n", "native-laf",
									"Use native look and feel"};
		String[] NO_PLUGINS = {"nop", "no-plugins", "Don't load plugins"};
		String[] NO_SPLASH = { "nos", "no-splash", "Don't display splash screen"};
		String[] USER_SETTINGS_DIR = { "userdir", "user-settings-dir",
								"User settings directory"};
		String[] UI_DEBUG = {"uidebug", "user-interface-debugging", 
			"Provides tool-tips and highlighting of UI components for easy identification" }; 
	}

	/** Only instance of this class. */
	private static ApplicationArguments s_instance;

	/** Collection of possible options that acn be passed. */
	private final Options _options = new Options();

	/** Parsed command line that was passed to application. */
	private CommandLine _cmdLine;

	/** &quot;Raw&quot; arguments straight from the command line. */
	private String[] _rawArgs;

	/** Squirrels home directory. */
	private String _squirrelHome = null;

	/**
	 * If not <TT>null</TT> then is an override for the users .squirrel-sql
	 * settings directory.
	 */
	private String _userSettingsDir = null;

	/** Path for logging configuration file */
	private String _loggingConfigFile = null;

	/**
	 * Ctor specifying arguments from command line.
	 *
	 * @param	args	Arguments passed on command line.
	 *
	 * @throws	ParseException
	 * 			Thrown if unable to parse arguments.
	 */
	private ApplicationArguments(String[] args)
		throws ParseException
	{
		super();
		createOptions();

        // set up array to return for public access to cmd line args
        _rawArgs = args;        

		final CommandLineParser parser = new GnuParser();
		try
		{
			_cmdLine = parser.parse(_options, args);
		}
		catch(ParseException ex)
		{
			System.err.println("Parsing failed. Reason: " + ex.getMessage());
			printHelp();
			throw ex;
		}

		if (_cmdLine.hasOption(IOptions.SQUIRREL_HOME[0]))
		{
			_squirrelHome = _cmdLine.getOptionValue(IOptions.SQUIRREL_HOME[0]);
		}
		if (_cmdLine.hasOption(IOptions.USER_SETTINGS_DIR[0]))
		{
			_userSettingsDir = _cmdLine.getOptionValue(IOptions.USER_SETTINGS_DIR[0]);
		}
		if (_cmdLine.hasOption(IOptions.LOG_FILE[0]))
		{
			_loggingConfigFile = _cmdLine.getOptionValue(IOptions.LOG_FILE[0]);
		}
	}

	/**
	 * Initialize application arguments.
	 *
	 * @param	args	Arguments passed on command line.
	 *
	 * @return	<TT>true</TT> if arguments parsed successfully else
	 *			<TT>false<.TT>. If parsing was unsuccessful an error was written
	 *			to standard error.
	 */
	public synchronized static boolean initialize(String[] args)
	{
		if (s_instance == null)
		{
			try
			{
				s_instance = new ApplicationArguments(args);
			}
			catch (ParseException ex)
			{
				return false;
			}
		}
		else
		{
			System.out.println("ApplicationArguments.initialize() called twice");
		}
		return true;
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
	 * @return	override for the user settings directory. Will be
	 * 				<TT>null</TT> if not overridden.
	 */
	public String getSquirrelHomeDirectory()
	{
		return _squirrelHome;
	}

	/**
	 * @return The name of the directory that Squirrel is installed into.
	 */
	public String getUserSettingsDirectoryOverride()
	{
		return _userSettingsDir;
	}

	/**
	 * @return <TT>true</TT> if splashscreen should be shown.
	 */
	public boolean getShowSplashScreen()
	{
		return !_cmdLine.hasOption(IOptions.NO_SPLASH[0]);
	}

	/**
	 * @return <TT>true</TT> if help information should be written to
	 * standard output.
	 */
	public boolean getShowHelp()
	{
		return _cmdLine.hasOption(IOptions.HELP[0]);
	}

	/**
	 * @return	the logging configuration file name. Will be
	 * 			<TT>null</TT> if not passed.
	 */
	public String getLoggingConfigFileName()
	{
		return _loggingConfigFile;
	}

	/**
	 * @return	<TT>true</TT> if the plugins should be loaded.
	 */
	public boolean getLoadPlugins()
	{
		return !_cmdLine.hasOption(IOptions.NO_PLUGINS[0]);
	}

	/**
	 * @return	<TT>true</TT> if the default metal theme should be used
	 *			rather than the SQuirreL metal theme.
	 */
	public boolean useDefaultMetalTheme()
	{
		return _cmdLine.hasOption(IOptions.USE_DEFAULT_METAL_THEME[0]);
	}

	/**
	 * Retrieve whether to use the native Look and Feel.
	 *
	 * @return		<TT>true</TT> to use the native LAF.
	 */
	public boolean useNativeLAF()
	{
		return _cmdLine.hasOption(IOptions.USE_NATIVE_LAF[0]);
	}

	/**
	 * @return The raw arguments passed on the command line.
	 */
	public String[] getRawArguments()
	{
		return _rawArgs;
	}

	/**
	 * @return a boolean indicating whether or not to enable user interface debugging mode
	 */
	public boolean getUserInterfaceDebugEnabled() {
		return _cmdLine.hasOption(IOptions.UI_DEBUG[0]);
	}
	
	void printHelp()
	{
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("squirrel-sql", _options);
	}

	/**
	 * Create the <TT>Options</TT> object used to parse the command line.
	 */
	private void createOptions()
	{
		Option opt;

		opt = createAnOption(IOptions.NO_SPLASH);
		_options.addOption(opt);

		opt = createAnOption(IOptions.HELP);
		_options.addOption(opt);

		opt = createAnOption(IOptions.NO_PLUGINS);
		_options.addOption(opt);

		opt = createAnOption(IOptions.USE_DEFAULT_METAL_THEME);
		_options.addOption(opt);

		opt = createAnOption(IOptions.USE_NATIVE_LAF);
		_options.addOption(opt);

		opt = createAnOptionWithArgument(IOptions.SQUIRREL_HOME);
		_options.addOption(opt);

		opt = createAnOptionWithArgument(IOptions.USER_SETTINGS_DIR);
		_options.addOption(opt);

		opt = createAnOptionWithArgument(IOptions.LOG_FILE);
		_options.addOption(opt);
		
		opt = createAnOption(IOptions.UI_DEBUG);
		_options.addOption(opt);
	}

	private Option createAnOption(String[] argInfo)
	{
		Option opt = new Option(argInfo[0], argInfo[2]);
		if (!isStringEmpty(argInfo[1]))
		{
			opt.setLongOpt(argInfo[1]);
		}

		return opt;
	}

	private Option createAnOptionWithArgument(String[] argInfo)
	{
		OptionBuilder.withArgName(argInfo[0]);
		OptionBuilder.hasArg();
		OptionBuilder.withDescription(argInfo[2]);
		Option opt = OptionBuilder.create( argInfo[0]);
		if (!isStringEmpty(argInfo[1]))
		{
			opt.setLongOpt(argInfo[1]);
		}
		return opt;
	}

	private static boolean isStringEmpty(String str)
	{
		return str == null || str.length() == 0;
	}
    
    /**
     * Resets the internally stored instance so that the next call to initialize
     * will function as the first call.  Useful for unit tests, so it uses package
     * level access.
     */
    static final void reset() {
        s_instance = null;
    }
}
