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
	private static Class CLAZZ = SessionProperties.class;

	private interface IPropNames extends SessionProperties.IPropertyNames
	{
		// Empty body.
	}

	public SessionPropertiesBeanInfo() throws IntrospectionException
	{
		super();
		if (s_dscrs == null)
		{
			s_dscrs = new PropertyDescriptor[26];
			s_dscrs[0] = new PropertyDescriptor(IPropNames.AUTO_COMMIT,
									CLAZZ, "getAutoCommit", "setAutoCommit");
			s_dscrs[1] = new PropertyDescriptor(IPropNames.COMMIT_ON_CLOSING_CONNECTION,
									CLAZZ, "getCommitOnClosingConnection", "setCommitOnClosingConnection");
			s_dscrs[2] = new PropertyDescriptor(IPropNames.CONTENTS_LIMIT_ROWS,
									CLAZZ, "getContentsLimitRows", "setContentsLimitRows");
			s_dscrs[3] = new PropertyDescriptor(IPropNames.CONTENTS_NBR_ROWS_TO_SHOW,
									CLAZZ, "getContentsNbrRowsToShow", "setContentsNbrRowsToShow");
			s_dscrs[4] = new PropertyDescriptor(IPropNames.FONT_INFO,
									CLAZZ, "getFontInfo", "setFontInfo");
			s_dscrs[5] = new PropertyDescriptor(IPropNames.META_DATA_OUTPUT_CLASS_NAME,
									CLAZZ, "getMetaDataOutputClassName", "setMetaDataOutputClassName");
			s_dscrs[6] = new PropertyDescriptor(IPropNames.SHOW_ROW_COUNT,
									CLAZZ, "getShowRowCount", "setShowRowCount");
			s_dscrs[7] = new PropertyDescriptor(IPropNames.SHOW_TOOL_BAR,
									CLAZZ, "getShowToolBar", "setShowToolBar");
			s_dscrs[8] = new PropertyDescriptor(IPropNames.SQL_LIMIT_ROWS,
									CLAZZ, "getSQLLimitRows", "setSQLLimitRows");
			s_dscrs[9] = new PropertyDescriptor(IPropNames.SQL_NBR_ROWS_TO_SHOW,
									CLAZZ, "getSQLNbrRowsToShow", "setSQLNbrRowsToShow");
			s_dscrs[10] = new PropertyDescriptor(IPropNames.SQL_STATEMENT_SEPARATOR_STRING,
									CLAZZ, "getSQLStatementSeparator", "setSQLStatementSeparator");
			s_dscrs[11] = new PropertyDescriptor(IPropNames.SQL_RESULTS_OUTPUT_CLASS_NAME,
									CLAZZ, "getSQLResultsOutputClassName", "setSQLResultsOutputClassName");
			s_dscrs[12] = new PropertyDescriptor(IPropNames.LARGE_RESULT_SET_OBJECT_INFO,
									CLAZZ, "getLargeResultSetObjectInfo", "setLargeResultSetObjectInfo");
			s_dscrs[13] = new PropertyDescriptor(IPropNames.SQL_START_OF_LINE_COMMENT,
									CLAZZ, "getStartOfLineComment", "setStartOfLineComment");
			s_dscrs[14] = new PropertyDescriptor(IPropNames.LIMIT_SQL_ENTRY_HISTORY_SIZE,
									CLAZZ, "getLimitSQLEntryHistorySize", "setLimitSQLEntryHistorySize");
			s_dscrs[15] = new PropertyDescriptor(IPropNames.SQL_ENTRY_HISTORY_SIZE,
									CLAZZ, "getSQLEntryHistorySize", "setSQLEntryHistorySize");
			s_dscrs[16] = new PropertyDescriptor(IPropNames.SQL_SHARE_HISTORY,
									CLAZZ, "getSQLShareHistory", "setSQLShareHistory");
			s_dscrs[17] = new PropertyDescriptor(IPropNames.MAIN_TAB_PLACEMENT,
									CLAZZ, "getMainTabPlacement", "setMainTabPlacement");
			s_dscrs[18] = new PropertyDescriptor(IPropNames.OBJECT_TAB_PLACEMENT,
									CLAZZ, "getObjectTabPlacement", "setObjectTabPlacement");
			s_dscrs[19] = new PropertyDescriptor(IPropNames.SQL_EXECUTION_TAB_PLACEMENT,
									CLAZZ, "getSQLExecutionTabPlacement", "setSQLExecutionTabPlacement");
			s_dscrs[20] = new PropertyDescriptor(IPropNames.SQL_RESULTS_TAB_PLACEMENT,
									CLAZZ, "getSQLResultsTabPlacement", "setSQLResultsTabPlacement");
			s_dscrs[21] = new PropertyDescriptor(IPropNames.TABLE_CONTENTS_OUTPUT_CLASS_NAME,
									CLAZZ, "getTableContentsOutputClassName", "setTableContentsOutputClassName");
			s_dscrs[22] = new PropertyDescriptor(IPropNames.ABORT_ON_ERROR,
									CLAZZ, "getAbortOnError", "setAbortOnError");
			s_dscrs[23] = new PropertyDescriptor(IPropNames.SCHEMA_PREFIX_LIST,
									CLAZZ, "getSchemaPrefixList", "setSchemaPrefixList");
			s_dscrs[24] = new PropertyDescriptor(IPropNames.LOAD_SCHEMAS_CATALOGS,
									CLAZZ, "getLoadSchemasCatalogs", "setLoadSchemasCatalogs");		
			s_dscrs[25] = new PropertyDescriptor(IPropNames.SHOW_RESULTS_META_DATA,
									CLAZZ, "getShowResultsMetaData", "setShowResultsMetaData");		
		}
	}

	public PropertyDescriptor[] getPropertyDescriptors()
	{
		return s_dscrs;
	}
}
