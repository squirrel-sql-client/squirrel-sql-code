package org.squirrelsql.session;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import org.squirrelsql.aliases.Alias;
import org.squirrelsql.services.I18n;

public class SessionUtil
{
   public static Node createSessionTabHeader(SessionTabContext tabContext)
   {
      return createSessionTabHeader(tabContext, null);
   }

   public static Node createSessionTabHeader(SessionTabContext tabContext, ImageView icon)
   {
      String title = getSessionTabTitle(tabContext);
      Label ret = new Label(title);

      if(null != icon)
      {
         ret.setGraphic(icon);
      }

      return ret;
   }

   public static String getSessionTabTitle(SessionTabContext tabContext)
   {
      Alias alias = tabContext.getSession().getAlias();
      return new I18n(SessionUtil.class).t("session.tab.header", alias.getName(), alias.getUserName());
   }
}
