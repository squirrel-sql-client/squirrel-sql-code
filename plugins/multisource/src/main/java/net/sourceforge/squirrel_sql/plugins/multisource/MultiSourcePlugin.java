package net.sourceforge.squirrel_sql.plugins.multisource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

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

/**
 * MultiSourcePlugin allows a user to query multiple databases with one query.
 */
public class MultiSourcePlugin extends DefaultSessionPlugin
{
	private PluginResources _resources;
	private static FileWrapper _userSettingsFolder;

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
		return "MultiSource Plugin";
	}

	/**
	 * Returns the current version of this plugin.
	 *
	 * @return the current version of this plugin.
	 */
	public String getVersion()
	{
		return "0.1";
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
		return "readme.txt";
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
		return "";
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
		try
		{
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

		// Determine if this is a Unity session.
		try
		{
			dbName = session.getMetaData().getDatabaseProductName().toLowerCase();
		}
		catch (SQLException e) {}

		if (dbName != null && dbName.contains("unity"))
		{	// Add new popup menu options for a Unity session
			addTreeNodeMenuActions(session);

			// Load session configuration information if URL says virtual
			if (session.getAlias() != null)
			{
				String url = session.getAlias().getUrl();
				if (url.toLowerCase().indexOf("/virtual") > 0)
				{	// Try to load based on session name
					Object schema = MultiSourcePlugin.getSchema(session.getSQLConnection().getConnection());		// Retrieve schema
					if (schema != null)
					{
						try
						{
						Method parseSourcesMethod = schema.getClass().getMethod("parseSourcesFile", new Class[]{java.io.BufferedReader.class, java.lang.String.class});
						String filePath = getSourceFilePath(session);
						System.out.println("LOad file: "+filePath);
						System.out.println("ID: "+session.getAlias().getIdentifier());
						BufferedReader reader = new BufferedReader(new FileReader(filePath));
						parseSourcesMethod.invoke(schema, new Object[]{reader, "jdbc:unity://"+filePath});
						// TODO: Not sure why these two lines below do not work.
						// IObjectTreeAPI otree = session.getSessionInternalFrame().getObjectTreeAPI();
						// otree.refreshTree();
						new UpdateThread(session).run();	// A poor solution to force the object tree to update after a slight delay
						}
						catch (Exception e)
						{
							System.out.println(e);
						}
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
	    	try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}
	    	refreshTree(session);
	    }
	}
	
	/**
	 * Returns the complete file path of the virtualization configuration file for the session.
	 * @param session
	 * @return
	 */
	private static String getSourceFilePath(ISession session)
	{
		ISQLAlias alias = session.getAlias();
		String aliasId = alias.getIdentifier().toString().replaceAll(":","_");
		return _userSettingsFolder+File.separator+aliasId+File.separator+alias.getName()+".xml";
	}

	/**
	 * Adds virtualization menu items to the popup menu for the object tree.
	 * @param session
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
			// otApi.addToPopup(DatabaseObjectType.SCHEMA, new MultiRenameSourceAction(app, _resources, session));
			otApi.addToPopup(DatabaseObjectType.TABLE, new MultiRemoveTableAction(app, _resources, session));
			// otApi.addToPopup(DatabaseObjectType.TABLE, new MultiRenameTableAction(app, _resources, session));
			otApi.addToPopup(DatabaseObjectType.COLUMN, new MultiRemoveFieldAction(app, _resources, session));
			// otApi.addToPopup(DatabaseObjectType.COLUMN, new MultiRenameFieldAction(app, _resources, session));
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 * Gets the virtual schema from the connection using reflections.
	 * @param con
	 * @return
	 */
	public static Object getSchema(Connection con)
	{
		Class<? extends Connection> cls = con.getClass();

        Object retobj;
		try {
			Method meth = cls.getMethod("getSchema", (Class[])  null);
			retobj = meth.invoke(con, (Object[]) null);
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
		return retobj;
	}

	/**
	 * Updates any session information including virtualization configuration files.
	 * @param session
	 */
	public static void updateSession(ISession session)
	{	// Saves session information in user settings directory
		String filePath = getSourceFilePath(session);		
		export(filePath, session);
	}

	/**
	 * Refreshes object tree.  Used when virtual schema changes (e.g. adding a source).
	 * @param session
	 */
	public static void refreshTree(ISession session)
	{
		SessionInternalFrame sessMainFrm = session.getSessionInternalFrame();
		IObjectTreeAPI otree =  sessMainFrm.getObjectTreeAPI();
		new RefreshObjectTreeCommand(otree).execute();
	}

	/**
	 * Saves the virtualization configuration file to disk in the plugin's user directory.
	 * @param sourcesFileName
	 * @param session
	 */
	public static void export(String sourcesFileName, ISession session)
	{
		File f = new File(sourcesFileName);
	       String path = f.getParent()+File.separator;
	       // Make sure directory exists
	       if (!f.getParentFile().exists())
	       {
	    	   f.getParentFile().mkdir();
	       }

	       sourcesFileName = f.getName();
	       String sourcesNoExt=sourcesFileName;
	       int idx = sourcesFileName.indexOf(".xml");
	       if (idx > 0)
	    	   sourcesNoExt = sourcesFileName.substring(0, sourcesFileName.length()-4);	
	       
	       Object schema = MultiSourcePlugin.getSchema(session.getSQLConnection().getConnection());		// Retrieve schema

	       try {
		       // Each source schema file is prefixed with sources file name (no extension) plus source name.
		       // Export schema files of each source first as each file location is needed in the sources file listing all sources.
				Method exportSourceMethod = schema.getClass().getMethod("exportSchema", new Class[]{java.lang.String.class});
				Method getDBsMethod = schema.getClass().getMethod("getAnnotatedDatabases", (Class[]) null);
				@SuppressWarnings("unchecked")
				ArrayList<Object> dbs = (ArrayList<Object>) getDBsMethod.invoke(schema,  (Object[]) null);
			    for (int i=0; i < dbs.size(); i++)
			    {	Object db = dbs.get(i);				
			    	Method getDBNameMethod = db.getClass().getMethod("getDatabaseName", (Class[]) null);
			    	Method setDBSchemaMethod = db.getClass().getMethod("setSchemaFile", new Class[]{java.lang.String.class});
			    	String dbName = (String) getDBNameMethod.invoke(db, (Object[]) null);
			    	String fileName = sourcesNoExt+"_"+dbName+".xml";
			    	setDBSchemaMethod.invoke(db, new Object[]{fileName});
			    	String source = (String) exportSourceMethod.invoke(schema,  new Object[]{dbName});
			    	writeToFile(path+fileName, source);
			    }

				// Write out sources file
				Method exportSourcesMethod = schema.getClass().getMethod("exportSources", (Class[]) null);
				String sources = (String) exportSourcesMethod.invoke(schema,  (Object[]) null);
				writeToFile(path+sourcesFileName, sources);
		} catch (Exception e) {			
			throw new RuntimeException(e);
		}
	}

	/**
	 * Writes to a given fileName the string contents.  Note: The file is over-written if it previously exists.
	 * @param fileName
	 * @param contents
	 */
	private static void writeToFile(String fileName, String contents) throws IOException
	{
		PrintWriter io = new PrintWriter(fileName);
		io.print(contents);
		io.close();
	}
}
