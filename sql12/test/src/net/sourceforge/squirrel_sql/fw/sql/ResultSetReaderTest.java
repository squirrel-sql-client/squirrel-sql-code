package net.sourceforge.squirrel_sql.fw.sql;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;

import net.sourceforge.squirrel_sql.BaseSQuirreLTestCase;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.DTProperties;

import org.easymock.EasyMock;

public class ResultSetReaderTest extends BaseSQuirreLTestCase {

    private static final String dateClassName = 
        "net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.DataTypeDate";

    
    ResultSet mockResultSet = null;
    ResultSetMetaData mockResultSetMetaData = null;
    
    ResultSetReader readerUnderTest = null;
    
    protected void setUp() throws Exception {
        super.setUp();
        mockResultSet = EasyMock.createMock(ResultSet.class);
        mockResultSetMetaData = EasyMock.createMock(ResultSetMetaData.class);
        expect(mockResultSet.getMetaData()).andReturn(mockResultSetMetaData);
        expect(mockResultSetMetaData.getColumnCount()).andReturn(1).atLeastOnce();
        expect(mockResultSet.next()).andReturn(true).once();
        expect(mockResultSetMetaData.getColumnType(1)).andReturn(91).anyTimes();
        expect(mockResultSetMetaData.getColumnTypeName(1)).andReturn("DATE").anyTimes();
        expect(mockResultSet.wasNull()).andReturn(false).anyTimes();
    }

    private void verifyAll() {
        verify(mockResultSet);
        verify(mockResultSetMetaData);        
        
    }
    
    private void replayAll() {
        replay(mockResultSet);
        replay(mockResultSetMetaData);        
    }
    
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testReadDateAsTimestamp() throws SQLException {
        testReadType(Timestamp.class.getName(), "true"); 
    }
    
    public void testReadDateAsDate() throws SQLException {
        testReadType(Date.class.getName(), "false");        
    }

    public void testReadDateAsDefault() throws SQLException {
        testReadType(Date.class.getName(), null);        
    }

    private void testReadType(String type,  String readDatePropVal) throws SQLException {
        DTProperties.put(dateClassName, "readDateAsTimestamp", readDatePropVal);
        
        long now = System.currentTimeMillis();
        if (type.equals(Date.class.getName())) {
            expect(mockResultSet.getDate(1)).andReturn(new java.sql.Date(now));
        }

        if (type.equals(Timestamp.class.getName())) {
            expect(mockResultSet.getTimestamp(1)).andReturn(new java.sql.Timestamp(now));
        }

        replayAll();
        readerUnderTest = new ResultSetReader(mockResultSet);
        Object[] result = readerUnderTest.readRow();
        if (result[0].getClass().getName().equals(type)) {
            // 
        } else {
            fail("result[0] not a Date: "+result[0].getClass().getName());
        }               
        verifyAll();
    }
    
}
