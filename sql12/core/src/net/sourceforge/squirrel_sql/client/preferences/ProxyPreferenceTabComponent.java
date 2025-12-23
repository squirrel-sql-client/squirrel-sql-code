package net.sourceforge.squirrel_sql.client.preferences;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.ProxySettings;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import org.apache.commons.lang3.StringUtils;

/**
* Created with IntelliJ IDEA.
* User: gerd
* Date: 03.03.13
* Time: 16:13
* To change this template use File | Settings | File Templates.
*/
public final class ProxyPreferenceTabComponent extends JPanel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ProxyPreferenceTabComponent.class);

   public static final String DEFAULT_PROXY_SETTINGS_NAME = "<Default>";

   private final ProxySettingsPanel _pnl = new ProxySettingsPanel();
   private ArrayList<ProxySettings> _additionalNamedProxySettings;
   private boolean _dontReactToAdditionalNamedProxySettingsChange;
   private ProxySettings _defaultProxySettings;

   private ProxySettings _currentSettings;

   ProxyPreferenceTabComponent()
   {
      setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(15,10,10,10), 0,0);
      add(_pnl, gbc);

      gbc = new GridBagConstraints(0,1,1,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0,0);
      add(new JPanel(), gbc);

      _pnl.httpUseProxyChk.addActionListener(e -> updateEnabled());
      _pnl.socksUseProxyChk.addActionListener(e -> updateEnabled());

      _pnl.cboAdditionalSettingsNames.addActionListener(e -> onCboAdditionalSettingsNamesChanged());
      _pnl.btnAddProxySetting.addActionListener(e -> onAddNewSetting());
      _pnl.btnRemoveProxySetting.addActionListener(e -> onDeleteSetting());
   }

   private void onDeleteSetting()
   {
      if(_currentSettings == _defaultProxySettings)
      {
         JOptionPane.showMessageDialog(this, s_stringMgr.getString("ProxyPreferenceTabComponent.cannot.delete.default.proxy.settings"));
         return;
      }

      int indexOfSettingToDelete = _additionalNamedProxySettings.indexOf(_currentSettings);

      if(-1 == indexOfSettingToDelete)
      {
         throw new IllegalStateException("Must contain settings of name " + _currentSettings.getSettingName());
      }

      _pnl.cboAdditionalSettingsNames.removeItemAt(indexOfSettingToDelete + 1); // + 1 because of the default setting name

      _additionalNamedProxySettings.remove(indexOfSettingToDelete);
      if(indexOfSettingToDelete + 1 < _additionalNamedProxySettings.size())
      {
         _currentSettings = _additionalNamedProxySettings.get(indexOfSettingToDelete + 1);
      }
      else
      {
         _currentSettings = _additionalNamedProxySettings.get(indexOfSettingToDelete + 1 - 1);
      }

      try(AutoCloseable ignored = () -> _dontReactToAdditionalNamedProxySettingsChange = false)
      {
         _dontReactToAdditionalNamedProxySettingsChange = true;
         _pnl.cboAdditionalSettingsNames.setSelectedItem(_currentSettings.getSettingName());
      }
      catch(Exception e)
      {
         throw Utilities.wrapRuntime(e);
      }

      applyCurrentSettingsToControls();
   }

   private void onAddNewSetting()
   {
      ProxySettingsAddCtrl proxySettingsAddCtrl =
            new ProxySettingsAddCtrl(
                  _additionalNamedProxySettings.stream().map(ps -> ps.getSettingName()).toList(),
                  GUIUtils.getOwningWindow(this));

      if(false == proxySettingsAddCtrl.isOk())
      {
         return;
      }

      applyChangesToCurrentSettings();

      _currentSettings = new ProxySettings();
      _currentSettings.setSettingName(proxySettingsAddCtrl.getNewSettingsName());

      _additionalNamedProxySettings.add(_currentSettings);

      try(AutoCloseable ignored = () -> _dontReactToAdditionalNamedProxySettingsChange = false)
      {
         _dontReactToAdditionalNamedProxySettingsChange = true;
         _pnl.cboAdditionalSettingsNames.addItem(_currentSettings.getSettingName());
         _pnl.cboAdditionalSettingsNames.setSelectedItem(_currentSettings.getSettingName());
      }
      catch(Exception e)
      {
         throw Utilities.wrapRuntime(e);
      }

      applyCurrentSettingsToControls();
   }

   private void onCboAdditionalSettingsNamesChanged()
   {
      if(_dontReactToAdditionalNamedProxySettingsChange)
      {
         return;
      }

      applyChangesToCurrentSettings();

      String newSelectedItem = (String) _pnl.cboAdditionalSettingsNames.getSelectedItem();

      if(StringUtils.equals(DEFAULT_PROXY_SETTINGS_NAME, newSelectedItem))
      {
         _currentSettings = _defaultProxySettings;
      }
      else
      {
         _currentSettings =
               _additionalNamedProxySettings.stream().filter(ps -> StringUtils.equals(ps.getSettingName(), newSelectedItem)).findFirst().orElseThrow();
      }

      applyCurrentSettingsToControls();
   }

   void loadData(SquirrelPreferences prefs)
   {
      _defaultProxySettings = Utilities.cloneObject(prefs.getProxySettings());
      _currentSettings = _defaultProxySettings;

      applyCurrentSettingsToControls();

      _additionalNamedProxySettings = new ArrayList<>(List.of(Utilities.cloneObject(prefs.getAdditionalNamedProxySettings())));

      try(AutoCloseable ignored = () -> _dontReactToAdditionalNamedProxySettingsChange = false)
      {
         _dontReactToAdditionalNamedProxySettingsChange = true;
         _pnl.cboAdditionalSettingsNames.addItem(DEFAULT_PROXY_SETTINGS_NAME);
         for(ProxySettings additionalNamedProxySetting : _additionalNamedProxySettings)
         {
            _pnl.cboAdditionalSettingsNames.addItem(additionalNamedProxySetting.getSettingName());
         }

         _pnl.cboAdditionalSettingsNames.setSelectedItem(DEFAULT_PROXY_SETTINGS_NAME);
      }
      catch(Exception e)
      {
         throw Utilities.wrapRuntime(e);
      }

      updateEnabled();
   }

   private void applyCurrentSettingsToControls()
   {
      _pnl.httpUseProxyChk.setSelected(_currentSettings.getHttpUseProxy());
      _pnl.httpProxyServer.setText(_currentSettings.getHttpProxyServer());
      _pnl.httpProxyPort.setText(_currentSettings.getHttpProxyPort());
      _pnl.httpNonProxyHosts.setText(_currentSettings.getHttpNonProxyHosts());
      _pnl.httpProxyUser.setText(_currentSettings.getHttpProxyUser());
      _pnl.httpProxyPassword.setText(_currentSettings.getHttpProxyPassword());

      _pnl.socksUseProxyChk.setSelected(_currentSettings.getSocksUseProxy());
      _pnl.socksProxyServer.setText(_currentSettings.getSocksProxyServer());
      _pnl.socksProxyPort.setText(_currentSettings.getSocksProxyPort());

      updateEnabled();
   }

   void applyChangesToSquirrelPrefs(SquirrelPreferences prefs)
   {
      applyChangesToCurrentSettings();

      prefs.setProxySettings(_defaultProxySettings);
      prefs.setAdditionalNamedProxySettings(_additionalNamedProxySettings.toArray(new ProxySettings[0]));
   }

   private void applyChangesToCurrentSettings()
   {
      _currentSettings.setHttpUseProxy(_pnl.httpUseProxyChk.isSelected());
      _currentSettings.setHttpProxyServer(_pnl.httpProxyServer.getText());
      _currentSettings.setHttpProxyPort(_pnl.httpProxyPort.getText());
      _currentSettings.setHttpNonProxyHosts(_pnl.httpNonProxyHosts.getText());
      _currentSettings.setHttpProxyUser(_pnl.httpProxyUser.getText());

      String password = new String(_pnl.httpProxyPassword.getPassword());
      _currentSettings.setHttpProxyPassword(password);

      _currentSettings.setSocksUseProxy(_pnl.socksUseProxyChk.isSelected());
      _currentSettings.setSocksProxyServer(_pnl.socksProxyServer.getText());
      _currentSettings.setSocksProxyPort(_pnl.socksProxyPort.getText());
   }

   private void updateEnabled()
   {
      final boolean http = _pnl.httpUseProxyChk.isSelected();
      _pnl.httpProxyServerLabel.setEnabled(http);
      _pnl.httpProxyServer.setEnabled(http);
      _pnl.httpProxyPortLabel.setEnabled(http);
      _pnl.httpProxyPort.setEnabled(http);
      _pnl.httpNonProxyHostsLabel.setEnabled(http);
      _pnl.httpNonProxyHosts.setEnabled(http);
      _pnl.httpProxyUserLabel.setEnabled(http);
      _pnl.httpProxyUser.setEnabled(http);
      _pnl.httpProxyPasswordLabel.setEnabled(http);
      _pnl.httpProxyPassword.setEnabled(http);

      final boolean socks = _pnl.socksUseProxyChk.isSelected();
      _pnl.socksProxyServerLabel.setEnabled(socks);
      _pnl.socksProxyServer.setEnabled(socks);
      _pnl.socksProxyPortLabel.setEnabled(socks);
      _pnl.socksProxyPort.setEnabled(socks);
   }

}
