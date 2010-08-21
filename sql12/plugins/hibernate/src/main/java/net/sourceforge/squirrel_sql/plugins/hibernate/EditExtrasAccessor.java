package net.sourceforge.squirrel_sql.plugins.hibernate;

import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;

public class EditExtrasAccessor
{

   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(EditExtrasAccessor.class);

   public static void quoteHQL(ISQLEntryPanel entryPanel, ISession session)
   {
      EditExtrasExternalService si = getService(session);

      if(null != si)
      {
         si.quoteSQL(entryPanel);
      }
   }


   public static void quoteHQLSb(ISQLEntryPanel entryPanel, ISession session)
   {
      EditExtrasExternalService si = getService(session);

      if(null != si)
      {
         si.quoteSQLSb(entryPanel);
      }
   }

   private static EditExtrasExternalService getService(ISession session)
   {
      EditExtrasExternalService si = (EditExtrasExternalService) session.getApplication().getPluginManager().bindExternalPluginService("editextras", EditExtrasExternalService.class);
      if (null == si)
      {
         // i18n[EditExtrasAccessor.editextrasPluginNeeded=Quoting is only available with the Edit Extras Plugin.\nGet the plugin from www.squirrelsql.org. It's free.]
         String msg = s_stringMgr.getString("EditExtrasAccessor.editextrasPluginNeeded");
         JOptionPane.showMessageDialog(session.getApplication().getMainFrame(), msg);
         return null;
      }
      return si;
   }

   public static void unquoteHQL(ISQLEntryPanel entryPanel, ISession session)
   {
      EditExtrasExternalService si = getService(session);
      if(null != si)
      {
         si.unquoteSQL(entryPanel);
      }
   }
}
