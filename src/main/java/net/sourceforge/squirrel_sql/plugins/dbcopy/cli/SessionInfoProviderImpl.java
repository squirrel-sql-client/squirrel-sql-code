/*
 * Copyright (C) 2011 Rob Manning
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

package net.sourceforge.squirrel_sql.plugins.dbcopy.cli;

import java.util.List;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.plugins.dbcopy.SessionInfoProvider;

public class SessionInfoProviderImpl implements SessionInfoProvider
{

	private ISession sourceSession = null;
	private ISession destSession = null;
	private IDatabaseObjectInfo destSelectedDatabaseObject = null;
	private List<IDatabaseObjectInfo> sourceSelectedDatabaseObjects = null;
	
	
	
	@Override
	public ISession getDestSession()
	{
		return destSession;
	}

	@Override
	public ISession getSourceSession()
	{
		return sourceSession;
	}

	@Override
	public IDatabaseObjectInfo getDestDatabaseObject()
	{	
		return destSelectedDatabaseObject;
	}

	@Override
	public List<IDatabaseObjectInfo> getSourceDatabaseObjects()
	{	
		return sourceSelectedDatabaseObjects;
	}

	@Override
	public void setSourceSession(ISession session)
	{
		this.sourceSession = session;
	}

	@Override
	public void setDestSession(ISession session)
	{
		this.destSession = session;
	}

	@Override
	public void setDestDatabaseObject(IDatabaseObjectInfo info)
	{
		this.destSelectedDatabaseObject = info;
	}

	@Override
	public void setSourceDatabaseObjects(List<IDatabaseObjectInfo> dbObjList)
	{
		this.sourceSelectedDatabaseObjects = dbObjList;
	}

}
