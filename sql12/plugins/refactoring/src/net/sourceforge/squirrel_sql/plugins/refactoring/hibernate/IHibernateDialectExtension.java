package net.sourceforge.squirrel_sql.plugins.refactoring.hibernate;
/*
 * Copyright (C) 2007 Daniel Regli & Yannick Winiger
 * http://sourceforge.net/projects/squirrel-sql
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

import net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;

import java.util.Collection;
import java.util.List;

/**
 * An interface for methods implemented by database dialects for the purpose of
 * handling standard and non-standard SQL and database types. Extended with methods
 * used by the refactoring plugin.
 */
public interface IHibernateDialectExtension extends HibernateDialect {

    /**
     * Returns a boolean value indicating whether or not this database dialect supports sequences.
     *
     * @return true if the database supports sequence; false otherwise.
     */
    public boolean supportsSequence();


    /**
     * Returns a boolean value indicating whether or not this database dialect suports tablespaces.
     *
     * @return true if the database supports tablespaces; false otherwise.
     */
    public boolean supportsTablespace();


    /**
     * Returns a boolean value indicating whether or not this database dialect supports indexes.
     *
     * @return true if the database supports indexes; false otherwise.
     */
    public boolean supportsIndexes();


    /**
     * Returns a boolean value indicating whether or not this database dialect supports access methods.
     *
     * @return true if the database supports access methods; false otherwise.
     */
    public boolean supportsAccessMethods();


    /**
     * Returns a boolean value indicatin whether or not this database dialect supports auto-increment on columns.
     *
     * @return true if the database supports auto-increment; false otherwise.
     */
    public boolean supportsAutoIncrement();


    /**
     * Returns a boolean value indicating whether or not this database dialect supports check options for views.
     *
     * @return true if the database supports check options for views; false otherwise.
     */
    public boolean supportsCheckOptionsForViews();


    /**
     * Returns a boolean value indicating whether or not this database dialect supports empty tables.
     *
     * @return true if the database supports empty tables; false otherwise.
     */
    public boolean supportsEmptyTables();


    /**
     * Returns a boolean value indicating whether or not this database dialect supports the optional SQL
     * feature "row value constructors" (F641) that allows to insert multiple rows in a single insert statement.
     *
     * @return true if the database supports multiple row inserts; false otherwise.
     */
    public boolean supportsMultipleRowInserts();


    /**
     * Returns a boolean value indicating whether or not this database dialect supports adding foreign key constraints.
     *
     * @return true if the database supports adding foreign key constraints; false otherwise.
     */
    public boolean supportsAddForeignKeyConstraint();


    /**
     * Returns a boolean value indicating whether or not this database dialect supports adding unique constraints.
     *
     * @return true if the database supports adding unique constraints; false otherwise.
     */
    public boolean supportsAddUniqueConstraint();


    /**
     * Returns a boolean value indicating whether or not this database dialect supports altering sequences.
     *
     * @return true if the database supports altering sequences; false otherwise.
     */
    public boolean supportsAlterSequence();


    /**
     * Returns a boolean value indicating whether or not this database dialect supports creating indexes.
     *
     * @return true if the database supports creating indexes; false otherwise.
     */
    public boolean supportsCreateIndex();


    /**
     * Returns a boolean value indicating whether or not this database dialect supports creating sequences.
     *
     * @return true if the database supports creating sequences; false otherwise.
     */
    public boolean supportsCreateSequence();


    /**
     * Returns a boolean value indicating whether or not this database dialect supports creating tables.
     *
     * @return true if the database supports creating tables; false otherwise.
     */
    public boolean supportsCreateTable();


    /**
     * Returns a boolean value indicating whether or not this database dialect supports creating views.
     *
     * @return true if the database supports creating views; false otherwise.
     */
    public boolean supportsCreateView();


    /**
     * Returns a boolean value indicating whether or not this database dialect supports dropping constraints.
     *
     * @return true if the database supports dropping constraints; false otherwise.
     */
    public boolean supportsDropConstraint();


    /**
     * Returns a boolean value indicating whether or not this database dialect supports dropping indexes.
     *
     * @return true if the database supports dropping indexes; false otherwise.
     */
    public boolean supportsDropIndex();


    /**
     * Returns a boolean value indicating whether or not this database dialect supports dropping sequences.
     *
     * @return true if the database supports dropping sequences; false otherwise.
     */
    public boolean supportsDropSequence();


    /**
     * Returns a boolean value indicating whether or not this database dialect supports dropping views.
     *
     * @return true if the database supports dropping views; false otherwise.
     */
    public boolean supportsDropView();


    /**
     * Returns a boolean value indicating whether or not this database dialect supports inserting rows.
     *
     * @return true if the database supports inserting rows; false otherwise.
     */
    public boolean supportsInsertInto();


    /**
     * Returns a boolean value indicating whether or not this database dialect supports renaming tables.
     *
     * @return true if the database supports renaming tables; false otherwise.
     */
    public boolean supportsRenameTable();


    /**
     * Returns a boolean value indicating whether or not this database dialect supports renaming views.
     *
     * @return true if the database supports renaming views; false otherwise.
     */
    public boolean supportsRenameView();


    /**
     * Returns a boolean value indicating whether or not this database dialect supports
     * gathering information about sequences.
     *
     * @return true if the database supports gathering information about sequences; false otherwise.
     */
    public boolean supportsSequenceInformation();


    /**
     * Returns a boolean value indicating whether or not this database dialect supports updating rows.
     *
     * @return true if the database supports updating rows; false otherwise.
     */
    public boolean supportsUpdate();


    /**
     * Gets the access methods which this dialect supports.
     *
     * @return all the access methods supported by this dialect.
     */
    public String[] getAccessMethodsTypes();


    /**
     * Gets the SQL command to create a new table.
     *
     * @param tableName   simple name of the table
     * @param columns     columns of the table
     * @param primaryKeys primary keys of the table
     * @param prefs       preferences for generated sql scripts
     * @param qualifier   qualifier of the table
     * @return the sql command to create a table.
     */
    public String getCreateTableSQL(String tableName, List<TableColumnInfo> columns, List<TableColumnInfo> primaryKeys, SqlGenerationPreferences prefs, DatabaseObjectQualifier qualifier);


    /**
     * Gets the SQL command to rename a table.
     *
     * @param oldTableName old name of the table
     * @param newTableName new name of the table
     * @param qualifier    qualifier of the table
     * @param prefs        preferences for generated sql scripts
     * @return the sql command to rename a table.
     */
    public String getRenameTableSQL(String oldTableName, String newTableName, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs);


    /**
     * Gets the SQL command to create a view.
     *
     * @param viewName    name of the view
     * @param definition  old definition of the view.
     * @param checkOption CHECK OPTION. CASCADE, LOCAL or null for no check option.
     * @param qualifier   qualifier of the table
     * @param prefs       preferences for generated sql scripts
     * @return the sql command to create a view.
     */
    public String getCreateViewSQL(String viewName, String definition, String checkOption, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs);


    /**
     * Gets the SQL command to rename a view.
     *
     * @param oldViewName old name of the view
     * @param newViewName new name of the view
     * @param qualifier   qualifier of the table
     * @param prefs       preferences for generated sql scripts
     * @return the sql command
     */
    public String getRenameViewSQL(String oldViewName, String newViewName, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs);


    /**
     * Gets the SQL command to drop a view.
     *
     * @param viewName  name of the view
     * @param cascade   cascade true if automatically drop object that depend on the view (such as other views).
     * @param qualifier qualifier of the table
     * @param prefs     preferences for generated sql scripts
     * @return the SQL command to drop a view.
     */
    public String getDropViewSQL(String viewName, boolean cascade, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs);


    /**
     * Gets the SQL command to create an index.
     *
     * @param indexName   name of the index to be created
     * @param tableName   name of the table
     * @param columns     columns where the index should be stored for
     * @param unique      true if the index should be unique
     * @param tablespace  tablespace for the index (leave empty for no tablespace)
     * @param constraints constraints for the index (leave empty for no constraints)
     * @param qualifier   qualifier of the table
     * @param prefs       preferences for generated sql scripts
     * @return the sql command to create an index.
     */
    public String getCreateIndexSQL(String indexName, String tableName, String[] columns, boolean unique, String tablespace, String constraints, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs);


    /**
     * Gets the SQL command to drop an index.
     *
     * @param indexName name of the index
     * @param cascade   true if automatically drop object that depend on the view (such as other views).
     * @param qualifier qualifier of the table
     * @param prefs     preferences for generated sql scripts
     * @return the sql command to drop an index.
     */
    public String getDropIndexSQL(String indexName, boolean cascade, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs);


    /**
     * Gets the SQL command to create a sequence.
     *
     * @param sequenceName name of the sequence
     * @param increment    increment value
     * @param minimum      minimum value (leave empty for NO MINVALUE)
     * @param maximum      maximum value (leave empty for NO MINVALUE)
     * @param start        start value (leave empty for default)
     * @param cache        cache value, how many sequences should be preallocated (leave empty for default)
     * @param cycle        true if the sequence should wrap around when the max-/minvalue has been reached (leave empty for NO CYCLE)
     * @param qualifier    qualifier of the table
     * @param prefs        preferences for generated sql scripts
     * @return the sql command to create a sequence.
     */
    public String getCreateSequenceSQL(String sequenceName, String increment, String minimum, String maximum, String start, String cache, boolean cycle, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs);


    /**
     * Gets the SQL command to alter a sequence.
     *
     * @param sequenceName name of the sequence.
     * @param increment    increment value.
     * @param minimum      minimum value.
     * @param maximum      maximum value.
     * @param restart      start value.
     * @param cache        cache value, how many sequences should be preallocated.
     * @param cycle        true if the sequence should wrap around when the max-/minvalue has been reached.
     * @param qualifier    qualifier of the table
     * @param prefs        preferences for generated sql scripts
     * @return the sql command
     */
    public String getAlterSequenceSQL(String sequenceName, String increment, String minimum, String maximum, String restart, String cache, boolean cycle, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs);


    /**
     * Gets the SQL command to query the specific database to find out the information about the sequence.
     * The query should return the following fields:
     * last_value, max_value, min_value, cache_value, increment_by, is_cycled
     *
     * @param sequenceName the name of the sequence.
     * @param qualifier    qualifier of the table
     * @param prefs        preferences for generated sql scripts
     * @return the sql command to query the database.
     */
    public String getSequenceInformationSQL(String sequenceName, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs);


    /**
     * Gets the SQL command to drop a sequence.
     *
     * @param sequenceName name of the sequence
     * @param cascade      true if automatically drop object that depend on the view (such as other views).
     * @param qualifier    qualifier of the table
     * @param prefs        preferences for generated sql scripts
     * @return the sql command to drop a sequence.
     */
    public String getDropSequenceSQL(String sequenceName, boolean cascade, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs);


    /**
     * Gets the SQL command to add a foreign key constraint to a table.
     *
     * @param localTableName    name of the table where the foreign key should be stored.
     * @param refTableName      name of the table where the foreign key should reference to.
     * @param constraintName    name of the constraint. Leave it empty and it won't create a CONSTRAINT name.
     * @param deferrable        true if the constraint is deferrable, false if not.
     * @param initiallyDeferred true if the constraint is deferrable and initially deferred, false if not.
     * @param matchFull         true if the referenced columns using MATCH FULL.
     * @param autoFKIndex       true to create an additional INDEX with the given fkIndexName Name.
     * @param fkIndexName       name of the foreign key index name.
     * @param localRefColumns   local and referenced column collection. In the first Element of the String Array should be the local and in the second Element the referenced Table.
     * @param onUpdateAction    update action. For example "RESTRICT".
     * @param onDeleteAction    delete action. For exampel "NO ACTION".
     * @param qualifier         qualifier of the table
     * @param prefs             preferences for generated sql scripts
     * @return the sql command to add a foreign key constraint.
     */
    public String getAddForeignKeyConstraintSQL(String localTableName, String refTableName, String constraintName, boolean deferrable, boolean initiallyDeferred, boolean matchFull, boolean autoFKIndex, String fkIndexName, Collection<String[]> localRefColumns, String onUpdateAction, String onDeleteAction, DatabaseObjectQualifier qualifier,
                                                SqlGenerationPreferences prefs);


    /**
     * Gets the SQL command to add a unique constraint to a table.
     *
     * @param tableName      name of the table where the unique constraint should be added to.
     * @param constraintName name of the constraint.
     * @param columns        the unique columns.
     * @param qualifier      qualifier of the table
     * @param prefs          preferences for generated sql scripts
     * @return the sql command to add a unique constraint.
     */
    public String getAddUniqueConstraintSQL(String tableName, String constraintName, String[] columns, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs);


    /**
     * Gets the SQL to add an auto-increment to a column.
     *
     * @param column column to where the auto-increment should be added to.
     * @param prefs  preferences for generated sql scripts
     * @return the sql command to add auto-increment.
     */
    public String getAddAutoIncrementSQL(TableColumnInfo column, SqlGenerationPreferences prefs);


    /**
     * Gets the SQL command to drop a constraint from a table.
     *
     * @param tableName      name of the table where the constraint should be dropped from.
     * @param constraintName name of the constraint.
     * @param qualifier      qualifier of the table
     * @param prefs          preferences for generated sql scripts
     * @return the sql command to drop a constraint.
     */
    public String getDropConstraintSQL(String tableName, String constraintName, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs);


    /**
     * Gets the SQL command to insert data into a table.
     * <p/>
     * If the list of columns is empty or null the SQL will look like:
     * INSERT INTO tablename
     * valuesPart;
     * <p/>
     * instead of:
     * INSERT INTO tablename ( column1, column2, ... )
     * valuesPart;
     *
     * @param tableName  simple name of the table
     * @param columns    columns of the table
     * @param valuesPart either a query or a VALUES( ... ) string that defines the data to insert
     * @param qualifier  qualifier of the table
     * @param prefs      preferences for generated sql scripts
     * @return the sql command to insert data.
     */
    public String getInsertIntoSQL(String tableName, List<String> columns, String valuesPart, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs);


    /**
     * Gets the SQL command to update the specified columns with the specified values.
     *
     * @param tableName    simple name of the table
     * @param setColumns   columns to be set
     * @param setValues    values the columns should be set with
     * @param fromTables   simple names of the tables in the FROM clause
     * @param whereColumns columns in the WHERE clause
     * @param whereValues  values of the columns in the WHERE clause
     * @param qualifier    qualifier of the table
     * @param prefs        preferences for generated sql scripts
     * @return the sql command to update data.
     */
    public String getUpdateSQL(String tableName, String[] setColumns, String[] setValues, String[] fromTables, String[] whereColumns, String[] whereValues, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs);


    /**
     * Gets the SQL command to add a column to the specified table.
     *
     * @param column    information about the column
     * @param qualifier qualifier of the table
     * @param prefs     preferences for generated sql scripts
     * @return the sql command to add a column
     */
    public String getAddColumnSQL(TableColumnInfo column, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs);
}