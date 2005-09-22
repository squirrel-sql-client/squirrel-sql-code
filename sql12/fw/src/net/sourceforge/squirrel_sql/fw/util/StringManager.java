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
		_rsrcBundle = ResourceBundle.getBundle(packageName + ".I18NStrings",
						Locale.getDefault(), loader);
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
			String msg = "No resource string found for key '" + key + "'";
			s_log.error(msg);
			return msg;
		}
	}

	/**
	 * Retrieve the localized string for the passed key and format it with the
	 * single passed argument.
	 *
	 * @param	key		Key to retrieve string for.
	 * @param	arg		Argument to place in message.
	 *
	 * @return	Localized string or error message.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>key</TT> passed.
	 */
	public String getString(String key, Object arg)
	{
		if (key == null)
		{
			throw new IllegalArgumentException("key == null");
		}

		Object[] args;
		if (arg == null)
		{
			args = new Object[0];
		}
		else
		{
			args = new Object[] {arg};
		}

		return getString(key, args);
	}

	/**
	 * Retrieve the localized string for the passed key and format it with the
	 * passed arguments.
	 *
	 * @param	key		Key to retrieve string for.
	 *
	 * @return	Localized string or error message.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>key</TT> passed.
	 */
	public String getString(String key, Object[] args)
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
}