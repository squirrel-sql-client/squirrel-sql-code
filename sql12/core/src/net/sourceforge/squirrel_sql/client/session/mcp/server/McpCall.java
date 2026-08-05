package net.sourceforge.squirrel_sql.client.session.mcp.server;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.McpNoArgs;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.McpResultSet;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.McpSimpleString;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.SimpleDataSet;
import net.sourceforge.squirrel_sql.fw.util.JsonMarshalUtil;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
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

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(McpCall.class);

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

   public <T> T createDisapprovedMsg(String userToAiDisapproveResponse)
   {
      return (T) switch(this)
      {
         case executeQuery, getTables, getPrimaryKeys, getImportedKeys, getExportedKeys, getIndexInfo, getSchemas, getCatalogs, getColumns -> McpResultSet.ofError(buildDisapprovedMessage(userToAiDisapproveResponse));
         default -> new McpSimpleString(buildDisapprovedMessage(userToAiDisapproveResponse));
      };
   }

   private static String buildDisapprovedMessage(String userToAiDisapproveResponse)
   {
      String ret;
      if(StringUtils.isBlank(userToAiDisapproveResponse))
      {
         ret = DISAPPROVED;
      }
      else
      {
         ret = "%s\nUser edited disapproval message to be respected by AI:\n%s".formatted(DISAPPROVED, userToAiDisapproveResponse);
      }

      Main.getApplication().getMessageHandler().showMessage(s_stringMgr.getString("McpCall.ai.disapprove.message", ret));

      return ret;

   }

   private String renderNowTime()
   {
      return SIMPLE_DATE_FORMAT.format(new Date());
   }

   public DataSetViewerTablePanel buildResultTableComponentForApproval(McpCallExecutor callExecutor)
   {
      try
      {

         switch(this)
         {
            case getSessionName, getDriverClassName, getDriverName, getDriverVersion, getDatabaseProductName, getDatabaseProductVersion, getCurrentSchema ->
            {
               return McpApprovalCallPreviewBuilder.createSingleMcpStringSetViewerTablePanel(callExecutor, this);
            }
            case getCatalogs, getSchemas, getTables, getPrimaryKeys, getImportedKeys, getExportedKeys, getIndexInfo, getColumns ->
            {
               return McpApprovalCallPreviewBuilder.createMcpResultSetDataSetViewerTablePanel(callExecutor, this);
            }
            case executeQuery ->
            {
               ColumnDisplayDefinition
                     [] columnDisplayDefinitions = {new ColumnDisplayDefinition(200, this.name())};
               ArrayList<Object[]> list = new ArrayList<>();
               list.add(new Object[]{"TODO"});

               SimpleDataSet simpleDataSet = new SimpleDataSet(list, columnDisplayDefinitions);
               DataSetViewerTablePanel table = new DataSetViewerTablePanel();
               table.init(null, null);
               table.show(simpleDataSet);
               return table;
            }
            default -> throw new IllegalStateException("Unknown McpCall " + this.name());
         }
      }
      catch(DataSetException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }
}
