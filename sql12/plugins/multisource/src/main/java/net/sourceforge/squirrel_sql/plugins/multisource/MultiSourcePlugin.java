package net.sourceforge.squirrel_sql.plugins.multisource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

import javax.swing.JOptionPane;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.session.SessionInternalFrame;
import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallback;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallbackAdaptor;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.RefreshObjectTreeCommand;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.util.FileWrapper;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;


/**
 * MultiSourcePlugin allows a user to query multiple databases with one SQL query.
 */
public class MultiSourcePlugin extends DefaultSessionPlugin
{
	private PluginResources _resources;
	private static FileWrapper _userSettingsFolder;
	private static boolean isTrial;
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(MultiSourcePlugin.class);
	
	/**
	 * Return the internal name of this plugin.
	 *
	 * @return the internal name of this plugin.
	 */
	public String getInternalName()
	{
		return "multisource";
	}

	/**
	 * Return the descriptive name of this plugin.
	 *
	 * @return the descriptive name of this plugin.
	 */
	public String getDescriptiveName()
	{
		return "MultiSource Virtualization Plugin";
	}

	/**
	 * Returns the current version of this plugin.
	 *
	 * @return the current version of this plugin.
	 */
	public String getVersion()
	{
		return "1.0";
	}

	/**
	 * Returns the authors name.
	 *
	 * @return the authors name.
	 */
	public String getAuthor()
	{
		return "Ramon Lawrence";
	}

	/**
	 * Returns the name of the change log for the plugin. This should be a text or HTML file residing in the
	 * <TT>getPluginAppSettingsFolder</TT> directory.
	 *
	 * @return the changelog file name or <TT>null</TT> if plugin doesn't have a change log.
	 */
	public String getChangeLogFileName()
	{
		return "changes.txt";
	}

	/**
	 * Returns the name of the Help file for the plugin. This should be a text or HTML file residing in the
	 * <TT>getPluginAppSettingsFolder</TT> directory.
	 *
	 * @return the Help file name or <TT>null</TT> if plugin doesn't have a help file.
	 */
	public String getHelpFileName()
	{
		return "readme.html";
	}

	/**
	 * Returns the name of the Licence file for the plugin. This should be a text or HTML file residing in the
	 * <TT>getPluginAppSettingsFolder</TT> directory.
	 *
	 * @return the Licence file name or <TT>null</TT> if plugin doesn't have a licence file.
	 */
	public String getLicenceFileName()
	{
		return "licence.txt";
	}

	/**
	 * @return Comma separated list of contributors.
	 */
	public String getContributors()
	{
		return "Michael Henderson";
	}

	/**
	 * Create preferences panel for the Global Preferences dialog.
	 *
	 * @return Preferences panel.
	 */
	public IGlobalPreferencesPanel[] getGlobalPreferencePanels()
	{
		return new IGlobalPreferencesPanel[0];
	}

	/**
	 * Initialize this plugin.
	 */
	public synchronized void initialize() throws PluginException
	{
		_resources = new PluginResources("net.sourceforge.squirrel_sql.plugins.multisource.multisource", this);
		try {
			_userSettingsFolder = getPluginUserSettingsFolder();	// Retrieve the folder for user settings for storing connection info.
		}
		catch (Exception e)
		{	throw new PluginException(e); }
	}

	/**
	 * Called when a session started. Add commands to popup menu in object tree.
	 *
	 * @param session
	 *           The session that is starting.
	 * @return An implementation of PluginSessionCallback or null to indicate the plugin does not work with
	 *         this session
	 */
	public PluginSessionCallback sessionStarted(ISession session)
	{
		String dbName = null;

		// Determine if this is a Unity multisource session.
		try
		{
			if (session != null)
				dbName = session.getMetaData().getDatabaseProductName().toLowerCase();
		}
		catch (SQLException e) {}

		if (dbName != null && dbName.contains("unity"))
		{								
			IMessageHandler messageHandler = session.getApplication().getMessageHandler();
			MultiSqlExecutionListener sqlExecutionListener = new MultiSqlExecutionListener(messageHandler);
			
			session.getSessionSheet().getSQLPaneAPI().addSQLExecutionListener(sqlExecutionListener);
			// Load session configuration information if URL says virtual
			if (session.getAlias() != null) {
				isTrial = MultiSourcePlugin.isTrial(session.getSQLConnection().getConnection());
				String url = session.getAlias().getUrl();
				if (url.toLowerCase().indexOf("/virtual") > 0) {	
					// Try to load based on session name					
					Object schema = MultiSourcePlugin.getSchema(session.getSQLConnection().getConnection());		// Retrieve schema
					String filePath = getSourceFilePath(session);
					File file = new File(filePath);

					boolean loaded = true;

					// Load virtualization schema information from configuration files
					// When connection is first made with jdbc:unity://virtual there will be no schema information in the connection.
					// This code loads the schema information from the SQuirreL folder for the connection that stores the XML files (perhaps encrypted) with the required information.
					if (schema != null && file.exists()) {
						try {
							// Get method that loads sources file
							Method parseSourcesMethod = schema.getClass().getMethod("parseSourcesFile", new Class[]{java.io.BufferedReader.class, java.lang.String.class, java.util.Properties.class});
							
							// Get password from SQuirreL connection information as password (if present) is used to encrypt source and schema files
							String password = session.getAlias().getPassword();
							Properties info = new Properties();
							
							BufferedReader reader = null;
							if (password != null && password.length() > 0) {								
								info.setProperty("password", password);
								ClassLoader loader = session.getSQLConnection().getConnection().getClass().getClassLoader();
								Class<?> fileManagerClass = Class.forName("unity.io.FileManager", true, loader);
								
								// If password is present, setup method to decrypt input stream
								Method getDecryptedStreamMethod = fileManagerClass.getMethod("getDecryptedStream", new Class[] {java.lang.String.class, java.lang.String.class, java.util.Properties.class});
								Properties prop = new Properties();
								
								InputStream is = (InputStream)getDecryptedStreamMethod.invoke(null, new Object[] {filePath, password, prop});
								reader = new BufferedReader(new InputStreamReader(is));
							} else {
								reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), Charset.forName("UTF-8")));
							}
							
							// Parse the sources file (list of sources in the virtualization)
							parseSourcesMethod.invoke(schema, new Object[]{reader, "jdbc:unity://"+filePath, info});
							
							new UpdateThread(session).start();	// A poor solution to force the object tree to update after a slight delay
						}
						catch (Exception e) {
							JOptionPane.showMessageDialog(null, s_stringMgr.getString("MultiSourcePlugin.loadFailed"));
							System.out.println(e);
							loaded = false;
						}
					}
					
					if (loaded) {
						// Add new popup menu options for a Unity session
						addTreeNodeMenuActions(session);
					}						
				}
			}
		}
		return new PluginSessionCallbackAdaptor(this);
	}

	/**
	 * Threads pauses for a small time then updates the object tree.
	 * Used when first starting the session to update the object tree.
	 */
	private class UpdateThread extends Thread {
	    private ISession session;
	    
		public UpdateThread(ISession session) {
	    	this.session = session;
	    }
		
	    public void run() {
	    	try 
	    	{
				Thread.sleep(1000);
			} 
	    	catch (InterruptedException e) 
	    	{	/* Exception ignored */
			}
	    	
	    	session.getSessionSheet().getObjectTreePanel().refreshTree(true);
	    }
	}
	
	/**
	 * Returns the complete file path of the virtualization configuration file for the session.
	 * 
	 * @param session 
	 * 		SQuirreL session
	 * @return
	 * 		path to sources file for the session
	 */
	private static String getSourceFilePath(ISession session)
	{
		ISQLAlias alias = session.getAlias();
		String aliasId = alias.getIdentifier().toString().replaceAll(":","_");
		return _userSettingsFolder+File.separator+aliasId+File.separator+alias.getName()+".xml";
	}

	/**
	 * Adds virtualization menu items to the popup menu for the object tree.
	 * 
	 * @param session
	 * 		SQuirreL session
	 */
	private void addTreeNodeMenuActions(ISession session)
	{
		try
		{
			// Add context menu items to the object tree's view and procedure nodes.
			IObjectTreeAPI otApi = session.getSessionInternalFrame().getObjectTreeAPI();
			IApplication app = session.getApplication();
			otApi.addToPopup(DatabaseObjectType.SESSION, new MultiAddSourceAction(app, _resources, session));
			otApi.addToPopup(DatabaseObjectType.SESSION, new MultiExportAction(app, _resources, session));
			otApi.addToPopup(DatabaseObjectType.SCHEMA, new MultiRemoveSourceAction(app, _resources, session));
			otApi.addToPopup(DatabaseObjectType.TABLE, new MultiRemoveTableAction(app, _resources, session));
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 * Gets the virtual schema from the connection using reflection.
	 * 
	 * @param con
	 * 		JDBC connection
	 * @return
	 * 		GlobalSchema for virtual connection
	 */
	public static Object getSchema(Connection con)
	{
		Class<? extends Connection> cls = con.getClass();

        Object retobj;
		try 
		{
			Method meth = cls.getMethod("getGlobalSchema", (Class[])  null);
			retobj = meth.invoke(con, (Object[]) null);
		} 
		catch (IllegalArgumentException e) 
		{
			throw new RuntimeException(e);
		} 
		catch (IllegalAccessException e) 
		{
			throw new RuntimeException(e);
		} 
		catch (InvocationTargetException e) 
		{
			throw new RuntimeException(e);
		} 
		catch (SecurityException e) 
		{
			throw new RuntimeException(e);
		} 
		catch (NoSuchMethodException e) 
		{
			throw new RuntimeException(e);
		}
		return retobj;
	}

	/**
	 * Returns true if virtualization driver is run in trial mode.
	 * 
	 * @param con
	 * 		JDBC connection
	 * @return
	 * 		true if trial (limited) mode, false otherwise
	 */
	public static boolean isTrial(Connection con)
	{
		Class<? extends Connection> cls = con.getClass();

        Object retobj;
		try {
			// Trial mode depends on the underlying UnityJDBC driver
			Method meth = cls.getMethod("isTrial", (Class[])  null);
			retobj = meth.invoke(con, (Object[]) null);
			return ((Boolean) retobj).booleanValue();
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}		
	}
	
	/**
	 * Returns true if virtualization driver is run in trial mode.
	 * 	
	 * @return
	 * 		true if trial (limited) mode, false otherwise
	 */
	public static boolean isTrial()
	{	return isTrial; }
	
	/**
	 * Updates any session information including virtualization configuration files.
	 * 
	 * @param session
	 * 		SQuirreL session
	 */
	public static void updateSession(ISession session)
	{	// Saves session information in user settings directory
		String filePath = getSourceFilePath(session);		
		export(filePath, session);
	}

	/**
	 * Refreshes object tree.  Used when virtual schema changes (e.g. adding a source).
	 * 
	 * @param session
	 * 		SQuirreL session
	 */
	public static void refreshTree(ISession session)
	{
		SessionInternalFrame sessMainFrm = session.getSessionInternalFrame();
		IObjectTreeAPI otree =  sessMainFrm.getObjectTreeAPI();
		new RefreshObjectTreeCommand(otree).execute();
	}

	/**
	 * Saves the virtualization configuration file to disk in a given file location.
	 * 
	 * @param sourcesFileName
	 * 		file name for sources file
	 * @param session
	 * 		SQuirreL session
	 */
	public static void export(String sourcesFileName, ISession session) {
		File f = new File(sourcesFileName);
				
		String path="";
		if (f.getParent() != null)
		{	path = f.getParent() + File.separator;
						
			// Make sure directory exists
			if (!f.getParentFile().exists()) {
				f.getParentFile().mkdir();
			}
		}
		
		sourcesFileName = f.getName();
		String sourcesNoExt = sourcesFileName;
		int idx = sourcesFileName.indexOf(".xml");
		if (idx > 0)
			sourcesNoExt = sourcesFileName.substring(0, sourcesFileName.length() - 4);

		Object schema = MultiSourcePlugin.getSchema(session.getSQLConnection().getConnection()); // Retrieve schema
		
		try {
			// Each source schema file is prefixed with sources file name (no extension) plus source name.
			// Export schema files of each source first as each file location is needed in the sources file listing all sources.			
			String password = session.getAlias().getPassword();
			if (password != null && password.length() < 1) {
				// No encryption of files if a password is not specified (including empty-string password)
				password = null;
			}
			
			// Retrieve list of all databases (sources)
			Method getDBsMethod = schema.getClass().getMethod("getAnnotatedDatabases", (Class[]) null);
			@SuppressWarnings("unchecked")
			ArrayList<Object> dbs = (ArrayList<Object>) getDBsMethod.invoke(schema, (Object[]) null);
			for (int i = 0; i < dbs.size(); i++) {
				Object db = dbs.get(i);
				
				// Export each source to its own XML file
				Method sdExportMethod = db.getClass().getMethod("export", new Class[] {java.io.OutputStream.class, java.lang.String.class});
				Method getDBNameMethod = db.getClass().getMethod("getDatabaseName", (Class[]) null);
				
				Method setDBSchemaMethod = db.getClass().getMethod("setSchemaFile", new Class[] { java.lang.String.class });
				String dbName = (String) getDBNameMethod.invoke(db, (Object[]) null);
				String fileName = sourcesNoExt + "_" + dbName + ".xml";
				setDBSchemaMethod.invoke(db, new Object[] { fileName });
				FileOutputStream fos = new FileOutputStream(path + fileName);
				sdExportMethod.invoke(db, new Object[]{fos,password});
				fos.close();
			}

			// Write out sources file
			Method gsExportMethod = schema.getClass().getMethod("export", new Class[] {java.io.OutputStream.class, java.lang.String.class});
			FileOutputStream fos = new FileOutputStream(path + sourcesFileName);
			gsExportMethod.invoke(schema, new Object[]{fos, password});
			fos.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
