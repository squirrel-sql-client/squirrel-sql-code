package net.sourceforge.squirrel_sql.plugins.codecompletion.completionfunctions;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.schemainfo.ObjFilterMatcher;
import net.sourceforge.squirrel_sql.fw.sql.ForeignKeyInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.codecompletion.CodeCompletionInfo;


public abstract class AbstractJoin extends CodeCompletionFunction
{

	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(AbstractJoin.class);

   private ISession _session;

   public AbstractJoin(ISession session)
   {
      _session = session;
   }


   public CodeCompletionInfo[] getFunctionResults(String functionSting)
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
         Vector<String> tables = new Vector<String>();
         HashMap<String, String> schemas = new HashMap<String, String>();
         while(st.hasMoreTokens())
         {
            String[] catSchemTab = st.nextToken().trim().split("\\.");
            String schema = null;
            String table = catSchemTab[catSchemTab.length -1];
            if(2 <= catSchemTab.length)
            {
               schema = catSchemTab[catSchemTab.length -2];
               schemas.put(schema, schema);
            }

            table = _session.getSchemaInfo().getCaseSensitiveTableName(table);
            if(null == table)
            {
					// i18n[codecompletion.unknowntable=unknown table {0}]
					_session.showMessage(s_stringMgr.getString("codecompletion.unknowntable", table));
               return null;
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
                   schemas.put(schemBuf, schemBuf);
               }
            }
         }

         Vector<CodeCompletionInfo> ret = new Vector<CodeCompletionInfo>();

         for (Iterator<String> i =schemas.keySet().iterator() ; i.hasNext();)
         {
            CodeCompletionInfo[] buf = getResultsForSchema(tables, jdbcMetaData, catalog, i.next());
            ret.addAll(Arrays.asList(buf));
         }

         return ret.toArray(new CodeCompletionInfo[ret.size()]);
      }
      catch (SQLException e)
      {
         throw new RuntimeException(e);
      }
   }

   private CodeCompletionInfo[] getResultsForSchema(Vector<String> tables, SQLDatabaseMetaData jdbcMetaData, String catalog, String schema)
      throws SQLException
   {
      Vector<String> completions = new Vector<String>();
      completions.add("");

      for (int i = 1; i < tables.size(); i++)
      {
         Hashtable<String, Vector<String>> conditionByFkName = 
             new Hashtable<String, Vector<String>>();
         Hashtable<String, Vector<ColBuffer>> colBuffersByFkName = 
             new Hashtable<String, Vector<ColBuffer>>();
         ForeignKeyInfo[] infos = jdbcMetaData.getImportedKeysInfo(catalog, schema, tables.get(i-1));
         fillConditionByFkName(infos, tables.get(i-1), tables.get(i), conditionByFkName, colBuffersByFkName);

         infos = jdbcMetaData.getExportedKeysInfo(catalog, schema, tables.get(i-1));
         fillConditionByFkName(infos, tables.get(i-1), tables.get(i), conditionByFkName, colBuffersByFkName);

         Vector<String> twoTableCompletions = new Vector<String>();
         for(Enumeration<String> e=conditionByFkName.keys(); e.hasMoreElements();)
         {
            String fkName = e.nextElement();

            String joinClause = getJoinClause(fkName, tables.get(i-1), tables.get(i), colBuffersByFkName);

            StringBuffer sb = new StringBuffer();
            sb.append(joinClause).append(tables.get(i)).append(" ON ");

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

         if(0 == conditionByFkName.size())
         {
            String joinClause = getJoinClause(null, tables.get(i-1), tables.get(i), colBuffersByFkName);
            twoTableCompletions.add(joinClause + tables.get(i) + " ON " + tables.get(i-1) + ". = " + tables.get(i) + ".\n");
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

   protected abstract String getJoinClause(String fkName, 
                                           String table1, 
                                           String table2, 
                                           Hashtable<String, 
                                           Vector<ColBuffer>> colBuffersByFkName);

   private void fillConditionByFkName(ForeignKeyInfo[] infos, 
                                      String table1, 
                                      String table2, 
                                      Hashtable<String, 
                                      Vector<String>> conditionByFkName, 
                                      Hashtable<String, Vector<ColBuffer>> colBuffersByFkName)
      throws SQLException
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
               conditions = new Vector<String>();
               conditionByFkName.put(fkName, conditions);
            }

            StringBuilder tmp = new StringBuilder(pkTableName);
            tmp.append(".");
            tmp.append(pkColumnName);
            tmp.append(" = ");
            tmp.append(fkTableName);
            tmp.append(".");
            tmp.append(fkColumnName);
            
            conditions.add(tmp.toString());

            Vector<ColBuffer> cols = colBuffersByFkName.get(fkName);
            if(null == cols)
            {
               cols = new Vector<ColBuffer>();
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
}
