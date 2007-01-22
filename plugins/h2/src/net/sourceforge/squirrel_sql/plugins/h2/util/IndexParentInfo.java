package net.sourceforge.squirrel_sql.plugins.h2.util;
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
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.plugins.h2.IObjectTypes;

/**
 * This class is used to provide schema and table information to any child
 * index nodes.
 *
 * @author manningr
 */
public class IndexParentInfo extends DatabaseObjectInfo
{
    
    private final IDatabaseObjectInfo _tableInfo;
    
    public IndexParentInfo(IDatabaseObjectInfo tableInfo,
                           SQLDatabaseMetaData md)
    {
        super(tableInfo.getCatalogName(), 
              tableInfo.getSchemaName(),
              "INDEX",
              IObjectTypes.INDEX_PARENT,
              md);    
        _tableInfo = tableInfo;
    }
    
    public IDatabaseObjectInfo getTableInfo()
    {
        return _tableInfo;
    }
    
}
