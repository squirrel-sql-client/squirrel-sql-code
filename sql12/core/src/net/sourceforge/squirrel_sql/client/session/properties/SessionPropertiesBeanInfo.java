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

	private interface IPropNames extends SessionProperties.IPropertyNames
	{
		// Empty body.
	}

	/**
	 * See http://tinyurl.com/63no6t for discussion of the proper thread-safe way to implement
	 * getPropertyDescriptors().
	 * 
	 * @see java.beans.SimpleBeanInfo#getPropertyDescriptors()
	 */
	@Override	
	public PropertyDescriptor[] getPropertyDescriptors()
	{
		try
		{
			PropertyDescriptor[] result =
				new PropertyDescriptor[] {
						new PropertyDescriptor(IPropNames.AUTO_COMMIT, SessionProperties.class, "getAutoCommit",
							"setAutoCommit"),
						new PropertyDescriptor(IPropNames.COMMIT_ON_CLOSING_CONNECTION, SessionProperties.class,
							"getCommitOnClosingConnection", "setCommitOnClosingConnection"),
						new PropertyDescriptor(IPropNames.CONTENTS_LIMIT_ROWS, SessionProperties.class,
							"getContentsLimitRows", "setContentsLimitRows"),
						new PropertyDescriptor(IPropNames.CONTENTS_NBR_ROWS_TO_SHOW, SessionProperties.class,
							"getContentsNbrRowsToShow", "setContentsNbrRowsToShow"),
						new PropertyDescriptor(IPropNames.FONT_INFO, SessionProperties.class, "getFontInfo",
							"setFontInfo"),
						new PropertyDescriptor(IPropNames.META_DATA_OUTPUT_CLASS_NAME, SessionProperties.class,
							"getMetaDataOutputClassName", "setMetaDataOutputClassName"),
						new PropertyDescriptor(IPropNames.SHOW_ROW_COUNT, SessionProperties.class,
							"getShowRowCount", "setShowRowCount"),
						new PropertyDescriptor(IPropNames.SHOW_TOOL_BAR, SessionProperties.class, "getShowToolBar",
							"setShowToolBar"),
						new PropertyDescriptor(IPropNames.SQL_LIMIT_ROWS, SessionProperties.class,
							"getSQLLimitRows", "setSQLLimitRows"),
						new PropertyDescriptor(IPropNames.SQL_NBR_ROWS_TO_SHOW, SessionProperties.class,
							"getSQLNbrRowsToShow", "setSQLNbrRowsToShow"),
						new PropertyDescriptor(IPropNames.SQL_STATEMENT_SEPARATOR_STRING, SessionProperties.class,
							"getSQLStatementSeparator", "setSQLStatementSeparator"),
						new PropertyDescriptor(IPropNames.SQL_RESULTS_OUTPUT_CLASS_NAME, SessionProperties.class,
							"getSQLResultsOutputClassName", "setSQLResultsOutputClassName"),
						new PropertyDescriptor(IPropNames.SQL_START_OF_LINE_COMMENT, SessionProperties.class,
							"getStartOfLineComment", "setStartOfLineComment"),
						new PropertyDescriptor(IPropNames.REMOVE_MULTI_LINE_COMMENT, SessionProperties.class,
							"getRemoveMultiLineComment", "setRemoveMultiLineComment"),
						new PropertyDescriptor(IPropNames.REMOVE_LINE_COMMENT, SessionProperties.class,
							"getRemoveLineComment", "setRemoveLineComment"),
						new PropertyDescriptor(IPropNames.LIMIT_SQL_ENTRY_HISTORY_SIZE, SessionProperties.class,
							"getLimitSQLEntryHistorySize", "setLimitSQLEntryHistorySize"),
						new PropertyDescriptor(IPropNames.SQL_ENTRY_HISTORY_SIZE, SessionProperties.class,
							"getSQLEntryHistorySize", "setSQLEntryHistorySize"),
						new PropertyDescriptor(IPropNames.SQL_SHARE_HISTORY, SessionProperties.class,
							"getSQLShareHistory", "setSQLShareHistory"),
						new PropertyDescriptor(IPropNames.MAIN_TAB_PLACEMENT, SessionProperties.class,
							"getMainTabPlacement", "setMainTabPlacement"),
						new PropertyDescriptor(IPropNames.OBJECT_TAB_PLACEMENT, SessionProperties.class,
							"getObjectTabPlacement", "setObjectTabPlacement"),
						new PropertyDescriptor(IPropNames.SQL_EXECUTION_TAB_PLACEMENT, SessionProperties.class,
							"getSQLExecutionTabPlacement", "setSQLExecutionTabPlacement"),
						new PropertyDescriptor(IPropNames.SQL_RESULTS_TAB_PLACEMENT, SessionProperties.class,
							"getSQLResultsTabPlacement", "setSQLResultsTabPlacement"),
						new PropertyDescriptor(IPropNames.SQL_PANEL_ORIENTATION, SessionProperties.class,
									"getSqlPanelOrientation", "setSqlPanelOrientation"),
						new PropertyDescriptor(IPropNames.SQL_USE_FETCH_SIZE, SessionProperties.class,
							"getSQLFetchSize", "setSQLFetchSize"),
						new PropertyDescriptor(IPropNames.SQL_FETCH_SIZE, SessionProperties.class,
							"getSQLUseFetchSize", "setSQLUseFetchSize"),

						new PropertyDescriptor(IPropNames.TABLE_CONTENTS_OUTPUT_CLASS_NAME,SessionProperties.class, "getTableContentsOutputClassName","setTableContentsOutputClassName"),
						new PropertyDescriptor(IPropNames.KEEP_TABLE_LAYOUT_ON_RERUN, SessionProperties.class, "getKeepTableLayoutOnRerun","setKeepTableLayoutOnRerun"),

						new PropertyDescriptor(IPropNames.SHOW_ROW_NUMBERS_IN_TEXT_LAYOUT, SessionProperties.class, "getShowRowNumberInTextLayout","setShowRowNumberInTextLayout"),

                  new PropertyDescriptor(IPropNames.ABORT_ON_ERROR, SessionProperties.class,"getAbortOnError", "setAbortOnError"),
						new PropertyDescriptor(IPropNames.SHOW_SQL_ERRORS_IN_TAB, SessionProperties.class, "getShowSQLErrorsInTab", "setShowSQLErrorsInTab"),
						new PropertyDescriptor(IPropNames.WRITE_SQL_ERRORS_TO_LOG, SessionProperties.class,"getWriteSQLErrorsToLog", "setWriteSQLErrorsToLog"),
						new PropertyDescriptor(IPropNames.SQL_RESULT_TAB_LIMIT, SessionProperties.class,"getSqlResultTabLimit", "setSqlResultTabLimit"),
						new PropertyDescriptor(IPropNames.LIMIT_SQL_RESULT_TABS, SessionProperties.class,"getLimitSQLResultTabs", "setLimitSQLResultTabs"),
						new PropertyDescriptor(IPropNames.LOAD_SCHEMAS_CATALOGS, SessionProperties.class,"getLoadSchemasCatalogs", "setLoadSchemasCatalogs"),
						new PropertyDescriptor(IPropNames.LOAD_CONNECTIONS_CURRENT_CATALOG_ONLY, SessionProperties.class,"getLoadConnectionsCurrentCatalogOnly", "setLoadConnectionsCurrentCatalogOnly"),
						new PropertyDescriptor(IPropNames.SHOW_RESULTS_META_DATA, SessionProperties.class,"getShowResultsMetaData", "setShowResultsMetaData"),

						new PropertyDescriptor(IPropNames.CATALOG_FILTER_INCLUDE, SessionProperties.class,
							"getCatalogFilterInclude", "setCatalogFilterInclude"),
						new PropertyDescriptor(IPropNames.SCHEMA_FILTER_INCLUDE, SessionProperties.class,
							"getSchemaFilterInclude", "setSchemaFilterInclude"),
						new PropertyDescriptor(IPropNames.TABLE_TYPE_FILTER_INCLUDE, SessionProperties.class,
							"getTableTypeFilterInclude", "setTableTypeFilterInclude"),
						new PropertyDescriptor(IPropNames.OBJECT_FILTER_INCLUDE, SessionProperties.class,
							"getObjectFilterInclude", "setObjectFilterInclude"),
						new PropertyDescriptor(IPropNames.CATALOG_FILTER_EXCLUDE, SessionProperties.class,
							"getCatalogFilterExclude", "setCatalogFilterExclude"),
						new PropertyDescriptor(IPropNames.SCHEMA_FILTER_EXCLUDE, SessionProperties.class,
							"getSchemaFilterExclude", "setSchemaFilterExclude"),
						new PropertyDescriptor(IPropNames.TABLE_TYPE_FILTER_EXCLUDE, SessionProperties.class,
							"getTableTypeFilterExclude", "setTableTypeFilterExclude"),
						new PropertyDescriptor(IPropNames.OBJECT_FILTER_EXCLUDE, SessionProperties.class,
							"getObjectFilterExclude", "setObjectFilterExclude"),

						new PropertyDescriptor(IPropNames.LOAD_COLUMNS_IN_BACKGROUND, SessionProperties.class,
							"getLoadColumnsInBackground", "setLoadColumnsInBackground"),

						new PropertyDescriptor(IPropNames.META_DATA_LOADING_TIME_OUT, SessionProperties.class,
							"getMetaDataLoadingTimeOut", "setMetaDataLoadingTimeOut"),

						new PropertyDescriptor(IPropNames.QUERY_CONNECTION_POOL_SIZE, SessionProperties.class,
													  "getQueryConnectionPoolSize", "setQueryConnectionPoolSize"),

						new PropertyDescriptor(IPropNames.SQL_READ_ON, SessionProperties.class,
                        "getSQLReadOn", "setSQLReadOn"),

                  new PropertyDescriptor(IPropNames.SQL_READ_ON_BLOCK_SIZE, SessionProperties.class,
                        "getSQLReadOnBlockSize", "setSQLReadOnBlockSize"),

                  new PropertyDescriptor(IPropNames.ALLOW_CTRL_B_JUMP_TO_OBJECT_TREE, SessionProperties.class,
                        "getAllowCtrlBJumpToObjectTree", "setAllowCtrlBJumpToObjectTree"),

                  new PropertyDescriptor(IPropNames.ALLOW_CTRL_MOUSE_CLICK_JUMP_TO_OBJECT_TREE, SessionProperties.class,
                        "getAllowCtrlMouseClickJumpToObjectTree", "setAllowCtrlMouseClickJumpToObjectTree"),

                  new PropertyDescriptor(IPropNames.NULL_VALUE_COLOR_RGB, SessionProperties.class,
                        "getNullValueColorRGB", "setNullValueColorRGB"),

                  new PropertyDescriptor(IPropNames.COLOR_NULL_VALUES, SessionProperties.class,
                        "isColorNullValues", "setColorNullValues"),

                  new PropertyDescriptor(IPropNames.SORT_NULLS_AS_HIGHEST_VALUE, SessionProperties.class,
                        "isSortNullsAsHighestValue", "setSortNullsAsHighestValue")
            };

			return result;
		}
		catch (IntrospectionException e)
		{
			throw new Error(e);
		}
	}
}
