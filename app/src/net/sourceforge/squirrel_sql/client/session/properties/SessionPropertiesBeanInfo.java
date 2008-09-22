package net.sourceforge.squirrel_sql.client.session.properties;

/*
 * Copyright (C) 2001-2003 Colin Bell
 * colbell@users.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

/**
 * This is the <CODE>BeanInfo</CODE> class for <CODE>SessionProperties</CODE>.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SessionPropertiesBeanInfo extends SimpleBeanInfo
{
   private static PropertyDescriptor[] s_dscrs;
   private static Class<SessionProperties> CLAZZ = SessionProperties.class;

   private interface IPropNames extends SessionProperties.IPropertyNames
   {
      // Empty body.
   }

   public SessionPropertiesBeanInfo() throws IntrospectionException
   {
      super();
      if (s_dscrs == null)
      {
         s_dscrs = new PropertyDescriptor[]
         {
            new PropertyDescriptor(IPropNames.AUTO_COMMIT,
               CLAZZ, "getAutoCommit", "setAutoCommit"),
            new PropertyDescriptor(IPropNames.COMMIT_ON_CLOSING_CONNECTION,
               CLAZZ, "getCommitOnClosingConnection", "setCommitOnClosingConnection"),
            new PropertyDescriptor(IPropNames.CONTENTS_LIMIT_ROWS,
               CLAZZ, "getContentsLimitRows", "setContentsLimitRows"),
            new PropertyDescriptor(IPropNames.CONTENTS_NBR_ROWS_TO_SHOW,
               CLAZZ, "getContentsNbrRowsToShow", "setContentsNbrRowsToShow"),
            new PropertyDescriptor(IPropNames.FONT_INFO,
               CLAZZ, "getFontInfo", "setFontInfo"),
            new PropertyDescriptor(IPropNames.META_DATA_OUTPUT_CLASS_NAME,
               CLAZZ, "getMetaDataOutputClassName", "setMetaDataOutputClassName"),
            new PropertyDescriptor(IPropNames.SHOW_ROW_COUNT,
               CLAZZ, "getShowRowCount", "setShowRowCount"),
            new PropertyDescriptor(IPropNames.SHOW_TOOL_BAR,
               CLAZZ, "getShowToolBar", "setShowToolBar"),
            new PropertyDescriptor(IPropNames.SQL_LIMIT_ROWS,
               CLAZZ, "getSQLLimitRows", "setSQLLimitRows"),
            new PropertyDescriptor(IPropNames.SQL_NBR_ROWS_TO_SHOW,
               CLAZZ, "getSQLNbrRowsToShow", "setSQLNbrRowsToShow"),
            new PropertyDescriptor(IPropNames.SQL_STATEMENT_SEPARATOR_STRING,
               CLAZZ, "getSQLStatementSeparator", "setSQLStatementSeparator"),
            new PropertyDescriptor(IPropNames.SQL_RESULTS_OUTPUT_CLASS_NAME,
               CLAZZ, "getSQLResultsOutputClassName", "setSQLResultsOutputClassName"),
            new PropertyDescriptor(IPropNames.SQL_START_OF_LINE_COMMENT,
               CLAZZ, "getStartOfLineComment", "setStartOfLineComment"),
            new PropertyDescriptor(IPropNames.REMOVE_MULTI_LINE_COMMENT,
               CLAZZ, "getRemoveMultiLineComment", "setRemoveMultiLineComment"),
            new PropertyDescriptor(IPropNames.LIMIT_SQL_ENTRY_HISTORY_SIZE,
               CLAZZ, "getLimitSQLEntryHistorySize", "setLimitSQLEntryHistorySize"),
            new PropertyDescriptor(IPropNames.SQL_ENTRY_HISTORY_SIZE,
               CLAZZ, "getSQLEntryHistorySize", "setSQLEntryHistorySize"),
            new PropertyDescriptor(IPropNames.SQL_SHARE_HISTORY,
               CLAZZ, "getSQLShareHistory", "setSQLShareHistory"),
            new PropertyDescriptor(IPropNames.MAIN_TAB_PLACEMENT,
               CLAZZ, "getMainTabPlacement", "setMainTabPlacement"),
            new PropertyDescriptor(IPropNames.OBJECT_TAB_PLACEMENT,
               CLAZZ, "getObjectTabPlacement", "setObjectTabPlacement"),
            new PropertyDescriptor(IPropNames.SQL_EXECUTION_TAB_PLACEMENT,
               CLAZZ, "getSQLExecutionTabPlacement", "setSQLExecutionTabPlacement"),
            new PropertyDescriptor(IPropNames.SQL_RESULTS_TAB_PLACEMENT,
               CLAZZ, "getSQLResultsTabPlacement", "setSQLResultsTabPlacement"),
            new PropertyDescriptor(IPropNames.TABLE_CONTENTS_OUTPUT_CLASS_NAME,
               CLAZZ, "getTableContentsOutputClassName", "setTableContentsOutputClassName"),
            new PropertyDescriptor(IPropNames.ABORT_ON_ERROR,
               CLAZZ, "getAbortOnError", "setAbortOnError"),
            new PropertyDescriptor(IPropNames.SQL_RESULT_TAB_LIMIT,
               CLAZZ, "getSqlResultTabLimit", "setSqlResultTabLimit"),
            new PropertyDescriptor(IPropNames.LIMIT_SQL_RESULT_TABS,
               CLAZZ, "getLimitSqlResultTabs", "setLimitSqlResultTabs"),
            new PropertyDescriptor(IPropNames.LOAD_SCHEMAS_CATALOGS,
               CLAZZ, "getLoadSchemasCatalogs", "setLoadSchemasCatalogs"),
            new PropertyDescriptor(IPropNames.SHOW_RESULTS_META_DATA,
               CLAZZ, "getShowResultsMetaData", "setShowResultsMetaData"),

            new PropertyDescriptor(IPropNames.CATALOG_FILTER_INCLUDE,
               CLAZZ, "getCatalogFilterInclude", "setCatalogFilterInclude"),
            new PropertyDescriptor(IPropNames.SCHEMA_FILTER_INCLUDE,
               CLAZZ, "getSchemaFilterInclude", "setSchemaFilterInclude"),
            new PropertyDescriptor(IPropNames.OBJECT_FILTER_INCLUDE,
                CLAZZ, "getObjectFilterInclude", "setObjectFilterInclude"),
            new PropertyDescriptor(IPropNames.CATALOG_FILTER_EXCLUDE,
               CLAZZ, "getCatalogFilterExclude", "setCatalogFilterExclude"),
            new PropertyDescriptor(IPropNames.SCHEMA_FILTER_EXCLUDE,
               CLAZZ, "getSchemaFilterExclude", "setSchemaFilterExclude"),
            new PropertyDescriptor(IPropNames.OBJECT_FILTER_EXCLUDE,
                CLAZZ, "getObjectFilterExclude", "setObjectFilterExclude")
         };
      }
   }

   public PropertyDescriptor[] getPropertyDescriptors()
   {
      return s_dscrs;
   }
}
