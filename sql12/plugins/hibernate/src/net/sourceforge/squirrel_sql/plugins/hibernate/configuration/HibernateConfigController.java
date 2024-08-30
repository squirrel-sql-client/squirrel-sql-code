package net.sourceforge.squirrel_sql.plugins.hibernate.configuration;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import net.sourceforge.squirrel_sql.fw.gui.ClipboardUtil;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.props.Props;
import net.sourceforge.squirrel_sql.fw.util.FileWrapper;
import net.sourceforge.squirrel_sql.fw.util.FileWrapperFactory;
import net.sourceforge.squirrel_sql.fw.util.FileWrapperFactoryImpl;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
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
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(HibernateConfigController.class);

	private HibernatePlugin _plugin;

	private HibernateConfigPanel _panel;

	static final String PERF_KEY_LAST_DIR = "Squirrel.Hibernate.lastDir";

	public static final String HIBERNATE_CONFIGS_XML_FILE_OLD = "hibernateConfigs.xml";

	public static final String HIBERNATE_CONFIGS_XML_FILE = "hibernateConfigs32.xml";

	private HibernatePrefsListener _hibernatePrefsListener;

	private ProcessDetails _processDetails;

	/** factory for creating FileWrappers which insulate the application from direct reference to File */
	private FileWrapperFactory fileWrapperFactory = new FileWrapperFactoryImpl();

	private JpaConnectionConfigCtrl _jpaConnectionConfigCtrl;

	public HibernateConfigController(HibernatePlugin plugin)
	{
		_plugin = plugin;
		_processDetails = new ProcessDetails(_plugin);

		_panel = new HibernateConfigPanel(_plugin.getResources());

		_hibernatePrefsListener = _plugin.removeHibernatePrefsListener();

		_jpaConnectionConfigCtrl = new JpaConnectionConfigCtrl(_panel._jpaConnectionConfigPanel, _plugin);

		_panel.btnNewConfig.addActionListener(e -> onNewConfig());

		_panel.btnRemoveConfig.addActionListener(e -> onRemoveConfig());

		_panel.btnCopyConfig.addActionListener(e -> onCopyConfig());



		_panel.btnClassPathAdd.addActionListener(e -> onAddClasspathEntry());

		_panel.btnClassPathDirAdd.addActionListener(e -> onAddClassPathDir());
		_panel.btnClassPathAddFormClip.addActionListener(e -> onAddClassPathFromClip());

		_panel.btnClassPathReplace.addActionListener(e -> onReplaceClasspathEntry());

		_panel.btnClassPathRemove.addActionListener(e -> onRemoveSelectedClasspathEntries());

		_panel.btnClassPathMoveUp.addActionListener(e -> onMoveUpClasspathEntries());

		_panel.btnClassPathMoveDown.addActionListener(e -> onMoveDownClasspathEntries());

		_panel.btnCopyClasspathToClip.addActionListener(e -> onCopyClasspathToClip());

		_panel.btnApplyConfigChanges.addActionListener(e -> onApplyConfigChanges(false));

		_panel.cboConfigs.addItemListener(e -> onSelectedConfigChanged(e));


		ItemListener radProcessListener = e -> onProcessChanged();

		_panel.radCreateProcess.addItemListener(radProcessListener);
		_panel.radInVM.addItemListener(radProcessListener);

		_panel.btnProcessDetails.addActionListener(e -> onProcessDetails());
	}

	private void onCopyClasspathToClip()
	{
		ClassPathItemListModel model = (ClassPathItemListModel) _panel.lstClassPath.getModel();
		String classpath = Arrays.stream(model.getClassPathArray()).collect(Collectors.joining(File.pathSeparator + "\n"));
		ClipboardUtil.copyToClip(classpath, true);
	}

	private void onAddClassPathFromClip()
	{
		ArrayList<String> cpEntries = new ArrayList<>();

		String clipboardAsString = ClipboardUtil.getClipboardAsString();
		String[] splits = clipboardAsString.split("\n");

		for( String split : splits )
		{
			cpEntries.addAll(Arrays.asList(split.trim().split(File.pathSeparator)));
		}

		for( String cpEntry : cpEntries )
		{
			if(false == StringUtilities.isEmpty(cpEntry, true))
			{
				getClassPathListModel().addJar(cpEntry.trim());
			}
		}
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


	private void onRemoveConfig()
	{
		HibernateConfiguration selConfig = (HibernateConfiguration) _panel.cboConfigs.getSelectedItem();

		if (null == selConfig)
		{
			JOptionPane.showMessageDialog(GUIUtils.getOwningFrame(_panel), s_stringMgr.getString("HibernateController.NoConfigToRemove"));
			return;
		}

		if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(GUIUtils.getOwningFrame(_panel), s_stringMgr.getString("HibernateController.ReallyRemoveConfig", selConfig)))
		{
			((DefaultComboBoxModel) _panel.cboConfigs.getModel()).removeElement(selConfig);
		}
	}

	private void onCopyConfig()
	{
		HibernateConfiguration selConfig = (HibernateConfiguration) _panel.cboConfigs.getSelectedItem();

		if (null == selConfig)
		{
			JOptionPane.showMessageDialog(GUIUtils.getOwningFrame(_panel), s_stringMgr.getString("HibernateController.NoConfigToCopy"));
			return;
		}

		CopyConfigCtrl copyConfigCtrl = new CopyConfigCtrl(GUIUtils.getOwningFrame(_panel), selConfig, _panel.cboConfigs);

		String newName = copyConfigCtrl.getCopiedConfigName();

		if(StringUtilities.isEmpty(newName))
		{
			return;
		}

		HibernateConfiguration newConfig = Utilities.cloneObject(selConfig, selConfig.getClass().getClassLoader());
		newConfig.setName(newName);

		_panel.cboConfigs.addItem(newConfig);
		_panel.cboConfigs.setSelectedItem(newConfig);
	}

	private void onRemoveSelectedClasspathEntries()
	{
		int[] selIces = _panel.lstClassPath.getSelectedIndices();

		List<ClassPathItem> toRemove = new ArrayList<>();
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
		if(false == _jpaConnectionConfigCtrl.checkValid(silent))
		{
			return false;
		}


		String cfgName = _panel.txtConfigName.getText();

		if (null == cfgName || 0 == cfgName.trim().length())
		{
			if (false == silent)
			{
				JOptionPane.showMessageDialog(GUIUtils.getOwningFrame(_panel),s_stringMgr.getString("HibernateController.noProviderMsg"));
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

		_jpaConnectionConfigCtrl.saveConfiguration(cfg);

		cfg.setName(cfgName);

		ClassPathItem[] classPathEntries = new ClassPathItem[getClassPathListModel().getSize()];

		for (int i = 0; i < getClassPathListModel().getSize(); ++i)
		{
			classPathEntries[i] = getClassPathListModel().getClassPathItemAt(i);
		}

      cfg.setClassPathItems(classPathEntries);

		cfg.setUseProcess(_panel.radCreateProcess.isSelected());

		_processDetails.apply(cfg);

		if (wasNull)
		{
			_panel.cboConfigs.addItem(cfg);
			_panel.cboConfigs.setSelectedItem(cfg);

		}

		return true;
	}


	private void onAddClassPathDir()
   {
      File[] files = chooseClassPathDirs();

      for (int i = 0; i < files.length; i++)
      {
         getClassPathListModel().addJarDir(files[i].getPath());
      }
   }

	private File[] chooseClassPathDirs()
	{
		String dirPath = Props.getString(PERF_KEY_LAST_DIR, System.getProperty("user.home"));

		JFileChooser fc = new JFileChooser(dirPath);
		fc.setDialogTitle(s_stringMgr.getString("HibernatePanel.classPathDirAdd.filechooser.title"));

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
			return new File[0];
		}

		File[] files = fc.getSelectedFiles();

		if (0 < files.length)
		{
			Props.putString(PERF_KEY_LAST_DIR, files[0].getPath());
		}

		return files;
	}


	private void onAddClasspathEntry()
	{
		File[] files = chooseClasspathEntries();

		for (int i = 0; i < files.length; i++)
		{
			getClassPathListModel().addJar(files[i].getPath());
		}
	}

	private File[] chooseClasspathEntries()
	{
		String dirPath = Props.getString(PERF_KEY_LAST_DIR, System.getProperty("user.home"));

		JFileChooser fc = new JFileChooser(dirPath);
		fc.setDialogTitle(s_stringMgr.getString("HibernatePanel.classPathAdd.filechooser.title"));


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
			return new File[0];
		}

		File[] files = fc.getSelectedFiles();

		if (0 < files.length)
		{
			if (null == files[0].getParent())
			{
				Props.putString(PERF_KEY_LAST_DIR, files[0].getPath());
			}
			else
			{
				Props.putString(PERF_KEY_LAST_DIR, files[0].getParent());
			}
		}

		return files;
	}


	private void onReplaceClasspathEntry()
	{
		ClassPathItem firstSelectedItem = (ClassPathItem) _panel.lstClassPath.getSelectedValue();

		if(null == firstSelectedItem)
		{
			JOptionPane.showMessageDialog(GUIUtils.getOwningFrame(_panel), s_stringMgr.getString("HibernatePanel.classPathReplace.no.selection"));
			return;
		}

		List<ClassPathItem> selectedItems = _panel.lstClassPath.getSelectedValuesList();

		int[] addedIndices;
		if(firstSelectedItem.isJarDir())
		{
			File[] dirs = chooseClassPathDirs();

			if(0 == dirs.length)
			{
				return;
			}

			addedIndices = getClassPathListModel().replaceByJarDirs(selectedItems, dirs);
		}
		else
		{
			File[] entries = chooseClasspathEntries();

			if(0 == entries.length)
			{
				return;
			}

			addedIndices = getClassPathListModel().replaceByJars(selectedItems, entries);
		}

		_panel.lstClassPath.setSelectedIndices(addedIndices);

		_panel.lstClassPath.ensureIndexIsVisible(addedIndices[0]);
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

		_jpaConnectionConfigCtrl.init(cfg);

		if (null == cfg)
		{
			_panel.txtConfigName.setText(null);


			getClassPathListModel().clear();



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
