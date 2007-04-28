package net.sourceforge.squirrel_sql.plugins.refactoring.commands;
/*
 * Copyright (C) 2007 Rob Manning
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


/**
 * This should be implemented by UI components that need to display/run SQL that
 * could potentially take a long time to build.  The caller can then return 
 * immediately to allow the UI to remain responsive, while an app thread builds
 * the SQL.  Finally, the method implementation will be called when the SQL 
 * result is built.  
 * 
 * @author manningr
 */
public interface SQLResultListener {

    /**
     * This is called to let the listener know when the SQL result is ready.
     * 
     * @param sql the SQL statements that resulted from a request.
     */
    void finished(String[] sql);
        
}
