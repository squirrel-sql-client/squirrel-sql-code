package net.sourceforge.squirrel_sql;

import net.sourceforge.squirrel_sql.client.ApplicationManager;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import org.apache.log4j.Level;

public class BaseSQuirreLJUnit4TestCase {

    static {
        ApplicationManager.initApplication();        
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
