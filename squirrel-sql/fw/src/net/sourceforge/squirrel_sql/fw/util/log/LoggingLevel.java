package net.sourceforge.squirrel_sql.fw.util.log;
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
public final class LoggingLevel {
	private interface ILoggingLevel {
		int DEBUG = 0;
		int INFO = 1;
		int WARN = 2;
		int ERROR = 3;
		int OFF = 4;
	}

	public static final LoggingLevel DEBUG = new LoggingLevel("Debug", ILoggingLevel.DEBUG);
	public static final LoggingLevel INFO = new LoggingLevel("Informational", ILoggingLevel.INFO);
	public static final LoggingLevel WARN = new LoggingLevel("Warning", ILoggingLevel.WARN);
	public static final LoggingLevel ERROR = new LoggingLevel("Error", ILoggingLevel.ERROR);
	public static final LoggingLevel OFF = new LoggingLevel("Off", ILoggingLevel.OFF);

	private String _description;
	private int _level;

	public static LoggingLevel get(int level) {
		switch (level) {
			case ILoggingLevel.DEBUG: return DEBUG;
			case ILoggingLevel.INFO: return INFO;
			case ILoggingLevel.WARN: return WARN;
			case ILoggingLevel.ERROR: return ERROR;
			case ILoggingLevel.OFF: return OFF;
			
			default: return DEBUG;
		}
	}

	private LoggingLevel(String description, int level) {
		super();
		_description = description;
		_level = level;
	}

	public String toString() {
		return _description;
	}

	public String getDescription() {
		return _description;
	}

	public int getLevel() {
		return _level;
	}
}
