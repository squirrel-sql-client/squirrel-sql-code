package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table;
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
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
/**
 * This tab shows the column privilege info for the currently selected table.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ColumnPriviligesTab extends BaseTableTab
{
    /** Internationalized strings for this class. */
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(ColumnPriviligesTab.class);	
	
    private int[] columnIndices = new int[] { 4, 6, 7, 5, 8 };
    
	/**
	 * Return the title for the tab.
	 *
	 * @return	The title for the tab.
	 */
	public String getTitle()
	{
		//i18n[ColumnPriviligesTab.title=Column Privileges]
		return s_stringMgr.getString("ColumnPriviligesTab.title");
	}

	/**
	 * Return the hint for the tab.
	 *
	 * @return	The hint for the tab.
	 */
	public String getHint()
	{
		//i18n[ColumnPriviligesTab.hint=Show access rights for columns in the selected table] 
		return s_stringMgr.getString("ColumnPriviligesTab.hint");
	}

	/**
	 * Create the <TT>IDataSet</TT> to be displayed in this tab.
	 */
	protected IDataSet createDataSet() throws DataSetException
	{
		final SQLDatabaseMetaData md = 
            getSession().getSQLConnection().getSQLMetaData();
        final ITableInfo ti = getTableInfo();
        return md.getColumnPrivilegesDataSet(ti,columnIndices, true);
	}
}
