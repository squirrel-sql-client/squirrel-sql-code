package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.whereClause;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.*;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.DataTypeBoolean;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponent;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import utils.EasyMockHelper;

/**
 * Tests the behavior of an {@link NoParameterWhereClausePart}
 * @author Stefan Willinger
 *
 */
public class ParameterWhereClausePartTest extends BaseSQuirreLJUnit4TestCase{
	
	private ColumnDisplayDefinition mockColumn;
	private PreparedStatement mockPstmt;
	private ParameterWhereClausePart classUnderTest;
	private IDataTypeComponent mockDataType;
	private final String expectedWhere = "myCol = ?";
	
	private static String value = "I am an value of the parameter";
	
	
	@Before
	public void setUp(){
		mockColumn = mockHelper.createMock(ColumnDisplayDefinition.class);
		expect(mockColumn.getColumnName()).andStubReturn("myCol");
		mockPstmt = mockHelper.createMock(PreparedStatement.class);
		mockDataType = mockHelper.createMock(IDataTypeComponent.class);
		try {
			mockDataType.setPreparedStatementValue(mockPstmt, value, 1);
		} catch (SQLException e) {
			// not possible
		}
		mockHelper.replayAll();
		classUnderTest = new ParameterWhereClausePart(mockColumn,value, mockDataType);
	}
	
	
	@Test
	public void testSetParameter() throws Exception {
		classUnderTest.setParameter(mockPstmt, 1);
	}
	
	
	@Test
	public void testShouldBeUsed() {
		assertTrue(classUnderTest.shouldBeUsed());
	}

	@Test()
	public void testGetWhereClause() {
		assertEquals(expectedWhere,classUnderTest.getWhereClause());
	}

	/**
	 * Ensure the concatenation as an AND-Clause
	 */
	@Test()
	public void testAppendToClause() {
		StringBuilder sb = new StringBuilder();

		classUnderTest.appendToClause(sb);
		assertEquals(" WHERE "+ expectedWhere, sb.toString());
		
		classUnderTest.appendToClause(sb);
		
		assertEquals(" WHERE "+ expectedWhere + " AND "+expectedWhere, sb.toString());
	}

	@Test()
	public void testIsParameterUsed() {
		assertTrue(classUnderTest.isParameterUsed());
	}
	/**
	 * An {@link ParameterWhereClausePart} cannot be constructed with an null Value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testNullValue(){
		new ParameterWhereClausePart(mockColumn, null, mockDataType);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testNullColumn(){
		new ParameterWhereClausePart(null, "value", mockDataType);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testNullDataType(){
		new ParameterWhereClausePart(mockColumn, "value", null);
	}

}
