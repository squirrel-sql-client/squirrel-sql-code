package net.sourceforge.squirrel_sql.fw.util;

import java.io.IOException;
import java.net.URL;

import net.sourceforge.squirrel_sql.BaseSQuirreLTestCase;
import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.test.TestUtil;

public class MyURLClassLoaderTest extends BaseSQuirreLTestCase {

    private static String filePrefix = "file:";
    
    private static String COMMONS_CLI_JAR = 
        "squirrel-sql-dist/squirrel-sql/core/dist/lib/commons-cli.jar";
    
    private static String DBCOPY_JAR =
        "squirrel-sql-dist/squirrel-sql/plugins/dbcopy/dist/dbcopy.jar";

    private static String BOGUS_ZIP_FILE = 
        "squirrel-sql-dist/squirrel-sql/core/dist/log4j.properties";

    private static String EXTERNAL_DEPENDS_JAR = 
        "plugins/syntax/lib/syntax.jar";
    
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

    /**
     * Try to locate the file using different prefixes so that the test can be
     * run from the ant script, or from in Eclipse.
     * 
     * @param filename
     * @return
     */
    private String getFilePrefixedFilename(String filename) {
        String distDir = 
            TestUtil.findAncestorSquirrelSqlDistDirBase("squirrel-sql-dist");
        if (distDir == null) {
            throw new IllegalStateException("Couldn't locate distDir (squirrel-sql-dist)");
        }
        String result = filePrefix + distDir + filename;
        System.out.println("Found file: "+result);
        return result;
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
        getIPluginAssignableClasses(getFilePrefixedFilename(COMMONS_CLI_JAR));
    }

    public void testMyURLClassLoaderURLArray() {
        try {
            URL url = new URL(getFilePrefixedFilename(DBCOPY_JAR));
            MyURLClassLoader loader = new MyURLClassLoader(new URL[] {url});
            loader.findClass(DBCOPY_PLUGIN_CLASS);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }                
    }

    // Method Tests
    
    public void testAddClassLoaderListener() {
        MyClassLoaderListener listener = new MyClassLoaderListener();
        MyURLClassLoader loader = getLoader(getFilePrefixedFilename(COMMONS_CLI_JAR));
        loader.addClassLoaderListener(listener);
    }

    public void testRemoveClassLoaderListener() {
        MyClassLoaderListener listener = new MyClassLoaderListener();
        MyURLClassLoader loader = getLoader(getFilePrefixedFilename(DBCOPY_JAR));
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

    @SuppressWarnings("unchecked")
    public void testGetAssignableClasses() {
        Class[] classes = getIPluginAssignableClasses(getFilePrefixedFilename(COMMONS_CLI_JAR));
        assertEquals(0, classes.length);
        
        classes = getIPluginAssignableClasses(getFilePrefixedFilename(DBCOPY_JAR));
        assertEquals(1, classes.length);
        
        classes = getIPluginAssignableClasses(getFilePrefixedFilename(BOGUS_ZIP_FILE));
        assertEquals(0, classes.length);
        
        classes = getIPluginAssignableClasses(getFilePrefixedFilename(EXTERNAL_DEPENDS_JAR));
        assertEquals(0, classes.length);
        
    }

    public void testFindClassString() {
        MyURLClassLoader loader = getLoader(getFilePrefixedFilename(COMMONS_CLI_JAR));
        try {
            loader.findClass(COMMONS_CLI_OPTION_CLASS);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    public void testClassHasBeenLoaded() {
        try {
            MyURLClassLoader loader = getLoader(getFilePrefixedFilename(COMMONS_CLI_JAR));
            loader.findClass(COMMONS_CLI_OPTION_CLASS);
            loader.classHasBeenLoaded(org.apache.commons.cli.Option.class);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    // HELPERS
    @SuppressWarnings("unchecked")    
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
