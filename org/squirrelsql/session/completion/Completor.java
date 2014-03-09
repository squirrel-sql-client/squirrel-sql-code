package org.squirrelsql.session.completion;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.squirrelsql.session.ColumnInfo;
import org.squirrelsql.session.ProcedureInfo;
import org.squirrelsql.session.TableInfo;
import org.squirrelsql.session.schemainfo.SchemaCache;
import org.squirrelsql.session.schemainfo.StructItemCatalog;
import org.squirrelsql.session.schemainfo.StructItemSchema;

import java.util.ArrayList;

public class Completor
{
   private SchemaCache _schemaCache;
   private TableCompletionCandidate _lastSeenTable;

   public Completor(SchemaCache schemaCache, TableCompletionCandidate lastSeenTable)
   {
      _schemaCache = schemaCache;
      _lastSeenTable = lastSeenTable;
   }

   public ObservableList<CompletionCandidate> getCompletions(TokenParser tokenParser)
   {
      ArrayList<CompletionCandidate> ret = new ArrayList<>();


      if(0 == tokenParser.completedSplitsCount()) // everything
      {

         if(null != _lastSeenTable)
         {

            for (ColumnInfo columnInfo : _schemaCache.getColumns(_lastSeenTable.getTableInfo()))
            {
               if(tokenParser.uncompletedSplitMatches(columnInfo.getColName()))
               {
                  ret.add(new ColumnCompletionCandidate(columnInfo, _lastSeenTable));
               }
            }
         }

         for (String keyword : _schemaCache.getDefaultKeywords())
         {
            if(tokenParser.uncompletedSplitMatches(keyword))
            {
               ret.add(new KeywordCompletionCandidate(keyword));
            }
         }

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
         ArrayList<TableInfo> tables;

         tables = _schemaCache.getTablesByFullyQualifiedName(schema.getCatalog(), schema.getSchema(), tableName);

         fillMatchingCols(ret, tokenParser, tables, schema);

         if(tables.size() > 0)
         {
            return;
         }

         tables = _schemaCache.getTablesBySchemaQualifiedName(schema.getSchema(), tableName);

         fillMatchingCols(ret, tokenParser, tables, schema);

         if(tables.size() > 0)
         {
            return;
         }

         tables = _schemaCache.getTablesBySimpleName(tableName);

         fillMatchingCols(ret, tokenParser, tables, schema);
      }
   }

   private void fillMatchingCols(ArrayList<CompletionCandidate> ret, TokenParser tokenParser, ArrayList<TableInfo> tables, StructItemSchema schema)
   {
      for (TableInfo table : tables)
      {

         for (ColumnInfo columnInfo : _schemaCache.getColumns(table))
         {
            if(tokenParser.uncompletedSplitMatches(columnInfo.getColName()))
            {
               ret.add(new ColumnCompletionCandidate(columnInfo, new TableCompletionCandidate(table, schema)));
            }
         }
      }
   }

   private void fillTopLevelObjectsForSchemas(ArrayList<CompletionCandidate> ret, TokenParser tokenParser, ArrayList<StructItemSchema> schemas)
   {
      for (StructItemSchema schema : schemas)
      {
         ArrayList<TableInfo> tableInfos = _schemaCache.getTableInfosMatching(schema.getCatalog(), schema.getSchema(), TableTypes.getTableAndView());

         DuplicateSimpleNamesCheck duplicateSimpleNamesCheck = new DuplicateSimpleNamesCheck();

         for (TableInfo tableInfo : tableInfos)
         {
            if(tokenParser.uncompletedSplitMatches(tableInfo.getName()))
            {
               /////////////////////////////////////////////////////////////////////
               // For now we check duplicates for tables only.
               TableCompletionCandidate tableCompletionCandidate = new TableCompletionCandidate(tableInfo, schema);
               duplicateSimpleNamesCheck.check(tableCompletionCandidate);
               //
               //////////////////////////////////////////////////////////////////////

               ret.add(tableCompletionCandidate);
            }
         }

         ArrayList<ProcedureInfo> procedureInfos = _schemaCache.getProcedureInfosMatching(schema.getCatalog(), schema.getSchema());

         for (ProcedureInfo procedureInfo : procedureInfos)
         {
            if(tokenParser.uncompletedSplitMatches(procedureInfo.getName()))
            {
               ret.add(new ProcedureCompletionCandidate(procedureInfo, schema));
            }
         }

//         Looks bad for Postgres should be made configurable
//         ArrayList<UDTInfo> udtInfos = _schemaCache.getUDTInfosMatching(schema.getCatalog(), schema.getSchema());
//
//         for (UDTInfo udtInfo : udtInfos)
//         {
//            if(tokenParser.uncompletedSplitMatches(udtInfo.getName()))
//            {
//               ret.add(new UDTCompletionCandidate(udtInfo, schema));
//            }
//         }
      }
   }
}
