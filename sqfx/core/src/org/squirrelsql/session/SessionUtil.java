package org.squirrelsql.session;

import org.squirrelsql.aliases.Alias;
import org.squirrelsql.services.I18n;

public class SessionUtil
{

   public static String getSessionTabTitle(SessionTabContext tabContext)
   {
      Alias alias = tabContext.getSession().getAlias();
      return new I18n(SessionUtil.class).t("session.tab.header", alias.getName(), alias.getUserName());
   }
}
