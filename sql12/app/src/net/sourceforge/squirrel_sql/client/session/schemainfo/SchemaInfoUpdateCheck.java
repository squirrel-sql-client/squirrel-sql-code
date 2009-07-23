package net.sourceforge.squirrel_sql.client.session.schemainfo;

import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.ISessionWidget;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SessionInternalFrame;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLExecutionInfo;
import net.sourceforge.squirrel_sql.fw.sql.*;

import javax.swing.*;
import java.sql.DatabaseMetaData;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * This class tries to update SchemaInfo after standard CREATE/ALTER statements.
 * This way Syntax highlighting and code completion are available just after
 * CREATE/ALTER statements were send to the DB.
 */
public class SchemaInfoUpdateCheck
{
   private static final Pattern PATTERN_CREATE_TABLE = Pattern.compile("CREATE\\s+TABLE\\s+([A-Z0-9_\\.\"]+)");
   private static final Pattern PATTERN_ALTER_TABLE = Pattern.compile("ALTER\\s+TABLE\\s+([A-Z0-9_\\.\"]+)");
   private static final Pattern PATTERN_INSERT_INTO = Pattern.compile("SELECT\\s+INTO\\s+([A-Z0-9_\\.\"]+)");

   private static final Pattern PATTERN_CREATE_VIEW = Pattern.compile("CREATE\\s+VIEW\\s+([A-Z0-9_\\.\"]+)");
   private static final Pattern PATTERN_CREATE_MATERIALIZED_VIEW = Pattern.compile("CREATE\\s+MATERIALIZED\\s+VIEW\\s+([A-Z0-9_\\.\"]+)");
   private static final Pattern PATTERN_CREATE_OR_REPLACE_VIEW = Pattern.compile("CREATE\\s+OR\\s+REPLACE\\s+VIEW\\s+([A-Z0-9_\\.\"]+)");
   private static final Pattern PATTERN_ALTER_VIEW = Pattern.compile("ALTER\\s+VIEW\\s+([A-Z0-9_\\.\"]+)");

   private static final Pattern PATTERN_CREATE_PROCEDURE = Pattern.compile("CREATE\\s+PROCEDURE\\s+([A-Z0-9_\\.\"]+)");
   private static final Pattern PATTERN_CREATE_OR_REPLACE_PROCEDURE = Pattern.compile("CREATE\\s+OR\\s+REPLACE\\s+PROCEDURE\\s+([A-Z0-9_\\.\"]+)");
   private static final Pattern PATTERN_ALTER_PROCEDURE = Pattern.compile("ALTER\\s+PROCEDURE\\s+([A-Z0-9_\\.\"]+)");

   private static final Pattern PATTERN_CREATE_FUNCTION = Pattern.compile("CREATE\\s+FUNCTION\\s+([A-Z0-9_\\.\"]+)");
   private static final Pattern PATTERN_CREATE_OR_REPLACE_FUNCTION = Pattern.compile("CREATE\\s+OR\\s+REPLACE\\s+FUNCTION\\s+([A-Z0-9_\\.\"]+)");
   private static final Pattern PATTERN_ALTER_FUNCTION = Pattern.compile("ALTER\\s+FUNCTION\\s+([A-Z0-9_\\.\"]+)");


   private static final Pattern PATTERN_DROP_TABLE = Pattern.compile("DROP\\s+TABLE\\s+([A-Z0-9_\\.\"]+)");
   private static final Pattern PATTERN_DROP_VIEW = Pattern.compile("DROP\\s+VIEW\\s+([A-Z0-9_\\.\"]+)");
   private static final Pattern PATTERN_DROP_MATERIALIZED_VIEW = Pattern.compile("DROP\\s+MATERIALIZED\\s+VIEW\\s+([A-Z0-9_\\.\"]+)");   

   private static final Pattern PATTERN_DROP_PROCEDURE = Pattern.compile("DROP\\s+PROCEDURE\\s+([A-Z0-9_\\.\"]+)");
   private static final Pattern PATTERN_DROP_FUNCTION = Pattern.compile("DROP\\s+FUNCTION\\s+([A-Z0-9_\\.\"]+)");


   private Set<IDatabaseObjectInfo> _updateDatabaseObjectInfos = 
       new HashSet<IDatabaseObjectInfo>();
   private Set<String> _dropTableSimpleNames = new HashSet<String>();
   private Set<String> _dropProcedureSimpleNames = new HashSet<String>();

   private ISession _session;
   private SQLDatabaseMetaData _dmd;

   public SchemaInfoUpdateCheck(ISession session)
   {
      _session = session;
      _dmd = _session.getSQLConnection().getSQLMetaData();
   }

   public void addExecutionInfo(SQLExecutionInfo exInfo)
   {
      if(null == exInfo || null == exInfo.getSQL())
      {
         return;
      }

      TableInfo[] tis = getTableInfos(exInfo.getSQL());
      for (int i = 0; i < tis.length; i++)
      {
         _updateDatabaseObjectInfos.add(tis[i]);
      }


      ProcedureInfo[] pi = getProcedureInfos(exInfo.getSQL());
      for (int i = 0; i < pi.length; i++)
      {
         _updateDatabaseObjectInfos.add(pi[i]);
      }

      String dtsn = getDropTableSimpleName(exInfo.getSQL());
      if(null != dtsn)
      {
         _dropTableSimpleNames.add(dtsn);
      }

      String dpsn = getDropProcedureSimpleName(exInfo.getSQL());
      if(null != dpsn)
      {
         _dropProcedureSimpleNames.add(dpsn);
      }
   }

   private String getDropProcedureSimpleName(String sql)
   {
      sql = sql.trim();
      String upperSql = sql.toUpperCase();

      Matcher matcher;

      matcher = PATTERN_DROP_PROCEDURE.matcher(upperSql);
      if(matcher.find())
      {
         return createProcdureInfos(matcher, sql, true)[0].getSimpleName();
      }

      matcher = PATTERN_DROP_FUNCTION.matcher(upperSql);
      if(matcher.find())
      {
         return createProcdureInfos(matcher, sql, true)[0].getSimpleName();
      }

      return null;
   }

   private String getDropTableSimpleName(String sql)
   {
      sql = sql.trim();
      String upperSql = sql.toUpperCase();

      Matcher matcher;

      matcher = PATTERN_DROP_TABLE.matcher(upperSql);
      if(matcher.find())
      {
         return createTableInfos(matcher, sql, "TABLE", true)[0].getSimpleName();
      }

      matcher = PATTERN_DROP_MATERIALIZED_VIEW.matcher(upperSql);
      if(matcher.find())
      {
         return createTableInfos(matcher, sql, "TABLE", true)[0].getSimpleName();
      }
      
      matcher = PATTERN_DROP_VIEW.matcher(upperSql);
      if(matcher.find())
      {
         return createTableInfos(matcher, sql, "VIEW", true)[0].getSimpleName();
      }

      return null;

   }

   public void flush()
   {
      if(60 < _updateDatabaseObjectInfos.size() + _dropTableSimpleNames.size() + _dropProcedureSimpleNames.size())
      {
         // reload complete SchemaInfo
         SQLDatabaseMetaData dmd = _session.getSQLConnection().getSQLMetaData();
         DatabaseObjectInfo sessionOI = new DatabaseObjectInfo(null, null, "SessionDummy", DatabaseObjectType.SESSION, dmd);
         _session.getSchemaInfo().reload(sessionOI, false);


      }
      else
      {
          for (IDatabaseObjectInfo doi : _updateDatabaseObjectInfos) {
              _session.getSchemaInfo().reload(doi);
          }

         for (String simpleTableName : _dropTableSimpleNames) {
             _session.getSchemaInfo().refershCacheForSimpleTableName(simpleTableName, false);
         }
         
         for (String simpleProcName : _dropProcedureSimpleNames) {
             _session.getSchemaInfo().refreshCacheForSimpleProcedureName(simpleProcName, false);
         }
      }

      if(0 < _updateDatabaseObjectInfos.size()  + _dropTableSimpleNames.size() + _dropProcedureSimpleNames.size())
      {

         _session.getSchemaInfo().fireSchemaInfoUpdate();
         SwingUtilities.invokeLater(new Runnable()
         {
            public void run()
            {
               repaintSqlEditor();
            }
         });
      }

      _updateDatabaseObjectInfos.clear();
      _dropTableSimpleNames.clear();
      _dropProcedureSimpleNames.clear();

   }

   private void repaintSqlEditor()
   {
      ISessionWidget activeSessionWidget = _session.getActiveSessionWindow();

      if (activeSessionWidget instanceof SQLInternalFrame)
      {
         ISQLEntryPanel sqlEntryPanel = ((SQLInternalFrame) activeSessionWidget).getSQLPanelAPI().getSQLEntryPanel();
         sqlEntryPanel.getTextComponent().repaint();
         _session.getParserEventsProcessor(sqlEntryPanel.getIdentifier()).triggerParser();
      }

      if (activeSessionWidget instanceof SessionInternalFrame)
      {
         ISQLEntryPanel sqlEntryPanel = ((SessionInternalFrame) activeSessionWidget).getSQLPanelAPI().getSQLEntryPanel();
         sqlEntryPanel.getTextComponent().repaint();
         _session.getParserEventsProcessor(sqlEntryPanel.getIdentifier()).triggerParser();
      }

   }


   private ProcedureInfo[] getProcedureInfos(String sql)
   {
      sql = sql.trim();
      String upperSql = sql.toUpperCase();

      Matcher matcher;

      matcher = PATTERN_CREATE_OR_REPLACE_PROCEDURE.matcher(upperSql);
      if(matcher.find())
      {
         return createProcdureInfos(matcher, sql, false);
      }

      matcher = PATTERN_CREATE_PROCEDURE.matcher(upperSql);
      if(matcher.find())
      {
         return createProcdureInfos(matcher, sql, false);
      }

      matcher = PATTERN_ALTER_PROCEDURE.matcher(upperSql);
      if(matcher.find())
      {
         return createProcdureInfos(matcher, sql, true);
      }

      matcher = PATTERN_CREATE_OR_REPLACE_FUNCTION.matcher(upperSql);
      if(matcher.find())
      {
         return createProcdureInfos(matcher, sql, false);
      }

      matcher = PATTERN_CREATE_FUNCTION.matcher(upperSql);
      if(matcher.find())
      {
         return createProcdureInfos(matcher, sql, false);
      }

      matcher = PATTERN_ALTER_FUNCTION.matcher(upperSql);
      if(matcher.find())
      {
         return createProcdureInfos(matcher, sql, true);
      }

      return new ProcedureInfo[0];


   }

   private ProcedureInfo[] createProcdureInfos(Matcher matcher, String sql, boolean isAlterOrDrop)
   {
      int endIx = matcher.end(1);
      int len = matcher.group(1).length();
      String proc = sql.substring(endIx - len, endIx);
      String[] splits = proc.split("\\.");
      String simpleName = splits[splits.length - 1];
      simpleName = removeQuotes(simpleName);

      if(isAlterOrDrop)
      {
         String buf = _session.getSchemaInfo().getCaseSensitiveProcedureName(simpleName);
         if(null != buf)
         {
            simpleName = buf;
         }
         return new ProcedureInfo[]{new ProcedureInfo(null, null, simpleName, null, DatabaseMetaData.procedureResultUnknown, _dmd)};
      }
      else
      {
         // DB2 stores all names in upper case.
         // PostgreSQL stores all names in lower case.
         // That's why we may not find proc as it was written in the create statement.
         // So we try out the upper and lower case names too.
         return new ProcedureInfo[]
            {
               new ProcedureInfo(null, null, simpleName, null, DatabaseMetaData.procedureResultUnknown, _dmd),
               new ProcedureInfo(null, null, simpleName.toUpperCase(), null, DatabaseMetaData.procedureResultUnknown, _dmd),
               new ProcedureInfo(null, null, simpleName.toLowerCase(), null, DatabaseMetaData.procedureResultUnknown, _dmd)
            };
      }
   }

   private String removeQuotes(String simpleName)
   {
      if(simpleName.startsWith("\""))
      {
         simpleName = simpleName.substring(1);
      }

      if(simpleName.endsWith("\""))
      {
         simpleName = simpleName.substring(0, simpleName.length() - 1);
      }

      return simpleName;
   }


   private TableInfo[] getTableInfos(String sql)
   {
      sql = sql.trim();
      String upperSql = sql.toUpperCase();

      Matcher matcher;

      matcher = PATTERN_CREATE_TABLE.matcher(upperSql);
      if(matcher.find())
      {
         return createTableInfos(matcher, sql, "TABLE", false);
      }

      matcher = PATTERN_ALTER_TABLE.matcher(upperSql);
      if(matcher.find())
      {
         return createTableInfos(matcher, sql, "TABLE", true);
      }

      matcher = PATTERN_INSERT_INTO.matcher(upperSql);
      if(matcher.find())
      {
         return createTableInfos(matcher, sql, "TABLE", false);
      }

      matcher = PATTERN_CREATE_OR_REPLACE_VIEW.matcher(upperSql);
      if(matcher.find())
      {
         return createTableInfos(matcher, sql, "VIEW", false);
      }

      matcher = PATTERN_CREATE_VIEW.matcher(upperSql);
      if(matcher.find())
      {
         return createTableInfos(matcher, sql, "VIEW", false);
      }

      matcher = PATTERN_CREATE_MATERIALIZED_VIEW.matcher(upperSql);
      if(matcher.find())
      {
         return createTableInfos(matcher, sql, "TABLE", false);
      }
      
      matcher = PATTERN_ALTER_VIEW.matcher(upperSql);
      if(matcher.find())
      {
         return createTableInfos(matcher, sql, "VIEW", true);
      }

      return new TableInfo[0];


   }

   private TableInfo[] createTableInfos(Matcher matcher, String sql, String type, boolean isAlterOrDrop)
   {
      int endIx = matcher.end(1);
      int len = matcher.group(1).length();
      String table = sql.substring(endIx - len, endIx);
      String[] splits = table.split("\\.");
      String simpleName = splits[splits.length - 1];
      simpleName = removeQuotes(simpleName);

      if(isAlterOrDrop)
      {
         String buf = _session.getSchemaInfo().getCaseSensitiveTableName(simpleName);
         if(null != buf)
         {
            simpleName = buf;
         }
         return new TableInfo[]{new TableInfo(null, null, simpleName, type, null, _dmd)};
      }
      else
      {
         // DB2 stores all names in upper case.
         // PostgreSQL stores table names in lower case.
         // That's why we may not find table as it was written in the create statement.
         // So we try out the upper and lower case names too.
         return new TableInfo[]
            {
               new TableInfo(null, null, simpleName, type, null, _dmd),
               new TableInfo(null, null, simpleName.toUpperCase(), type, null, _dmd),
               new TableInfo(null, null, simpleName.toLowerCase(), type, null, _dmd)
            };
      }
   }

}
