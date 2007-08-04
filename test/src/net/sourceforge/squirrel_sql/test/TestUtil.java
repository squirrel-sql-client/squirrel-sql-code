package net.sourceforge.squirrel_sql.test;

import static java.sql.Types.BIGINT;
import static java.sql.Types.BINARY;
import static java.sql.Types.BLOB;
import static java.sql.Types.CLOB;
import static java.sql.Types.DATE;
import static java.sql.Types.INTEGER;
import static java.sql.Types.LONGVARCHAR;
import static java.sql.Types.VARCHAR;
import static java.util.Arrays.asList;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.startsWith;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.createNiceMock;
import static org.easymock.classextension.EasyMock.replay;

import java.beans.PropertyChangeListener;
import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.Action;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.ForeignKeyInfo;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.IQueryTokenizer;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.IndexInfo;
import net.sourceforge.squirrel_sql.fw.sql.JDBCTypeMapper;
import net.sourceforge.squirrel_sql.fw.sql.PrimaryKeyInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverPropertyCollection;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.TaskThreadPool;
import net.sourceforge.squirrel_sql.plugins.dbcopy.prefs.DBCopyPreferenceBean;

import org.easymock.classextension.EasyMock;

/**
 * A utility class for building test objects.
 * 
 * @author manningr
 */
public class TestUtil {

    public static ISession getEasyMockSession(String dbName, boolean replay)
            throws SQLException {
        ISQLDatabaseMetaData md = getEasyMockSQLMetaData(dbName, "jdbc:oracle");
        ISession session = getEasyMockSession(md, replay);
        return session;
    }

    /**
     * Calls replay by default.
     * 
     * @param dbName
     * @return
     * @throws SQLException
     */
    public static ISession getEasyMockSession(String dbName)
            throws SQLException {
        return getEasyMockSession(dbName, true);
    }

    public static ISession getEasyMockSession(ISQLDatabaseMetaData md,
            boolean replay) {
        ISession session = createMock(ISession.class);
        IQueryTokenizer tokenizer = getEasyMockQueryTokenizer();
        // IMessageHandler messageHandler = getEasyMockMessageHandler();

        expect(session.getMetaData()).andReturn(md).anyTimes();
        expect(session.getApplication()).andReturn(getEasyMockApplication())
                .anyTimes();
        expect(session.getQueryTokenizer()).andReturn(tokenizer).anyTimes();
        // expect(session.getMessageHandler()).andReturn(messageHandler).anyTimes();
        if (replay) {
            replay(session);
        }
        return session;
    }

    public static IMessageHandler getEasyMockMessageHandler() {
        IMessageHandler result = createMock(IMessageHandler.class);
        result.showErrorMessage(isA(Throwable.class), null);
        result.showErrorMessage(isA(String.class));
        result.showMessage(isA(String.class));
        result.showMessage(isA(Throwable.class), null);
        result.showWarningMessage(isA(String.class));
        replay(result);
        return result;
    }

    public static IQueryTokenizer getEasyMockQueryTokenizer() {
        return getEasyMockQueryTokenizer(";", "--", true, 5);        
    }

    public static IQueryTokenizer getEasyMockQueryTokenizer(String sep,
                                                            String solComment,
                                                            boolean removeMultiLineComment,
                                                            int queryCount) {
        IQueryTokenizer tokenizer = createMock(IQueryTokenizer.class);
        expect(tokenizer.getSQLStatementSeparator()).andReturn(sep).anyTimes();
        expect(tokenizer.getLineCommentBegin()).andReturn(solComment).anyTimes();
        expect(tokenizer.isRemoveMultiLineComment()).andReturn(removeMultiLineComment).anyTimes();
        expect(tokenizer.getQueryCount()).andReturn(queryCount).anyTimes();
        replay(tokenizer);
        return tokenizer;
    }
    
    /**
     * Calls replay by default.
     * 
     * @param md
     * @return
     */
    public static ISession getEasyMockSession(ISQLDatabaseMetaData md) {
        return getEasyMockSession(md, true);
    }

    public static ISession getEasyMockSession(ISQLDatabaseMetaData md,
            ResultSet rs) throws SQLException {
        ISQLConnection con = getEasyMockSQLConnection(rs);
        ISession session = getEasyMockSession(md, false);
        expect(session.getSQLConnection()).andReturn(con).anyTimes();
        replay(session);
        return session;
    }

    public static SQLConnection getEasyMockSQLConnection() throws SQLException {
        SQLConnection result = createMock(SQLConnection.class);
        result.addPropertyChangeListener(EasyMock
                .isA(PropertyChangeListener.class));
        expect(result.getCatalog()).andReturn("TestCatalog").anyTimes();
        return result;
    }

    public static ISQLConnection getEasyMockSQLConnection(ResultSet rs)
            throws SQLException {
        if (rs == null) {
            throw new IllegalArgumentException("rs cannot be null");
        }
        Statement stmt = createNiceMock(Statement.class);
        expect(stmt.executeQuery(startsWith("select"))).andReturn(rs)
                .anyTimes();
        replay(stmt);

        Connection con = createNiceMock(Connection.class);
        expect(con.createStatement()).andReturn(stmt);
        expect(
                con.createStatement(ResultSet.TYPE_FORWARD_ONLY,
                        ResultSet.CONCUR_READ_ONLY)).andReturn(stmt);
        replay(con);

        ISQLConnection sqlCon = createNiceMock(ISQLConnection.class);
        expect(sqlCon.getConnection()).andReturn(con);
        replay(sqlCon);

        return sqlCon;
    }

    public static ISQLDatabaseMetaData getEasyMockH2SQLMetaData()
            throws SQLException {
        ISQLDatabaseMetaData md = createMock(ISQLDatabaseMetaData.class);
        expect(md.getDatabaseProductName()).andReturn("H2").anyTimes();
        expect(md.getDatabaseProductVersion()).andReturn("1.0 (2007-04-29)")
                .anyTimes();
        expect(md.supportsSchemasInDataManipulation()).andReturn(true)
                .anyTimes();
        expect(md.supportsCatalogsInDataManipulation()).andReturn(false)
                .anyTimes();
        expect(md.getCatalogSeparator()).andReturn(".").anyTimes();
        expect(md.getIdentifierQuoteString()).andReturn("\"").anyTimes();
        expect(md.getURL()).andReturn(
                "jdbc:h2:tcp://localhost:9094/testDatabase").anyTimes();
        replay(md);
        return md;
    }

    public static ISQLDatabaseMetaData getEasyMockSQLMetaData(String dbName,
                                                              String dbURL,
                                                              DatabaseMetaData md) 
        throws SQLException
    {
        ISQLDatabaseMetaData result = getEasyMockSQLMetaData(dbName, dbURL, false, false);
        expect(result.getJDBCMetaData()).andReturn(md);
        replay(result);
        return result;
    }
    
    
    public static ISQLDatabaseMetaData getEasyMockSQLMetaData(String dbName,
                                                              String dbURL, 
                                                              boolean nice, 
                                                              boolean replay) 
        throws SQLException 
    {
        ISQLDatabaseMetaData md = null;
        if (nice) {
            md = createNiceMock(ISQLDatabaseMetaData.class);
        } else {
            md = createMock(ISQLDatabaseMetaData.class);
        }

        expect(md.getDatabaseProductName()).andReturn(dbName).anyTimes();
        expect(md.getDatabaseProductVersion()).andReturn("1.0").anyTimes();
        expect(md.supportsSchemasInDataManipulation()).andReturn(true)
                .anyTimes();
        expect(md.supportsCatalogsInDataManipulation()).andReturn(true)
                .anyTimes();
        expect(md.getCatalogSeparator()).andReturn("").anyTimes();
        expect(md.getIdentifierQuoteString()).andReturn("\"").anyTimes();
        expect(md.getURL()).andReturn(dbURL).anyTimes();
        DatabaseMetaData dbmd = createMock(DatabaseMetaData.class);
        expect(md.getJDBCMetaData()).andReturn(dbmd).anyTimes();
        if (replay) {
            replay(md);
        }
        return md;
    }

    /**
     * Calls replay by default. Nice by default.
     * 
     * @param dbName
     * @param dbURL
     * @return
     * @throws SQLException
     */
    public static ISQLDatabaseMetaData getEasyMockSQLMetaData(String dbName,
            String dbURL) throws SQLException {
        return getEasyMockSQLMetaData(dbName, dbURL, true, true);
    }

    /**
     * Calls replay by default.
     * 
     * @param dbName
     * @param dbURL
     * @param nice
     * @return
     * @throws SQLException
     */
    public static ISQLDatabaseMetaData getEasyMockSQLMetaData(String dbName,
            String dbURL, boolean nice) throws SQLException {
        return getEasyMockSQLMetaData(dbName, dbURL, nice, true);
    }

    public static IApplication getEasyMockApplication(boolean nice) {
        IApplication result = null;
        if (nice) {
            result = createNiceMock(IApplication.class);
        } else {
            result = createMock(IApplication.class);
        }
        SquirrelResources resoures = getEasyMockSquirrelResources();
        SessionProperties props = getEasyMockSessionProperties(";", "--", true);
        SquirrelPreferences prefs = getEasyMockSquirrelPreferences(props);
        expect(result.getMainFrame()).andReturn(null).anyTimes();
        expect(result.getResources()).andReturn(resoures).anyTimes();
        expect(result.getSquirrelPreferences()).andReturn(prefs).anyTimes();
        replay(result);
        return result;
    }

    public static IApplication getEasyMockApplication() {
        return getEasyMockApplication(true);
    }

    public static SquirrelResources getEasyMockSquirrelResources() {
        SquirrelResources resources = EasyMock
                .createMock(SquirrelResources.class);
        resources.setupAction(isA(Action.class), EasyMock.anyBoolean());
        EasyMock.expectLastCall().times(1, 10000);
        replay(resources);
        return resources;
    }

    public static SessionProperties getEasyMockSessionProperties(String sep,
            String solComment, boolean removeMultLineComments) {
        SessionProperties result = createMock(SessionProperties.class);
        expect(result.getSQLStatementSeparator()).andReturn(sep).anyTimes();
        expect(result.getStartOfLineComment()).andReturn(solComment).anyTimes();
        expect(result.getRemoveMultiLineComment()).andReturn(
                removeMultLineComments).anyTimes();
        expect(result.clone()).andReturn(result).anyTimes();
        replay(result);
        return result;
    }

    public static TaskThreadPool getThreadPool() {
        TaskThreadPool result = createMock(TaskThreadPool.class);
        result.addTask(isA(Runnable.class));
        replay(result);
        return result;
    }

    public static SquirrelPreferences getEasyMockSquirrelPreferences(
            SessionProperties props) 
    {
        SquirrelPreferences prefs = createMock(SquirrelPreferences.class);
        expect(prefs.getShowColoriconsInToolbar()).andReturn(true).anyTimes();
        expect(prefs.getSessionProperties()).andReturn(props).anyTimes();
        expect(prefs.getWarnJreJdbcMismatch()).andReturn(false).anyTimes();
        replay(prefs);
        return prefs;
    }

    public static ForeignKeyInfo[] getEasyMockForeignKeyInfos(String fkName,
            String ctab, String ccol, String ptab, String pcol) {
        ForeignKeyInfo result = createMock(ForeignKeyInfo.class);
        expect(result.getSimpleName()).andReturn(fkName).anyTimes();
        expect(result.getForeignKeyColumnName()).andReturn(ccol).anyTimes();
        expect(result.getPrimaryKeyColumnName()).andReturn(pcol).anyTimes();
        expect(result.getForeignKeyTableName()).andReturn(ctab).anyTimes();
        expect(result.getPrimaryKeyTableName()).andReturn(ptab).anyTimes();
        expect(result.getDeleteRule()).andReturn(
                DatabaseMetaData.importedKeyCascade).anyTimes();
        expect(result.getUpdateRule()).andReturn(
                DatabaseMetaData.importedKeyCascade).anyTimes();
        replay(result);
        return new ForeignKeyInfo[] { result };
    }

    public static List<IndexInfo> getEasyMockIndexInfos(String tableName,
            String columnName) {
        IndexInfo result = createMock(IndexInfo.class);
        expect(result.getColumnName()).andReturn(columnName).anyTimes();
        expect(result.getSimpleName()).andReturn("TestIndex").anyTimes();
        expect(result.getOrdinalPosition()).andReturn((short) 1).anyTimes();
        expect(result.getTableName()).andReturn(tableName).anyTimes();
        expect(result.isNonUnique()).andReturn(false).anyTimes();
        replay(result);
        return Arrays.asList(new IndexInfo[] { result });
    }

    public static PrimaryKeyInfo getEasyMockPrimaryKeyInfo(String catalog,
            String schemaName, String tableName, String columnName,
            short keySequence, String pkName, boolean replay) {
        PrimaryKeyInfo pki = createMock(PrimaryKeyInfo.class);
        expect(pki.getCatalogName()).andReturn(catalog).anyTimes();
        expect(pki.getColumnName()).andReturn(columnName).anyTimes();
        expect(pki.getDatabaseObjectType()).andReturn(
                DatabaseObjectType.PRIMARY_KEY).anyTimes();
        expect(pki.getKeySequence()).andReturn(keySequence).anyTimes();
        expect(pki.getQualifiedColumnName()).andReturn(columnName).anyTimes();
        expect(pki.getQualifiedName()).andReturn(pkName).anyTimes();
        expect(pki.getSchemaName()).andReturn(schemaName).anyTimes();
        expect(pki.getSimpleName()).andReturn(pkName).anyTimes();
        expect(pki.getTableName()).andReturn(tableName).anyTimes();
        if (replay) {
            replay(pki);
        }
        return pki;
    }

    /**
     * Calls replay by default.
     * 
     * @param catalog
     * @param schemaName
     * @param tableName
     * @param columnName
     * @param keySequence
     * @param pkName
     * @return
     */
    public static PrimaryKeyInfo getEasyMockPrimaryKeyInfo(String catalog,
            String schemaName, String tableName, String columnName,
            short keySequence, String pkName) {
        return getEasyMockPrimaryKeyInfo(catalog, schemaName, tableName,
                columnName, keySequence, pkName, true);
    }

    public static TableColumnInfo getEasyMockTableColumn(String catalogName,
            String schemaName, String tableName, String columnName, int dataType) {
        String[] columnNames = new String[] { columnName };
        Integer[] dataTypes = new Integer[] { dataType };
        TableColumnInfo[] result = getEasyMockTableColumns(catalogName,
                schemaName, tableName, asList(columnNames), asList(dataTypes));
        return result[0];
    }

    public static TableColumnInfo[] getEasyMockTableColumns(String catalogName,
            String schemaName, String tableName, List<String> columnNames,
            List<Integer> dataTypes) {
        if (columnNames.size() != dataTypes.size()) {
            throw new IllegalArgumentException(
                    "columnNames.size() != dataTypes.size()");
        }
        ArrayList<TableColumnInfo> result = new ArrayList<TableColumnInfo>();

        int index = 0;
        for (String columnName : columnNames) {
            Integer columnDataType = dataTypes.get(index++);

            TableColumnInfo info = getEasyMockTableColumnInfo(catalogName,
                    schemaName, tableName, columnName, columnDataType, 10,
                    "defval", "remark", 10, 10, 10, true);

            result.add(info);
        }

        return result.toArray(new TableColumnInfo[0]);
    }

    public static TableColumnInfo getEasyMockTableColumnInfo(
            String catalogName, String schemaName, String tableName,
            String columnName, int dataType, int columnSize,
            String defaultValue, String remarks, int decimalDigits,
            int octetLength, int radix, boolean nullable) {
        TableColumnInfo info = createMock(TableColumnInfo.class);
        expect(info.getCatalogName()).andReturn(catalogName).anyTimes();
        expect(info.getSchemaName()).andReturn(schemaName).anyTimes();
        expect(info.getTableName()).andReturn(tableName).anyTimes();
        expect(info.getColumnName()).andReturn(columnName).anyTimes();
        expect(info.getDataType()).andReturn(dataType).anyTimes();
        expect(info.getTypeName()).andReturn(
                JDBCTypeMapper.getJdbcTypeName(dataType)).anyTimes();
        expect(info.getColumnSize()).andReturn(columnSize).anyTimes();
        expect(info.getDatabaseObjectType()).andReturn(
                DatabaseObjectType.COLUMN).anyTimes();
        expect(info.getDefaultValue()).andReturn(defaultValue).anyTimes();
        expect(info.getRemarks()).andReturn(remarks).anyTimes();
        expect(info.getDecimalDigits()).andReturn(decimalDigits).anyTimes();
        expect(info.getOctetLength()).andReturn(octetLength).anyTimes();
        expect(info.getQualifiedName()).andReturn(
                schemaName + "." + tableName + "." + columnName).anyTimes();
        expect(info.getRadix()).andReturn(radix).anyTimes();
        if (nullable) {
            expect(info.isNullable()).andReturn("YES").anyTimes();
            expect(info.isNullAllowed()).andReturn(1).anyTimes();
        } else {
            expect(info.isNullable()).andReturn("NO").anyTimes();
            expect(info.isNullAllowed()).andReturn(0).anyTimes();
        }
        replay(info);
        return info;
    }

    /**
     * Returns a new TableColumnInfo EasyMock based on values from the one
     * specified, only the column size is the one specified.
     * 
     * @param info
     *            the existing TableColumnInfo to replicate
     * @param newSize
     *            the new column size
     * @return
     */
    public static TableColumnInfo setEasyMockTableColumnInfoSize(
            final TableColumnInfo info, final int newSize) {
        TableColumnInfo result = getEasyMockTableColumnInfo(info
                .getCatalogName(), info.getSchemaName(), info.getTableName(),
                info.getColumnName(), info.getDataType(), newSize, info
                        .getDefaultValue(), info.getRemarks(), info
                        .getDecimalDigits(), info.getOctetLength(), info
                        .getRadix(), info.isNullAllowed() == 1 ? true : false);
        return result;

    }

    /**
     * Returns a new TableColumnInfo EasyMock based on values from the one
     * specified, only the column size is the one specified.
     * 
     * @param info
     *            the existing TableColumnInfo to replicate
     * @param newSize
     *            the new column size
     * @return
     */
    public static TableColumnInfo setEasyMockTableColumnInfoNullable(
            final TableColumnInfo info, final boolean nullable) {
        TableColumnInfo result = getEasyMockTableColumnInfo(info
                .getCatalogName(), info.getSchemaName(), info.getTableName(),
                info.getColumnName(), info.getDataType(), info.getColumnSize(),
                info.getDefaultValue(), info.getRemarks(), info
                        .getDecimalDigits(), info.getOctetLength(), info
                        .getRadix(), nullable);
        return result;

    }

    /**
     * Returns a new TableColumnInfo EasyMock based on values from the one
     * specified, only the column data type is the one specified.
     * 
     * @param info
     *            the existing TableColumnInfo to replicate
     * @param dataTyoe
     *            the new column data type
     * @return
     */
    public static TableColumnInfo setEasyMockTableColumnInfoType(
            final TableColumnInfo info, final int dataType) {
        TableColumnInfo result = getEasyMockTableColumnInfo(info
                .getCatalogName(), info.getSchemaName(), info.getTableName(),
                info.getColumnName(), dataType, info.getColumnSize(), info
                        .getDefaultValue(), info.getRemarks(), info
                        .getDecimalDigits(), info.getOctetLength(), info
                        .getRadix(), info.isNullAllowed() == 1 ? true : false);
        return result;

    }

    public static TableColumnInfo getBigintColumnInfo(ISQLDatabaseMetaData md,
            boolean nullable) {
        return getTableColumnInfo(md, BIGINT, 20, 10, nullable);
    }

    public static TableColumnInfo getBinaryColumnInfo(ISQLDatabaseMetaData md,
            boolean nullable) {
        return getTableColumnInfo(md, BINARY, -1, 0, nullable);
    }

    public static TableColumnInfo getBlobColumnInfo(ISQLDatabaseMetaData md,
            boolean nullable) {
        return getTableColumnInfo(md, BLOB, Integer.MAX_VALUE, 0, nullable);
    }

    public static TableColumnInfo getClobColumnInfo(ISQLDatabaseMetaData md,
            boolean nullable) {
        return getTableColumnInfo(md, CLOB, Integer.MAX_VALUE, 0, nullable);
    }

    public static TableColumnInfo getIntegerColumnInfo(ISQLDatabaseMetaData md,
            boolean nullable) {
        return getTableColumnInfo(md, INTEGER, 10, 0, nullable);
    }

    public static TableColumnInfo getDateColumnInfo(ISQLDatabaseMetaData md,
            boolean nullable) {
        return getTableColumnInfo(md, DATE, 0, 0, nullable);
    }

    public static TableColumnInfo getLongVarcharColumnInfo(
            ISQLDatabaseMetaData md, boolean nullable, int length) {
        return getTableColumnInfo(md, LONGVARCHAR, length, 0, nullable);
    }

    public static TableColumnInfo getVarcharColumnInfo(ISQLDatabaseMetaData md,
            boolean nullable, int length) {
        return getTableColumnInfo(md, VARCHAR, length, 0, nullable);
    }

    public static TableColumnInfo getTableColumnInfo(ISQLDatabaseMetaData md,
            int type, int columnSize, int decimalDigits, boolean nullable) {
        return getTableColumnInfo(md, "TestColumn", type, columnSize,
                decimalDigits, nullable);
    }

    public static TableColumnInfo getTableColumnInfo(ISQLDatabaseMetaData md,
            String columnName, int type, int columnSize, int decimalDigits,
            boolean nullable) {
        int isNullableInt = 0;
        String isNullableStr = "no";

        if (nullable) {
            isNullableInt = 1;
            isNullableStr = "yes";
        }
        TableColumnInfo info = new TableColumnInfo("TestCatalog", "TestSchema",
                "TestTable", columnName, type, JDBCTypeMapper
                        .getJdbcTypeName(type), // typeName
                columnSize, // columnSize
                decimalDigits, // decimalDigits
                0, // radix
                isNullableInt, // isNullAllowable
                "TestRemark", "0", // defaultValue
                0, // octetLength
                0, // ordinalPosition
                isNullableStr, // isNullable
                md);
        return info;
    }

    public static String findAncestorSquirrelSqlDistDirBase(String dirToFind) {
        File f = new File("../" + dirToFind);
        if (f.exists())
            return "../";
        f = new File("../../" + dirToFind);
        if (f.exists())
            return "../../";
        f = new File("../../../" + dirToFind);
        if (f.exists())
            return "../../../";
        f = new File("../../../../" + dirToFind);
        if (f.exists())
            return "../../../../";
        f = new File("../../../../../" + dirToFind);
        if (f.exists())
            return "../../../../../";
        return null;
    }

    public static SQLAlias getEasyMockSQLAlias(IIdentifier SqlAliasId,
            IIdentifier SqlDriverId) {
        SQLAlias mockSqlAlias = createMock(SQLAlias.class);
        SQLDriverPropertyCollection mockSqlDriverPropCol = createMock(SQLDriverPropertyCollection.class);
        expect(mockSqlAlias.getIdentifier()).andReturn(SqlAliasId).anyTimes();
        expect(mockSqlAlias.getName()).andReturn("TestAliasName").anyTimes();
        expect(mockSqlAlias.getDriverIdentifier()).andReturn(SqlDriverId)
                .anyTimes();
        expect(mockSqlAlias.getUrl()).andReturn("TestUrl").anyTimes();
        expect(mockSqlAlias.getUserName()).andReturn("TestUserName").anyTimes();
        expect(mockSqlAlias.getPassword()).andReturn("TestPassword").anyTimes();
        expect(mockSqlAlias.isAutoLogon()).andReturn(true).anyTimes();
        expect(mockSqlAlias.getUseDriverProperties()).andReturn(true)
                .anyTimes();
        expect(mockSqlAlias.getDriverPropertiesClone()).andReturn(
                mockSqlDriverPropCol).anyTimes();
        replay(mockSqlAlias);
        return mockSqlAlias;
    }
    
    public static IDatabaseObjectInfo getEasyMockDatabaseObjectInfo(String catalog, 
                                                                    String schema, 
                                                                    String simpleName, 
                                                                    String qualName)
    {
        IDatabaseObjectInfo result = EasyMock.createMock(IDatabaseObjectInfo.class);
        expect(result.getCatalogName()).andReturn(catalog).anyTimes();
        expect(result.getSchemaName()).andReturn(schema).anyTimes();
        expect(result.getSimpleName()).andReturn(simpleName).anyTimes();
        expect(result.getQualifiedName()).andReturn(qualName).anyTimes();
        replay(result);
        return result;
    }
    
    public static DBCopyPreferenceBean getEasyMockDBCopyPreferenceBean() {
        DBCopyPreferenceBean result = EasyMock.createMock(DBCopyPreferenceBean.class);
        expect(result.isCopyForeignKeys()).andReturn(true);
        expect(result.isCopyPrimaryKeys()).andReturn(true);
        expect(result.isPruneDuplicateIndexDefs()).andReturn(true);
        expect(result.isPromptForDialect()).andReturn(false);
        replay(result);
        return result;
    }
    
    public static ITableInfo getEasyMockTableInfo(String catalog, 
                                                  String schema, 
                                                  String simpleName, 
                                                  String qualName) 
    {
        ITableInfo result = EasyMock.createMock(ITableInfo.class);
        expect(result.getCatalogName()).andReturn(catalog).anyTimes();
        expect(result.getSchemaName()).andReturn(schema).anyTimes();
        expect(result.getSimpleName()).andReturn(simpleName).anyTimes();
        expect(result.getQualifiedName()).andReturn(qualName).anyTimes();
        replay(result);        
        return result;
    }
}
