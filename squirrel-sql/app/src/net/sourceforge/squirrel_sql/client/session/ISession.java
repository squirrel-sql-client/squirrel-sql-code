package net.sourceforge.squirrel_sql.client.session;
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
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IHasIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.session.properties.SessionSheetProperties;
/**
 * The current session.
 */
public interface ISession extends IHasIdentifier {
    IApplication getApplication();
    SQLConnection getSQLConnection();
    ISQLDriver getDriver();
    ISQLAlias getAlias();
    SessionSheetProperties getProperties();
    void closeSQLConnection() throws SQLException;

    Object getPluginObject(IPlugin plugin, String key);
    Object putPluginObject(IPlugin plugin, String key, Object obj);

    void setMessageHandler(IMessageHandler handler);
    IMessageHandler getMessageHandler();

    String getSQLScript();
    void setSQLScript(String sqlScript);

    void setSessionSheet(SessionSheet child);
}
