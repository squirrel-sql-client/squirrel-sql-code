package net.sourceforge.squirrel_sql.plugins.dataimport.gui;

import net.sourceforge.squirrel_sql.client.session.DefaultSQLExecuterHandler;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLExecuterTask;
import net.sourceforge.squirrel_sql.client.session.schemainfo.DatabaseUpdateInfos;
import net.sourceforge.squirrel_sql.client.util.codereformat.CodeReformator;
import net.sourceforge.squirrel_sql.client.util.codereformat.CodeReformatorConfigFactory;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.plugins.dataimport.EDTMessageBoxUtil;
import net.sourceforge.squirrel_sql.plugins.dataimport.ImportFileUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

public class TableCreateUtils
{
   static CreateTableInDatabaseResult execCreateTableInDatabase(ISession session, String createSql)
   {

      boolean[] errorOccurredRef = new boolean[1];
      DefaultSQLExecuterHandler sqlExecuterHandler = new DefaultSQLExecuterHandler(session)
      {
         @Override
         public String sqlExecutionException(Throwable th, String postErrorString)
         {
            errorOccurredRef[0] = true;
            return super.sqlExecutionException(th, postErrorString);
         }
      };


      SQLExecuterTask sqlExecuterTask = new SQLExecuterTask(session, createSql, sqlExecuterHandler);

      DatabaseUpdateInfos[] databaseUpdateInfosRef = new DatabaseUpdateInfos[1];
      sqlExecuterTask.runDirect(upd -> databaseUpdateInfosRef[0] = upd);

      ITableInfo createdTable = null;

      if (0 < databaseUpdateInfosRef[0].getUpdateDatabaseObjectInfos().size())
      {
         createdTable = (ITableInfo) databaseUpdateInfosRef[0].getUpdateDatabaseObjectInfos().iterator().next();

         createdTable = session.getSchemaInfo().getITableInfos(createdTable.getCatalogName(), createdTable.getSchemaName(), createdTable.getSimpleName())[0];
         return new CreateTableInDatabaseResult(createdTable);
      }
      else if(errorOccurredRef[0])
      {
         return new CreateTableInDatabaseResult().setErrorOccured(true);
      }
      else
      {
         return new CreateTableInDatabaseResult().setTableNotFound(true);
      }


   }

   public static String suggestTableName(File importFile)
   {

      String fileName = ImportFileUtils.getFileNameWithoutEnding(importFile);

      String normalizedFileName = StringUtilities.javaNormalize(fileName);

      String tableNamePattern = ImportPropsDAO.getTableNamePattern();

      String tableName = tableNamePattern.replaceAll("@file", normalizedFileName);
      Date now = new Date();

      tableName = tableName.replaceAll("@date", new SimpleDateFormat("YYYY_MM_DD").format(now));
      tableName = tableName.replaceAll("@time", new SimpleDateFormat("hh_mm_ss").format(now));

      return tableName;
   }

   public static String suggestCreateScript(String tableName, ISession session, String[][] previewData, boolean headerIncluded)
   {
      int varCharLength = ImportPropsDAO.getVarCharLength();
      int numericPrecision = ImportPropsDAO.getNumericPrecision();
      int numericScale = ImportPropsDAO.getNumericScale();

      boolean suggestColumnTypes = ImportPropsDAO.isSuggestColumnTypes();

      ArrayList<ColumnGuess> columnGuesses = new ArrayList<>();

      HashSet<String> duplicateColumnNameCheck = new HashSet<>();

      for (int i = 0; i < previewData.length; i++)
      {

         for (int j = 0; j < previewData[i].length; j++)
         {
            if(columnGuesses.size() <= j)
            {
               if(headerIncluded)
               {
                  columnGuesses.add(new ColumnGuess(StringUtilities.javaNormalize(previewData[i][j]), varCharLength, numericPrecision, numericScale, suggestColumnTypes, duplicateColumnNameCheck));
               }
               else
               {
                  columnGuesses.add(new ColumnGuess("Column" + j, varCharLength, numericPrecision, numericScale, suggestColumnTypes, duplicateColumnNameCheck));
               }
            }
            else
            {
               columnGuesses.get(j).exampleValue(previewData[i][j]);
            }
         }
      }

      String createScript = "CREATE TABLE " + tableName + "\n(\n";


      for (int i = 0; i < columnGuesses.size(); i++)
      {
         if(i > 0)
         {
            createScript += ",\n";
         }

         createScript +=  columnGuesses.get(i).getColumnSQL();
      }

      createScript += "\n)";


      CodeReformator cr = new CodeReformator(CodeReformatorConfigFactory.createConfig(session));


      return cr.reformat(createScript);
   }
}
