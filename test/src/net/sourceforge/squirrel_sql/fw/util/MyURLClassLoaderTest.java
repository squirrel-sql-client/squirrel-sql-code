package net.sourceforge.squirrel_sql.fw.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import net.sourceforge.squirrel_sql.BaseSQuirreLTestCase;
import net.sourceforge.squirrel_sql.fw.sql.IQueryTokenizer;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class MyURLClassLoaderTest extends BaseSQuirreLTestCase
{

	private static String COMMONS_CLI_JAR = "http://www.squirrelsql.org/downloads/commons-cli.jar";

	private static FileWrapper commonsCliTempFileWrapper = null;

	private static String ORACLE_PLUGIN_JAR = "http://www.squirrelsql.org/downloads/oracle.jar";

	private static FileWrapper oracleTempFileWrapper = null;

	private static String BOGUS_ZIP_FILE = "http://www.squirrelsql.org/downloads/log4j.properties";

	private static FileWrapper bogusZipTempFileWrapper = null;

	private static String EXTERNAL_DEPENDS_JAR = "http://www.squirrelsql.org/downloads/syntax.jar";

	private static FileWrapper extDependsTempFileWrapper = null;

	private static final String COMMONS_CLI_OPTION_CLASS = "org.apache.commons.cli.Option";

	private static final String ORACLE_JAR_CLASS =
		"net.sourceforge.squirrel_sql.plugins.oracle.common.AutoWidthResizeTable";

	private static final IOUtilities ioutils = new IOUtilitiesImpl();

	/** Logger for this class. */
	private final static ILogger s_log = LoggerController.createLogger(MyURLClassLoaderTest.class);

	protected void setUp() throws Exception
	{
		super.setUp();
		if (oracleTempFileWrapper == null)
		{
			downloadTestFiles();
		}
	}

	protected void tearDown() throws Exception
	{
		super.tearDown();
		oracleTempFileWrapper.deleteOnExit();
		bogusZipTempFileWrapper.deleteOnExit();
		extDependsTempFileWrapper.deleteOnExit();
		commonsCliTempFileWrapper.deleteOnExit();
	}

	// Constructor Tests

	public void testMyURLClassLoaderString()
	{
		try
		{
			MyURLClassLoader loader = new MyURLClassLoader(".");
			loader.getAssignableClasses(IQueryTokenizer.class, s_log);
		}
		catch (IOException e)
		{
			fail(e.getMessage());
		}
	}

	public void testMyURLClassLoaderURL() throws Exception
	{
		getIQueryTokenizerAssignableClasses(commonsCliTempFileWrapper.toURL().toString());
	}

	private void downloadTestFiles()
	{
		disableLogging(org.apache.commons.logging.impl.SimpleLog.class);
		disableLogging(org.apache.commons.httpclient.HttpMethodBase.class);
		disableLogging(org.apache.commons.httpclient.HttpConnection.class);
		disableLogging(org.apache.commons.httpclient.HttpClient.class);
		disableLogging(org.apache.commons.httpclient.params.DefaultHttpParams.class);
		disableLogging(org.apache.commons.httpclient.methods.GetMethod.class);
		disableLogging(org.apache.commons.httpclient.HttpState.class);
		disableLogging(org.apache.commons.httpclient.util.HttpURLConnection.class);
		commonsCliTempFileWrapper = downloadTestFile(COMMONS_CLI_JAR, "commons-cli", ".jar");
		oracleTempFileWrapper = downloadTestFile(ORACLE_PLUGIN_JAR, "oracle", ".jar");
		bogusZipTempFileWrapper = downloadTestFile(BOGUS_ZIP_FILE, "log4j", ".properties");
		extDependsTempFileWrapper = downloadTestFile(EXTERNAL_DEPENDS_JAR, "syntax", ".jar");
	}

	private FileWrapper downloadTestFile(String urlStr, String localTempFilePrefix, String localTempFileSuffix)
	{
		InputStream is = null;
		FileWrapper result = null;
		try
		{
			URL url = new URL(urlStr);
			is = url.openStream();
			String prefix = "MyURLClassLoaderTest-" + localTempFilePrefix;
			result = FileWrapperImpl.createTempFile(prefix, localTempFileSuffix);
			final int bytesRead = ioutils.downloadHttpFile(url, result, null);
			assertTrue(bytesRead > 0);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
		finally
		{
			ioutils.closeInputStream(is);
		}
		return result;
	}

	public void testMyURLClassLoaderURLArray()
	{
		try
		{
			MyURLClassLoader loader = new MyURLClassLoader(new URL[] { oracleTempFileWrapper.toURL() });
			loader.findClass(ORACLE_JAR_CLASS);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	// Method Tests

	public void testAddClassLoaderListener() throws Exception
	{
		MyClassLoaderListener listener = new MyClassLoaderListener();
		MyURLClassLoader loader = getLoader(commonsCliTempFileWrapper.toURL().toString());
		loader.addClassLoaderListener(listener);
	}

	public void testRemoveClassLoaderListener() throws Exception
	{
		MyClassLoaderListener listener = new MyClassLoaderListener();
		MyURLClassLoader loader = getLoader(oracleTempFileWrapper.toURL().toString());
		loader.addClassLoaderListener(listener);
		loader.getAssignableClasses(IQueryTokenizer.class, s_log);
		assertEquals(1, listener.loadingZipFileCount);
		listener.loadingZipFileCount = 0;
		loader.removeClassLoaderListener(listener);
		loader.getAssignableClasses(IQueryTokenizer.class, s_log);
		assertEquals(0, listener.loadingZipFileCount);
	}

	@SuppressWarnings("unchecked")
	public void testGetAssignableClasses() throws Exception
	{
		Class[] classes = getIQueryTokenizerAssignableClasses(commonsCliTempFileWrapper.toURL().toString());
		assertEquals(0, classes.length);

		classes = getIQueryTokenizerAssignableClasses(oracleTempFileWrapper.toURL().toString());
		assertEquals(1, classes.length);

		classes = getIQueryTokenizerAssignableClasses(bogusZipTempFileWrapper.toURL().toString());
		assertEquals(0, classes.length);

		classes = getIQueryTokenizerAssignableClasses(extDependsTempFileWrapper.toURL().toString());
		assertEquals(0, classes.length);

	}

	public void testFindClassString() throws Exception
	{
		MyURLClassLoader loader = getLoader(commonsCliTempFileWrapper.toURL().toString());
		try
		{
			loader.findClass(COMMONS_CLI_OPTION_CLASS);
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}

	public void testClassHasBeenLoaded()
	{
		try
		{
			MyURLClassLoader loader = getLoader(commonsCliTempFileWrapper.toURL().toString());
			loader.findClass(COMMONS_CLI_OPTION_CLASS);
			loader.classHasBeenLoaded(org.apache.commons.cli.Option.class);
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}

	// HELPERS
	@SuppressWarnings("unchecked")
	private Class[] getIQueryTokenizerAssignableClasses(String urlToSearch)
	{
		MyURLClassLoader loader = getLoader(urlToSearch);
		return loader.getAssignableClasses(IQueryTokenizer.class, s_log);
	}

	private MyURLClassLoader getLoader(String urlToSearch)
	{
		MyURLClassLoader result = null;
		try
		{
			URL url = new URL(urlToSearch);
			MyURLClassLoader loader = new MyURLClassLoader(url);
			return loader;
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
		return result;
	}

	private static class MyClassLoaderListener implements ClassLoaderListener
	{

		public int loadingZipFileCount = 0;

		/**
		 * @see net.sourceforge.squirrel_sql.fw.util.ClassLoaderListener#finishedLoadingZipFiles()
		 */
		public void finishedLoadingZipFiles()
		{
		}

		/**
		 * @see net.sourceforge.squirrel_sql.fw.util.ClassLoaderListener#loadedZipFile(java.lang.String)
		 */
		public void loadedZipFile(String filename)
		{
			loadingZipFileCount++;
		}

	}
}
