package net.sourceforge.squirrel_sql.client.db;
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
import java.sql.DatabaseMetaData;
import java.sql.Types;

// ?? move to fw
// i18n
public class Utils {

    private Utils() {
    }

    public static String getNullableDescription(int type) {
        switch (type) {
            case DatabaseMetaData.typeNoNulls: return "No";
            case DatabaseMetaData.typeNullable: return "Yes";
            default: return "Unknown";
        }
    }

    public static String getSearchableDescription(int type) {
        switch (type) {
            case DatabaseMetaData.typePredNone: return "No";
            case DatabaseMetaData.typePredChar: return "Only WHERE LIKE";
            case DatabaseMetaData.typePredBasic: return "Not WHERE LIKE";
            case DatabaseMetaData.typeSearchable: return "Yes";
            default: return "" + type + " Unknown";
        }
    }

    public static String getSqlTypeName(int type) {
        switch (type) {
            case Types.ARRAY: return "ARRAY";
            case Types.BIGINT: return "BIGINT";
            case Types.BIT: return "BIT";
            case Types.TINYINT: return "TINYINT";
            case Types.SMALLINT: return "SMALLINT";
            case Types.INTEGER: return "INTEGER";
            case Types.FLOAT: return "FLOAT";
            case Types.REAL: return "REAL";
            case Types.DOUBLE: return "DOUBLE";
            case Types.NUMERIC: return "NUMERIC";
            case Types.DECIMAL: return "DECIMAL";
            case Types.CHAR: return "CHAR";
            case Types.VARCHAR: return "VARCHAR";
            case Types.LONGVARCHAR: return "LONGVARCHAR";
            case Types.DATE: return "DATE";
            case Types.TIME: return "TIME";
            case Types.TIMESTAMP: return "TIMESTAMP";
            case Types.BINARY: return "BINARY";
            case Types.VARBINARY: return "VARBINARY";
            case Types.LONGVARBINARY: return "LONGVARBINARY";
            case Types.NULL: return "NULL";
            case Types.OTHER: return "OTHER";
            case Types.JAVA_OBJECT: return "JAVA_OBJECT";
            case Types.DISTINCT: return "DISTINCT";
            case Types.STRUCT: return "STRUCT";
            case Types.BLOB: return "BLOB";
            case Types.CLOB: return "CLOB";
            case Types.REF: return "REF";
            default: return "" + type + " Unknown";
        }
    }
}
