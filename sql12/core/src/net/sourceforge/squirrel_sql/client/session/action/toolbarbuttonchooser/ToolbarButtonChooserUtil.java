package net.sourceforge.squirrel_sql.client.session.action.toolbarbuttonchooser;

import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import javax.swing.Action;

public class ToolbarButtonChooserUtil
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
}
