package net.sourceforge.squirrel_sql.client.gui.session.catalogspanel;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class CatalogsPanelUtil
{
   private static final ILogger s_log = LoggerController.createLogger(CatalogsPanelUtil.class);

   public static String[] getCatalogsIfSupportedElseNull(ISession session)
   {
      try
      {
         if(false == session.getSQLConnection().getSQLMetaData().supportsCatalogs())
         {
            return null;
         }

         final String[] catalogs = session.getSQLConnection().getSQLMetaData().getCatalogs();
         if(null == catalogs || 0 == catalogs.length)
         {
            return null;
         }
         return catalogs;
      }
      catch(Exception e)
      {
         s_log.error("Failed to access Catalogs", e);
         return null;
      }
   }
}
