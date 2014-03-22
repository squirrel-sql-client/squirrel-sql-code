package org.squirrelsql.session.action;

import org.squirrelsql.Props;
import org.squirrelsql.services.I18n;

import java.util.ArrayList;

public enum StandardActionConfigurations
{
   RUN_SQL(new ActionConfiguration(Help.props.getImageView("run.png"), Help.i18n.t("sql.run"), ActionScope.SQL_EDITOR, true));


   private static class Help
   {
      private static final I18n i18n = new I18n(StandardActionConfigurations.class);
      private static final Props props = new Props(StandardActionConfigurations.class);
   }

   private ActionConfiguration _actionConfiguration;

   StandardActionConfigurations(ActionConfiguration actionConfiguration)
   {
      _actionConfiguration = actionConfiguration;
   }

   public ActionConfiguration getActionConfiguration()
   {
      return _actionConfiguration;
   }

   static ArrayList<ActionConfiguration> getToolbarConfigs()
   {
      ArrayList<ActionConfiguration> ret = new ArrayList<>();

      for (StandardActionConfigurations stdEnum : values())
      {
         if(stdEnum._actionConfiguration.isOnToolbar())
         {
            ret.add(stdEnum._actionConfiguration);
         }
      }

      return ret;
   }

}
