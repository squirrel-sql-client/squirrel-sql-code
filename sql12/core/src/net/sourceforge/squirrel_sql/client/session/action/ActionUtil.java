package net.sourceforge.squirrel_sql.client.session.action;

import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.gui.session.IToolsPopupDescription;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import javax.swing.Action;

public class ActionUtil
{
   public static String getAcceleratorString(SquirrelResources rsrc, Action action)
   {
      String acceleratorString = rsrc.getAcceleratorString(action);

      if (StringUtilities.isEmpty(acceleratorString, true))
      {
         return "";
      }
      else
      {
         return " (" + acceleratorString + ")";
      }
   }

   /**
    * For an alternative implementation see
    * {@link net.sourceforge.squirrel_sql.fw.gui.ToolBar#initialiseButton(javax.swing.Action, javax.swing.AbstractButton)}
    */
   public static boolean actionDescriptionContainsAccelerator(Action action)
   {
      // SquirrelAction descriptions already contain the accelerator
      return action instanceof SquirrelAction && false == action instanceof IToolsPopupDescription;
   }

}
