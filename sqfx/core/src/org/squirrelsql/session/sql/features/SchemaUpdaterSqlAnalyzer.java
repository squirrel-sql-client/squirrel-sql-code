package org.squirrelsql.session.sql.features;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * This class tries to update SchemaInfo after standard CREATE/ALTER statements.
 * This way Syntax highlighting and code completion are available just after
 * CREATE/ALTER statements were send to the DB.
 */
public class SchemaUpdaterSqlAnalyzer
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


   public static String getProcedureSimpleName(String sql)
   {
      sql = sql.trim();
      String upperSql = sql.toUpperCase();

      Matcher matcher;

      matcher = PATTERN_CREATE_OR_REPLACE_PROCEDURE.matcher(upperSql);
      if(matcher.find())
      {
         return _getProcedureSimpleName(matcher, sql);
      }

      matcher = PATTERN_CREATE_PROCEDURE.matcher(upperSql);
      if(matcher.find())
      {
         return _getProcedureSimpleName(matcher, sql);
      }

      matcher = PATTERN_ALTER_PROCEDURE.matcher(upperSql);
      if(matcher.find())
      {
         return _getProcedureSimpleName(matcher, sql);
      }

      matcher = PATTERN_CREATE_OR_REPLACE_FUNCTION.matcher(upperSql);
      if(matcher.find())
      {
         return _getProcedureSimpleName(matcher, sql);
      }

      matcher = PATTERN_CREATE_FUNCTION.matcher(upperSql);
      if(matcher.find())
      {
         return _getProcedureSimpleName(matcher, sql);
      }

      matcher = PATTERN_ALTER_FUNCTION.matcher(upperSql);
      if(matcher.find())
      {
         return _getProcedureSimpleName(matcher, sql);
      }

      matcher = PATTERN_DROP_PROCEDURE.matcher(upperSql);
      if(matcher.find())
      {
         return _getProcedureSimpleName(matcher, sql);
      }

      matcher = PATTERN_DROP_FUNCTION.matcher(upperSql);
      if(matcher.find())
      {
         return _getProcedureSimpleName(matcher, sql);
      }


      return null;


   }

   private static String _getProcedureSimpleName(Matcher matcher, String sql)
   {
      int endIx = matcher.end(1);
      int len = matcher.group(1).length();
      String proc = sql.substring(endIx - len, endIx);
      String[] splits = proc.split("\\.");
      String simpleName = splits[splits.length - 1];
      simpleName = removeQuotes(simpleName);

      return simpleName;

   }

   private static String removeQuotes(String simpleName)
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


   public static String getTableSimpleName(String sql)
   {
      sql = sql.trim();
      String upperSql = sql.toUpperCase();

      Matcher matcher;

      matcher = PATTERN_CREATE_TABLE.matcher(upperSql);
      if(matcher.find())
      {
         return _getTableSimpleName(matcher, sql);
      }

      matcher = PATTERN_ALTER_TABLE.matcher(upperSql);
      if(matcher.find())
      {
         return _getTableSimpleName(matcher, sql);
      }

      matcher = PATTERN_INSERT_INTO.matcher(upperSql);
      if(matcher.find())
      {
         return _getTableSimpleName(matcher, sql);
      }

      matcher = PATTERN_CREATE_OR_REPLACE_VIEW.matcher(upperSql);
      if(matcher.find())
      {
         return _getTableSimpleName(matcher, sql);
      }

      matcher = PATTERN_CREATE_VIEW.matcher(upperSql);
      if(matcher.find())
      {
         return _getTableSimpleName(matcher, sql);
      }

      matcher = PATTERN_CREATE_MATERIALIZED_VIEW.matcher(upperSql);
      if(matcher.find())
      {
         return _getTableSimpleName(matcher, sql);
      }
      
      matcher = PATTERN_ALTER_VIEW.matcher(upperSql);
      if(matcher.find())
      {
         return _getTableSimpleName(matcher, sql);
      }

      matcher = PATTERN_DROP_TABLE.matcher(upperSql);
      if(matcher.find())
      {
         return _getTableSimpleName(matcher, sql);
      }

      matcher = PATTERN_DROP_MATERIALIZED_VIEW.matcher(upperSql);
      if(matcher.find())
      {
         return _getTableSimpleName(matcher, sql);
      }

      matcher = PATTERN_DROP_VIEW.matcher(upperSql);
      if(matcher.find())
      {
         return _getTableSimpleName(matcher, sql);
      }


      return null;


   }

   private static String _getTableSimpleName(Matcher matcher, String sql)
   {
      int endIx = matcher.end(1);
      int len = matcher.group(1).length();
      String table = sql.substring(endIx - len, endIx);
      String[] splits = table.split("\\.");
      String simpleName = splits[splits.length - 1];
      simpleName = removeQuotes(simpleName);
      return simpleName;
   }

}
