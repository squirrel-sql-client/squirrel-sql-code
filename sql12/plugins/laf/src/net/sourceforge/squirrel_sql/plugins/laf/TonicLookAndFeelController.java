package net.sourceforge.squirrel_sql.plugins.laf;
/*
 * Copyright (C) 2006 Colin Bell
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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.util.Iterator;

import javax.swing.JCheckBox;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;

import net.sourceforge.squirrel_sql.fw.id.IHasIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IntegerIdentifier;
import net.sourceforge.squirrel_sql.fw.util.DuplicateObjectException;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.xml.XMLObjectCache;
/**
 * Behaviour for the Tonic Look and Feel.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class TonicLookAndFeelController extends DefaultLookAndFeelController
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(TonicLookAndFeelController.class);


	/** Logger for this class. */
	private static final ILogger s_log =
		LoggerController.createLogger(TonicLookAndFeelController.class);

	/** Class name of the Tonic Look and Feel. */
	public static final String TONIC_LAF_CLASS_NAME =
		"com.digitprop.tonic.TonicLookAndFeel";

	/** Preferences for this LAF. */
	private TonicPreferences _prefs;

	/**
	 * Ctor specifying the Look and Feel plugin.
	 * 
	 * @param	plugin	The plugin that this controller is a part of.
	 */
	TonicLookAndFeelController(LAFPlugin plugin) throws IOException
	{
		super();

		XMLObjectCache cache = plugin.getSettingsCache();
		Iterator it = cache.getAllForClass(TonicPreferences.class);
		if (it.hasNext())
		{
			_prefs = (TonicPreferences)it.next();
		}
		else
		{
			_prefs = new TonicPreferences();
			try
			{
				cache.add(_prefs);
			}
			catch (DuplicateObjectException ex)
			{
				s_log.error("TonicPreferences object already in XMLObjectCache", ex);
			}
		}
	}

	/**
	 * This Look and Feel is about to be installed.
	 */
	public void aboutToBeInstalled(LAFRegister lafRegister, LookAndFeel laf)
	{
	}

	/**
	 * This Look and Feel has just been installed.
	 */
	public void hasBeenInstalled(LAFRegister lafRegister, LookAndFeel laf)
	{
		UIManager.getDefaults().put(
				"TabbedPane.thickBorders",
				Boolean.valueOf(_prefs.getUseTabbedPaneThickBorders()));
	}

	/**
	 * @see ILookAndFeelController#getPreferencesComponent()
	 */
	public BaseLAFPreferencesPanelComponent getPreferencesComponent()
	{
		return new TonicPrefsPanel(this);
	}

	private static final class TonicPrefsPanel extends BaseLAFPreferencesPanelComponent
	{
		/**
		 * This interface defines locale specific strings. This should be
		 * replaced with a property file.
		 */
		interface SkinPrefsPanelI18n
		{
			// i18n[laf.skinThemPack=Theme Pack:]
			//String THEME_PACK = s_stringMgr.getString("laf.skinThemPack");
			// i18n[laf.skinThemePackDir=Theme Pack Directory:]
			//String THEMEPACK_LOC = s_stringMgr.getString("laf.skinThemePackDir");
		}

		private TonicLookAndFeelController _ctrl;
		private JCheckBox _useThickBordersChk = new JCheckBox(s_stringMgr.getString("laf.tonicUseThickBorders"));

		TonicPrefsPanel(TonicLookAndFeelController ctrl)
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
			add(_useThickBordersChk, gbc);
		}
	
		/**
		 * @see BaseLAFPreferencesPanelComponent#loadPreferencesPanel()
		 */
		public void loadPreferencesPanel()
		{
			super.loadPreferencesPanel();
			_useThickBordersChk.setSelected(_ctrl._prefs.getUseTabbedPaneThickBorders());
		}

		/**
		 * @see BaseLAFPreferencesPanelComponent#applyChanges()
		 */
		public boolean applyChanges()
		{
			super.applyChanges();
			_ctrl._prefs.setUseTabbedPaneThickBorders(_useThickBordersChk.isSelected());
			
			// Force the LAF to be set even if Tonic is the current one. This
			// allows changes to take affect.
			return true;
		}
	}

	public static final class TonicPreferences implements IHasIdentifier
	{
		private boolean _useTabbedPaneThickBorders = false;
		private IntegerIdentifier _id = new IntegerIdentifier(1);

		public boolean getUseTabbedPaneThickBorders()
		{
			return _useTabbedPaneThickBorders;
		}

		public void setUseTabbedPaneThickBorders(boolean value)
		{
			_useTabbedPaneThickBorders = value;
		}

		/**
		 * @return		The unique identifier for this object.
		 */
		public IIdentifier getIdentifier()
		{
			return _id;
		}
	}
}

