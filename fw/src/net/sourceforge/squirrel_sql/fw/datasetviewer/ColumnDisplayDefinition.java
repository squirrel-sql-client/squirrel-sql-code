package net.sourceforge.squirrel_sql.fw.datasetviewer;
/*
 * Copyright (C) 2001-2003 Colin Bell
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
import java.sql.Types;
/**
 * This defines the display information for a column.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ColumnDisplayDefinition
{
	/** Number of characters to display. */
	private int _displayWidth;

	/** Column heading. */
	private String _label;

	/**
	 * Type of data displayed in column. When set to Types.NULL, the value is unknown.
	 * Value is from java.sql.Types.
	 * This is needed, for example, when editing the column to know
	 * what operations to apply during cell editing.
	 */
	private int _sqlType;

	/**
	 * Ctor.
	 *
	 * @param	displayWidth	Number of characters to display.
	 * @param	label			Column heading.
	 */
	public ColumnDisplayDefinition(int displayWidth, String label)
	{
		super();
		init(displayWidth, label, Types.NULL);
	}

	/**
	 * Constructor for use when the type of data in the column is known/needed.
	 *
	 * @param	displayWidth	Number of characters to display.
	 * @param	label			Column heading.
	 * @param	className		Name of the class for the type of data in the column.
	 */
	public ColumnDisplayDefinition(int displayWidth, String label, int sqlType) {
		super();
		init(displayWidth, label, sqlType);
	}

	/**
	 * Return the number of characters to display.
	 *
	 * @return	The number of characters to display.
	 */
	public int getDisplayWidth()
	{
		return _displayWidth;
	}

	/**
	 * Return the column heading.
	 *
	 * @return	The column heading.
	 */
	public String getLabel()
	{
		return _label;
	}

	/**
	 * Return the column data type, which may be Types.NULL.
	 *
	 * @return	The type of data in the column (may be Types.NULL).
	 */
	public int getSqlType()
	{
		return _sqlType;
	}

	/**
	 * Return the class name associated with the sql data type.
	 * When the type is unknown or cannot be edited we return
	 * "java.lang.Object".
	 * 
	 * @return	The java class name for the data type
	 */
	public String getClassName()
	{
		switch (_sqlType)
		{
			case Types.NULL:	// should never happen
				return "java.lang.Object";

			// TODO: When JDK1.4 is the earliest JDK supported
			// by Squirrel then remove the hardcoding of the
			// boolean data type.
			case Types.BIT:
			case 16:
//			case Types.BOOLEAN:
				return "java.lang.Object";

			case Types.TIME :
				return "java.lang.Object";

			case Types.DATE :
				return "java.lang.Object";

			case Types.TIMESTAMP :
				return "java.lang.Object";

			case Types.BIGINT :
				return "java.lang.Object";

			case Types.DOUBLE:
			case Types.FLOAT:
			case Types.REAL:
				return "java.lang.Object";

			case Types.DECIMAL:
			case Types.NUMERIC:
				return "java.lang.Object";

			case Types.INTEGER:
			case Types.SMALLINT:
			case Types.TINYINT:
				return "java.lang.Integer";


			// TODO: Hard coded -. JDBC/ODBC bridge JDK1.4
			// brings back -9 for nvarchar columns in
			// MS SQL Server tables.
			// -8 is ROWID in Oracle.
			case Types.CHAR:
			case Types.VARCHAR:
			case Types.LONGVARCHAR:
			case -9:
			case -8:
				return "java.lang.String";

			case Types.BINARY:
				return "java.lang.Object";

			case Types.VARBINARY:
				return "java.lang.Object";

			case Types.LONGVARBINARY:
				return "java.lang.Object";

			case Types.BLOB:
				return "java.lang.Object";

			case Types.CLOB:
				return "java.lang.Object";

			case Types.OTHER:
				return "java.lang.Object";

			default:	// should never happen
				return "java.lang.Object";
		}
	}

	/**
	 * Private initializer method for ctors. If the display width
	 * is less than the width of the heading then make the display
	 * width the same as the width of the heading.
	 *
	 * @param	displayWidth	Number of characters to display.
	 * @param	label			Column heading.
	 * @param	sqlType			Type of data (from java.sql.Types).
	 */
	private void init(int displayWidth, String label, int sqlType)
	{
		if (label == null)
		{
			label = "";	// Some drivers will give null.
		}
		_displayWidth = displayWidth;
		if (_displayWidth < label.length())
		{
			_displayWidth = label.length();
		}
		_label = label;
		_sqlType = sqlType;
	}
}

