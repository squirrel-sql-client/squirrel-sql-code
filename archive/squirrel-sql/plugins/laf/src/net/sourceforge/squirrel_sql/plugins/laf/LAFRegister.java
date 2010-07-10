package net.sourceforge.squirrel_sql.plugins.laf;
/*
 * Copyright (C) 2001-2002 Colin Bell
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
import java.awt.Frame;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.UIManager.LookAndFeelInfo;

import net.sourceforge.squirrel_sql.fw.gui.FontInfo;
import net.sourceforge.squirrel_sql.fw.util.MyURLClassLoader;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;

/**
 * Register of Look and Feels.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
class LAFRegister
{
	private final static int FONT_KEYS_ARRAY_OTHER = 0;
	private final static int FONT_KEYS_ARRAY_MENU = 1;
	private final static int FONT_KEYS_ARRAY_STATIC = 2;

	/** Logger for this class. */
	private static ILogger s_log = LoggerController.createLogger(LAFRegister.class);

	// What about these
	// Viewport.font, ColorChooser.font, InternalFrame.font,
	// OptionPane.font, "Panel.font",
	// ScrollPane.font, DesktopIcon.font
	// 

	private final static String[][] FONT_KEYS = {
		// Editable Text
		{
			"EditorPane.font",
			"List.font",
			"TextArea.font",
			"TextField.font",
			"PasswordField.font",
			"Table.font",
			"TableHeader.font",
			"TextPane.font",
			"Tree.font",
		},

		// Menus
		{
			"CheckBoxMenuItem.acceleratorFont",
			"CheckBoxMenuItem.font",
			"Menu.acceleratorFont",
			"Menu.font",
			"MenuBar.font",
			"MenuItem.acceleratorFont",
			"MenuItem.font",
			"PopupMenu.font",
			"RadioButtonMenuItem.acceleratorFont",
			"RadioButtonMenuItem.font",
		},

		// Static text
		{
			"Button.font",
			"CheckBox.font",
			"ComboBox.font",
			"InternalFrame.titleFont",
			"Label.font",
			"ProgressBar.font",
			"RadioButton.font",
			"TabbedPane.font",
			"TitledBorder.font",
			"ToggleButton.font",
			"ToolBar.font",
			"ToolTip.font",
		},
	};

	/** Application API. */
	private IApplication _app;

	/** Look and Feel plugin. */
	private LAFPlugin _plugin;

	/** Classloader for Look and Feel classes. */
	private MyURLClassLoader _lafClassLoader;

	/**
	 * Collection of <TT>ILookAndFeelController</TT> objects keyed by
	 * the Look and Feel class name.
	 */
	private Map _lafControllers = new HashMap();

	/**
	 * Default LAF controller. Used if a specialised one isn't available for
     * the LAF in _lafControllers.
     */
	private ILookAndFeelController _dftLAFController = new DefaultLookAndFeelController();

	/** <UI defaults prior to us modifying them. */
	private UIDefaults _origUIDefaults;

	/**
	 * Ctor. Load all Look and Feels from the Look and Feel folder. Set the
	 * current Look and Feel to that specified in the application preferences.
	 *
	 * @param	app	 Application API.
	 * @param	plugin  The LAF plugin.
	 *
	 * @throws	IllegalArgumentException
	 *			If <TT>IApplication</TT>, or <TT>LAFPlugin</TT> are <TT>null</TT>.
	 */
	LAFRegister(IApplication app, LAFPlugin plugin) throws IllegalArgumentException
	{
		super();
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

		try
		{
			_lafControllers.put(SkinLookAndFeelController.SKINNABLE_LAF_CLASS_NAME, new SkinLookAndFeelController(plugin));
		}
		catch (IOException ex)
		{
			s_log.error("Error storing SkinLookAndFeelController", ex);
		}
		try
		{
			_lafControllers.put(OyoahaLookAndFeelController.OA_LAF_CLASS_NAME, new OyoahaLookAndFeelController(plugin));
		}
		catch (IOException ex)
		{
			s_log.error("Error storing OyoahaLookAndFeelController", ex);
		}

		try
		{
			setLookAndFeel();
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
	 * @return	the ClassLoader used to load the look and feels.
	 */
	ClassLoader getLookAndFeelClassLoader()
	{
		return _lafClassLoader;
	}

	/**
	 * Get a Look and Feel Controller for the passed L&F class name.
	 * 
	 * @param	lafClassName	Look and Feel class name to get controller for.
	 * 
	 * @return	L&F Controller.
	 * 
	 * @throws	IllegalArgumentException	Thrown if <TT>null</TT>
	 * 										<TT>lafClassName</TT> passed.
	 */
	ILookAndFeelController getLookAndFeelController(String lafClassName)
	{
		if (lafClassName == null)
		{
			throw new IllegalArgumentException("lafClassName == null");
		}

		ILookAndFeelController ctrl = (ILookAndFeelController)_lafControllers.get(lafClassName);
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
			_app.getFontInfoStore().setStatusBarFontInfo(
				_plugin.getLAFPreferences().getStatusBarFontInfo());
		}
	}

	/**
	 * Set the current Look and Feel to that specified in the app preferences.
	 */
	void setLookAndFeel()
		throws
			ClassNotFoundException,
			IllegalAccessException,
			InstantiationException,
			UnsupportedLookAndFeelException
	{
		final LAFPreferences prefs = _plugin.getLAFPreferences();
		final String lafClassName = prefs.getLookAndFeelClassName();

		// Get Look and Feel class object.
		Class lafClass = null;
		if (_lafClassLoader != null)
		{
			lafClass = Class.forName(lafClassName, true, _lafClassLoader);
		}
		else
		{
			lafClass = Class.forName(lafClassName);
		}

		// Get the Look and Feel object.
		final LookAndFeel laf = (LookAndFeel)lafClass.newInstance();

		ILookAndFeelController lafCont = getLookAndFeelController(lafClassName);
		lafCont.aboutToBeInstalled(this, laf);

		// Set Look and Feel.
		if (_lafClassLoader != null)
		{
			UIManager.setLookAndFeel(laf);
			UIManager.getLookAndFeelDefaults().put("ClassLoader", _lafClassLoader);
		}
		else
		{
			UIManager.setLookAndFeel(laf);
		}

		lafCont.hasBeenInstalled(this, laf);
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
	void updateAllFrames()
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
		// Retrieve URLs of all the Look and Feel jars and store in lafUrls.
		List lafUrls = new ArrayList();
		File dir = _plugin.getLookAndFeelFolder();
		if (dir.isDirectory())
		{
			File[] files = dir.listFiles();
			for (int i = 0; i < files.length; ++i)
			{
				File jarFile = files[i];
				String jarFileName = jarFile.getAbsolutePath();
				if (jarFile.isFile()
					&& (jarFileName.toLowerCase().endsWith(".zip")
						|| jarFileName.toLowerCase().endsWith(".jar")))
				{
					try
					{
						lafUrls.add(jarFile.toURL());
					}
					catch (IOException ex)
					{
						s_log.error("Error occured reading Look and Feel jar: " + jarFileName, ex);
					}
				}
			}
		}

		// Create a ClassLoader for all the LAF jars. Install all Look and Feels
		// into the UIManager.
		try
		{
			_lafClassLoader = new MyURLClassLoader((URL[]) lafUrls.toArray(new URL[lafUrls.size()]));
			Class[] lafClasses = _lafClassLoader.getAssignableClasses(LookAndFeel.class, s_log);
			List lafNames = new ArrayList();
			for (int i = 0; i < lafClasses.length; ++i)
			{
				Class lafClass = lafClasses[i];
				try
				{
					LookAndFeel laf = (LookAndFeel) lafClass.newInstance();
					if (laf.isSupportedLookAndFeel())
					{
						LookAndFeelInfo info = new LookAndFeelInfo(laf.getName(), lafClass.getName());
						UIManager.installLookAndFeel(info);
						lafNames.add(lafClass.getName());
					}
				}
				catch (Throwable th)
				{
					s_log.error("Error occured loading Look and Feel: " + lafClass.getName(), th);
				}
			}
		}
		catch (Throwable th)
		{
			s_log.error("Error occured trying to load Look and Feel classes", th);
		}

	}
}