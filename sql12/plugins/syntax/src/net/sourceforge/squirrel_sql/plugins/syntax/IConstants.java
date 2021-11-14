package net.sourceforge.squirrel_sql.plugins.syntax;
/*
 * Copyright (C) 2003 Colin Bell
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

/**
 * Plugin constants.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public interface IConstants
{
	/** Name of file to store user prefs in. */
	static final String USER_PREFS_FILE_NAME = "prefs.xml";

	/** Keys to objects stored in session. */
	interface ISessionKeys
	{
		/** The sessions <TT>JPreferences</TT> object. */
		String PREFS = "prefs";

		/** The SQL entry area object. */
		String SQL_ENTRY_CONTROL = "sqlentry";
	}

	public interface IStyleNames
	{
		String COLUMN = "columnName";
		String COMMENT = "comment";
		String DATA_TYPE = "datatype";
		String ERROR = "error";
		String FUNCTION = "function";
		String IDENTIFIER = "identifier";
		String LITERAL = "literal";
		String OPERATOR = "operator";
		String RESERVED_WORD = "reservedWord";
		String SEPARATOR = "separator";
		String TABLE = "tableName";
		String WHITESPACE = "whitespace";
	}
}
