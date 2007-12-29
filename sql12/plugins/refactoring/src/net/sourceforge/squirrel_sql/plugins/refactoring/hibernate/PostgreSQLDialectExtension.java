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

import net.sourceforge.squirrel_sql.fw.dialects.PostgreSQLDialect;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;

import java.util.Collection;
import java.util.List;
import java.util.Vector;


public class PostgreSQLDialectExtension extends PostgreSQLDialect implements IHibernateDialectExtension {

    /**
     * Access Methods Field. Values "btree", "hash", "gist", "gin".
     */
    private static final String[] ACCESS_METHODS = {"btree", "hash", "gist", "gin"};


    /**
     * Shapes the table name depending on the prefereneces.
     * If isQualifyTableNames is true, the qualified name of the table is returned.
     *
     * @param identifier identifier to be shaped
     * @param qualifier  qualifier of the identifier
     * @param prefs      preferences for generated sql scripts
     * @return the shaped table name
     */
    private String shapeQualifiableIdentifier(String identifier, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs) {
        return DialectUtilsExtension.shapeQualifiableIdentifier(identifier, qualifier, prefs, this);
    }


    /**
     * Shapes the identifier depending on the preferences.
     * If isQuoteIdentifiers is true, the identifier is quoted with dialect-specific delimiters.
     *
     * @param identifier identifier to be shaped
     * @param prefs      preferences for generated sql scripts
     * @return the shaped identifier
     */
    private String shapeIdentifier(String identifier, SqlGenerationPreferences prefs) {
        return DialectUtilsExtension.shapeIdentifier(identifier, prefs, this);
    }


    public boolean supportsSequence() {
        return supportsSequences();
    }


    public boolean supportsCheckOptionsForViews() {
        return false;
    }


    public boolean supportsIndexes() {
        return true;
    }


    public boolean supportsTablespace() {
        return true;
    }


    public boolean supportsAccessMethods() {
        return true;
    }


    public boolean supportsDropView() {
        return true;
    }


    public boolean supportsRenameView() {
        return true;
    }


    public boolean supportsAutoIncrement() {
        return true;
    }


    public boolean supportsEmptyTables() {
        return true;
    }


    public boolean supportsMultipleRowInserts() {

        return true;
    }


    public boolean supportsAddForeignKeyConstraint() {
        return true;
    }


    public boolean supportsAddUniqueConstraint() {
        return true;
    }


    public boolean supportsAlterSequence() {
        return true;
    }


    public boolean supportsCreateIndex() {
        return true;
    }


    public boolean supportsCreateSequence() {
        return true;
    }


    public boolean supportsCreateTable() {
        return true;
    }


    public boolean supportsCreateView() {
        return true;
    }


    public boolean supportsDropConstraint() {
        return true;
    }


    public boolean supportsDropIndex() {
        return true;
    }


    public boolean supportsDropSequence() {
        return true;
    }


    public boolean supportsInsertInto() {
        return true;
    }


    public boolean supportsRenameTable() {
        return true;
    }


    public boolean supportsSequenceInformation() {
        return true;
    }


    public boolean supportsUpdate() {
        return true;
    }


    public String[] getAccessMethodsTypes() {
        return ACCESS_METHODS;
    }


    public String getCreateTableSQL(String simpleName, List<TableColumnInfo> columns, List<TableColumnInfo> primaryKeys, SqlGenerationPreferences prefs, DatabaseObjectQualifier qualifier) {
        if (columns.isEmpty() && !supportsEmptyTables()) {
            throw new IllegalArgumentException(getDisplayName() + " does not support empty tables. (parameter 'columns' has to contain at least one column)");
        }

        // CREATE TABLE tableName (
        //  column1 int,
        //  column2 varchar(20) NOT NULL DEFAULT 'Hello World'
        //  CONSTRAINT tableName_pkey PRIMARY KEY(column1,column2)
        // );
        StringBuilder sql = new StringBuilder();

        sql.append(DialectUtilsExtension.CREATE_TABLE_CLAUSE + " ");
        sql.append(shapeQualifiableIdentifier(simpleName, qualifier, prefs)).append(" (\n");
        for (TableColumnInfo column : columns) {
            sql.append(" ").append(shapeIdentifier(column.getColumnName(), prefs)).append(" ");
            sql.append(getTypeName(column.getDataType(), column.getColumnSize(), column.getColumnSize(), column.getDecimalDigits()));

            if (primaryKeys != null && primaryKeys.size() == 1 &&
                    primaryKeys.get(0).getColumnName().equals(column.getColumnName())) {
                sql.append(" " + DialectUtilsExtension.PRIMARY_KEY_CLAUSE);
            } else if (column.isNullAllowed() == 0) {
                sql.append(" " + DialectUtilsExtension.NOT_NULL_CLAUSE);
            }
            if (column.getDefaultValue() != null)
                sql.append(" " + DialectUtilsExtension.DEFAULT_CLAUSE + " ").append(column.getDefaultValue());

            sql.append(",\n");
        }

        if (primaryKeys != null && primaryKeys.size() > 1) {
            sql.append(" " + DialectUtilsExtension.CONSTRAINT_CLAUSE + " ").append(shapeIdentifier(simpleName + "_pkey", prefs))
                    .append(" " + DialectUtilsExtension.PRIMARY_KEY_CLAUSE + "(");
            for (TableColumnInfo pkPart : primaryKeys) {
                sql.append(shapeIdentifier(pkPart.getColumnName(), prefs)).append(",");
            }
            sql.setLength(sql.length() - 1);
            sql.append(")");
        } else {
            sql.setLength(sql.length() - 2);
        }
        sql.append("\n)").append(prefs.getSqlStatementSeparator());

        return sql.toString();
    }


    public String getRenameTableSQL(String oldTableName, String newTableName, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs) {
        // ALTER TABLE oldTableName RENAME TO newTableName;
        StringBuilder sql = new StringBuilder();

        sql.append(DialectUtilsExtension.ALTER_TABLE_CLAUSE + " ");
        sql.append(shapeQualifiableIdentifier(oldTableName, qualifier, prefs)).append(" ");
        sql.append("RENAME TO ").append(shapeIdentifier(newTableName, prefs)).append(prefs.getSqlStatementSeparator());

        return sql.toString();
    }


    public String getCreateViewSQL(String viewName, String definition, String checkOption, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs) {
        // CREATE VIEW viewName
        //  AS definition;
        StringBuilder sql = new StringBuilder();

        sql.append(DialectUtilsExtension.CREATE_VIEW_CLAUSE + " ").append(shapeQualifiableIdentifier(viewName, qualifier, prefs)).append("\n");
        sql.append(" AS ").append(definition);
        if (supportsCheckOptionsForViews() && checkOption != null && !checkOption.equals("")) {
            sql.append("\n WITH ").append(checkOption).append(" CHECK OPTION");
        }
        sql.append(prefs.getSqlStatementSeparator());

        return sql.toString();
    }


    public String getRenameViewSQL(String oldViewName, String newViewName, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs) {
        // Renaming a view has the same syntax as renaming a table.
        return getRenameTableSQL(oldViewName, newViewName, qualifier, prefs);
    }


    public String getDropViewSQL(String viewName, boolean cascade, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs) {
        // DROP VIEW viewName CASCADE;
        StringBuffer sql = new StringBuffer();

        sql.append(DialectUtilsExtension.DROP_VIEW_CLAUSE + " ");
        sql.append(shapeQualifiableIdentifier(viewName, qualifier, prefs));
        sql.append(" ");
        sql.append(cascade ? DialectUtilsExtension.CASCADE_CLAUSE : DialectUtilsExtension.RESTRICT_CLAUSE);
        sql.append(prefs.getSqlStatementSeparator());

        return sql.toString();
    }


    public String getCreateIndexSQL(String indexName, String tableName, String[] columns, boolean unique, String tablespace, String constraints, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs) {
        // CREATE UNIQUE INDEX indexName ON tableName (column1, column2) TABLESPACE
        //  WHERE constraints;
        StringBuilder sql = new StringBuilder();

        sql.append(DialectUtilsExtension.CREATE_CLAUSE + " ");
        if (unique) sql.append(DialectUtilsExtension.UNIQUE_CLAUSE + " ");
        sql.append(DialectUtilsExtension.INDEX_CLAUSE + " ");
        sql.append(shapeIdentifier(indexName, prefs));
        sql.append(" ON ").append(shapeQualifiableIdentifier(tableName, qualifier, prefs)).append(" ");

        sql.append("(");
        for (String column : columns) {
            sql.append(shapeIdentifier(column, prefs)).append(", ");
        }
        sql.delete(sql.length() - 2, sql.length());     // deletes the last ", "
        sql.append(")");

        if (tablespace != null && !tablespace.equals("")) {
            sql.append(" \n TABLESPACE ").append(tablespace);
        }

        if (constraints != null && !constraints.equals("")) {
            sql.append(" \n " + DialectUtilsExtension.WHERE_CLAUSE + " ").append(constraints);
        }
        sql.append(prefs.getSqlStatementSeparator());

        return sql.toString();
    }


    public String getDropIndexSQL(String indexName, boolean cascade, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs) {
        // DROP INDEX indexName CASCADE;
        StringBuilder sql = new StringBuilder();

        sql.append(DialectUtilsExtension.DROP_INDEX_CLAUSE + " ");
        sql.append(shapeQualifiableIdentifier(indexName, qualifier, prefs)).append(" ");
        sql.append(cascade ? DialectUtilsExtension.CASCADE_CLAUSE : DialectUtilsExtension.RESTRICT_CLAUSE);
        sql.append(prefs.getSqlStatementSeparator());

        return sql.toString();
    }


    public String getCreateSequenceSQL(String sequenceName, String increment, String minimum, String maximum, String start, String cache, boolean cycle, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs) {
        // CREATE SEQUENCE sequenceName
        //  INCREMENT BY increment MINVALUE minimum MAXVALUE maxvalue
        //  RESTART WITH restart CACHE cache CYCLE;
        StringBuilder sql = new StringBuilder();

        sql.append(DialectUtilsExtension.CREATE_SEQUENCE_CLAUSE).append(" ");
        sql.append(shapeQualifiableIdentifier(sequenceName, qualifier, prefs)).append("\n");

        if (increment != null && !increment.equals("")) {
            sql.append("INCREMENT BY ");
            sql.append(increment).append(" ");
        }

        if (minimum != null && !minimum.equals("")) {
            sql.append("MINVALUE ");
            sql.append(minimum).append(" ");
        } else sql.append("NO MINVALUE ");

        if (maximum != null && !maximum.equals("")) {
            sql.append("MAXVALUE ");
            sql.append(maximum).append("\n");
        } else sql.append("NO MAXVALUE\n");


        if (start != null && !start.equals("")) {
            sql.append("START WITH ");
            sql.append(start).append(" ");
        }

        if (cache != null && !cache.equals("")) {
            sql.append("CACHE ");
            sql.append(cache).append(" ");
        }

        if (!cycle) sql.append("NO ");
        sql.append("CYCLE").append(prefs.getSqlStatementSeparator());

        return sql.toString();
    }


    public String getAlterSequenceSQL(String sequenceName, String increment, String minimum, String maximum, String restart, String cache, boolean cycle, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs) {
        // ALTER SEQUENCE sequenceName
        //  INCREMENT BY increment MINVALUE minimum MAXVALUE maxvalue
        //  RESTART WITH restart CACHE cache CYCLE;
        StringBuilder sql = new StringBuilder();

        sql.append(DialectUtilsExtension.ALTER_SEQUENCE_CLAUSE + " ");
        sql.append(shapeQualifiableIdentifier(sequenceName, qualifier, prefs)).append("\n");

        if (increment != null && !increment.equals("")) {
            sql.append("INCREMENT BY ");
            sql.append(increment).append(" ");
        }

        if (minimum != null && !minimum.equals("")) {
            sql.append("MINVALUE ");
            sql.append(minimum).append(" ");
        } else sql.append("NO MINVALUE ");

        if (maximum != null && !maximum.equals("")) {
            sql.append("MAXVALUE ");
            sql.append(maximum).append("\n");
        } else sql.append("NO MAXVALUE\n");

        if (restart != null && !restart.equals("")) {
            sql.append("RESTART WITH ");
            sql.append(restart).append(" ");
        }

        if (cache != null && !cache.equals("")) {
            sql.append("CACHE ");
            sql.append(cache).append(" ");
        }

        if (!cycle) sql.append("NO ");
        sql.append("CYCLE").append(prefs.getSqlStatementSeparator());

        return sql.toString();
    }


    public String getSequenceInformationSQL(String sequenceName, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs) {
        // SELECT last_value, max_value, min_value, cache_value, increment_by, is_cycled FROM sequenceName;
        StringBuilder sql = new StringBuilder();

        sql.append("SELECT last_value, max_value, min_value, cache_value, increment_by, is_cycled FROM ");
        sql.append(shapeQualifiableIdentifier(sequenceName, qualifier, prefs));
        sql.append(prefs.getSqlStatementSeparator());

        return sql.toString();
    }


    public String getDropSequenceSQL(String sequenceName, boolean cascade, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs) {
        // DROP SEQUENCE sequenceName CASCADE;
        StringBuilder sql = new StringBuilder();

        sql.append("DROP SEQUENCE ").append(shapeQualifiableIdentifier(sequenceName, qualifier, prefs)).append(" ");
        sql.append(cascade ? "CASCADE" : "RESTRICT").append(prefs.getSqlStatementSeparator());

        return sql.toString();
    }


    public String getAddForeignKeyConstraintSQL(String localTableName, String refTableName, String constraintName, boolean deferrable, boolean initiallyDeferred, boolean matchFull, boolean autoFKIndex, String fkIndexName, Collection<String[]> localRefColumns, String onUpdateAction, String onDeleteAction, DatabaseObjectQualifier qualifier,
                                                SqlGenerationPreferences prefs) {
        // ALTER TABLE localTableName
        //  ADD CONSTRAINT constraintName FOREIGN KEY (localColumn1, localColumn2)
        //  REFERENCES referencedTableName (referencedColumn1, referencedColumn2)
        //  MATCH FULL ON UPDATE RESTRICT ON DELETE NO ACTION DEFERRABLE INITIALLY DEFERRED;
        StringBuilder sql = new StringBuilder();

        sql.append(DialectUtilsExtension.ALTER_TABLE_CLAUSE + " ");
        sql.append(shapeQualifiableIdentifier(localTableName, qualifier, prefs)).append("\n");

        if (constraintName != null && !constraintName.equals("")) {
            sql.append(" " + DialectUtilsExtension.ADD_CONSTRAINT_CLAUSE + " ");
            sql.append(shapeIdentifier(constraintName, prefs)).append("\n");
        }

        sql.append(" " + DialectUtilsExtension.FOREIGN_KEY_CLAUSE + " (");

        Vector<String> localColumns = new Vector<String>();
        StringBuilder refColumns = new StringBuilder();
        for (String[] columns : localRefColumns) {
            sql.append(shapeIdentifier(columns[0], prefs)).append(", ");
            localColumns.add(columns[0]);
            refColumns.append(shapeIdentifier(columns[1], prefs)).append(", ");
        }
        sql.setLength(sql.length() - 2);                    // deletes the last ", "
        refColumns.setLength(refColumns.length() - 2);      // deletes the last ", "

        sql.append(")\n REFERENCES ");
        sql.append(shapeQualifiableIdentifier(refTableName, qualifier, prefs)).append(" (");
        sql.append(refColumns.toString()).append(")\n");

        // Options
        if (matchFull) sql.append(" MATCH FULL");

        if (onUpdateAction != null && !onUpdateAction.equals("")) {
            sql.append(" ON UPDATE ");
            sql.append(onUpdateAction);
        }

        if (onDeleteAction != null && !onDeleteAction.equals("")) {
            sql.append(" ON DELETE ");
            sql.append(onDeleteAction);
        }

        if (deferrable) sql.append(" DEFERRABLE");
        if (initiallyDeferred) sql.append(" INITIALLY DEFERRED");
        sql.append(prefs.getSqlStatementSeparator());

        // Additional Index Creation
        if (autoFKIndex && !fkIndexName.equals("")) {
            sql.append("\n");
            sql.append(getCreateIndexSQL(fkIndexName, localTableName,
                    localColumns.toArray(new String[localColumns.size()]), false, null, null,
                    qualifier, prefs));
        }

        return sql.toString();
    }


    public String getAddUniqueConstraintSQL(String tableName, String constraintName, String[] columns, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs) {
        // ALTER TABLE tableName
        //  ADD CONSTRAINT constraintName UNIQUE (column1, column2);
        StringBuilder sql = new StringBuilder();

        sql.append(DialectUtilsExtension.ALTER_TABLE_CLAUSE + " ");
        sql.append(shapeQualifiableIdentifier(tableName, qualifier, prefs)).append("\n");

        sql.append(" " + DialectUtilsExtension.ADD_CONSTRAINT_CLAUSE + " ");
        sql.append(shapeIdentifier(constraintName, prefs));

        sql.append(" " + DialectUtilsExtension.UNIQUE_CLAUSE + " (");
        for (String column : columns) {
            sql.append(shapeIdentifier(column, prefs)).append(", ");
        }
        sql.delete(sql.length() - 2, sql.length());     // deletes the last ", "
        sql.append(")").append(prefs.getSqlStatementSeparator());

        return sql.toString();
    }


    public String getAddAutoIncrementSQL(TableColumnInfo column, SqlGenerationPreferences prefs) {
        DatabaseObjectQualifier qualifier = new DatabaseObjectQualifier(column.getCatalogName(), column.getSchemaName());
        // ALTER TABLE tableName
        //  ALTER COLUMN columnName
        //  SET DEFAULT nextval('tableName_columnName_seq');
        // ALTER SEQUENCE tableName_columnName_seq OWNED BY tableName.columnName;
        StringBuilder sql = new StringBuilder();

        // In PostgreSQL we need to add a sequence to support auto-increment (name: tablename_colname_seq)
        String sequenceName = column.getTableName() + "_" + column.getColumnName() + "_seq";
        sql.append(getCreateSequenceSQL(sequenceName, null, null, null, null, null, false,
                qualifier, prefs)).append("\n\n");

        sql.append(DialectUtilsExtension.ALTER_TABLE_CLAUSE + " ");
        sql.append(shapeQualifiableIdentifier(column.getTableName(), qualifier, prefs)).append("\n");

        sql.append(" " + DialectUtilsExtension.ALTER_COLUMN_CLAUSE + " ");
        sql.append(shapeIdentifier(column.getColumnName(), prefs)).append("\n");

        sql.append(" " + DialectUtilsExtension.SET_DEFAULT_CLAUSE + " nextval('")
                .append(shapeQualifiableIdentifier(sequenceName, qualifier, prefs)).append("')");
        sql.append(prefs.getSqlStatementSeparator()).append("\n\n");

        sql.append(DialectUtilsExtension.ALTER_SEQUENCE_CLAUSE + " ").
                append(shapeQualifiableIdentifier(sequenceName, qualifier, prefs)).append("\n");
        sql.append(" OWNED BY ");
        sql.append(shapeQualifiableIdentifier(column.getTableName(), qualifier, prefs)).append(".")
                .append(shapeIdentifier(column.getColumnName(), prefs));
        sql.append(prefs.getSqlStatementSeparator());

        return sql.toString();
    }


    public String getDropConstraintSQL(String tableName, String constraintName, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs) {
        // ALTER TABLE tableName
        //  DROP CONSTRAINT constraintName;
        StringBuilder sql = new StringBuilder();

        sql.append(DialectUtilsExtension.ALTER_TABLE_CLAUSE + " ");
        sql.append(shapeQualifiableIdentifier(tableName, qualifier, prefs)).append("\n");

        sql.append(" " + DialectUtilsExtension.DROP_CONSTRAINT_CLAUSE + " ");
        sql.append(shapeIdentifier(constraintName, prefs));
        sql.append(prefs.getSqlStatementSeparator());
        return sql.toString();
    }


    public String getInsertIntoSQL(String tableName, List<String> columns, String query, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs) {
        if (query == null || query.length() == 0) return "";

        // INSERT INTO tableName (column1, column2)
        //  query;
        StringBuilder sql = new StringBuilder();

        sql.append(DialectUtilsExtension.INSERT_INTO_CLAUSE + " ");
        sql.append(shapeQualifiableIdentifier(tableName, qualifier, prefs));
        if (columns != null && !columns.isEmpty()) {
            sql.append(" (");
            for (String column : columns) {
                sql.append(shapeIdentifier(column, prefs)).append(", ");
            }
            sql.setLength(sql.length() - 2);
            sql.append(")");
        }
        sql.append("\n");

        sql.append(" ").append(query);
        if (sql.charAt(sql.length() - 1) != ';') sql.append(prefs.getSqlStatementSeparator());

        return sql.toString();
    }


    public String getUpdateSQL(String tableName, String[] setColumns, String[] setValues, String[] fromTables, String[] whereColumns, String[] whereValues, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs) {
        if ((setColumns == null && setValues == null) || (setColumns != null && setValues != null && setColumns.length == 0 && setValues.length == 0))
            return "";
        if ((setColumns != null && setValues != null && setColumns.length != setValues.length) || setColumns == null || setValues == null)
            throw new IllegalArgumentException("The amount of SET columns and values must be the same!");
        if ((whereColumns != null && whereValues != null && whereColumns.length != whereValues.length) || (whereColumns == null && whereValues != null) || (whereColumns != null && whereValues == null))
            throw new IllegalArgumentException("The amount of WHERE columns and values must be the same!");

        // UPDATE tableName SET setColumn1 = setValue1, setColumn2 = setValue2
        //  FROM fromTable1, fromTable2
        //  WHERE whereColumn1 = whereValue1 AND whereColumn2 = whereValue2;
        StringBuilder sql = new StringBuilder();

        sql.append(DialectUtilsExtension.UPDATE_CLAUSE + " ");
        sql.append(shapeQualifiableIdentifier(tableName, qualifier, prefs));
        sql.append(" " + DialectUtilsExtension.SET_CLAUSE + " ");
        for (int i = 0; i < setColumns.length; i++) {
            sql.append(shapeIdentifier(setColumns[i], prefs));
            if (setValues[i] == null) sql.append(" = NULL");
            else sql.append(" = ").append(setValues[i]);
            sql.append(", ");
        }
        sql.setLength(sql.length() - 2);

        if (fromTables != null) {
            sql.append("\n " + DialectUtilsExtension.FROM_CLAUSE + " ");
            for (String from : fromTables) {
                sql.append(shapeQualifiableIdentifier(from, qualifier, prefs)).append(", ");
            }
            sql.setLength(sql.length() - 2);
        }

        if (whereColumns != null && whereColumns.length != 0) {
            sql.append("\n " + DialectUtilsExtension.WHERE_CLAUSE + " ");
            for (int i = 0; i < whereColumns.length; i++) {
                sql.append(shapeIdentifier(whereColumns[i], prefs));
                if (whereValues[i] == null) sql.append(" IS NULL");
                else sql.append(" = ").append(whereValues[i]);
                sql.append(" " + DialectUtilsExtension.AND_CLAUSE + " ");
            }
            sql.setLength(sql.length() - 5);
        }
        sql.append(prefs.getSqlStatementSeparator());

        return sql.toString();
    }


    public String getAddColumnSQL(TableColumnInfo column, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs) {
        // ALTER TABLE tableName
        //  ADD COLUMN columnName columnType NOT NULL DEFAULT 'defaultValue';
        StringBuilder sql = new StringBuilder();

        sql.append(DialectUtilsExtension.ALTER_TABLE_CLAUSE + " ");
        sql.append(shapeQualifiableIdentifier(column.getTableName(), qualifier, prefs)).append("\n");

        sql.append(" " + DialectUtilsExtension.ADD_COLUMN_CLAUSE + " ");
        sql.append(shapeIdentifier(column.getColumnName(), prefs)).append(" ");
        sql.append(getTypeName(column.getDataType(), column.getColumnSize(), column.getColumnSize(), column.getDecimalDigits()));

        if (column.isNullAllowed() == 0) {
            sql.append(" " + DialectUtilsExtension.NOT_NULL_CLAUSE);
        }
        if (column.getDefaultValue() != null) {
            sql.append(" DEFAULT ").append(column.getDefaultValue());
        }

        sql.append(prefs.getSqlStatementSeparator());

        return sql.toString();
    }
}
