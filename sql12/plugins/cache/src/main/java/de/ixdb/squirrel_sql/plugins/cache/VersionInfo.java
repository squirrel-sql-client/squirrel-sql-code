package de.ixdb.squirrel_sql.plugins.cache;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.sql.SQLException;

public class VersionInfo
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(VersionInfo.class);


   static boolean is5(ISession session)
   {
      try
      {
         return 5 == session.getMetaData().getDatabaseMajorVersion();
      }
      catch (SQLException e)
      {
         throw new RuntimeException(e);
      }
   }

   public static void showNotSupported(ISession session)
   {
      JOptionPane.showMessageDialog(session.getApplication().getMainFrame(), s_stringMgr.getString("cache.VersionInfo.unsupported"));

   }
}
