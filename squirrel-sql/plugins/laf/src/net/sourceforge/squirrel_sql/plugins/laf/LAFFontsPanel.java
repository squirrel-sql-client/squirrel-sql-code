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
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import net.sourceforge.squirrel_sql.fw.gui.FontChooser;
import net.sourceforge.squirrel_sql.fw.gui.FontInfo;
import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
/**
 * The Fonts panel for the Global Preferences dialog.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
class LAFFontsPanel implements IGlobalPreferencesPanel {
	/** Logger for this class. */
	private static ILogger s_log = LoggerController.createLogger(LAFFontsPanel.class);

	/** The plugin. */
	private LAFPlugin _plugin;

	/** Plugin preferences object. */
	private LAFPreferences _prefs;

	/** Look and Feel register. */
	private LAFRegister _lafRegister;

	/** Fonts panel to display in the Global preferences dialog. */
	private FontSelectionPanel _myPanel;

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
	public LAFFontsPanel(LAFPlugin plugin, LAFRegister lafRegister) {
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
		((FontSelectionPanel)getPanelComponent()).loadData();
	}

	/**
	 * Return the component to be displayed in the Preferences dialog.
	 *
	 * @return  the component to be displayed in the Preferences dialog.
	 */
	public synchronized Component getPanelComponent() {
		if (_myPanel == null) {
			_myPanel = new FontSelectionPanel(_plugin, _lafRegister);
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
		return FontSelectionPanel.i18n.TAB_TITLE;
	}

	/**
	 * Return the hint for this panel.
	 *
	 * @return  the hint for this panel.
	 */
	public String getHint() {
		return FontSelectionPanel.i18n.TAB_HINT;
	}

	/**
	 * "Fonts" panel to be displayed in the preferences dialog.
	 */
	private static final class FontSelectionPanel extends JPanel {
		/**
		 * This interface defines locale specific strings. This should be
		 * replaced with a property file.
		 */
		interface i18n {
			String LAF_WARNING = "Note: Controls may not be drawn correctly after changes in this panel until the application is restarted.";
			String TAB_TITLE = "Fonts";
			String TAB_HINT = "Fonts";
		}

		/** Button to select font for menus. */
		private FontButton _menuFontBtn;

		/** Button to select font for static text. */
		private FontButton _staticFontBtn;

		/** Button to select font for toolbars. */
		private FontButton _toolBarFontBtn;

		/** Button to select font for other controls. */
		private FontButton _otherFontBtn;

		private JLabel _menuFontLbl = new JLabel();
		private JLabel _staticFontLbl = new JLabel();
		private JLabel _toolBarFontLbl = new JLabel();
		private JLabel _otherFontLbl = new JLabel();

		private JCheckBox _menuFontEnabledChk = new JCheckBox("Enabled");
		private JCheckBox _staticFontEnabledChk = new JCheckBox("Enabled");
		private JCheckBox _toolBarFontEnabledChk = new JCheckBox("Enabled");
		private JCheckBox _otherFontEnabledChk = new JCheckBox("Enabled");

		private LAFPlugin _plugin;
		private LAFRegister _lafRegister;

		private LAFPreferences _prefs;

		FontSelectionPanel(LAFPlugin plugin, LAFRegister lafRegister) {
			super();
			_plugin = plugin;
			_lafRegister = lafRegister;
			_prefs = _plugin.getLAFPreferences();
			createUserInterface();
		}

		void loadData() {
			_menuFontEnabledChk.setSelected(_prefs.isMenuFontEnabled());
			_staticFontEnabledChk.setSelected(_prefs.isStaticFontEnabled());
			_toolBarFontEnabledChk.setSelected(_prefs.isToolBarFontEnabled());
			_otherFontEnabledChk.setSelected(_prefs.isOtherFontEnabled());

			FontInfo fi = _prefs.getMenuFontInfo();
			_menuFontLbl.setText(fi != null ? fi.toString() : "");
			fi = _prefs.getStaticFontInfo();
			_staticFontLbl.setText(fi != null ? fi.toString() : "");
			fi = _prefs.getToolBarFontInfo();
			_toolBarFontLbl.setText(fi != null ? fi.toString() : "");
			fi = _prefs.getOtherFontInfo();
			_otherFontLbl.setText(fi != null ? fi.toString() : "");

			_menuFontBtn.setEnabled(_prefs.isMenuFontEnabled());
			_staticFontBtn.setEnabled(_prefs.isStaticFontEnabled());
			_toolBarFontBtn.setEnabled(_prefs.isToolBarFontEnabled());
			_otherFontBtn.setEnabled(_prefs.isOtherFontEnabled());
		}

		void applyChanges() {
			_prefs.setMenuFontInfo(_menuFontBtn.getFontInfo());
			_prefs.setStaticFontInfo(_staticFontBtn.getFontInfo());
			_prefs.setToolBarFontInfo(_toolBarFontBtn.getFontInfo());
			_prefs.setOtherFontInfo(_otherFontBtn.getFontInfo());

			_prefs.setMenuFontEnabled(_menuFontEnabledChk.isSelected());
			_prefs.setStaticFontEnabled(_staticFontEnabledChk.isSelected());
			_prefs.setToolBarFontEnabled(_toolBarFontEnabledChk.isSelected());
			_prefs.setOtherFontEnabled(_otherFontEnabledChk.isSelected());

			try {
				_lafRegister.updateApplicationFonts();
			} catch (Exception ex) {
				s_log.error("Error updating fonts", ex);
			}
		}

		private void createUserInterface() {
			setLayout(new GridBagLayout());
			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.anchor = gbc.WEST;
			gbc.fill = gbc.HORIZONTAL;
			gbc.insets = new Insets(4, 4, 4, 4);

			add(createFontsPanel(), gbc);

			++gbc.gridy;
			gbc.gridx = 0;
			gbc.gridwidth = gbc.REMAINDER;
			add(new MultipleLineLabel(i18n.LAF_WARNING), gbc);
		}

		private JPanel createFontsPanel() {
			_menuFontBtn = new FontButton("Menus", _menuFontLbl, _prefs.getMenuFontInfo());
			_staticFontBtn = new FontButton("Static Text", _staticFontLbl, _prefs.getStaticFontInfo());
			_toolBarFontBtn = new FontButton("Toolbars", _toolBarFontLbl, _prefs.getToolBarFontInfo());
			_otherFontBtn = new FontButton("Other", _otherFontLbl, _prefs.getOtherFontInfo());

			FontButtonListener lis = new FontButtonListener();
			_menuFontBtn.addActionListener(lis);
			_staticFontBtn.addActionListener(lis);
			_toolBarFontBtn.addActionListener(lis);
			_otherFontBtn.addActionListener(lis);

			_menuFontEnabledChk.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					_menuFontBtn.setEnabled(_menuFontEnabledChk.isSelected());
				}
			});
			_staticFontEnabledChk.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					_staticFontBtn.setEnabled(_staticFontEnabledChk.isSelected());
				}
			});
			_toolBarFontEnabledChk.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					_toolBarFontBtn.setEnabled(_toolBarFontEnabledChk.isSelected());
				}
			});
			_otherFontEnabledChk.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					_otherFontBtn.setEnabled(_otherFontEnabledChk.isSelected());
				}
			});

			JPanel pnl = new JPanel();
			pnl.setBorder(BorderFactory.createTitledBorder("Fonts"));
			pnl.setLayout(new GridBagLayout());
			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = gbc.HORIZONTAL;
			gbc.insets = new Insets(4, 4, 4, 4);

			gbc.gridx = 0;
			gbc.gridy = 0;
			pnl.add(_menuFontEnabledChk, gbc);

			++gbc.gridy;
			pnl.add(_staticFontEnabledChk, gbc);

			++gbc.gridy;
			pnl.add(_toolBarFontEnabledChk, gbc);

			++gbc.gridy;
			pnl.add(_otherFontEnabledChk, gbc);

			++gbc.gridx;
			gbc.gridy = 0;
			pnl.add(_menuFontBtn, gbc);

			++gbc.gridy;
			pnl.add(_staticFontBtn, gbc);

			++gbc.gridy;
			pnl.add(_toolBarFontBtn, gbc);

			++gbc.gridy;
			pnl.add(_otherFontBtn, gbc);

			++gbc.gridx;
			gbc.gridy = 0;
			gbc.fill = gbc.HORIZONTAL;
			gbc.weightx = 1.0;
			pnl.add(_menuFontLbl, gbc);

			++gbc.gridy;
			pnl.add(_staticFontLbl, gbc);

			++gbc.gridy;
			pnl.add(_toolBarFontLbl, gbc);

			++gbc.gridy;
			pnl.add(_otherFontLbl, gbc);

			return pnl;
		}

		private static final class FontButton extends JButton {
			private FontInfo _fi;
			private JLabel _lbl;
			private Font _font;
			private boolean _dirty;

			FontButton(String text, JLabel lbl, FontInfo fi) {
				super(text);
				_lbl = lbl;
				_fi = fi;
			}
			
			FontInfo getFontInfo() {
				return _fi;
			}
			
			Font getSelectedFont() {
				return _font;
			}
			
			void setSelectedFont(Font font) {
				_font = font;
				if (_fi == null) {
					_fi = new FontInfo(font);
				} else {
					_fi.setFont(font);
				}
				_dirty = true;
			}
			
			boolean isDirty() {
				return _dirty;
			}
		}

		private static final class FontButtonListener implements ActionListener {
			public void actionPerformed(ActionEvent evt) {
				if (evt.getSource() instanceof FontButton) {
					FontButton btn = (FontButton)evt.getSource();
					FontInfo fi = btn.getFontInfo();
					Font font = null;
					if (fi != null) {
						font = fi.createFont();
					}
					font = new FontChooser().showDialog(font);
					if (font != null) {
						btn.setSelectedFont(font);
						btn._lbl.setText(new FontInfo(font).toString());
					}
				}
			}
		}

		private static final class RightLabel extends JLabel {
			RightLabel(String title) {
				super(title, SwingConstants.RIGHT);
			}
		}

		private static final class OutputLabel extends JLabel {
			OutputLabel(String title) {
				super(title);
				setToolTipText(title);
				Dimension ps = getPreferredSize();
				ps.width = 150;
				setPreferredSize(ps);
			}
		}

	}

}

