package net.sourceforge.squirrel_sql.client.session.objectstree.tablepanel;
/*
 * Copyright (C) 2001 Colin Bell
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
import java.awt.Component;

import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;

import net.sourceforge.squirrel_sql.client.session.ISession;

/**
 * This interface defines the behaviour for a tab in <TT>TablePane</TT>, the
 * panel displayed when a table is selected in the object tree.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public interface ITablePanelTab {
    /**
     * Return the title for the tab.
     *
     * @return    The title for the tab.
     */
    String getTitle();

    /**
     * Return the hint for the tab.
     *
     * @return    The hint for the tab.
     */
    String getHint();

    /**
     * Return the component to be displayed in the panel.
     *
     * @return    The component to be displayed in the panel.
     */
    Component getComponent();

    /**
     * Set the current session.
     *
     * @param    session        Current session.
     */
    void setSession(ISession session);

    /**
     * Set the <TT>ITableInfo</TT> object that specifies the table that
     * is to have its information displayed.
     *
     * @param    value  <TT>ITableInfo</TT> object that specifies the currently
     *                  selected table. This can be <TT>null</TT>.
     */
    void setTableInfo(ITableInfo value);

    /**
     * This tab has been selected.
     */
    void select();
}