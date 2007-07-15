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
public class PasteTableCommand extends I18NBaseObject 
                                  implements ICommand
{
    
    /** the provider of information about what to copy and where */
    @SuppressWarnings("unused")
    private SessionInfoProvider prov = null;

    /** the class that does the work of copying */
    private CopyExecutor executor = null;
    
    /** the class that provides feedback to the user throught the copy operation */
    private CopyProgressMonitor monitor = null;
    
    /** the class that writes a script that represents the copy operation */
    private CopyScripter copyScripter = null;
        
    /**
     * Constructor specifying the current session.
     */
    public PasteTableCommand(SessionInfoProvider provider)
    {
        super();
        prov = provider;
        executor = new CopyExecutor(provider);
        monitor = new CopyProgressMonitor(provider);
        copyScripter = new CopyScripter();
        executor.addListener(monitor);
        executor.addListener(copyScripter);
        executor.setPref(monitor);
        monitor.setExecutor(executor);
        
    }

    // ICommand Interface implementation
    
    /**
     * Kicks off the copy operation.  All pieces of information are provided
     * by the SessionInfoProvider and have been verified in the action prior 
     * to this point.  Nothing left to do except start the copy operation.
     */
    public void execute() {
        executor.execute();        
    }
}
