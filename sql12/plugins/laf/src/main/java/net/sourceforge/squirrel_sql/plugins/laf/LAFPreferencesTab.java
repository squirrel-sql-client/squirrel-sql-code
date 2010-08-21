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
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.gui.OutputLabel;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringManager;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
/**
 * The Look and Feel panel for the Global Preferences dialog.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class LAFPreferencesTab implements IGlobalPreferencesPanel
{

	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(LAFPreferencesTab.class);

	/** Logger for this class. */
	private static ILogger s_log = LoggerController.createLogger(LAFPreferencesTab.class);

	/** The plugin. */
	private LAFPlugin _plugin;

	/** Plugin preferences object. */
	//private LAFPreferences _prefs;

	/** Look and Feel register. */
	private LAFRegister _lafRegister;

	/** LAF panel to display in the Global preferences dialog. */
	private LAFPreferencesPanel _myPanel;

	/**
	 * Ctor.
	 *
	 * @param	plugin			The LAF plugin.
	 * @param	lafRegister		Look and Feel register.
	 *
	 * @throws	IllegalArgumentException
	 *			if <TT>LAFPlugin</TT>, or <TT>LAFRegister</TT> is <TT>null</TT>.
	 */
	public LAFPreferencesTab(LAFPlugin plugin, LAFRegister lafRegister)
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
	 * @param	app	Application API.
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
		((LAFPreferencesPanel)getPanelComponent()).loadData();
	}

   public void uninitialize(IApplication app)
   {
      
   }

   /**
	 * Return the component to be displayed in the Preferences dialog.
	 *
	 * @return  the component to be displayed in the Preferences dialog.
	 */
	public synchronized Component getPanelComponent()
	{
		if (_myPanel == null)
		{
			_myPanel = new LAFPreferencesPanel(_plugin, _lafRegister);
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
		return LAFPreferencesPanel.LAFPreferencesPanelI18n.TAB_TITLE;
	}

	/**
	 * Return the hint for this panel.
	 *
	 * @return  the hint for this panel.
	 */
	public String getHint()
	{
		return LAFPreferencesPanel.LAFPreferencesPanelI18n.TAB_HINT;
	}

	/**
	 * "Change L&F" panel to be displayed in the preferences dialog.
	 */
	private static final class LAFPreferencesPanel extends JPanel
	{
		/**
		 * This interface defines locale specific strings. This should be
		 * replaced with a property file.
		 */
		interface LAFPreferencesPanelI18n
		{
			// i18n[laf.lookAndFeel=Look and Feel:]
			String LOOK_AND_FEEL = s_stringMgr.getString("laf.lookAndFeel");
			// i18n[laf.lafWarning=Note: Controls may not be drawn correctly after changes in this panel until the application is restarted.]
			String LAF_WARNING = s_stringMgr.getString("laf.lafWarning");
			// i18n[laf.lf=L & F]
			String TAB_TITLE = s_stringMgr.getString("laf.lf");
			// i18n[laf.settings=Look and Feel settings]
			String TAB_HINT = s_stringMgr.getString("laf.settings");
			// i18n[laf.jars=L & F jars:]
			String LAF_LOC = s_stringMgr.getString("laf.jars");
         // i18n[laf.lafPerformanceWarning=Also note: Some Look and Feels may cause performance problems.
         // If you think your selected Look and Feel slows down SQuirreL switch to a Metal or Plastic Look and Feel.]
         String LAF_CRITICAL_WARNING = s_stringMgr.getString("laf.lafCriticalWarning");
      }

		private LookAndFeelComboBox _lafCmb = new LookAndFeelComboBox();
		private JCheckBox _allowSetBorder = new JCheckBox(s_stringMgr.getString("laf.allowsetborder"));


		private LAFPlugin _plugin;
		private LAFRegister _lafRegister;

		private LAFPreferences _prefs;

		/** Listener on the Look and Feel combo box. */
		private LookAndFeelComboListener _lafComboListener;

		/**
		 * Component for extra config for the current Look and Feel. This
		 * will be <TT>null</TT> if the Look and Feel doesn't require extra
		 * configuration information.
		 */
		private BaseLAFPreferencesPanelComponent _curLAFConfigComp;

		private JPanel _lafPnl;
	
		LAFPreferencesPanel(LAFPlugin plugin, LAFRegister lafRegister)
		{
			super(new GridBagLayout());
			_plugin = plugin;
			_lafRegister = lafRegister;
			_prefs = _plugin.getLAFPreferences();
			createUserInterface();
		}

		public void addNotify()
		{
			super.addNotify();
			_lafComboListener = new LookAndFeelComboListener();
			_lafCmb.addActionListener(_lafComboListener);
		}

		public void removeNotify()
		{
			if (_lafComboListener != null)
			{
				_lafCmb.removeActionListener(_lafComboListener);
				_lafComboListener = null;
			}
			super.removeNotify();
		}

		void loadData()
		{
			final String selLafClassName = _prefs.getLookAndFeelClassName();
			_allowSetBorder.setSelected(_prefs.getCanLAFSetBorder());
			_lafCmb.setSelectedLookAndFeelClassName(selLafClassName);

			updateLookAndFeelConfigControl();
		}

		void applyChanges()
		{
			_prefs.setCanLAFSetBorder(_allowSetBorder.isSelected());
			_prefs.setLookAndFeelClassName(_lafCmb.getSelectedLookAndFeel().getClassName());
			
			_lafRegister.applyPreferences();

			boolean forceChange = false;
			if (_curLAFConfigComp != null)
			{
				forceChange = _curLAFConfigComp.applyChanges();
			}

			try
			{
				_lafRegister.setLookAndFeel(forceChange);
			}
			catch (Exception ex)
			{
				s_log.error("Error setting Look and Feel", ex);
			}
		}

		private void createUserInterface()
		{
			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.anchor = GridBagConstraints.WEST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(4, 4, 4, 4);

			gbc.gridy = 0;
			gbc.gridx = 0;
			add(createSettingsPanel(), gbc);

			++gbc.gridy;
			add(createLookAndFeelPanel(), gbc);

			++gbc.gridy;
			gbc.gridx = 0;
			gbc.gridwidth = GridBagConstraints.REMAINDER;
			add(new MultipleLineLabel(LAFPreferencesPanelI18n.LAF_WARNING), gbc);

         ++gbc.gridy;
         gbc.gridx = 0;
         gbc.gridwidth = GridBagConstraints.REMAINDER;
         MultipleLineLabel enforedWarningLabel = new MultipleLineLabel(LAFPreferencesPanelI18n.LAF_CRITICAL_WARNING);
         enforedWarningLabel.setForeground(Color.red);
         add(enforedWarningLabel, gbc);
		}

		private JPanel createLookAndFeelPanel()
		{
			_lafPnl = new JPanel(new GridBagLayout());
			// i18n[laf.broderLaf=Look and Feel]
			_lafPnl.setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("laf.broderLaf")));

			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.weightx = 1;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(4, 4, 4, 4);
			gbc.anchor = GridBagConstraints.WEST;

			gbc.gridx = 0;
			gbc.gridy = 0;
			_lafPnl.add(new JLabel(LAFPreferencesPanelI18n.LOOK_AND_FEEL, SwingConstants.RIGHT), gbc);

			++gbc.gridx;
			_lafPnl.add(_lafCmb, gbc);

			gbc.gridx = 0;
			++gbc.gridy;
			_lafPnl.add(new JLabel(LAFPreferencesPanelI18n.LAF_LOC, SwingConstants.RIGHT), gbc);

			++gbc.gridx;
			_lafPnl.add(new OutputLabel(_plugin.getLookAndFeelFolder().getAbsolutePath()), gbc);

			return _lafPnl;
		}

		private JPanel createSettingsPanel()
		{
			JPanel pnl  = new JPanel(new GridBagLayout());
			pnl.setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("laf.general")));

			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.weightx = 1;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(4, 4, 4, 4);
			gbc.anchor = GridBagConstraints.WEST;

			gbc.gridx = 0;
			gbc.gridy = 0;
			pnl.add(_allowSetBorder, gbc);


			return pnl;
		}

		private void updateLookAndFeelConfigControl()
		{
			if (_curLAFConfigComp != null)
			{
				_lafPnl.remove(_curLAFConfigComp);
				_curLAFConfigComp = null;
			}

			UIManager.LookAndFeelInfo lafInfo = _lafCmb.getSelectedLookAndFeel();
			if (lafInfo != null)
			{
				final String selLafClassName = lafInfo.getClassName();
				if (selLafClassName != null)
				{
					ILookAndFeelController ctrl = _lafRegister.getLookAndFeelController(selLafClassName);
					if (ctrl != null)
					{
						_curLAFConfigComp = ctrl.getPreferencesComponent();
						if (_curLAFConfigComp != null)
						{
							_curLAFConfigComp.loadPreferencesPanel();
							final GridBagConstraints gbc = new GridBagConstraints();
							gbc.fill = GridBagConstraints.HORIZONTAL;
							gbc.insets = new Insets(4, 4, 4, 4);
							gbc.gridx = 0;
							gbc.gridy = GridBagConstraints.RELATIVE;
							gbc.gridwidth = GridBagConstraints.REMAINDER;
							_lafPnl.add(_curLAFConfigComp, gbc);
						}
					}
					else
					{
						s_log.debug("No ILookAndFeelController found for: " +
											selLafClassName);
					}
				}
			}
			else
			{
				s_log.debug("Selected Look and Feel class is null");
			}
			validate();
		}

		private class LookAndFeelComboListener implements ActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				LAFPreferencesPanel.this.updateLookAndFeelConfigControl();
			}
		}

	}
}
