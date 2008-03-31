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

import javax.swing.Action;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.gui.db.ISQLAliasExt;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.gui.session.SessionInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SessionPanel;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.session.IAllowedSchemaChecker;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SessionManager;
import net.sourceforge.squirrel_sql.client.session.action.DeleteSelectedTablesAction;
import net.sourceforge.squirrel_sql.client.session.action.EditWhereColsAction;
import net.sourceforge.squirrel_sql.client.session.action.FilterObjectsAction;
import net.sourceforge.squirrel_sql.client.session.action.RefreshObjectTreeAction;
import net.sourceforge.squirrel_sql.client.session.action.RefreshObjectTreeItemAction;
import net.sourceforge.squirrel_sql.client.session.action.RefreshSchemaInfoAction;
import net.sourceforge.squirrel_sql.client.session.action.SQLFilterAction;
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
import net.sourceforge.squirrel_sql.fw.sql.QueryTokenizer;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverManager;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverPropertyCollection;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.util.ExceptionFormatter;
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
   public static ISession getEasyMockSession(String dbName) throws SQLException {
      return getEasyMockSession(dbName, true);
   }

   public static ISession getEasyMockSession(ISQLDatabaseMetaData md,
         boolean replay) {
      ISession session = null;
      try {
         ISQLConnection con = getEasyMockSQLConnection();
         session = getEasyMockSession(md, con, false);
         if (replay) {
            replay(session);
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
      return session;
   }

   public static ISession getEasyMockSession(ISQLDatabaseMetaData md,
         ISQLConnection con, boolean replay) {
      ISession session = createMock(ISession.class);
      IQueryTokenizer tokenizer = getEasyMockQueryTokenizer();
      // IMessageHandler messageHandler = getEasyMockMessageHandler();

      expect(session.getMetaData()).andReturn(md).anyTimes();
      expect(session.getApplication()).andReturn(getEasyMockApplication())
                                      .anyTimes();
      expect(session.getQueryTokenizer()).andReturn(tokenizer).anyTimes();
      session.setQueryTokenizer(isA(QueryTokenizer.class));
      ISQLPanelAPI api = getEasyMockSqlPanelApi();
      expect(session.getSQLPanelAPIOfActiveSessionWindow()).andReturn(api)
                                                           .anyTimes();
      // expect(session.getMessageHandler()).andReturn(messageHandler).anyTimes();
      expect(session.getAlias()).andReturn(getEasyMockSqlAliasExt());
      expect(session.getIdentifier()).andReturn(getEasyMockIdentifier())
                                     .anyTimes();
      expect(session.getSQLConnection()).andReturn(con).anyTimes();
      session.setExceptionFormatter(isA(ExceptionFormatter.class));
      expectLastCall().anyTimes();
      session.addSeparatorToToolbar();
      expectLastCall().anyTimes();
      SessionPanel panel = getEasyMockSessionPanel();
      expect(session.getSessionSheet()).andReturn(panel).anyTimes();
      session.addToToolbar(isA(Action.class));
      expectLastCall().anyTimes();
      SessionInternalFrame frame = getEasyMockSessionInternalFrame();
      expect(session.getSessionInternalFrame()).andReturn(frame).anyTimes();

      if (replay) {
         replay(session);
      }
      return session;
   }

   public static SessionPanel getEasyMockSessionPanel() {
      SessionPanel result = createMock(SessionPanel.class);
      ISQLPanelAPI api = getEasyMockSqlPanelApi();
      expect(result.getSQLPaneAPI()).andReturn(api);
      replay(result);
      return result;
   }

   public static ISQLPanelAPI getEasyMockSqlPanelApi() {
      ISQLPanelAPI result = createMock(ISQLPanelAPI.class);
      ISQLEntryPanel panel = getEasyMockSqlEntryPanel();
      expect(result.getSQLEntryPanel()).andReturn(panel).anyTimes();
      replay(result);
      return result;
   }

   public static ISQLEntryPanel getEasyMockSqlEntryPanel() {
      ISQLEntryPanel result = createMock(ISQLEntryPanel.class);
      expect(result.getBoundsOfSQLToBeExecuted()).andReturn(new int[] {10, 20}).anyTimes();
      result.setCaretPosition(org.easymock.EasyMock.anyInt());
      expectLastCall().anyTimes();
      replay(result);
      return result;
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
         String solComment, boolean removeMultiLineComment, int queryCount) {
      IQueryTokenizer tokenizer = createMock(IQueryTokenizer.class);
      expect(tokenizer.getSQLStatementSeparator()).andReturn(sep).anyTimes();
      expect(tokenizer.getLineCommentBegin()).andReturn(solComment).anyTimes();
      expect(tokenizer.isRemoveMultiLineComment()).andReturn(removeMultiLineComment)
                                                  .anyTimes();
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
      ISession session = getEasyMockSession(md, con, false);
      replay(session);
      return session;
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

   public static IApplication getEasyMockApplication(boolean nice,
         boolean replay, ActionCollection col) {
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
      TaskThreadPool mockThreadPool = getEasyMockTaskThreadPool();
      expect(result.getThreadPool()).andReturn(mockThreadPool).anyTimes();
      ActionCollection mockActColl = col;
      if (col == null) {
         mockActColl = getEasyMockActionCollection();
      }
      expect(result.getActionCollection()).andReturn(mockActColl).anyTimes();
      SQLDriverManager driverManager = getEasyMockSQLDriverManager();
      expect(result.getSQLDriverManager()).andReturn(driverManager).anyTimes();
      SessionManager mockSessionManager = getEasyMockSessionManager();
      expect(result.getSessionManager()).andReturn(mockSessionManager)
                                        .anyTimes();
      if (replay) {
         replay(result);
      }
      return result;
   }

   public static SessionManager getEasyMockSessionManager() {
      SessionManager result = createMock(SessionManager.class);
      result.addAllowedSchemaChecker(isA(IAllowedSchemaChecker.class));
      expectLastCall().anyTimes();
      replay(result);
      return result;
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

   public static ActionCollection getEasyMockActionCollection() {
      ActionCollection result = createMock(ActionCollection.class);
      result.add(isA(Action.class));
      expectLastCall().anyTimes();
      expectActionCollectionGet("refreshSchema",
                                RefreshSchemaInfoAction.class,
                                result);
      expectActionCollectionGet("refreshObjectTree",
                                RefreshObjectTreeAction.class,
                                result);
      expectActionCollectionGet("refreshObjectItemTree",
                                RefreshObjectTreeItemAction.class,
                                result);
      expectActionCollectionGet("editWhereColsAction",
                                EditWhereColsAction.class,
                                result);
      expectActionCollectionGet("SQLFilterAction",
                                SQLFilterAction.class,
                                result);
      expectActionCollectionGet("DeleteSelectedTablesAction",
                                DeleteSelectedTablesAction.class,
                                result);
      expectActionCollectionGet("FilterObjectsAction",
                                FilterObjectsAction.class,
                                result);
      replay(result);
      return result;
   }

   public static void expectActionCollectionGet(String actionName,
         Class<? extends Action> actionClass, ActionCollection col) {
      SquirrelAction action = getEasyMockSquirrelAction(actionName);
      expect(col.get(actionClass)).andReturn(action).anyTimes();
   }

   public static SquirrelAction getEasyMockSquirrelAction(String name) {
      SquirrelAction result = createMock(SquirrelAction.class);
      expect(result.getValue(Action.NAME)).andReturn(name).anyTimes();
      expect(result.getValue(Action.SMALL_ICON)).andReturn(null).anyTimes();
      expect(result.getValue(Action.MNEMONIC_KEY)).andReturn(null).anyTimes();
      expect(result.getValue(Action.SHORT_DESCRIPTION)).andReturn(null)
                                                       .anyTimes();
      expect(result.getValue(Action.ACTION_COMMAND_KEY)).andReturn(null)
                                                        .anyTimes();
      expect(result.getValue(Action.ACCELERATOR_KEY)).andReturn(null)
                                                     .anyTimes();
      expect(result.isEnabled()).andReturn(true).anyTimes();
      expect(result.getKeyStroke()).andReturn(null).anyTimes();
      result.addPropertyChangeListener(isA(PropertyChangeListener.class));
      expectLastCall().anyTimes();
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

   public static ISQLAliasExt getEasyMockSqlAliasExt() {
      ISQLAliasExt result = createMock(ISQLAliasExt.class);
      expect(result.getName()).andReturn("TestAlias").anyTimes();
      IIdentifier id = getEasyMockIdentifier();
      expect(result.getDriverIdentifier()).andReturn(id).anyTimes();
      replay(result);
      return result;
   }

   public static ActionCollection getEasyMockActionCollection(boolean replay) {
      ActionCollection result = createMock(ActionCollection.class);
      if (replay) {
         replay(result);
      }
      return result;
   }

   public static IApplication getEasyMockApplication() {
      return getEasyMockApplication(true, true, null);
   }

   public static IApplication getEasyMockApplication(ActionCollection col) {
      IApplication result = getEasyMockApplication(false, false, col);
      replay(result);
      return result;
   }

   public static SquirrelResources getEasyMockSquirrelResources() {
      SquirrelResources resources = EasyMock.createMock(SquirrelResources.class);
      resources.setupAction(isA(Action.class), EasyMock.anyBoolean());
      EasyMock.expectLastCall().times(1, 10000);
      replay(resources);
      return resources;
   }

   public static SessionInternalFrame getEasyMockSessionInternalFrame() {
      SessionInternalFrame result = createMock(SessionInternalFrame.class);
      result.addToToolsPopUp(isA(String.class), isA(SquirrelAction.class));
      expectLastCall().anyTimes();
      return result;
   }

   public static SessionProperties getEasyMockSessionProperties(String sep,
         String solComment, boolean removeMultLineComments) {
      SessionProperties result = createMock(SessionProperties.class);
      expect(result.getSQLStatementSeparator()).andReturn(sep).anyTimes();
      expect(result.getStartOfLineComment()).andReturn(solComment).anyTimes();
      expect(result.getRemoveMultiLineComment()).andReturn(removeMultLineComments)
                                                .anyTimes();
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
         SessionProperties props) {
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
      expect(result.getDeleteRule()).andReturn(DatabaseMetaData.importedKeyCascade)
                                    .anyTimes();
      expect(result.getUpdateRule()).andReturn(DatabaseMetaData.importedKeyCascade)
                                    .anyTimes();
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
      expect(mockSqlAlias.getUseDriverProperties()).andReturn(true).anyTimes();
      expect(mockSqlAlias.getDriverPropertiesClone()).andReturn(mockSqlDriverPropCol)
                                                     .anyTimes();
      replay(mockSqlAlias);
      return mockSqlAlias;
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

   public static DBCopyPreferenceBean getEasyMockDBCopyPreferenceBean() {
      DBCopyPreferenceBean result = EasyMock.createMock(DBCopyPreferenceBean.class);
      expect(result.isCopyForeignKeys()).andReturn(true);
      expect(result.isCopyPrimaryKeys()).andReturn(true);
      expect(result.isPruneDuplicateIndexDefs()).andReturn(true);
      expect(result.isPromptForDialect()).andReturn(false);
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
