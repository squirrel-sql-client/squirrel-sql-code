package net.sourceforge.squirrel_sql.fw.datasetviewer;

import static org.easymock.EasyMock.anyInt;
import static org.easymock.EasyMock.expect;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import org.easymock.EasyMock;

import utils.EasyMockHelper;
import junit.framework.TestCase;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.mo.sql.MockResultSet;



public class TableColumnsDataSetTest extends TestCase {

	private TableColumnsDataSet iut = null;
	
	
	EasyMockHelper mockHelper = new EasyMockHelper();
	private ResultSet mockResultSet = null;
	
    private static int[] columnIndices = 
        new int[] { 4, 6, 18, 9, 7, 13, 12, 5, 8, 10, 11, 14, 15, 16, 17 };
	
	
	
	protected void setUp() throws Exception {
		super.setUp();
		mockResultSet = mockHelper.createMock(ResultSet.class);		
		
	}

	public void testGetColumnCount() {
		//fail("Not yet implemented");
	}

	public void testGetDataSetDefinition() {
		//fail("Not yet implemented");
	}

	public void testNext() {
		//fail("Not yet implemented");
	}

	/**
	 * Test the get method to see if it returns the correct value for the 
	 * DATA_TYPE column.  It should have the JDBC type name in brackets next to
	 * the jdbc type code.
	 *
	 */
	public void testGet() throws Exception {
		
		ResultSetMetaData mockMetaData = mockHelper.createMock(ResultSetMetaData.class);
		expect(mockResultSet.getMetaData()).andReturn(mockMetaData);
		expect(mockMetaData.getColumnDisplaySize(anyInt())).andReturn(5).anyTimes();
		expect(mockMetaData.getColumnLabel(EasyMock.anyInt())).andReturn("foo").anyTimes();
		expect(mockResultSet.next()).andReturn(true);
		expect(mockResultSet.next()).andReturn(false);
		expect(mockResultSet.getString(anyInt())).andReturn("aStringValue").anyTimes();
		expect(mockResultSet.getInt(anyInt())).andReturn(-5).anyTimes();
		mockResultSet.close();
		
		mockHelper.replayAll();
		
		iut = new TableColumnsDataSet(mockResultSet, columnIndices);
		
		try {
			if (iut.next(null)) {
				String value = String.valueOf(iut.get(7));
				assertEquals("-5 [BIGINT]", value);
			} else {
				fail("expected next");
			}
		} catch (DataSetException e) {
			fail("Unexpected exception: "+e.getMessage());
		}
		
		mockHelper.verifyAll();
	}

}
