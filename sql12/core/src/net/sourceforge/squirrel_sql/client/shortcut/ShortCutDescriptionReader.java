package net.sourceforge.squirrel_sql.client.shortcut;

import java.util.MissingResourceException;
import javax.swing.Action;
import javax.swing.JMenuItem;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.resources.IResources;
import net.sourceforge.squirrel_sql.fw.resources.ResourceBundleHandler;
import net.sourceforge.squirrel_sql.fw.resources.Resources;
import org.apache.commons.lang3.StringUtils;

public record ShortCutDescriptionReader(Action action,
                                        Class<? extends Action> actionClass,
                                        JMenuItem mnuItem,
                                        Resources resources,
                                        ResourceBundleHandler bundleHandler,
                                        String fullResourceKey,
                                        String callerProvidedText)
{
   public static ShortCutDescriptionReader of(Action action)
   {
      return new ShortCutDescriptionReader(action, null, null, null, null, null, null);
   }

   public static ShortCutDescriptionReader of(Action action, JMenuItem mnuItem)
   {
      return new ShortCutDescriptionReader(action, null, mnuItem, null, null, null, null);
   }

   public static ShortCutDescriptionReader of(JMenuItem menuItem)
   {
      return new ShortCutDescriptionReader(null, null, menuItem, null, null, null, null);
   }

   public static ShortCutDescriptionReader of(Resources resources, Class<? extends Action> actionClass)
   {
      return new ShortCutDescriptionReader(null, actionClass, null, resources, null, null, null);
   }

   public static ShortCutDescriptionReader of(Action action, String fullResourceKey, ResourceBundleHandler bundleHandler)
   {
      return new ShortCutDescriptionReader(action, null, null, null, bundleHandler, fullResourceKey, null);
   }

   public static ShortCutDescriptionReader of()
   {
      return new ShortCutDescriptionReader(null, null, null, null, null, null, null);
   }

   public static ShortCutDescriptionReader of(String callerProvidedText)
   {
      return new ShortCutDescriptionReader(null, null, null, null, null, null, callerProvidedText);
   }

   public String getDescription()
   {
      String description = callerProvidedText;

      Class<? extends Action> actCls = actionClass;

      if(null == actCls && null != action)
      {
         actCls = action.getClass();
      }


      if(StringUtils.isBlank(description) && null != resources && null != actCls)
      {
         description = resources.getTooltipFromResource(actCls);
      }

      if(StringUtils.isBlank(description))
      {
         Action act = action;

         if(null == act && null != actionClass)
         {
            act = Main.getApplication().getActionCollection().get(actionClass);
         }

         if(null != act)
         {
            description = (null == act.getValue(Action.SHORT_DESCRIPTION) ? null : "" + act.getValue(Action.SHORT_DESCRIPTION));

            if(StringUtils.isBlank(description))
            {
               description = (null == act.getValue(Action.SHORT_DESCRIPTION) ? null : "" + act.getValue(Action.LONG_DESCRIPTION));
            }
         }
      }

      if(StringUtils.isBlank(description) && !StringUtils.isBlank(fullResourceKey))
      {
         String resKey = fullResourceKey;

         if(fullResourceKey.startsWith(IResources.Keys.MENU_ITEM))
         {
            resKey = StringUtils.replace(fullResourceKey, IResources.Keys.MENU_ITEM, IResources.Keys.ACTION, 1);
         }

         if(null != resources)
         {
            try
            {
               description = resources.getTooltipFromResource(resKey);
            }
            catch(MissingResourceException e)
            {
            }
         }

         if(StringUtils.isBlank(description) && null != bundleHandler)
         {
            try
            {
               description = bundleHandler.getResourceString(resKey, IResources.ActionProperties.TOOLTIP);
            }
            catch(MissingResourceException e)
            {
            }
         }
      }

      if(StringUtils.isBlank(description))
      {
         description = null;
      }

      return description;
   }

}
