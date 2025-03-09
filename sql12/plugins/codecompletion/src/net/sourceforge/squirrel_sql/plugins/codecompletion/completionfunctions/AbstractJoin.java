package net.sourceforge.squirrel_sql.plugins.codecompletion.completionfunctions;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.parser.kernel.TableAliasParseInfo;
import net.sourceforge.squirrel_sql.client.session.parser.kernel.TableAndAliasParseResult;
import net.sourceforge.squirrel_sql.client.session.schemainfo.ObjFilterMatcher;
import net.sourceforge.squirrel_sql.fw.sql.ForeignKeyInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.databasemetadata.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.codecompletion.CodeCompletionInfo;
import org.apache.commons.lang3.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;


public abstract class AbstractJoin extends CodeCompletionFunction
{

	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(AbstractJoin.class);

   private ISession _session;

   private TableAndAliasParseResult _tableAndAliasParseResult = new TableAndAliasParseResult();

   public AbstractJoin(ISession session)
   {
      _session = session;
   }


   public CodeCompletionInfo[] getFunctionResults(String functionSting, int caretPos)
   {
      try
      {
         if(false == functionMatches(functionSting))
         {
            return null;
         }

         StringTokenizer st = new StringTokenizer(functionSting, ",");

         if(3 > st.countTokens())
         {
				// i18n[codecompletion.function.needsTwoArgs=function needs at least two arguments]
				_session.showMessage(s_stringMgr.getString("codecompletion.function.needsTwoArgs"));
            return null;
         }

         if(false == functionSting.trim().endsWith(","))
         {
				// i18n[codecompletion.function.mustEndWith=function must end with ',']
				_session.showMessage(s_stringMgr.getString("codecompletion.function.mustEndWith"));
            return null;
         }

         st.nextToken(); // remove the function name

         String catalog = _session.getSQLConnection().getCatalog();
         SQLDatabaseMetaData jdbcMetaData = _session.getSQLConnection().getSQLMetaData();
         Vector<String> tables = new Vector<>();
         HashSet<String> schemas = new HashSet<>();
         HashMap<String, TableAliasParseInfo> table_tableAliasParseInfo = new HashMap<>();
         while(st.hasMoreTokens())
         {
            String[] catSchemTab = st.nextToken().trim().split("\\.");
            String schema = null;
            String table = catSchemTab[catSchemTab.length -1];
            if(2 <= catSchemTab.length)
            {
               schema = catSchemTab[catSchemTab.length -2];
               schemas.add(schema);
            }

            table = _session.getSchemaInfo().getCaseSensitiveTableName(table);
            if(null == table)
            {
               if(1 == catSchemTab.length) // A table alias cannot be schema scoped
               {
                  TableAliasParseInfo tableAliasParseInfo =
                        _tableAndAliasParseResult.getAliasInStatementAt(catSchemTab[0], caretPos);

                  if(null == tableAliasParseInfo)
                  {
                     _session.showMessage(s_stringMgr.getString("codecompletion.unknowntable", table));
                     return null;
                  }

                  table = tableAliasParseInfo.getTableQualifier().getTableName();
                  schema = tableAliasParseInfo.getTableQualifier().getSchema();
                  if(false == StringUtils.isEmpty(schema))
                  {
                     schemas.add(schema);
                  }
                  table_tableAliasParseInfo.put(table, tableAliasParseInfo);

               }
            }
            tables.add(table);

            if(null == schema)
            {
               if(null == catalog)
               {
                  catalog = _session.getSQLConnection().getCatalog();
               }
               ITableInfo[] infos = _session.getSchemaInfo().getITableInfos(catalog, null, new ObjFilterMatcher(tables.get(0)), new String[]{"TABLE"});

               if(0 == infos.length)
               {
                  // Needed for example on PostgreSQL
                  infos = _session.getSchemaInfo().getITableInfos(null, null, new ObjFilterMatcher(tables.get(0)), new String[]{"TABLE"});
               }

               for (int i = 0; i < infos.length; i++)
               {
                   String schemBuf = infos[i].getSchemaName();
                   schemas.add(schemBuf);
               }
            }
         }

         ArrayList<CodeCompletionInfo> ret = new ArrayList<>();

         for(String schema : schemas)
         {
            CodeCompletionInfo[] buf = getResultsForSchema(tables, jdbcMetaData, catalog, schema, table_tableAliasParseInfo);
            ret.addAll(Arrays.asList(buf));
         }

         return ret.toArray(new CodeCompletionInfo[0]);
      }
      catch (SQLException e)
      {
         throw new RuntimeException(e);
      }
   }

   private CodeCompletionInfo[] getResultsForSchema(Vector<String> tables,
                                                    SQLDatabaseMetaData jdbcMetaData,
                                                    String catalog,
                                                    String schema,
                                                    HashMap<String, TableAliasParseInfo> table_tableAliasParseInfo)
      throws SQLException
   {
      Vector<String> completions = new Vector<>();
      completions.add("");

      for (int i = 1; i < tables.size(); i++)
      {
         Hashtable<String, Vector<String>> conditionByFkName = new Hashtable<>();
         Hashtable<String, Vector<ColBuffer>> colBuffersByFkName = new Hashtable<>();
         ForeignKeyInfo[] infos = jdbcMetaData.getImportedKeysInfo(catalog, schema, tables.get(i-1));
         fillConditionByFkName(infos, tables.get(i-1), tables.get(i), conditionByFkName, colBuffersByFkName, table_tableAliasParseInfo);

         infos = jdbcMetaData.getExportedKeysInfo(catalog, schema, tables.get(i-1));
         fillConditionByFkName(infos, tables.get(i-1), tables.get(i), conditionByFkName, colBuffersByFkName, table_tableAliasParseInfo);

         Vector<String> twoTableCompletions = new Vector<>();
         for(Enumeration<String> e=conditionByFkName.keys(); e.hasMoreElements();)
         {
            String fkName = e.nextElement();

            String joinClause = getJoinClause(fkName, tables.get(i-1), tables.get(i), colBuffersByFkName);

            StringBuilder sb = new StringBuilder();
            sb.append(joinClause).append(getTableOrAliasName(tables.get(i), table_tableAliasParseInfo)).append(" ON ");

            Vector<String> conditions = conditionByFkName.get(fkName);
            if(1 == conditions.size())
            {
               sb.append(conditions.get(0));
            }
            else if(1 < conditions.size())
            {
               sb.append("(");
               sb.append(conditions.get(0));
               for (int j = 1; j < conditions.size(); j++)
               {
                  sb.append(" AND ").append(conditions.get(j));
               }
               sb.append(")");
            }
            sb.append("\n");

            twoTableCompletions.add(sb.toString());
         }

         if(conditionByFkName.isEmpty())
         {
            String joinClause = getJoinClause(null, tables.get(i-1), tables.get(i), colBuffersByFkName);

            twoTableCompletions.add(joinClause + getTableOrAliasName(tables.get(i), table_tableAliasParseInfo)
                                               + " ON " + getTableOrAliasName(tables.get(i - 1), table_tableAliasParseInfo)
                                               + ". = " + getTableOrAliasName(tables.get(i), table_tableAliasParseInfo) + ".\n");
         }


         Vector<String> newCompletions = new Vector<String>();

         for (int j = 0; j < completions.size(); j++)
         {
            String begin = completions.get(j);
            for (int k = 0; k < twoTableCompletions.size(); k++)
            {
               String end = twoTableCompletions.get(k);
               newCompletions.add(begin + end);
            }
         }
         completions = newCompletions;
      }

      GenericCodeCompletionInfo[] ret = new GenericCodeCompletionInfo[completions.size()];

      for (int i = 0; i < completions.size(); i++)
      {
         ret[i] = new GenericCodeCompletionInfo(completions.get(i));
      }

      return ret;
   }

   private static String getTableOrAliasName(String tableName, HashMap<String, TableAliasParseInfo> table_tableAliasParseInfo)
   {
      TableAliasParseInfo tableAliasParseInfo = table_tableAliasParseInfo.get(tableName);
      if(null == tableAliasParseInfo)
      {
         return tableName;
      }

      return tableAliasParseInfo.getAliasName();
   }

   protected abstract String getJoinClause(String fkName, 
                                           String table1, 
                                           String table2, 
                                           Hashtable<String,Vector<ColBuffer>> colBuffersByFkName);

   private void fillConditionByFkName(ForeignKeyInfo[] infos,
                                      String table1,
                                      String table2,
                                      Hashtable<String,Vector<String>> conditionByFkName,
                                      Hashtable<String,Vector<ColBuffer>> colBuffersByFkName,
                                      HashMap<String, TableAliasParseInfo> table_tableAliasParseInfo)
   {
      for (int i = 0; i < infos.length; i++)
      {
          String pkTableName = infos[i].getPrimaryKeyTableName();
          String pkColumnName = infos[i].getPrimaryKeyColumnName();
          String fkTableName = infos[i].getForeignKeyTableName();
          String fkColumnName = infos[i].getForeignKeyColumnName();
          String fkName = infos[i].getForeignKeyName();
         if
         (
              (pkTableName.equalsIgnoreCase(table2)  && fkTableName.equalsIgnoreCase(table1))
                  || (fkTableName.equalsIgnoreCase(table2)  && pkTableName.equalsIgnoreCase(table1))
         )
         {

            Vector<String> conditions = conditionByFkName.get(fkName);
            if(null == conditions)
            {
               conditions = new Vector<>();
               conditionByFkName.put(fkName, conditions);
            }

            String tmp = getTableOrAliasName(pkTableName, table_tableAliasParseInfo) + "." + pkColumnName +
                  " = " + getTableOrAliasName(fkTableName, table_tableAliasParseInfo) + "." + fkColumnName;

            conditions.add(tmp);

            Vector<ColBuffer> cols = colBuffersByFkName.get(fkName);
            if(null == cols)
            {
               cols = new Vector<>();
               colBuffersByFkName.put(fkName, cols);
            }
            cols.add(new ColBuffer(fkTableName, fkColumnName));
         }
      }
   }

   static class ColBuffer
   {
      String tableName;
      String colName;

      public ColBuffer(String tableName, String colName)
      {
         this.tableName = tableName;
         this.colName = colName;
      }
   }

   public void replaceLastTableAndAliasParseResult(TableAndAliasParseResult tableAndAliasParseResult)
   {
      _tableAndAliasParseResult = tableAndAliasParseResult;
   }
}
