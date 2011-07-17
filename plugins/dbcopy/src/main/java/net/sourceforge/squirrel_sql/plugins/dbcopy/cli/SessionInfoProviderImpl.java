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
