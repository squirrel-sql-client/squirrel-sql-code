package net.sourceforge.squirrel_sql.fw;

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
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.startsWith;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.createNiceMock;
import static org.easymock.classextension.EasyMock.replay;

import java.beans.PropertyChangeListener;
import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.ForeignKeyColumnInfo;
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
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverManager;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.sql.TokenizerSessPropsInteractions;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.TaskThreadPool;

import org.easymock.classextension.EasyMock;

/**
 * This is intended to provide helper methods to build EasyMock mocks for classes and interfaces located
 * in the Fw module.  App mocks should be located in the corresponding AppTestUtil class, where as plugins 
 * mocks should be relocated to individual plugin TestUtil helpers.
 */
public class FwTestUtil {

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
         String solComment, boolean removeMultiLineComment, int queryCount) {
      IQueryTokenizer tokenizer = createMock(IQueryTokenizer.class);
      expect(tokenizer.getSQLStatementSeparator()).andReturn(sep).anyTimes();
      expect(tokenizer.getLineCommentBegin()).andReturn(solComment).anyTimes();
      expect(tokenizer.isRemoveMultiLineComment()).andReturn(removeMultiLineComment)
                                                  .anyTimes();
      expect(tokenizer.getQueryCount()).andReturn(queryCount).anyTimes();
      
      TokenizerSessPropsInteractions tspi = createMock(TokenizerSessPropsInteractions.class);
      expect(tspi.isTokenizerDefinesRemoveMultiLineComment()).andStubReturn(true);
      expect(tspi.isTokenizerDefinesStartOfLineComment()).andStubReturn(true);
      expect(tspi.isTokenizerDefinesStatementSeparator()).andStubReturn(true);
      
      expect(tokenizer.getTokenizerSessPropsInteractions()).andStubReturn(tspi);
      replay(tspi);
      replay(tokenizer);
      return tokenizer;
   }

   public static SQLConnection getEasyMockSQLConnection() throws SQLException {
      SQLConnection result = createMock(SQLConnection.class);
      result.addPropertyChangeListener(EasyMock.isA(PropertyChangeListener.class));
      expect(result.getCatalog()).andReturn("TestCatalog").anyTimes();
      return result;
   }

   public static ISQLConnection getEasyMockSQLConnection(ResultSet rs)
         throws SQLException {
      if (rs == null) {
         throw new IllegalArgumentException("rs cannot be null");
      }
      Statement stmt = createNiceMock(Statement.class);
      expect(stmt.executeQuery(startsWith("select"))).andReturn(rs).anyTimes();
      replay(stmt);

      Connection con = createNiceMock(Connection.class);
      expect(con.createStatement()).andReturn(stmt);
      expect(con.createStatement(ResultSet.TYPE_FORWARD_ONLY,
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
      expect(md.supportsSchemasInDataManipulation()).andReturn(true).anyTimes();
      expect(md.supportsSchemasInTableDefinitions()).andStubReturn(true);
      expect(md.supportsCatalogsInDataManipulation()).andReturn(false)
                                                     .anyTimes();
      expect(md.getCatalogSeparator()).andReturn(".").anyTimes();
      expect(md.getIdentifierQuoteString()).andReturn("\"").anyTimes();
      expect(md.getURL()).andReturn("jdbc:h2:tcp://localhost:9094/testDatabase")
                         .anyTimes();
      replay(md);
      return md;
   }

   public static ISQLDatabaseMetaData getEasyMockSybase15SQLMetaData()
         throws SQLException 
   {
      ISQLDatabaseMetaData md = getSybaseSQLMetaData();
      String version = 
         "Adaptive Server Enterprise/15.0/EBF 13194 EC " +
         "ESD/P/Linux Intel/Linux 2.4.21-20.ELsmp " +
         "i686/ase150/2179/32-bit/FBO/Mon Feb  6 04:14:19 2006";
      expect(md.getDatabaseProductVersion()).andReturn(version).anyTimes();
      expect(md.supportsSchemasInTableDefinitions()).andStubReturn(true);
      replay(md);
      return md;
   }

   public static ISQLDatabaseMetaData getEasyMockSybase12SQLMetaData()
         throws SQLException 
   {
      ISQLDatabaseMetaData md = getSybaseSQLMetaData();
      String version = 
         "Adaptive Server Enterprise/12.5.4/EBF 13194 " +
         "EC ESD/P/Linux Intel/Linux 2.4.21-20.ELsmp i686/" +
         "ase120/2179/32-bit/FBO/Mon Feb  6 04:14:19 2006";
      expect(md.getDatabaseProductVersion()).andReturn(version).anyTimes();
      expect(md.supportsSchemasInTableDefinitions()).andStubReturn(true);
      replay(md);
      return md;
   }

   private static ISQLDatabaseMetaData getSybaseSQLMetaData() throws SQLException {
      ISQLDatabaseMetaData md = createMock(ISQLDatabaseMetaData.class);
      expect(md.getDatabaseProductName()).andReturn("Adaptive Server Enterprise")
                                         .anyTimes();
      expect(md.supportsSchemasInDataManipulation()).andReturn(true).anyTimes();
      expect(md.supportsCatalogsInDataManipulation()).andReturn(true)
                                                     .anyTimes();
      expect(md.getCatalogSeparator()).andReturn(".").anyTimes();
      expect(md.getIdentifierQuoteString()).andReturn("\"").anyTimes();
      expect(md.getURL()).andReturn("jdbc:sybase:Tds:192.168.1.135:4115/dbcopydest")
                         .anyTimes();
      return md;      
   }
   
   public static ISQLDatabaseMetaData getEasyMockSQLMetaData(String dbName,
         String dbURL, DatabaseMetaData md) throws SQLException {
      ISQLDatabaseMetaData result = getEasyMockSQLMetaData(dbName,
                                                           dbURL,
                                                           false,
                                                           false);
      expect(result.getJDBCMetaData()).andReturn(md);
      replay(result);
      return result;
   }

   public static ISQLDatabaseMetaData getEasyMockSQLMetaData(String dbName,
         String dbURL, boolean nice, boolean replay) throws SQLException {
      ISQLDatabaseMetaData md = null;
      if (nice) {
         md = createNiceMock(ISQLDatabaseMetaData.class);
      } else {
         md = createMock(ISQLDatabaseMetaData.class);
      }

      expect(md.getDatabaseProductName()).andReturn(dbName).anyTimes();
      expect(md.getDatabaseProductVersion()).andReturn("1.0").anyTimes();
      expect(md.supportsSchemasInDataManipulation()).andReturn(true).anyTimes();
      expect(md.supportsCatalogsInDataManipulation()).andReturn(true)
                                                     .anyTimes();
      expect(md.supportsSchemasInTableDefinitions()).andStubReturn(true);
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

   public static SQLDriverManager getEasyMockSQLDriverManager() {
      SQLDriverManager result = createMock(SQLDriverManager.class);
      Driver mockDriver = createMock(Driver.class);
      replay(mockDriver);
      expect(result.getJDBCDriver(isA(IIdentifier.class))).andReturn(mockDriver)
                                                          .anyTimes();
      replay(result);
      return result;
   }

   public static TaskThreadPool getEasyMockTaskThreadPool() {
      TaskThreadPool result = createMock(TaskThreadPool.class);
      result.addTask(isA(Runnable.class));
      expectLastCall().anyTimes();
      replay(result);
      return result;
   }

   public static IIdentifier getEasyMockIdentifier() {
      IIdentifier result = createMock(IIdentifier.class);
      replay(result);
      return result;
   }

   public static TaskThreadPool getThreadPool() {
      TaskThreadPool result = createMock(TaskThreadPool.class);
      result.addTask(isA(Runnable.class));
      replay(result);
      return result;
   }

   public static ForeignKeyInfo[] getEasyMockForeignKeyInfos(String fkName,
         String ctab, String ccol, String ptab, String pcol) {
      ForeignKeyInfo result = createMock(ForeignKeyInfo.class);
      expect(result.getSimpleName()).andReturn(fkName).anyTimes();
      expect(result.getForeignKeyColumnName()).andReturn(ccol).anyTimes();
      expect(result.getPrimaryKeyColumnName()).andReturn(pcol).anyTimes();
      expect(result.getForeignKeyTableName()).andReturn(ctab).anyTimes();
      expect(result.getPrimaryKeyTableName()).andReturn(ptab).anyTimes();
      expect(result.getDeleteRule()).andReturn(DatabaseMetaData.importedKeyCascade)
                                    .anyTimes();
      expect(result.getUpdateRule()).andReturn(DatabaseMetaData.importedKeyCascade)
                                    .anyTimes();
      expect(result.getForeignKeySchemaName()).andStubReturn("TestSchema");
      expect(result.getPrimaryKeySchemaName()).andStubReturn("TestSchema");
      
      ForeignKeyColumnInfo mockForeignKeyColumnInfo = createMock("mockForeignKeyColumnInfo", ForeignKeyColumnInfo.class);
      expect(mockForeignKeyColumnInfo.getForeignKeyColumnName()).andStubReturn(ccol);
      expect(mockForeignKeyColumnInfo.getPrimaryKeyColumnName()).andStubReturn(pcol);
      expect(mockForeignKeyColumnInfo.getKeySequence()).andStubReturn(0);
      
      
      expect(result.getForeignKeyColumnInfo()).andStubReturn(new ForeignKeyColumnInfo[] { mockForeignKeyColumnInfo });
      
      replay(mockForeignKeyColumnInfo);
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
      expect(result.getSchemaName()).andStubReturn("TestSchema");
      replay(result);
      return Arrays.asList(new IndexInfo[] { result });
   }

   public static PrimaryKeyInfo getEasyMockPrimaryKeyInfo(String catalog,
         String schemaName, String tableName, String columnName,
         short keySequence, String pkName, boolean replay) {
      PrimaryKeyInfo pki = createMock(PrimaryKeyInfo.class);
      expect(pki.getCatalogName()).andReturn(catalog).anyTimes();
      expect(pki.getColumnName()).andReturn(columnName).anyTimes();
      expect(pki.getDatabaseObjectType()).andReturn(DatabaseObjectType.PRIMARY_KEY)
                                         .anyTimes();
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
      return getEasyMockPrimaryKeyInfo(catalog,
                                       schemaName,
                                       tableName,
                                       columnName,
                                       keySequence,
                                       pkName,
                                       true);
   }

   public static TableColumnInfo getEasyMockTableColumn(String catalogName,
         String schemaName, String tableName, String columnName, int dataType) {
      String[] columnNames = new String[] { columnName };
      Integer[] dataTypes = new Integer[] { dataType };
      TableColumnInfo[] result = getEasyMockTableColumns(catalogName,
                                                         schemaName,
                                                         tableName,
                                                         asList(columnNames),
                                                         asList(dataTypes));
      return result[0];
   }

   public static TableColumnInfo[] getEasyMockTableColumns(String catalogName,
         String schemaName, String tableName, List<String> columnNames,
         List<Integer> dataTypes) {
      if (columnNames.size() != dataTypes.size()) {
         throw new IllegalArgumentException("columnNames.size() != dataTypes.size()");
      }
      ArrayList<TableColumnInfo> result = new ArrayList<TableColumnInfo>();

      int index = 0;
      for (String columnName : columnNames) {
         Integer columnDataType = dataTypes.get(index++);

         TableColumnInfo info = getEasyMockTableColumnInfo(catalogName,
                                                           schemaName,
                                                           tableName,
                                                           columnName,
                                                           columnDataType,
                                                           10,
                                                           "defval",
                                                           "remark",
                                                           10,
                                                           10,
                                                           10,
                                                           true);

         result.add(info);
      }

      return result.toArray(new TableColumnInfo[0]);
   }

   public static TableColumnInfo getEasyMockTableColumnInfo(String catalogName,
         String schemaName, String tableName, String columnName, int dataType,
         int columnSize, String defaultValue, String remarks,
         int decimalDigits, int octetLength, int radix, boolean nullable) {
      TableColumnInfo info = createMock(TableColumnInfo.class);
      expect(info.getCatalogName()).andReturn(catalogName).anyTimes();
      expect(info.getSchemaName()).andReturn(schemaName).anyTimes();
      expect(info.getTableName()).andReturn(tableName).anyTimes();
      expect(info.getColumnName()).andReturn(columnName).anyTimes();
      expect(info.getDataType()).andReturn(dataType).anyTimes();
      expect(info.getTypeName()).andReturn(JDBCTypeMapper.getJdbcTypeName(dataType))
                                .anyTimes();
      expect(info.getColumnSize()).andReturn(columnSize).anyTimes();
      expect(info.getDatabaseObjectType()).andReturn(DatabaseObjectType.COLUMN)
                                          .anyTimes();
      expect(info.getDefaultValue()).andReturn(defaultValue).anyTimes();
      expect(info.getRemarks()).andReturn(remarks).anyTimes();
      expect(info.getDecimalDigits()).andReturn(decimalDigits).anyTimes();
      expect(info.getOctetLength()).andReturn(octetLength).anyTimes();
      expect(info.getQualifiedName()).andReturn(schemaName + "." + tableName
            + "." + columnName).anyTimes();
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
    *           the existing TableColumnInfo to replicate
    * @param newSize
    *           the new column size
    * @return
    */
   public static TableColumnInfo setEasyMockTableColumnInfoSize(
         final TableColumnInfo info, final int newSize) {
      TableColumnInfo result = getEasyMockTableColumnInfo(info.getCatalogName(),
                                                          info.getSchemaName(),
                                                          info.getTableName(),
                                                          info.getColumnName(),
                                                          info.getDataType(),
                                                          newSize,
                                                          info.getDefaultValue(),
                                                          info.getRemarks(),
                                                          info.getDecimalDigits(),
                                                          info.getOctetLength(),
                                                          info.getRadix(),
                                                          info.isNullAllowed() == 1 ? true
                                                                : false);
      return result;

   }

   /**
    * Returns a new TableColumnInfo EasyMock based on values from the one
    * specified, only the column size is the one specified.
    * 
    * @param info
    *           the existing TableColumnInfo to replicate
    * @param newSize
    *           the new column size
    * @return
    */
   public static TableColumnInfo setEasyMockTableColumnInfoNullable(
         final TableColumnInfo info, final boolean nullable) {
      TableColumnInfo result = getEasyMockTableColumnInfo(info.getCatalogName(),
                                                          info.getSchemaName(),
                                                          info.getTableName(),
                                                          info.getColumnName(),
                                                          info.getDataType(),
                                                          info.getColumnSize(),
                                                          info.getDefaultValue(),
                                                          info.getRemarks(),
                                                          info.getDecimalDigits(),
                                                          info.getOctetLength(),
                                                          info.getRadix(),
                                                          nullable);
      return result;

   }

   /**
    * Returns a new TableColumnInfo EasyMock based on values from the one
    * specified, only the column data type is the one specified.
    * 
    * @param info
    *           the existing TableColumnInfo to replicate
    * @param dataTyoe
    *           the new column data type
    * @return
    */
   public static TableColumnInfo setEasyMockTableColumnInfoType(
         final TableColumnInfo info, final int dataType) {
      TableColumnInfo result = getEasyMockTableColumnInfo(info.getCatalogName(),
                                                          info.getSchemaName(),
                                                          info.getTableName(),
                                                          info.getColumnName(),
                                                          dataType,
                                                          info.getColumnSize(),
                                                          info.getDefaultValue(),
                                                          info.getRemarks(),
                                                          info.getDecimalDigits(),
                                                          info.getOctetLength(),
                                                          info.getRadix(),
                                                          info.isNullAllowed() == 1 ? true
                                                                : false);
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
      return getTableColumnInfo(md,
                                "TestColumn",
                                type,
                                columnSize,
                                decimalDigits,
                                nullable);
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
      TableColumnInfo info = new TableColumnInfo("TestCatalog",
                                                 "TestSchema",
                                                 "TestTable",
                                                 columnName,
                                                 type,
                                                 JDBCTypeMapper.getJdbcTypeName(type), // typeName
                                                 columnSize, // columnSize
                                                 decimalDigits, // decimalDigits
                                                 0, // radix
                                                 isNullableInt, // isNullAllowable
                                                 "TestRemark",
                                                 "0", // defaultValue
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

   public static IDatabaseObjectInfo getEasyMockDatabaseObjectInfo(
         String catalog, String schema, String simpleName, String qualName,
         DatabaseObjectType type) {
      IDatabaseObjectInfo result = EasyMock.createMock(IDatabaseObjectInfo.class);
      expect(result.getCatalogName()).andReturn(catalog).anyTimes();
      expect(result.getSchemaName()).andReturn(schema).anyTimes();
      expect(result.getSimpleName()).andReturn(simpleName).anyTimes();
      expect(result.getQualifiedName()).andReturn(qualName).anyTimes();
      expect(result.getDatabaseObjectType()).andReturn(type).anyTimes();
      replay(result);
      return result;
   }

   public static ITableInfo getEasyMockTableInfo(String catalog, String schema,
         String simpleName, String qualName) {
      ITableInfo result = EasyMock.createMock(ITableInfo.class);
      expect(result.getCatalogName()).andReturn(catalog).anyTimes();
      expect(result.getSchemaName()).andReturn(schema).anyTimes();
      expect(result.getSimpleName()).andReturn(simpleName).anyTimes();
      expect(result.getQualifiedName()).andReturn(qualName).anyTimes();
      replay(result);
      return result;
   }
   
   // EasyMock Class extension helpers.  Since classextension and interface 
   // EasyMock methods cannot be used on the same mocks, this provides convenience
   // to not have to specify the package name for classextension mocks.
   
   public static <T> T createClassMock(Class<T> toMock) {
      return org.easymock.classextension.EasyMock.createMock(toMock);
   }
   
   public static void replayClassMock(Object... mocks) {
      org.easymock.classextension.EasyMock.replay(mocks);
   }
   
   public static void verifyClassMock(Object... mocks) {
      org.easymock.classextension.EasyMock.verify(mocks);
   }
   
   public static void resetClassMock(Object... mocks) {
      org.easymock.classextension.EasyMock.reset(mocks);
   }
   
}
