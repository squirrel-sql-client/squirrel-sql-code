package net.sourceforge.squirrel_sql.plugins.hibernate;

import net.sourceforge.squirrel_sql.client.session.mainpanel.IMainPanelTab;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanReader;
import net.sourceforge.squirrel_sql.plugins.hibernate.configuration.HibernateConfiguration;
import net.sourceforge.squirrel_sql.plugins.hibernate.configuration.HibernateController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.HashMap;
import java.util.prefs.Preferences;

public class HQLTabController implements IMainPanelTab, IHQLTabController
{

   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(HQLTabController.class);

   private static ILogger s_log = LoggerController.createLogger(HQLTabController.class);


   private HQLTabPanel _panel;
   private ISession _session;
   private HibernatePlugin _plugin;
   private static final String PREF_KEY_LAST_SELECTED_CONFIG = "SQuirreL.hibernateplugin.lastSelectedConfig";
   private HibernateConnection _con;
   private HibnerateConnector _hibnerateConnector;
   private HibernatePluginResources _resource;
   private SQLPanelController _sqlPanelController;

   public HQLTabController(ISession session, HibernatePlugin plugin, HibernatePluginResources resource)
   {
      _resource = resource;
      try
      {
         _session = session;
         _plugin = plugin;
         _panel = new HQLTabPanel();
         _panel.btnConnected.setIcon(resource.getIcon(HibernatePluginResources.IKeys.DISCONNECTED_IMAGE));

         _sqlPanelController = new SQLPanelController(_panel.txtHQL, this, _session, _resource);

         XMLBeanReader reader = new XMLBeanReader();
         File pluginUserSettingsFolder = _plugin.getPluginUserSettingsFolder();


         File xmlFile = new File(pluginUserSettingsFolder.getPath(), HibernateController.HIBERNATE_CONFIGS_XML_FILE);

         if(false == xmlFile.exists())
         {
            return;
         }

         reader.load(xmlFile);


         HashMap cfgByName = new HashMap();
         for (Object o : reader)
         {
            HibernateConfiguration cfg = (HibernateConfiguration) o;

            cfgByName.put(cfg.getName(), cfg);
            _panel.cboConfigurations.addItem(cfg);
         }

         _panel.cboConfigurations.setSelectedItem(cfgByName.get(Preferences.userRoot().get(PREF_KEY_LAST_SELECTED_CONFIG, null)));


         _hibnerateConnector = new HibnerateConnector(new HibnerateConnectorListener()
         {
            public void connected(HibernateConnection con)
            {
               onConnected(con);
            }

            public void connectFailed(Throwable t)
            {
               onConnectFailed(t);
            }
         });



         _panel.btnConnected.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent e)
            {
               onConnect();
            }
         });

      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }


   private void onConnect()
   {
      if(null == _con)
      {
         if(null != _panel.cboConfigurations.getSelectedItem())
         {
            //_panel.btnConnected.setIcon(_resource.getIcon(HibernatePluginResources.IKeys.CONNECTING_IMAGE));
            _panel.btnConnected.setEnabled(false);
            _panel.btnConnected.setDisabledSelectedIcon(_resource.getIcon(HibernatePluginResources.IKeys.CONNECTING_IMAGE));
            _panel.btnConnected.repaint();
            _hibnerateConnector.connect((HibernateConfiguration)_panel.cboConfigurations.getSelectedItem());
         }
         else
         {
            // i18n[HQLTabController.noConfigSelected=Please select a Hibernate Configuration to connect to.\nHibernate Configurations can be defined in the global preferences window.]
            JOptionPane.showMessageDialog(_session.getApplication().getMainFrame(), s_stringMgr.getString("HQLTabController.noConfigSelected"));
         }

      }
      else
      {
         _panel.btnConnected.setIcon(_resource.getIcon(HibernatePluginResources.IKeys.DISCONNECTED_IMAGE));
         try
         {
            _con.close();
         }
         catch (Exception e)
         {
            s_log.error(e);
         }
         finally
         {
            _con = null;
            _sqlPanelController.setConnection(null);
         }
      }
   }

   private void onConnected(HibernateConnection con)
   {
      _con = con;
      _panel.btnConnected.setIcon(_resource.getIcon(HibernatePluginResources.IKeys.CONNECTED_IMAGE));
      _panel.btnConnected.setEnabled(true);
      _sqlPanelController.setConnection(con);

   }

   private void onConnectFailed(Throwable t)
   {
      _panel.btnConnected.setIcon(_resource.getIcon(HibernatePluginResources.IKeys.DISCONNECTED_IMAGE));
      _panel.btnConnected.setEnabled(true);
      _session.getApplication().getMessageHandler().showErrorMessage(t);
      s_log.error(t);

      _con = null;
      _sqlPanelController.setConnection(null);

   }


   public String getTitle()
   {
      // i18n[HQLTabController.title=HQL]
      return s_stringMgr.getString("HQLTabController.title");
   }

   public String getHint()
   {
      // i18n[HQLTabController.hint=Support for Hibernate HQL Queries]
      return s_stringMgr.getString("HQLTabController.hint");
   }

   public Component getComponent()
   {
      return _panel;
   }


   public void sessionClosing(ISession session)
   {
      HibernateConfiguration cfg = (HibernateConfiguration) _panel.cboConfigurations.getSelectedItem();

      if(null != cfg)
      {
         Preferences.userRoot().put(PREF_KEY_LAST_SELECTED_CONFIG, cfg.getName());
      }

      
      _panel.closing();

   }

   public void select()
   {
   }

   public void setSession(ISession session)
   {
   }

   public void displaySQLs(String sqls)
   {
      _panel.txtSQL.setText(sqls);
   }

   public void addToToolbar(AbstractAction action)
   {
      _panel.addToToolbar(new JButton(action));
   }
}
