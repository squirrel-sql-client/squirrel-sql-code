package net.sourceforge.squirrel_sql.client.preferences.themes;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JOptionPane;

public class LAFPluginAccessor
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(LAFPluginAccessor.class);

   private static LAFExternalService getService()
   {
      LAFExternalService si = (LAFExternalService) Main.getApplication().getPluginManager().bindExternalPluginService("laf", LAFExternalService.class);
      if (null == si)
      {
         String msg = s_stringMgr.getString("LAFPluginAccessor.lafPluginNeeded");
         JOptionPane.showMessageDialog(Main.getApplication().getMainFrame(), msg);
         return null;
      }
      return si;
   }


   public static void applyMetalOcean()
   {
      getService().applyMetalOcean();
   }

   public static void applyFlatLafDark()
   {
      getService().applyFlatLafDark();
   }
}
