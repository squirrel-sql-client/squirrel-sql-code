package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.whereClause;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.*;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.DataTypeBoolean;

import org.junit.Before;
import org.junit.Test;

import utils.EasyMockHelper;

/**
 * Tests the behavior of an {@link IsNullWhereClausePart}
 * @author Stefan Willinger
 *
 */
public class IsNullWhereClausePartTest extends BaseSQuirreLJUnit4TestCase{
	
	private ColumnDisplayDefinition mockColumn;
	private PreparedStatement mockPstmt;
	private IsNullWhereClausePart classUnderTest;
	
	@Before
	public void setUp(){
		mockColumn = mockHelper.createMock(ColumnDisplayDefinition.class);
		expect(mockColumn.getColumnName()).andStubReturn("myCol");
		mockPstmt = mockHelper.createMock(PreparedStatement.class);
		mockHelper.replayAll();
		classUnderTest = new IsNullWhereClausePart(mockColumn);
	}
	
	
	@Test(expected=IllegalStateException.class)
	public void testSetParameter() throws Exception {
		classUnderTest.setParameter(mockPstmt, 1);
	}

	@Test
	public void testShouldBeUsed() {
		assertTrue(classUnderTest.shouldBeUsed());
	}

	@Test()
	public void testGetWhereClause() {
		assertEquals("myCol is null",classUnderTest.getWhereClause());
	}

	@Test()
	public void testAppendToClause() {
		StringBuilder sb = new StringBuilder();

		classUnderTest.appendToClause(sb);
		assertEquals(" WHERE myCol is null", sb.toString());
		
		classUnderTest.appendToClause(sb);
		
		assertEquals(" WHERE myCol is null AND myCol is null", sb.toString());
	}

	@Test()
	public void testIsParameterUsed() {
		assertFalse(classUnderTest.isParameterUsed());
	}

}
