package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.database;
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
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BaseDataSetTab;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.MetaDataDecoratorDataSet;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

/**
 * This is the tab displaying database metadata.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class MetaDataTab extends BaseDataSetTab
{
    
    /** Internationalized strings for this class. */
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(MetaDataTab.class);  

	/**
	 * Return the title for the tab.
	 *
	 * @return	The title for the tab.
	 */
	public String getTitle()
	{
        // i18n[MetaDataTab.title=Metadata]
		return s_stringMgr.getString("MetaDataTab.title");
	}

	/**
	 * Return the hint for the tab.
	 *
	 * @return	The hint for the tab.
	 */
	public String getHint()
	{
        // i18n[MetaDataTab.hint=Show database metadata]
		return s_stringMgr.getString("MetaDataTab.hint");
	}

	/**
	 * Create the <TT>IDataSet</TT> to be displayed in this tab.
	 */
	protected IDataSet createDataSet() throws DataSetException
	{
		final ISQLConnection conn = getSession().getSQLConnection();
		try
		{
            DatabaseMetaData md = conn.getSQLMetaData().getJDBCMetaData();
			return new MetaDataDecoratorDataSet(md, getSession().getDriver().getDriverClassName(), getSession().getDriver().getJarFileNames());
		}
		catch (SQLException ex)
		{
			throw new DataSetException(ex);
		}
	}
}
