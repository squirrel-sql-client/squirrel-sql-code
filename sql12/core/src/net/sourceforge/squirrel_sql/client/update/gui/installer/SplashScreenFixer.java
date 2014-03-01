package net.sourceforge.squirrel_sql.client.update.gui.installer;

/*
 * Copyright (C) 2010 Rob Manning
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

import net.sourceforge.squirrel_sql.fw.util.ScriptLineFixer;

/**
 * A line fixer implementation that adds the splash screen icon to the line that launches the SQuirreL 
 * application in the launcher scripts. 
 */
public class SplashScreenFixer implements ScriptLineFixer {

	/** The main class.  Assumption is that this line is where the splash setting needs to be added */
	public static final String CLIENT_MAIN_CLASS = "net.sourceforge.squirrel_sql.client.Main";
	
	/** The splash setting */
	public static final String SPLASH_ICON = "splash.jpg";
	
	/** A regex pattern version of the main class */
	private static final String MAIN_CLASS_PATTERN = "net\\.sourceforge\\.squirrel_sql\\.client\\.Main";
	
	/** The platform-dependent newline string */
	public static String newline = System.getProperty("line.separator");
		
	/**
	 * @see net.sourceforge.squirrel_sql.fw.util.ScriptLineFixer#fixLine(java.lang.String)
	 */
	@Override
	public String fixLine(String scriptFileName, String line) {
		String result = line;
		
		String splashIconArgument = "-splash:" + SPLASH_ICON;
		if (scriptFileName.toLowerCase().endsWith(".bat")) {
			splashIconArgument = "-splash:\"%SQUIRREL_SQL_HOME%\\icons\\"+SPLASH_ICON+"\""; 
		} else {
			splashIconArgument = "-splash:\"$SQUIRREL_SQL_HOME/icons/"+SPLASH_ICON+"\"";
		}
		
		if (line.contains(CLIENT_MAIN_CLASS)) {
			if (!line.contains(SPLASH_ICON)) {
				String[] parts = line.split(MAIN_CLASS_PATTERN);
				if (parts.length == 2) {
					StringBuilder newline = new StringBuilder();
					newline.append(parts[0]);
					newline.append(" ");
					newline.append(splashIconArgument);
					newline.append(" ");
					newline.append(CLIENT_MAIN_CLASS);
					newline.append(" ");
					newline.append(parts[1]);
					result = newline.toString();
				} else {
					System.err.println("Uh-oh, expected parts to be 2");
				}
			}
		}
		return result;
	}


}
