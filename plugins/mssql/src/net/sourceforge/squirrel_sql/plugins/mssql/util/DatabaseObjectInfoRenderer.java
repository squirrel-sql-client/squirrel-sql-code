package net.sourceforge.squirrel_sql.plugins.mssql.util;

/*
 * Copyright (C) 2004 Ryan Walberg <generalpf@yahoo.com>
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

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.plugins.mssql.MssqlPlugin;

public class DatabaseObjectInfoRenderer extends DefaultTableCellRenderer {
    
    private static final long serialVersionUID = 1L;

    /** Creates a new instance of DatabaseObjectInfoRenderer */
    public DatabaseObjectInfoRenderer() {
    }
    
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        
        super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);
        
        if (value instanceof IDatabaseObjectInfo) {
            IDatabaseObjectInfo oi = (IDatabaseObjectInfo) value;

            String gif;
            String simpleName = oi.getSimpleName();
            
            int mssqlType = MssqlIntrospector.getObjectInfoType(oi);
            switch (mssqlType) {
                case MssqlIntrospector.MSSQL_TABLE:
                    gif = "properties.gif";
                    break;
                case MssqlIntrospector.MSSQL_VIEW:
                    gif = "arraypartition_obj.gif";
                    break;
                case MssqlIntrospector.MSSQL_STOREDPROCEDURE:
                    simpleName = simpleName.replaceAll(";0","");
                    gif = "thread_view.gif";
                    break;
                case MssqlIntrospector.MSSQL_UDF:
                    simpleName = simpleName.replaceAll(";1","");
                    gif = "variable_tab.gif";
                    break;
                case MssqlIntrospector.MSSQL_UDT:
                    gif = "type.gif";
                    break;
                default:
                    gif = "error_co.gif";
            }
            
            java.net.URL url = MssqlPlugin.class.getResource("resources/icons/eclipse/" + gif);
            if (url != null) {
                setText(simpleName);
                setIcon(new ImageIcon(url,oi.getDatabaseObjectType().toString()));
                return this;
            }
            else
                return null;
        }
        else
            return null;
    }
    
}
