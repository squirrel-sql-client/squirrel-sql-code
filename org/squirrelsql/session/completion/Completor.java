package org.squirrelsql.session.completion;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.squirrelsql.session.ColumnInfo;
import org.squirrelsql.session.ProcedureInfo;
import org.squirrelsql.session.TableInfo;
import org.squirrelsql.session.UDTInfo;
import org.squirrelsql.session.schemainfo.SchemaCache;
import org.squirrelsql.session.schemainfo.StructItemCatalog;
import org.squirrelsql.session.schemainfo.StructItemSchema;

import java.util.ArrayList;

public class Completor
{
   private SchemaCache _schemaCache;

   public Completor(SchemaCache schemaCache)
   {
      _schemaCache = schemaCache;
   }

   public ObservableList<CompletionCandidate> getCompletions(String tokenAtCarret)
   {
      ArrayList<CompletionCandidate> ret = new ArrayList<>();

      TokenParser tokenParser = new TokenParser(tokenAtCarret);


      if(0 == tokenParser.completedSplitsCount()) // everything
      {

         // Last loaded columns here

         for (String keyword : _schemaCache.getKeywords().getCellsAsString(0))
         {
            if(tokenParser.uncompletedSplitMatches(keyword))
            {
               ret.add(new KeywordCompletionCandidate(keyword));
            }
         }

         ArrayList<StructItemCatalog> catalogs = _schemaCache.getCatalogs();

         for (StructItemCatalog catalog : catalogs)
         {
            if(tokenParser.uncompletedSplitMatches(catalog.getCatalog()))
            {
               ret.add(new CatalogCompletionCandidate(catalog));
            }
         }

         ArrayList<StructItemSchema> schemas = _schemaCache.getSchemas();

         for (StructItemSchema schema : schemas)
         {
            if(tokenParser.uncompletedSplitMatches(schema.getSchema()))
            {
               ret.add(new SchemaCompletionCandidate(schema));
            }
         }

         ArrayList<String> functions = _schemaCache.getAllFunctions();

         for (String function : functions)
         {
            if(tokenParser.uncompletedSplitMatches(function))
            {
               ret.add(new FunctionCompletionCandidate(function));
            }
         }

         // ???????
//         TableLoader<DataBaseType> types = _schemaCache.getTypes();
//
//         for (String function : functions)
//         {
//            if(tokenParser.uncompletedSplitMatches(function))
//            {
//               ret.add(new DataBaseTypeCompletionCandidate(function));
//            }
//         }

         fillTopLevelObjectsForSchemas(ret, tokenParser, createFakeSchemaArrayForCatalog(null));

      }
      else if(1 == tokenParser.completedSplitsCount()) // MyCatalog.xxx or MySchema.xxx or MyTable.xxx
      {
         StructItemCatalog catalog = _schemaCache.getCatalogByName(tokenParser.getCompletedSplitAt(0));

         if(null != catalog) // MyCatalog.xxx
         {
            fillTopLevelObjectsForSchemas(ret, tokenParser, createFakeSchemaArrayForCatalog(catalog));
         }

         ///////////////////////////////////////////
         // MySchema.xxx
         ArrayList<StructItemSchema> schemas = _schemaCache.getSchemasByName(tokenParser.getCompletedSplitAt(0));

         fillTopLevelObjectsForSchemas(ret, tokenParser, schemas);
         //
         ////////////////////////////////////////////

         ////////////////////////////////////////////////
         // MyTable.xxx
         fillColumnsForTable(ret, createFakeSchemaArrayForCatalog(null), tokenParser.getCompletedSplitAt(0), tokenParser);
         //
         //////////////////////////////////////////////////


      }
      else if(2 == tokenParser.completedSplitsCount()) // MyCatalog.MySchema,xxx or MyCatalog.MyTable.xxx or MySchema.MyTable.xxx
      {
         StructItemCatalog catalog = _schemaCache.getCatalogByName(tokenParser.getCompletedSplitAt(0));


         if (null != catalog) // MyCatalog.MySchema,xxx or MyCatalog.MyTable.xxx
         {
            ArrayList<StructItemSchema> schemas = _schemaCache.getSchemaByNameAsArray(catalog.getCatalog(), tokenParser.getCompletedSplitAt(1));

            fillTopLevelObjectsForSchemas(ret, tokenParser, schemas);

            if(0 == schemas.size())
            {
               fillColumnsForTable(ret, createFakeSchemaArrayForCatalog(catalog), tokenParser.getCompletedSplitAt(1), tokenParser);
            }
         }
         else // MySchema.MyTable.xxx
         {
            ArrayList<StructItemSchema> schemas = _schemaCache.getSchemasByName(tokenParser.getCompletedSplitAt(0));
            fillColumnsForTable(ret, schemas, tokenParser.getCompletedSplitAt(1), tokenParser);
         }
      }
      else if(3 == tokenParser.completedSplitsCount()) // MyCatalog.MySchema,MyTable.xxx
      {
         StructItemCatalog catalog = _schemaCache.getCatalogByName(tokenParser.getCompletedSplitAt(0));

         if(null != catalog)
         {
            ArrayList<StructItemSchema> schemas = _schemaCache.getSchemaByNameAsArray(catalog.getCatalog(), tokenParser.getCompletedSplitAt(1));

            fillColumnsForTable(ret, schemas, tokenParser.getCompletedSplitAt(2), tokenParser);
         }
      }

      return FXCollections.observableArrayList(ret);
   }

   private ArrayList<StructItemSchema> createFakeSchemaArrayForCatalog(StructItemCatalog catalog)
   {
      ArrayList<StructItemSchema> fakeSchemaArray = new ArrayList<>();

      StructItemSchema fakeSchema;

      if(null == catalog)
      {
         fakeSchema = new StructItemSchema(null, null);
      }
      else
      {
         fakeSchema = new StructItemSchema(null, catalog.getCatalog());
      }

      fakeSchemaArray.add(fakeSchema);
      return fakeSchemaArray;
   }

   private void fillColumnsForTable(ArrayList<CompletionCandidate> ret, ArrayList<StructItemSchema> schemas, String tableName, TokenParser tokenParser)
   {
      for (StructItemSchema schema : schemas)
      {
         ArrayList<TableInfo> tables = _schemaCache.getTablesByName(schema.getCatalog(), schema.getSchema(), tableName);

//         for (TableInfo table : tables)
//         {
//            ArrayList<ColumnInfo> columnInfo = table.getColumns();
//
//            if(tokenParser.uncompletedSplitMatches(columnInfo.getName()))
//            {
//               ret.add(new ColumnCompletionCandidate(columnInfo));
//            }
//         }

      }
   }

   private void fillTopLevelObjectsForSchemas(ArrayList<CompletionCandidate> ret, TokenParser tokenParser, ArrayList<StructItemSchema> schemas)
   {
      for (StructItemSchema schema : schemas)
      {
         ArrayList<TableInfo> tableInfos = _schemaCache.getTableInfos(schema.getCatalog(), schema.getSchema(), null);

         for (TableInfo tableInfo : tableInfos)
         {
            if(tokenParser.uncompletedSplitMatches(tableInfo.getName()))
            {
               ret.add(new TableCompletionCandidate(tableInfo, schema));
            }
         }

         ArrayList<ProcedureInfo> procedureInfos = _schemaCache.getProcedureInfos(schema.getCatalog(), schema.getSchema());

         for (ProcedureInfo procedureInfo : procedureInfos)
         {
            if(tokenParser.uncompletedSplitMatches(procedureInfo.getName()))
            {
               ret.add(new ProcedureCompletionCandidate(procedureInfo, schema));
            }
         }

         ArrayList<UDTInfo> udtInfos = _schemaCache.getUDTInfos(schema.getCatalog(), schema.getSchema());

         for (UDTInfo udtInfo : udtInfos)
         {
            if(tokenParser.uncompletedSplitMatches(udtInfo.getName()))
            {
               ret.add(new UDTCompletionCandidate(udtInfo, schema));
            }
         }
      }
   }
}
