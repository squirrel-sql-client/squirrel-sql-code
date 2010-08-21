package net.sourceforge.squirrel_sql.fw.gui;
/*
 * Copyright (C) 2002-2003 Colin Bell
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
import java.sql.SQLException;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JComboBox;

import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
/**
 * This <TT>JComboBox</TT> will display all the catalogs
 * in an SQL connection.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SQLCatalogsComboBox extends JComboBox
{
    private static final long serialVersionUID = 1L;

 	/** Internationalized strings for this class. */
 	private static final StringManager s_stringMgr =
 		StringManagerFactory.getStringManager(SQLCatalogsComboBox.class);
 	
 	private interface i18n {
 		// i18n[SQLCatalogsComboBox.noneLabel=None]
 		String NONE_LABEL = s_stringMgr.getString("SQLCatalogsComboBox.noneLabel");
 	}
 	
    /**
	 * Default ctor. Builds an empty combo box.
	 */
	public SQLCatalogsComboBox()
	{
		super();
	}

   /**
	 * Sets the catalogs that should appear in the catalog drop-down menu. Clear control and places all the
	 * catalog names from the list in it in alphabetic sequence. Selects the specified catalog; If the
	 * selectedCatalog is null, then selects the first catalog.
	 * 
	 * @param catalogs
	 *           an array of catalogs names that should in the catalogs drop-down menu.
	 * @param selectedCatalog
	 *           the catalog for the current connection that should be selected.
	 */
   public void setCatalogs(String[] catalogs, String selectedCatalog)
	{
		super.removeAllItems();
		if (catalogs != null)
		{
			final Map<String, String> map = new TreeMap<String, String>();
			for (String catalog : catalogs)
			{
				if (!isEmptyCatalog(catalog))
				{
					map.put(catalog, catalog);
				}
			}
			if (isEmptyCatalog(selectedCatalog))
			{
				addItem(new NoCatalogPlaceHolder());
			}
			for (String catalog : map.values())
			{
				addItem(catalog);
			}
			if (!isEmptyCatalog(selectedCatalog))
			{
				setSelectedCatalog(selectedCatalog);
			}
		}
		setMaximumSize(getPreferredSize());
	}
    
    private boolean isEmptyCatalog(String catalog) {
   	 return catalog == null || "".equals(catalog);
    }
    
	/**
	 * Set the <TT>SQLConnection</TT> for this control. Clear control and place all the catalog names from
	 * the connection in it in alphabetic sequence. Select the first catalog.
	 * 
	 * @param conn
	 *           <TT>SQLConnection</TT> to retrieve catalog names from.
	 * @throws IllegalArgumentException
	 *            Thrown if a <TT>null</TT> <TT>SQLConnection</TT> passed.
	 * @throws SQLException
	 *            Thrown if an SQL exception occurs.
	 * @deprecated This method has been deprecated because the view should not have direct access to the model.
	 *             Use the setCatalogs method instead.
	 */
	public void setConnection(ISQLConnection conn) throws SQLException
	{
		if (conn == null)
		{
			throw new IllegalArgumentException("SQLConnection == null");
		}
		final SQLDatabaseMetaData md = conn.getSQLMetaData();
		if (md.supportsCatalogs())
		{
			final String[] catalogs = md.getCatalogs();
			if (catalogs != null)
			{
				setCatalogs(catalogs, conn.getCatalog());
			}
		}
	}

	public String getSelectedCatalog()
	{
		return getSelectedItem().toString();
	}
		
	public void setSelectedCatalog(String selectedCatalog)
	{
		if (selectedCatalog != null)
		{
			getModel().setSelectedItem(selectedCatalog);
		}
	}
	
	/**
	 * @see javax.swing.JComboBox#setSelectedItem(java.lang.Object)
	 */
	@Override
	public void setSelectedItem(Object o) {
		super.setSelectedItem(o);
		// If the "None" place-holder is in the list in the first position, remove it.  It is not possible to 
		// select the "None" place-holder upon startup, because it is already selected in the list if it is 
		// present.
		if (super.getItemAt(0) instanceof NoCatalogPlaceHolder) {
			super.removeItemAt(0);
			validate();
		}		
	}	
	
	/**
	 * A place holder for the label "None" which is only intended to appear at startup if: 
	 * 
	 * 	1) the database uses catalogs and 
	 *    2) it supports connections where the catalog is not specified.  
	 * 
	 * We want to allow the user to switch to any other catalog when in this state. The selected catalog by
	 * default is the first item in the list. However, it is confusing to have that be an actual catalog, when
	 * in fact the user did not specify one in the connection URL. So, when we connect, if the driver says that
	 * the catalog is null, this place-holder is created to take the first position in the combobox to allow
	 * the user to choose any other "real" catalog, and when they do, this place-holder gets removed, since it
	 * is no longer needed at that point.
	 * 
	 * Note: This placeholder allows us to do instanceof instead of a string comparison, which would prevent 
	 * the user from ever having and using a catalog called "None" (or whatever the equivalent i18n message 
	 * label is for their internationalized strings)
	 * 
	 * @author manningr
	 */
	private class NoCatalogPlaceHolder {
		public String toString() { 
			return i18n.NONE_LABEL; 
		}
	}
}
