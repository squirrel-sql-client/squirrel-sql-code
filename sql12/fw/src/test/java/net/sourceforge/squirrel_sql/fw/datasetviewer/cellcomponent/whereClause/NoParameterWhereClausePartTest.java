package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.whereClause;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.PreparedStatement;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests the behavior of an {@link NoParameterWhereClausePart}
 * @author Stefan Willinger
 *
 */
public class NoParameterWhereClausePartTest extends BaseSQuirreLJUnit4TestCase{
	
	private static final String WHERE_PART = "myCol like 'ham%eggs'";
	private ColumnDisplayDefinition mockColumn;
	private PreparedStatement mockPstmt;
	private NoParameterWhereClausePart classUnderTest;
	
	@Before
	public void setUp(){
		mockColumn = mockHelper.createMock(ColumnDisplayDefinition.class);
		expect(mockColumn.getColumnName()).andStubReturn("myCol");
		mockPstmt = mockHelper.createMock(PreparedStatement.class);
		mockHelper.replayAll();
		classUnderTest = new NoParameterWhereClausePart(mockColumn, WHERE_PART);
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
		assertEquals(WHERE_PART,classUnderTest.getWhereClause());
	}

	@Test()
	public void testAppendToClause() {
		StringBuilder sb = new StringBuilder();

		classUnderTest.appendToClause(sb);
		assertEquals(" WHERE "+WHERE_PART, sb.toString());
		
		classUnderTest.appendToClause(sb);
		
		assertEquals(" WHERE "+ WHERE_PART + " AND "+WHERE_PART, sb.toString());
	}

	@Test()
	public void testIsParameterUsed() {
		assertFalse(classUnderTest.isParameterUsed());
	}

}
