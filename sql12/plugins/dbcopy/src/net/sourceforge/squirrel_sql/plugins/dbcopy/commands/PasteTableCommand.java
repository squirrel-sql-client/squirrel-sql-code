package net.sourceforge.squirrel_sql.plugins.dbcopy.commands;

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

import net.sourceforge.squirrel_sql.client.session.mainpanel.sqltypecheck.DataChangesAllowedCheck;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.plugins.dbcopy.CopyExecutor;
import net.sourceforge.squirrel_sql.plugins.dbcopy.CopyProgressMonitor;
import net.sourceforge.squirrel_sql.plugins.dbcopy.CopyScripter;
import net.sourceforge.squirrel_sql.plugins.dbcopy.I18NBaseObject;
import net.sourceforge.squirrel_sql.plugins.dbcopy.SessionInfoProvider;

/** 
 * This class represents the command that gets executed when the user clicks 
 * paste table in a schema after copying one or more tables.
 */
public class PasteTableCommand extends I18NBaseObject implements ICommand
{

    private final SessionInfoProvider _provider;

    /**
     * the class that does the work of copying
     */
    private CopyExecutor _executor = null;

    /**
     * the class that provides feedback to the user throught the copy operation
     */
    private CopyProgressMonitor _monitor = null;

    /**
     * the class that writes a script that represents the copy operation
     */
    private CopyScripter _copyScripter = null;
        
    /**
     * Constructor specifying the current session.
     */
    public PasteTableCommand(SessionInfoProvider provider)
    {
        _executor = new CopyExecutor(provider);
        _monitor = new CopyProgressMonitor(provider);
        _provider = provider;
        _copyScripter = new CopyScripter();
        _executor.addListener(_monitor);
        _executor.addListener(_copyScripter);
        _executor.setPref(_monitor);
        _monitor.setExecutor(_executor);
    }

    // ICommand Interface implementation
    
    /**
     * Kicks off the copy operation.  All pieces of information are provided
     * by the SessionInfoProvider and have been verified in the action prior 
     * to this point.  Nothing left to do except start the copy operation.
     */
    public void execute()
    {
        if(false == DataChangesAllowedCheck.checkDbCopyPaste(_provider.getDestSession()))
        {
            return;
        }

        _executor.execute();
    }
}
