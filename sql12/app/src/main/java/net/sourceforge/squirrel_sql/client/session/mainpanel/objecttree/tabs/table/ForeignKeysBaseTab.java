/*
 * Copyright (C) 2009 Rob Manning
 * manningr@users.sourceforge.net
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
package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table;

import static java.sql.DatabaseMetaData.importedKeyCascade;
import static java.sql.DatabaseMetaData.importedKeyNoAction;
import static java.sql.DatabaseMetaData.importedKeyRestrict;
import static java.sql.DatabaseMetaData.importedKeySetDefault;
import static java.sql.DatabaseMetaData.importedKeySetNull;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.FilterDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

/**
 * Base class to provide common code for both the Imported and Exported keys tabs.
 *
 */
public abstract class ForeignKeysBaseTab extends BaseTableTab
{

	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ForeignKeysBaseTab.class);

	public ForeignKeysBaseTab()
	{
		super();
	}

	/**
	 * Return the title for the tab.
	 *
	 * @return	The title for the tab.
	 */
	public String getTitle()
	{
		return s_stringMgr.getString(getTitleKey());
	}

	protected abstract String getTitleKey();
	
	/**
	 * Return the hint for the tab.
	 *
	 * @return	The hint for the tab.
	 */
	public String getHint()
	{ 
		return s_stringMgr.getString(getHintKey());
	}
	
	protected abstract String getHintKey();

	/**
	 * Retrieve the <TT>IDataSet</TT> to be displayed in this tab.
	 * 
	 * @return	the <TT>IDataSet</TT> to be displayed in this tab.
	 */
	protected IDataSet createDataSet() throws DataSetException
	{
		// Provide the actual integer value, as well as it's meaning
		Map<Integer, Map<String,String>> replacements = new HashMap<Integer, Map<String,String>>(20);
		HashMap<String,String> replacementMap = new HashMap<String,String>();
		replacementMap.put(""+importedKeyCascade, importedKeyCascade + " (CASCADE)");
		replacementMap.put(""+importedKeyRestrict, importedKeyRestrict + " (RESTRICT)");
		replacementMap.put(""+importedKeySetNull, importedKeySetNull + " (SET NULL)");
		replacementMap.put(""+importedKeyNoAction, importedKeyNoAction + " (NO ACTION)");
		replacementMap.put(""+importedKeySetDefault, importedKeySetDefault + " (SET DEFAULT)");
		replacements.put(9, replacementMap);
		replacements.put(10, replacementMap);
		
		final ISQLConnection conn = getSession().getSQLConnection();
		
		IDataSet orig = getUnfilteredDataSet(conn.getSQLMetaData(), getTableInfo()); 
	   return new FilterDataSet(orig, replacements);
	}

	protected abstract IDataSet getUnfilteredDataSet(SQLDatabaseMetaData md, ITableInfo tableInfo)
		throws DataSetException;
		
	
}