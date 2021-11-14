package net.sourceforge.squirrel_sql.client.preferences.themes;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JOptionPane;

public class SyntaxPluginAccessor
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(LAFPluginAccessor.class);

   private static SyntaxExternalService getService()
   {
      SyntaxExternalService syntaxExternalService =
            (SyntaxExternalService) Main.getApplication().getPluginManager().bindExternalPluginService("syntax", SyntaxExternalService.class);

      if (null == syntaxExternalService)
      {
         String msg = s_stringMgr.getString("SyntaxPluginAccessor.syntaxPluginNeeded");
         JOptionPane.showMessageDialog(Main.getApplication().getMainFrame(), msg);
         return null;
      }
      return syntaxExternalService;
   }

   public static void applyDarkTheme()
   {
      getService().applyDarkTheme();
   }

   public static void applyDefaultTheme()
   {
      getService().applyDefaultTheme();
   }
}
