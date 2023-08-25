package net.sourceforge.squirrel_sql.client.gui.session.catalogspanel;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.schemainfo.SchemaInfo;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import java.util.ArrayList;
import java.util.List;

public class AliasCatalogLoadModel
{
   private static final ILogger s_log = LoggerController.createLogger(AliasCatalogLoadModel.class);

   private AliasCatalogLoadModelJsonBean _modelJson;

   /**
    * Usually null unless the object was created by {@link #of(String)}.
    * Represents the way {@link SchemaInfo} was loaded before the {@link CatalogsPanel} allowed to choose
    * additional catalogs to load.
    */
   private String _catalogRepresentingDefaultLoad;

   public AliasCatalogLoadModel(AliasCatalogLoadModelJsonBean modelJson, ISession session)
   {
      _modelJson = modelJson;

      _catalogRepresentingDefaultLoad = null;
      if(DialectFactory.isMSSQLServer(session.getMetaData()))
      {
         // This code is a workaround for bug #1508 (Sourceforge):
         // After switching catalogs using the catalogs combobox (see CatalogsComboListener.actionPerformed())
         // the MSSQL-Server JDBC driver (https://github.com/microsoft/mssql-jdbc)
         // throws a "SQLServerException: The prepared statement handle 1 is not valid in this context ..."
         // when SchemaInfo is reloaded.
         // The problem doesn't occur, when the reloading is done with the current catalog specified.
         //
         // Note: Without switching catalog the MSSQL-Server JDBC driver loads SchemaInfo fine with catalog = null, too.
         // It then loads the current catalog's objects. So setting the current catalog here makes no difference except from
         // preventing the JDBC driver's bug.
         try
         {
            _catalogRepresentingDefaultLoad = session.getSQLConnection().getCatalog();
         }
         catch (Exception e)
         {
            s_log.error("Error getting the current catalog from MSSQL-Server to workaround Sourceforge bug #1508.", e);
         }
      }
   }

   /**
    * Usually the first element of this List is null, see {@link #_catalogRepresentingDefaultLoad}
    *
    */
   public List<String> getCatalogStringsToLoad()
   {
      ArrayList<String> ret = new ArrayList<>();
      ret.add(_catalogRepresentingDefaultLoad);
      ret.addAll(_modelJson.getAdditionalUserChosenCatalogs());

      return ret;
   }

   /**
    * Just to represent a simple caller given catalog name.
    * Maybe if one day complexity grows this class will become an interface with two implementations.
    */
   public static AliasCatalogLoadModel of(String catalogName)
   {
      AliasCatalogLoadModel ret = new AliasCatalogLoadModel(new AliasCatalogLoadModelJsonBean(), null);
      ret._catalogRepresentingDefaultLoad = catalogName;
      return ret;
   }
}
