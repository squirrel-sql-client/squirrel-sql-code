package net.sourceforge.squirrel_sql.client;
/*
 * Copyright (C) 2001 Colin Bell
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
import java.util.StringTokenizer;

/**
 * Application arguments.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ApplicationArguments {
	/** &quot;Raw&quot; arguments straight from the command line. */
	private String[] _rawArgs;

	/** <TT>true</TT> if splashscreen should be shown. */
	private boolean _showSplashScreen = true;

	/**
	 * If not <TT>null</TT> then is an override for the users .squirrel-sql settings directory.
	 */
	private String _userSettingsDir = null;

	/**
	 * Ctor specifying arguments from command line. 
	 *
	 * @param   args	Arguments passed on command line.
	 */
	public ApplicationArguments(String[] args) {
		super();
		_rawArgs = args;
		for (int i = 0; i < args.length; ++i) {
			StringTokenizer strTok = new StringTokenizer(args[i], "=");
			String parm = null;
			String value = null;
			while (strTok.hasMoreTokens()) {
				String token = strTok.nextToken();
				if (parm == null) {
					parm = token;
				} else {
					value = token;
				}
			}
			if (parm.equalsIgnoreCase("-nosplash")) {
				_showSplashScreen = false;
			} else if (parm.equalsIgnoreCase("-settingsdir")) {
				_userSettingsDir = value;
			}
		}
	}

	/**
	 *  @return The raw arguments passed on the command line.
	 */
	public String[] getRawArguments() {
		return _rawArgs;
	}

	/**
	 *  @return <TT>true</TT> if splashscreen should be shown.
	 */
	public String getUserSettingsDirectoryOverride() {
		return _userSettingsDir;
	}

	/**
	 *  @return override for the user settings directory. Can be <TT>null</TT>.
	 */
	public boolean getShowSplashScreen() {
		return _showSplashScreen;
	}
}
