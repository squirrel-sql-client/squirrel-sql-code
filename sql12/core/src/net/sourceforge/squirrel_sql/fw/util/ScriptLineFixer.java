package net.sourceforge.squirrel_sql.fw.util;

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

/**
 * Interface for line fixer implementations. ScriptLineFixers are called upon to fix lines in scripts that are
 * shipped in the installer. Since scripts can be modified by the installer at install time and end users
 * after installation is complete, they cannot simply be overwritten during a software update. Instead, each
 * line of the script is examined and transformed as required by the current release.
 */
public interface ScriptLineFixer
{

	/**
	 * Fixes the line specified, returning the "fixed" version
	 * 
	 * @param scriptFileName
	 *           the filename of the script (may or may not include the absolute path)
	 * @param line
	 *           the line that needs to be fixed
	 * @return the fixed line
	 */
	String fixLine(String scriptFileName, String line);
}
