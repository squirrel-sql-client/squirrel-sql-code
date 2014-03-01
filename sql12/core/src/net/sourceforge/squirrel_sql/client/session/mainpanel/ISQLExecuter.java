package net.sourceforge.squirrel_sql.client.session.mainpanel;
/*
 * Copyright (C) 2001-2003 Colin Bell
 * colbell@users.sourceforge.net
 *
 * Modifications Copyright (C) 2001-2002 Johan Compagner
 * jcompagner@j-com.nl
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

import javax.swing.JComponent;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;

/**
 * This is the interface that can store executors for SQL.
 *
 */
@Deprecated
public interface ISQLExecuter
{
	/** Returns the title of this executor.*/
	public String getTitle();

	public JComponent getComponent();

	public void execute(ISQLEntryPanel parent);
}