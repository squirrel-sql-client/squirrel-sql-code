package net.sourceforge.squirrel_sql.client.update.gui.installer;

import net.sourceforge.squirrel_sql.fw.util.IOUtilities;
import net.sourceforge.squirrel_sql.fw.util.ScriptLineFixer;

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
 * This script line fixer updates the buildCPFromDir function in squirrel-sql.sh to fix a bug that was 
 * causing the classpath of the update application to prefer the installed jars rather than the ones that 
 * were downloaded. 
 */
public class ClasspathFunctionFixer implements ScriptLineFixer
{

	private boolean inFunctionDeclaration = false; 
	
	private boolean scriptWasAlreadyFixed = false;
	
	private boolean sawOpenCurlyBrace = false; 
	
	/**
	 * @see net.sourceforge.squirrel_sql.fw.util.ScriptLineFixer#fixLine(java.lang.String, java.lang.String)
	 */
	@Override
	public String fixLine(String scriptFileName, String line)
	{
		if (scriptWasAlreadyFixed) {
			return line;
		}
		
		if (!scriptFileName.toLowerCase().endsWith(".sh")) {
			return line;
		}
		
		if (line.contains("buildCPFromDir()")) {
			inFunctionDeclaration = true;
			return line;
		}
		if (inFunctionDeclaration) {
			
			if (line.contains("{")) {
				sawOpenCurlyBrace = true;
				return line;
			}
			
			// Perhaps the line has already been added.  If so, skip future checks.
			if (line.contains("CP=\"\"")) {
				scriptWasAlreadyFixed = true;
				return line;
			}
			
			// At this point we are still in the function declaration, and the current line is neither the 
			// opening curly brace, nor the 'CP=""' line, so it must be the first actual line of the function.
			// So, add in the 
			
			if (sawOpenCurlyBrace) {
				inFunctionDeclaration = false;
				scriptWasAlreadyFixed = true;
				StringBuilder alteredLine = new StringBuilder();
				alteredLine.append("\tCP=\"\"");
				alteredLine.append(IOUtilities.NEW_LINE);
				alteredLine.append(line);
				return alteredLine.toString();
			}
		}
		return line;
	}

}
