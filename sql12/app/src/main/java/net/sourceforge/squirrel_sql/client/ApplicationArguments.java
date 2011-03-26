package net.sourceforge.squirrel_sql.client;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
 * that themselves use the logging package.</EM> Since StringManager uses the 
 * logging facility, neither can it be internationalized.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ApplicationArguments implements IApplicationArguments
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
	public interface IOptions
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
		String[] PLUGIN_LIST = { "pluginlist", "plugin-classpath-list", 
			"Specify a comma-delimited list of plugins to load from the CLASSPATH" };
		String[] SHUTDOWN_TIMEOUT_SECONDS = { "s", "shutdown-timeout-seconds", 
			"Specify the number of seconds to allow the application to run before exiting the VM" };

	}

	/** Only instance of this class. */
	private static volatile ApplicationArguments s_instance;

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

	/** List of plugins to load from the classloader */
	private List<String> _pluginList = null;
	
	/** Time in seconds to allow the application to run prior to exiting the VM */
	private Integer _shutdownTimerSeconds = null;
	
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
		if (_cmdLine.hasOption(IOptions.PLUGIN_LIST[0]))
		{
			String pluginList = _cmdLine.getOptionValue(IOptions.PLUGIN_LIST[0]);
			if (pluginList != null && !pluginList.isEmpty()) {
				String[] pluginArr = pluginList.split(",");
				_pluginList = new ArrayList<String>(Arrays.asList(pluginArr));
				_pluginList = Collections.unmodifiableList(_pluginList);
			}
		}
		if (_cmdLine.hasOption(IOptions.SHUTDOWN_TIMEOUT_SECONDS[0])) {
			_shutdownTimerSeconds = Integer.parseInt(_cmdLine.getOptionValue(IOptions.SHUTDOWN_TIMEOUT_SECONDS[0]));
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
	 * @return the single instance of this class. If initialize() hasn't yet been called, then it is
	 * assumed that there are no arguments to the application.
	 */
	public static ApplicationArguments getInstance()
	{
		if (s_instance == null)
		{
			try {
				s_instance = new ApplicationArguments(new String[] {});
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return s_instance;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.IApplicationArguments#getSquirrelHomeDirectory()
	 */
	public String getSquirrelHomeDirectory()
	{
		return _squirrelHome;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.IApplicationArguments#getUserSettingsDirectoryOverride()
	 */
	public String getUserSettingsDirectoryOverride()
	{
		return _userSettingsDir;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.IApplicationArguments#getShowSplashScreen()
	 */
	public boolean getShowSplashScreen()
	{
		return !_cmdLine.hasOption(IOptions.NO_SPLASH[0]);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.IApplicationArguments#getShowHelp()
	 */
	public boolean getShowHelp()
	{
		return _cmdLine.hasOption(IOptions.HELP[0]);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.IApplicationArguments#getLoggingConfigFileName()
	 */
	public String getLoggingConfigFileName()
	{
		return _loggingConfigFile;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.IApplicationArguments#getLoadPlugins()
	 */
	public boolean getLoadPlugins()
	{
		return !_cmdLine.hasOption(IOptions.NO_PLUGINS[0]);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.IApplicationArguments#useDefaultMetalTheme()
	 */
	public boolean useDefaultMetalTheme()
	{
		return _cmdLine.hasOption(IOptions.USE_DEFAULT_METAL_THEME[0]);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.IApplicationArguments#useNativeLAF()
	 */
	public boolean useNativeLAF()
	{
		return _cmdLine.hasOption(IOptions.USE_NATIVE_LAF[0]);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.IApplicationArguments#getRawArguments()
	 */
	public String[] getRawArguments()
	{
		return _rawArgs;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.IApplicationArguments#getUserInterfaceDebugEnabled()
	 */
	public boolean getUserInterfaceDebugEnabled() {
		return _cmdLine.hasOption(IOptions.UI_DEBUG[0]);
	}
	
	/**
	 * @see net.sourceforge.squirrel_sql.client.IApplicationArguments#getPluginList()
	 */
	public List<String> getPluginList() {
		return _pluginList;
	}
	
	public Integer getShutdownTimerSeconds() {
		return _shutdownTimerSeconds;
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
		
		opt = createAnOptionWithArgument(IOptions.PLUGIN_LIST);
		_options.addOption(opt);
		
		opt = createAnOptionWithArgument(IOptions.SHUTDOWN_TIMEOUT_SECONDS);
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
