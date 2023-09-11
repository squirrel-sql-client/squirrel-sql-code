package net.sourceforge.squirrel_sql.fw.resources;

import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import javax.swing.*;

public class ResourceUtil
{
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
}
