package net.sourceforge.squirrel_sql.fw.dialects;

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
import java.awt.Component;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Vector;

import org.openide.loaders.DataNode;

import net.sourceforge.squirrel_sql.client.ApplicationArguments;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.MockSession;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableInfo;
import net.sourceforge.squirrel_sql.plugins.db2.DB2JCCExceptionFormatter;
import net.sourceforge.squirrel_sql.plugins.informix.exception.InformixExceptionFormatter;
import net.sourceforge.squirrel_sql.plugins.refactoring.commands.MergeTableCommand;
import net.sourceforge.squirrel_sql.plugins.refactoring.gui.IMergeTableDialog;
import net.sourceforge.squirrel_sql.plugins.refactoring.gui.IMergeTableDialogFactory;

/**
 * The purpose of this class is to hookup to the database(s) specified in
 * dialectLiveTest.properties and test SQL generation parts of the dialect
 * syntatically using the database' native parser. This is not a JUnit test, as
 * it requires a running database to complete.
 * 
 * @author manningr
 */
public class DialectLiveTestRunner {

   ArrayList<ISession> sessions = new ArrayList<ISession>();

   ResourceBundle bundle = null;

   TableColumnInfo firstCol = null;

   TableColumnInfo secondCol = null;

   TableColumnInfo thirdCol = null;

   TableColumnInfo fourthCol = null;

   TableColumnInfo dropCol = null;

   TableColumnInfo noDefaultValueVarcharCol = null;

   TableColumnInfo noDefaultValueIntegerCol = null;

   TableColumnInfo renameCol = null;

   TableColumnInfo pkCol = null;

   // This column is created in the create script abd unused unless testing DB2
   TableColumnInfo db2pkCol = null;

   TableColumnInfo notNullIntegerCol = null;

   // two columns to represent a Primary key in the pktest table
   TableColumnInfo doubleColumnPKOne = null;

   TableColumnInfo doubleColumnPKTwo = null;

   TableColumnInfo autoIncrementColumn = null;
   
   TableColumnInfo addColumn = null;
  
   TableColumnInfo myIdColumn = null;
      
   private static final String DB2_PK_COLNAME = "db2pkCol";

   private DatabaseObjectQualifier qualifier = new DatabaseObjectQualifier();
   
   private SqlGenerationPreferences prefs = new SqlGenerationPreferences();
   
   private static final String fkParentTableName = "fkTestParentTable";
   private static final String fkChildTableName = "fkTestChildTable";
   private static final String fkParentColumnName = "parentid";
   private static final String fkChildColumnName = "fkchildid";
   private static final String testUniqueConstraintTableName = "testUniqueConstraintTable";
   private static final String uniqueConstraintName = "uniq_constraint";
   private static final String autoIncrementTableName = "testAutoIncrementTable";
   private static final String autoIncrementColumnName = "myid";
   private static final String integerDataTableName = "integerDataTable";
   
   private static final String testSequenceName = "testSequence";
   private static final String testSequenceName2 = "testSequence2";
   private static final String testCreateTable = "testCreateTable";
   private static final String testInsertIntoTable = "testInsertIntoTable";
   private static final String testCreateViewTable = "createviewtest";
   private static final String testViewName = "testview";
   private static final String testNewViewName = "newTestView";
   private static final String testView2Name = "testview2";
   private static final String testRenameTableBefore = "tableRenameTest";
   private static final String testRenameTableAfter = "tableWasRenamed";
   private static final String testCreateIndexTable = "createIndexTest";
   private static final String testFirstMergeTable = "firstTableToBeMerged";
   private static final String testSecondMergeTable = "secondTableToBeMerged";
   
   public DialectLiveTestRunner() throws Exception {
      ApplicationArguments.initialize(new String[] {});
      bundle = ResourceBundle.getBundle("net.sourceforge.squirrel_sql.fw.dialects.dialectLiveTest");
      initSessions();
   }

   private void initSessions() throws Exception {
   	prefs.setQualifyTableNames(false);
   	prefs.setQuoteIdentifiers(false);
   	prefs.setSqlStatementSeparator(";");
      String dbsToTest = bundle.getString("dbsToTest");
      StringTokenizer st = new StringTokenizer(dbsToTest, ",");
      ArrayList<String> dbs = new ArrayList<String>();
      while (st.hasMoreTokens()) {
         String db = st.nextToken().trim();
         dbs.add(db);
      }
      for (Iterator<String> iter = dbs.iterator(); iter.hasNext();) {
         String db = iter.next();
         String url = bundle.getString(db + "_jdbcUrl");
         String user = bundle.getString(db + "_jdbcUser");
         String pass = bundle.getString(db + "_jdbcPass");
         String driver = bundle.getString(db + "_jdbcDriver");
         String catalog = bundle.getString(db + "_catalog");
         String schema = bundle.getString(db + "_schema");
         MockSession session = new MockSession(driver, url, user, pass);
         session.setDefaultCatalog(catalog);
         session.setDefaultSchema(schema);
         sessions.add(session);
      }
   }

   private void init(ISession session) throws Exception {
      createTestTables(session);
      firstCol = getIntegerColumn("nullint",
                                  fixTableName(session, "test1"),
                                  true,
                                  "0",
                                  "An int comment");
      secondCol = getIntegerColumn("notnullint",
                                   fixTableName(session, "test2"),
                                   false,
                                   "0",
                                   "An int comment");
      thirdCol = getVarcharColumn("nullvc",
                                  fixTableName(session, "test3"),
                                  true,
                                  "defVal",
                                  "A varchar comment");
      fourthCol = getVarcharColumn("notnullvc",
                                   fixTableName(session, "test4"),
                                   false,
                                   "defVal",
                                   "A varchar comment");
      noDefaultValueVarcharCol = getVarcharColumn("noDefaultVarcharCol",
                                                  fixTableName(session, "test"),
                                                  true,
                                                  null,
                                                  "A varchar column with no default value");
      dropCol = getVarcharColumn("dropCol",
                                 fixTableName(session, "test5"),
                                 true,
                                 null,
                                 "A varchar comment");
      noDefaultValueIntegerCol = getIntegerColumn("noDefaultIntgerCol",
                                                  fixTableName(session, "test5"),
                                                  true,
                                                  null,
                                                  "An integer column with no default value");
      renameCol = getVarcharColumn("renameCol",
                                   fixTableName(session, "test"),
                                   true,
                                   null,
                                   "A column to be renamed");
      pkCol = getIntegerColumn("pkCol",
                               fixTableName(session, "test"),
                               false,
                               "0",
                               "primary key column");
      notNullIntegerCol = getIntegerColumn("notNullIntegerCol",
                                           fixTableName(session, "test5"),
                                           false,
                                           "0",
                                           "potential pk column");
      db2pkCol = getIntegerColumn(DB2_PK_COLNAME,
                                  fixTableName(session, "test"),
                                  false,
                                  "0",
                                  "A DB2 Primary Key column");

      // These two columns will be the only ones in the pktest table. They will
      // start out being nullable, and we will test that the dialect correctly
      // converts them to non-null then applies the PK constraint to them.
      // This test shall not be run against any database dialect that claims not
      // to support changing the nullability of a column.
      doubleColumnPKOne = getIntegerColumn("pk_col_1",
                                           fixTableName(session, "pktest"),
                                           true,
                                           null,
                                           "an initially nullable field to be made part of a PK");
      doubleColumnPKTwo = getIntegerColumn("pk_col_2",
                                           fixTableName(session, "pktest"),
                                           true,
                                           null,
                                           "an initially nullable field to be made part of a PK");
      
      autoIncrementColumn =
			getIntegerColumn(autoIncrementColumnName,
				fixTableName(session, autoIncrementTableName),
				false,
				null,
				"Column that will be auto incrementing");
      
      addColumn = getIntegerColumn("columnAdded", fixTableName(session, testUniqueConstraintTableName), true, null, null);
      
      myIdColumn = getCharColumn("myid", fixTableName(session, testUniqueConstraintTableName), true, null, null);
   }

   private ITableInfo getTableInfo(ISession session, String tableName)
         throws Exception {
      SQLDatabaseMetaData md = session.getSQLConnection().getSQLMetaData();
      String catalog = ((MockSession) session).getDefaultCatalog();
      String schema = ((MockSession) session).getDefaultSchema();
      // System.out.println("Looking for table with catalog="+catalog+"
      // schema="+schema+" tableName="+tableName);
      ITableInfo[] infos = md.getTables(catalog,
                                        schema,
                                        tableName,
                                        new String[] { "TABLE" },
                                        null);
      if (infos.length == 0) {
         if (md.storesUpperCaseIdentifiers()) {
            tableName = tableName.toUpperCase();
         } else {
            tableName = tableName.toLowerCase();
         }
         infos = md.getTables(catalog,
                              schema,
                              tableName,
                              new String[] { "TABLE" },
                              null);
         if (infos.length == 0) {
            return null;
         }
      }
      if (infos.length > 1) {
         throw new IllegalStateException("Found more than one table matching name="
               + tableName);

      }

      return infos[0];

   }

   private void dropTable(ISession session, String tableName) throws Exception {
      try {
         ITableInfo ti = getTableInfo(session, tableName);
         if (ti == null) {
            System.out.println("Table " + tableName
                  + " couldn't be dropped - doesn't exist");
            return;
         }
         dropTable(session, ti);
      } catch (SQLException e) {
         // Do Nothing
      }
   }

   private void dropView(ISession session, String viewName) throws Exception {
      try {
      	runSQL(session, "drop view "+viewName);
      } catch (SQLException e) {
         // Do Nothing
      }
   }
   
   private void dropSequence(ISession session, String sequenceName) throws Exception {
      try {
      	runSQL(session, "drop sequence "+sequenceName);
      } catch (SQLException e) {
         // Do Nothing
      }
   }
   
   private void dropTable(ISession session, ITableInfo ti) throws Exception {
      HibernateDialect dialect = getDialect(session);
      try {
         runSQL(session, dialect.getTableDropSQL(ti, true, false));
      } catch (SQLException e) {
         // Do Nothing
      }
   }
   
   
   /**
    * Setup the test tables. This used to be only one table but it grew due
    * primarily to Ingres' inability to have a table with more than 5-10
    * columns.
    * 
    * @param session
    * @throws Exception
    */
   private void createTestTables(ISession session) throws Exception {
      HibernateDialect dialect = getDialect(session);
      
      // views will depend on tables, so drop them first
      dropView(session, testViewName);
      dropView(session, testView2Name);
      dropView(session, testNewViewName);
      
      // Tables might have triggers that depend on sequences, so drop tables next.
      dropTable(session, fixTableName(session, "test"));
      dropTable(session, fixTableName(session, "test1"));
      dropTable(session, fixTableName(session, "test2"));
      dropTable(session, fixTableName(session, "test3"));
      dropTable(session, fixTableName(session, "test4"));
      dropTable(session, fixTableName(session, "test5"));
      dropTable(session, fixTableName(session, "pktest"));
      dropTable(session, fixTableName(session, testRenameTableBefore));
      dropTable(session, fixTableName(session, testRenameTableAfter));
      dropTable(session, fixTableName(session, testCreateViewTable));
      dropTable(session, fixTableName(session, testCreateIndexTable));
      dropTable(session, fixTableName(session, fkChildTableName));
      dropTable(session, fixTableName(session, fkParentTableName));
      dropTable(session, fixTableName(session, testUniqueConstraintTableName));
      dropTable(session, fixTableName(session, autoIncrementTableName));
      dropTable(session, fixTableName(session, integerDataTableName));
      dropTable(session, fixTableName(session, "a"));
      dropTable(session, fixTableName(session, testCreateTable));
      dropTable(session, fixTableName(session, testInsertIntoTable));
      dropTable(session, fixTableName(session, testFirstMergeTable)); 
      dropTable(session, fixTableName(session, testSecondMergeTable));
      
      // Now sequences should go.
      dropSequence(session, testSequenceName);
      dropSequence(session, testSequenceName2);
      dropSequence(session, autoIncrementTableName + "_MYID_SEQ");
      dropSequence(session, autoIncrementColumnName + "_AUTOINC_SEQ");
      
      if (DialectFactory.isOracle(session.getMetaData())) {
         dropTable(session, fixTableName(session, "matview"));
      }

      String pageSizeClause = "";

      if (DialectFactory.isIngres(session.getMetaData())) {
         // alterations fail for some reason unless you do this...
         pageSizeClause = " with page_size=4096";
      }

      if (DialectFactory.isDB2(session.getMetaData())) {
         // db2pkCol is used to create a PK when using DB2. DB2 doesn't allow
         // you to add a PK to a table after it has been constructed unless the
         // column(s) that comprise the PK were originally there when created
         // *and* created not null.
         runSQL(session, "create table " + fixTableName(session, "test")
               + " ( mychar char(10), " + DB2_PK_COLNAME + " integer not null)");
      } else {
         runSQL(session, "create table " + fixTableName(session, "test")
               + " ( mychar char(10))" + pageSizeClause);
      }

      runSQL(session, "create table " + fixTableName(session, "test1")
            + " ( mychar char(10))" + pageSizeClause);
      runSQL(session, "create table " + fixTableName(session, "test2")
            + " ( mychar char(10))" + pageSizeClause);
      runSQL(session, "create table " + fixTableName(session, "test3")
            + " ( mychar char(10))" + pageSizeClause);
      runSQL(session, "create table " + fixTableName(session, "test4")
            + " ( mychar char(10))" + pageSizeClause);
      runSQL(session, "create table " + fixTableName(session, "test5")
            + " ( mychar char(10))" + pageSizeClause);
      runSQL(session, "create table " + fixTableName(session, testRenameTableBefore)
         + " ( mychar char(10))" + pageSizeClause);
      runSQL(session, "create table " + fixTableName(session, testCreateViewTable)
         + " ( mychar char(10))" + pageSizeClause);
      // MySQL spatial index requires a not null column
      runSQL(session, "create table " + fixTableName(session, testCreateIndexTable)
         + " ( mychar varchar(10) not null, myuniquechar varchar(10))" + pageSizeClause);
      /* DB2 requires primary keys to also be declared "not null" */
      runSQL(session, "create table " + fixTableName(session, fkParentTableName)
         + " ( "+fkParentColumnName+" integer not null primary key, mychar char(10))" + pageSizeClause);
      runSQL(session, "create table " + fixTableName(session, fkChildTableName)
         + " ( myid integer, "+fkChildColumnName+" integer)" + pageSizeClause);
      
      runSQL(session, "create table " + fixTableName(session, testUniqueConstraintTableName)
         + " ( myid char(10))" + pageSizeClause);

      runSQL(session, "create table " + fixTableName(session, autoIncrementTableName)
         + " ( "+autoIncrementColumnName+" integer)" + pageSizeClause);

      runSQL(session, "create table " + fixTableName(session, integerDataTableName)
         + " ( myid integer)" + pageSizeClause);

      int count = 0;
      while (count++ < 11) {
      	runSQL(session, "insert into "+fixTableName(session, integerDataTableName)+" values ("+count+")");
      }

      runSQL(session, 
   		"CREATE TABLE " + fixTableName(session, "a") +
   		"( " +
   		"   acol int NOT NULL PRIMARY KEY, " + 
   		"   adesc varchar(10), " +
   		"   bdesc varchar(10)," +
   		"   joined varchar(20) " +
			") ");
      
      runSQL(session, 
      		"INSERT INTO " + fixTableName(session, "a") + " (acol,adesc,bdesc,joined) VALUES (1,'a1','b1','a1b1') ");

      runSQL(session,
      		"INSERT INTO " + fixTableName(session, "a") + " (acol,adesc,bdesc,joined) VALUES (2,'a2','b2','a2b2') ");

      runSQL(session,
      		"INSERT INTO " + fixTableName(session, "a") + " (acol,adesc,bdesc,joined) VALUES (3,'a3','b3','a3b3') ");

      if (dialect.supportsAlterColumnNull()) {
         runSQL(session, "create table " + fixTableName(session, "pktest")
               + " ( pk_col_1 integer, pk_col_2 integer )" + pageSizeClause);
      }
      
      runSQL(session, "create table " + fixTableName(session, testInsertIntoTable)
         + " ( myid integer)" + pageSizeClause);
      
      
      runSQL(session, "create table " + fixTableName(session, testFirstMergeTable)
         + " ( myid integer, desc_t1 varchar(20))" + pageSizeClause);

      runSQL(session, 
   		"INSERT INTO " + fixTableName(session, testFirstMergeTable) + " (myid, desc_t1) VALUES (1,'table1-row1') ");
      runSQL(session, 
   		"INSERT INTO " + fixTableName(session, testFirstMergeTable) + " (myid, desc_t1) VALUES (2,'table1-row2') ");
      runSQL(session, 
   		"INSERT INTO " + fixTableName(session, testFirstMergeTable) + " (myid, desc_t1) VALUES (3,'table1-row3') ");

      
      runSQL(session, "create table " + fixTableName(session, testSecondMergeTable)
         + " ( myid integer, desc_t2 varchar(20))" + pageSizeClause);

      runSQL(session, 
   		"INSERT INTO " + fixTableName(session, testSecondMergeTable) + " (myid, desc_t2) VALUES (1,'table2-row1') ");
      runSQL(session, 
   		"INSERT INTO " + fixTableName(session, testSecondMergeTable) + " (myid, desc_t2) VALUES (2,'table2-row2') ");
      runSQL(session, 
   		"INSERT INTO " + fixTableName(session, testSecondMergeTable) + " (myid, desc_t2) VALUES (3,'table2-row3') ");
      
   }

   private void runTests() throws Exception {
      for (Iterator<ISession> iter = sessions.iterator(); iter.hasNext();) {
         ISession session = iter.next();
         HibernateDialect dialect = getDialect(session);
         DialectType dialectType = DialectFactory.getDialectType(session.getMetaData());
         if (dialectType == DialectType.SYBASEASE || dialectType == DialectType.MSSQL) {
         	prefs.setSqlStatementSeparator("GO");
         }
         init(session);
         testAddColumn(session);
         testDropColumn(session);
         testAlterDefaultValue(session);
         testColumnComment(session);
         testAlterNull(session);
         testAlterName(session);
         testAlterColumnlength(session);
         // DB2 cannot alter a column's null attribute directly (only
         // through constraints). Not only that, but it's apparently not a
         // valid thing to do to create a primary key using a column that has
         // been made "not null" via a check constraint. Therefore, the only
         // columns that qualify to be made PKs are those that were declared
         // not null at the time of table creation.
         if (DialectFactory.isDB2(session.getMetaData())) {
            testAddPrimaryKey(session, new TableColumnInfo[] { db2pkCol });
            testDropPrimaryKey(session, db2pkCol.getTableName());
         } else {
            try {
               testAddPrimaryKey(session,
                                 new TableColumnInfo[] { notNullIntegerCol });
            } catch (UnsupportedOperationException e) {
               System.err.println("doesn't support adding primary keys");
            }
            try {
               testDropPrimaryKey(session, notNullIntegerCol.getTableName());
            } catch (UnsupportedOperationException e) {
               System.err.println("doesn't support dropping primary keys");
            }

            // Test whether or not the dialect correctly converts nullable
            // columns to not-null before applying the primary key - if
            // necessary
            if (dialect.supportsAlterColumnNull()) {
               try {
                  TableColumnInfo[] infos = new TableColumnInfo[] {
                        doubleColumnPKOne, doubleColumnPKTwo };
                  testAddPrimaryKey(session, infos);
               } catch (UnsupportedOperationException e) {
                  System.err.println("doesn't support adding primary keys");
               }
            }
         }
         testDropMatView(session);
         testCreateTableWithDefault(session);
         testCreateTableSql(session);
         testRenameTable(session);
         testCreateView(session);
         testRenameView(session);
         testDropView(session);
         testCreateAndDropIndex(session);
         testCreateSequence(session);
         testGetSequenceInfo(session);
         testAlterSequence(session);
         testGetSequenceInformation(session);
         testDropSequence(session);
         testAddForeignKeyConstraint(session);
         testAddUniqueConstraint(session);
         testAddAutoIncrement(session);
         testDropConstraint(session);
         testInsertIntoSQL(session);
         testAddColumnSQL(session);
         testUpdateSQL(session);
         testMergeTable(session);
         System.out.println("Completed tests for "+dialect.getDisplayName());
      }
   }
   
   /**
    * CREATE MATERIALIZED VIEW matview2 REFRESH COMPLETE NEXT SYSDATE + 1 WITH
    * PRIMARY KEY AS SELECT * FROM TEST;
    * 
    * @param session
    * @throws Exception
    */
   private void testDropMatView(ISession session) throws Exception {
      if (!DialectFactory.isOracle(session.getMetaData()))
         return;
      HibernateDialect dialect = getDialect(session);
      
      String dropMatViewSql = "DROP MATERIALIZED VIEW MATVIEW ";
      
      try {
         runSQL(session, dropMatViewSql);
      } catch (Exception e) {
         // Don't care if it's not there
      }
      
      testAddPrimaryKey(session, new TableColumnInfo[] { pkCol });
      String createMatViewSQL = "CREATE MATERIALIZED VIEW MATVIEW "
            + "       REFRESH COMPLETE " + "   NEXT  SYSDATE + 1 "
            + "   WITH PRIMARY KEY " + "   AS SELECT * FROM TEST ";
      runSQL(session, createMatViewSQL);
      MockSession msession = (MockSession) session;
      String cat = msession.getDefaultCatalog();
      String schema = msession.getDefaultSchema();
      SQLDatabaseMetaData md = session.getSQLConnection().getSQLMetaData();
      ITableInfo info = new TableInfo(cat, schema, "MATVIEW", "TABLE", "", md);
      List<String> dropSQL = dialect.getTableDropSQL(info, true, true);
      runSQL(session, dropSQL);
   }

   private void testAlterName(ISession session) throws Exception {
      HibernateDialect dialect = getDialect(session);
      
      TableColumnInfo newNameCol = getVarcharColumn("newNameCol",
                                                    "test",
                                                    true,
                                                    null,
                                                    "A column to be renamed");
      if (dialect.supportsRenameColumn()) {
         String sql = dialect.getColumnNameAlterSQL(renameCol, newNameCol);
         runSQL(session, sql);
      } else {
         try {
            dialect.getColumnNameAlterSQL(renameCol, newNameCol);
         } catch (UnsupportedOperationException e) {
            // this is expected
            System.err.println(e.getMessage());
         }
      }
   }

   private void testDropColumn(ISession session) throws Exception {
      HibernateDialect dialect = getDialect(session);
      
      if (dialect.supportsDropColumn()) {
         dropColumn(session, dropCol);
      } else {
         try {
            dropColumn(session, dropCol);
            throw new IllegalStateException("Expected dialect to fail to provide SQL for dropping a column");
         } catch (UnsupportedOperationException e) {
            // This is what we expect
            System.err.println(e.getMessage());
         }
      }
   }

   private void testAlterColumnlength(ISession session) throws Exception {
      HibernateDialect dialect = getDialect(session);
      
      // convert nullint into a varchar(100)
      /*
       * This won't work on Derby where non-varchar columns cannot be altered
       * among other restrictions.
       * 
       * TableColumnInfo nullintVC = getVarcharColumn("nullint", true, "defVal",
       * "A varchar comment"); String alterColTypeSQL =
       * dialect.getColumnTypeAlterSQL(firstCol, nullintVC); runSQL(session,
       * alterColTypeSQL);
       */
      TableColumnInfo thirdColLonger = getVarcharColumn("nullvc",
                                                        "test3",
                                                        true,
                                                        "defVal",
                                                        "A varchar comment",
                                                        30);
      if (dialect.supportsAlterColumnType()) {
         List<String> alterColLengthSQL = dialect.getColumnTypeAlterSQL(thirdCol,
                                                                        thirdColLonger, qualifier, prefs);
         runSQL(session, alterColLengthSQL);
      } else {
         try {
            dialect.getColumnTypeAlterSQL(thirdCol, thirdColLonger, null, null);
            throw new IllegalStateException("Expected dialect to fail to provide SQL for altering column type");
         } catch (UnsupportedOperationException e) {
            // this is expected
         }
      }
   }

   private void testAlterDefaultValue(ISession session) throws Exception {
      HibernateDialect dialect = getDialect(session);
      TableColumnInfo varcharColWithDefaultValue = getVarcharColumn("noDefaultVarcharCol",
                                                                    noDefaultValueVarcharCol.getTableName(),
                                                                    true,
                                                                    "Default Value",
                                                                    "A column with a default value");

      TableColumnInfo integerColWithDefaultVal = getIntegerColumn("noDefaultIntgerCol",
                                                                  noDefaultValueIntegerCol.getTableName(),
                                                                  true,
                                                                  "0",
                                                                  "An integer column with a default value");

      if (dialect.supportsAlterColumnDefault()) {
         String defaultValSQL = dialect.getColumnDefaultAlterSQL(varcharColWithDefaultValue);
         runSQL(session, defaultValSQL);

         defaultValSQL = dialect.getColumnDefaultAlterSQL(integerColWithDefaultVal);
         runSQL(session, defaultValSQL);
      } else {
         try {
            dialect.getColumnDefaultAlterSQL(noDefaultValueVarcharCol);
            throw new IllegalStateException("Expected dialect to fail to provide SQL for column default alter");
         } catch (UnsupportedOperationException e) {
            // This is what we expect.
            System.err.println(e.getMessage());
         }
      }
   }

   private void testAlterNull(ISession session) throws Exception {
      HibernateDialect dialect = getDialect(session);
      TableColumnInfo notNullThirdCol = getVarcharColumn("nullvc",
                                                         "test3",
                                                         false,
                                                         "defVal",
                                                         "A varchar comment");
      if (dialect.supportsAlterColumnNull()) {
         String[] notNullSQL = dialect.getColumnNullableAlterSQL(notNullThirdCol, qualifier, prefs);
         runSQL(session, notNullSQL);
      } else {
         try {
            dialect.getColumnNullableAlterSQL(notNullThirdCol, qualifier, prefs);
            throw new IllegalStateException("Expected dialect to fail to provide SQL for column nullable alter");
         } catch (UnsupportedOperationException e) {
            // this is expected
            System.err.println(e.getMessage());
         }
      }
   }

   private void testAddColumn(ISession session) throws Exception {
      addColumn(session, firstCol);
      addColumn(session, secondCol);
      addColumn(session, thirdCol);
      addColumn(session, fourthCol);
      addColumn(session, dropCol);
      addColumn(session, noDefaultValueVarcharCol);
      addColumn(session, noDefaultValueIntegerCol);
      addColumn(session, renameCol);
      addColumn(session, pkCol);
      addColumn(session, notNullIntegerCol);
   }

   private void addColumn(ISession session, TableColumnInfo info)
         throws Exception {
      HibernateDialect dialect = getDialect(session);
      String[] sqls = dialect.getAddColumnSQL(info, qualifier, prefs);
      for (int i = 0; i < sqls.length; i++) {
         String sql = sqls[i];
         runSQL(session, sql);
      }

   }

   private void testColumnComment(ISession session) throws Exception {
      HibernateDialect dialect = getDialect(session);
      if (dialect.supportsColumnComment()) {
         alterColumnComment(session, firstCol);
         alterColumnComment(session, secondCol);
         alterColumnComment(session, thirdCol);
         alterColumnComment(session, fourthCol);
      } else {
         try {
            alterColumnComment(session, firstCol);
            throw new IllegalStateException("Expected dialect to fail to provide SQL for column comment alter");
         } catch (UnsupportedOperationException e) {
            // This is expected
            System.err.println(e.getMessage());
         }
      }
   }

   private void alterColumnComment(ISession session, TableColumnInfo info)
         throws Exception {
      HibernateDialect dialect = getDialect(session);
      String commentSQL = dialect.getColumnCommentAlterSQL(info);
      if (commentSQL != null && !commentSQL.equals("")) {
         runSQL(session, commentSQL);
      }
   }

   private String getPKName(String tableName) {
      return tableName.toUpperCase() + "_PK";
   }

   private void testDropPrimaryKey(ISession session, String tableName)
         throws Exception {
      HibernateDialect dialect = getDialect(session);
      String pkName = getPKName(tableName);
      String sql = dialect.getDropPrimaryKeySQL(pkName, tableName);
      runSQL(session, sql);
   }

   private void testAddPrimaryKey(ISession session, TableColumnInfo[] colInfos)
         throws Exception {
      HibernateDialect dialect = getDialect(session);

      String tableName = colInfos[0].getTableName();

      if (session.getSQLConnection()
                 .getSQLMetaData()
                 .storesUpperCaseIdentifiers()) {
         tableName = tableName.toUpperCase();
      }

      SQLDatabaseMetaData md = session.getSQLConnection().getSQLMetaData();
      String catalog = ((MockSession) session).getDefaultCatalog();
      String schema = ((MockSession) session).getDefaultSchema();

      ITableInfo[] infos = null;
      try {
         md.getTables(catalog,
                      schema,
                      tableName,
                      new String[] { "TABLE" },
                      null);
      } catch (SQLException e) {
         // Do nothing
      }

      ITableInfo ti = null;
      if (infos != null && infos.length > 0) {
         ti = infos[0];
      } else {
         // Couldn't locate the table - just try to fake it.
         ti = new TableInfo(catalog, schema, tableName, "TABLE", "", md);
      }

      String[] pkSQLs = dialect.getAddPrimaryKeySQL(getPKName(tableName),
                                                    colInfos,
                                                    ti);

      for (int i = 0; i < pkSQLs.length; i++) {
         String pkSQL = pkSQLs[i];
         runSQL(session, pkSQL);
      }
   }

   private void testCreateTableWithDefault(ISession session) throws Exception {
      HibernateDialect dialect = getDialect(session);
      String testTableName = "TESTDEFAULT";
         
      dropTable(session, testTableName);
      
      /* create a table with a default value to use as the model */
      String createSql = "create table " + testTableName
            + " ( mycol integer default 0 )";

      runSQL(session, createSql);

      /* Build the sql statement(s) to create the table */
      ITableInfo tableInfo = getTableInfo(session, testTableName); 
      if (tableInfo == null) {
         tableInfo = getTableInfo(session, testTableName.toLowerCase());
      }
      if (tableInfo == null) {
         tableInfo = getTableInfo(session, testTableName.toUpperCase());
      }
      
      CreateScriptPreferences prefs = new CreateScriptPreferences();
      List<String> sqls = dialect.getCreateTableSQL(Arrays.asList(tableInfo),
                                                    session.getMetaData(),
                                                    prefs,
                                                    false);
      
      /* drop the table so that we can test our create statement */
      dropTable(session, tableInfo);
      
      /* Test the create statement */
      for (String sql : sqls) {
         runSQL(session, sql);
      }
   }
   
   private void testCreateTableSql(ISession session) throws Exception {
      HibernateDialect dialect = getDialect(session);
      
   
      List<TableColumnInfo> columns = new ArrayList<TableColumnInfo>();
      String catalog = ((MockSession) session).getDefaultCatalog();
      String schema = ((MockSession) session).getDefaultSchema();
      String columnName = "firstCol";
      String tableName = fixTableName(session, testCreateTable);
      
      TableColumnInfo firstCol =
			new TableColumnInfo(	catalog,
										schema,
										testCreateTable,
										columnName,
										Types.BIGINT,
										"BIGINT",
										10,
										0,
										0,
										1,
										null,
										null,
										0,
										0,
										"YES");
      columns.add(firstCol);
      List<TableColumnInfo> pkColumns = null;
      
   	if (dialect.supportsCreateTable()) {
   		String sql = dialect.getCreateTableSQL(tableName, columns, pkColumns, prefs, qualifier); 
   		runSQL(session, sql);
   	} else {
   		try {
   			dialect.getCreateTableSQL(tableName, columns, pkColumns, prefs, qualifier);
   			throw new IllegalStateException("Expected dialect to fail to provide SQL for create table");
   		} catch (Exception e) {
   			// This is expected
   			System.err.println(e.getMessage());
   		}
   	}
      
   }   
   private void testRenameTable(ISession session) throws Exception {
      HibernateDialect dialect = getDialect(session);
      String oldTableName = fixTableName(session, testRenameTableBefore);
      String newTableName = fixTableName(session, testRenameTableAfter);
      
   	if (dialect.supportsRenameTable()) {
   		String sql = dialect.getRenameTableSQL(oldTableName, newTableName, qualifier, prefs);
   		runSQL(session, sql);
   	} else {
   		try {
   			dialect.getRenameTableSQL(oldTableName, newTableName, qualifier, prefs);
   			throw new IllegalStateException("Expected dialect to fail to provide SQL for rename table");
   		} catch (Exception e) {
   			// This is expected
   			System.err.println(e.getMessage());
   		}
   	}
   }

   private void testCreateView(ISession session) throws Exception {
      HibernateDialect dialect = getDialect(session);
      
      String checkOption = "";
      
   	if (dialect.supportsCreateView()) {
         
   		String sql =
				dialect.getCreateViewSQL(testViewName, "select * from "+testCreateViewTable, checkOption, qualifier, prefs);
   		runSQL(session, sql);
   		
         if (dialect.supportsCheckOptionsForViews()) {
         	checkOption = "some_check";
         	sql =
   				dialect.getCreateViewSQL(testView2Name, "select * from "+testCreateViewTable, checkOption, qualifier, prefs);
      		runSQL(session, sql);
         }

   	} else {
   		try {
   			dialect.getCreateViewSQL(testViewName, "select * from "+testCreateViewTable, checkOption, qualifier, prefs);
   			throw new IllegalStateException("Expected dialect to fail to provide SQL for " +
   					"create view");
   		} catch (Exception e) {
   			// This is expected
   			System.err.println(e.getMessage());
   		}
   	}
   }
   
   private void testRenameView(ISession session) throws Exception {
   	HibernateDialect dialect = getDialect(session);
   	String oldViewName = testViewName;
   	String newViewName = testNewViewName;
   	String catalog = ((MockSession)session).getDefaultCatalog();
   	String schema = ((MockSession)session).getDefaultSchema();
   	DatabaseObjectQualifier qual = new DatabaseObjectQualifier(catalog, schema);
   	
   	String viewDefSql = dialect.getViewDefinitionSQL(oldViewName, qual, prefs);
   	if (dialect.supportsRenameView()) {
   		String[] sql = 
   			dialect.getRenameViewSQL(oldViewName, newViewName, qual, prefs);
   		runSQL(session, sql);
   	} else if ( viewDefSql != null && dialect.supportsCreateView()) {
   		
   		ResultSet rs = this.runQuery(session, viewDefSql);
   		StringBuilder tmp = new StringBuilder();
   		while (rs.next()) {
   			tmp.append(rs.getString(1));
   		}
   		String viewDefinition = tmp.toString();

			if (viewDefinition == null || "".equals(viewDefinition)) {
				throw new IllegalStateException("View source was not retrieved by this query: "+viewDefSql);
			}
			int asIndex = viewDefinition.toUpperCase().indexOf("AS");
			if (asIndex != -1) {
				viewDefinition = "create view "+newViewName+ " as "+viewDefinition.substring(asIndex + 2);
			}

			String dropOldViewSql = dialect.getDropViewSQL(oldViewName, false, qual, prefs);
			runSQL(session, new String[] { viewDefinition, dropOldViewSql });
   	} else {
   		try {
   			dialect.getRenameViewSQL(oldViewName, newViewName, qual, prefs);
   			throw new IllegalStateException("Expected dialect to fail to provide SQL for rename view");
   		} catch (Exception e) {
   			// this is expected
   			System.err.println(e.getMessage());
   		}
   	}
   }
   
   private void testDropView(ISession session) throws Exception {
   	HibernateDialect dialect = getDialect(session);
   	if (dialect.supportsCreateView() && dialect.supportsDropView()) {
   		String sql = "";
   		sql = dialect.getDropViewSQL(testNewViewName, false, qualifier, prefs);
   		runSQL(session, sql);
   	} else {
   		try {
   			dialect.getDropViewSQL(testViewName, false, qualifier, prefs);
   			throw new IllegalStateException("Expected dialect to fail to provide SQL for drop view");
   		} catch (Exception e) {
   			// this is expected
   			System.err.println(e.getMessage());
   		}
   	}   	
   }
   
   private void testCreateAndDropIndex(ISession session) throws Exception {
   	HibernateDialect dialect = getDialect(session);
		String[] columns = new String[] { "mychar" };
		String tablespace = null;
		String constraints = null;

		final String indexName1 = "testIndex";
		final String indexName2 = "testUniqueIndex";
		final String tableName = fixTableName(session, testCreateIndexTable);
		
   	if (dialect.supportsCreateIndex() && dialect.supportsDropIndex()) {
   		
   		String[] accessMethods = null;
   		if (dialect.supportsAccessMethods()) {
   			accessMethods = dialect.getIndexAccessMethodsTypes();
   		} else {
   			accessMethods = new String[] { "" };
   		}
   		
   		for (String accessMethod : accessMethods) {
   			// Postgres has some special indexes that only work on certain columns.  Skip tests for those
   			if (accessMethod.equalsIgnoreCase("gin") || accessMethod.equalsIgnoreCase("gist")) {
   				continue;
   			}
   			// MySQL requires MyISAM storage engine for spatial indexes.
				if (DialectFactory.isMySQL5(session.getMetaData())
					|| DialectFactory.isMySQL(session.getMetaData()))
				{
					if (accessMethod.equalsIgnoreCase("spatial"))
					{
						continue;
					}
				}
	   		// create a non-unique index on mychar
	   		columns = new String[] { "mychar" };
				String sql =
					dialect.getCreateIndexSQL(indexName1,
						tableName,
						accessMethod,
						columns,
						false,
						tablespace,
						constraints,
						qualifier,
						prefs);
				// create it.
				runSQL(session, sql);
   		
	   		// now drop it.
	   		runSQL(session, dialect.getDropIndexSQL(tableName, indexName1, true, qualifier, prefs));
	   		
	   		// create a unique index on myuniquechar
				columns = new String[] { "myuniquechar" };
				sql =
					dialect.getCreateIndexSQL(indexName2,
						tableName,
						accessMethod,
						columns,
						true,
						tablespace,
						constraints,
						qualifier,
						prefs);
	   		
	   		// create it.
	   		runSQL(session, sql);
	   		
	   		// now drop it.
	   		runSQL(session, dialect.getDropIndexSQL(tableName, indexName2, true, qualifier, prefs));
   		}   		
   	} else {
   		try {
   			dialect.getCreateIndexSQL(indexName1, tableName, null, columns, false, tablespace, constraints, qualifier, prefs);
   			throw new IllegalStateException("Expected dialect to fail to provide SQL for create index");
   		} catch (Exception e) {
   			// this is expected
   			System.err.println(e.getMessage());
   		}
   	}   	
   }
      
   private void testCreateSequence(ISession session) throws Exception {
   	HibernateDialect dialect = getDialect(session);
   	String cache = null;
   	boolean cycle = true;
   	if (dialect.supportsCreateSequence()) {
   		
   		String sql = 
   			dialect.getCreateSequenceSQL(testSequenceName, "1", "1", "100", "1", cache, cycle, qualifier, prefs);
   		runSQL(session, sql);
   		
   		cycle = false;
   		 sql = 
    			dialect.getCreateSequenceSQL(testSequenceName2, "1", "1", "100", "1", cache, cycle, qualifier, prefs);
    		runSQL(session, sql);   		
   	} else {
   		try {
   			dialect.getCreateSequenceSQL(testSequenceName, "1", "1", "100", "1", cache, cycle, qualifier, prefs);
   			throw new IllegalStateException("Expected dialect to fail to provide SQL for create sequence");
   		} catch (Exception e) {
   			// this is expected
   			System.err.println(e.getMessage());
   		}
   	}   	   	   	
   }
   
   private void testGetSequenceInfo(ISession session) throws Exception {
   	HibernateDialect dialect = getDialect(session);
   	if (dialect.supportsSequence()) {
   		String sql = dialect.getSequenceInformationSQL(testSequenceName, qualifier, prefs);
   		if (sql.endsWith("?") || sql.endsWith("?)")) {
   			ResultSet rs = runPreparedQuery(session, sql, testSequenceName);
   			if (!rs.next()) {
   				throw new IllegalStateException("Expected a result from sequence info query");
   			}
   		} else {
   			runSQL(session, sql);
   		}
   		
   	} else {
   		try {
   			dialect.getSequenceInformationSQL(testSequenceName, qualifier, prefs);
   			throw new IllegalStateException("Expected dialect to fail to provide SQL for sequence info query");
   		} catch (Exception e) {
   			// this is expected
   			System.err.println(e.getMessage());
   		}
   	}   	   	   	   	
   }
   
   private void testAlterSequence(ISession session) throws Exception {
   	HibernateDialect dialect = getDialect(session);
   	String cache = null;
   	boolean cycle = true;
   	if (dialect.supportsAlterSequence()) {
   		
   		String restart = "5";
   		
   		String[] sql = 
   			dialect.getAlterSequenceSQL(testSequenceName, "1", "1", "1000", restart, cache, cycle, qualifier, prefs);
   		runSQL(session, sql);
   		
   		cache = "10";
   		cycle = false;
   		restart = null;
   		sql = 
   			dialect.getAlterSequenceSQL(testSequenceName, "1", "1", "1000", restart, cache, cycle, qualifier, prefs);
   		runSQL(session, sql);
   		
   	} else {
   		try {
   			dialect.getCreateSequenceSQL(testSequenceName, "1", "1", "1000", "1", cache, cycle, qualifier, prefs);
   			throw new IllegalStateException("Expected dialect to fail to provide SQL for alter sequence");
   		} catch (Exception e) {
   			// this is expected
   			System.err.println(e.getMessage());
   		}
   	}   	   	   	   	
   }
   
   private void testGetSequenceInformation(ISession session) throws Exception {
   	HibernateDialect dialect = getDialect(session);
   	if (dialect.supportsSequenceInformation()) {
   		String sql = 
   			dialect.getSequenceInformationSQL(testSequenceName, qualifier, prefs);
   		if (sql.endsWith("?") || sql.endsWith("?)")) {
   			ResultSet rs = runPreparedQuery(session, sql, testSequenceName);
   			if (!rs.next()) {
   				throw new IllegalStateException("Expected a record for the sql with bind variable = "
						+ testSequenceName);
   			}
   		} else {
   			runSQL(session, sql);
   		}
   	} else {
   		try {
   			dialect.getSequenceInformationSQL(testSequenceName, qualifier, prefs);
   			throw new IllegalStateException("Expected dialect to fail to provide SQL for get sequence information");
   		} catch (Exception e) {
   			// this is expected
   			System.err.println(e.getMessage());
   		}
   	}   	   	   	   	   	
   }
   
   private void testDropSequence(ISession session) throws Exception {
   	HibernateDialect dialect = getDialect(session);
   	if (dialect.supportsDropSequence()) {
   		String sql = 
   			dialect.getDropSequenceSQL(testSequenceName, false, qualifier, prefs);
   		runSQL(session, sql);   		
   	} else {
   		try {
   			dialect.getDropSequenceSQL(testSequenceName, false, qualifier, prefs);
   			throw new IllegalStateException("Expected dialect to fail to provide SQL for drop sequence");
   		} catch (Exception e) {
   			// this is expected
   			System.err.println(e.getMessage());
   		}
   	}   	   	   	   	   	   	
   }
   
   private void testAddForeignKeyConstraint(ISession session) throws Exception
	{
		HibernateDialect dialect = getDialect(session);
		boolean deferrable = true;
		boolean initiallyDeferred = true;
		boolean matchFull = true;
		boolean autoFKIndex = false;
		String fkIndexName = "fk_idx";
		ArrayList<String[]> localRefColumns = new ArrayList<String[]>();
		localRefColumns.add(new String[] { fkChildColumnName, fkParentColumnName });
		String onUpdateAction = null;
		String onDeleteAction = null;
		String childTable = fixTableName(session, fkChildTableName);
		String parentTable = fixTableName(session, fkParentTableName);
		if (dialect.supportsAddForeignKeyConstraint())
		{
			String[] sql =
				dialect.getAddForeignKeyConstraintSQL(childTable,
					parentTable,
					"fk_child_refs_parent",
					deferrable,
					initiallyDeferred,
					matchFull,
					autoFKIndex,
					fkIndexName,
					localRefColumns,
					onUpdateAction,
					onDeleteAction,
					qualifier,
					prefs);
			runSQL(session, sql);
		} else
		{
			try
			{
				dialect.getAddForeignKeyConstraintSQL(childTable,
					parentTable,
					"fk_child_refs_parent",
					deferrable,
					initiallyDeferred,
					matchFull,
					autoFKIndex,
					fkIndexName,
					localRefColumns,
					onUpdateAction,
					onDeleteAction,
					qualifier,
					prefs);
				throw new IllegalStateException("Expected dialect to fail to provide SQL for add FK constraint");
			} catch (Exception e)
			{
				// this is expected
				System.err.println(e.getMessage());
			}
		}
	}
   
   private void testAddUniqueConstraint(ISession session) throws Exception {
   	HibernateDialect dialect = getDialect(session);
   	TableColumnInfo[] columns = new TableColumnInfo[] { myIdColumn };
   	String tableName = fixTableName(session, testUniqueConstraintTableName);
   	if (dialect.supportsAddUniqueConstraint()) {
   		String[] sql = 
   			dialect.getAddUniqueConstraintSQL(tableName,
   				uniqueConstraintName,
					columns,
					qualifier,
					prefs);
   		runSQL(session, sql);   		
   	} else {
   		try {
   			dialect.getAddUniqueConstraintSQL(tableName,
   				uniqueConstraintName,
					columns,
					qualifier,
					prefs);
   			throw new IllegalStateException("Expected dialect to fail to provide SQL for add unique "
					+ "constraint");
   		} catch (Exception e) {
   			// this is expected
   			System.err.println(e.getMessage());
   		}
   	}   	   	   	   	   	   	
   }
   
   private void testAddAutoIncrement(ISession session) throws Exception {
   	HibernateDialect dialect = getDialect(session);
   	if (dialect.supportsAutoIncrement()) {
   		String[] sql = 
   			dialect.getAddAutoIncrementSQL(autoIncrementColumn, qualifier, prefs);
   		runSQL(session, sql);   		
   	} else {
   		try {
   			dialect.getAddAutoIncrementSQL(autoIncrementColumn, qualifier, prefs);
   			throw new IllegalStateException("Expected dialect to fail to provide SQL for add auto increment "
					+ "column");
   		} catch (Exception e) {
   			// this is expected
   			System.err.println(e.getMessage());
   		}
   	}   	   	   	   	   	   	   	
   }
   
   private void testDropConstraint(ISession session) throws Exception {
   	HibernateDialect dialect = getDialect(session);
   	String tableName = fixTableName(session, testUniqueConstraintTableName);
		if (dialect.supportsDropConstraint())
		{
			String sql =
				dialect.getDropConstraintSQL(tableName,
					uniqueConstraintName,
					qualifier,
					prefs);
			runSQL(session, sql);
		} 
		else
		{
			try
			{
				dialect.getDropConstraintSQL(tableName,
					uniqueConstraintName,
					qualifier,
					prefs);
				throw new IllegalStateException("Expected dialect to fail to provide SQL for add unique "
					+ "constraint");
			} catch (Exception e)
			{
				// this is expected
				System.err.println(e.getMessage());
			}
		}   	   	   	   	   	   	   	
   }
   
   private void testInsertIntoSQL(ISession session) throws Exception {
   	HibernateDialect dialect = getDialect(session);
   	String valuesPart = "select distinct myid from "+fixTableName(session, integerDataTableName);
   	ArrayList<String> columns = new ArrayList<String>();
   	String tableName = fixTableName(session, testInsertIntoTable);
		if (dialect.supportsInsertInto())
		{
			String sql = 
				dialect.getInsertIntoSQL(tableName, columns, valuesPart, qualifier, prefs);
			runSQL(session, sql);
			
			valuesPart = " values ( 20 )";
			sql = dialect.getInsertIntoSQL(tableName, columns, valuesPart, qualifier, prefs);
			runSQL(session, sql);
		} 
		else
		{
			try
			{
				dialect.getInsertIntoSQL(testInsertIntoTable, columns, valuesPart, qualifier, prefs);
				throw new IllegalStateException("Expected dialect to fail to provide SQL for insert into ");
			} catch (Exception e)
			{
				// this is expected
				System.err.println(e.getMessage());
			}
		}   	   	   	   	   	   	   	   	
   }
   
   private void testAddColumnSQL(ISession session) throws Exception {
   	HibernateDialect dialect = getDialect(session);

		if (dialect.supportsAddColumn()) 
		{
			String[] sql = 
				dialect.getAddColumnSQL(addColumn, qualifier, prefs);
			runSQL(session, Arrays.asList(sql));
		} 
		else
		{
			try
			{
				dialect.getAddColumnSQL(addColumn, qualifier, prefs);
				throw new IllegalStateException("Expected dialect to fail to provide SQL for add column ");
			} catch (Exception e)
			{
				// this is expected
				System.err.println(e.getMessage());
			}
		}   	   	   	   	   	   	   	   	   	
   }
   
   /**
        // UPDATE tableName SET setColumn1 = setValue1, setColumn2 = setValue2
		// FROM fromTable1, fromTable2
		// WHERE whereColumn1 = whereValue1 AND whereColumn2 = whereValue2;
 
    * @param session
    * @throws Exception
    */
   private void testUpdateSQL(ISession session) throws Exception {
   	HibernateDialect dialect = getDialect(session);
   	String tableName = "a";
   	String[] setColumns = new String[] { "joined" };
   	String[] setValues = new String[] { "'a1b1'" };
   	String[] fromTables = null;
   	String[] whereColumns = new String[] { "adesc", "bdesc"};
   	String[] whereValues = new String[] { "'a1'", "'b1'" };
   	
		if (dialect.supportsUpdate())
		{
			String sql =
				dialect.getUpdateSQL(tableName,
					setColumns,
					setValues,
					fromTables,
					whereColumns,
					whereValues,
					qualifier,
					prefs);
			runSQL(session, sql);			
		} 
		else
		{
			try
			{
				dialect.getUpdateSQL(testUniqueConstraintTableName,
					setColumns,
					setValues,
					fromTables,
					whereColumns,
					whereValues,
					qualifier,
					prefs);
				throw new IllegalStateException("Expected dialect to fail to provide SQL for update ");
			} catch (Exception e)
			{
				// this is expected
				System.err.println(e.getMessage());
			}
		}   	   	   	   	   	   	   	   	   	
   }   
   
   private void testMergeTable(ISession session) throws Exception {
   	HibernateDialect dialect = getDialect(session);
   	if (dialect.supportsUpdate() && dialect.supportsAddColumn()) {
   		
   		IDatabaseObjectInfo[] selectedTables = new IDatabaseObjectInfo[1];
   		String catalog = ((MockSession)session).getDefaultCatalog();
   		String schema = ((MockSession)session).getDefaultSchema();
   		
   		selectedTables[0] =
				new DatabaseObjectInfoHelper(	catalog,
														schema,
														fixTableName(session, testSecondMergeTable),
														fixTableName(session, testSecondMergeTable),
														DatabaseObjectType.TABLE);
   		
   		MergeTableCommandHelper command = new MergeTableCommandHelper(session, selectedTables, null);
   		
   		String[] sqls = command.generateSQLStatements();
   		
   		runSQL(session, sqls);
   	}
   }
   
   // Utility methods
   
   private void dropColumn(ISession session, TableColumnInfo info)
         throws Exception {
      HibernateDialect dialect = getDialect(session);
      String sql = dialect.getColumnDropSQL(info.getTableName(),
                                            info.getColumnName());
      runSQL(session, sql);
   }

   private HibernateDialect getDialect(ISession session) throws Exception {
      return DialectFactory.getDialect(DialectFactory.DEST_TYPE,
                                       null,
                                       session.getMetaData());
   }

   private void runSQL(ISession session, List<String> sql) throws Exception {
      for (String stmt : sql) {
         runSQL(session, stmt);
      }
   }

   private void runSQL(ISession session, String[] sql) throws Exception {
      for (String stmt : sql) {
      	runSQL(session, stmt);
      }
   } 
   
   private void runSQL(ISession session, String sql) throws Exception {
   	HibernateDialect dialect = getDialect(session);
   	if (sql == null) {
   		throw new IllegalStateException("sql argument was null");
   	}
   	if (!sql.startsWith("--")) {
	      Connection con = session.getSQLConnection().getConnection();
	      Statement stmt = con.createStatement();
	      System.out.println("Running SQL  (" + dialect.getDisplayName() + "): "
	            + sql);
	      try {
	      	stmt.execute(sql);
	      } catch(Exception e) {
	      	if (DialectFactory.isDB2(session.getMetaData())) {
	      		DB2JCCExceptionFormatter formatter = new DB2JCCExceptionFormatter();
	      		System.err.println("Formatted message: "+formatter.format(e));
	      	}
	      	if (DialectFactory.isInformix(session.getMetaData())) {
	      		InformixExceptionFormatter formatter = new InformixExceptionFormatter(session);
	      		System.err.println("Formatted message: "+formatter.format(e));
	      	}
	      	throw e;
	      }
   	} else {
         System.out.println("Skip Comment (" + dialect.getDisplayName() + "): "
            + sql);   		
   	}
   }

   private ResultSet runQuery(ISession session, String sql) throws Exception {
      HibernateDialect dialect = getDialect(session);
      Connection con = session.getSQLConnection().getConnection();
      Statement stmt = con.createStatement();
      
      System.out.println("Running SQL (" + dialect.getDisplayName() + "): "
            + sql);
      return stmt.executeQuery(sql);
   }
   
   
   private ResultSet runPreparedQuery(ISession session, String sql, String value) throws Exception {
      HibernateDialect dialect = getDialect(session);
      Connection con = session.getSQLConnection().getConnection();
      PreparedStatement stmt = con.prepareStatement(sql);
      System.out.println("Running SQL (" + dialect.getDisplayName() + "): "
            + sql+ " with bind variable : "+value);
      stmt.setString(1, value);
      return stmt.executeQuery();
   }
   
   private TableColumnInfo getIntegerColumn(String name, String tableName,
         boolean nullable, String defaultVal, String comment) {
      return getColumn(java.sql.Types.INTEGER,
                       "INTEGER",
                       name,
                       tableName,
                       nullable,
                       defaultVal,
                       comment,
                       10,
                       0);
   }

   private TableColumnInfo getCharColumn(String name, String tableName,
      boolean nullable, String defaultVal, String comment) {
   return getColumn(java.sql.Types.CHAR,
                    "CHAR",
                    name,
                    tableName,
                    nullable,
                    defaultVal,
                    comment,
                    10,
                    0);
}
   
   
   private TableColumnInfo getVarcharColumn(String name, String tableName,
         boolean nullable, String defaultVal, String comment, int size) {
      return getColumn(java.sql.Types.VARCHAR,
                       "VARCHAR",
                       name,
                       tableName,
                       nullable,
                       defaultVal,
                       comment,
                       size,
                       0);
   }

   private TableColumnInfo getVarcharColumn(String name, String tableName,
         boolean nullable, String defaultVal, String comment) {
      return getColumn(java.sql.Types.VARCHAR,
                       "VARCHAR",
                       name,
                       tableName,
                       nullable,
                       defaultVal,
                       comment,
                       20,
                       0);
   }

   private TableColumnInfo getColumn(int dataType, String dataTypeName,
         String name, String tableName, boolean nullable, String defaultVal,
         String comment, int columnSize, int scale) {
      String isNullable = "YES";
      int isNullAllowed = DatabaseMetaData.columnNullable;
      if (!nullable) {
         isNullable = "NO";
         isNullAllowed = DatabaseMetaData.columnNoNulls;
      }
      TableColumnInfo result = new TableColumnInfo("testCatalog", // catalog
                                                   "testSchema", // schema
                                                   tableName, // tableName
                                                   name, // columnName
                                                   dataType, // dataType
                                                   dataTypeName, // typeName
                                                   columnSize, // columnSize
                                                   scale, // decimalDigits
                                                   10, // radix
                                                   isNullAllowed, // isNullAllowed
                                                   comment, // remarks
                                                   defaultVal, // defaultValue
                                                   0, // octet length
                                                   0, // ordinal position
                                                   isNullable); // isNullable
      return result;
   }

   /**
    * @param session
    * @param table
    * @return
    * @throws Exception
    */
   private String fixTableName(ISession session, String table) throws Exception {
      String result = null;
      SQLDatabaseMetaData md = session.getSQLConnection().getSQLMetaData();
      if (md.storesUpperCaseIdentifiers()) {
         result = table.toUpperCase();
      } else {
         result = table.toLowerCase();
      }
      return result;
   }

   /**
    * @param session
    * @param tableName
    * @return
    * @throws SQLException
    */
   private TableColumnInfo[] getLiveColumnInfo(ISession session, String tableName) throws SQLException {
   	ISQLDatabaseMetaData md = session.getMetaData();
   	String catalog = ((MockSession)session).getDefaultCatalog();
   	String schema = ((MockSession)session).getDefaultSchema();
   	return md.getColumnInfo(catalog, schema, tableName);
   }
   
   /**
    * @param args
    */
   public static void main(String[] args) throws Exception {
      DialectLiveTestRunner runner = new DialectLiveTestRunner();
      runner.runTests();
   }

   
   /**
    * A helper "dialog" implementation that simulates getting user input for automating the test.  The merge
    * table command takes substantial input, and based on that generates a substantial amount of SQL.
    * 
    * @author manningr
    * 
    */
   private class MergeTableDialogHelper implements IMergeTableDialog {

   	private Vector<String> _mergeColumns = null;
   	private String _referencedTable = null;
   	private Vector<String[]> _whereDataColumns = null;
   	private boolean _isMergeData = false;
   	
   	public MergeTableDialogHelper(Vector<String> mergeColumns, 
   										   String referencedTable,
   										   Vector<String[]> whereDataColumns,
   										   boolean isMergeData) {
   		_mergeColumns = mergeColumns;
   		_referencedTable = referencedTable;
   		_whereDataColumns = whereDataColumns;
   		_isMergeData = isMergeData;
   	}
   	
   	// Unused methods - this is not a real dialog, just a fake.
		public void addEditSQLListener(ActionListener listener) {}
		public void addExecuteListener(ActionListener listener) {}
		public void addShowSQLListener(ActionListener listener) {}
		public void setLocationRelativeTo(Component c) {}
		public void setVisible(boolean val) {}
		public void dispose() {}
		
		public Vector<String> getMergeColumns() { return _mergeColumns; }
		public String getReferencedTable() { return _referencedTable; }
		public Vector<String[]> getWhereDataColumns() { return _whereDataColumns; }
		public boolean isMergeData() { return _isMergeData; }
   }
   
   /**
      runSQL(session, "create table " + fixTableName(session, testFirstMergeTable)
         + " ( myid integer, desc_t1 varchar(20))" + pageSizeClause);
      
      runSQL(session, "create table " + fixTableName(session, testSecondMergeTable)
         + " ( myid integer, desc_t2 varchar(20))" + pageSizeClause);
 
    */
   private class MergeTableCommandHelper extends MergeTableCommand {
		public MergeTableCommandHelper(ISession session, IDatabaseObjectInfo[] info,
			IMergeTableDialogFactory dialogFactory) throws Exception 
		{
			super(session, info, dialogFactory);
	   	Vector<String> mergeColumns = new Vector<String>();
	   	mergeColumns.add("desc_t1");
	   	String referencedTable = fixTableName(session, testFirstMergeTable);
	   	Vector<String[]> whereDataColumns = new Vector<String[]>();
	   	whereDataColumns.add(new String[] { "myid", "myid" });
	   	boolean _isMergeData = true;			
			super.customDialog = 
				new MergeTableDialogHelper(mergeColumns, referencedTable, whereDataColumns, _isMergeData);		
			_allTables = new HashMap<String, TableColumnInfo[]>();
			_allTables.put(fixTableName(session, testFirstMergeTable), getLiveColumnInfo(session, fixTableName(session, testFirstMergeTable)));
			_allTables.put(fixTableName(session, testSecondMergeTable), getLiveColumnInfo(session, fixTableName(session, testSecondMergeTable)));
			super._dialect = DialectFactory.getDialect(session.getMetaData());
		}
		
		
		public String[] generateSQLStatements() throws UserCancelledOperationException, SQLException
		{
			return super.generateSQLStatements();
		}
   }
   
   private class DatabaseObjectInfoHelper implements IDatabaseObjectInfo {

   	private String _catalog = null;
   	private String _schema = null;
   	private String _simpleName = null;
   	private String _qualName = null;
   	private DatabaseObjectType _type = null;
   	
   	
   	public DatabaseObjectInfoHelper(String catalog, String schema, String simpleName, String qualName,
			DatabaseObjectType type)
		{
			_catalog = catalog;
			_schema = schema;
			_simpleName = simpleName;
			_qualName = qualName;
			_type = type;
		}
   	
		public String getCatalogName() { return _catalog; }
		public DatabaseObjectType getDatabaseObjectType() { return _type; }
		public String getQualifiedName() { return _qualName; }
		public String getSchemaName() { return _schema; }
		public String getSimpleName() { return _simpleName; }
		public int compareTo(IDatabaseObjectInfo o) { return 0; }
   	
   }
   
   
   
}
