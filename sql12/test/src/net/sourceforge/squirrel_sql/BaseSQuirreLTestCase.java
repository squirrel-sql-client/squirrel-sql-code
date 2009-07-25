package net.sourceforge.squirrel_sql;

import junit.framework.TestCase;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import org.apache.log4j.Level;

public abstract class BaseSQuirreLTestCase extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
    @SuppressWarnings("unchecked")
    protected static void disableLogging(Class c) {
        ILogger s_log = LoggerController.createLogger(c);
        s_log.setLevel(Level.OFF);        
    }
    
    @SuppressWarnings("unchecked")
    protected static void debugLogging(Class c) {
        ILogger s_log = LoggerController.createLogger(c);
        s_log.setLevel(Level.DEBUG);        
    }
    
}
