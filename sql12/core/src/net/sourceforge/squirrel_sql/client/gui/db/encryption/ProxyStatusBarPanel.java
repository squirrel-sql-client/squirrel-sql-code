package net.sourceforge.squirrel_sql.client.gui.db.encryption;

import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.preferences.ProxyPreferenceTabComponent;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.JsonMarshalUtil;
import net.sourceforge.squirrel_sql.fw.util.ProxySettings;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import org.apache.commons.lang3.StringUtils;

public class ProxyStatusBarPanel extends JPanel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ProxyStatusBarPanel.class);

   private final JLabel _lblProxy;
   private final Timer _proxyChangeIndicatingTimer;
   private Color _originalLabelBackground;
   private String _changeCheckJson;

   public ProxyStatusBarPanel()
   {
      super(new GridLayout());

      _lblProxy = new JLabel(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.PROXY_12x12), JLabel.LEADING);
      _lblProxy.setOpaque(true);

      _originalLabelBackground = _lblProxy.getBackground();

      _proxyChangeIndicatingTimer = new Timer(1000, e -> _lblProxy.setBackground(_originalLabelBackground));

      _proxyChangeIndicatingTimer.setRepeats(false);

      GUIUtils.setPreferredWidth(_lblProxy, 70);
      GUIUtils.setMinimumWidth(_lblProxy, 70);

      add(_lblProxy);

      Main.getApplication().getNonDefaultProxySwitcher().setListener(ps -> updatePanel(ps));

   }

   public void updatePanel(ProxySettings currentProxySettings)
   {
      if(false == isChanged(currentProxySettings))
      {
         return;
      }

      _lblProxy.setText(getProxySettingsName(currentProxySettings));
      _lblProxy.setToolTipText(getLabelToolTip(currentProxySettings));
      _lblProxy.setBackground(Color.yellow);
      _proxyChangeIndicatingTimer.restart();
   }

   private boolean isChanged(ProxySettings currentProxySettings)
   {
      String buf = JsonMarshalUtil.toJsonString(currentProxySettings);

      if(StringUtils.isBlank(_changeCheckJson))
      {
         _changeCheckJson = buf;
         return true;
      }

      if(false == StringUtils.equals(_changeCheckJson, buf))
      {
         _changeCheckJson = buf;
         return true;
      }

      return false;
   }

   private String getLabelToolTip(ProxySettings currentProxySettings)
   {
      String unused = "";
      if( false == currentProxySettings.getSocksUseProxy() && false == currentProxySettings.getHttpUseProxy() )
      {
         unused = ", unused";
      }

      return s_stringMgr.getString("ProxyStatusBarPanel.current.proxy", getProxySettingsName(currentProxySettings) + unused);
   }

   private String getProxySettingsName(ProxySettings currentProxySettings)
   {
      return StringUtils.isBlank(currentProxySettings.getSettingName()) ? ProxyPreferenceTabComponent.DEFAULT_PROXY_SETTINGS_NAME : currentProxySettings.getSettingName();
   }
}
