package net.sourceforge.squirrel_sql.client;

import java.util.List;

/*
 * Copyright (C) 2011 Rob Manning
 * manningr@users.sourceforge.net
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

public interface IApplicationArguments
{

	/**
	 * @return	override for the user settings directory. Will be
	 * 				<TT>null</TT> if not overridden.
	 */
	String getSquirrelHomeDirectory();

	/**
	 * @return The name of the directory that Squirrel is installed into.
	 */
	String getUserSettingsDirectoryOverride();

	/**
	 * @return <TT>true</TT> if splashscreen should be shown.
	 */
	boolean getShowSplashScreen();

	/**
	 * @return <TT>true</TT> if help information should be written to
	 * standard output.
	 */
	boolean getShowHelp();

	/**
	 * @return	the logging configuration file name. Will be
	 * 			<TT>null</TT> if not passed.
	 */
	String getLoggingConfigFileName();

	/**
	 * @return	<TT>true</TT> if the plugins should be loaded.
	 */
	boolean getLoadPlugins();

	/**
	 * @return	<TT>true</TT> if the default metal theme should be used
	 *			rather than the SQuirreL metal theme.
	 */
	boolean useDefaultMetalTheme();

	/**
	 * Retrieve whether to use the native Look and Feel.
	 *
	 * @return		<TT>true</TT> to use the native LAF.
	 */
	boolean useNativeLAF();

	/**
	 * @return The raw arguments passed on the command line.
	 */
	String[] getRawArguments();

	/**
	 * @return a boolean indicating whether or not to enable user interface debugging mode
	 */
	boolean getUserInterfaceDebugEnabled();

	/**
	 * @return
	 */
	List<String> getPluginList();

}