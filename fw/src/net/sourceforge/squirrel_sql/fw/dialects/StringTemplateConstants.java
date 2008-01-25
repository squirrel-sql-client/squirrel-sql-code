/*
 * Copyright (C) 2008 Rob Manning
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

/**
 * This interface is meant to collect all of the variant refactoring SQL templates.  Each template is meant
 * to be given to a StringTemplate instance and the template markers (surrounded with $ signs) can be replaced
 * easily using setAttribute('marker', 'value');  If a marker is meant to represent a list, then a separator
 * char (usually a command ',') is specified in the template.
 * 
 * @author manningr
 */
public interface StringTemplateConstants
{

	// Template bodies
	String ST_ADD_UNIQUE_CONSTRAINT_STYLE_ONE = 
		"ALTER TABLE $tableName$ " +
		"ADD $constraint$ $constraintName$ UNIQUE $index$ $indexName$ $indexType$ ( $indexColumnName$ )";

	String ST_ADD_UNIQUE_CONSTRAINT_STYLE_TWO =
		"ALTER TABLE $tableName$ " +
		"add constraint $constraintName$ unique ($columnName;  separator=\",\"$ )";
	
	String ST_ADD_AUTO_INCREMENT_STYLE_ONE = 
		"ALTER TABLE $tableName$ MODIFY $columnName$ BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY";
	
	String ST_ADD_FOREIGN_KEY_CONSTRAINT_STYLE_ONE = 
		"ALTER TABLE $childTableName$ " +
		"ADD $constraint$ $constraintName$ FOREIGN KEY ( $childColumn; separator=\",\"$ ) " +
		"REFERENCES $parentTableName$ ( $parentColumn; separator=\",\"$ )";
		
	String ST_CREATE_INDEX_STYLE_ONE = 
		"CREATE $accessMethod$ INDEX $indexName$ $indexType$ " +
		"ON $tableName$ ( $columnName; separator=\",\"$ )";
	
	String ST_CREATE_INDEX_STYLE_TWO =
		"CREATE $unique$ $storageOption$ INDEX $indexName$ " +
		"ON $tableName$ ( $columnName; separator=\",\"$ )";
	
	String ST_DROP_CONSTRAINT_STYLE_ONE = 
		"ALTER TABLE $tableName$ DROP CONSTRAINT $constraintName$";
	
	String ST_DROP_INDEX_STYLE_ONE = 
		"DROP INDEX $indexName$ ON $tableName$";
	
	String ST_CREATE_VIEW_STYLE_ONE =
		"CREATE VIEW $viewName$ " +
		"AS $selectStatement$ $with$ $checkOptionType$ $checkOption$";
	
	// Keys that can be embedded in templates for replacement later.
	
	String ST_ACCESS_METHOD_KEY = "accessMethod";

	String ST_CHECK_OPTION = "checkOption";
	
	String ST_CHECK_OPTION_TYPE = "checkOptionType";
	
	String ST_CHILD_COLUMN_KEY = "childColumn";
	
	String ST_CHILD_TABLE_KEY = "childTableName";
	
	String ST_COLUMN_NAME_KEY = "columnName";

	String ST_CONSTRAINT_KEY = "constraint";
	
	String ST_CONSTRAINT_NAME_KEY = "constraintName";

	String ST_INDEX_COLUMNS_KEY = "indexColumns";

	String ST_INDEX_COLUMN_NAME_KEY = "indexColumnName";	
	
	String ST_INDEX_KEY = "index";
	
	String ST_INDEX_NAME_KEY = "indexName";
	
	String ST_INDEX_TYPE_KEY = "indexType";
	
	String ST_PARENT_COLUMN_KEY = "parentColumn";
	
	String ST_PARENT_TABLE_KEY = "parentTableName";
	
	String ST_SEQUENCE_NAME_KEY = "sequenceName";
	
	String ST_SELECT_STATEMENT_KEY = "selectStatement";
	
	String ST_STORAGE_OPTION_KEY = "storageOption";
	
	String ST_TABLE_NAME_KEY = "tableName";
	
	String ST_UNIQUE_KEY = "unique";
	
	String ST_VIEW_NAME_KEY = "viewName";
}
