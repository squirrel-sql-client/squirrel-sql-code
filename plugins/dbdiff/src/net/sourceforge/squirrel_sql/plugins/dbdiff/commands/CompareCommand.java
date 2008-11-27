package net.sourceforge.squirrel_sql.plugins.dbdiff.commands;

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
import net.sourceforge.squirrel_sql.plugins.dbdiff.DiffExecutor;
import net.sourceforge.squirrel_sql.plugins.dbdiff.SessionInfoProvider;

/** 
 * This class represents the command that gets executed when the user clicks 
 * compare in a schema after selecting one or more tables.
 */
public class CompareCommand implements ICommand
{
    
    /** the class that does the work of copying */
    private DiffExecutor executor = null;
        
    /**
     * Constructor specifying the current session.
     */
    public CompareCommand(SessionInfoProvider provider)
    {
        super();
        executor = new DiffExecutor(provider);
    }

    // ICommand Interface implementation
    
    /**
     * Kicks off the diff operation.  All pieces of information are provided
     * by the SessionInfoProvider and have been verified in the action prior 
     * to this point.  Nothing left to do except start the copy operation.
     */
    public void execute() {
        executor.execute();        
    }
}
