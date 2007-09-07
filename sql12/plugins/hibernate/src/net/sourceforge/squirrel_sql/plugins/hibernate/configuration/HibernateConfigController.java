package net.sourceforge.squirrel_sql.plugins.hibernate.configuration;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanWriter;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanReader;
import net.sourceforge.squirrel_sql.plugins.hibernate.HibernatePlugin;
import net.sourceforge.squirrel_sql.plugins.hibernate.HibernatePrefsListener;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.util.prefs.Preferences;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.io.File;

public class HibernateConfigController
{
   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(HibernateConfigController.class);


   private HibernatePlugin _plugin;
   private HibernateConfigPanel _panel;

   static final String PERF_KEY_LAST_DIR = "Squirrel.Hibernate.lastDir";
   public static final String HIBERNATE_CONFIGS_XML_FILE = "hibernateConfigs.xml";
   private HibernatePrefsListener _hibernatePrefsListener;

   public HibernateConfigController(HibernatePlugin plugin)
   {
      _plugin = plugin;
      _panel = new HibernateConfigPanel();

      _hibernatePrefsListener = _plugin.removeHibernatePrefsListener();

      _panel.lstClassPath.setModel(new DefaultListModel());

      _panel.btnNewConfig.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onNewConfig();
         }
      });

      _panel.btnRemoveConfig.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onRemoveConfig();
         }
      });


      _panel.btnClassPathAdd.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onAddClasspathEntry();
         }
      });

      _panel.btnClassPathRemove.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onRemoveSelectedClasspathEntries();
         }
      });



      _panel.btnEditFactoryProviderInfo.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onFactoryProviderInfo();
         }
      });


      _panel.btnApplyConfigChanges.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onApplyConfigChanges();
         }
      });

      _panel.cboConfigs.addItemListener(new ItemListener()
      {
         public void itemStateChanged(ItemEvent e)
         {
            onSelectedConfigChanged(e);
         }
      });

      ItemListener radObtainSessFactListener = new ItemListener()
      {
         public void itemStateChanged(ItemEvent e)
         {
            onObtainSessFactChanged();
         }
      };

      _panel.radConfiguration.addItemListener(radObtainSessFactListener);
      _panel.radJPA.addItemListener(radObtainSessFactListener);
      _panel.radUserDefProvider.addItemListener(radObtainSessFactListener);


   }

   private void onObtainSessFactChanged()
   {
      if(_panel.radUserDefProvider.isSelected())
      {
         _panel.btnEditFactoryProviderInfo.setEnabled(true);
         _panel.txtPersistenceUnitName.setEnabled(false);
      }
      else if(_panel.radJPA.isSelected())
      {
         _panel.btnEditFactoryProviderInfo.setEnabled(false);
         _panel.txtPersistenceUnitName.setEnabled(true);
      }
      else if(_panel.radConfiguration.isSelected())
      {
         _panel.btnEditFactoryProviderInfo.setEnabled(false);
         _panel.txtPersistenceUnitName.setEnabled(false);
      }
   }

   private void onRemoveConfig()
   {
      HibernateConfiguration selConfig = (HibernateConfiguration) _panel.cboConfigs.getSelectedItem();

      if(null == selConfig)
      {
         // i18n[HibernateConfigController.NoConfigToRemove=No configuration selected to remove.]
         JOptionPane.showMessageDialog(_plugin.getApplication().getMainFrame(), s_stringMgr.getString("HibernateController.NoConfigToRemove"));
      }
      else
      {
         // i18n[HibernateConfigController.ReallyRemoveConfig=Are you sure you want to delete configuration "{0}".]
         if(JOptionPane.YES_OPTION ==
            JOptionPane.showConfirmDialog(_plugin.getApplication().getMainFrame(), s_stringMgr.getString("HibernateController.ReallyRemoveConfig", selConfig)))
         {
            ((DefaultComboBoxModel)_panel.cboConfigs.getModel()).removeElement(selConfig);
         }
      }
   }

   private void onRemoveSelectedClasspathEntries()
   {
      int[] selIces = _panel.lstClassPath.getSelectedIndices();


      List<String> toRemove = new ArrayList<String>();
      DefaultListModel listModel = (DefaultListModel) _panel.lstClassPath.getModel();
      for (int i = 0; i < selIces.length; i++)
      {
         toRemove.add((String) listModel.getElementAt(selIces[i]));
      }

      for (String s : toRemove)
      {
         listModel.removeElement(s);
      }
   }

   private void onSelectedConfigChanged(ItemEvent e)
   {
      if(ItemEvent.SELECTED == e.getStateChange() && null != e.getItem())
      {
         initConfig((HibernateConfiguration) e.getItem());
      }
      else if(ItemEvent.DESELECTED == e.getStateChange() && null != e.getItem())
      {
         initConfig(null);
      }
   }

   private boolean onApplyConfigChanges()
   {
      String provider = _panel.txtFactoryProvider.getText();
      String persistenceUnitName = _panel.txtPersistenceUnitName.getText();

      if(_panel.radUserDefProvider.isSelected() && (null == provider || 0 == provider.trim().length()))
      {
         // i18n[HibernateConfigController.noProviderMsg=Missing SessionFactoryImplProvider .\nChanges cannot be applied.]
         JOptionPane.showMessageDialog(_plugin.getApplication().getMainFrame(), s_stringMgr.getString("HibernateController.noProviderMsg"));
         return false;
      }

      if(_panel.radJPA.isSelected() && (null == persistenceUnitName || 0 == persistenceUnitName.trim().length()))
      {
         // i18n[HibernateConfigController.noPersistenceUnitName=Missing Persitence-Unit name .\nChanges cannot be applied.]
         JOptionPane.showMessageDialog(_plugin.getApplication().getMainFrame(), s_stringMgr.getString("HibernateController.noPersistenceUnitName"));
         return false;
      }


      String cfgName = _panel.txtConfigName.getText();

      if(null == cfgName || 0 == cfgName.trim().length())
      {
         // i18n[HibernateConfigController.noCfgNameMsg=Not a valid configuration name\nChanges cannot be applied.]
         JOptionPane.showMessageDialog(_plugin.getApplication().getMainFrame(), s_stringMgr.getString("HibernateController.noProviderMsg"));
         return false;
      }


      HibernateConfiguration cfg = (HibernateConfiguration) _panel.cboConfigs.getSelectedItem();

      boolean wasNull = false;
      if(null == cfg)
      {
         wasNull = true;
         cfg = new HibernateConfiguration();
      }

      cfg.setProvider(provider);
      cfg.setPersistenceUnitName(persistenceUnitName);
      cfg.setName(cfgName);

      String[] classPathEntries = new String[_panel.lstClassPath.getModel().getSize()];

      for (int i=0; i< _panel.lstClassPath.getModel().getSize(); ++i)
      {
         classPathEntries[i] = (String) _panel.lstClassPath.getModel().getElementAt(i);
      }
      cfg.setClassPathEntries(classPathEntries);


      if(_panel.radUserDefProvider.isSelected())
      {
         cfg.setUserDefinedProvider(true);
         cfg.setJPA(false);
      }
      else if(_panel.radJPA.isSelected())
      {
         cfg.setUserDefinedProvider(false);
         cfg.setJPA(true);
      }
      else
      {
         cfg.setUserDefinedProvider(false);
         cfg.setJPA(false);
      }



      if(wasNull)
      {
         _panel.cboConfigs.addItem(cfg);
         _panel.cboConfigs.setSelectedItem(cfg);

      }

      return true;
   }

   private void onFactoryProviderInfo()
   {
      String className = new FactoryProviderController(_plugin, _panel.txtFactoryProvider.getText()).getClassName();
      _panel.txtFactoryProvider.setText(className);

   }

   private void onAddClasspathEntry()
   {
      String dirPath = Preferences.userRoot().get(PERF_KEY_LAST_DIR, System.getProperty("user.home"));

      JFileChooser fc = new JFileChooser(dirPath);

      fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
      fc.setMultiSelectionEnabled(true);

      fc.setFileFilter(new FileFilter()
      {
         public boolean accept(File f)
         {
            if(f.isDirectory() || f.getName().toUpperCase().endsWith(".ZIP") || f.getName().toUpperCase().endsWith(".JAR"))
            {
               return true;
            }
            return false;
         }

         public String getDescription()
         {
            // i18n[HibernateConfigController.classpathEntryDesc=Jars, Zips or directories]
            return s_stringMgr.getString("HibernateController.classpathEntryDesc");
         }
      });

      if(JFileChooser.APPROVE_OPTION != fc.showOpenDialog(_plugin.getApplication().getMainFrame()))
      {
         return;
      }

      File[] files = fc.getSelectedFiles();

      DefaultListModel listModel = (DefaultListModel) _panel.lstClassPath.getModel();
      for (int i = 0; i < files.length; i++)
      {
         listModel.addElement(files[i].getPath());
      }

      if(0 < files.length)
      {
         if(null == files[0].getParent())
         {
            Preferences.userRoot().put(PERF_KEY_LAST_DIR, files[0].getPath());
         }
         else
         {
            Preferences.userRoot().put(PERF_KEY_LAST_DIR, files[0].getParent());
         }
      }
   }

   private void onNewConfig()
   {
      _panel.cboConfigs.setSelectedItem(null);
      initConfig(null);
   }

   private void initConfig(HibernateConfiguration config)
   {

      if(null == config)
      {
         _panel.txtConfigName.setText(null);


         DefaultListModel listModel = (DefaultListModel) _panel.lstClassPath.getModel();

         listModel.clear();


         _panel.txtFactoryProvider.setText(null);

         _panel.radConfiguration.setSelected(true);
         onObtainSessFactChanged();

         return;

      }

      _panel.txtConfigName.setText(config.getName());


      DefaultListModel listModel = (DefaultListModel) _panel.lstClassPath.getModel();

      listModel.clear();

      for (String path : config.getClassPathEntries())
      {
         listModel.addElement(path);
      }

      if(config.isUserDefinedProvider())
      {
         _panel.radUserDefProvider.setSelected(true);
      }
      else if(config.isJPA())
      {
         _panel.radJPA.setSelected(true);
      }
      else
      {
         _panel.radConfiguration.setSelected(true);
      }
      onObtainSessFactChanged();


      _panel.txtFactoryProvider.setText(config.getProvider());
      _panel.txtPersistenceUnitName.setText(config.getPersistenceUnitName());
   }




   public HibernateConfigPanel getPanel()
   {
      return _panel;
   }

   public void applyChanges()
   {
      try
      {
         if(onApplyConfigChanges())
         {
            File pluginUserSettingsFolder = _plugin.getPluginUserSettingsFolder();

            File cfgsFile = new File(pluginUserSettingsFolder.getPath(), HIBERNATE_CONFIGS_XML_FILE);

            XMLBeanWriter bw = new XMLBeanWriter();

            ArrayList<HibernateConfiguration> buf = new ArrayList<HibernateConfiguration>();
            for (int i = 0; i < _panel.cboConfigs.getItemCount(); i++)
            {
               HibernateConfiguration cfg = (HibernateConfiguration) _panel.cboConfigs.getItemAt(i);
               bw.addToRoot(cfg);
               buf.add(cfg);
            }

            bw.save(cfgsFile);

            if(null != _hibernatePrefsListener)
            {
               _hibernatePrefsListener.configurationChanged(buf);
            }


         }
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   public void initialize()
   {
      try
      {
         XMLBeanReader reader = new XMLBeanReader();
         File pluginUserSettingsFolder = _plugin.getPluginUserSettingsFolder();


         File xmlFile = new File(pluginUserSettingsFolder.getPath(), HIBERNATE_CONFIGS_XML_FILE);

         if(false == xmlFile.exists())
         {
            return;
         }

         reader.load(xmlFile, _plugin.getClass().getClassLoader());



         HibernateConfiguration toSel = null;

         for (Object o : reader)
         {
            HibernateConfiguration cfg = (HibernateConfiguration) o;

            if(null != _hibernatePrefsListener &&
               null != _hibernatePrefsListener.getPreselectedCfg() &&
               cfg.getName().equals(_hibernatePrefsListener.getPreselectedCfg().getName()))
            {
               toSel = cfg;
            }

            _panel.cboConfigs.addItem(cfg);
         }


         if(null != toSel)
         {
            _panel.cboConfigs.setSelectedItem(toSel);
         }
         else if(0 < _panel.cboConfigs.getItemCount())
         {
            _panel.cboConfigs.setSelectedItem(_panel.cboConfigs.getItemAt(0));
         }
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }
}
