/*
 * Copyright (C) 2005 Rob Manning
 * manningr@users.sourceforge.net
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
package net.sourceforge.squirrel_sql.plugins.dbcopy;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.plugins.dbcopy.event.CopyEvent;
import net.sourceforge.squirrel_sql.plugins.dbcopy.event.CopyTableAdaptor;
import net.sourceforge.squirrel_sql.plugins.dbcopy.event.CopyTableListener;
import net.sourceforge.squirrel_sql.plugins.dbcopy.event.StatementEvent;
import net.sourceforge.squirrel_sql.plugins.dbcopy.util.ScriptWriter;

/**
 * This class listens for copy table events that represent SQL statements that
 * should be recorded in the script file if so configured.
 */
public class CopyScripter extends CopyTableAdaptor implements CopyTableListener {

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.plugins.dbcopy.event.CopyTableListener#copyStarted(net.sourceforge.squirrel_sql.plugins.dbcopy.event.CopyEvent)
     */
    public void copyStarted(CopyEvent e) {
        initializeScript(e.getSessionInfoProvider());
    }    

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.plugins.dbcopy.event.CopyTableListener#statementExecuted(net.sourceforge.squirrel_sql.plugins.dbcopy.event.StatementEvent)
     */
    public void statementExecuted(StatementEvent e) {
        String sql = e.getStatement();
        if (e.getStatementType() == StatementEvent.INSERT_RECORD_TYPE) {
            String[] values = e.getBindValues();
            ScriptWriter.write(sql, values);
        } else {
            ScriptWriter.write(sql);
        }
    }
    
    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.plugins.dbcopy.event.CopyTableListener#copyFinished(int)
     */
    public void copyFinished(int seconds) {
        finalizeScript();
    }

    /**
     * Open the script.
     * @param prov
     */
    private void initializeScript(SessionInfoProvider prov) {
        ISession source = prov.getSourceSession();
        ISession dest = prov.getDestSession();
        ScriptWriter.open(source,dest);
    }
    
    /**
     * close the script.
     */
    private void finalizeScript() {
        ScriptWriter.close();
    }
}
