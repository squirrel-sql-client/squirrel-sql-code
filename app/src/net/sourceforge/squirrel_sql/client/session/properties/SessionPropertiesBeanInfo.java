package net.sourceforge.squirrel_sql.client.session.properties;
/*
 * Copyright (C) 2001-2002 Colin Bell
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
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SessionPropertiesBeanInfo extends SimpleBeanInfo
{
	private static PropertyDescriptor[] s_dscrs;
	private static Class cls = SessionProperties.class;

	public SessionPropertiesBeanInfo() throws IntrospectionException
	{
		super();
		if (s_dscrs == null)
		{
			s_dscrs = new PropertyDescriptor[14];
			int i = 0;
			s_dscrs[i++] =
				new PropertyDescriptor(
					SessionProperties.IPropertyNames.AUTO_COMMIT,
					cls,
					"getAutoCommit",
					"setAutoCommit");
			s_dscrs[i++] =
				new PropertyDescriptor(
					SessionProperties.IPropertyNames.COMMIT_ON_CLOSING_CONNECTION,
					cls,
					"getCommitOnClosingConnection",
					"setCommitOnClosingConnection");
			s_dscrs[i++] =
				new PropertyDescriptor(
					SessionProperties.IPropertyNames.CONTENTS_LIMIT_ROWS,
					cls,
					"getContentsLimitRows",
					"setContentsLimitRows");
			s_dscrs[i++] =
				new PropertyDescriptor(
					SessionProperties.IPropertyNames.CONTENTS_NBR_ROWS_TO_SHOW,
					cls,
					"getContentsNbrRowsToShow",
					"setContentsNbrRowsToShow");
			s_dscrs[i++] =
				new PropertyDescriptor(
					SessionProperties.IPropertyNames.FONT_INFO,
					cls,
					"getFontInfo",
					"setFontInfo");
			s_dscrs[i++] =
				new PropertyDescriptor(
					SessionProperties.IPropertyNames.META_DATA_OUTPUT_CLASS_NAME,
					cls,
					"getMetaDataOutputClassName",
					"setMetaDataOutputClassName");
			s_dscrs[i++] =
				new PropertyDescriptor(
					SessionProperties.IPropertyNames.SHOW_ROW_COUNT,
					cls,
					"getShowRowCount",
					"setShowRowCount");
			s_dscrs[i++] =
				new PropertyDescriptor(
					SessionProperties.IPropertyNames.SHOW_TOOL_BAR,
					cls,
					"getShowToolBar",
					"setShowToolBar");
			s_dscrs[i++] =
				new PropertyDescriptor(
					SessionProperties.IPropertyNames.SQL_LIMIT_ROWS,
					cls,
					"getSQLLimitRows",
					"setSQLLimitRows");
			s_dscrs[i++] =
				new PropertyDescriptor(
					SessionProperties.IPropertyNames.SQL_NBR_ROWS_TO_SHOW,
					cls,
					"getSQLNbrRowsToShow",
					"setSQLNbrRowsToShow");
			s_dscrs[i++] =
				new PropertyDescriptor(
					SessionProperties.IPropertyNames.SQL_STATEMENT_SEPARATOR,
					cls,
					"getSQLStatementSeparatorChar",
					"setSQLStatementSeparatorChar");
			s_dscrs[i++] =
				new PropertyDescriptor(
					SessionProperties.IPropertyNames.SQL_RESULTS_OUTPUT_CLASS_NAME,
					cls,
					"getSQLResultsOutputClassName",
					"setSQLResultsOutputClassName");
			s_dscrs[i++] =
				new PropertyDescriptor(
					SessionProperties.IPropertyNames.LARGE_RESULT_SET_OBJECT_INFO,
					cls,
					"getLargeResultSetObjectInfo",
					"setLargeResultSetObjectInfo");
			s_dscrs[i++] =
				new PropertyDescriptor(
					SessionProperties.IPropertyNames.SQL_START_OF_LINE_COMMENT,
					cls,
					"getStartOfLineComment",
					"setStartOfLineComment");
		}
	}

	public PropertyDescriptor[] getPropertyDescriptors()
	{
		return s_dscrs;
	}
}