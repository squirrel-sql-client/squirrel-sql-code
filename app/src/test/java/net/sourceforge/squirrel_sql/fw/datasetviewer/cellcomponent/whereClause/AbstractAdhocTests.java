package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.whereClause;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.CellComponentFactory;
import net.sourceforge.squirrel_sql.fw.dialects.DialectType;
import net.sourceforge.squirrel_sql.fw.persist.ValidationException;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverManager;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Abstract Test, which ensures the functionality of {@link IWhereClausePart} 
 * with an specific DB-driver.
 * @author Stefan Willinger
 *
 */
public abstract class AbstractAdhocTests extends BaseSQuirreLJUnit4TestCase {

	private ISQLConnection sqlConn;
	private String user;
	private String pw;
	private SQLDriver sqlDriver;
	private String url;
	private DialectType dialect;
	private String driverClassName;
	private String dbName;
	private String[] jarFileNames;
	
	
	public AbstractAdhocTests(String user, String pw, String url, String driverClassName , DialectType dialect, String dbName, String[] jarFileNames) {
		this.user = user;
		this.pw = pw;
		this.url = url;
		this.dialect = dialect;
		this.driverClassName = driverClassName;
		this.dbName = dbName;
		this.jarFileNames = jarFileNames;
	}
	
	
	/**
	 * SetUp the test environment.
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception{
		initTestDriver();
		
		ISQLAlias mockAlias = mockHelper.createMock(ISQLAlias.class);
		EasyMock.expect(mockAlias.getUrl()).andReturn(url);
		mockHelper.replayAll();
		
		SQLDriverManager mg = new SQLDriverManager();
		
		sqlConn = mg.getConnection(sqlDriver, mockAlias, user, pw);
		sqlConn.setAutoCommit(false);
		
		assertNotNull(dialect);
		
	}
	
	protected void initTestDriver() throws ValidationException{
			sqlDriver = new SQLDriver();
			sqlDriver.setDriverClassName(driverClassName);
			sqlDriver.setName(dbName);
			sqlDriver.setUrl(url);
			sqlDriver.setJarFileNames(jarFileNames);
	}


	
	

	@Test
	public void testConnect(){
		assertNotNull(sqlConn);
	}
	
	/**
	 * Each row of the testtable has unique data.
	 * This test ensures, that we can find each row with the data provided, when we use {@link IWhereClausePart}. Escaping the special
	 * characters is done by the JDBC-Driver
	 * @throws Exception
	 */
	@Test
	public void testCount() throws Exception{
	
		String sql = "select * from basicTypes";
		
		ResultSet rs = sqlConn.createStatement().executeQuery(sql);
		
		
		List<ColumnDisplayDefinition> cols = new ArrayList<ColumnDisplayDefinition>();
		List<List<Object>> values = new ArrayList<List<Object>>();
		
		
		// Create ColumnDisplayDefinitions for each selected row, so that we can later create the where clause
		while (rs.next()) {
			int columnCount = rs.getMetaData().getColumnCount();
			if(cols.isEmpty()){
				for (int i = 1; i <= columnCount; i++) {
					cols.add(new ColumnDisplayDefinition(rs, i, dialect));
				}
			}
			List<Object> v = new ArrayList<Object>();
			for (int i = 1; i <= columnCount; i++) {
				v.add(CellComponentFactory.readResultSet(cols.get(i-1), rs, i, false));
			}
			values.add(v);
			
		}
		
		SQLDatabaseMetaData md = sqlConn.getSQLMetaData();

		
		/*
		 *  Create for each selected row a where clause and ensure that we find exactly the same row with this where clause
		 *  This is for testing the escape functionality of the jdbc-driver.
		 */
		for (List<Object> row : values) {
			Object[] anRow = row.toArray();
			List<IWhereClausePart> whereClause = new ArrayList<IWhereClausePart>();
			for (int i = 0; i < anRow.length; i++) {
				Object value = anRow[i];
				whereClause.add(CellComponentFactory.getWhereClauseValue(cols.get(i), value, md));
			}
			assertFalse(whereClause.isEmpty());
			
			
			IWhereClausePartUtil whereClausePartUtil = new WhereClausePartUtil();
			String where = whereClausePartUtil.createWhereClause(whereClause);
			
			String countSql = "select count(*) from basicTypes " + where;
			System.out.println(countSql);
			PreparedStatement pstmt = sqlConn.prepareStatement(countSql);
			whereClausePartUtil.setParameters(pstmt, whereClause, 1);
			ResultSet countRS = pstmt.executeQuery();
			countRS.next();
			assertEquals(1, countRS.getInt(1));
		}
	
		
	}
	
	
	@After
	public void tearDown() throws SQLException{
		sqlConn.rollback();
		sqlConn.close();
	}
}
