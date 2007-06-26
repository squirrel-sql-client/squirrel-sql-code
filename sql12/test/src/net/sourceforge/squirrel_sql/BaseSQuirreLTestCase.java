package net.sourceforge.squirrel_sql;

import junit.framework.TestCase;
import net.sourceforge.squirrel_sql.client.ApplicationManager;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import org.apache.log4j.Level;

public class BaseSQuirreLTestCase extends TestCase {

    protected void setUp() throws Exception {
        ApplicationManager.initApplication();
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    protected static void disableLogging(Class c) {
        ILogger s_log = LoggerController.createLogger(c);
        s_log.setLevel(Level.OFF);        
    }
    
    protected static void debugLogging(Class c) {
        ILogger s_log = LoggerController.createLogger(c);
        s_log.setLevel(Level.DEBUG);        
    }
    
}
