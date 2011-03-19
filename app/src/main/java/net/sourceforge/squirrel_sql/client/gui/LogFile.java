/*
 * Copyright (C) 2011 Stefan Willinger
 * wis775@users.sourceforge.net
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
package net.sourceforge.squirrel_sql.client.gui;

import java.io.File;

import net.sourceforge.squirrel_sql.fw.util.Utilities;

/**
 * Represents a Squirrel-SQL log file.
 * 
 * This class was originally a inner class of {@link ViewLogsSheet}.
 * @author Stefan Willinger
 *
 */
public class LogFile extends File
{
	private static final long serialVersionUID = 1L;

	private final String _stringRep;

	LogFile(File dir, String name)
	{
		super(dir, name);
		final StringBuffer buf = new StringBuffer();
		buf.append(getName()).append(" (").append(Utilities.formatSize(length())).append(")");
		_stringRep = buf.toString();
	}

	/**
	 * @param appLogFile
	 */
	public LogFile(File appLogFile) {
		this(appLogFile.getParentFile(), appLogFile.getName());
	}

	@Override
	public String toString()
	{
		return _stringRep;
	}
}