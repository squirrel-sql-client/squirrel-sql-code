package net.sourceforge.squirrel_sql.fw.sql;
/*
 * Copyright (C) 2002 David MacLean
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

public class JDBCTypeMapper {

	public static String getJdbcTypeName(int jdbcType) {
		String typeName = "";
		switch (jdbcType) {
			case Types.ARRAY:
				typeName = "ARRAY";
				break;

			case Types.BIGINT:
				typeName = "BIGINT";
				break;
			case Types.BINARY:
				typeName = "BINARY";
				break;
			case Types.BIT:
				typeName = "BIT";
				break;
			case Types.BLOB:
				typeName = "BLOB";
				break;
			case Types.CHAR:
				typeName = "CHAR";
				break;
			case Types.CLOB:
				typeName = "CLOB";
				break;
			case Types.DATE:
				typeName = "DATE";
				break;
			case Types.DECIMAL:
				typeName = "DECIMAL";
				break;
			case Types.DISTINCT:
				typeName = "DISTINCT";
				break;
			case Types.DOUBLE:
				typeName = "DOUBLE";
				break;
			case Types.FLOAT:
				typeName = "FLOAT";
				break;
			case Types.INTEGER:
				typeName = "INTEGER";
				break;
			case Types.JAVA_OBJECT:
				typeName = "JAVA_OBJECT";
				break;
			case Types.LONGVARBINARY:
				typeName = "LONGVARBINARY";
				break;
			case Types.LONGVARCHAR:
				typeName = "LONGVARCHAR";
				break;
			case Types.NULL:
				typeName = "NULL";
				break;
			case Types.NUMERIC:
				typeName = "NUMERIC";
				break;
			case Types.OTHER:
				typeName = "OTHER";
				break;
			case Types.REAL:
				typeName = "REAL";
				break;
			case Types.REF:
				typeName = "REF";
				break;
			case Types.SMALLINT:
				typeName = "SMALLINT";
				break;
			case Types.STRUCT:
				typeName = "STRUCT";
				break;
			case Types.TIME:
				typeName = "TIME";
				break;
			case Types.TIMESTAMP:
				typeName = "TIMESTAMP";
				break;
			case Types.TINYINT:
				typeName = "TINYINT";
				break;
			case Types.VARBINARY:
				typeName = "VARBINARY";
				break;
			case Types.VARCHAR:
				typeName = "VARCHAR";
				break;
		}
		return typeName;
	}

	public static int getJdbcType(String jdbcTypeName) {
		int type = Types.NULL;
		if ("ARRAY".equals(jdbcTypeName)) {
			type = Types.ARRAY;
		} else if ("BIGINT".equals(jdbcTypeName)) {
			type = Types.BIGINT;
		} else if ("BINARY".equals(jdbcTypeName)) {
			type = Types.BINARY;
		} else if ("BIT".equals(jdbcTypeName)) {
			type = Types.BIT;
		} else if ("BLOB".equals(jdbcTypeName)) {
			type = Types.BLOB;
		} else if ("CHAR".equals(jdbcTypeName)) {
			type = Types.CHAR;
		} else if ("CLOB".equals(jdbcTypeName)) {
			type = Types.CLOB;
		} else if ("DATE".equals(jdbcTypeName)) {
			type = Types.DATE;
		} else if ("DECIMAL".equals(jdbcTypeName)) {
			type = Types.DECIMAL;
		} else if ("DISTINCT".equals(jdbcTypeName)) {
			type = Types.DISTINCT;
		} else if ("DOUBLE".equals(jdbcTypeName)) {
			type = Types.DOUBLE;
		} else if ("FLOAT".equals(jdbcTypeName)) {
			type = Types.FLOAT;
		} else if ("INTEGER".equals(jdbcTypeName)) {
			type = Types.INTEGER;
		} else if ("JAVA_OBJECT".equals(jdbcTypeName)) {
			type = Types.JAVA_OBJECT;
		} else if ("LONGVARBINARY".equals(jdbcTypeName)) {
			type = Types.LONGVARBINARY;
		} else if ("LONGVARCHAR".equals(jdbcTypeName)) {
			type = Types.LONGVARCHAR;
		} else if ("NULL".equals(jdbcTypeName)) {
			type = Types.NULL;
		} else if ("NUMERIC".equals(jdbcTypeName)) {
			type = Types.NUMERIC;
		} else if ("OTHER".equals(jdbcTypeName)) {
			type = Types.OTHER;
		} else if ("REAL".equals(jdbcTypeName)) {
			type = Types.REAL;
		} else if ("REF".equals(jdbcTypeName)) {
			type = Types.REF;
		} else if ("SMALLINT".equals(jdbcTypeName)) {
			type = Types.SMALLINT;
		} else if ("STRUCT".equals(jdbcTypeName)) {
			type = Types.STRUCT;
		} else if ("TIME".equals(jdbcTypeName)) {
			type = Types.TIME;
		} else if ("TIMESTAMP".equals(jdbcTypeName)) {
			type = Types.TIMESTAMP;
		} else if ("TINYINT".equals(jdbcTypeName)) {
			type = Types.TINYINT;
		} else if ("VARBINARY".equals(jdbcTypeName)) {
			type = Types.VARBINARY;
		} else if ("VARCHAR".equals(jdbcTypeName)) {
			type = Types.VARCHAR;
		}
		return type;
	}
}


