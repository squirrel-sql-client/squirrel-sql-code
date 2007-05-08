package net.sourceforge.squirrel_sql.plugins.dbcopy.util;

import static org.easymock.EasyMock.createNiceMock;
import net.sourceforge.squirrel_sql.BaseSQuirreLTestCase;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;


public class DBUtilTest extends BaseSQuirreLTestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testGetForeignKeySQL() throws Exception {
        ITableInfo ti = createNiceMock(ITableInfo.class);
        DBUtil.getForeignKeySQL(null, ti, null);
    }

}
