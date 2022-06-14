package net.sourceforge.squirrel_sql.plugins.laf;

/*
 * Copyright (C) 2010 Rob Manning
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
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.Set;

/**
 * Behavior for the Radiance Look and Feel.
 * 
 */
public class RadianceLookAndFeelController extends DefaultLookAndFeelController {
	/**
	 * Class name of the Radiance LAF class to use by default. This can be
	 * re-skinned at any time.
	 */
	private static final String RADIANCE_LOOK_AND_FEEL_CLASS = "org.pushingpixels.radiance.theming.api.skin.RadianceBusinessLookAndFeel";

	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(RadianceLookAndFeelController.class);

	/** Logger for this class. */
	private static final ILogger s_log = LoggerController.createLogger(RadianceLookAndFeelController.class);

	/**
	 * Placeholder LAF that identifies itself as "Radiance". No other LAF does this.
	 */
	public static final String RADIANCE_LAF_PLACEHOLDER_CLASS_NAME = new RadianceLafPlaceholder().getClass().getName();

	/** Preferences for this LAF. */
	private RadiancePreferences _prefs;

	private RadianceLafData _lafData = null;

	private ClassLoader _cl = null;

	/**
	 * Ctor specifying the Look and Feel plugin.
	 * 
	 * @param plugin The plugin that this controller is a part of.
	 */
	RadianceLookAndFeelController(LAFPlugin plugin, LAFRegister register)
	{
		super();

		_cl = register.getLookAndFeelClassLoader();
		_lafData = new RadianceLafData(_cl);

		XMLObjectCache cache = plugin.getSettingsCache();
		Iterator<?> it = cache.getAllForClass(RadiancePreferences.class);
		if (it.hasNext()) {
			_prefs = (RadiancePreferences) it.next();
		} else {
			_prefs = new RadiancePreferences();
			try {
				cache.add(_prefs);
			} catch (DuplicateObjectException ex) {
				s_log.error("RadiancePreferences object already in XMLObjectCache", ex);
			}
		}

	}

	/**
	 * This Look and Feel is about to be installed. Load the selected themepack.
	 */
	public void aboutToBeInstalled(LAFRegister lafRegister, LookAndFeel laf) {
	}

	/**
	 * This Look and Feel has just been installed.
	 */
	public void hasBeenInstalled(LAFRegister lafRegister, LookAndFeel laf) {
		final String skinName = _prefs.getSkinName();
		Class<?> skinClass = _lafData.getSkinClassForName(skinName);
		LookAndFeel skinObject;
		try {
			skinObject = (LookAndFeel) skinClass.getDeclaredConstructor().newInstance();
			SquirrelLookAndFeelHandler.setLookAndFeel(skinObject);
			UIManager.getLookAndFeelDefaults().put("ClassLoader", _cl);
		} catch (InstantiationException e) {
			// skinClass.newInstance();
			s_log.error("Unable to instantiate skinClass (" + skinName + "):" + e.getMessage(), e);
		} catch (IllegalAccessException e) {
			// skinClass.newInstance();
			s_log.error("Unable to instantiate skinClass (" + skinName + "):" + e.getMessage(), e);
		} catch (UnsupportedLookAndFeelException e) {
			// UIManager.setLookAndFeel
			s_log.error("Unable to set look and feel using skinClass(" + skinName + "):" + e.getMessage(), e);
		} catch (InvocationTargetException e) {
			s_log.error("Unable to instantiate skinClass (" + skinName + "):" + e.getMessage(), e);
		} catch (NoSuchMethodException e) {
			s_log.error("Unable to instantiate skinClass (" + skinName + "):" + e.getMessage(), e);
		}
	}

	/**
	 * @see ILookAndFeelController#getPreferencesComponent()
	 */
	public BaseLAFPreferencesPanelComponent getPreferencesComponent() {
		return new RadianceSkinPrefsPanel(this);
	}

	private static final class RadianceSkinPrefsPanel extends BaseLAFPreferencesPanelComponent {

		interface SkinPrefsPanelI18n {
			// i18n[RadianceLookAndFeelController.radianceSkinLabel=Radiance Skin:]
			String THEME_PACK = s_stringMgr.getString("RadianceLookAndFeelController.radianceSkinLabel");
		}

		private RadianceLookAndFeelController _ctrl;

		private JComboBox _skinCmb = new JComboBox();

		RadianceSkinPrefsPanel(RadianceLookAndFeelController ctrl) {
			super(new GridBagLayout());
			_ctrl = ctrl;
			createUserInterface();
		}

		private void createUserInterface() {
			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.anchor = GridBagConstraints.WEST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(4, 4, 4, 4);

			gbc.gridx = 0;
			gbc.gridy = 0;
			add(new JLabel(SkinPrefsPanelI18n.THEME_PACK, SwingConstants.RIGHT), gbc);

			++gbc.gridx;
			add(_skinCmb, gbc);
		}

		/**
		 * @see BaseLAFPreferencesPanelComponent#loadPreferencesPanel()
		 */
		public void loadPreferencesPanel() {
			super.loadPreferencesPanel();
			Set<String> radianceThemes = _ctrl._lafData.getRadianceSkins();
			Object[] comboItems = new Object[radianceThemes.size()];
			int count = 0;
			for (String theme : radianceThemes){
				comboItems[count++] = theme;
			}
			ComboBoxModel model = new DefaultComboBoxModel(comboItems);
			_skinCmb.setModel(model);
			_skinCmb.setSelectedItem(_ctrl._prefs.getSkinName());
			if(_skinCmb.getSelectedIndex() == -1 && _skinCmb.getModel().getSize() > 0){
				_skinCmb.setSelectedIndex(0);
			}
		}

		/**
		 * @see BaseLAFPreferencesPanelComponent#applyChanges()
		 */
		public boolean applyChanges() {
			super.applyChanges();
			_ctrl._prefs.setSkinName((String) _skinCmb.getSelectedItem());
			return true;
		}
	}

	public static final class RadiancePreferences implements IHasIdentifier {

		private String _skinName;

		private IntegerIdentifier _id = new IntegerIdentifier(1);

		public String getSkinName() {
			return _skinName;
		}

		public void setSkinName(String value) {
			_skinName = value;
		}

		/**
		 * @return The unique identifier for this object.
		 */
		public IIdentifier getIdentifier() {
			return _id;
		}
	}
}
