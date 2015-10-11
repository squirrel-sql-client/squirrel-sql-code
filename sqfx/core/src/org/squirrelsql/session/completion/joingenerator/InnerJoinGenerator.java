package org.squirrelsql.session.completion.joingenerator;

import org.squirrelsql.services.*;
import org.squirrelsql.session.Session;
import org.squirrelsql.session.TableInfo;
import org.squirrelsql.session.completion.CaretVicinity;
import org.squirrelsql.session.completion.CompletionCandidate;
import org.squirrelsql.session.completion.CompletionUtil;
import org.squirrelsql.session.schemainfo.FullyQualifiedTableName;
import org.squirrelsql.table.TableLoader;
import org.squirrelsql.table.TableLoaderFactory;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.*;

public class InnerJoinGenerator
{
   public static final java.lang.String FUNCTION_NAME = JoinGeneratorProvider.FUNCTION_START + "i";

   private I18n _i18n = new I18n(getClass());
   private MessageHandler _mh = new MessageHandler(getClass(), MessageHandlerDestination.MESSAGE_PANEL);
   private CaretVicinity _caretVicinity;
   private Session _session;

   public InnerJoinGenerator(CaretVicinity caretVicinity, Session session)
   {
      _caretVicinity = caretVicinity;
      _session = session;
   }

   private List<String> getReplacements()
   {
      try
      {
         String lineTillCaret = _caretVicinity.getLineTillCaret();
         int fctBegin = lineTillCaret.indexOf(FUNCTION_NAME);

         if (-1 == fctBegin)
         {
            return Collections.EMPTY_LIST;
         }

         String tableList = lineTillCaret.substring(fctBegin + FUNCTION_NAME.length());

         List<String> filteredSplits = CollectionUtil.filter(tableList.split(","), s -> false == Utils.isEmptyString(s));

         filteredSplits = CollectionUtil.transform(filteredSplits, s -> s.trim());

         if(2 > filteredSplits.size())
         {
            _mh.warning(_i18n.t("codecompletion.joingenerator.needsTwoArgs"));
            return Collections.EMPTY_LIST;
         }

         if(false == lineTillCaret.trim().endsWith(","))
         {
            _mh.warning(_i18n.t("codecompletion.joingenerator.mustEndWith"));
            return Collections.EMPTY_LIST;
         }

         List<String> ret = null;

         for (int i = 1; i < filteredSplits.size(); i++)
         {
            String table1String = filteredSplits.get(i - 1);
            String table2String = filteredSplits.get(i);

            List<String> joinClauses = getJoinClauses(table1String, table2String);

            if(null == ret)
            {
               ret = joinClauses;
            }
            else
            {
               ArrayList<String> buf = new ArrayList<>();

               for (String clause : ret)
               {
                  for (String joinClause : joinClauses)
                  {
                     // This is concatenating join clauses for table pairs.
                     // More than one array element can only occur when two tables
                     // have more than one FK-constraint.
                     buf.add(clause + "\n" + joinClause);
                  }
               }

               ret = buf;
            }
         }


         return ret;
      }
      catch (SQLException e)
      {
         throw new RuntimeException(e);
      }
   }

   private List<String> getJoinClauses(String table1String, String table2String) throws SQLException
   {
      TableFromStringResult res1 = getTableFromString(table1String);
      TableInfo table1 = res1.getTableInfo();
      if(null == table1)
      {
         return Collections.EMPTY_LIST;
      }
      if(res1.isUseQualified())
      {
         table1String = res1.getCatalogSchema() + "." + table1String;
      }


      TableFromStringResult res2 = getTableFromString(table2String);
      TableInfo table2 = res2.getTableInfo();
      if(null == table2)
      {
         return Collections.EMPTY_LIST;
      }
      if(res2.isUseQualified())
      {
         table2String = res2.getCatalogSchema() + "." + table2String;
      }



      ArrayList<String> ret = new ArrayList<>();

      DatabaseMetaData metaData = _session.getDbConnectorResult().getSQLConnection().getConnection().getMetaData();

      TableLoader exportedKeys = TableLoaderFactory.loadDataFromResultSet(metaData.getExportedKeys(table1.getCatalog(), table1.getSchema(), table1.getName()));
      TableLoader importedKeys = TableLoaderFactory.loadDataFromResultSet(metaData.getImportedKeys(table1.getCatalog(), table1.getSchema(), table1.getName()));

      HashSet<String> fkNames = new HashSet<>();

      for (int i = 0; i < exportedKeys.getRows().size(); i++)
      {
         if
         (
               Utils.compareRespectEmpty(exportedKeys.getCellValuePlain("FKTABLE_CAT", i), table2.getCatalog()) &&
               Utils.compareRespectEmpty(exportedKeys.getCellValuePlain("FKTABLE_SCHEM", i), table2.getSchema()) &&
               Utils.compareRespectEmpty(exportedKeys.getCellValuePlain("FKTABLE_NAME", i), table2.getName())
         )
         {
            fkNames.add(exportedKeys.getCellAsString("FK_NAME", i));
         }
      }


      for (String fkName : fkNames)
      {
         String joinColumnString = "";

         for (int i = 0; i < exportedKeys.getRows().size(); i++)
         {
            if
            (
               Utils.compareRespectEmpty(exportedKeys.getCellAsString("FK_NAME", i), fkName)
            )
            {
               if(0 == joinColumnString.length())
               {
                  joinColumnString += "INNER JOIN " + table2String + " ON ";
               }
               else
               {
                  joinColumnString += " AND ";
               }

               joinColumnString +=
                     table1String + "." + exportedKeys.getCellAsString("PKCOLUMN_NAME", i) + " = " + table2String + "." + exportedKeys.getCellAsString("FKCOLUMN_NAME", i);
            }
         }

         ret.add(joinColumnString);
      }



      fkNames.clear();


      for (int i = 0; i < importedKeys.getRows().size(); i++)
      {
         if
         (
               Utils.compareRespectEmpty(importedKeys.getCellValuePlain("PKTABLE_CAT", i), table2.getCatalog()) &&
               Utils.compareRespectEmpty(importedKeys.getCellValuePlain("PKTABLE_SCHEM", i), table2.getSchema()) &&
               Utils.compareRespectEmpty(importedKeys.getCellValuePlain("PKTABLE_NAME", i), table2.getName())
         )
         {
            fkNames.add(importedKeys.getCellAsString("FK_NAME", i));
         }
      }


      for (String constraintName : fkNames)
      {
         String joinColumnString = "";

         for (int i = 0; i < importedKeys.getRows().size(); i++)
         {
            if
            (
               Utils.compareRespectEmpty(importedKeys.getCellAsString("FK_NAME", i), constraintName)
            )
            {
               if(0 == joinColumnString.length())
               {
                  joinColumnString += "INNER JOIN " + table2String + " ON ";
               }
               else
               {
                  joinColumnString += " AND ";
               }

               joinColumnString +=
                     table1String + "." + importedKeys.getCellAsString("FKCOLUMN_NAME", i) + " = " + table2String + "." + importedKeys.getCellAsString("PKCOLUMN_NAME", i);
            }
         }

         ret.add(joinColumnString);
      }

      if(0 == ret.size())
      {
         ret.add("INNER JOIN " + table2String + " ON " + table1String + ". = " + table2String + ".");
      }

      return ret;
   }

   private TableFromStringResult getTableFromString(String tableString)
   {
      FullyQualifiedTableName fqTableName = CompletionUtil.getFullyQualifiedTableName(tableString);

      if(null == fqTableName)
      {
         _mh.warning(_i18n.t("codecompletion.function.not.a.valid.table", tableString));
      }



      List<TableInfo> tables = _session.getSchemaCacheValue().get().getTablesByFullyQualifiedName(fqTableName);

      if (tables.isEmpty())
      {
         if(null == fqTableName.getCatalog() && null != fqTableName.getSchema())
         {
            tables = _session.getSchemaCacheValue().get().getTablesBySchemaQualifiedName(fqTableName.getSchema().toString(), fqTableName.getName().toString());
         }
         else
         {
            tables = _session.getSchemaCacheValue().get().getTablesBySimpleName(fqTableName.getName().toString());
         }
      }

      if(1 == tables.size())
      {
         return new TableFromStringResult(tables);
      }


      if(1 < tables.size())
      {
         _mh.warning(_i18n.t("codecompletion.function.table.not.unique", tableString));
         return new TableFromStringResult(tables);
      }

      if(0 == tables.size())
      {
         _mh.warning(_i18n.t("codecompletion.function.unknown.table", tableString));
      }


      return null;
   }

   public List<CompletionCandidate> getCompletionCandidates()
   {
      List<String> replacements = getReplacements();

      if (0 < replacements.size())
      {
         return CollectionUtil.transform(replacements, r -> new InnerJoinCompletionCandidate(r));
      }


      if(_caretVicinity.uncompletedSplitMatches(FUNCTION_NAME))
      {
         return Arrays.asList(new InnerJoinCompletionCandidate());
      }

      return new ArrayList<>();
   }
}
