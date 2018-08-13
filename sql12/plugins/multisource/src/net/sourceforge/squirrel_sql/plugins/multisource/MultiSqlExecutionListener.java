package net.sourceforge.squirrel_sql.plugins.multisource;

import net.sourceforge.squirrel_sql.client.session.event.SQLExecutionAdapter;
import net.sourceforge.squirrel_sql.fw.sql.QueryHolder;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;

/**
 * A ISQLExecutionListener that displays the SQL that will display a notification if the virtualization is run in trial mode.
 */
public class MultiSqlExecutionListener extends SQLExecutionAdapter
{
	/** This is what gives the ability to print a message to the message panel */
	private final IMessageHandler _messageHandler;
	
	public MultiSqlExecutionListener(IMessageHandler messageHandler) {
		_messageHandler = messageHandler;
	}

	@Override
	public void statementExecuted(QueryHolder sql) {
	}

	@Override
	public String statementExecuting(String sql) {
		return sql;
	}

   public void executionFinished() {
	   if (MultiSourcePlugin.isTrial())	   
		   _messageHandler.showMessage("UnityJDBC Virtualization Driver is running in trial mode.  Results are limited to 100.  More info at: www.unityjdbc.com.");
   }
}
