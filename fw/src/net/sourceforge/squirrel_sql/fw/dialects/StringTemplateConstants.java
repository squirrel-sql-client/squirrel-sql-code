/*
 * Copyright (C) 2007 Rob Manning
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
package net.sourceforge.squirrel_sql.fw.dialects;

public interface StringTemplateConstants
{

	// Template bodies
	String ST_ADD_UNIQUE_CONSTRAINT_STYLE_ONE = 
		"ALTER TABLE $tableName$ " +
		"ADD $constraint$ $constraintName$ UNIQUE $index$ $indexName$ $indexType$ ( $indexColumnName$ )";

	String ST_ADD_AUTO_INCREMENT_STYLE_ONE = 
		"ALTER TABLE $tableName$ MODIFY $columnName$ BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY";
	
	String ST_ADD_FOREIGN_KEY_CONSTRAINT_STYLE_ONE = 
		"ALTER TABLE $childTableName$ " +
		"ADD $constraint$ $constraintName$ FOREIGN KEY ( $childColumn; separator=\",\"$ ) " +
		"REFERENCES $parentTableName$ ( $parentColumn; separator=\",\"$ )";
		
	String ST_CREATE_INDEX_STYLE_ONE = 
		"CREATE $accessMethod$ INDEX $indexName$ $indexType$ " +
		"ON $tableName$ ( $indexColumns; separator=\",\"$ )";
	
	// Keys that can be embedded in templates for replacement later.
	
	String ST_ACCESS_METHOD_KEY = "accessMethod";

	String ST_CHILD_COLUMN_KEY = "childColumn";
	
	String ST_CHILD_TABLE_KEY = "childTableName";
	
	String ST_COLUMN_NAME_KEY = "columnName";

	String ST_CONSTRAINT_KEY = "constraint";
	
	String ST_CONSTRAINT_NAME_KEY = "constraintName";
	
	String ST_INDEX_KEY = "index";
	
	String ST_INDEX_NAME_KEY = "indexName";
	
	String ST_INDEX_COLUMNS_KEY = "indexColumns";

	String ST_INDEX_COLUMN_NAME_KEY = "indexColumnName";	
	
	String ST_PARENT_COLUMN_KEY = "parentColumn";
	
	String ST_PARENT_TABLE_KEY = "parentTableName";
	
	String ST_SEQUENCE_NAME_KEY = "sequenceName";
	
	String ST_TABLE_NAME_KEY = "tableName";
}
