package net.sourceforge.squirrel_sql.plugins.sqlscript.table_script.insert;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.plugins.sqlscript.prefs.SQLScriptPreferencesManager;
import net.sourceforge.squirrel_sql.plugins.sqlscript.table_script.ScriptUtil;
import net.sourceforge.squirrel_sql.plugins.sqlscript.table_script.scriptbuilder.ScriptBuilder;

import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;

public class InsertGenerator
{
   private ISession _session;

   private Boolean _dialectSupportsSubSecondTimestamps;

   public InsertGenerator(ISession session)
   {
      _session = session;
   }

   public void genInserts(ResultSet srcResult, String sTable, ScriptBuilder sbRows, boolean headerOnly, boolean templateScriptOnly, InsertAbortCallBack insertAbortCallBack)
         throws SQLException
   {
      ResultSetMetaData metaData = srcResult.getMetaData();
      final HibernateDialect dialect = DialectFactory.getDialect(_session.getMetaData());

      int iColumnCount = metaData.getColumnCount();
      InsertScriptColumnInfo[] colInfo = new InsertScriptColumnInfo[iColumnCount];


      for (int i = 1; i <= iColumnCount; i++)
      {
         colInfo[i-1] = new InsertScriptColumnInfo(metaData.getColumnName(i), metaData.getColumnType(i));
      }

      // Just a helper to make the fromResultSet ? ... below
      // look nicer.
      boolean fromResultSet = !templateScriptOnly && !headerOnly;

      sbRows.append("\n\n");
      Timestamp currentTime = new Timestamp(System.currentTimeMillis());
      while (srcResult.next() || templateScriptOnly || headerOnly)
      {
         if (insertAbortCallBack.isAborted())
         {
            break;
         }

         sbRows.append("INSERT INTO ");
         sbRows.append(sTable);

         StringBuilder sbColumns = new StringBuilder();
         sbColumns.append(" (");

         StringBuilder sbValues = new StringBuilder();
         sbValues.append(" VALUES (");

         ScriptUtil su = new ScriptUtil();

         for (int i = 0; i < iColumnCount; i++)
         {
            int iIndexPoint = colInfo[i].columnName.lastIndexOf('.');
            sbColumns.append(su.makeColumnNameUnique(colInfo[i].columnName.substring(iIndexPoint + 1)));

            if (Types.TINYINT == colInfo[i].sqlType
               || Types.BIGINT == colInfo[i].sqlType
               || Types.SMALLINT == colInfo[i].sqlType
               || Types.INTEGER == colInfo[i].sqlType
               || Types.FLOAT == colInfo[i].sqlType
               || Types.REAL == colInfo[i].sqlType
               || Types.DOUBLE == colInfo[i].sqlType
               || Types.NUMERIC == colInfo[i].sqlType
               || Types.DECIMAL == colInfo[i].sqlType)
            {
               Object value = fromResultSet ? srcResult.getObject(i + 1) : "0" + getNullableComment(metaData, i+1);
               sbValues.append(value);
            }
            else if (Types.DATE == colInfo[i].sqlType
               || Types.TIME == colInfo[i].sqlType
               || Types.TIMESTAMP == colInfo[i].sqlType)
            {
               Calendar calendar = Calendar.getInstance();
               java.util.Date timestamp = null;
               if (Types.DATE == colInfo[i].sqlType)
               {
                  timestamp = fromResultSet ? srcResult.getDate(i + 1): currentTime;
               }
               else if (Types.TIME == colInfo[i].sqlType)
               {
                  timestamp = fromResultSet ? srcResult.getTime(i + 1): currentTime;
               }
               else if (Types.TIMESTAMP == colInfo[i].sqlType)
               {
                  timestamp = fromResultSet ? srcResult.getTimestamp(i + 1): currentTime;
               }


               if (timestamp == null)
               {
                  sbValues.append("null");
               }
               else
               {
                  calendar.setTime(timestamp);

                  if (Types.DATE == colInfo[i].sqlType)
                  {
                     String esc = "{d '" + prefixNulls(calendar.get(Calendar.YEAR), 4) + "-" +
                        prefixNulls(calendar.get(Calendar.MONTH) + 1, 2) + "-" +
                        prefixNulls(calendar.get(Calendar.DAY_OF_MONTH), 2) + "'}";
                     esc = fromResultSet ? esc : esc + getNullableComment(metaData, i+1);
                     sbValues.append(esc);
                  }
                  else if (Types.TIME == colInfo[i].sqlType)
                  {
                     String esc = "{t '" + prefixNulls(calendar.get(Calendar.HOUR_OF_DAY), 2) + ":" +
                        prefixNulls(calendar.get(Calendar.MINUTE), 2) + ":" +
                        prefixNulls(calendar.get(Calendar.SECOND), 2) + "'}";
                     esc = fromResultSet ? esc : esc + getNullableComment(metaData, i+1);
                     sbValues.append(esc);
                  }
                  else if (Types.TIMESTAMP == colInfo[i].sqlType)
                  {
                  	Timestamp ts = (Timestamp)timestamp;

                     StringBuilder esc = new StringBuilder("{ts '");
                     esc.append(prefixNulls(calendar.get(Calendar.YEAR), 4)).append("-");
                     esc.append(prefixNulls(calendar.get(Calendar.MONTH) + 1, 2)).append("-");
                     esc.append(prefixNulls(calendar.get(Calendar.DAY_OF_MONTH), 2)).append(" ");
                     esc.append(prefixNulls(calendar.get(Calendar.HOUR_OF_DAY), 2)).append(":");
                     esc.append(prefixNulls(calendar.get(Calendar.MINUTE), 2)).append(":");
                     esc.append(prefixNulls(calendar.get(Calendar.SECOND), 2)).append(".");
                     esc.append(getNanos(ts, _session));
                     esc.append("'}");

                     if (!fromResultSet) {
                     	esc.append(getNullableComment(metaData, i+1));
                     }
                     sbValues.append(esc);
                  }

               }
            }
            else if (Types.BIT == colInfo[i].sqlType || Types.BOOLEAN == colInfo[i].sqlType)
            {
               boolean booleanValue = fromResultSet ? srcResult.getBoolean(i + 1) : false;

               if(fromResultSet && srcResult.wasNull())
               {
                  sbValues.append("null");
               }
               else
               {
                  boolean dbSupportsTrueFalse = DialectFactory.isPostgreSQL(_session.getMetaData()) || DialectFactory.isDerby(_session.getMetaData());

                  if (booleanValue)
                  {
                     // PostgreSQL uses literal values true/false instead of 1/0.
                     // Derby, too, see bug 1452
                     if (dbSupportsTrueFalse)
                     {
                        sbValues.append("true");
                     }
                     else
                     {
                        sbValues.append(1);
                     }
                  }
                  else
                  {
                     // PostgreSQL uses literal values true/false instead of 1/0.
                     if (dbSupportsTrueFalse)
                     {
                        sbValues.append("false");
                     }
                     else
                     {
                        sbValues.append(0);
                     }
                  }
               }

               if(false == fromResultSet)
               {
                  sbValues.append(getNullableComment(metaData, i+1));
               }
            }
            else if (Types.BLOB == colInfo[i].sqlType || Types.BINARY == colInfo[i].sqlType)
            {
               if (fromResultSet)
               {
                  if (srcResult.wasNull())
                  {
                     sbValues.append("null");
                  }
                  else
                  {
                     byte[] binaryData = null;
                     if (Types.BLOB == colInfo[i].sqlType)
                     {
                        Blob blobResult = srcResult.getBlob(i + 1);
                        binaryData = blobResult.getBytes(1, (int) blobResult.length());
                     }
                     else
                     {
                        binaryData = srcResult.getBytes(i + 1);
                     }
                     sbValues.append(dialect.getBinaryLiteralString(binaryData));
                  }
               }
               else
               {
                  sbValues.append("'CAFEBABE'").append(getNullableComment(metaData, i + 1));
               }
            }
            else // Types.CHAR,
                 // Types.VARCHAR,
                 // Types.LONGVARCHAR,
                 // Types.VARBINARY
                 // Types.LONGVARBINARY
                 // Types.NULL
                 // Types.JAVA_OBJECT
                 // Types.DISTINCT
                 // Types.ARRAY
                 // Types.CLOB
                 // Types.REF
                 // Types.DATALINK
            {
               String sResult = fromResultSet ? srcResult.getString(i + 1) : "s";
               if (sResult == null)
               {
                  sbValues.append("null");
               }
               else
               {
                  int iIndex = sResult.indexOf("'");
                  if (iIndex != -1)
                  {
                     int iPrev = 0;
                     StringBuffer sb = new StringBuffer();
                     sb.append(sResult.substring(iPrev, iIndex));
                     sb.append('\'');
                     iPrev = iIndex;
                     iIndex = sResult.indexOf("'", iPrev + 1);
                     while (iIndex != -1)
                     {
                        sb.append(sResult.substring(iPrev, iIndex));
                        sb.append('\'');
                        iPrev = iIndex;
                        iIndex = sResult.indexOf("'", iPrev + 1);
                     }
                     sb.append(sResult.substring(iPrev));
                     sResult = sb.toString();
                  }

                  if (SQLScriptPreferencesManager.getPreferences().isEscapeNewLine())
                  {
                     sResult = escapeNewlines(sResult);
                  }

                  sbValues.append("\'");
                  sbValues.append(sResult);
                  sbValues.append("\'");

                  if(false == fromResultSet)
                  {
                     sbValues.append(getNullableComment(metaData, i+1));
                  }
               }
            }
            sbValues.append(",");
            sbColumns.append(",");
         }

         // delete last ','
         sbValues.setLength(sbValues.length() - 1);
         sbColumns.setLength(sbColumns.length() - 1);

         // close it.
         sbColumns.append(")");
         sbValues.append(")").append(getStatementSeparator(_session)).append("\n");

         sbRows.append(sbColumns);
         if(false == headerOnly)
         {
            sbRows.append(sbValues);
         }

         if(templateScriptOnly || headerOnly)
         {
            break;
         }
      }
      srcResult.close();
   }

   private static String escapeNewlines(String sResult)
   {
      String escape = SQLScriptPreferencesManager.getPreferences().getEscapeNewLineString();

      return sResult.replaceAll("\n", escape);

//      int iIndex;
//      iIndex = sResult.indexOf('\n');
//      if (iIndex != -1)
//      {
//         int iPrev = 0;
//         StringBuffer sb = new StringBuffer();
//         sb.append(sResult.substring(iPrev, iIndex));
//         sb.append(escape);
//         iPrev = iIndex + escape.length();
//         iIndex = sResult.indexOf('\n', iPrev-1);
//         while (iIndex != -1)
//         {
//            sb.append(sResult.substring(iPrev-1, iIndex));
//            sb.append(escape);
//            iPrev = iIndex + escape.length();
//            iIndex = sResult.indexOf('\n', iPrev-1);
//         }
//         sb.append(sResult.substring(iPrev-1));
//         sResult = sb.toString();
//      }
//      return sResult;
   }

   /**
	 * Returns the sub-second precision value from the specified timestamp if supported by the session's
	 * dialect.
	 *
	 * @param ts
	 *           the Timestamp to get the nanosecond value from
	 * @param dialectSupportsSubSecondTimestamps1
    * @param session
    * @return a string representing the nanosecond value.
	 */
	private String getNanos(Timestamp ts, ISession session) throws SQLException
	{
		ISQLDatabaseMetaData md = session.getMetaData();
		HibernateDialect dialect = DialectFactory.getDialect(md);

		boolean dialectSupportsSubSecondTimestamps = getTimestampFlag();

      if (!dialectSupportsSubSecondTimestamps || dialect.getTimestampMaximumFractionalDigits() == 0)
      {
         return "";
      }

		String result = "" + ts.getNanos();

      int timestampMaximumFractionalDigits = dialect.getTimestampMaximumFractionalDigits();
      if(result.length() >= timestampMaximumFractionalDigits)
      {
         result = result.substring(0, timestampMaximumFractionalDigits);
      }
		return result;
	}

   /**
    * If necessary inits the timestamp flag and returns the value indicating whether or not this _session
    * supports sub-second timestamps.
    *
    * @return true if supported; false otherwise.
    * @param dialectSupportsSubSecondTimestamps
    * @param _session
    */
   private boolean getTimestampFlag()
   {
      if (_dialectSupportsSubSecondTimestamps == null)
      {
         ISQLDatabaseMetaData md = _session.getMetaData();
         HibernateDialect dialect = DialectFactory.getDialect(md);
         _dialectSupportsSubSecondTimestamps = dialect.supportsSubSecondTimestamps();
      }
      return _dialectSupportsSubSecondTimestamps;
   }

   private String getNullableComment(ResultSetMetaData metaData, int colIndex) throws SQLException
   {
      if(ResultSetMetaData.columnNoNulls == metaData.isNullable(colIndex))
      {
         return " /*not nullable*/";
      }
      else
      {
         return "";
      }
   }

   private String prefixNulls(int toPrefix, int digitCount)
   {
      String ret = "" + toPrefix;

      while (ret.length() < digitCount)
      {
         ret = 0 + ret;
      }

      return ret;
   }

   private String getStatementSeparator(ISession session)
   {
      String statementSeparator = session.getQueryTokenizer().getSQLStatementSeparator();

      if (1 < statementSeparator.length())
      {
         statementSeparator = "\n" + statementSeparator + "\n";
      }

      return statementSeparator;
   }

}
