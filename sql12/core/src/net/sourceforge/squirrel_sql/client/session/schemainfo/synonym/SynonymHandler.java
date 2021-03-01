package net.sourceforge.squirrel_sql.client.session.schemainfo.synonym;

import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.TableQualifier;

import java.util.ArrayList;

/**
 * Inspired by bug #1461 handling of Synonyms was concentrated in this class's package
 * and made this class its single access point.
 */
public class SynonymHandler
{
   public static final String SYNONYM_TABLE_TYPE_NAME = "SYNONYM";
   public static final String ALT_SYNONYM_TABLE_TYPE_NAME = "ALIAS";

   private ISQLDatabaseMetaData _sqlDatabaseMetaData;

   private boolean _isInit;

   private boolean _isOracle;
   private boolean _isDB2;

   private boolean _isNetezza;
   private NetezzaSpecifics _netezzaSpecifics = null;

   public SynonymHandler(ISQLDatabaseMetaData sqlDatabaseMetaData)
   {
      _sqlDatabaseMetaData = sqlDatabaseMetaData;
   }

   /**
    * Because it is created during creation of SQLDatabaseMetaData.
    */
   private void initLazy()
   {
      if (_isInit)
      {
         return;
      }

      _isInit = true;
      _isNetezza = DialectFactory.isNetezza(_sqlDatabaseMetaData);
      _isOracle = DialectFactory.isOracle(_sqlDatabaseMetaData);
      _isDB2 = DialectFactory.isDB2(_sqlDatabaseMetaData);
   }

   /**
    * Function: resolve synonym to the referenced table, so we can get columns for that table.
    */
   public TableQualifier getQualifiedSynonymName(String catalog, String schema, String table)
   {
      initLazy();

      if (false == _isNetezza)
      {
         return null;
      }

      if (_netezzaSpecifics == null)
      {
         _netezzaSpecifics = new NetezzaSpecifics(_sqlDatabaseMetaData);
      }

      NetezzaSynonym synonym = _netezzaSpecifics.findSynonym(catalog, schema, table);
      if (synonym != null)
      {
         return new TableQualifier(synonym.getCatalog(), synonym.getSchema(), synonym.getTable());
      }

      return null;
   }



   public void appendSynonymTableTypesForSchemaInfoCache(ArrayList<String> tableTypeCandidates)
   {
      initLazy();

      if (_isOracle || _isNetezza)
      {
         tableTypeCandidates.add(SYNONYM_TABLE_TYPE_NAME);
      }

      if (_isDB2)
      {
         tableTypeCandidates.add(ALT_SYNONYM_TABLE_TYPE_NAME);
      }
   }

   public boolean ignoreAsCodeCompletionTableInfo(String tableType)
   {
      initLazy();

      if(false == _isOracle)
      {
         return false;
      }

      return SYNONYM_TABLE_TYPE_NAME.equals(tableType);
   }
}
