package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.whereClause;

import static org.junit.Assert.assertFalse;

import java.sql.PreparedStatement;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import org.junit.Test;

/**
 * Tests the behavior of an {@link EmptyWhereClausePart}
 * @author Stefan Willinger
 *
 */
public class EmptyWhereClausePartTest extends BaseSQuirreLJUnit4TestCase{
	private EmptyWhereClausePart classUnderTest = new EmptyWhereClausePart();
	private PreparedStatement mockPstmt = mockHelper.createMock(PreparedStatement.class);
	
	@Test(expected=IllegalStateException.class)
	public void testSetParameter() throws Exception {
		classUnderTest.setParameter(mockPstmt, 1);
	}

	@Test
	public void testShouldBeUsed() {
		assertFalse(classUnderTest.shouldBeUsed());
	}

	@Test(expected=IllegalStateException.class)
	public void testGetWhereClause() {
		classUnderTest.getWhereClause();
	}

	@Test(expected=IllegalStateException.class)
	public void testAppendToClause() {
		classUnderTest.appendToClause(new StringBuilder());
	}

	@Test(expected=IllegalStateException.class)
	public void testIsParameterUsed() {
		classUnderTest.isParameterUsed();
	}

}
