package net.sourceforge.squirrel_sql.fw.sql;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import net.sourceforge.squirrel_sql.BaseSQuirreLTestCase;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.DTProperties;
import net.sourceforge.squirrel_sql.mo.sql.MockResultSet;

import com.mockobjects.sql.MockResultSetMetaData;

public class ResultSetReaderTest extends BaseSQuirreLTestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testReadRow() throws SQLException {
        MockResultSet rs = new MockResultSet();
        Date d = Calendar.getInstance().getTime();
        DTProperties props = new DTProperties();
        props.setDataArray(new String[] {"DataTypeDate readDateAsTimestamp=true"});

        rs.addRow(new Object[]{ new Integer(1), d });      
        // TODO: Need to set the ResultSetMetaData in rs here
        MockResultSetMetaData rsmd = new MockResultSetMetaData();
        rsmd.setupAddColumnTypes(new int[] { 4, 91 });
        rsmd.setupGetColumnCount(2);
        rs.setMetaData(rsmd);
        
        ResultSetReader reader = new ResultSetReader(rs);
        Object[] result = reader.readRow();
        if (result[1] instanceof Timestamp) {
            // 
        } else {
            fail("result[1] not a timestamp: "+result[1].getClass().getName());
        }
        
    }
}
