package net.sourceforge.squirrel_sql.client.session.objectstree.procedurepanel;
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
import net.sourceforge.squirrel_sql.fw.sql.IProcedureInfo;

import net.sourceforge.squirrel_sql.client.session.objectstree.objectpanel.IObjectPanelTab;

/**
 * This interface defines the behaviour for a tab in <TT>ProcedurePanel</TT>, the
 * panel displayed when a table is selected in the object tree.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public interface IProcedurePanelTab extends IObjectPanelTab {
    /**
     * Set the <TT>IProcedureInfo</TT> object that specifies the table that
     * is to have its information displayed.
     *
     * @param    value  <TT>IProcedureInfo</TT> object that specifies the currently
     *                  selected procedure. This can be <TT>null</TT>.
     */
    void setProcedureInfo(IProcedureInfo value);
}

