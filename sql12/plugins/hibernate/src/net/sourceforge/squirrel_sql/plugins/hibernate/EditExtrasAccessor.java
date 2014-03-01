package net.sourceforge.squirrel_sql.plugins.hibernate;

import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;

public class EditExtrasAccessor
{

   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(EditExtrasAccessor.class);

   public static void quoteHQL(ISQLEntryPanel entryPanel, ISession session)
   {
      // i18n[EditExtrasAccessor.editextrasPluginNeeded=Quoting is only available with the Edit Extras Plugin.\nGet the plugin from www.squirrelsql.org. It's free.]
      EditExtrasExternalService si = getService(session, s_stringMgr.getString("EditExtrasAccessor.editextrasPluginNeeded"));

      if(null != si)
      {
         si.quoteSQL(entryPanel);
      }
   }


   public static void quoteHQLSb(ISQLEntryPanel entryPanel, ISession session)
   {
      EditExtrasExternalService si = getService(session, s_stringMgr.getString("EditExtrasAccessor.editextrasPluginNeeded"));

      if(null != si)
      {
         si.quoteSQLSb(entryPanel);
      }
   }

   private static EditExtrasExternalService getService(ISession session, String msg)
   {
      EditExtrasExternalService si = (EditExtrasExternalService) session.getApplication().getPluginManager().bindExternalPluginService("editextras", EditExtrasExternalService.class);
      if (null == si)
      {
         JOptionPane.showMessageDialog(session.getApplication().getMainFrame(), msg);
         return null;
      }
      return si;
   }

   public static void unquoteHQL(ISQLEntryPanel entryPanel, ISession session)
   {
      EditExtrasExternalService si = getService(session, s_stringMgr.getString("EditExtrasAccessor.editextrasPluginNeeded"));
      if(null != si)
      {
         si.unquoteSQL(entryPanel);
      }
   }

   public static String escapeDate(ISQLEntryPanel entryPanel, ISession session)
   {
      // i18n[EditExtrasAccessor.editextrasPluginNeededForEscapeDate=Escape date is only available with the Edit Extras Plugin.\nGet the plugin from www.squirrelsql.org. It's free.]
      EditExtrasExternalService si = getService(session, s_stringMgr.getString("EditExtrasAccessor.editextrasPluginNeededForEscapeDate"));
      if(null != si)
      {
         return si.getDateEscape(GUIUtils.getOwningFrame(entryPanel.getTextComponent()));
      }
      return null;
   }
}
