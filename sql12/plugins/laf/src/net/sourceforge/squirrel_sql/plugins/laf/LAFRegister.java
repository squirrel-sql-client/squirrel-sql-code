package net.sourceforge.squirrel_sql.plugins.laf;

/*
 * Copyright (C) 2001-2006 Colin Bell
 * colbell@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;
import net.sourceforge.squirrel_sql.fw.gui.FontInfo;
import net.sourceforge.squirrel_sql.fw.util.*;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.laf.flatlaf.FlatLookAndFeelController;
import net.sourceforge.squirrel_sql.plugins.laf.jtattoo.JTattooLookAndFeelController;
import net.sourceforge.squirrel_sql.plugins.laf.nimbus.NimbusLookAndFeelController;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import java.awt.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.*;

/**
 * Register of Look and Feels.
 * 
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class LAFRegister
{
	private final static int FONT_KEYS_ARRAY_OTHER = 0;

	private final static int FONT_KEYS_ARRAY_MENU = 1;

	private final static int FONT_KEYS_ARRAY_STATIC = 2;

	private static ILogger s_log = LoggerController.createLogger(LAFRegister.class);

	// TODO: What about these
	// Viewport.font, ColorChooser.font, InternalFrame.font,
	// OptionPane.font, "Panel.font",
	// ScrollPane.font, DesktopIcon.font
	private final static String[][] FONT_KEYS = {
			// Editable Text
			{ "EditorPane.font", "List.font", "TextArea.font", "TextField.font", "PasswordField.font",
					"Table.font", "TableHeader.font", "TextPane.font", "Tree.font", },
			// Menus
			{ "CheckBoxMenuItem.acceleratorFont", "CheckBoxMenuItem.font", "Menu.acceleratorFont", "Menu.font",
					"MenuBar.font", "MenuItem.acceleratorFont", "MenuItem.font", "PopupMenu.font",
					"RadioButtonMenuItem.acceleratorFont", "RadioButtonMenuItem.font", },
			// Static text
			{ "Button.font", "CheckBox.font", "ComboBox.font", "InternalFrame.titleFont", "Label.font", "defaultFont",
					"ProgressBar.font", "RadioButton.font", "TabbedPane.font", "TitledBorder.font",
					"ToggleButton.font", "ToolBar.font", "ToolTip.font", }, };

	/** Application API. */
	private IApplication _app;

	/** Look and Feel plugin. */
	private LAFPlugin _plugin;

	/** Classloader for Look and Feel classes. */
	private SquirrelURLClassLoader _lafClassLoader;

	/**
	 * Collection of <TT>ILookAndFeelController</TT> objects keyed by the Look and Feel class name.
	 */
	private Map<String, ILookAndFeelController> _lafControllers = new HashMap<String, ILookAndFeelController>();

	/**
	 * Default LAF controller. Used if a specialised one isn't available for the LAF in _lafControllers.
	 */
	private ILookAndFeelController _dftLAFController = new DefaultLookAndFeelController();

	/** <UI defaults prior to us modifying them. */
	private UIDefaults _origUIDefaults;

	/** factory for creating FileWrappers which insulate the application from direct reference to File */
	private FileWrapperFactory fileWrapperFactory = new FileWrapperFactoryImpl();

	/**
	 * Ctor. Load all Look and Feels from the Look and Feel folder. Set the current Look and Feel to that
	 * specified in the application preferences.
	 * 
	 * @param app
	 *           Application API.
	 * @param plugin
	 *           The LAF plugin.
	 * @throws IllegalArgumentException
	 *            If <TT>IApplication</TT>, or <TT>LAFPlugin</TT> are <TT>null</TT>.
	 */
	LAFRegister(IApplication app, LAFPlugin plugin) throws IllegalArgumentException
	{
		if (app == null)
		{
			throw new IllegalArgumentException("Null IApplication passed");
		}
		if (plugin == null)
		{
			throw new IllegalArgumentException("Null LAFPlugin passed");
		}
		_app = app;
		_plugin = plugin;
		// Save the current UI defaults.
		_origUIDefaults = (UIDefaults) UIManager.getDefaults().clone();
		installLookAndFeels();
		installLookAndFeelControllers(plugin);
		try
		{
			applyPreferences();
			setLookAndFeel(true);
		}
		catch (Throwable ex)
		{
			s_log.error("Error setting Look and Feel", ex);
		}
		try
		{
			updateApplicationFonts();
		}
		catch (Throwable ex)
		{
			s_log.error("Error updating application fonts", ex);
		}
	}

	LAFPlugin getPlugin()
	{
		return _plugin;
	}

	/**
	 * Return the class loader used to load the Look and Feels.
	 * 
	 * @return the ClassLoader used to load the look and feels.
	 */
	public ClassLoader getLookAndFeelClassLoader()
	{
		return _lafClassLoader;
	}

	/**
	 * Get a Look and Feel Controller for the passed L&F class name.
	 * 
	 * @param lafClassName
	 *           Look and Feel class name to get controller for.
	 * @return L&F Controller.
	 * @throws IllegalArgumentException
	 *            Thrown if <TT>null</TT> <TT>lafClassName</TT> passed.
	 */
	ILookAndFeelController getLookAndFeelController(String lafClassName)
	{
		if (lafClassName == null)
		{
			throw new IllegalArgumentException("lafClassName == null");
		}
		ILookAndFeelController ctrl = _lafControllers.get(lafClassName);
		if (ctrl == null)
		{
			ctrl = _dftLAFController;
		}
		return ctrl;
	}

	/**
	 * Set the font that the application uses for statusbars.
	 */
	void updateStatusBarFont()
	{
		if (_plugin.getLAFPreferences().isStatusBarFontEnabled())
		{
			_app.getFontInfoStore().setStatusBarFontInfo(_plugin.getLAFPreferences().getStatusBarFontInfo());
		}
	}

	/**
	 * Set the current Look and Feel to that specified in the app preferences.
	 */
	void setLookAndFeel(boolean force)
	{
		try
		{
			final LAFPreferences prefs = _plugin.getLAFPreferences();
			LookAndFeel laf = LAFLoader.getLafOrDefaultMetalOnError(prefs, _lafClassLoader);

			String lafClassName = laf.getClass().getName();


			// If a different LAF to the current one has been requested then
			// change to the requested LAF.
			LookAndFeel curLaf = UIManager.getLookAndFeel();
			if (force || curLaf == null || !curLaf.getName().equals(laf.getName()))
			{
				ILookAndFeelController lafCont = getLookAndFeelController(lafClassName);
				lafCont.aboutToBeInstalled(this, laf);

				// Set Look and Feel. If this is the Substance/JTattoo placeholder, skip it as it is not a real
				// look and feel. The controller will handle setting the look and feel using the UIManager.
				s_log.info("Current Look and Feel before L&F Plugin starts installing: " + curLaf.getName() + "(" + curLaf.getClass().getName() + ")");
				if (!PlaceholderLookAndFeel.isPlaceholder(lafClassName))
				{
					s_log.info("L&F Plugin starts installing: " + laf.getName() + "(" + lafClassName + ")");

					if (_lafClassLoader != null)
					{
						UIManager.put("ClassLoader", _lafClassLoader);
						SquirrelLookAndFeelHandler.setLookAndFeel(laf);
					}
					else
					{
						SquirrelLookAndFeelHandler.setLookAndFeel(laf);
					}
				}
				lafCont.hasBeenInstalled(this, laf);
				updateAllFrames();
			}
		}
		catch (Exception e)
		{
			throw Utilities.wrapRuntime(e);
		}
	}

	void applyPreferences()
	{
		final LAFPreferences prefs = _plugin.getLAFPreferences();
		JFrame.setDefaultLookAndFeelDecorated(prefs.getCanLAFSetBorder());
		JDialog.setDefaultLookAndFeelDecorated(prefs.getCanLAFSetBorder());
	}

	/**
	 * Update the applications fonts.
	 */
	void updateApplicationFonts()
	{
		final LAFPreferences prefs = _plugin.getLAFPreferences();
		FontInfo fi = prefs.getMenuFontInfo();
		String[] keys = FONT_KEYS[FONT_KEYS_ARRAY_MENU];
		for (int i = 0; i < keys.length; ++i)
		{
			if (prefs.isMenuFontEnabled())
			{
				if (fi != null)
				{
					UIManager.put(keys[i], fi.createFont());
				}
			}
			else
			{
				UIManager.put(keys[i], _origUIDefaults.getFont(keys[i]));
			}
		}
		fi = prefs.getStaticFontInfo();
		keys = FONT_KEYS[FONT_KEYS_ARRAY_STATIC];
		for (int i = 0; i < keys.length; ++i)
		{
			if (prefs.isStaticFontEnabled())
			{
				if (fi != null)
				{
					UIManager.put(keys[i], fi.createFont());
				}
			}
			else
			{
				UIManager.put(keys[i], _origUIDefaults.getFont(keys[i]));
			}
		}
		fi = prefs.getOtherFontInfo();
		keys = FONT_KEYS[FONT_KEYS_ARRAY_OTHER];
		for (int i = 0; i < keys.length; ++i)
		{
			if (prefs.isOtherFontEnabled())
			{
				if (fi != null)
				{
					UIManager.put(keys[i], fi.createFont());
				}
			}
			else
			{
				UIManager.put(keys[i], _origUIDefaults.getFont(keys[i]));
			}
		}
	}

	/**
	 * Update all open frames for the new Look and Feel info.
	 */
	private void updateAllFrames()
	{
		Frame[] frames = Frame.getFrames();
		if (frames != null)
		{
			for (int i = 0; i < frames.length; ++i)
			{
				SwingUtilities.updateComponentTreeUI(frames[i]);
				frames[i].pack();
			}
		}
	}

	/**
	 * Install Look and Feels from their jars.
	 */
	private void installLookAndFeels()
	{
		// Map of JAR file URLs containing LAFs keyed by the LAF class name.
		final Map<String, List<URL>> lafs = loadInstallProperties();

		final Collection<URL> lafUrls = new LinkedHashSet<>();

		// Put the lafplugin jar into the list of lafUrls as it contains a LAF placeholder (for Substance).
		String jarFilePath = _plugin.getPluginJarFilePath();
		try
		{
			lafUrls.add(new File(jarFilePath).toURI().toURL());
		}
		catch (Exception e)
		{
			s_log.error("Unable to add the plugin jar file (" + jarFilePath
				+ ") to the list of classloader URLs");
		}

		// Retrieve URLs of all the Look and Feel jars and store in lafUrls.
		for (List<URL> jarUrls : lafs.values())
		{
			lafUrls.addAll(jarUrls);
		}

		// Create a ClassLoader for all the LAF jars. Install all Look and Feels
		// into the UIManager.
		try
		{
			URL[] urls = new URL[lafUrls.size()];
			_lafClassLoader = new SquirrelURLClassLoader(lafUrls.toArray(urls));

			for (Iterator<String> it = lafs.keySet().iterator(); it.hasNext();)
			{
				String lafClassName = it.next();
				try
				{
					LookAndFeel laf = LAFLoader.getLafOrNullOnError(lafClassName, _lafClassLoader);

					if (null != laf && laf.isSupportedLookAndFeel())
					{
						LookAndFeelInfo info = new LookAndFeelInfo(laf.getName(), lafClassName);
						UIManager.installLookAndFeel(info);
					}
				}
				catch (Throwable th)
				{
					s_log.error("Error occurred loading Look and Feel: " + lafClassName, th);
				}
			}
		}
		catch (Throwable th)
		{
			s_log.error("Error occurred trying to load Look and Feel classes", th);
		}
	}

	/**
	 * Install the controllers for those LAFs that require them.
	 * 
	 * @param plugin
	 *           The LAF plugin.
	 */
	private void installLookAndFeelControllers(LAFPlugin plugin)
	{
		try
		{
			_lafControllers.put(SkinLookAndFeelController.SKINNABLE_LAF_CLASS_NAME,
				new SkinLookAndFeelController(plugin));
		}
		catch (Throwable ex)
		{
			s_log.error("Error installing SkinLookAndFeelController", ex);
		}

		try
		{
			PlasticLookAndFeelController ctrl = new PlasticLookAndFeelController(plugin, this);
			String[] ar = PlasticLookAndFeelController.LAF_CLASS_NAMES;
			for (int i = 0; i < ar.length; ++i)
			{
				_lafControllers.put(ar[i], ctrl);
			}
		}
		catch (Throwable ex)
		{
			s_log.error("Error installing PlasticLookAndFeelController", ex);
		}

		try
		{
			MetalLookAndFeelController ctrl = new MetalLookAndFeelController(plugin, this);
			_lafControllers.put(MetalLookAndFeelController.METAL_LAF_CLASS_NAME, ctrl);
		}
		catch (Throwable ex)
		{
			s_log.error("Error installing PlasticLookAndFeelController", ex);
		}

		try
		{
			NimbusLookAndFeelController ctrl = new NimbusLookAndFeelController(plugin, this);
			_lafControllers.put(NimbusLookAndFeelController.NIMBUS_LAF_CLASS_NAME, ctrl);
		}
		catch (Throwable ex)
		{
			s_log.error("Error installing PlasticLookAndFeelController", ex);
		}

		try
		{
			_lafControllers.put(TonicLookAndFeelController.TONIC_LAF_CLASS_NAME, new TonicLookAndFeelController(
				plugin));
		}
		catch (Throwable ex)
		{
			s_log.error("Error installing SkinLookAndFeelController", ex);
		}

		try
		{
			_lafControllers.put(RadianceLookAndFeelController.RADIANCE_LAF_PLACEHOLDER_CLASS_NAME,
				new RadianceLookAndFeelController(plugin, this));
		}
		catch (Throwable ex)
		{
			s_log.error("Error installing SubstanceLookAndFeelController", ex);
		}

		try
		{
			_lafControllers.put(JTattooLookAndFeelController.JTATTOO_LAF_PLACEHOLDER_CLASS_NAME,
				new JTattooLookAndFeelController(plugin, this));
		}
		catch (Throwable ex)
		{
			s_log.error("Error installing JTattooLookAndFeelController", ex);
		}

		try
		{
			_lafControllers.put(FlatLookAndFeelController.FLAT_LAF_PLACEHOLDER_CLASS_NAME,
			                    new FlatLookAndFeelController(plugin, this));
		}
		catch (Throwable ex)
		{
			s_log.error("Error installing FlatLookAndFeelController", ex);
		}

		// Initialize all the LAF controllers.
		for (Iterator<ILookAndFeelController> it = _lafControllers.values().iterator(); it.hasNext();)
		{
			it.next().initialize();
		}
	}

	/**
	 * Load the installation properties and return a <TT>Map</TT> keyed by the class name of the LAF and
	 * containing a URL to the jar file that contains the LAF.
	 * 
	 * @return Map
	 */
	private Map<String, List<URL>> loadInstallProperties()
	{
		Map<String, List<URL>> lafs = new HashMap<>();
		// Directory containing the standard LAF jar files.
		final FileWrapper stdLafJarDir = _plugin.getLookAndFeelFolder();
		// Load info about the standard LAFs that come with this plugin.
		PluginResources rsrc = _plugin.getResources();
		for (int i = 0; /* forever */; ++i)
		{
			try
			{
				String className = rsrc.getString(LAFPluginResources.IKeys.CLASSNAME + i);
				if (className == null || className.length() == 0)
				{
					break;
				}
				String jarNames = rsrc.getString(LAFPluginResources.IKeys.JARS + i);
				if (jarNames == null || jarNames.length() == 0)
				{
					break;
				}

				final String[] jarNamesArray = jarNames.split(";");

				if(0 == jarNamesArray.length)
				{
					break;
				}

				List<URL> jarUrls = getJarUrls(stdLafJarDir, jarNamesArray);
				lafs.put(className, jarUrls);

			}
			catch (MissingResourceException ignore)
			{
				// We have read in all properties.
				break;
			}
		}
		// Load info about any extra LAFs supplied by the user.
		try
		{
			final FileWrapper extraLafsDir = _plugin.getUsersExtraLAFFolder();
			FileWrapper extraFile = fileWrapperFactory.create(extraLafsDir, ILAFConstants.USER_EXTRA_LAFS_PROPS_FILE);

			BufferedInputStream is = new BufferedInputStream(extraFile.getFileInputStream());
			try
			{
				Properties props = new Properties();
				props.load(is);
				for (int i = 0; /* forever */; ++i)
				{
					String className = props.getProperty(LAFPluginResources.IKeys.CLASSNAME + i);
					if (className == null || className.length() == 0)
					{
						break;
					}
					String jarNames = props.getProperty(LAFPluginResources.IKeys.JARS + i);
					if (jarNames == null || jarNames.length() == 0)
					{
						jarNames = rsrc.getString(LAFPluginResources.IKeys.JAR + i);
					}
					if (jarNames == null || jarNames.length() == 0)
					{
						break;
					}

					final String[] jarNamesArray = jarNames.split(";");

					if(0 == jarNamesArray.length)
					{
						break;
					}

					List<URL> jarUrls = new ArrayList<>(1);
					for (String jarName : jarNamesArray)
					{
						FileWrapper file = fileWrapperFactory.create(extraLafsDir, jarName);
						try
						{
							if (file.isFile() && file.exists())
							{
								jarUrls.add(file.toURI().toURL());
							}
						}
						catch (IOException ex)
						{
							s_log.error("Error occurred reading Look and Feel jar: " + file.getAbsolutePath(), ex);
						}
					}
					if (!jarUrls.isEmpty())
					{
						lafs.put(className, jarUrls);
					}
				}
			}
			finally
			{
				is.close();
			}
		}
		catch (IOException ex)
		{
			s_log.error("Error occurred loading extra LAFs property file", ex);
		}
		return lafs;
	}

	private List<URL> getJarUrls(FileWrapper stdLafJarDir, String[] jarNamesArray)
	{
		List<URL> jarUrls = new ArrayList<>();
		for (String jarName : jarNamesArray)
		{
			FileWrapper file = fileWrapperFactory.create(stdLafJarDir, jarName);
			try
			{
				if (file.isFile() && file.exists())
				{
					jarUrls.add(file.toURI().toURL());
				}
			}
			catch (IOException ex)
			{
				s_log.error("Error occurred reading Look and Feel jar: " + file.getAbsolutePath(), ex);
			}
		}
		return jarUrls;
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

	public MetalLookAndFeelController getMetalLookAndFeelController()
	{
		return (MetalLookAndFeelController) _lafControllers.get(MetalLookAndFeelController.METAL_LAF_CLASS_NAME);
	}

	public FlatLookAndFeelController getFlatLafLookAndFeelController()
	{
		return (FlatLookAndFeelController) _lafControllers.get(FlatLookAndFeelController.FLAT_LAF_PLACEHOLDER_CLASS_NAME);
	}
}
