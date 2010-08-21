package net.sourceforge.squirrel_sql.plugins.hibernate;

import java.util.HashMap;

import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallback;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallbackAdaptor;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.hibernate.configuration.HibernateConfigController;
import net.sourceforge.squirrel_sql.plugins.hibernate.configuration.HibernatePrefsTab;

public class HibernatePlugin extends DefaultSessionPlugin
{
   private static ILogger s_log = LoggerController.createLogger(HibernatePlugin.class);


	private HibernatePluginResources _resources;
   private HashMap<IIdentifier, HibernateTabController> _hqlTabControllerBySessionID = new HashMap<IIdentifier, HibernateTabController>();
   private HibernatePrefsListener _curHibernatePrefsListener;

   public String getInternalName()
	{
		return "hibernate";
	}

	public String getDescriptiveName()
	{
		return "Hibernate Plugin";
	}

	public String getVersion()
	{
		return "1.0";
	}

	public String getAuthor()
	{
		return "Gerd Wagner";
	}

	public String getChangeLogFileName()
	{
		return "changes.txt";
	}

	public String getHelpFileName()
	{
		return "readme.html";
	}

	public String getLicenceFileName()
	{
		return "licence.txt";
	}

   /**
	 * @return	Comma separated list of contributors.
	 */
	public String getContributors()
	{
		return "";
	}

	public IGlobalPreferencesPanel[] getGlobalPreferencePanels()
	{
		return new IGlobalPreferencesPanel[]{new HibernatePrefsTab(new HibernateConfigController(this))};
	}

	public synchronized void initialize() throws PluginException
   {
		_resources = new HibernatePluginResources(this);
   }


   public void sessionEnding(ISession session)
   {
      try
      {
         _hqlTabControllerBySessionID.remove(session.getIdentifier()).sessionEnding();
      }
      catch (Throwable t)
      {
         s_log.error(t);
      }
   }

   public PluginSessionCallback sessionStarted(ISession session)
	{
		try
		{
         HibernateTabController hibernateTabController = new HibernateTabController(session, this, _resources);

         _hqlTabControllerBySessionID.put(session.getIdentifier(), hibernateTabController);


         session.getSessionSheet().insertMainTab(hibernateTabController, 2, false);

         return new PluginSessionCallbackAdaptor(this);
		}
		catch(Exception e)
		{
         throw new RuntimeException(e);
		}
	}



   public HibernatePluginResources getResources()
   {
      return _resources;
   }

   public void setHibernatePrefsListener(HibernatePrefsListener hibernatePrefsListener)
   {
      _curHibernatePrefsListener = hibernatePrefsListener;
   }

   public HibernatePrefsListener removeHibernatePrefsListener()
   {
      HibernatePrefsListener buf = _curHibernatePrefsListener;
      _curHibernatePrefsListener = null;
      return buf;

   }
}
