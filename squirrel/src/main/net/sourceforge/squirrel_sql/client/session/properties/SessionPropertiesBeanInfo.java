package net.sourceforge.squirrel_sql.client.session.properties;
/*
 * Copyright (C) 2001 Colin Bell
 * colbell@users.sourceforge.net
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
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

/**
 * This is the <CODE>BeanInfo</CODE> class for <CODE>SessionProperties</CODE>.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SessionPropertiesBeanInfo extends SimpleBeanInfo {

    private static PropertyDescriptor[] s_dscrs;
    private static Class cls = SessionProperties.class;

    public SessionPropertiesBeanInfo() throws IntrospectionException {
        super();
        if (s_dscrs == null) {
            s_dscrs = new PropertyDescriptor[22];
            int i = 0;
            s_dscrs[i++] = new PropertyDescriptor(SessionProperties.IPropertyNames.AUTO_COMMIT, cls,
                "getAutoCommit", "setAutoCommit");
            s_dscrs[i++] = new PropertyDescriptor(SessionProperties.IPropertyNames.COLUMNS_OUTPUT_CLASS_NAME, cls,
                "getColumnsOutputClassName", "setColumnsOutputClassName");
            s_dscrs[i++] = new PropertyDescriptor(SessionProperties.IPropertyNames.COLUMN_PRIVILIGES_OUTPUT_CLASS_NAME, cls,
                "getColumnPriviligesOutputClassName", "setColumnPriviligesOutputClassName");
            s_dscrs[i++] = new PropertyDescriptor(SessionProperties.IPropertyNames.COMMIT_ON_CLOSING_CONNECTION, cls,
                "getCommitOnClosingConnection", "setCommitOnClosingConnection");
            s_dscrs[i++] = new PropertyDescriptor(SessionProperties.IPropertyNames.CONTENTS_LIMIT_ROWS, cls,
                "getContentsLimitRows", "setContentsLimitRows");
            s_dscrs[i++] = new PropertyDescriptor(SessionProperties.IPropertyNames.CONTENTS_NBR_ROWS_TO_SHOW, cls,
                "getContentsNbrRowsToShow", "setContentsNbrRowsToShow");
            s_dscrs[i++] = new PropertyDescriptor(SessionProperties.IPropertyNames.CONTENTS_OUTPUT_CLASS_NAME, cls,
                "getContentsOutputClassName", "setContentsOutputClassName");
            s_dscrs[i++] = new PropertyDescriptor(SessionProperties.IPropertyNames.DATA_TYPES_OUTPUT_CLASS_NAME, cls,
                "getDataTypesOutputClassName", "setDataTypesOutputClassName");
            s_dscrs[i++] = new PropertyDescriptor(SessionProperties.IPropertyNames.EXP_KEYS_OUTPUT_CLASS_NAME, cls,
                "getExportedKeysOutputClassName", "setExportedKeysOutputClassName");
            s_dscrs[i++] = new PropertyDescriptor(SessionProperties.IPropertyNames.IMP_KEYS_OUTPUT_CLASS_NAME, cls,
                "getImportedKeysOutputClassName", "setImportedKeysOutputClassName");
            s_dscrs[i++] = new PropertyDescriptor(SessionProperties.IPropertyNames.INDEXES_OUTPUT_CLASS_NAME, cls,
                "getIndexesOutputClassName", "setIndexesOutputClassName");
            s_dscrs[i++] = new PropertyDescriptor(SessionProperties.IPropertyNames.META_DATA_OUTPUT_CLASS_NAME, cls,
                "getMetaDataOutputClassName", "setMetaDataOutputClassName");
            s_dscrs[i++] = new PropertyDescriptor(SessionProperties.IPropertyNames.PRIM_KEY_OUTPUT_CLASS_NAME, cls,
                "getPrimaryKeyOutputClassName", "setPrimaryKeyOutputClassName");
            s_dscrs[i++] = new PropertyDescriptor(SessionProperties.IPropertyNames.PRIVILIGES_OUTPUT_CLASS_NAME, cls,
                "getPriviligesOutputClassName", "setPriviligesOutputClassName");
            s_dscrs[i++] = new PropertyDescriptor(SessionProperties.IPropertyNames.ROWID_OUTPUT_CLASS_NAME, cls,
                "getRowIdOutputClassName", "setRowIdOutputClassName");
            s_dscrs[i++] = new PropertyDescriptor(SessionProperties.IPropertyNames.SHOW_ROW_COUNT, cls,
                "getRowIdOutputClassName", "setRowIdOutputClassName");
            s_dscrs[i++] = new PropertyDescriptor(SessionProperties.IPropertyNames.SQL_LIMIT_ROWS, cls,
                "getSqlLimitRows", "setSqlLimitRows");
            s_dscrs[i++] = new PropertyDescriptor(SessionProperties.IPropertyNames.SQL_REUSE_OUTPUT_TABS, cls,
                "getSqlReuseOutputTabs", "setSqlReuseOutputTabs");
            s_dscrs[i++] = new PropertyDescriptor(SessionProperties.IPropertyNames.SQL_NBR_ROWS_TO_SHOW, cls,
                "getSqlNbrRowsToShow", "setSqlNbrRowsToShow");
            s_dscrs[i++] = new PropertyDescriptor(SessionProperties.IPropertyNames.SQL_OUTPUT_CLASS_NAME, cls,
                "getShowRowCount", "setShowRowCount");
            s_dscrs[i++] = new PropertyDescriptor(SessionProperties.IPropertyNames.SQL_STATEMENT_SEPARATOR, cls,
                "getSqlStatementSeparatorChar", "setSqlStatementSeparatorChar");
            s_dscrs[i++] = new PropertyDescriptor(SessionProperties.IPropertyNames.TABLE_OUTPUT_CLASS_NAME, cls,
                "getTableOutputClassName", "setTableOutputClassName");
            s_dscrs[i++] = new PropertyDescriptor(SessionProperties.IPropertyNames.VERSIONS_OUTPUT_CLASS_NAME, cls,
                "getVersionsOutputClassName", "setVersionsOutputClassName");
        }
    }

    public PropertyDescriptor[] getPropertyDescriptors() {
        return s_dscrs;
    }
}

