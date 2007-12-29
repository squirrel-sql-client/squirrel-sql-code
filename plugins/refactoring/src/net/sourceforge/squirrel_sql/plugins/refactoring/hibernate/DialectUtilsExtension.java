package net.sourceforge.squirrel_sql.plugins.refactoring.hibernate;
/*
* Copyright (C) 2007 Daniel Regli & Yannick Winiger
* http://sourceforge.net/projects/squirrel-sql
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

import net.sourceforge.squirrel_sql.fw.dialects.DialectUtils;
import net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import org.hibernate.dialect.Dialect;

public class DialectUtilsExtension extends DialectUtils {
    /**
     * Logger for this class.
     */
    private static final ILogger s_log = LoggerController.createLogger(DialectUtilsExtension.class);

    /**
     * Internationalized strings for this class.
     */
    private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(DialectUtilsExtension.class);


    // Clauses
    public static final String CREATE_CLAUSE = "CREATE";
    public static final String ALTER_CLAUSE = "ALTER";
    public static final String DROP_CLAUSE = "DROP";

    public static final String TABLE_CLAUSE = "TABLE";
    public static final String CREATE_TABLE_CLAUSE = CREATE_CLAUSE + " " + TABLE_CLAUSE;
    public static final String ALTER_TABLE_CLAUSE = ALTER_CLAUSE + " " + TABLE_CLAUSE;
    public static final String DROP_TABLE_CLAUSE = DROP_CLAUSE + " " + TABLE_CLAUSE;

    public static final String ADD_COLUMN_CLAUSE = "ADD " + COLUMN_CLAUSE;

    public static final String SEQUENCE_CLAUSE = "SEQUENCE";
    public static final String CREATE_SEQUENCE_CLAUSE = CREATE_CLAUSE + " " + SEQUENCE_CLAUSE;
    public static final String ALTER_SEQUENCE_CLAUSE = ALTER_CLAUSE + " " + SEQUENCE_CLAUSE;
    public static final String DROP_SEQUENCE_CLAUSE = DROP_CLAUSE + " " + SEQUENCE_CLAUSE;

    public static final String INDEX_CLAUSE = "INDEX";
    public static final String CREATE_INDEX_CLAUSE = CREATE_CLAUSE + " " + INDEX_CLAUSE;
    public static final String DROP_INDEX_CLAUSE = DROP_CLAUSE + " " + INDEX_CLAUSE;

    public static final String VIEW_CLAUSE = "VIEW";
    public static final String CREATE_VIEW_CLAUSE = CREATE_CLAUSE + " " + VIEW_CLAUSE;
    public static final String DROP_VIEW_CLAUSE = DROP_CLAUSE + " " + VIEW_CLAUSE;

    public static final String UPDATE_CLAUSE = "UPDATE";

    public static final String INSERT_INTO_CLAUSE = "INSERT INTO";

    public static final String FROM_CLAUSE = "FROM";
    public static final String WHERE_CLAUSE = "WHERE";
    public static final String AND_CLAUSE = "AND";

    public static final String PRIMARY_KEY_CLAUSE = "PRIMARY KEY";
    public static final String FOREIGN_KEY_CLAUSE = "FOREIGN KEY";
    public static final String NOT_NULL_CLAUSE = "NOT NULL";
    public static final String UNIQUE_CLAUSE = "UNIQUE";

    public static final String RESTRICT_CLAUSE = "RESTRICT";

    public static final String CONSTRAINT_CLAUSE = "CONSTRAINT";
    public static final String ADD_CONSTRAINT_CLAUSE = "ADD " + CONSTRAINT_CLAUSE;
    public static final String DROP_CONSTRAINT_CLAUSE = "DROP " + CONSTRAINT_CLAUSE;

    // Features (0-7 already defined by DialectUtils, start with 8)
    public static final int CREATE_TABLE_TYPE = 8;
    public static final int RENAME_TABLE_TYPE = 9;
    public static final int CREATE_VIEW_TYPE = 10;
    public static final int RENAME_VIEW_TYPE = 11;
    public static final int DROP_VIEW_TYPE = 12;
    public static final int CREATE_INDEX_TYPE = 13;
    public static final int DROP_INDEX_TYPE = 14;
    public static final int CREATE_SEQUENCE_TYPE = 15;
    public static final int ALTER_SEQUENCE_TYPE = 16;
    public static final int SEQUENCE_INFORMATION_TYPE = 17;
    public static final int DROP_SEQUENCE_TYPE = 18;
    public static final int ADD_FOREIGN_KEY_TYPE = 19;
    public static final int ADD_UNIQUE_TYPE = 20;
    public static final int ADD_AUTO_INCREMENT_TYPE = 21;
    public static final int DROP_CONSTRAINT_TYPE = 22;
    public static final int INSERT_INTO_TYPE = 23;
    public static final int UPDATE_TYPE = 24;


    public static String getUnsupportedMessage(HibernateDialect dialect, int featureId)
            throws UnsupportedOperationException {
        if (featureId < 8) return DialectUtils.getUnsupportedMessage(dialect, featureId);

        switch (featureId) {
            case CREATE_TABLE_TYPE:
                return s_stringMgr.getString("DialectUtils.createTableUnsupported",
                        dialect.getDisplayName());
            case RENAME_TABLE_TYPE:
                return s_stringMgr.getString("DialectUtils.renameTableUnsupported",
                        dialect.getDisplayName());
            case CREATE_VIEW_TYPE:
                return s_stringMgr.getString("DialectUtils.createViewUnsupported",
                        dialect.getDisplayName());
            case RENAME_VIEW_TYPE:
                return s_stringMgr.getString("DialectUtils.renameViewUnsupported",
                        dialect.getDisplayName());
            case DROP_VIEW_TYPE:
                return s_stringMgr.getString("DialectUtils.dropViewUnsupported",
                        dialect.getDisplayName());
            case CREATE_INDEX_TYPE:
                return s_stringMgr.getString("DialectUtils.createIndexUnsupported",
                        dialect.getDisplayName());
            case DROP_INDEX_TYPE:
                return s_stringMgr.getString("DialectUtils.dropIndexUnsupported",
                        dialect.getDisplayName());
            case CREATE_SEQUENCE_TYPE:
                return s_stringMgr.getString("DialectUtils.createSequenceUnsupported",
                        dialect.getDisplayName());
            case ALTER_SEQUENCE_TYPE:
                return s_stringMgr.getString("DialectUtils.alterSequenceUnsupported",
                        dialect.getDisplayName());
            case SEQUENCE_INFORMATION_TYPE:
                return s_stringMgr.getString("DialectUtils.sequenceInformationUnsupported",
                        dialect.getDisplayName());
            case DROP_SEQUENCE_TYPE:
                return s_stringMgr.getString("DialectUtils.dropSequenceUnsupported",
                        dialect.getDisplayName());
            case ADD_FOREIGN_KEY_TYPE:
                return s_stringMgr.getString("DialectUtils.addForeignKeyUnsupported",
                        dialect.getDisplayName());
            case ADD_UNIQUE_TYPE:
                return s_stringMgr.getString("DialectUtils.addUniqueUnsupported",
                        dialect.getDisplayName());
            case ADD_AUTO_INCREMENT_TYPE:
                return s_stringMgr.getString("DialectUtils.addAutoIncrementUnsupported",
                        dialect.getDisplayName());
            case DROP_CONSTRAINT_TYPE:
                return s_stringMgr.getString("DialectUtils.dropConstraintUnsupported",
                        dialect.getDisplayName());
            case INSERT_INTO_TYPE:
                return s_stringMgr.getString("DialectUtils.insertIntoUnsupported",
                        dialect.getDisplayName());
            case UPDATE_TYPE:
                return s_stringMgr.getString("DialectUtils.updateUnsupported",
                        dialect.getDisplayName());
            default:
                throw new IllegalArgumentException("Unknown featureId: " + featureId);
        }
    }


    /**
     * Shapes the table name depending on the prefereneces.
     * If isQualifyTableNames is true, the qualified name of the table is returned.
     *
     * @param identifier identifier to be shaped
     * @param qualifier  qualifier of the identifier
     * @param prefs      preferences for generated sql scripts
     * @param dialect    hibernate dialect
     * @return the shaped table name
     */
    public static String shapeQualifiableIdentifier(String identifier, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs, Dialect dialect) {
        if (prefs.isQualifyTableNames())
            return shapeIdentifier(qualifier.getSchema(), prefs, dialect) + "." + shapeIdentifier(identifier, prefs, dialect);
        else return shapeIdentifier(identifier, prefs, dialect);
    }


    /**
     * Shapes the identifier depending on the preferences.
     * If isQuoteIdentifiers is true, the identifier is quoted with dialect-specific delimiters.
     *
     * @param identifier identifier to be shaped
     * @param prefs      preferences for generated sql scripts
     * @param dialect    hibernate dialect for the dialect specific quotes
     * @return the shaped identifier
     */
    public static String shapeIdentifier(String identifier, SqlGenerationPreferences prefs, Dialect dialect) {
        if (prefs.isQuoteIdentifiers()) return dialect.openQuote() + identifier + dialect.closeQuote();
        else return identifier;
    }
}
