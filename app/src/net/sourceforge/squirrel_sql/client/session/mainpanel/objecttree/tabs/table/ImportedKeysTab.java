package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;

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

/**
 * This tab shows the imported keys in the currently selected table.
 * 
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ImportedKeysTab extends ForeignKeysBaseTab
{

	@Override
	protected String getTitleKey()
	{
		return "ImportedKeysTab.title";
	}

	@Override
	protected String getHintKey()
	{
		return "ImportedKeysTab.hint";
	}

	@Override
	protected IDataSet getUnfilteredDataSet(SQLDatabaseMetaData md, ITableInfo tableInfo)
		throws DataSetException
	{
		return md.getImportedKeysDataSet(tableInfo);
	}
}
