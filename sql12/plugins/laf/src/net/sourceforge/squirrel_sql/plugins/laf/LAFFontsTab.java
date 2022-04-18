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
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.fw.gui.FontChooser;
import net.sourceforge.squirrel_sql.fw.gui.FontInfo;
import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.gui.OutputLabel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
/**
 * The Fonts panel for the Global Preferences dialog.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
class LAFFontsTab implements IGlobalPreferencesPanel
{
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(LAFFontsTab.class);


	/** Logger for this class. */
	private static final ILogger s_log =
		LoggerController.createLogger(LAFFontsTab.class);

	/** The plugin. */
	private LAFPlugin _plugin;

	/** Plugin preferences object. */
	//private LAFPreferences _prefs;

	/** Look and Feel register. */
	private LAFRegister _lafRegister;

	/** Fonts panel to display in the Global preferences dialog. */
	private FontSelectionPanel _myPanel;

	/** Application API. */
	//private IApplication _app;

	/**
	 * Ctor.
	 *
	 * @param	plugin			The LAF plugin.
	 * @param	lafRegister		Look and Feel register.
	 *
	 * @throws	IllegalArgumentException
	 *			if <TT>LAFPlugin</TT>, or <TT>LAFRegister</TT> is <TT>null</TT>.
	 */
	public LAFFontsTab(LAFPlugin plugin, LAFRegister lafRegister)
	{
		super();
		if (plugin == null)
		{
			throw new IllegalArgumentException("Null LAFPlugin passed");
		}
		if (lafRegister == null)
		{
			throw new IllegalArgumentException("Null LAFRegister passed");
		}
		_plugin = plugin;
		//_prefs = plugin.getLAFPreferences();
		_lafRegister = lafRegister;
	}

	/**
	 * Load panel with data from plugin preferences.
	 *
	 * @param	app		Application API.
	 *
	 * @throws	IllegalArgumentException
	 *			if <TT>IApplication</TT> is <TT>null</TT>.
	 */
	public void initialize(IApplication app)
	{
		if (app == null)
		{
			throw new IllegalArgumentException("Null IApplication passed");
		}
		//_app = app;
		((FontSelectionPanel)getPanelComponent()).loadData();
	}

   public void uninitialize(IApplication app)
   {
   }

   /**
	 * Return the component to be displayed in the Preferences dialog.
	 *
	 * @return  the component to be displayed in the Preferences dialog.
	 */
	public Component getPanelComponent()
	{
		if (_myPanel == null)
		{
			_myPanel = new FontSelectionPanel(_plugin, _lafRegister);
		}
		return _myPanel;
	}

	/**
	 * User has pressed OK or Apply in the dialog so save data from
	 * panel.
	 */
	public void applyChanges()
	{
		_myPanel.applyChanges();
	}

	/**
	 * Return the title for this panel.
	 *
	 * @return  the title for this panel.
	 */
	public String getTitle()
	{
		return FontSelectionPanel.i18n.TAB_TITLE;
	}

	/**
	 * Return the hint for this panel.
	 *
	 * @return  the hint for this panel.
	 */
	public String getHint()
	{
		return FontSelectionPanel.i18n.TAB_HINT;
	}

	/**
	 * "Fonts" panel to be displayed in the preferences dialog.
	 */
	private static final class FontSelectionPanel extends JPanel
	{
		/**
		 * This interface defines locale specific strings. This should be
		 * replaced with a property file.
		 */
		interface i18n
		{
			// i18n[laf.warning=Note: Changes may not take effect until the application is restarted.]
			String LAF_WARNING =	s_stringMgr.getString("laf.warning");
			// i18n[laf.tabTitle=Fonts]
			String TAB_TITLE = s_stringMgr.getString("laf.tabTitle");
			// i18n[laf.tabHint=Fonts]
			String TAB_HINT = s_stringMgr.getString("laf.tabHint");
		}

		/** Button to select font for menus. */
		private FontButton _menuFontBtn;

		/** Button to select font for static text. */
		private FontButton _staticFontBtn;

		/** Button to select font for status bars. */
		private FontButton _statusBarFontBtn;

		/** Button to select font for other controls. */
		private FontButton _otherFontBtn;

		private JLabel _menuFontLbl = new OutputLabel(" ");
		private JLabel _staticFontLbl = new OutputLabel(" ");
		private JLabel _statusBarFontLbl = new OutputLabel(" ");
		private JLabel _otherFontLbl = new OutputLabel(" ");

		// i18n[laf.menuFontEanbled=Enabled]
		private JCheckBox _menuFontEnabledChk = new JCheckBox(s_stringMgr.getString("laf.menuFontEanbled"));
		// i18n[laf.staticFontEanbled=Enabled]
		private JCheckBox _staticFontEnabledChk = new JCheckBox(s_stringMgr.getString("laf.staticFontEanbled"));
		// i18n[laf.statusFontEanbled=Enabled]
		private JCheckBox _statusBarFontEnabledChk = new JCheckBox(s_stringMgr.getString("laf.statusFontEanbled"));
		// i18n[laf.otherFontEanbled=Enabled]
		private JCheckBox _otherFontEnabledChk = new JCheckBox(s_stringMgr.getString("laf.otherFontEanbled"));

		private LAFPlugin _plugin;
		private LAFRegister _lafRegister;

		private LAFPreferences _prefs;

		FontSelectionPanel(LAFPlugin plugin, LAFRegister lafRegister)
		{
			super();
			_plugin = plugin;
			_lafRegister = lafRegister;
			_prefs = _plugin.getLAFPreferences();
			createUserInterface();
		}

		void loadData()
		{
			_menuFontEnabledChk.setSelected(_prefs.isMenuFontEnabled());
			_staticFontEnabledChk.setSelected(_prefs.isStaticFontEnabled());
			_statusBarFontEnabledChk.setSelected(
				_prefs.isStatusBarFontEnabled());
			_otherFontEnabledChk.setSelected(_prefs.isOtherFontEnabled());

			FontInfo fi = _prefs.getMenuFontInfo();
			_menuFontLbl.setText(fi != null ? fi.toString() : "");
			fi = _prefs.getStaticFontInfo();
			_staticFontLbl.setText(fi != null ? fi.toString() : "");
			fi = _prefs.getStatusBarFontInfo();
			_statusBarFontLbl.setText(fi != null ? fi.toString() : "");
			fi = _prefs.getOtherFontInfo();
			_otherFontLbl.setText(fi != null ? fi.toString() : "");

			_menuFontBtn.setEnabled(_prefs.isMenuFontEnabled());
			_staticFontBtn.setEnabled(_prefs.isStaticFontEnabled());
			_statusBarFontBtn.setEnabled(_prefs.isStatusBarFontEnabled());
			_otherFontBtn.setEnabled(_prefs.isOtherFontEnabled());
		}

		void applyChanges()
		{
			_prefs.setMenuFontInfo(_menuFontBtn.getFontInfo());
			_prefs.setStaticFontInfo(_staticFontBtn.getFontInfo());
			_prefs.setStatusBarFontInfo(_statusBarFontBtn.getFontInfo());
			_prefs.setOtherFontInfo(_otherFontBtn.getFontInfo());

			_prefs.setMenuFontEnabled(_menuFontEnabledChk.isSelected());
			_prefs.setStaticFontEnabled(_staticFontEnabledChk.isSelected());
			_prefs.setStatusBarFontEnabled(
				_statusBarFontEnabledChk.isSelected());
			_prefs.setOtherFontEnabled(_otherFontEnabledChk.isSelected());

			try
			{
				_lafRegister.updateApplicationFonts();
				//_lafRegister.updateAllFrames();

			}
			catch (Exception ex)
			{
				s_log.error("Error updating fonts", ex);
			}
		}

		private void createUserInterface()
		{
			setLayout(new GridBagLayout());
			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.anchor = GridBagConstraints.WEST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(4, 4, 4, 4);
			gbc.gridx = 0;
			gbc.gridy = 0;
			add(createFontsPanel(), gbc);

			++gbc.gridy;
			gbc.gridwidth = GridBagConstraints.REMAINDER;
			add(new MultipleLineLabel(i18n.LAF_WARNING), gbc);
		}

		private JPanel createFontsPanel()
		{
			_menuFontBtn =
				// i18n[laf.menus=Menus]
				new FontButton(s_stringMgr.getString("laf.menus"), _menuFontLbl, _prefs.getMenuFontInfo());
			_staticFontBtn =
				// i18n[laf.staticText=Static Text]
				new FontButton(s_stringMgr.getString("laf.staticText"), _staticFontLbl, _prefs.getStaticFontInfo());
			_statusBarFontBtn =
				// i18n[laf.statusBars=Status Bars]
				new FontButton(s_stringMgr.getString("laf.statusBars"), _statusBarFontLbl, _prefs.getStatusBarFontInfo());
			_otherFontBtn =
				// i18n[laf.other=Other]
				new FontButton(s_stringMgr.getString("laf.other"), _otherFontLbl, _prefs.getOtherFontInfo());

			FontButtonListener lis = new FontButtonListener();
			_menuFontBtn.addActionListener(lis);
			_staticFontBtn.addActionListener(lis);
			_statusBarFontBtn.addActionListener(lis);
			_otherFontBtn.addActionListener(lis);

			_menuFontEnabledChk.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					_menuFontBtn.setEnabled(_menuFontEnabledChk.isSelected());
				}
			});
			_staticFontEnabledChk.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					_staticFontBtn.setEnabled(
						_staticFontEnabledChk.isSelected());
				}
			});
			_statusBarFontEnabledChk.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					_statusBarFontBtn.setEnabled(
						_statusBarFontEnabledChk.isSelected());
				}
			});
			_otherFontEnabledChk.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					_otherFontBtn.setEnabled(_otherFontEnabledChk.isSelected());
				}
			});

			JPanel pnl = new JPanel();
			// i18n[laf.tabFonts=Fonts]
			pnl.setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("laf.tabFonts")));
			pnl.setLayout(new GridBagLayout());
			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(4, 4, 4, 4);

			gbc.gridx = 0;
			gbc.gridy = 0;
			pnl.add(_menuFontEnabledChk, gbc);

			++gbc.gridy;
			pnl.add(_staticFontEnabledChk, gbc);

			++gbc.gridy;
			pnl.add(_statusBarFontEnabledChk, gbc);

			++gbc.gridy;
			pnl.add(_otherFontEnabledChk, gbc);

			++gbc.gridx;
			gbc.gridy = 0;
			pnl.add(_menuFontBtn, gbc);

			++gbc.gridy;
			pnl.add(_staticFontBtn, gbc);

			++gbc.gridy;
			pnl.add(_statusBarFontBtn, gbc);

			++gbc.gridy;
			pnl.add(_otherFontBtn, gbc);

			++gbc.gridx;
			gbc.gridy = 0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1.0;
			pnl.add(_menuFontLbl, gbc);

			++gbc.gridy;
			pnl.add(_staticFontLbl, gbc);

			++gbc.gridy;
			pnl.add(_statusBarFontLbl, gbc);

			++gbc.gridy;
			pnl.add(_otherFontLbl, gbc);

			return pnl;
		}

		private static final class FontButton extends JButton
		{
			private FontInfo _fi;
			private JLabel _lbl;
			private Font _font;
			private boolean _dirty;

			FontButton(String text, JLabel lbl, FontInfo fi)
			{
				super(text);
				_lbl = lbl;
				_fi = fi;
			}

			FontInfo getFontInfo()
			{
				return _fi;
			}

			Font getSelectedFont()
			{
				return _font;
			}

			void setSelectedFont(Font font)
			{
				_font = font;
				if (_fi == null)
				{
					_fi = new FontInfo(font);
				}
				else
				{
					_fi.setFont(font);
				}
				_dirty = true;
			}

			boolean isDirty()
			{
				return _dirty;
			}
		}

		private static final class FontButtonListener implements ActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				if (evt.getSource() instanceof FontButton)
				{
					FontButton btn = (FontButton) evt.getSource();
					FontInfo fi = btn.getFontInfo();
					Font font = null;
					if (fi != null)
					{
						font = fi.createFont();
					}
					font = new FontChooser().showDialog(font);
					if (font != null)
					{
						btn.setSelectedFont(font);
						btn._lbl.setText(new FontInfo(font).toString());
					}
				}
			}
		}
	}
}
