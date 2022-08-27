package net.sourceforge.squirrel_sql.client.session.menuattic;

import javax.swing.JMenuItem;

public class MenuAtticUtil
{
   public static String getMenuText(JMenuItem menuItem)
   {
      String ret = menuItem.getText();

      if(null != menuItem.getAccelerator())
      {
         ret += " (" + menuItem.getAccelerator() + ")";
      }

      return ret;
   }
}
