package net.sourceforge.squirrel_sql.client.session;

import static org.easymock.EasyMock.createMock;
import junit.framework.TestCase;

public class SQLExecuterTaskTest extends TestCase {

    private ISession session = null;
    
    protected void setUp() throws Exception {
        super.setUp();
        session = createMock(ISession.class);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testNullSQL() {
        SQLExecuterTask task = new SQLExecuterTask(session, null, null, null);
        task.run();
    }
}
