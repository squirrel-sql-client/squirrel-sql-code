package net.sourceforge.squirrel_sql.plugins.laf;
/*
 * Copyright (C) 2001 Colin Bell
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
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.MutableComboBoxModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import net.sourceforge.squirrel_sql.fw.gui.FontChooser;
import net.sourceforge.squirrel_sql.fw.gui.LookAndFeelComboBox;
import net.sourceforge.squirrel_sql.fw.util.FileExtensionFilter;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;

/**
 * The Look and Feel panel for the Global Preferences dialog.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class LAFPreferencesPanel implements IGlobalPreferencesPanel {
	
	private final static String[][] FONTS = { {"Menus", "Menu.font", "MenuBar.font"}
	};

	/** Plugin preferences object. */
	private LAFPreferences _lafPrefs;

	/** Component to display in the Global preferences dialog. */
	private MyPanel _myPanel;

	/** Application API. */
	private IApplication _app;

	/**
	 * Ctor.
	 *
	 * @param   plugin		  The LAF plugin.
	 * @param   lafRegister		Look and Feel register.
	 *
	 * @throws  IllegalArgumentException
	 *		  if <TT>LAFPlugin</TT>, or <TT>LAFRegister</TT> is <TT>null</TT>.
	 */
	public LAFPreferencesPanel(LAFPlugin plugin, LAFRegister lafRegister)
		throws IllegalArgumentException {
		super();
		if (plugin == null) {
			throw new IllegalArgumentException("Null LAFPlugin passed");
		}
		if (lafRegister == null) {
			throw new IllegalArgumentException("Null LAFRegister passed");
		}
		_lafPrefs = plugin.getLAFPreferences();
		// Create the actual panel that will be displayed in dialog.
		_myPanel = new MyPanel(plugin, lafRegister);
	}

	/**
	 * Load panel with data from plugin preferences.
	 *
	 * @param   app	 Application API.
	 *
	 * @throws  IllegalArgumentException
	 *		  if <TT>IApplication</TT> is <TT>null</TT>.
	 */
	public void initialize(IApplication app) throws IllegalArgumentException {
		if (app == null) {
			throw new IllegalArgumentException("Null IApplication passed");
		}
		_app = app;
		_myPanel.loadData();
	}

	/**
	 * Return the component to be displayed in the Preferences dialog.
	 *
	 * @return  the component to be displayed in the Preferences dialog.
	 */
	public Component getPanelComponent() {
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
		return MyPanel.i18n.TAB_TITLE;
	}

	/**
	 * Return the hint for this panel.
	 *
	 * @return  the hint for this panel.
	 */
	public String getHint() {
		return MyPanel.i18n.TAB_HINT;
	}
	/**
	 * Component to be displayed in the preferences dialog.
	 */
	private static final class MyPanel extends JPanel {
		/**
		 * This interface defines locale specific strings. This should be
		 * replaced with a property file.
		 */
		interface i18n {
			String LOOK_AND_FEEL = "Look and Feel:";
			String THEME_PACK = "Theme Pack:";
			//			String LAF_WARNING = "Note: Changes to this panel will require a\nrestart of Squirrel-SQL to take effect.";
			String TAB_TITLE = "L & F";
			String TAB_HINT = "Look and Feel settings";
			String LAF_LOC = "Look and Feel jars folder:";
			String THEMEPACK_LOC = "Theme Pack folder:";
		}
		private LookAndFeelComboBox _lafCmb = new LookAndFeelComboBox();
		private ThemePackComboBox _themePackCmb;

		private LAFPlugin _plugin;
		private LAFRegister _lafRegister;

		private LAFPreferences _lafsPrefs;
		MyPanel(LAFPlugin plugin, LAFRegister lafRegister) {
			super();
			_plugin = plugin;
			_lafRegister = lafRegister;
			_lafsPrefs = _plugin.getLAFPreferences();
			createUserInterface();
		}

		void loadData() {
			final String skinLafName = _lafRegister.getSkinnableLookAndFeelName();
			//		  _themePackCmb.setSelectedItem(_lafsPrefs.getThemePackName());
			//		  _themePackCmb.setEnabled(((String)_lafCmb.getSelectedItem()).equals(_skinLafName));
			if (_themePackCmb.getModel().getSize() == 0) {
				_themePackCmb.setEnabled(false);
				ComboBoxModel model = _lafCmb.getModel();
				if (model instanceof MutableComboBoxModel) {
					((MutableComboBoxModel) model).removeElement(skinLafName);
				}
			} else {
				_themePackCmb.setSelectedItem(_lafsPrefs.getThemePackName());
				if (_themePackCmb.getSelectedIndex() == -1) {
					_themePackCmb.setSelectedIndex(0);
				}
				_themePackCmb.setEnabled(
					((String) _lafCmb.getSelectedItem()).equals(skinLafName));
			}
		}

		void applyChanges() {
			_lafsPrefs.setLookAndFeelClassName(
				_lafCmb.getSelectedLookAndFeel().getClassName());
			_lafsPrefs.setThemePackName((String) _themePackCmb.getSelectedItem());
			try {
				//UIManager.put("Menu.font", new Font("Lucinda Console", Font.PLAIN, 10));
				_lafRegister.setLookAndFeel();
			} catch (Exception ex) {
				//?? Need to report this.
			}
		}

		private void createUserInterface() {
			//final GridBagLayout gbl = ;
			final GridBagConstraints gbc = new GridBagConstraints();
			setLayout(new GridBagLayout());

			gbc.anchor = GridBagConstraints.NORTHWEST;
			Insets compInsets = gbc.insets = new Insets(2, 2, 2, 2);
			gbc.gridx = gbc.gridy = 0;
			add(new JLabel(i18n.LOOK_AND_FEEL, SwingConstants.RIGHT), gbc);

			_lafCmb.setSelectedLookAndFeelClassName(_lafsPrefs.getLookAndFeelClassName());
			_lafCmb.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					_themePackCmb.setEnabled(
						((String) _lafCmb.getSelectedItem()).equals(
							_lafRegister.getSkinnableLookAndFeelName()));
				}
			});
			++gbc.gridx;
			add(_lafCmb, gbc);

			gbc.gridx = 0;
			++gbc.gridy;
			add(new JLabel(i18n.THEME_PACK, SwingConstants.RIGHT), gbc);

			_themePackCmb = new ThemePackComboBox(_plugin.getSkinThemePackFolder());
			_themePackCmb.setEnabled(false);
			++gbc.gridx;
			add(_themePackCmb, gbc);

			gbc.gridx = 0;
			++gbc.gridy;
			add(new JLabel(i18n.LAF_LOC, SwingConstants.RIGHT), gbc);

			++gbc.gridx;
			add(new JLabel(_plugin.getLookAndFeelFolder().getAbsolutePath()), gbc);

			gbc.gridx = 0;
			++gbc.gridy;
			add(new JLabel(i18n.THEMEPACK_LOC, SwingConstants.RIGHT), gbc);

			++gbc.gridx;
			add(new JLabel(_plugin.getSkinThemePackFolder().getAbsolutePath()), gbc);

			for (int i = 0; i < FONTS.length; ++i) {
				if (i % 2 == 0) {
					gbc.gridx = 0;
					++gbc.gridy;
				} else {
					++gbc.gridx;
				}
				String[] buttonInfo = FONTS[i];
				if (buttonInfo.length > 1) {
					JButton btn = new JButton(buttonInfo[0]);
					String[] propertyNames = new String[buttonInfo.length - 1];
					System.arraycopy(buttonInfo, 1, propertyNames, 0, propertyNames.length);
					btn.addActionListener(new FontButtonListener(propertyNames));
					add(btn, gbc);
				}
			}

			gbc.gridx = 0;
			++gbc.gridy;
			JButton defaultsBtn = new JButton("Default Fonts");
			add(defaultsBtn, gbc);
			// Warning message in bottom panel.
			//			JTextArea ta = new JTextArea(i18n.LAF_WARNING);
			//			ta.setBackground(getBackground());
			//			ta.setEditable(false);
			//			ta.setFont(lbl.getFont());
			//			add(ta, BorderLayout.SOUTH);
		}

		private void createFontControls(JPanel pnl) {
		}

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
		
		private class FontButtonListener implements ActionListener {
			private String[] _propertyNames;
			FontButtonListener(String[] propertyNames) {
				super();
				_propertyNames = propertyNames;
			}
			public void actionPerformed(ActionEvent evt) {
				FontChooser chooser = new FontChooser();
				if (chooser.showDialog()) {
					Font font = chooser.getSelectedFont();
					if (font != null) {
					}
				}
			}
		}
	}
}