package net.sourceforge.squirrel_sql.client.gui.db;

import net.sourceforge.squirrel_sql.fw.util.ProxySettings;

@FunctionalInterface
public interface NonDefaultProxySwitcherListener
{
   void proxyChanged(ProxySettings proxySettings);
}
