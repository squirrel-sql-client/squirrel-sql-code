package org.firebirdsql.squirrel.util;
/*
 * Copyright (C) 2004 Colin Bell
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
/**
 * Information about the Firebird System tables.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SystemTables
{
	/**
	 * Metadata for the indices table.
	 */
	public interface IIndexTable
	{
		String TABLE_NAME = "RDB$INDICES";

		String COL_ID = "RDB$INDEX_ID";
		String COL_NAME = "RDB$INDEX_NAME";
		String COL_DESCRIPTION = "RDB$DESCRIPTION";
		String COL_EXPRESSION_SOURCE = "RDB$EXPRESSION_SOURCE";
		String COL_FOREIGN_KEY = "RDB$FOREIGN_KEY";
		String COL_INACTIVE = "RDB$INDEX_INACTIVE";
		String COL_RELATION_NAME = "RDB$RELATION_NAME";
		String COL_SEGMENT_COUNT = "RDB$SEGMENT_COUNT";
		String COL_UNIQUE = "RDB$UNIQUE_FLAG";
		String COL_SYSTEM = "RDB$SYSTEM_FLAG";
	}

	private SystemTables()
	{
		super();
	}
}
