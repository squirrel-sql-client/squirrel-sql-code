package net.sourceforge.squirrel_sql.fw.gui;
/*
 * Copyright (C) 2002 Colin Bell
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JComboBox;

import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;

/**
 * This <TT>JComboBox</TT> will display all the catalogs
 * in an SQL connection.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
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
	 * Set the <TT>SQLConnection</TT> for this control. Clear control
	 * and place all the catalog names from the connection in it in alphabetic
	 * sequence. Select the first catalog.
	 * 
	 * @param	conn	<TT>SQLConnection</TT>  to retrieve catalog names from.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if a <TT>null</TT> <TT>SQLConnection</TT> passed.
	 * 
	 * @throws	SQLException
	 * 			Thrown if an SQL exception occurs.
	 */
	public void setConnection(SQLConnection conn) throws SQLException
	{
		if (conn == null)
		{
			throw new IllegalArgumentException("SQLConnection == null");
		}

		super.removeAllItems();
		SQLDatabaseMetaData md = conn.getSQLMetaData();
		if (md.supportsCatalogs())
		{
			String[] catalogs = md.getCatalogs();
			if (catalogs != null)
			{
				Map map = new HashMap();
				for (int i = 0; i < catalogs.length; ++i)
				{
					map.put(catalogs[i], catalogs[i]);
				}
				for (Iterator it = map.values().iterator(); it.hasNext();)
				{
					addItem(it.next());
				}
			}
		}

		setMaximumSize(getPreferredSize());
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
