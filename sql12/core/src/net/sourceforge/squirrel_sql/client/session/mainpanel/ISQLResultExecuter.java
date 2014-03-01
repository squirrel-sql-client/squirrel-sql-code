package net.sourceforge.squirrel_sql.client.session.mainpanel;
/*
 * Copyright (C) 2003 Jason Height
 * jmheight@users.sourceforge.net
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
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
/**
 * This is the interface that can store executors for SQL.
 *
 */
public interface ISQLResultExecuter
{

   /** Returns the title of this executor.*/
	public String getTitle();

	public JComponent getComponent();

	public void execute(ISQLEntryPanel parent);
    
    /**
     * Returns the currently selected ResultTab.
     * @return
     */
    public IResultTab getSelectedResultTab();
}