package net.sourceforge.squirrel_sql.fw.sql;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;

import net.sourceforge.squirrel_sql.BaseSQuirreLTestCase;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.DTProperties;
import net.sourceforge.squirrel_sql.mo.sql.MockResultSet;

import com.mockobjects.sql.MockResultSetMetaData;

public class ResultSetReaderTest extends BaseSQuirreLTestCase {

    private static final String dateClassName = 
        "net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.DataTypeDate";
    
    protected void setUp() throws Exception {
        super.setUp();
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
        if (readDatePropVal != null) {
            DTProperties.put(dateClassName, "readDateAsTimestamp", readDatePropVal);
        }
        ResultSetReader reader = getDateResultSetReader();
        Object[] result = reader.readRow();
        if (result[1].getClass().getName().equals(type)) {
            // 
        } else {
            fail("result[1] not a Date: "+result[1].getClass().getName());
        }                
    }
    
    private ResultSetReader getDateResultSetReader() throws SQLException {
        MockResultSet rs = new MockResultSet();
        Date d = new Date(Calendar.getInstance().getTimeInMillis());
        
        rs.addRow(new Object[]{ new Integer(1), d });      
        MockResultSetMetaData rsmd = new MockResultSetMetaData();
        rsmd.setupAddColumnTypes(new int[] { 4, 91 });
        rsmd.setupGetColumnCount(2);
        rs.setMetaData(rsmd);
        
        ResultSetReader reader = new ResultSetReader(rs);
        return reader;
    }
    
    
}
