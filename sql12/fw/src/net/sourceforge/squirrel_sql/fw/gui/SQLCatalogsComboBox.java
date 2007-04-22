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
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JComboBox;

import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
/**
 * This <TT>JComboBox</TT> will display all the catalogs
 * in an SQL connection.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SQLCatalogsComboBox extends JComboBox
{
	/**
	 * Default ctor. Builds an empty combo box.
	 */
	public SQLCatalogsComboBox()
	{
		super();
	}

    /**
     * Sets the catalogs that should appear in the catalog drop-down menu.
     * Clear control and places all the catalog names from the list in it in 
     * alphabetic sequence. Selects the specified catalog; If the selectedCatalog
     * is null, then selects the first catalog.
     * 
     * @param catalogs an array of catalogs names that should in the catalogs
     *        drop-down menu.
     *         
     * @param selectedCatalog the catalog for the current connection that should
     *        be selected.
     */
    public void setCatalogs(String[] catalogs, String selectedCatalog) {
        super.removeAllItems();
        if (catalogs != null)
        {
            final Map map = new TreeMap();
            for (int i = 0; i < catalogs.length; ++i)
            {
                map.put(catalogs[i], catalogs[i]);
            }
            for (Iterator it = map.values().iterator(); it.hasNext();)
            {
                addItem(it.next());
            }
            if (selectedCatalog != null)
            {
                setSelectedCatalog(selectedCatalog);
            }
        }
        setMaximumSize(getPreferredSize());
        
    }
    
	/**
	 * Set the <TT>SQLConnection</TT> for this control. Clear control
	 * and place all the catalog names from the connection in it in alphabetic
	 * sequence. Select the first catalog.
	 *
	 * @param	conn	<TT>SQLConnection</TT> to retrieve catalog names from.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if a <TT>null</TT> <TT>SQLConnection</TT> passed.
	 *
	 * @throws	SQLException
	 * 			Thrown if an SQL exception occurs.
     * 
     * @deprecated This method has been deprecated because the view should not
     *             have direct access to the model.  Use the setCatalogs method
     *             instead.
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
		return (String) getSelectedItem();
	}

	public void setSelectedCatalog(String selectedCatalog)
	{
		if (selectedCatalog != null)
		{
			getModel().setSelectedItem(selectedCatalog);
		}
	}
}
