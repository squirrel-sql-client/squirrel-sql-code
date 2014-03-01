package net.sourceforge.squirrel_sql.plugins.hibernate.configuration;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import net.sourceforge.squirrel_sql.fw.util.FileWrapper;
import net.sourceforge.squirrel_sql.fw.util.FileWrapperFactory;
import net.sourceforge.squirrel_sql.fw.util.FileWrapperFactoryImpl;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanReader;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanWriter;
import net.sourceforge.squirrel_sql.plugins.hibernate.HibernatePlugin;
import net.sourceforge.squirrel_sql.plugins.hibernate.HibernatePrefsListener;
import net.sourceforge.squirrel_sql.plugins.hibernate.server.ClassPathItem;
import net.sourceforge.squirrel_sql.plugins.hibernate.server.HibernateConfiguration;
import net.sourceforge.squirrel_sql.plugins.hibernate.util.HibernateUtil;

public class HibernateConfigController
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(HibernateConfigController.class);

	private HibernatePlugin _plugin;

	private HibernateConfigPanel _panel;

	static final String PERF_KEY_LAST_DIR = "Squirrel.Hibernate.lastDir";

	public static final String HIBERNATE_CONFIGS_XML_FILE_OLD = "hibernateConfigs.xml";

	public static final String HIBERNATE_CONFIGS_XML_FILE = "hibernateConfigs32.xml";

	private HibernatePrefsListener _hibernatePrefsListener;

	private ProcessDetails _processDetails;

	/** factory for creating FileWrappers which insulate the application from direct reference to File */
	private FileWrapperFactory fileWrapperFactory = new FileWrapperFactoryImpl();

	public HibernateConfigController(HibernatePlugin plugin)
	{
		_plugin = plugin;
		_processDetails = new ProcessDetails(_plugin);

		_panel = new HibernateConfigPanel(_plugin.getResources());

		_hibernatePrefsListener = _plugin.removeHibernatePrefsListener();


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

		_panel.btnClassPathDirAdd.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				onAddClassPathDir();
			}
		});

		_panel.btnClassPathRemove.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				onRemoveSelectedClasspathEntries();
			}
		});

		_panel.btnClassPathMoveUp.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				onMoveUpClasspathEntries();
			}
		});

		_panel.btnClassPathMoveDown.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				onMoveDownClasspathEntries();
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
				onApplyConfigChanges(false);
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

		ItemListener radProcessListener = new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				onProcessChanged();
			}
		};

		_panel.radCreateProcess.addItemListener(radProcessListener);
		_panel.radInVM.addItemListener(radProcessListener);

		_panel.btnProcessDetails.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				onProcessDetails();
			}
		});
	}

	private void onProcessDetails()
	{
      new ProcessDetailsController(_plugin, _processDetails, getClassPathListModel().getClassPathArray());
	}

   private ClassPathItemListModel getClassPathListModel()
   {
      return (ClassPathItemListModel) _panel.lstClassPath.getModel();
   }

   private void onProcessChanged()
	{
		_panel.btnProcessDetails.setEnabled(_panel.radCreateProcess.isSelected());
	}

	private void onObtainSessFactChanged()
	{
		if (_panel.radUserDefProvider.isSelected())
		{
			_panel.btnEditFactoryProviderInfo.setEnabled(true);
			_panel.txtPersistenceUnitName.setEnabled(false);
		}
		else if (_panel.radJPA.isSelected())
		{
			_panel.btnEditFactoryProviderInfo.setEnabled(false);
			_panel.txtPersistenceUnitName.setEnabled(true);
		}
		else if (_panel.radConfiguration.isSelected())
		{
			_panel.btnEditFactoryProviderInfo.setEnabled(false);
			_panel.txtPersistenceUnitName.setEnabled(false);
		}
	}

	private void onRemoveConfig()
	{
		HibernateConfiguration selConfig = (HibernateConfiguration) _panel.cboConfigs.getSelectedItem();

		if (null == selConfig)
		{
			// i18n[HibernateConfigController.NoConfigToRemove=No configuration selected to remove.]
			JOptionPane.showMessageDialog(_plugin.getApplication().getMainFrame(),
				s_stringMgr.getString("HibernateController.NoConfigToRemove"));
		}
		else
		{
			// i18n[HibernateConfigController.ReallyRemoveConfig=Are you sure you want to delete configuration
			// "{0}".]
			if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(_plugin.getApplication().getMainFrame(),
				s_stringMgr.getString("HibernateController.ReallyRemoveConfig", selConfig)))
			{
				((DefaultComboBoxModel) _panel.cboConfigs.getModel()).removeElement(selConfig);
			}
		}
	}

	private void onRemoveSelectedClasspathEntries()
	{
		int[] selIces = _panel.lstClassPath.getSelectedIndices();

		List<ClassPathItem> toRemove = new ArrayList<ClassPathItem>();
		DefaultListModel listModel = (DefaultListModel) _panel.lstClassPath.getModel();
		for (int i = 0; i < selIces.length; i++)
		{
			toRemove.add((ClassPathItem) listModel.getElementAt(selIces[i]));
		}

		for (ClassPathItem ci : toRemove)
		{
			listModel.removeElement(ci);
		}
	}

	private void onSelectedConfigChanged(ItemEvent e)
	{
		if (ItemEvent.SELECTED == e.getStateChange() && null != e.getItem())
		{
			initConfig((HibernateConfiguration) e.getItem());
		}
		else if (ItemEvent.DESELECTED == e.getStateChange() && null != e.getItem())
		{
			initConfig(null);
		}
	}

	private boolean onApplyConfigChanges(boolean silent)
	{
		String provider = _panel.txtFactoryProvider.getText();
		String persistenceUnitName = _panel.txtPersistenceUnitName.getText();

		if (_panel.radUserDefProvider.isSelected() && (null == provider || 0 == provider.trim().length()))
		{
			if (false == silent)
			{
				// i18n[HibernateConfigController.noProviderMsg=Missing SessionFactoryImplProvider .\nChanges
				// cannot be applied.]
				JOptionPane.showMessageDialog(_plugin.getApplication().getMainFrame(),
					s_stringMgr.getString("HibernateController.noProviderMsg"));
			}
			return false;
		}

		if (_panel.radJPA.isSelected()
			&& (null == persistenceUnitName || 0 == persistenceUnitName.trim().length()))
		{
			if (false == silent)
			{
				// i18n[HibernateConfigController.noPersistenceUnitName=Missing Persitence-Unit name .\nChanges
				// cannot be applied.]
				JOptionPane.showMessageDialog(_plugin.getApplication().getMainFrame(),
					s_stringMgr.getString("HibernateController.noPersistenceUnitName"));
			}
			return false;
		}

		String cfgName = _panel.txtConfigName.getText();

		if (null == cfgName || 0 == cfgName.trim().length())
		{
			if (false == silent)
			{
				// i18n[HibernateConfigController.noCfgNameMsg=Not a valid configuration name\nChanges cannot be
				// applied.]
				JOptionPane.showMessageDialog(_plugin.getApplication().getMainFrame(),
					s_stringMgr.getString("HibernateController.noProviderMsg"));
			}
			return false;
		}

		HibernateConfiguration cfg = (HibernateConfiguration) _panel.cboConfigs.getSelectedItem();

		boolean wasNull = false;
		if (null == cfg)
		{
			wasNull = true;
			cfg = new HibernateConfiguration();
		}

		cfg.setProvider(provider);
		cfg.setPersistenceUnitName(persistenceUnitName);
		cfg.setName(cfgName);

		ClassPathItem[] classPathEntries = new ClassPathItem[getClassPathListModel().getSize()];

		for (int i = 0; i < getClassPathListModel().getSize(); ++i)
		{
			classPathEntries[i] = getClassPathListModel().getClassPathItemAt(i);
		}

      cfg.setClassPathItems(classPathEntries);

		if (_panel.radUserDefProvider.isSelected())
		{
			cfg.setUserDefinedProvider(true);
			cfg.setJPA(false);
		}
		else if (_panel.radJPA.isSelected())
		{
			cfg.setUserDefinedProvider(false);
			cfg.setJPA(true);
		}
		else
		{
			cfg.setUserDefinedProvider(false);
			cfg.setJPA(false);
		}

		cfg.setUseProcess(_panel.radCreateProcess.isSelected());

		_processDetails.apply(cfg);

		if (wasNull)
		{
			_panel.cboConfigs.addItem(cfg);
			_panel.cboConfigs.setSelectedItem(cfg);

		}

		return true;
	}

	private void onFactoryProviderInfo()
	{
		String className =
			new FactoryProviderController(_plugin, _panel.txtFactoryProvider.getText()).getClassName();
		_panel.txtFactoryProvider.setText(className);

	}


   private void onAddClassPathDir()
   {
      String dirPath = Preferences.userRoot().get(PERF_KEY_LAST_DIR, System.getProperty("user.home"));

      JFileChooser fc = new JFileChooser(dirPath);

      fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
      fc.setMultiSelectionEnabled(true);

      fc.setFileFilter(new FileFilter()
      {
         public boolean accept(File f)
         {
            if (f.isDirectory())
            {
               return true;
            }
            return false;
         }

         public String getDescription()
         {
            // i18n[HibernateConfigController.classpathEntryDesc=Jars, Zips or directories]
            return s_stringMgr.getString("HibernateController.classpathDirEntryDesc");
         }
      });

      if (JFileChooser.APPROVE_OPTION != fc.showOpenDialog(_plugin.getApplication().getMainFrame()))
      {
         return;
      }

      File[] files = fc.getSelectedFiles();

      for (int i = 0; i < files.length; i++)
      {
         getClassPathListModel().addJarDir(files[i].getPath());
      }

      if (0 < files.length)
      {
         Preferences.userRoot().put(PERF_KEY_LAST_DIR, files[0].getPath());
      }
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
				if (f.isDirectory() || f.getName().toUpperCase().endsWith(".ZIP")
					|| f.getName().toUpperCase().endsWith(".JAR"))
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

		if (JFileChooser.APPROVE_OPTION != fc.showOpenDialog(_plugin.getApplication().getMainFrame()))
		{
			return;
		}

		File[] files = fc.getSelectedFiles();

		for (int i = 0; i < files.length; i++)
		{
			getClassPathListModel().addJar(files[i].getPath());
		}

		if (0 < files.length)
		{
			if (null == files[0].getParent())
			{
				Preferences.userRoot().put(PERF_KEY_LAST_DIR, files[0].getPath());
			}
			else
			{
				Preferences.userRoot().put(PERF_KEY_LAST_DIR, files[0].getParent());
			}
		}
	}

	private void onMoveUpClasspathEntries()
	{
		int[] selIx = _panel.lstClassPath.getSelectedIndices();

		if (null == selIx || 0 == selIx.length)
		{
			return;
		}


		for (int i : selIx)
		{
			if (0 == i)
			{
				return;
			}
		}

		int[] newSelIx = new int[selIx.length];
		for (int i = 0; i < selIx.length; ++i)
		{
			ClassPathItem item = (ClassPathItem) getClassPathListModel().remove(selIx[i]);
			newSelIx[i] = selIx[i] - 1;
			getClassPathListModel().insertElementAt(item, newSelIx[i]);
		}

		_panel.lstClassPath.setSelectedIndices(newSelIx);

		_panel.lstClassPath.ensureIndexIsVisible(newSelIx[0]);

	}

	private void onMoveDownClasspathEntries()
	{
		int[] selIx = _panel.lstClassPath.getSelectedIndices();

		if (null == selIx || 0 == selIx.length)
		{
			return;
		}


		for (int i : selIx)
		{
			if (getClassPathListModel().getSize() - 1 == i)
			{
				return;
			}
		}

		int[] newSelIx = new int[selIx.length];
		for (int i = selIx.length - 1; i >= 0; --i)
		{
			ClassPathItem item = (ClassPathItem) getClassPathListModel().remove(selIx[i]);
			newSelIx[i] = selIx[i] + 1;
			getClassPathListModel().insertElementAt(item, newSelIx[i]);
		}

		_panel.lstClassPath.setSelectedIndices(newSelIx);

		_panel.lstClassPath.ensureIndexIsVisible(newSelIx[newSelIx.length - 1]);

	}

	private void onNewConfig()
	{
		_panel.cboConfigs.setSelectedItem(null);
		initConfig(null);
	}

	private void initConfig(HibernateConfiguration cfg)
	{

		if (null == cfg)
		{
			_panel.txtConfigName.setText(null);


			getClassPathListModel().clear();

			_panel.txtFactoryProvider.setText(null);

			_panel.radConfiguration.setSelected(true);
			onObtainSessFactChanged();

			_panel.radCreateProcess.setSelected(true);

			_panel.btnProcessDetails.setEnabled(true);

			return;

		}

		_panel.txtConfigName.setText(cfg.getName());


		getClassPathListModel().clear();

		for (ClassPathItem path : cfg.getClassPathItems())
		{
			getClassPathListModel().addItem(path);
		}

		if (cfg.isUserDefinedProvider())
		{
			_panel.radUserDefProvider.setSelected(true);
		}
		else if (cfg.isJPA())
		{
			_panel.radJPA.setSelected(true);
		}
		else
		{
			_panel.radConfiguration.setSelected(true);
		}
		onObtainSessFactChanged();

		_panel.txtFactoryProvider.setText(cfg.getProvider());
		_panel.txtPersistenceUnitName.setText(cfg.getPersistenceUnitName());

		_panel.radCreateProcess.setSelected(cfg.isUseProcess());
		_panel.btnProcessDetails.setEnabled(cfg.isUseProcess());
		_panel.radInVM.setSelected(!cfg.isUseProcess());

		if (null != cfg.getCommand())
		{
			_processDetails.setCommand(cfg.getCommand());
			_processDetails.setEndProcessOnDisconnect(cfg.isEndProcessOnDisconnect());
		}
	}

	public HibernateConfigPanel getPanel()
	{
		return _panel;
	}

	public void applyChanges()
	{
		try
		{
			if (onApplyConfigChanges(true))
			{
				FileWrapper pluginUserSettingsFolder = _plugin.getPluginUserSettingsFolder();

				FileWrapper cfgsFile =
					fileWrapperFactory.create(pluginUserSettingsFolder, HIBERNATE_CONFIGS_XML_FILE);

				XMLBeanWriter bw = new XMLBeanWriter();

				ArrayList<HibernateConfiguration> buf = new ArrayList<HibernateConfiguration>();
				for (int i = 0; i < _panel.cboConfigs.getItemCount(); i++)
				{
					HibernateConfiguration cfg = (HibernateConfiguration) _panel.cboConfigs.getItemAt(i);
					bw.addToRoot(cfg);
					buf.add(cfg);
				}

				bw.save(cfgsFile);

				if (null != _hibernatePrefsListener)
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
			XMLBeanReader reader = HibernateUtil.createHibernateConfigsReader(_plugin);

			if (reader == null)
			{
				return;
			}

			HibernateConfiguration toSel = null;

			for (Object o : reader)
			{
				HibernateConfiguration cfg = (HibernateConfiguration) o;

				if (null != _hibernatePrefsListener && null != _hibernatePrefsListener.getPreselectedCfg()
					&& cfg.getName().equals(_hibernatePrefsListener.getPreselectedCfg().getName()))
				{
					toSel = cfg;
				}

				if (null == cfg.getCommand())
				{
					_processDetails.apply(cfg);
				}

				_panel.cboConfigs.addItem(cfg);
			}

			if (null != toSel)
			{
				_panel.cboConfigs.setSelectedItem(toSel);
			}
			else if (0 < _panel.cboConfigs.getItemCount())
			{
				_panel.cboConfigs.setSelectedItem(_panel.cboConfigs.getItemAt(0));
			}
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param fileWrapperFactory
	 *           the fileWrapperFactory to set
	 */
	public void setFileWrapperFactory(FileWrapperFactory fileWrapperFactory)
	{
		Utilities.checkNull("setFileWrapperFactory", "fileWrapperFactory", fileWrapperFactory);
		this.fileWrapperFactory = fileWrapperFactory;
	}

}
