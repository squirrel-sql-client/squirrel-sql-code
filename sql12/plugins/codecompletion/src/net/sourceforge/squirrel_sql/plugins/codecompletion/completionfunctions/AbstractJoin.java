package net.sourceforge.squirrel_sql.plugins.codecompletion.completionfunctions;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.plugins.codecompletion.CodeCompletionInfo;
import net.sourceforge.squirrel_sql.fw.completion.CompletionInfo;

import java.util.*;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.DatabaseMetaData;


public abstract class AbstractJoin extends CodeCompletionFunction
{
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
            _session.getMessageHandler().showMessage("function needs at least two arguments");
            return null;
         }

         if(false == functionSting.trim().endsWith(","))
         {
            _session.getMessageHandler().showMessage("function must end with ','");
            return null;
         }

         st.nextToken(); // remove the function name

         String catalog = _session.getSQLConnection().getCatalog();
         DatabaseMetaData jdbcMetaData = _session.getSQLConnection().getSQLMetaData().getJDBCMetaData();
         Vector tables = new Vector();
         HashMap schemas = new HashMap();
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
               _session.getMessageHandler().showMessage("unknown table " + table);
               return null;
            }
            tables.add(table);

            if(null == schema)
            {
               if(null == catalog)
               {
                  catalog = _session.getSQLConnection().getCatalog();
               }
               ResultSet resSchema = jdbcMetaData.getTables(catalog, null, (String) tables.get(0), new String[]{"TABLE"});
               while(resSchema.next())
               {
                  String schemBuf = resSchema.getString("TABLE_SCHEM");
                  schemas.put(schemBuf, schemBuf);
               }
               resSchema.close();
            }
         }

         Vector ret = new Vector();

         for (Iterator i =schemas.keySet().iterator() ; i.hasNext();)
         {
            CodeCompletionInfo[] buf = getResultsForSchema(tables, jdbcMetaData, catalog, (String)i.next());
            ret.addAll(Arrays.asList(buf));
         }

         return (CodeCompletionInfo[]) ret.toArray(new CodeCompletionInfo[ret.size()]);
      }
      catch (SQLException e)
      {
         throw new RuntimeException(e);
      }
   }

   private CodeCompletionInfo[] getResultsForSchema(Vector tables, DatabaseMetaData jdbcMetaData, String catalog, String schema)
      throws SQLException
   {
      Vector completions = new Vector();
      completions.add("");

      for (int i = 1; i < tables.size(); i++)
      {
         Hashtable conditionByFkName = new Hashtable();
         Hashtable colBuffersByFkName = new Hashtable();
         ResultSet res;
         res = jdbcMetaData.getImportedKeys(catalog, schema, (String) tables.get(i-1));
         fillConditionByFkName(res, (String)tables.get(i-1), (String)tables.get(i), conditionByFkName, colBuffersByFkName);
         res.close();

         res = jdbcMetaData.getExportedKeys(catalog, schema, (String) tables.get(i-1));
         fillConditionByFkName(res, (String)tables.get(i-1), (String)tables.get(i), conditionByFkName, colBuffersByFkName);
         res.close();


         Vector twoTableCompletions = new Vector();
         for(Enumeration e=conditionByFkName.keys(); e.hasMoreElements();)
         {
            String fkName = (String) e.nextElement();

            String joinClause = getJoinClause(fkName, (String)tables.get(i-1), (String)tables.get(i), colBuffersByFkName);

            StringBuffer sb = new StringBuffer();
            sb.append(joinClause).append(tables.get(i)).append(" ON ");

            Vector conditions = (Vector) conditionByFkName.get(fkName);
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
            String joinClause = getJoinClause(null, (String)tables.get(i-1), (String)tables.get(i), colBuffersByFkName);
            twoTableCompletions.add(joinClause + tables.get(i) + " ON " + tables.get(i-1) + ". = " + tables.get(i) + ".\n");
         }


         Vector newCompletions = new Vector();

         for (int j = 0; j < completions.size(); j++)
         {
            String begin = (String) completions.get(j);
            for (int k = 0; k < twoTableCompletions.size(); k++)
            {
               String end = (String) twoTableCompletions.get(k);
               newCompletions.add(begin + end);
            }
         }
         completions = newCompletions;
      }

      GenericCodeCompletionInfo[] ret = new GenericCodeCompletionInfo[completions.size()];

      for (int i = 0; i < completions.size(); i++)
      {
         ret[i] = new GenericCodeCompletionInfo((String) completions.get(i));
      }

      return ret;
   }

   protected abstract String getJoinClause(String fkName, String table1, String table2, Hashtable colBuffersByFkName);

   private void fillConditionByFkName(ResultSet res, String table1, String table2, Hashtable conditionByFkName, Hashtable colBuffersByFkName)
      throws SQLException
   {
      while(res.next())
      {
         if
         (
              (res.getString("PKTABLE_NAME").equalsIgnoreCase((String) table2)  && res.getString("FKTABLE_NAME").equalsIgnoreCase((String) table1))
           || (res.getString("FKTABLE_NAME").equalsIgnoreCase((String) table2)  && res.getString("PKTABLE_NAME").equalsIgnoreCase((String) table1))
         )
         {
            String fkName = "" + res.getString("FK_NAME");

            Vector conditions = (Vector) conditionByFkName.get(fkName);
            if(null == conditions)
            {
               conditions = new Vector();
               conditionByFkName.put(fkName, conditions);
            }

            conditions.add(res.getString("PKTABLE_NAME") + "." + res.getString("PKCOLUMN_NAME") + " = " +
                           res.getString("FKTABLE_NAME") + "." + res.getString("FKCOLUMN_NAME"));


            Vector cols = (Vector) colBuffersByFkName.get(fkName);
            if(null == cols)
            {
               cols = new Vector();
               colBuffersByFkName.put(fkName, cols);
            }
            cols.add(new ColBuffer(res.getString("FKTABLE_NAME"), res.getString("FKCOLUMN_NAME")));
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
