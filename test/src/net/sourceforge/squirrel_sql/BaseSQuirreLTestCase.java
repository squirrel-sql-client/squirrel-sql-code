package net.sourceforge.squirrel_sql;

import net.sourceforge.squirrel_sql.client.ApplicationManager;
import junit.framework.TestCase;

public class BaseSQuirreLTestCase extends TestCase {

    static {
        ApplicationManager.initApplication();        
    }
    
    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

}
