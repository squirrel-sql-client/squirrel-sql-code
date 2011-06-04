package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;

public class EditExtrasAccessor
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(EditExtrasAccessor.class);

   public static String getDateEscape(Window parentForDialog, ISession session)
   {
      EditExtrasExternalService si = getService(session, parentForDialog);

      return si.getDateEscape(parentForDialog);
   }


   private static EditExtrasExternalService getService(ISession session, Window parentForDialog)
   {
      EditExtrasExternalService si = (EditExtrasExternalService) session.getApplication().getPluginManager().bindExternalPluginService("editextras", EditExtrasExternalService.class);
      if (null == si)
      {
         // i18n[EditExtrasAccessor.editextrasPluginNeeded=Escape date s only available with the Edit Extras Plugin. Get the plugin from www.squirrelsql.org. It's free.]
         String msg = s_stringMgr.getString("EditExtrasAccessor.editextrasPluginNeeded");
         JOptionPane.showMessageDialog(parentForDialog, msg);
         return null;
      }
      return si;
   }
}
