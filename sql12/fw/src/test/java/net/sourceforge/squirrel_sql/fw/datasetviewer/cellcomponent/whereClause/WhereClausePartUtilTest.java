package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.whereClause;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponent;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the behavior of an {@link WhereClausePartUtil}
 * @author Stefan Willinger
 *
 */
public class WhereClausePartUtilTest extends BaseSQuirreLJUnit4TestCase{
	
	private IWhereClausePart nopWhereClause;
	private IWhereClausePart isNullWhereClause;
	private IWhereClausePart noParameterWhereClause;
	private IWhereClausePart parameterWhereClause;
	private IWhereClausePart anotherParameterWhereClause;
	
	private ColumnDisplayDefinition mockColumn;
	private PreparedStatement mockPstmt;
	private IDataTypeComponent mockLongDataTypeComponent;
	private IDataTypeComponent mockStringDataTypeComponent;
	
	private IWhereClausePartUtil whereClausePartUtil = new WhereClausePartUtil();
	
	@Before
	public void setUp(){
		mockColumn = mockHelper.createMock(ColumnDisplayDefinition.class);
		expect(mockColumn.getColumnName()).andStubReturn("myCol");
		mockPstmt = mockHelper.createMock(PreparedStatement.class);
		mockLongDataTypeComponent = mockHelper.createMock(IDataTypeComponent.class);
		mockStringDataTypeComponent = mockHelper.createMock(IDataTypeComponent.class);
		try {
			mockLongDataTypeComponent.setPreparedStatementValue(mockPstmt, new Long(5), 2);
			mockStringDataTypeComponent.setPreparedStatementValue(mockPstmt, "austria", 3);
		} catch (SQLException e) {
			// not possible
		}
		
		
		mockHelper.replayAll();
		
		nopWhereClause = new EmptyWhereClausePart();
		isNullWhereClause = new IsNullWhereClausePart(mockColumn);
		noParameterWhereClause = new NoParameterWhereClausePart(mockColumn, "myCol like 'ham%eggs'");
		parameterWhereClause = new ParameterWhereClausePart(mockColumn, new Long(5), mockLongDataTypeComponent);
		anotherParameterWhereClause = new ParameterWhereClausePart(mockColumn, "austria", mockStringDataTypeComponent);
		
	}
	
	
	
	@Test
	public void testCreateWhereClause(){
		List<IWhereClausePart> parts = new ArrayList<IWhereClausePart>();
		parts.add(nopWhereClause);
		parts.add(isNullWhereClause);
		parts.add(noParameterWhereClause);
		parts.add(parameterWhereClause);
		parts.add(anotherParameterWhereClause);
		
		String whereClause = whereClausePartUtil.createWhereClause(parts);
		
		assertEquals(" WHERE myCol is null AND myCol like 'ham%eggs' AND myCol = ? AND myCol = ?", whereClause);
		
	}
	
	
	@Test
	public void testHasUsableWhereClause(){
		
		List<IWhereClausePart> parts = new ArrayList<IWhereClausePart>();
		parts.add(nopWhereClause);
		assertFalse(whereClausePartUtil.hasUsableWhereClause(parts));
		
		parts.add(isNullWhereClause);
		assertTrue(whereClausePartUtil.hasUsableWhereClause(parts));
	}
	
	@Test
	public void testSetParameters() throws SQLException{
		List<IWhereClausePart> parts = new ArrayList<IWhereClausePart>();
		parts.add(nopWhereClause);
		parts.add(isNullWhereClause);
		parts.add(noParameterWhereClause);
		parts.add(parameterWhereClause);
		parts.add(anotherParameterWhereClause);
		
		int nextIndex = whereClausePartUtil.setParameters(mockPstmt, parts , 2);
		assertEquals(4, nextIndex);
		EasyMock.verify(mockLongDataTypeComponent);
		EasyMock.verify(mockStringDataTypeComponent);
		
	}

}
