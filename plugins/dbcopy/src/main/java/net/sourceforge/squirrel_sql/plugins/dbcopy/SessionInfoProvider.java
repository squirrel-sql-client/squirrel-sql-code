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
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;

/**
 * This is implemented in order to pass needed info along to copy executor. 
 *
 */
public interface SessionInfoProvider {
    
    public void setCopySourceSession(ISession session);
    
    public ISession getCopySourceSession();
    
    public IDatabaseObjectInfo[] getSourceSelectedDatabaseObjects();
    
    public IDatabaseObjectInfo getDestSelectedDatabaseObject();
    
    public void setDestSelectedDatabaseObject(IDatabaseObjectInfo info);
    
    public void setDestCopySession(ISession session);
    
    public ISession getCopyDestSession();
}
