/*
 * Copyright (C) 2002 Christian Sell
 * csell@users.sourceforge.net
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
 *
 * created by cse, 14.10.2002 10:40:42
 */
package net.sourceforge.squirrel_sql.plugins.jcomplete;

import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanelFactory;

/**
 * a plugin class for the JComplete simple text area plugin
 * @version $Id: JCompletePlugin.java,v 1.1 2002-10-14 19:13:56 csell Exp $
 */
public class JCompletePlugin extends DefaultSessionPlugin
{
    public static String JCOMPLETE_SQL_ENTRY_CONTROL = "sqlentry_control";

    /** Factory that creates text controls. */
    private ISQLEntryPanelFactory _panelFactory;

    public String getInternalName()
    {
        return "jcomplete";
    }

    public String getDescriptiveName()
    {
        return "JComplete SQL Entry Panel Plugin";
    }

    public String getAuthor()
    {
        return "Christian Sell";
    }

    public String getVersion()
    {
        return "$Revision: 1.1 $";
    }

    public void initialize() throws PluginException
    {
        super.initialize();

        // Install the panel factory for creating SQL entry text controls.
        _panelFactory = new JCompleteSQLEntryPanelFactory(this);
        getApplication().setSQLEntryPanelFactory(_panelFactory);
    }
}
