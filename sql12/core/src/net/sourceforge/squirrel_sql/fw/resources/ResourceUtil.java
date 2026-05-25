package net.sourceforge.squirrel_sql.fw.resources;

import javax.swing.Action;
import javax.swing.JMenuItem;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import org.apache.commons.lang3.StringUtils;

public class ResourceUtil
{

   private static ILogger s_log = LoggerController.createLogger(ResourceUtil.class);


   public static void trySetToolTip(JMenuItem mnuAdded, Action action)
   {
      if(   null != action.getValue(Action.SHORT_DESCRIPTION)
         && false == StringUtilities.isEmpty("" + action.getValue(Action.SHORT_DESCRIPTION), true))
      {
         mnuAdded.setToolTipText("" + action.getValue(Action.SHORT_DESCRIPTION));
      }
      else if(   null != action.getValue(Action.LONG_DESCRIPTION)
              && false == StringUtilities.isEmpty("" + action.getValue(Action.LONG_DESCRIPTION), true))
      {
         mnuAdded.setToolTipText("" + action.getValue(Action.LONG_DESCRIPTION));
      }
   }

   public static void storeToolTipInAction(Action action, String toolTip)
   {
      action.putValue(Action.SHORT_DESCRIPTION, toolTip);
   }

   public static String getActionName(Class actionClass)
   {
         Exception exp = null;
         String ret = null;

         try
         {
            ret = Main.getApplication().getResources().getActionName(actionClass);
            if(false == StringUtils.isBlank(ret))
            {
               return ret;
            }
         }
         catch(Exception e)
         {
            exp = e;
         }

         try
         {
            ret = Main.getApplication().getResourcesFw().getActionName(actionClass);
            if(false == StringUtils.isBlank(ret))
            {
               return ret;
            }
         }
         catch(Exception e)
         {
            if(null != exp)
            {
               exp = e;
            }
         }

         if(null != exp)
         {
            s_log.warn(exp);
         }

      s_log.warn("Class %s should override ISQLPanelAction.getActionName() to supply a decent action name".formatted(actionClass));
      return actionClass.getSimpleName();
   }
}
