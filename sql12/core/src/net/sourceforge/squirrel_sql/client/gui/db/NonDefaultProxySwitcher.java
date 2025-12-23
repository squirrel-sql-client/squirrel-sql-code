package net.sourceforge.squirrel_sql.client.gui.db;

import java.util.Optional;
import java.util.stream.Stream;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.util.ProxyHandler;
import net.sourceforge.squirrel_sql.fw.util.ProxySettings;
import org.apache.commons.lang3.StringUtils;

public class NonDefaultProxySwitcher
{
   private ProxySettings _currentProxySettings;
   private NonDefaultProxySwitcherListener _listener;

   public boolean hasValidNonDefaultProxySettings(SQLAlias sqlAlias)
   {
      return false == StringUtils.isBlank(sqlAlias.getNonDefaultProxySettingsName())
             && getNonDefaultProxySetting(sqlAlias).isPresent();
   }

   private Optional<ProxySettings> getNonDefaultProxySetting(SQLAlias sqlAlias)
   {
      ProxySettings[] additionalNamedProxySettings = Main.getApplication().getSquirrelPreferences().getAdditionalNamedProxySettings();
      return Stream.of(additionalNamedProxySettings).filter(ps -> StringUtils.equals(sqlAlias.getNonDefaultProxySettingsName(), ps.getSettingName())).findFirst();
   }

   public void maybeApplyNonDefaultProxySettings(SQLAlias sqlAlias)
   {
      if(false == hasValidNonDefaultProxySettings(sqlAlias))
      {
         _currentProxySettings = Main.getApplication().getSquirrelPreferences().getProxySettings();
      }
      else
      {
         _currentProxySettings = getNonDefaultProxySetting(sqlAlias).orElseThrow();
      }
      applyCurrentProxy();
   }

   public void updateDefaultProxyWhenItsDue()
   {
      if(null == _currentProxySettings)
      {
         _currentProxySettings = Main.getApplication().getSquirrelPreferences().getProxySettings();
      }

      if(isDefaultSetting(_currentProxySettings) && isDefaultSetting(Main.getApplication().getSquirrelPreferences().getProxySettings()))
      {
         // When the default Proxy-Setting was updated in global preferences a new instance was created.
         _currentProxySettings = Main.getApplication().getSquirrelPreferences().getProxySettings();
         applyCurrentProxy();
      }
   }

   private boolean isDefaultSetting(ProxySettings proxySettings)
   {
      return null == proxySettings.getSettingName();
   }

   private void applyCurrentProxy()
   {
      ProxyHandler.apply(_currentProxySettings);

      if(null != _listener)
      {
         _listener.proxyChanged(_currentProxySettings);
      }
   }

   public void setListener(NonDefaultProxySwitcherListener listener)
   {
      _listener = listener;
      _listener.proxyChanged(_currentProxySettings);
   }

   public void allSessionsClosed()
   {
      _currentProxySettings = Main.getApplication().getSquirrelPreferences().getProxySettings();
      applyCurrentProxy();
   }
}
