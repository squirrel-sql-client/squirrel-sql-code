package net.sourceforge.squirrel_sql.client.session;
/*
 * Copyright (C) 2001 Johan Compagner
 * jcompagner@j-com.nl
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
import java.awt.Container;

public class ResultFrame extends javax.swing.JInternalFrame
{
    private ResultTab _tab;

    /**
     * JInternalResultFrame constructor comment.
     */
    public ResultFrame(ResultTab tab)
    {
        super(tab.getSqlString(),true,true,true,true);
        _tab = tab;
        Container cont = getContentPane();
        cont.setLayout(new java.awt.BorderLayout());
        cont.add(tab.getOutputComponent(), java.awt.BorderLayout.CENTER);
    }

    /**
     * Close this window.
     */
    public void dispose()
    {
        _tab.closeTab();
        super.dispose();
    }
}