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

import java.util.List;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.TableInfo;

/**
 * This is implemented in order to pass needed info along to copy executor. 
 *
 */
public interface SessionInfoProvider {
    
    public void setSourceSession(ISession session);
    
    public ISession getSourceSession();
    
    public void setSourceDatabaseObjects(List<IDatabaseObjectInfo> dbObjList);
    
    public List<IDatabaseObjectInfo> getSourceDatabaseObjects();
    
    public IDatabaseObjectInfo getDestDatabaseObject();
    
    public void setDestDatabaseObject(IDatabaseObjectInfo info);
    
    public void setDestSession(ISession session);
    
    public ISession getDestSession();

    void setPasteToTableName(String pasteToTableName);

    String getPasteToTableName();

    TableInfo getPasteToTableInfo(ISQLConnection destConn, String destSchema, String destCatalog);

    boolean isCopiedFormDestinationSession();
}
