package net.sourceforge.squirrel_sql.plugins.sqlscript.table_script;

/*
 * Copyright (C) 2001 Johan Compagner
 * jcompagner@j-com.nl
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SessionUtils;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.*;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.sqlscript.FrameWorkAcessor;
import net.sourceforge.squirrel_sql.plugins.sqlscript.prefs.SQLScriptPreferencesManager;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.sql.*;
import java.util.Calendar;

public class CreateDataScriptCommand extends WindowAdapter implements ICommand
{
   private static final ILogger s_log =  LoggerController.createLogger(CreateDataScriptCommand.class);

   /** flag that gets set when the first timestamp column is encountered */
   private Boolean dialectSupportsSubSecondTimestamps = null; 
   
	private IAbortController _abortController;

   protected ISession _session;

   private boolean _templateScriptOnly;

   private final IObjectTreeAPI _objectTreeAPI;

   public CreateDataScriptCommand(ISession session, boolean templateScriptOnly)
   {
      this(FrameWorkAcessor.getObjectTreeAPI(session), templateScriptOnly);
   }

   public CreateDataScriptCommand(IObjectTreeAPI objectTreeAPI, boolean templateScriptOnly)
   {
      _objectTreeAPI = objectTreeAPI;
      _templateScriptOnly = templateScriptOnly;

      _session = _objectTreeAPI.getSession();

      Frame owningFrame = SessionUtils.getOwningFrame(FrameWorkAcessor.getSQLPanelAPI(_session));
      _abortController = new AbortController(owningFrame);
   }



      protected void showAbortFrame()
   {
      if (false == _abortController.isVisble())
      {
         _abortController.setVisible(true);
      }
   }


   /**
    * Execute this command.
    */
   public void execute()
   {
      final StringBuilder sbRows = new StringBuilder();

      _session.getApplication().getThreadPool().addTask(new Runnable()
      {
         public void run()
         {
            ISQLConnection conn = _session.getSQLConnection();
            try (Statement stmt = conn.createStatement())
            {
               IDatabaseObjectInfo[] dbObjs = _objectTreeAPI.getSelectedDatabaseObjects();

               for (int k = 0; k < dbObjs.length; k++)
               {
                  if (dbObjs[k] instanceof ITableInfo)
                  {
                     if (isAborted()) break;
                     ITableInfo ti = (ITableInfo) dbObjs[k];
                     String sTable = ScriptUtil.getTableName(ti);

                     ResultSet srcResult = executeDataSelectSQL(stmt, ti);
                     genInserts(srcResult, sTable, sbRows, false);
                  }
               }

               GUIUtils.processOnSwingEventThread(new Runnable()
               {
                  public void run()
                  {
                     if (sbRows.length() > 0)
                     {
                        FrameWorkAcessor.appendScriptToEditor(sbRows.toString(), _objectTreeAPI);
                     }
                     hideAbortFrame();
                  }
               });
            }
            catch (Exception e)
            {
               _session.showErrorMessage(e);
               s_log.error(e);
            }
         }
      });

      showAbortFrame();
   }

   private ResultSet executeDataSelectSQL(Statement stmt, ITableInfo ti) throws SQLException
   {
      StringBuilder sql = genDataSelectSQL(ti, ScriptUtil.getTableName(ti));

      ResultSet srcResult;

      try
      {
         srcResult = stmt.executeQuery(sql.toString());
      }
      catch (SQLException e)
      {
         boolean qualifyTableNames = SQLScriptPreferencesManager.getPreferences().isQualifyTableNames();
         if(false == qualifyTableNames)
         {
            try
            {
               sql = genDataSelectSQL(ti, ScriptUtil.getTableName(ti, true, false));
               srcResult = stmt.executeQuery(sql.toString());
            }
            catch (SQLException e1)
            {
               boolean useDoubleQuotes = SQLScriptPreferencesManager.getPreferences().isUseDoubleQuotes();
               if(false == useDoubleQuotes)
               {
                  sql = genDataSelectSQL(ti, ScriptUtil.getTableName(ti, true, false));
                  srcResult = stmt.executeQuery(sql.toString());
               }
               else
               {
                  throw e;
               }
            }
         }
         else
         {
            throw e;
         }
      }
      return srcResult;
   }

   private StringBuilder genDataSelectSQL(ITableInfo ti, String tableName) throws SQLException
   {
      StringBuilder sql = new StringBuilder();
      sql.append("select * from ");
      sql.append(tableName);

      // Some databases cannot order by LONG/LOB columns.
      if (!JDBCTypeMapper.isLongType(getFirstColumnType(ti)))
      {
          sql.append(" order by ");
          sql.append(getFirstColumnName(ti));
          sql.append(" asc ");
      }
      if (s_log.isDebugEnabled()) {
         s_log.debug("execute: generating insert statements from data retrieved with SQL = "
            + sql.toString());
      }
      return sql;
   }

   protected String getFirstColumnName(ITableInfo ti) throws SQLException {
       TableColumnInfo[] infos = 
           _session.getSQLConnection().getSQLMetaData().getColumnInfo(ti);
       return infos[0].getColumnName();
   }

   protected int getFirstColumnType(ITableInfo ti) throws SQLException {
       TableColumnInfo[] infos = 
           _session.getSQLConnection().getSQLMetaData().getColumnInfo(ti);
       return infos[0].getDataType();
   }
   
   
   protected void genInserts(ResultSet srcResult, String sTable, StringBuilder sbRows, boolean headerOnly)
      throws SQLException
   {
      ResultSetMetaData metaData = srcResult.getMetaData();
      final HibernateDialect dialect = DialectFactory.getDialect(_session.getMetaData());

      int iColumnCount = metaData.getColumnCount();
      ColumnInfo[] colInfo = new ColumnInfo[iColumnCount];


      for (int i = 1; i <= iColumnCount; i++)
      {
         colInfo[i-1] = new ColumnInfo(metaData.getColumnName(i), metaData.getColumnType(i));
      }

      // Just a helper to make the fromResultSet ? ... below
      // look nicer.
      boolean fromResultSet = !_templateScriptOnly && !headerOnly;

      sbRows.append("\n\n");
      Timestamp currentTime = new Timestamp(System.currentTimeMillis());
      while (srcResult.next() || _templateScriptOnly || headerOnly)
      {
         if (isAborted()) break;
         sbRows.append("INSERT INTO ");
         StringBuffer sbValues = new StringBuffer();
         sbRows.append(sTable);
         sbRows.append(" (");
         sbValues.append(" VALUES (");

         ScriptUtil su = new ScriptUtil();

         for (int i = 0; i < iColumnCount; i++)
         {
            int iIndexPoint = colInfo[i].columnName.lastIndexOf('.');
            sbRows.append(su.makeColumnNameUnique(colInfo[i].columnName.substring(iIndexPoint + 1)));

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
                     esc.append(getNanos(ts));
                     esc.append("'}");

                     if (!fromResultSet) {
                     	esc.append(getNullableComment(metaData, i+1));
                     }
                     sbValues.append(esc);
                  }

               }
            }
            else if (Types.BIT == colInfo[i].sqlType
                     || Types.BOOLEAN == colInfo[i].sqlType)
            {
               boolean iBoolean = fromResultSet ? srcResult.getBoolean(i + 1) : false;

               if(fromResultSet && srcResult.wasNull())
               {
                  sbValues.append("null");
               }
               else if (iBoolean)
               {
                   // PostgreSQL uses literal values true/false instead of 1/0.
                   if (DialectFactory.isPostgreSQL(_session.getMetaData())) {
                       sbValues.append("true");
                   } else {
                       sbValues.append(1);
                   }
               }
               else
               {
                   // PostgreSQL uses literal values true/false instead of 1/0.
                   if (DialectFactory.isPostgreSQL(_session.getMetaData())) {
                       sbValues.append("false");
                   } else {
                       sbValues.append(0);
                   }
               }

               if(false == fromResultSet)
               {
                  sbValues.append(getNullableComment(metaData, i+1));
               }
            }
            else if (Types.BLOB == colInfo[i].sqlType
            			|| Types.BINARY == colInfo[i].sqlType) {
            	if(fromResultSet) {            
                	if(srcResult.wasNull()) {
                		sbValues.append("null");
                	} else {
                		byte[] binaryData = null;
                		if (Types.BLOB == colInfo[i].sqlType) {
                   		Blob blobResult = srcResult.getBlob(i+1);
                   		binaryData = blobResult.getBytes(1, (int)blobResult.length());                			
                		} else {
                			binaryData = srcResult.getBytes(i+1);
                		}
                		sbValues.append(dialect.getBinaryLiteralString(binaryData));                			
                	}
            	} else {
            		sbValues.append("'CAFEBABE'").append(getNullableComment(metaData, i+1));
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
            sbRows.append(",");
         }
         // delete last ','
         sbValues.setLength(sbValues.length() - 1);
         sbRows.setLength(sbRows.length() - 1);

         // close it.
         sbValues.append(")").append(getStatementSeparator()).append("\n");
         sbRows.append(")");

         if(false == headerOnly)
         {
            sbRows.append(sbValues.toString());
         }

         if(_templateScriptOnly || headerOnly)
         {
            break;
         }
      }
      srcResult.close();
   }

   private String escapeNewlines(String sResult)
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
	 * @return a string representing the nanosecond value.
	 */
	private String getNanos(Timestamp ts) throws SQLException
	{
		ISQLDatabaseMetaData md = _session.getMetaData();
		HibernateDialect dialect = DialectFactory.getDialect(md);

		boolean dialectSupportsSubSecondTimestamps = getTimestampFlag();
		if (!dialectSupportsSubSecondTimestamps 
				|| dialect.getTimestampMaximumFractionalDigits() == 0) { 
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
    * If necessary inits the timestamp flag and returns the value indicating whether or not this session
    * supports sub-second timestamps.
    * 
    * @return true if supported; false otherwise.
    * @throws SQLException
    */
   private boolean getTimestampFlag() throws SQLException {
   	if (dialectSupportsSubSecondTimestamps == null) {
   		ISQLDatabaseMetaData md = _session.getMetaData();
   		HibernateDialect dialect = DialectFactory.getDialect(md);
   		dialectSupportsSubSecondTimestamps = dialect.supportsSubSecondTimestamps();
   	}
   	return dialectSupportsSubSecondTimestamps;
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


   private String getStatementSeparator()
   {
      String statementSeparator = 
          _session.getQueryTokenizer().getSQLStatementSeparator();

      if (1 < statementSeparator.length())
      {
         statementSeparator = "\n" + statementSeparator + "\n";
      }

      return statementSeparator;
   }



   private static class ColumnInfo
   {
      int sqlType; // As in java.sql.Types
      String columnName;

      public ColumnInfo(String columnName, int sqlType)
      {
         this.columnName = columnName;
         this.sqlType = sqlType;
      }
   }

   protected void hideAbortFrame()
   {
      _abortController.setVisible(false);
   }

   protected boolean isAborted()
   {
      return _abortController.isStop();
   }

}
