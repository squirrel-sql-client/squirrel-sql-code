package net.sourceforge.squirrel_sql.plugins.postgres.commands;
/*
* Copyright (C) 2007 Daniel Regli & Yannick Winiger
* http://sourceforge.net/projects/squirrel-sql
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

import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.client.session.ISession;

public abstract class AbstractPostgresCommand implements ICommand {
    /**
     * Current session.
     */
    protected ISession _session;


    public AbstractPostgresCommand(ISession session) {
        if (session == null) throw new IllegalArgumentException("ISession cannot be null");
        _session = session;
    }


    /**
     * The subclass should implement this so that getSQLStatements can generate the actual statements.
     *
     * @return the sql statements
     */
    protected abstract String[] generateSQLStatements();


    /**
     * Adds a new task to the ThreadPool that generates the SQL statements and
     * notifies the listener when the statements are ready.
     *
     * @param listener the listener to notify when the statments are ready
     */
    protected void getSQLStatements(final SQLResultListener listener) {
        _session.getApplication().getThreadPool().addTask(new Runnable() {
            public void run() {
                listener.finished(generateSQLStatements());
            }
        });
    }
}
