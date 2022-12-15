package net.sourceforge.squirrel_sql.plugins.laf.jtattoo;

/*
 * Copyright (C) 2013 Rob Manning
 * manningr@users.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

import net.sourceforge.squirrel_sql.fw.id.IHasIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IntegerIdentifier;
import net.sourceforge.squirrel_sql.fw.util.DuplicateObjectException;
import net.sourceforge.squirrel_sql.fw.util.SquirrelLookAndFeelHandler;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.xml.XMLObjectCache;
import net.sourceforge.squirrel_sql.plugins.laf.BaseLAFPreferencesPanelComponent;
import net.sourceforge.squirrel_sql.plugins.laf.DefaultLookAndFeelController;
import net.sourceforge.squirrel_sql.plugins.laf.LAFPlugin;
import net.sourceforge.squirrel_sql.plugins.laf.LAFRegister;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.LookAndFeel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

/**
 * Behavior for JTattoo Look and Feel.
 * 
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class JTattooLookAndFeelController extends DefaultLookAndFeelController
{
	/** Class name of the JTattoo LAF class to use by default. This can be re-skinned at any time. */
	private static final String JTATTOO_LOOK_AND_FEEL_CLASS = 
		"com.jtattoo.plaf.acryl.AcrylLookAndFeel";
	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(JTattooLookAndFeelController.class);

	/** Logger for this class. */
	private static final ILogger s_log = LoggerController.createLogger(JTattooLookAndFeelController.class);

	/** Placeholder LAF that identifies itself as "JTattoo".  No other LAF does this. */
	public static final String JTATTOO_LAF_PLACEHOLDER_CLASS_NAME =
			new JTattooLafPlaceholder().getClass().getName();

	/** Preferences for this LAF. */
	private JTattooPreferences _prefs;

	private JTattooLafData _lafData = null;

	private ClassLoader _cl = null;
	
	/**
	 * Ctor specifying the Look and Feel plugin.
	 * 
	 * @param plugin
	 *           The plugin that this controller is a part of.
	 */
	public JTattooLookAndFeelController(LAFPlugin plugin, LAFRegister register) throws IOException
	{
		super();

		_cl = register.getLookAndFeelClassLoader();
		_lafData = new JTattooLafData(_cl);
		
		XMLObjectCache cache = plugin.getSettingsCache();
		Iterator<?> it = cache.getAllForClass(JTattooPreferences.class);
		if (it.hasNext())
		{
			_prefs = (JTattooPreferences) it.next();
		}
		else
		{
			_prefs = new JTattooPreferences();
			try
			{
				cache.add(_prefs);
			}
			catch (DuplicateObjectException ex)
			{
				s_log.error("JTattooPreferences object already in XMLObjectCache", ex);
			}
		}

	}

	/**
	 * This Look and Feel is about to be installed. Load the selected themepack.
	 */
	public void aboutToBeInstalled(LAFRegister lafRegister, LookAndFeel laf)
	{
	}

	/**
	 * This Look and Feel has just been installed.
	 */
	public void hasBeenInstalled(LAFRegister lafRegister, LookAndFeel laf)
	{
		final String skinName = _prefs.getSkinName();
		Class<?> skinClass = _lafData.getSkinClassForName(skinName);
		LookAndFeel skinObject;
		
		
		try
		{
			skinObject = (LookAndFeel)skinClass.getDeclaredConstructor().newInstance();
			
			// McWin requires special handling to make table header rows more visually appealing (square borders
			// instead of rounded ones that look like buttons)
			if (skinClass.getName().equals(JTattooLafData.MCWIN_LAF_CLASS_NAME)) {
				if (s_log.isInfoEnabled()) {
					s_log.info("Detected McWin L&F selection.  Setting theme to draw square buttons.");
				}
				// Setup the look and feel properties
            Properties props = new Properties();
            props.put("drawSquareButtons", "on");
            // Set theme
            com.jtattoo.plaf.mcwin.McWinLookAndFeel.setCurrentTheme(props);
			}

			SquirrelLookAndFeelHandler.setLookAndFeel(skinObject);
			UIManager.getLookAndFeelDefaults().put("ClassLoader", _cl);
		}
		catch (InstantiationException e)
		{
			// skinClass.newInstance();
			s_log.error("Unable to instantiate skinClass ("+skinName+"):"+e.getMessage(), e);
		}
		catch (IllegalAccessException e)
		{
			// skinClass.newInstance();
			s_log.error("Unable to instantiate skinClass ("+skinName+"):"+e.getMessage(), e);
		}
		catch (UnsupportedLookAndFeelException e)
		{
			// UIManager.setLookAndFeel
			s_log.error("Unable to set look and feel using skinClass("+skinName+"):"+e.getMessage(), e);
		}
		catch (InvocationTargetException e)
		{
			s_log.error("Unable to instantiate skinClass ("+skinName+"):"+e.getMessage(), e);
		}
		catch (NoSuchMethodException e)
		{
			s_log.error("Unable to instantiate skinClass ("+skinName+"):"+e.getMessage(), e);
		}
	}

	/**
	 * @see ILookAndFeelController#getPreferencesComponent()
	 */
	public BaseLAFPreferencesPanelComponent getPreferencesComponent()
	{
		return new JTattooSkinPrefsPanel(this);
	}

	private static final class JTattooSkinPrefsPanel extends BaseLAFPreferencesPanelComponent
	{

		interface SkinPrefsPanelI18n
		{
			// i18n[JTattooLookAndFeelController.JTattooSkinLabel=JTattoo Skin:]
			String SKIN_LABEL = s_stringMgr.getString("JTattooLookAndFeelController.JTattooSkinLabel");
		}

		private JTattooLookAndFeelController _ctrl;

		private JComboBox _skinCmb = new JComboBox();

		JTattooSkinPrefsPanel(JTattooLookAndFeelController ctrl)
		{
			super(new GridBagLayout());
			_ctrl = ctrl;
			createUserInterface();
		}

		private void createUserInterface()
		{
			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.anchor = GridBagConstraints.WEST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(4, 4, 4, 4);

			gbc.gridx = 0;
			gbc.gridy = 0;
			add(new JLabel(SkinPrefsPanelI18n.SKIN_LABEL, SwingConstants.RIGHT), gbc);

			++gbc.gridx;
			add(_skinCmb, gbc);
		}

		/**
		 * @see BaseLAFPreferencesPanelComponent#loadPreferencesPanel()
		 */
		public void loadPreferencesPanel()
		{
			super.loadPreferencesPanel();
			Set<String> JTattooThemes = _ctrl._lafData.getSkins();
			Object[] comboItems = new Object[JTattooThemes.size()];
			int count = 0;
			for (String theme : JTattooThemes)
			{
				comboItems[count++] = theme;
			}
			ComboBoxModel model = new DefaultComboBoxModel(comboItems);
			_skinCmb.setModel(model);
			_skinCmb.setSelectedItem(_ctrl._prefs.getSkinName());
			if (_skinCmb.getSelectedIndex() == -1 && _skinCmb.getModel().getSize() > 0)
			{
				_skinCmb.setSelectedIndex(0);
			}
		}

		/**
		 * @see BaseLAFPreferencesPanelComponent#applyChanges()
		 */
		public boolean applyChanges()
		{
			super.applyChanges();
			_ctrl._prefs.setSkinName((String) _skinCmb.getSelectedItem());
			return true;
		}
	}

	public static final class JTattooPreferences implements IHasIdentifier
	{

		private String _skinName;

		private IntegerIdentifier _id = new IntegerIdentifier(1);

		public String getSkinName()
		{
			return _skinName;
		}

		public void setSkinName(String value)
		{
			_skinName = value;
		}

		/**
		 * @return The unique identifier for this object.
		 */
		public IIdentifier getIdentifier()
		{
			return _id;
		}
	}
}
