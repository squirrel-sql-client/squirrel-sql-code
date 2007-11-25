package net.sourceforge.squirrel_sql.client.preferences;
/*
 * Copyright (C) 2007 Rob Manning
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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.preferences.UpdateChannelComboBoxEntry.ChannelType;
import net.sourceforge.squirrel_sql.client.preferences.UpdateCheckFrequencyComboBoxEntry.Frequency;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.UpdateSettings;

class UpdatePreferencesPanel implements IGlobalPreferencesPanel
{
	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(UpdatePreferencesPanel.class);

	/** Panel to be displayed in preferences dialog. */
	private MyPanel _myPanel;

	/** Application API. */
	private IApplication _app;
	
	private static interface i18n {

	   //i18n[UpdatePreferencesPanel.atStartupLabel=At Startup]
	   String AT_STARTUP_LABEL = 
	      s_stringMgr.getString("UpdatePreferencesPanel.atStartupLabel");

      //i18n[UpdatePreferencesPanel.autoBorderLabel=Automatic Updates]
      String AUTO_BORDER_LABEL = 
         s_stringMgr.getString("UpdatePreferencesPanel.autoBorderLabel");

      //i18n[UpdatePreferencesPanel.autoCheckFrequency=How often to check for 
      //updates:]
      String AUTO_CHECK_FREQUENCY = 
         s_stringMgr.getString("UpdatePreferencesPanel.autoCheckFrequency");

      //i18n[UpdatePreferencesPanel.channel=Channel:]
      String CHANNEL = s_stringMgr.getString("UpdatePreferencesPanel.channel");
      
      // i18n[UpdatePreferencesPanel.updateSiteBorderLabel=Update Site]
      String UPDATE_SITE_BORDER_LABEL = 
         s_stringMgr.getString("UpdatePreferencesPanel.updateSiteBorderLabel");
	   
      //i18n[UpdatePreferencesPanel.enableAutoUpdate=Enable Automatic Updates]
      String ENABLE_AUTO_UPDATE = 
         s_stringMgr.getString("UpdatePreferencesPanel.enableAutoUpdate");

	   // i18n[UpdatePreferencesPanel.hint=Software Update Settings]
	   String HINT = s_stringMgr.getString("UpdatePreferencesPanel.hint");
	   
	   // i18n[UpdatePreferencesPanel.localPathLabel=Local Path:]
	   String LOCAL_PATH = 
	      s_stringMgr.getString("UpdatePreferencesPanel.localPathLabel");
	   
      //i18n[UpdatePreferencesPanel.path=Path:]
      String PATH = s_stringMgr.getString("UpdatePreferencesPanel.path");

	   // i18n[UpdatePreferencesPanel.port=Port:]
	   String PORT = s_stringMgr.getString("UpdatePreferencesPanel.port");
	   
	   // i18n[UpdatePreferencesPanel.server=Server:]
      String SERVER = s_stringMgr.getString("UpdatePreferencesPanel.server");
      
      //i18n[UpdatePreferencesPanel.stableChannelLabel=Snapshot]     
      String SNAPSHOT_CHANNEL_LABEL = 
         s_stringMgr.getString("UpdatePreferencesPanel.snapshotChannelLabel");

      //i18n[UpdatePreferencesPanel.stableChannelLabel=Stable]
      String STABLE_CHANNEL_LABEL = 
         s_stringMgr.getString("UpdatePreferencesPanel.stableChannelLabel");
      
	   // i18n[UpdatePreferencesPanel.title=Update]
	   String TITLE = s_stringMgr.getString("UpdatePreferencesPanel.title");

      //i18n[UpdatePreferencesPanel.weeklyLabel=Weekly]
      String WEEKLY_LABEL = 
         s_stringMgr.getString("UpdatePreferencesPanel.weeklyLabel");
	
	}
	
	/**
	 * Default ctor.
	 */
	public UpdatePreferencesPanel()
	{
		super();
	}

	public void initialize(IApplication app)
	{
		if (app == null)
		{
			throw new IllegalArgumentException("Null IApplication passed");
		}

		_app = app;

		((MyPanel)getPanelComponent()).loadData(_app.getSquirrelPreferences());
	}

   public void uninitialize(IApplication app)
   {
      
   }

   public synchronized Component getPanelComponent()
	{
		if (_myPanel == null)
		{
			_myPanel = new MyPanel();
		}
		return _myPanel;
	}

	public void applyChanges()
	{
		_myPanel.applyChanges(_app.getSquirrelPreferences());
	}

	public String getTitle()
	{
		return i18n.TITLE;
	}

	public String getHint()
	{
		return i18n.HINT;
	}


   private static final class MyPanel extends JPanel
   {
      private static final long serialVersionUID = 6411907298042579120L;
      private JLabel serverLabel = null;
      private JLabel portLabel = null;
      private JLabel pathLabel = null;
      private JLabel localPathLabel = null;
      private JLabel channelLabel = null;
      private JTextField _updateServerName = new JTextField();
      private JTextField _updateServerPort = new JTextField();
      private JTextField _updateServerPath = new JTextField();
      private JTextField _localPath = new JTextField();
      
      private JLabel siteTypeLabel = null;
      private JRadioButton _remoteTypeButton = new JRadioButton("Remote");
      private JRadioButton _localTypeButton = new JRadioButton("Local");
      private ButtonGroup _updateSiteTypeGroup = new ButtonGroup();
      
      private UpdateChannelComboBoxEntry stableChannel = 
         new UpdateChannelComboBoxEntry(ChannelType.STABLE, 
                                  i18n.STABLE_CHANNEL_LABEL); 
      
      private UpdateChannelComboBoxEntry snapshotChannel = 
         new UpdateChannelComboBoxEntry(ChannelType.SNAPSHOT, 
                                  i18n.SNAPSHOT_CHANNEL_LABEL);
      
      private JComboBox _updateServerChannel = 
         new JComboBox(new Object[] { stableChannel, snapshotChannel } );
      
      private JCheckBox _enableAutoUpdateChk = 
         new JCheckBox(i18n.ENABLE_AUTO_UPDATE);
      
      private UpdateCheckFrequencyComboBoxEntry checkAtStartup = 
         new UpdateCheckFrequencyComboBoxEntry(Frequency.AT_STARTUP, 
                                               i18n.AT_STARTUP_LABEL);
      
      private UpdateCheckFrequencyComboBoxEntry checkWeekly = 
         new UpdateCheckFrequencyComboBoxEntry(Frequency.WEEKLY, 
                                               i18n.WEEKLY_LABEL);
      
      private JComboBox _updateCheckFrequency = 
         new JComboBox(new Object[] {checkAtStartup, checkWeekly});
      
      private final Insets SEP_INSETS = new Insets(10, 14, 0, 14);
      private final Insets LABEL_INSETS = new Insets(2, 28, 6, 0);
      private final Insets FIELD_INSETS = new Insets(2, 8, 6, 28);      
      
      MyPanel()
      {
         super(new GridBagLayout());
         createUserInterface();
      }

      void loadData(SquirrelPreferences prefs)
      {
         final UpdateSettings updateSettings = prefs.getUpdateSettings();

         _updateServerName.setText(updateSettings.getUpdateServer());
         _updateServerPort.setText(updateSettings.getUpdateServerPort());
         _updateServerPath.setText(updateSettings.getUpdateServerPath());

         String channelStr = updateSettings.getUpdateServerChannel();
         _updateServerChannel.setSelectedItem(stableChannel);
         if (channelStr != null 
               && channelStr.equals(ChannelType.SNAPSHOT.name())) 
         {
            _updateServerChannel.setSelectedItem(snapshotChannel);
         }

         _enableAutoUpdateChk.setSelected(updateSettings.isEnableAutomaticUpdates());
         
         String freqStr = updateSettings.getUpdateCheckFrequency();
         _updateCheckFrequency.setSelectedItem(checkWeekly);
         if (freqStr != null && freqStr.equals(Frequency.AT_STARTUP.name())) 
         {
            _updateCheckFrequency.setSelectedItem(checkAtStartup);
         }
         if (updateSettings.isRemoteUpdateSite()) {
            _remoteTypeButton.setSelected(true);
            enableRemoteSite();
         } else {
            _localTypeButton.setSelected(true);
            enableLocalPath();
         }
         updateControlStatus();
      }

      void applyChanges(SquirrelPreferences prefs)
      {
         
         final UpdateSettings updateSettings = new UpdateSettings();

         updateSettings.setUpdateServer(_updateServerName.getText());
         updateSettings.setUpdateServerPort(_updateServerPort.getText());
         updateSettings.setUpdateServerPath(_updateServerPath.getText());

         UpdateChannelComboBoxEntry channelEntry = 
            (UpdateChannelComboBoxEntry)_updateServerChannel.getSelectedItem();
         
         String channelStr = ChannelType.STABLE.name();
         if (channelEntry.isSnapshot()) {
            channelStr = ChannelType.SNAPSHOT.name();
         }
         updateSettings.setUpdateServerChannel(channelStr);

         updateSettings.setEnableAutomaticUpdates(_enableAutoUpdateChk.isSelected());
         
         UpdateCheckFrequencyComboBoxEntry freqEntry = 
            (UpdateCheckFrequencyComboBoxEntry)_updateCheckFrequency.getSelectedItem();
         String freqStr = Frequency.WEEKLY.name();
         if (freqEntry.isStartup()) {
            freqStr = Frequency.AT_STARTUP.name();
         }
         updateSettings.setUpdateCheckFrequency(freqStr);

         prefs.setUpdateSettings(updateSettings);
         
      }

      private void updateControlStatus()
      {
         final boolean enableAutoCheck = _enableAutoUpdateChk.isSelected();
         _updateCheckFrequency.setEnabled(enableAutoCheck);
      }

      private void createUserInterface()
      {
         final GridBagConstraints gbc = new GridBagConstraints();
         gbc.fill = GridBagConstraints.HORIZONTAL;
         gbc.insets = new Insets(4, 4, 4, 4);
         gbc.gridx = 0;
         gbc.gridy = 0;
         gbc.weightx = 1;
         add(createUpdateSitePanel(), gbc);
         ++gbc.gridy;
         add(createAutoUpdatePanel(), gbc);

         final ActionListener lis = new MyActionHandler();
         _enableAutoUpdateChk.addActionListener(lis);
      }

      private JPanel createUpdateSitePanel()
      {         
         JPanel pnl = new JPanel(new GridBagLayout());
         pnl.setBorder(BorderFactory.createTitledBorder(i18n.UPDATE_SITE_BORDER_LABEL));

         final GridBagConstraints gbc = new GridBagConstraints();
         gbc.gridx = 0;
         gbc.gridy = 0;
         gbc.gridwidth = 1;
         gbc.insets = SEP_INSETS;
         gbc.fill = GridBagConstraints.NONE;
         gbc.anchor = GridBagConstraints.EAST;
         siteTypeLabel = new JLabel("Site Type:", JLabel.RIGHT);
         pnl.add(siteTypeLabel, gbc);
         
         gbc.gridx = 1;
         gbc.gridy = 0;
         gbc.gridwidth = 1;
         gbc.weightx = 1;
         gbc.insets = SEP_INSETS;
         gbc.fill = GridBagConstraints.NONE;
         gbc.anchor = GridBagConstraints.WEST;
         pnl.add(getSiteTypePanel(), gbc);
         
         gbc.gridx = 0;
         gbc.gridy = 1;
         gbc.gridwidth = 2;
         gbc.insets = SEP_INSETS;
         gbc.fill = GridBagConstraints.HORIZONTAL;
         gbc.anchor = GridBagConstraints.WEST;
         pnl.add(getSep(), gbc);

         
         gbc.fill = GridBagConstraints.NONE;
         gbc.gridx = 0;
         gbc.gridy = 2;
         gbc.gridwidth = 1;
         gbc.weightx = 0;
         gbc.insets = LABEL_INSETS;
         gbc.anchor = GridBagConstraints.EAST;
         serverLabel = new JLabel(i18n.SERVER, SwingConstants.RIGHT);
         pnl.add(serverLabel, gbc);

         gbc.gridx = 1;
         gbc.gridy = 2;
         gbc.gridwidth = 1;
         gbc.weightx = 1;
         gbc.insets = FIELD_INSETS;
         gbc.fill = GridBagConstraints.HORIZONTAL;
         gbc.anchor = GridBagConstraints.WEST;
         pnl.add(_updateServerName, gbc);
         
         gbc.gridx = 0;
         gbc.gridy = 3;
         gbc.gridwidth = 1;
         gbc.weightx = 0;
         gbc.insets = LABEL_INSETS;
         gbc.fill = GridBagConstraints.NONE;
         gbc.anchor = GridBagConstraints.EAST;
         portLabel = new JLabel(i18n.PORT, SwingConstants.RIGHT);
         pnl.add(portLabel, gbc);

         gbc.gridx = 1;
         gbc.gridy = 3;
         gbc.gridwidth = 1;
         gbc.weightx = 1;
         gbc.insets = FIELD_INSETS;
         gbc.fill = GridBagConstraints.HORIZONTAL;
         gbc.anchor = GridBagConstraints.WEST;
         pnl.add(_updateServerPort, gbc);

         gbc.gridx = 0;
         gbc.gridy = 4;
         gbc.gridwidth = 1;
         gbc.weightx = 0;
         gbc.insets = LABEL_INSETS;
         gbc.fill = GridBagConstraints.NONE;
         gbc.anchor = GridBagConstraints.EAST;
         pathLabel = new JLabel(i18n.PATH, SwingConstants.RIGHT);
         pnl.add(pathLabel, gbc);

         gbc.gridx = 1;
         gbc.gridy = 4;
         gbc.gridwidth = 1;
         gbc.weightx = 1;
         gbc.insets = FIELD_INSETS;
         gbc.fill = GridBagConstraints.HORIZONTAL;
         gbc.anchor = GridBagConstraints.WEST;
         pnl.add(_updateServerPath, gbc);
         
         gbc.gridx = 0;
         gbc.gridy = 5;
         gbc.gridwidth = 1;
         gbc.weightx = 0;
         gbc.insets = LABEL_INSETS;
         gbc.fill = GridBagConstraints.NONE;
         gbc.anchor = GridBagConstraints.EAST;
         channelLabel = new JLabel(i18n.CHANNEL, SwingConstants.RIGHT);
         pnl.add(channelLabel, gbc);

         gbc.gridx = 1;
         gbc.gridy = 5;
         gbc.gridwidth = 1;
         gbc.weightx = 1;
         gbc.insets = FIELD_INSETS;
         gbc.fill = GridBagConstraints.NONE;
         gbc.anchor = GridBagConstraints.WEST;
         pnl.add(_updateServerChannel, gbc);
         
         gbc.gridx = 0;
         gbc.gridy = 6;
         gbc.gridwidth = 2;
         gbc.insets = SEP_INSETS;
         gbc.fill = GridBagConstraints.HORIZONTAL;
         gbc.anchor = GridBagConstraints.WEST;
         pnl.add(getSep(), gbc);
         
         gbc.gridx = 0;
         gbc.gridy = 7;
         gbc.gridwidth = 1;
         gbc.weightx = 0;
         gbc.insets = LABEL_INSETS;
         gbc.fill = GridBagConstraints.NONE;
         gbc.anchor = GridBagConstraints.EAST;
         localPathLabel = new JLabel(i18n.LOCAL_PATH, SwingConstants.RIGHT);
         pnl.add(localPathLabel, gbc);         
         
         gbc.gridx = 1;
         gbc.gridy = 7;
         gbc.gridwidth = 1;
         gbc.weightx = 1;
         gbc.insets = FIELD_INSETS;
         gbc.fill = GridBagConstraints.HORIZONTAL;
         gbc.anchor = GridBagConstraints.WEST;
         pnl.add(_localPath, gbc);
         
         
         
         return pnl;
      }

      private void enableRemoteSite() {
         _localPath.setEnabled(false);
         localPathLabel.setEnabled(false);
         _updateServerChannel.setEnabled(true);
         _updateServerName.setEnabled(true);
         _updateServerPath.setEnabled(true);
         _updateServerPort.setEnabled(true);
         serverLabel.setEnabled(true);
         portLabel.setEnabled(true);
         pathLabel.setEnabled(true);
         channelLabel.setEnabled(true);
      }
      
      private void enableLocalPath() {
         _localPath.setEnabled(true);
         localPathLabel.setEnabled(true);
         _updateServerChannel.setEnabled(false);
         _updateServerName.setEnabled(false);
         _updateServerPath.setEnabled(false);
         _updateServerPort.setEnabled(false);
         serverLabel.setEnabled(false);
         portLabel.setEnabled(false);
         pathLabel.setEnabled(false);
         channelLabel.setEnabled(false);         
      }
      
      private JPanel getSiteTypePanel() {
         _remoteTypeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               enableRemoteSite();
            }
         });
         _localTypeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               enableLocalPath();
            }
         });
         _updateSiteTypeGroup.add(_remoteTypeButton);
         _updateSiteTypeGroup.add(_localTypeButton);
         JPanel siteTypePanel = new JPanel();
         siteTypePanel.add(_remoteTypeButton);
         siteTypePanel.add(_localTypeButton);
         return siteTypePanel;
      }
      
      private JSeparator getSep() {
         JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
         // separators need preferred size in GridBagLayout
         sep.setPreferredSize(new Dimension(100, 20));
         sep.setMinimumSize(new Dimension(100, 20));
         return sep;         
      }
      
      private JPanel createAutoUpdatePanel()
      {
         JPanel pnl = new JPanel(new GridBagLayout());
         pnl.setBorder(BorderFactory.createTitledBorder(i18n.AUTO_BORDER_LABEL));

         final GridBagConstraints gbc = new GridBagConstraints();
         gbc.anchor = GridBagConstraints.WEST;
         gbc.insets = new Insets(4, 4, 4, 4);
         gbc.gridx = 0;
         gbc.gridy = 0;
         gbc.gridwidth = 2;
         gbc.fill = GridBagConstraints.HORIZONTAL;
         pnl.add(_enableAutoUpdateChk, gbc);

         gbc.gridwidth = 1;
         
         gbc.gridx = 0;
         gbc.gridy = 1;
         gbc.weightx = 0;
         gbc.insets = new Insets(4, 20, 4, 10);
         pnl.add(new JLabel(i18n.AUTO_CHECK_FREQUENCY, JLabel.LEFT), gbc);

         gbc.gridx = 1;
         gbc.gridy = 1;
         gbc.weightx = 1;
         gbc.insets = new Insets(4, 0, 4, 0);
         gbc.fill = GridBagConstraints.NONE;
         pnl.add(this._updateCheckFrequency, gbc);

         return pnl;
      }

      private final class MyActionHandler implements ActionListener
      {
         public void actionPerformed(ActionEvent evt)
         {
            updateControlStatus();
         }
      }
           
   }


}
