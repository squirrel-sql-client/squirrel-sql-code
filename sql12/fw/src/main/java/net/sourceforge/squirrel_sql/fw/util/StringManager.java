package net.sourceforge.squirrel_sql.fw.util;
/*
 * Copyright (C) 2003 Colin Bell
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
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.net.URLClassLoader;
import java.net.URL;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
/**
 * This class defines i18nized strings. These strings are stored in a file
 * with a base name I18NStrings.properties in each package directory.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class StringManager
{
	/** Logger for this class. */
	private static ILogger s_log = LoggerController.createLogger(StringManager.class);

	/** Contains the localised strings. */
	private ResourceBundle _rsrcBundle;
	private String _bundleBaseName;
	private URL[] _bundleLoaderUrLs = new URL[0];
	
	/** a flag to indicate whether or not to throw exception for missing resource string */
	private static boolean _testMode = false;

	/**
	 * Ctor specifying the package name. Attempt to load a resource bundle
	 * from the package directory.
	 *
	 * @param	packageName	Name of package
	 * @param	classLoader	Class loader to use
	 */
	StringManager(String packageName, ClassLoader loader)
	{
		super();
		_bundleBaseName = packageName + ".I18NStrings";
		_rsrcBundle = ResourceBundle.getBundle(_bundleBaseName, Locale.getDefault(), loader);

		if(loader instanceof URLClassLoader)
		{
			_bundleLoaderUrLs = ((URLClassLoader) loader).getURLs();
		}


	}

	/**
	 * Retrieve the localized string for the passed key. If it isn't found
	 * an error message is returned instead.
	 *
	 * @param	key		Key to retrieve string for.
	 *
	 * @return	Localized string or error message.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>key</TT> passed.
	 */
	public String getString(String key)
	{
		if (key == null)
		{
			throw new IllegalArgumentException("key == null");
		}

		try
		{
			return _rsrcBundle.getString(key);
		}
		catch (MissingResourceException ex)
		{
			StringBuilder sb = new StringBuilder();
			sb.append("No resource string found for key '" + key + "' in bundle " + _bundleBaseName + "\n\n");

			if(0 < _bundleLoaderUrLs.length)
			{
				sb.append("The following classpath entries are available to the bundle loader:\n");
				for (int i = 0; i < _bundleLoaderUrLs.length; i++)
				{
					sb.append(_bundleLoaderUrLs[i]).append("\n");
				}
			}
			s_log.error(sb.toString(), ex);
			if (_testMode) {
				throw new IllegalStateException(sb.toString());
			}
			return "No resource found for key " + key;
		}
	}

    /**
     * Retrieve the localized string for the passed key and format it with the
     * passed arguments.
     *
     * @param   key     Key to retrieve string for.
     * @param   args    Any string arguments that should be used as values to 
     *                  parameters found in the localized string.
     *                   
     * @return  Localized string or error message.
     *
     * @throws  IllegalArgumentException
     *          Thrown if <TT>null</TT> <TT>key</TT> passed.
     */    
    public String getString(String key, String[] args) 
    {
        return getString(key, (Object[])args);
    }
    
	/**
	 * Retrieve the localized string for the passed key and format it with the
	 * passed arguments.
	 *
	 * @param	key		Key to retrieve string for.
     * @param   args    Any string arguments that should be used as values to 
     *                  parameters found in the localized string. 
	 *
	 * @return	Localized string or error message.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>key</TT> passed.
	 */
	public String getString(String key, Object... args)
	{
		if (key == null)
		{
			throw new IllegalArgumentException("key == null");
		}

		if (args == null)
		{
			args = new Object[0];
		}

		final String str = getString(key);
		try
		{
			return MessageFormat.format(str, args);
		}
		catch (IllegalArgumentException ex)
		{
			String msg = "Error formatting i18 string. Key is '" + key + "'";
			s_log.error(msg, ex);
			return msg + ": " + ex.toString();
		}
	}
	
	/**
	 * Allows the caller to enable/disable test mode which results in an exception being thrown for no 
	 * resource string defined. 
	 * 
	 * @param enabled 
	 */
	public static void setTestMode(boolean enabled) {
		_testMode = enabled;
	}
}