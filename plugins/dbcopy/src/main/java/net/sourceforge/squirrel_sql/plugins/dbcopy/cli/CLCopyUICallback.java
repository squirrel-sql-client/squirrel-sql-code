package net.sourceforge.squirrel_sql.plugins.dbcopy.cli;

import net.sourceforge.squirrel_sql.fw.dialects.UserCancelledOperationException;
import net.sourceforge.squirrel_sql.plugins.dbcopy.UICallbacks;

public class CLCopyUICallback implements UICallbacks
{

	@Override
	public boolean appendRecordsToExisting(String tableName) throws UserCancelledOperationException
	{
		return false;
	}

	@Override
	public boolean deleteTableData(String tableName) throws UserCancelledOperationException
	{
		return true;
	}

}
