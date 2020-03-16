package net.sourceforge.squirrel_sql.plugins.hibernate;

import net.sourceforge.squirrel_sql.client.gui.titlefilepath.TitleFilePathHandler;
import net.sourceforge.squirrel_sql.client.gui.titlefilepath.TitleFilePathHandlerUtil;
import net.sourceforge.squirrel_sql.client.preferences.GlobalPreferencesSheet;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.filemanager.IFileEditorAPI;
import net.sourceforge.squirrel_sql.client.session.mainpanel.IMainPanelTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLPanel;
import net.sourceforge.squirrel_sql.fw.props.Props;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanReader;
import net.sourceforge.squirrel_sql.fw.xml.XMLException;
import net.sourceforge.squirrel_sql.plugins.hibernate.configuration.HibernateConfigPanel;
import net.sourceforge.squirrel_sql.plugins.hibernate.mapping.MappedObjectController;
import net.sourceforge.squirrel_sql.plugins.hibernate.server.HibernateConfiguration;
import net.sourceforge.squirrel_sql.plugins.hibernate.util.HibernateUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class HibernateTabController implements IMainPanelTab
{

   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(HibernateTabController.class);

   private static ILogger s_log = LoggerController.createLogger(HibernateTabController.class);
   private final HibernateChannel _hibernateChannel;


   private HibernateTabPanel _hibernateTabPanel;
   private ISession _session;
   private HibernatePlugin _plugin;
   private static final String PREF_KEY_LAST_SELECTED_CONFIG = "SQuirreL.hibernateplugin.lastSelectedConfig";
   private HibernateConnection _con;
   private HibnerateConnector _hibnerateConnector;
   private HibernatePluginResources _resource;
   private HQLPanelController _hqlPanelController;
   private ArrayList<ConnectionListener> _listeners = new ArrayList<ConnectionListener>();
   private HqlResultPanelManager _hqlResultPanelManager;
   private MappedObjectController _mappedObjectsController;
   private final TitleFilePathHandler _titleFileHandler;

   public HibernateTabController(ISession session, HibernatePlugin plugin, HibernatePluginResources resource)
   {
      _resource = resource;
      try
      {
         _session = session;
         _plugin = plugin;

         _titleFileHandler = new TitleFilePathHandler(() -> setSqlTabComponentTitle());

         _hibernateChannel = new HibernateChannel(this);

         _hqlPanelController = new HQLPanelController(_hibernateChannel, _session, resource, _titleFileHandler);
         _hqlResultPanelManager = new HqlResultPanelManager(_session, resource);
         _mappedObjectsController = new MappedObjectController(_hibernateChannel, _session, resource);

         _hibernateTabPanel = new HibernateTabPanel(_mappedObjectsController.getComponent(), _hqlPanelController.getComponent(), _hqlResultPanelManager.getComponent(), _resource);
         _hibernateTabPanel.btnConnected.setIcon(resource.getIcon(HibernatePluginResources.IKeys.DISCONNECTED_IMAGE));


         HibnerateConnectorListener hibnerateConnectorListener = new HibnerateConnectorListener()
         {
            public void connected(HibernateConnection con, HibernateConfiguration cfg)
            {
               onConnected(con, cfg);
            }

            public void connectFailed(Throwable t)
            {
               onConnectFailed(t);
            }
         };

         _hibnerateConnector = new HibnerateConnector(_plugin, hibnerateConnectorListener);



         _hibernateTabPanel.btnConnected.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent e)
            {
               onConnect();
            }
         });


         _hibernateTabPanel.btnOpenConfigs.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent e)
            {
               onOpenConfigs();
            }
         });


         _hibernateTabPanel.tabHibernateTabbedPane.addChangeListener(e -> onTabChanged());

         loadConfigsFromXml();

         _hqlPanelController.initActions();



      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   private void setSqlTabComponentTitle()
   {
      TitleFilePathHandlerUtil.setTitle(_hibernateTabPanel.getHqlTabTitle(), _titleFileHandler, _hibernateTabPanel.tabComponentOfHqlTab);
   }

   private void onTabChanged()
   {
      _session.getSessionInternalFrame().getSessionPanel().performStateChanged();
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
      GlobalPreferencesSheet.showSheet(HibernateConfigPanel.class);
   }

   private HibernateConfiguration onGetPreselectedCfg()
   {
      return (HibernateConfiguration) _hibernateTabPanel.cboConfigurations.getSelectedItem();
   }

   private void onConfigurationChanged(ArrayList<HibernateConfiguration> changedCfgs)
   {
      HibernateConfiguration selCfg = (HibernateConfiguration) _hibernateTabPanel.cboConfigurations.getSelectedItem();

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

      XMLBeanReader reader = HibernateUtil.createHibernateConfigsReader(_plugin);

      if (null != reader)
      {
         loadConfigs(reader, Props.getString(PREF_KEY_LAST_SELECTED_CONFIG, null));
      }
   }

   private void loadConfigs(Iterable reader, String cfgNameToSelect)
   {
      _hibernateTabPanel.cboConfigurations.removeAllItems();

      HashMap<String, HibernateConfiguration> cfgByName = new HashMap<>();
      for (Object o : reader)
      {
         HibernateConfiguration cfg = (HibernateConfiguration) o;

         cfgByName.put(cfg.getName(), cfg);
         _hibernateTabPanel.cboConfigurations.addItem(cfg);
      }

      if(null != cfgNameToSelect)
      {
         _hibernateTabPanel.cboConfigurations.setSelectedItem(cfgByName.get(cfgNameToSelect));
      }
   }


   private void onConnect()
   {
      if(null == _con)
      {
         if(null != _hibernateTabPanel.cboConfigurations.getSelectedItem())
         {
            _hibernateTabPanel.btnConnected.setEnabled(false);
            _hibernateTabPanel.btnConnected.setDisabledSelectedIcon(_resource.getIcon(HibernatePluginResources.IKeys.CONNECTING_IMAGE));
            _hibernateTabPanel.btnConnected.repaint();
            _hibnerateConnector.connect((HibernateConfiguration) _hibernateTabPanel.cboConfigurations.getSelectedItem(), _session);
         }
         else
         {
            _hibernateTabPanel.btnConnected.setSelected(false);

            // i18n[HibernateTabController.noConfigSelected=Please select a Hibernate configuration to connect to.\nHibernate configurations can be defined in the global preferences window.\nWould you like to open the window now?]
            int opt = JOptionPane.showConfirmDialog(_session.getApplication().getMainFrame(), s_stringMgr.getString("HQLTabController.noConfigSelected"));


            if(JOptionPane.YES_OPTION == opt)
            {
               GlobalPreferencesSheet.showSheet(HibernateConfigPanel.class);
            }
         }

      }
      else
      {
         _hibernateTabPanel.btnConnected.setIcon(_resource.getIcon(HibernatePluginResources.IKeys.DISCONNECTED_IMAGE));
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
      _hibernateTabPanel.btnConnected.setIcon(_resource.getIcon(HibernatePluginResources.IKeys.CONNECTED_IMAGE));
      _hibernateTabPanel.btnConnected.setEnabled(true);
      _hibernateTabPanel.cboConfigurations.setEnabled(false);
      _hqlPanelController.setConnection(con);

      for (ConnectionListener listener : _listeners)
      {
         listener.connectionOpened(con, cfg);
      }

   }

   private void onConnectFailed(Throwable t)
   {
      _hibernateTabPanel.btnConnected.setIcon(_resource.getIcon(HibernatePluginResources.IKeys.DISCONNECTED_IMAGE));
      _hibernateTabPanel.btnConnected.setEnabled(true);
      _hibernateTabPanel.btnConnected.setSelected(false);
      _session.showErrorMessage(t);
      s_log.error(t);
      _con = null;
      _hqlPanelController.setConnection(null);

      if(Utilities.getDeepestThrowable(t) instanceof StackOverflowError)
      {
         String warnMessage = s_stringMgr.getString("hibernate.stackOverFlowMessage");
         _session.showWarningMessage(warnMessage);
         s_log.warn(warnMessage);
      }

   }


   public String getTitle()
   {
      // i18n[HibernateTabController.title=Hibernate]
      return s_stringMgr.getString("HQLTabController.title");
   }

   @Override
   public Component getTabComponent()
   {
      return null;
   }

   public String getHint()
   {
      // i18n[HibernateTabController.hint=Support for Hibernate]
      return s_stringMgr.getString("HQLTabController.hint");
   }

   public Component getComponent()
   {
      return _hibernateTabPanel;
   }


   public void sessionClosing(ISession session)
   {
      HibernateConfiguration cfg = (HibernateConfiguration) _hibernateTabPanel.cboConfigurations.getSelectedItem();

      if(null != cfg)
      {
         Props.putString(PREF_KEY_LAST_SELECTED_CONFIG, cfg.getName());
      }

      _hibernateTabPanel.closing();

      _mappedObjectsController.closing();

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
      size.height = _hibernateTabPanel.btnConnected.getPreferredSize().height;
      btn.setPreferredSize(size);

      
      _hibernateTabPanel.addToToolbar(btn);
   }


   public void displayObjects(HibernateConnection con, String hqlQuery)
   {
      _hqlResultPanelManager.displayObjects(con, hqlQuery);
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
      _hibernateTabPanel.cboConfigurations.setEnabled(true);

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

   @Override
   public void mouseWheelClickedOnTab()
   {

   }

   @Override
   public IFileEditorAPI getActiveFileEditorAPIOrNull()
   {
      if(isHqlEditorTabActive())
      {
         return _hqlPanelController.getFileEditorAPIOrNull();
      }

      return null;
   }

   private boolean isHqlEditorTabActive()
   {
      return _hibernateTabPanel.tabHibernateTabbedPane.getSelectedComponent() == _hibernateTabPanel.splitHqlSql;
   }

   public void viewInMappedObjects(String wordAtCursor)
   {
      if(_mappedObjectsController.viewInMappedObjects(wordAtCursor))
      {
         _hibernateTabPanel.tabHibernateTabbedPane.setSelectedIndex(0);
      }
   }


   @Override
   public SQLPanel getSqlPanelOrNull()
   {
      return null;
   }
}
