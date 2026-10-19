package net.sourceforge.squirrel_sql.client.preferences;

import net.sourceforge.squirrel_sql.client.gui.db.mainframetitle.MainFrameTitlePrefsCtrl;
import net.sourceforge.squirrel_sql.client.gui.db.passwordaccess.PasswordAccessPrefsCtrl;
import net.sourceforge.squirrel_sql.client.session.messagepanel.MessagePrefsCtrl;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.IntegerField;
import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

final class GeneralPreferencesGUI extends JPanel
{

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(GeneralPreferencesGUI.class);

   private JRadioButton _tabbedStyle = new JRadioButton(s_stringMgr.getString("GeneralPreferencesPanel.tabbedStyle"));
    private JRadioButton _internalFrameStyle = new JRadioButton(s_stringMgr.getString("GeneralPreferencesPanel.internalFrameStyle"));
   private JRadioButton _useNewFramePerConnection = new JRadioButton(s_stringMgr.getString("GeneralPreferencesPanel.useNewFramePerConnection"));
   private JCheckBox _useScrollableTabbedPanesForSessionTabs = new JCheckBox(s_stringMgr.getString("GeneralPreferencesPanel.useScrollableTabbedPanesForSessionTabs"));
   private JCheckBox _showContents = new JCheckBox(s_stringMgr.getString("GeneralPreferencesPanel.showwindowcontents"));
   private JCheckBox _maximimizeSessionSheet = new JCheckBox(s_stringMgr.getString("GeneralPreferencesPanel.maxonopen"));
   private JCheckBox _showTabbedStyleHint = new JCheckBox(s_stringMgr.getString("GeneralPreferencesPanel.showTabbedStyleHint"));


   private JCheckBox _showAliasesToolBar = new JCheckBox(s_stringMgr.getString("GeneralPreferencesPanel.showaliasestoolbar"));
   private JCheckBox _showDriversToolBar = new JCheckBox(s_stringMgr.getString("GeneralPreferencesPanel.showdriverstoolbar"));
   private JCheckBox _showMainStatusBar = new JCheckBox(s_stringMgr.getString("GeneralPreferencesPanel.showmainwinstatusbar"));
   private JCheckBox _showMainToolBar = new JCheckBox(s_stringMgr.getString("GeneralPreferencesPanel.showmainwintoolbar"));
   private JCheckBox _showToolTips = new JCheckBox(s_stringMgr.getString("GeneralPreferencesPanel.showtooltips"));
   private JCheckBox _useScrollableTabbedPanes = new JCheckBox(s_stringMgr.getString("GeneralPreferencesPanel.usescrolltabs"));

   private JCheckBox _showColoriconsInToolbar = new JCheckBox(s_stringMgr.getString("GeneralPreferencesPanel.showcoloricons"));
   private JCheckBox _showPluginFilesInSplashScreen = new JCheckBox(s_stringMgr.getString("GeneralPreferencesPanel.showpluginfiles"));
   private JCheckBox _useShortSessionTitle = new JCheckBox(s_stringMgr.getString("GeneralPreferencesPanel.useShortSessionTitle"));
   private JCheckBox _rememberValueOfPopup = new JCheckBox(s_stringMgr.getString("GeneralPreferencesPanel.rememberValueOfPopup"));
   private IntegerField _maxCharsInValuePopup = new IntegerField(8, 0);


   //      private JLabel _executionLogFileNameLbl = new OutputLabel(" ");
//      // Must have at least 1 blank otherwise width gets set to zero.
//      private JLabel _logConfigFileNameLbl = new OutputLabel(" ");
//      // Must have at least 1 blank otherwise width gets set to zero.
   private JCheckBox _confirmSessionCloseChk = new JCheckBox(s_stringMgr.getString("GeneralPreferencesPanel.confirmSessionClose"));
   private JCheckBox _warnJreJdbcMismatch = new JCheckBox(s_stringMgr.getString("GeneralPreferencesPanel.warnJreJdbcMismatch"));
   private JCheckBox _warnForUnsavedFileEdits = new JCheckBox(s_stringMgr.getString("GeneralPreferencesPanel.warnForUnsavedFileEdits"));
   private JCheckBox _warnForUnsavedBufferEdits = new JCheckBox(s_stringMgr.getString("GeneralPreferencesPanel.warnForUnsavedBufferEdits"));
   private JCheckBox _showSessionStartupTimeHint = new JCheckBox(s_stringMgr.getString("GeneralPreferencesPanel.showSessionStartupTimeHint"));

   private JRadioButton _saveNoPreferencesImmediatelyNone = new JRadioButton(s_stringMgr.getString("GeneralPreferencesPanel.saveNoPreferencesImmediately"));
   private JRadioButton _saveAliasesAndDriversImmediately = new JRadioButton(s_stringMgr.getString("GeneralPreferencesPanel.saveAliasesAndDriversImmediately"));
   private JRadioButton _savePreferencesImmediately = new JRadioButton(s_stringMgr.getString("GeneralPreferencesPanel.savePreferencesImmediately_new"));

   private JCheckBox _selectOnRightMouseClick = new JCheckBox(s_stringMgr.getString("GeneralPreferencesPanel.selectOnRightMouseClick"));
   private JCheckBox _showPleaseWaitDialog = new JCheckBox(s_stringMgr.getString("GeneralPreferencesPanel.showPleaseWaitDialog"));

   private JComboBox _localeChooser = new JComboBox(LocaleWrapper.getAvailableLocaleWrappers());
   private MaxColumnAdjustLengthCtrl _maxColumnAdjustLengthCtrl = new MaxColumnAdjustLengthCtrl();
   private MessagePrefsCtrl _messagePrefsCtrl = new MessagePrefsCtrl();

   private PasswordAccessPrefsCtrl _passwordAccessPrefsCtrl = new PasswordAccessPrefsCtrl();

   private MainFrameTitlePrefsCtrl _mainFrameTitlePrefsCtrl = new MainFrameTitlePrefsCtrl();

   GeneralPreferencesGUI()
   {
      super(new GridBagLayout());
      createUserInterface();
   }

   void loadData(SquirrelPreferences prefs)
   {
       if (prefs.getUseNewFramePerConnection())
       {
           _useNewFramePerConnection.setSelected(true);
           _tabbedStyle.setSelected(false);
           _internalFrameStyle.setSelected(false);
       }
       else {
           _tabbedStyle.setSelected(prefs.getTabbedStyle());
           _internalFrameStyle.setSelected(!prefs.getTabbedStyle());
           _useNewFramePerConnection.setSelected(false);
       }
      _useScrollableTabbedPanesForSessionTabs.setSelected(prefs.getUseScrollableTabbedPanesForSessionTabs());
      onStyleChanged();
      _showTabbedStyleHint.setSelected(prefs.getShowTabbedStyleHint());


      _showContents.setSelected(prefs.getShowContentsWhenDragging());
      _maximimizeSessionSheet.setSelected(prefs.getMaximizeSessionSheetOnOpen());

      _showToolTips.setSelected(prefs.getShowToolTips());
      _useScrollableTabbedPanes.setSelected(prefs.getUseScrollableTabbedPanes());
      _showMainStatusBar.setSelected(prefs.getShowMainStatusBar());
      _showMainToolBar.setSelected(prefs.getShowMainToolBar());
      _showAliasesToolBar.setSelected(prefs.getShowAliasesToolBar());
      _showDriversToolBar.setSelected(prefs.getShowDriversToolBar());
      _showColoriconsInToolbar.setSelected(prefs.getShowColoriconsInToolbar());
      _showPluginFilesInSplashScreen.setSelected(prefs.getShowPluginFilesInSplashScreen());
      _useShortSessionTitle.setSelected(prefs.getUseShortSessionTitle());
      _rememberValueOfPopup.setSelected(prefs.isRememberValueOfPopup());
      _maxCharsInValuePopup.setInt(prefs.getMaxCharsInValuePopup());

      _confirmSessionCloseChk.setSelected(prefs.getConfirmSessionClose());
      _warnJreJdbcMismatch.setSelected(prefs.getWarnJreJdbcMismatch());
      _warnForUnsavedFileEdits.setSelected(prefs.getWarnForUnsavedFileEdits());
      _warnForUnsavedBufferEdits.setSelected(prefs.getWarnForUnsavedBufferEdits());
      _showSessionStartupTimeHint.setSelected(prefs.getShowSessionStartupTimeHint());


      _saveNoPreferencesImmediatelyNone.setSelected(true); // Default, may be reset by the following two on through their ButtonGroup
      _saveAliasesAndDriversImmediately.setSelected(prefs.getSaveAliasesAndDriversImmediately());
      _savePreferencesImmediately.setSelected(prefs.getSavePreferencesImmediately());

      _selectOnRightMouseClick.setSelected(prefs.getSelectOnRightMouseClick());
      _showPleaseWaitDialog.setSelected(prefs.getShowPleaseWaitDialog());
      _maxColumnAdjustLengthCtrl.init(prefs.getMaxColumnAdjustLengthDefined(), prefs.getMaxColumnAdjustLength());

      LocaleWrapper.setSelectedLocalePrefsString(_localeChooser, prefs.getPreferredLocale());

      _tabbedStyle.addActionListener(e -> onStyleChanged());
      _internalFrameStyle.addActionListener(e -> onStyleChanged());
      _useNewFramePerConnection.addActionListener(e -> onStyleChanged());

      _messagePrefsCtrl.loadData(prefs);

      _passwordAccessPrefsCtrl.loadData(prefs);
      _mainFrameTitlePrefsCtrl.loadData(prefs);

   }

   private void onStyleChanged()
   {
      _useScrollableTabbedPanesForSessionTabs.setEnabled(_tabbedStyle.isSelected());
      _showContents.setEnabled(_internalFrameStyle.isSelected());
      _maximimizeSessionSheet.setEnabled(_internalFrameStyle.isSelected());
      _showTabbedStyleHint.setEnabled(_internalFrameStyle.isSelected());
   }

   void applyChanges(SquirrelPreferences prefs)
   {
       if (_useNewFramePerConnection.isSelected()) {
           prefs.setUseNewFramePerConnection(true);
           prefs.setTabbedStyle(true);
       } else {
           prefs.setUseNewFramePerConnection(false);
           prefs.setTabbedStyle(_tabbedStyle.isSelected());
       }
      prefs.setUseScrollableTabbedPanesForSessionTabs(_useScrollableTabbedPanesForSessionTabs.isSelected());
      prefs.setShowContentsWhenDragging(_showContents.isSelected());
      prefs.setShowTabbedStyleHint(_showTabbedStyleHint.isSelected());
      prefs.setShowToolTips(_showToolTips.isSelected());
      prefs.setUseScrollableTabbedPanes(_useScrollableTabbedPanes.isSelected());
      prefs.setShowMainStatusBar(_showMainStatusBar.isSelected());
      prefs.setShowMainToolBar(_showMainToolBar.isSelected());
      prefs.setShowAliasesToolBar(_showAliasesToolBar.isSelected());
      prefs.setShowDriversToolBar(_showDriversToolBar.isSelected());
      prefs.setMaximizeSessionSheetOnOpen(_maximimizeSessionSheet.isSelected());
      prefs.setShowColoriconsInToolbar(_showColoriconsInToolbar.isSelected());
      prefs.setShowPluginFilesInSplashScreen(_showPluginFilesInSplashScreen.isSelected());
      prefs.setUseShortSessionTitle(_useShortSessionTitle.isSelected());
      prefs.setRememberValueOfPopup(_rememberValueOfPopup.isSelected());
      prefs.setMaxCharsInValuePopup(_maxCharsInValuePopup.getInt());
      prefs.setConfirmSessionClose(_confirmSessionCloseChk.isSelected());
      prefs.setWarnJreJdbcMismatch(_warnJreJdbcMismatch.isSelected());
      prefs.setWarnForUnsavedFileEdits(_warnForUnsavedFileEdits.isSelected());
      prefs.setWarnForUnsavedBufferEdits(_warnForUnsavedBufferEdits.isSelected());
      prefs.setShowSessionStartupTimeHint(_showSessionStartupTimeHint.isSelected());

      prefs.setSaveAliasesAndDriversImmediately(_saveAliasesAndDriversImmediately.isSelected());
      prefs.setSavePreferencesImmediately(_savePreferencesImmediately.isSelected());

      prefs.setSelectOnRightMouseClick(_selectOnRightMouseClick.isSelected());
      prefs.setShowPleaseWaitDialog(_showPleaseWaitDialog.isSelected());
      prefs.setPreferredLocale(LocaleWrapper.getSelectedLocalePrefsString(_localeChooser));
      prefs.setMaxColumnAdjustLengthDefined(_maxColumnAdjustLengthCtrl.isMaxColumnAdjustLengthDefined());
      prefs.setMaxColumnAdjustLength(_maxColumnAdjustLengthCtrl.getMaxColumnAdjustLength());

      _messagePrefsCtrl.applyChanges(prefs);

      _passwordAccessPrefsCtrl.applyChanges(prefs);
      _mainFrameTitlePrefsCtrl.applyChanges(prefs);
   }

   private void createUserInterface()
   {
      final GridBagConstraints gbc = new GridBagConstraints();
      gbc.fill = GridBagConstraints.HORIZONTAL;
      gbc.anchor = GridBagConstraints.NORTHWEST;
      gbc.insets = new Insets(4, 4, 4, 4);
      gbc.gridx = 0;
      gbc.gridy = 0;
      gbc.weightx = 1;
      add(createAppearancePanel(), gbc);
      ++gbc.gridx;
      add(createGeneralPanel(), gbc);
      gbc.gridx = 0;
      ++gbc.gridy;
      gbc.gridwidth = 2;
      add(createLoggingPanel(), gbc);
      gbc.gridx = 0;
      ++gbc.gridy;
      gbc.gridwidth = 2;
      add(createPathsPanel(), gbc);
   }

   private JPanel createAppearancePanel()
   {
      final JPanel pnl = new JPanel(new GridBagLayout());
      pnl.setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("GeneralPreferencesPanel.appearance")));
      pnl.setLayout(new GridBagLayout());

      ButtonGroup g = new ButtonGroup();

      g.add(_tabbedStyle);
      g.add(_internalFrameStyle);
      g.add(_useNewFramePerConnection);
      final GridBagConstraints gbc = new GridBagConstraints();
      gbc.fill = GridBagConstraints.HORIZONTAL;
      gbc.insets = new Insets(2, 4, 2, 4);
      gbc.gridx = 0;
      gbc.weightx = 1;
      gbc.insets.top = 0;

      gbc.gridy = 0;
      _tabbedStyle.setName("tabbedStyleRadioButton");
      pnl.add(_tabbedStyle, gbc);
      ++gbc.gridy;
      _internalFrameStyle.setName("internalFrameStyleRadioButton");
      pnl.add(_internalFrameStyle, gbc);
      ++gbc.gridy;
      _useNewFramePerConnection.setName("useNewFramePerConnectionRadioButton");
      pnl.add(_useNewFramePerConnection, gbc);
      ++gbc.gridy;

      _useScrollableTabbedPanesForSessionTabs.setName("useScrollableTabbedPanes");
      pnl.add(_useScrollableTabbedPanesForSessionTabs, gbc);
      ++gbc.gridy;

      _showContents.setName("showContentsCheckBox");
      pnl.add(_showContents, gbc);
      ++gbc.gridy;
      _maximimizeSessionSheet.setName("maximizeSessionSheetCheckBox");
      pnl.add(_maximimizeSessionSheet, gbc);
      ++gbc.gridy;
      _showTabbedStyleHint.setName("showTabbedStyleHintCheckBox");
      pnl.add(_showTabbedStyleHint, gbc);


      ++gbc.gridy;
      pnl.add(_showToolTips, gbc);
      ++gbc.gridy;
      pnl.add(_useScrollableTabbedPanes, gbc);
      ++gbc.gridy;
      pnl.add(_showMainToolBar, gbc);
      ++gbc.gridy;
      pnl.add(_showMainStatusBar, gbc);
      ++gbc.gridy;
      pnl.add(_showDriversToolBar, gbc);
      ++gbc.gridy;
      pnl.add(_showAliasesToolBar, gbc);
      ++gbc.gridy;
      pnl.add(_showColoriconsInToolbar, gbc);
      ++gbc.gridy;
      pnl.add(_showPluginFilesInSplashScreen, gbc);
      ++gbc.gridy;
      pnl.add(_useShortSessionTitle, gbc);
      ++gbc.gridy;
      pnl.add(createValuePopupPanel(), gbc);

      //++gbc.gridy;
      //final GridBagConstraints gbcThemes = (GridBagConstraints) gbc.clone();
      //gbcThemes.fill = GridBagConstraints.NONE;
      //gbcThemes.anchor = GridBagConstraints.NORTHWEST;
      //pnl.add(new ThemesController(_messagePrefsCtrl).getPanel(), gbcThemes);

      ++gbc.gridy;
      final GridBagConstraints gbcMessagePanel = (GridBagConstraints) gbc.clone();
      gbcMessagePanel.fill = GridBagConstraints.NONE;
      gbcMessagePanel.anchor = GridBagConstraints.NORTHWEST;
      pnl.add(_messagePrefsCtrl.getPanel(), gbcMessagePanel);

      return pnl;
   }

   private JPanel createValuePopupPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0, 1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,0,0), 0,0);
      ret.add(_rememberValueOfPopup, gbc);

      gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10,5,0,0), 0,0);
      ret.add(new JLabel(s_stringMgr.getString("GeneralPreferencesPanel.maxCharNumbersInValueOfPopup")), gbc);

      gbc = new GridBagConstraints(0,2,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(2,5,0,0), 0,0);
      ret.add(_maxCharsInValuePopup, gbc);

      ret.setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("GeneralPreferencesPanel.value.popup.title")));

      return ret;
   }

   private JPanel createGeneralPanel()
   {
      final JPanel pnl = new JPanel();
      pnl.setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString(
            "GeneralPreferencesPanel.general")));
      pnl.setLayout(new GridBagLayout());

      final GridBagConstraints gbc = new GridBagConstraints();
      gbc.fill = GridBagConstraints.HORIZONTAL;
      gbc.insets = new Insets(2, 4, 2, 4);
      gbc.gridx = 0;
      gbc.gridy = 0;
      gbc.weightx = 1;
      pnl.add(_confirmSessionCloseChk, gbc);

      gbc.gridx = 0;
      gbc.gridy = 1;
      pnl.add(_warnJreJdbcMismatch, gbc);

      gbc.gridx = 0;
      gbc.gridy = 2;
      pnl.add(_warnForUnsavedFileEdits, gbc);

      gbc.gridx = 0;
      gbc.gridy = 3;
      pnl.add(_warnForUnsavedBufferEdits, gbc);

      gbc.gridx = 0;
      gbc.gridy = 4;
      pnl.add(_showSessionStartupTimeHint, gbc);

      gbc.gridx = 0;
      gbc.gridy = 5;
      pnl.add(getSavePreferencesImmediatelyPanel(), gbc);

      gbc.gridx = 0;
      gbc.gridy = 6;
      pnl.add(_selectOnRightMouseClick, gbc);

      gbc.gridx = 0;
      gbc.gridy = 7;
      pnl.add(_showPleaseWaitDialog, gbc);

      gbc.gridx = 0;
      gbc.gridy = 8;
      pnl.add(createLocalePanel(), gbc);

      gbc.gridx = 0;
      gbc.gridy = 9;
      pnl.add(_maxColumnAdjustLengthCtrl.getPanel(), gbc);

      gbc.gridx = 0;
      gbc.gridy = 10;
      pnl.add(_passwordAccessPrefsCtrl.getPanel(), gbc);

      gbc.gridx = 0;
      gbc.gridy = 11;
      pnl.add(_mainFrameTitlePrefsCtrl.getPanel(), gbc);

      return pnl;
   }


   private JPanel createLocalePanel()
   {
      JPanel localePanel = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE, new Insets(5,5,5,0),0,0 );
      localePanel.add(new JLabel(s_stringMgr.getString("GeneralPreferencesPanel.localeChooserLabel")), gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE, new Insets(5,5,5,0),0,0 );
      GUIUtils.setPreferredWidth(_localeChooser, 300);
      localePanel.add(_localeChooser, gbc);

      gbc = new GridBagConstraints(2,0,1,1,1,0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL, new Insets(0,0,5,0),0,0 );
      localePanel.add(new JPanel(), gbc);

      return localePanel;
   }

   private JPanel getSavePreferencesImmediatelyPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,1,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0,0);
      ret.add(new MultipleLineLabel(s_stringMgr.getString("GeneralPreferencesPanel.savePreferencesImmediatelyWarning_new")), gbc);

      gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(3,0,0,0), 0,0);
      ret.add(_saveNoPreferencesImmediatelyNone, gbc);

      gbc = new GridBagConstraints(0,2,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(3,0,0,0), 0,0);
      ret.add(_saveAliasesAndDriversImmediately, gbc);

      gbc = new GridBagConstraints(0,3,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(3,0,0,0), 0,0);
      ret.add(_savePreferencesImmediately, gbc);

      ButtonGroup bg = new ButtonGroup();
      bg.add(_saveNoPreferencesImmediatelyNone);
      bg.add(_saveAliasesAndDriversImmediately);
      bg.add(_savePreferencesImmediately);


      ret.setBorder(BorderFactory.createEtchedBorder());
      return ret;
   }


   private JPanel createLoggingPanel()
   {
      final JPanel pnl = new JPanel();
      pnl.setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("GeneralPreferencesPanel.logging")));

      pnl.setLayout(new GridBagLayout());
      final GridBagConstraints gbc = new GridBagConstraints();
      gbc.anchor = GridBagConstraints.NORTHWEST;
      gbc.fill = GridBagConstraints.NONE;
      gbc.insets = new Insets(2, 4, 2, 4);

      ApplicationFiles appFiles = new ApplicationFiles();
      String execLogFile = appFiles.getExecutionLogFile().getPath();

      gbc.gridx = 0;
      gbc.gridy = 0;
      JTextField execLogFileField = new JTextField(s_stringMgr.getString("GeneralPreferencesPanel.execlogfileNew", execLogFile));
      GUIUtils.styleTextFieldToCopyableLabel(execLogFileField);
      pnl.add(execLogFileField, gbc);

      gbc.weightx = 1.0;
      gbc.gridy = 0;
      ++gbc.gridx;
      pnl.add(new JPanel(), gbc);

      return pnl;
   }

   private JPanel createPathsPanel()
   {
      final JPanel pnl = new JPanel();
      // i18n[GeneralPreferencesPanel.paths=SQuirreL paths]
      pnl.setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("GeneralPreferencesPanel.paths")));

      pnl.setLayout(new GridBagLayout());
      final GridBagConstraints gbc = new GridBagConstraints();
      gbc.anchor = GridBagConstraints.NORTHWEST;
      gbc.fill = GridBagConstraints.NONE;
      gbc.insets = new Insets(2, 4, 2, 4);

      ApplicationFiles appFiles = new ApplicationFiles();
      String userDir = appFiles.getUserSettingsDirectory().getPath();
      String homeDir = appFiles.getSquirrelHomeDir().getPath();


      gbc.gridx = 0;
      gbc.gridy = 0;
      // i18n[GeneralPreferencesPanel.squirrelHomePath=Home directory: -home {0}]
      JTextField homePathField = new JTextField(s_stringMgr.getString("GeneralPreferencesPanel.squirrelHomePath", homeDir));
      GUIUtils.styleTextFieldToCopyableLabel(homePathField);
      pnl.add(homePathField, gbc);

      ++gbc.gridy;
      // i18n[GeneralPreferencesPanel.squirrelUserPath=User directory: -userdir {0}]
      JTextField userPathField = new JTextField(s_stringMgr.getString("GeneralPreferencesPanel.squirrelUserPath", userDir));
      GUIUtils.styleTextFieldToCopyableLabel(userPathField);
      pnl.add(userPathField, gbc);

      gbc.weightx = 1.0;

      gbc.gridy = 0;
      ++gbc.gridx;
      pnl.add(new JPanel(), gbc);

      ++gbc.gridy;
      pnl.add(new JPanel(), gbc);

      return pnl;
   }


}
