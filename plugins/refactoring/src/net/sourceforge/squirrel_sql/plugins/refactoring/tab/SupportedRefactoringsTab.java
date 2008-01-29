package net.sourceforge.squirrel_sql.plugins.refactoring.tab;

/*
 * Copyright (C) 2008 Rob Manning
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
import java.lang.reflect.Method;
import java.util.HashMap;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BaseDataSetTab;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.MapDataSet;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * This tab shows the columns in the currently selected stored procedure.
 * 
 * @author manningr
 */
public class SupportedRefactoringsTab extends BaseDataSetTab
{
	/** Logger for this class. */
	private static final ILogger s_log = LoggerController.createLogger(SupportedRefactoringsTab.class);
	
	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(SupportedRefactoringsTab.class);

	/** map of refactorings and whether or not they are supported (true|false) */
	HashMap<String, String> refactorings = new HashMap<String, String>();

	/** the dataset build from the map of supported refactorings */
	MapDataSet dataSet = null;

	/** method name for supports rename view refactoring */
	private static final String SUPPORTS_RENAME_VIEW_METHOD_NAME = "supportsRenameView";
	
	private static final String SUPPORTS_VIEW_DEF_METHOD_NAME = "supportsViewDefinition";
	
	/**
	 * Constructor
	 * 
	 * @param session the session to check for supported refactorings.
	 */
	public SupportedRefactoringsTab(ISession session)
	{
		try {
			HibernateDialect dialect = DialectFactory.getDialect(session.getMetaData());
			Method[] methods = dialect.getClass().getMethods();
			for (Method method : methods)
			{
				if (isRefactoringSupportMethodName(method.getName()))
				{
						Boolean supported = (Boolean) method.invoke(dialect, (Object[])null);
						refactorings.put(method.getName(), supported.toString());
				}
			}
			if (refactorings.containsKey(SUPPORTS_RENAME_VIEW_METHOD_NAME) 
					&& refactorings.containsKey(SUPPORTS_VIEW_DEF_METHOD_NAME)) 
			{
				String supportsRenameView = refactorings.get(SUPPORTS_RENAME_VIEW_METHOD_NAME);
				String supportsViewDefinition = refactorings.get(SUPPORTS_VIEW_DEF_METHOD_NAME);
				
				if (supportsRenameView.equalsIgnoreCase("false") 
						&&  supportsViewDefinition.equalsIgnoreCase("true")) 
				{
					refactorings.put(SUPPORTS_RENAME_VIEW_METHOD_NAME, "true");
				}
				refactorings.remove(SUPPORTS_VIEW_DEF_METHOD_NAME);
			}
			dataSet = new MapDataSet(refactorings);
		} catch (Exception e)
		{
			s_log.error("SupportedRefactoringsTab.init: unexpected exception "+e.getMessage(), e);
		}
	}

	/**
	 * Since HibernateDialects contain many "supports..." methods that are not related to refactoring
	 * this method will discriminate them.
	 * 
	 * @param methodName the name of the method to check
	 * 
	 * @return true if the method is used by the refactoring plugin; false otherwise.
	 */
	private boolean isRefactoringSupportMethodName(String methodName)
	{

		if (methodName.startsWith("supportsAdd") || methodName.startsWith("supportsCreate")
			|| methodName.startsWith("supportsAlter") || methodName.startsWith("supportsDrop")
			|| methodName.startsWith("supportsRename") || methodName.equals("supportsViewDefinition"))
		{
			return true;
		}
		return false;
	}

	/**
	 * Return the title for the tab.
	 * 
	 * @return The title for the tab.
	 */
	public String getTitle()
	{
		// i18n[SupportedRefactoringsTab.title=Supported Refactorings]
		return s_stringMgr.getString("SupportedRefactoringsTab.title");
	}

	/**
	 * Return the hint for the tab.
	 * 
	 * @return The hint for the tab.
	 */
	public String getHint()
	{
		// i18n[SupportedRefactoringsTab.hint=Show refactorings that are supported by the plugin]
		return s_stringMgr.getString("SupportedRefactoringsTab.hint");
	}

	/**
	 * Create the <TT>IDataSet</TT> to be displayed in this tab.
	 */
	protected IDataSet createDataSet() throws DataSetException
	{
		return dataSet;
	}
}
