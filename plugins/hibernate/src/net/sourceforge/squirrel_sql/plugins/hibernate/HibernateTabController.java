package net.sourceforge.squirrel_sql.plugins.hibernate;

import net.sourceforge.squirrel_sql.client.preferences.GlobalPreferencesSheet;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.IMainPanelTab;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanReader;
import net.sourceforge.squirrel_sql.fw.xml.XMLException;
import net.sourceforge.squirrel_sql.plugins.hibernate.configuration.HibernateConfiguration;
import net.sourceforge.squirrel_sql.plugins.hibernate.configuration.HibernateConfigController;
import net.sourceforge.squirrel_sql.plugins.hibernate.configuration.HibernateConfigPanel;
import net.sourceforge.squirrel_sql.plugins.hibernate.mapping.MappedObjectPanelManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.prefs.Preferences;

public class HibernateTabController implements IMainPanelTab, IHibernateTabController, IHibernateConnectionProvider
{

   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(HibernateTabController.class);

   private static ILogger s_log = LoggerController.createLogger(HibernateTabController.class);


   private HibernateTabPanel _panel;
   private ISession _session;
   private HibernatePlugin _plugin;
   private static final String PREF_KEY_LAST_SELECTED_CONFIG = "SQuirreL.hibernateplugin.lastSelectedConfig";
   private HibernateConnection _con;
   private HibnerateConnector _hibnerateConnector;
   private HibernatePluginResources _resource;
   private HQLPanelController _hqlPanelController;
   private ArrayList<ConnectionListener> _listeners = new ArrayList<ConnectionListener>();
   private SQLPanelManager _sqlPanelManager;
   private MappedObjectPanelManager _mappedObjectsPanelManager;

   public HibernateTabController(ISession session, HibernatePlugin plugin, HibernatePluginResources resource)
   {
      _resource = resource;
      try
      {
         _session = session;
         _plugin = plugin;

         _hqlPanelController = new HQLPanelController(this, _session, resource);
         _sqlPanelManager = new SQLPanelManager(_session);
         _mappedObjectsPanelManager = new MappedObjectPanelManager(this, _session, resource);

         _panel = new HibernateTabPanel(_mappedObjectsPanelManager.getComponent(), _hqlPanelController.getComponent(), _sqlPanelManager.getComponent(), _resource);
         _panel.btnConnected.setIcon(resource.getIcon(HibernatePluginResources.IKeys.DISCONNECTED_IMAGE));

         Dimension oldSize = _panel.btnConnected.getPreferredSize();
         Dimension newSize = new Dimension(oldSize.width-20, oldSize.height);
         _panel.btnConnected.setPreferredSize(newSize);


         _hibnerateConnector = new HibnerateConnector(new HibnerateConnectorListener()
         {
            public void connected(HibernateConnection con, HibernateConfiguration cfg)
            {
               onConnected(con, cfg);
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


         _panel.btnOpenConfigs.setPreferredSize(_panel.btnConnected.getPreferredSize());
         _panel.btnOpenConfigs.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent e)
            {
               onOpenConfigs();
            }
         });


         loadConfigsFromXml();

         _hqlPanelController.initActions();

      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   private void onOpenConfigs()
   {
      _plugin.setHibernatePrefsListener(new HibernatePrefsListener()
      {
         public void configurationChanged(ArrayList<HibernateConfiguration> changedCfgs)
         {
            onConfigurationChanged(changedCfgs);
         }

         public HibernateConfiguration getPreselectedCfg()
         {
            return onGetPreselectedCfg();
         }
      });
      GlobalPreferencesSheet.showSheet(_plugin.getApplication(), HibernateConfigPanel.class);
   }

   private HibernateConfiguration onGetPreselectedCfg()
   {
      return (HibernateConfiguration) _panel.cboConfigurations.getSelectedItem();
   }

   private void onConfigurationChanged(ArrayList<HibernateConfiguration> changedCfgs)
   {
      HibernateConfiguration selCfg = (HibernateConfiguration) _panel.cboConfigurations.getSelectedItem();

      if(null != selCfg)
      {
         loadConfigs(changedCfgs, selCfg.getName());
      }
      else
      {
         loadConfigs(changedCfgs, null);
      }
   }


   private void loadConfigsFromXml()
      throws IOException, XMLException
   {
      XMLBeanReader reader = new XMLBeanReader();
      File pluginUserSettingsFolder = _plugin.getPluginUserSettingsFolder();

      File xmlFile = new File(pluginUserSettingsFolder.getPath(), HibernateConfigController.HIBERNATE_CONFIGS_XML_FILE);

      if (xmlFile.exists())
      {
         reader.load(xmlFile, _plugin.getClass().getClassLoader());
         loadConfigs(reader, Preferences.userRoot().get(PREF_KEY_LAST_SELECTED_CONFIG, null));
      }
   }

   private void loadConfigs(Iterable reader, String cfgNameToSelect)
   {
      _panel.cboConfigurations.removeAllItems();

      HashMap<String, HibernateConfiguration> cfgByName = new HashMap<String, HibernateConfiguration>();
      for (Object o : reader)
      {
         HibernateConfiguration cfg = (HibernateConfiguration) o;

         cfgByName.put(cfg.getName(), cfg);
         _panel.cboConfigurations.addItem(cfg);
      }

      if(null != cfgNameToSelect)
      {
         _panel.cboConfigurations.setSelectedItem(cfgByName.get(cfgNameToSelect));
      }
   }


   private void onConnect()
   {
      if(null == _con)
      {
         if(null != _panel.cboConfigurations.getSelectedItem())
         {
            _panel.btnConnected.setEnabled(false);
            _panel.btnConnected.setDisabledSelectedIcon(_resource.getIcon(HibernatePluginResources.IKeys.CONNECTING_IMAGE));
            _panel.btnConnected.repaint();
            _hibnerateConnector.connect((HibernateConfiguration)_panel.cboConfigurations.getSelectedItem(), _session);
         }
         else
         {
            _panel.btnConnected.setSelected(false);

            // i18n[HibernateTabController.noConfigSelected=Please select a Hibernate configuration to connect to.\nHibernate configurations can be defined in the global preferences window.\nWould you like to open the window now?]
            int opt = JOptionPane.showConfirmDialog(_session.getApplication().getMainFrame(), s_stringMgr.getString("HQLTabController.noConfigSelected"));


            if(JOptionPane.YES_OPTION == opt)
            {
               GlobalPreferencesSheet.showSheet(_plugin.getApplication(), HibernateConfigPanel.class);
            }
         }

      }
      else
      {
         _panel.btnConnected.setIcon(_resource.getIcon(HibernatePluginResources.IKeys.DISCONNECTED_IMAGE));
         try
         {
            closeConnection();
         }
         catch (Exception e)
         {
            s_log.error(e);
         }
         finally
         {
            _con = null;
            _hqlPanelController.setConnection(null);
         }
      }
   }

   private void onConnected(HibernateConnection con, HibernateConfiguration cfg)
   {
      _con = con;
      _panel.btnConnected.setIcon(_resource.getIcon(HibernatePluginResources.IKeys.CONNECTED_IMAGE));
      _panel.btnConnected.setEnabled(true);
      _hqlPanelController.setConnection(con);

      for (ConnectionListener listener : _listeners)
      {
         listener.connectionOpened(con, cfg);
      }

   }

   private void onConnectFailed(Throwable t)
   {
      _panel.btnConnected.setIcon(_resource.getIcon(HibernatePluginResources.IKeys.DISCONNECTED_IMAGE));
      _panel.btnConnected.setEnabled(true);
      _panel.btnConnected.setSelected(false);
      _session.showErrorMessage(t);
      s_log.error(t);
      _con = null;
      _hqlPanelController.setConnection(null);

   }


   public String getTitle()
   {
      // i18n[HibernateTabController.title=Hibernate]
      return s_stringMgr.getString("HQLTabController.title");
   }

   public String getHint()
   {
      // i18n[HibernateTabController.hint=Support for Hibernate]
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

      _mappedObjectsPanelManager.closing();

   }

   public void select()
   {
      _hqlPanelController.requestFocus();   
   }

   public void setSession(ISession session)
   {
   }


   public void addToToolbar(AbstractAction action)
   {
      JButton btn = new JButton(action);
      Dimension size = btn.getPreferredSize();
      size.height = _panel.btnConnected.getPreferredSize().height;
      btn.setPreferredSize(size);

      
      _panel.addToToolbar(btn);
   }

   public void displaySqls(ArrayList<String> sqls)
   {
      _sqlPanelManager.displaySqls(sqls);
   }

   public IHibernateConnectionProvider getHibernateConnectionProvider()
   {
      return this;
   }

   public void sessionEnding()
   {
      if(null != _con)
      {
         closeConnection();
      }
   }

   private void closeConnection()
   {
      _con.close();

      for (ConnectionListener listener : _listeners)
      {
         listener.connectionClosed();
      }
   }


   public HibernateConnection getHibernateConnection()
   {
      return _con;
   }

   public void addConnectionListener(ConnectionListener connectionListener)
   {
      _listeners.add(connectionListener);
   }

}
