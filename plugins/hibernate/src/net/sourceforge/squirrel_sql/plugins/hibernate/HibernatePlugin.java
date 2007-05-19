package net.sourceforge.squirrel_sql.plugins.hibernate;

import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.gui.session.ObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallback;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.plugins.hibernate.configuration.HibernateController;

public class HibernatePlugin extends DefaultSessionPlugin
{
	private HibernatePluginResources _resources;

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
		return "0.01";
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
		return "readme.txt";
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
		return new IGlobalPreferencesPanel[]{new HibernatePrefsTab(new HibernateController(this))};
	}

	public synchronized void initialize() throws PluginException
   {
		_resources = new HibernatePluginResources(this);
   }


	public PluginSessionCallback sessionStarted(ISession session)
	{
		try
		{
         ActionCollection coll = getApplication().getActionCollection();

         session.getSessionSheet().insertMainTab(new HQLTabController(session, this, _resources), 2);


         return new PluginSessionCallback()
         {
            public void sqlInternalFrameOpened(SQLInternalFrame sqlInternalFrame, ISession sess)
            {
               //plugin supports Session main window only
            }

            public void objectTreeInternalFrameOpened(ObjectTreeInternalFrame objectTreeInternalFrame, ISession sess)
            {
               //plugin supports Session main window only
            }
         };
		}
		catch(Exception e)
		{
         throw new RuntimeException(e);
		}
	}

}
