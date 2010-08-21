
package net.sourceforge.squirrel_sql.plugins.dbcopy;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

/**
 * A description of this class goes here...
 */

public abstract class I18NBaseObject {

    
    /** Internationalized strings for this class. */
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(I18NBaseObject.class);
    
    /**
     * 
     * @param key
     * @return
     */
    protected static String getMessage(String key) {
        return s_stringMgr.getString(key);
    }

    /**
     * 
     * @param key
     * @return
     */
    protected static String getMessage(String key, Object arg) {
        return s_stringMgr.getString(key, arg);
    }   

    /**
     * 
     * @param key
     * @return
     */
    protected static String getMessage(String key, Object[] args) {
        return s_stringMgr.getString(key, args);
    }      
    

}
