package net.sourceforge.squirrel_sql.fw.util;

import java.io.IOException;
import java.net.URL;

import net.sourceforge.squirrel_sql.BaseSQuirreLTestCase;
import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class MyURLClassLoaderTest extends BaseSQuirreLTestCase {

    private static final String COMMONS_CLI_JAR_URL = 
        "file:../squirrel-sql-dist/squirrel-sql/core/dist/lib/commons-cli.jar";
    
    private static final String DBCOPY_JAR_URL =
        "file:../squirrel-sql-dist/squirrel-sql/plugins/dbcopy/dist/dbcopy.jar";

    private static final String BOGUS_ZIP_FILE_URL = 
        "file:../squirrel-sql-dist/squirrel-sql/core/dist/log4j.properties";

    private static final String EXTERNAL_DEPENDS_JAR_URL = 
        "file:./plugins/syntax/lib/syntax.jar";
    
    private static final String COMMONS_CLI_OPTION_CLASS = 
        "org.apache.commons.cli.Option";

    private static final String DBCOPY_PLUGIN_CLASS = 
        "net.sourceforge.squirrel_sql.plugins.dbcopy.DBCopyPlugin";

    
    /** Logger for this class. */
    private final static ILogger s_log = 
        LoggerController.createLogger(MyURLClassLoaderTest.class);  
    
    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    // Constructor Tests
    
    public void testMyURLClassLoaderString() {
        try {
            MyURLClassLoader loader = new MyURLClassLoader(".");
            loader.getAssignableClasses(IPlugin.class, s_log);
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    public void testMyURLClassLoaderURL() {
        getIPluginAssignableClasses(COMMONS_CLI_JAR_URL);
    }

    public void testMyURLClassLoaderURLArray() {
        try {
            URL url = new URL(DBCOPY_JAR_URL);
            MyURLClassLoader loader = new MyURLClassLoader(new URL[] {url});
            loader.findClass(DBCOPY_PLUGIN_CLASS);
        } catch (Exception e) {
            fail(e.getMessage());
        }                
    }

    // Method Tests
    
    public void testAddClassLoaderListener() {
        MyClassLoaderListener listener = new MyClassLoaderListener();
        MyURLClassLoader loader = getLoader(COMMONS_CLI_JAR_URL);
        loader.addClassLoaderListener(listener);
    }

    public void testRemoveClassLoaderListener() {
        MyClassLoaderListener listener = new MyClassLoaderListener();
        MyURLClassLoader loader = getLoader(DBCOPY_JAR_URL);
        loader.addClassLoaderListener(listener);
        try {
            loader.getAssignableClasses(IPlugin.class, s_log);
        } catch (Exception e) {
            // fail ??
        }
        assertEquals(1, listener.loadingZipFileCount);
        listener.loadingZipFileCount = 0;
        loader.removeClassLoaderListener(listener);
        
        try {
            loader.getAssignableClasses(IPlugin.class, s_log);
        } catch (Exception e) {
            // fail ??
        }
        assertEquals(0, listener.loadingZipFileCount);
    }

    public void testGetAssignableClasses() {
        Class[] classes = getIPluginAssignableClasses(COMMONS_CLI_JAR_URL);
        assertEquals(0, classes.length);
        
        classes = getIPluginAssignableClasses(DBCOPY_JAR_URL);
        assertEquals(1, classes.length);
        
        classes = getIPluginAssignableClasses(BOGUS_ZIP_FILE_URL);
        assertEquals(0, classes.length);
        
        classes = getIPluginAssignableClasses(EXTERNAL_DEPENDS_JAR_URL);
        assertEquals(0, classes.length);
        
    }

    public void testFindClassString() {
        MyURLClassLoader loader = getLoader(COMMONS_CLI_JAR_URL);
        try {
            loader.findClass(COMMONS_CLI_OPTION_CLASS);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    public void testClassHasBeenLoaded() {
        try {
            MyURLClassLoader loader = getLoader(COMMONS_CLI_JAR_URL);
            loader.findClass(COMMONS_CLI_OPTION_CLASS);
            loader.classHasBeenLoaded(org.apache.commons.cli.Option.class);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    // HELPERS
    
    private Class[] getIPluginAssignableClasses(String urlToSearch) {
        MyURLClassLoader loader = getLoader(urlToSearch);
        return loader.getAssignableClasses(IPlugin.class, s_log);
    }
    
    private MyURLClassLoader getLoader(String urlToSearch) {
        MyURLClassLoader result = null;
        try {
            URL url = new URL(urlToSearch);
            MyURLClassLoader loader = new MyURLClassLoader(url);
            return loader;
        } catch (Exception e) {
            fail(e.getMessage());
        }                
        return result;
    }
    
    private static class MyClassLoaderListener implements ClassLoaderListener {

        public int loadingZipFileCount = 0;
        
        /* (non-Javadoc)
         * @see net.sourceforge.squirrel_sql.fw.util.ClassLoaderListener#finishedLoadingZipFiles()
         */
        public void finishedLoadingZipFiles() {
            // TODO Auto-generated method stub
            
        }

        /* (non-Javadoc)
         * @see net.sourceforge.squirrel_sql.fw.util.ClassLoaderListener#loadedZipFile(java.lang.String)
         */
        public void loadedZipFile(String filename) {
            loadingZipFileCount++;
        }
        
    }
}
