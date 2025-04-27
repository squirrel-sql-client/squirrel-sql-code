package net.sourceforge.squirrel_sql.fw.resources;
/*
 * Copyright (C) 2002-2004 Colin Bell
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

public class LibraryResources extends Resources
{
   public interface IImageNames
   {
      String TABLE_ASCENDING = "table.ascending";
      String TABLE_DESCENDING = "table.descending";
      String RIGHT_ARROW = "right.arrow";
      String LEFT_ARROW = "left.arrow";

      String SORT_ASC = "sort.asc";
      String SORT_DESC = "sort.desc";

      String OPEN = "open";

      String DOT_CATALOG = "dot.catalog";
      String DOT_DATABASE = "dot.database";
      String DOT_DATATYPE = "dot.datatype";
      String DOT_DATATYPES = "dot.datatypes";
      String DOT_INDEX = "dot.index";
      String DOT_INDEXES = "dot.indexes";
      String DOT_PROCEDURE = "dot.procedure";
      String DOT_PROCEDURES = "dot.procedures";
      String DOT_SCHEMA = "dot.schema";
      String DOT_TABLE = "dot.table";
      String DOT_TABLES = "dot.tables";
      String DOT_VIEW = "dot.view";
      String DOT_USER = "dot.user";
      String DOT_SEQUENCE = "dot.sequence";
      String DOT_SEQUENCES = "dot.sequences";
      String DOT_TRIGGER = "dot.trigger";
      String DOT_TRIGGERS = "dot.triggers";
      String DOT_FUNCTION = "dot.function";

      String TABLE_MARKED = "tableMarked";
      String TABLE_CLEAN = "tableClean";
      String MARKED_TO_NEW_TABLE = "markedToTable";
      String HIDE = "hide";
      String HIDE_SELECTED = "hideSelected";
      String COLOR_CHOOSE = "colorChoose";
      String SELECT_COLUMN = "selectColumn";
      String SELECT_COLUMN_CHECKED = "selectColumnChecked";
      String CONFIGURE = "configure";

      String MARK_ALL_RESULTS = "mark_all_results";
   }

	public LibraryResources()
	{
		super(LibraryResources.class.getName(), LibraryResources.class.getClassLoader());
	}
}
