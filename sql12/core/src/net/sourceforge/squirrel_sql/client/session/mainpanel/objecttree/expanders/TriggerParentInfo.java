package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders;
/*
 * Copyright (C) 2006 Rob Manning
 * manningr@users.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;

/**
 * This class stores information about a Trigger parent. This just
 * stores info about the table that the trigger relates to.
 *
 * @author manningr
 */
public class TriggerParentInfo extends DatabaseObjectInfo
{
    public interface IPropertyNames {
        String SIMPLE_NAME = "simpleName";
        String TABLE_INFO = "tableInfo";
    }
    
	private final IDatabaseObjectInfo _tableInfo;

	public TriggerParentInfo(IDatabaseObjectInfo tableInfo, String schema,
								SQLDatabaseMetaData md)
		throws SQLException
	{
		super(tableInfo.getCatalogName(), 
              schema, 
              "TRIGGER", 
              DatabaseObjectType.TRIGGER_TYPE_DBO, 
              md);
		_tableInfo = tableInfo;
	}

	public IDatabaseObjectInfo getTableInfo()
	{
		return _tableInfo;
	}
}
