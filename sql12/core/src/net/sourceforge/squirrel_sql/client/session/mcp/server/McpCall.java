package net.sourceforge.squirrel_sql.client.session.mcp.server;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.McpNoArgs;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.McpResultSet;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.McpSimpleString;
import net.sourceforge.squirrel_sql.fw.util.JsonMarshalUtil;
import org.apache.commons.lang3.StringUtils;

@SuppressWarnings("unchecked")
public enum McpCall
{
   getSessionName,
   getDriverClassName,
   getDriverName,
   getDriverVersion,
   getDatabaseProductName,
   getDatabaseProductVersion,
   executeQuery,
   getCatalogs,
   getSchemas,
   getCurrentSchema,
   getTables,
   getPrimaryKeys,
   getImportedKeys,
   getExportedKeys,
   getIndexInfo,
   getColumns;

   public static final String DISAPPROVED = "Call was not approved by SQuirreL user";
   private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");

   public String createCallString(Object callArgs)
   {
      switch( this )
      {
         case executeQuery:
            return createPrefix() + "\n" + StringUtils.trim(((McpSimpleString) callArgs).stringContent());
         default:
         {
            if( callArgs instanceof McpNoArgs )
            {
               return createPrefix() + "()";
            }
            else
            {
               return createPrefix() + "( " + JsonMarshalUtil.toJsonString(callArgs, false) + " )";
            }
         }
      }
   }

   private String createPrefix()
   {
      return "--" + renderNowTime() + " call: " + this.name();
   }

   public <T> T createDisapprovedMsg()
   {
      return (T) switch(this)
      {
         case executeQuery, getTables, getPrimaryKeys, getImportedKeys, getExportedKeys, getIndexInfo -> McpResultSet.ofError(DISAPPROVED);
         default -> new McpSimpleString(DISAPPROVED);
      };
   }

   private String renderNowTime()
   {
      return SIMPLE_DATE_FORMAT.format(new Date());
   }

}
