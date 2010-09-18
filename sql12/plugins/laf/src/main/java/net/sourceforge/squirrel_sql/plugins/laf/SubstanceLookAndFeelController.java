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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Set;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.LookAndFeel;
import javax.swing.SwingConstants;

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
 * Behaviour for the Skin Look and Feel.
 * 
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SubstanceLookAndFeelController extends DefaultLookAndFeelController
{
	/** Class name of the Substance LAF class to use by default. This can be re-skinned at any time. */
	private static final String SUBSTANCE_LOOK_AND_FEEL_CLASS = 
		"org.jvnet.substance.skin.SubstanceAutumnLookAndFeel";
	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(SubstanceLookAndFeelController.class);

	/** Logger for this class. */
	private static final ILogger s_log = LoggerController.createLogger(SubstanceLookAndFeelController.class);

	/** Placeholder LAF that identifies itself as "Substance".  No other LAF does this. */
	public static final String SUBSTANCE_LAF_DEFAULT_CLASS_NAME =
		"net.sourceforge.squirrel_sql.plugins.laf.SubstanceLafPlaceholder";

	/** Preferences for this LAF. */
	private SubstancePreferences _prefs;

	private SubstanceLafData _lafData = null;

	private ClassLoader _cl = null;
	
	/**
	 * Ctor specifying the Look and Feel plugin.
	 * 
	 * @param plugin
	 *           The plugin that this controller is a part of.
	 */
	SubstanceLookAndFeelController(LAFPlugin plugin, LAFRegister register) throws IOException
	{
		super();

		_cl = register.getLookAndFeelClassLoader();
		_lafData = new SubstanceLafData(_cl);
		
		XMLObjectCache cache = plugin.getSettingsCache();
		Iterator<?> it = cache.getAllForClass(SubstancePreferences.class);
		if (it.hasNext())
		{
			_prefs = (SubstancePreferences) it.next();
		}
		else
		{
			_prefs = new SubstancePreferences();
			try
			{
				cache.add(_prefs);
			}
			catch (DuplicateObjectException ex)
			{
				s_log.error("SubstancePreferences object already in XMLObjectCache", ex);
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
		Object skinObject;
		try
		{
			skinObject = skinClass.newInstance();
			Class<?> substancelafclass = Class.forName(SUBSTANCE_LOOK_AND_FEEL_CLASS, true, _cl);
			Method[] methods = substancelafclass.getMethods();
			Method setSkinStaticMethod = null;
			for (Method method : methods) {
				if (method.getName().equals("setSkin")) {
					Class<?>[] paramTypes = method.getParameterTypes();
					String firstParamName = paramTypes[0].getCanonicalName(); 
					if (firstParamName.equals("org.jvnet.substance.api.SubstanceSkin")) {
						setSkinStaticMethod = method;
						break;
					}
				}
			}
			setSkinStaticMethod.invoke(null, skinObject);
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
		catch (InvocationTargetException e)
		{
			// setSkinStaticMethod.invoke(null, skinObject);
			s_log.error("Unable to invoke SubstanceLookAndFeel.setSkin for skin ("+skinName+"):"+e.getMessage(), e);
		
		}
		catch (ClassNotFoundException e)
		{
			// Class.forName(SUBSTANCE_LOOK_AND_FEEL_CLASS, true, _cl);
			s_log.error("Unable to find class ("+SUBSTANCE_LOOK_AND_FEEL_CLASS+"):"+e.getMessage(), e);
		}
	}

	/**
	 * @see ILookAndFeelController#getPreferencesComponent()
	 */
	public BaseLAFPreferencesPanelComponent getPreferencesComponent()
	{
		return new SubstanceSkinPrefsPanel(this);
	}

	private static final class SubstanceSkinPrefsPanel extends BaseLAFPreferencesPanelComponent
	{
		private static final long serialVersionUID = 1L;

		interface SkinPrefsPanelI18n
		{
			// i18n[SubstanceLookAndFeelController.substanceSkinLabel=Substance Skin:]
			String THEME_PACK = s_stringMgr.getString("SubstanceLookAndFeelController.substanceSkinLabel");
		}

		private SubstanceLookAndFeelController _ctrl;

		private JComboBox _skinCmb = new JComboBox();

		SubstanceSkinPrefsPanel(SubstanceLookAndFeelController ctrl)
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
			add(new JLabel(SkinPrefsPanelI18n.THEME_PACK, SwingConstants.RIGHT), gbc);

			++gbc.gridx;
			add(_skinCmb, gbc);
		}

		/**
		 * @see BaseLAFPreferencesPanelComponent#loadPreferencesPanel()
		 */
		public void loadPreferencesPanel()
		{
			super.loadPreferencesPanel();
			Set<String> substanceThemes = _ctrl._lafData.getSubstanceSkins();
			Object[] comboItems = new Object[substanceThemes.size()];
			int count = 0;
			for (String theme : substanceThemes)
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

	public static final class SubstancePreferences implements IHasIdentifier
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
