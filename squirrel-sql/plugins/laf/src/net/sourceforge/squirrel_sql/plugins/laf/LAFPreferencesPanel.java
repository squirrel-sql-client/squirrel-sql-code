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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.MutableComboBoxModel;
import javax.swing.SwingConstants;

import net.sourceforge.squirrel_sql.fw.gui.DirectoryListComboBox;
import net.sourceforge.squirrel_sql.fw.gui.LookAndFeelComboBox;
import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.gui.OutputLabel;
import net.sourceforge.squirrel_sql.fw.util.FileExtensionFilter;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
/**
 * The Look and Feel panel for the Global Preferences dialog.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class LAFPreferencesPanel implements IGlobalPreferencesPanel {
	/** Logger for this class. */
	private static ILogger s_log = LoggerController.createLogger(LAFPreferencesPanel.class);

	/** The plugin. */
	private LAFPlugin _plugin;

	/** Plugin preferences object. */
	private LAFPreferences _prefs;

	/** Look and Feel register. */
	private LAFRegister _lafRegister;

	/** LAF panel to display in the Global preferences dialog. */
	private LAFPanel _myPanel;

	/** Application API. */
	private IApplication _app;

	/**
	 * Ctor.
	 *
	 * @param   plugin			The LAF plugin.
	 * @param   lafRegister		Look and Feel register.
	 *
	 * @throws	IllegalArgumentException
	 *			if <TT>LAFPlugin</TT>, or <TT>LAFRegister</TT> is <TT>null</TT>.
	 */
	public LAFPreferencesPanel(LAFPlugin plugin, LAFRegister lafRegister) {
		super();
		if (plugin == null) {
			throw new IllegalArgumentException("Null LAFPlugin passed");
		}
		if (lafRegister == null) {
			throw new IllegalArgumentException("Null LAFRegister passed");
		}
		_plugin = plugin;
		_prefs = plugin.getLAFPreferences();
		_lafRegister = lafRegister;
	}

	/**
	 * Load panel with data from plugin preferences.
	 *
	 * @param   app	 Application API.
	 *
	 * @throws  IllegalArgumentException
	 *			if <TT>IApplication</TT> is <TT>null</TT>.
	 */
	public void initialize(IApplication app) {
		if (app == null) {
			throw new IllegalArgumentException("Null IApplication passed");
		}
		_app = app;
		((LAFPanel)getPanelComponent()).loadData();
	}

	/**
	 * Return the component to be displayed in the Preferences dialog.
	 *
	 * @return  the component to be displayed in the Preferences dialog.
	 */
	public synchronized Component getPanelComponent() {
		if (_myPanel == null) {
			_myPanel = new LAFPanel(_plugin, _lafRegister);
		}
		return _myPanel;
	}

	/**
	 * User has pressed OK or Apply in the dialog so save data from
	 * panel.
	 */
	public void applyChanges() {
		_myPanel.applyChanges();
	}

	/**
	 * Return the title for this panel.
	 *
	 * @return  the title for this panel.
	 */
	public String getTitle() {
		return LAFPanel.i18n.TAB_TITLE;
	}

	/**
	 * Return the hint for this panel.
	 *
	 * @return  the hint for this panel.
	 */
	public String getHint() {
		return LAFPanel.i18n.TAB_HINT;
	}

	/**
	 * "Change L&F" panel to be displayed in the preferences dialog.
	 */
	private static final class LAFPanel extends JPanel {
		/**
		 * This interface defines locale specific strings. This should be
		 * replaced with a property file.
		 */
		interface i18n {
			String LOOK_AND_FEEL = "Look and Feel:";
			String THEME_PACK = "Skin Theme Pack:";
			String LAF_WARNING = "Note: Controls may not be drawn correctly after changes in this panel until the application is restarted.";
			String TAB_TITLE = "L & F";
			String TAB_HINT = "Look and Feel settings";
			String LAF_LOC = "L & F jars:";
			String THEMEPACK_LOC = "Skin Theme Packs:";
		}
		private LookAndFeelComboBox _lafCmb = new LookAndFeelComboBox();
		private DirectoryListComboBox _themePackCmb;

		private LAFPlugin _plugin;
		private LAFRegister _lafRegister;

		private LAFPreferences _prefs;

		LAFPanel(LAFPlugin plugin, LAFRegister lafRegister) {
			super();
			_plugin = plugin;
			_lafRegister = lafRegister;
			_prefs = _plugin.getLAFPreferences();
			createUserInterface();
		}

		void loadData() {
			final String skinLafName = _lafRegister.getSkinnableLookAndFeelName();
			if (_themePackCmb.getModel().getSize() == 0) {
				_themePackCmb.setEnabled(false);
				ComboBoxModel model = _lafCmb.getModel();
				if (model instanceof MutableComboBoxModel) {
					((MutableComboBoxModel) model).removeElement(skinLafName);
				}
			} else {
				_themePackCmb.setSelectedItem(_prefs.getSkinThemePackName());
				if (_themePackCmb.getSelectedIndex() == -1) {
					_themePackCmb.setSelectedIndex(0);
				}
				_themePackCmb.setEnabled(
					((String) _lafCmb.getSelectedItem()).equals(skinLafName));
			}
		}

		void applyChanges() {
			_prefs.setLookAndFeelClassName(_lafCmb.getSelectedLookAndFeel().getClassName());
			_prefs.setSkinThemePackName((String)_themePackCmb.getSelectedItem());

			try {
				_lafRegister.setLookAndFeel();
				_lafRegister.updateAllFrames();
			} catch (Exception ex) {
				s_log.error("Error setting Look and Feel", ex);
			}
		}

		private void createUserInterface() {
			setLayout(new GridBagLayout());
			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.anchor = gbc.WEST;
			gbc.fill = gbc.HORIZONTAL;
			gbc.insets = new Insets(4, 4, 4, 4);

			gbc.gridx = 0;
			gbc.gridy = 0;
			add(createLookAndFeelPanel(), gbc);

			++gbc.gridy;
			gbc.gridx = 0;
			gbc.gridwidth = gbc.REMAINDER;
			add(new MultipleLineLabel(i18n.LAF_WARNING), gbc);
		}

		private JPanel createLookAndFeelPanel() {
//			_themePackCmb = new ThemePackComboBox(_plugin.getSkinThemePackFolder());
			_themePackCmb = new DirectoryListComboBox();
			_themePackCmb.load(_plugin.getSkinThemePackFolder(),
								new FileExtensionFilter("JAR files", new String[] { ".jar", ".zip" }));
			_themePackCmb.setEnabled(false);

			_lafCmb.setSelectedLookAndFeelClassName(_prefs.getLookAndFeelClassName());
			_lafCmb.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					_themePackCmb.setEnabled(
						((String) _lafCmb.getSelectedItem()).equals(
							_lafRegister.getSkinnableLookAndFeelName()));
				}
			});

			JPanel pnl = new JPanel();
			pnl.setBorder(BorderFactory.createTitledBorder("Look and Feel"));
			pnl.setLayout(new GridBagLayout());
			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = gbc.HORIZONTAL;
			gbc.insets = new Insets(4, 4, 4, 4);

			gbc.gridx = 0;
			gbc.gridy = 0;
			pnl.add(new JLabel(i18n.LOOK_AND_FEEL, SwingConstants.RIGHT), gbc);

			++gbc.gridx;
			pnl.add(_lafCmb, gbc);

			gbc.gridx = 0;
			++gbc.gridy;
			pnl.add(new JLabel(i18n.THEME_PACK, SwingConstants.RIGHT), gbc);

			++gbc.gridx;
			pnl.add(_themePackCmb, gbc);

			gbc.gridx = 0;
			++gbc.gridy;
			pnl.add(new JLabel(i18n.LAF_LOC, SwingConstants.RIGHT), gbc);

			++gbc.gridx;
			pnl.add(new OutputLabel(_plugin.getLookAndFeelFolder().getAbsolutePath()), gbc);

			gbc.gridx = 0;
			++gbc.gridy;
			pnl.add(new JLabel(i18n.THEMEPACK_LOC, SwingConstants.RIGHT), gbc);

			++gbc.gridx;
			pnl.add(new OutputLabel(_plugin.getSkinThemePackFolder().getAbsolutePath()), gbc);
			
			return pnl;
		}

/*
		private class ThemePackComboBox extends JComboBox {
			ThemePackComboBox(File themePackFolder) {
				super();
				loadThemePacks(themePackFolder);
			}

			private void loadThemePacks(File themePackFolder) {
				if (themePackFolder.canRead() && themePackFolder.isDirectory()) {
					File[] files =
						themePackFolder.listFiles(
							new FileExtensionFilter("JAR files", new String[] { ".jar", ".zip" }));
					for (int i = 0; i < files.length; ++i) {
						addItem(files[i].getName());
					}
				}
			}
		}
*/
	}
}
