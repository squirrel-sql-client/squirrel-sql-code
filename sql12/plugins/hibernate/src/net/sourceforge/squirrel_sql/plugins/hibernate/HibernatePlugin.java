package net.sourceforge.squirrel_sql.plugins.hibernate;

import net.sourceforge.squirrel_sql.client.gui.session.ObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallback;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.hibernate.configuration.HibernateController;
import net.sourceforge.squirrel_sql.plugins.hibernate.completion.HQLCompleteCodeAction;

import javax.swing.*;
import java.util.HashMap;

public class HibernatePlugin extends DefaultSessionPlugin
{
   private static ILogger s_log = LoggerController.createLogger(HibernatePlugin.class);


	private HibernatePluginResources _resources;
   private HashMap<IIdentifier, HQLTabController> _hqlTabControllerBySessionID = new HashMap<IIdentifier, HQLTabController>();

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
         HQLTabController hqlTabController = new HQLTabController(session, this, _resources);

         _hqlTabControllerBySessionID.put(session.getIdentifier(), hqlTabController);


         session.getSessionSheet().insertMainTab(hqlTabController, 2, false);

         initCodeCompletion(hqlTabController, session);


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

   private void initCodeCompletion(HQLTabController hqlTabController, ISession session)
   {
      ISQLEntryPanel entryPanel = hqlTabController.getHqlEntrPanelManager().getEntryPanel();

      HQLCompleteCodeAction hcca = new HQLCompleteCodeAction(getApplication(), this, entryPanel, hqlTabController);


      JMenuItem item = entryPanel.addToSQLEntryAreaMenu(hcca);

      _resources.configureMenuItem(hcca, item);

      JComponent comp = entryPanel.getTextComponent();
      comp.registerKeyboardAction(hcca, _resources.getKeyStroke(hcca), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
   }


   public HibernatePluginResources getResources()
   {
      return _resources;
   }
}
