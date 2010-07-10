package net.sourceforge.squirrel_sql.plugins.sessionscript;
/*
 * Copyright (C) 2002 Colin Bell
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
import java.util.Iterator;

import javax.swing.JComboBox;

import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;

import net.sourceforge.squirrel_sql.client.IApplication;

/**
 * This <TT>JComboBox</TT> will display all aliases.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SQLALiasesCombo extends JComboBox
{
	/**
	 * Default ctor. Builds an empty combo box.
	 */
	public SQLALiasesCombo()
	{
		super();
	}

	public ISQLAlias getSelectedSQLAlias()
	{
		return (ISQLAlias)getSelectedItem();
	}

	/**
	 * Load control with all the aliases in the system.
	 * 
	 * @param	conn	<TT>app</TT>  Application API.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if a <TT>null</TT> <TT>IApplication</TT> passed.
	 * 
	 * @throws	BaseSQLException
	 * 			Thrown if an SQL exception occurs.
	 */
	public void load(IApplication app)
	{
		removeAllItems();
		for (Iterator it = app.getDataCache().aliases(); it.hasNext();)
		{
			ISQLAlias alias = (ISQLAlias)it.next();
			addItem(alias);
		}
	}
}
