package net.sourceforge.squirrel_sql.plugins.dbcopy.cli;

import net.sourceforge.squirrel_sql.plugins.dbcopy.event.AnalysisEvent;
import net.sourceforge.squirrel_sql.plugins.dbcopy.event.CopyEvent;
import net.sourceforge.squirrel_sql.plugins.dbcopy.event.CopyTableListener;
import net.sourceforge.squirrel_sql.plugins.dbcopy.event.ErrorEvent;
import net.sourceforge.squirrel_sql.plugins.dbcopy.event.RecordEvent;
import net.sourceforge.squirrel_sql.plugins.dbcopy.event.StatementEvent;
import net.sourceforge.squirrel_sql.plugins.dbcopy.event.TableEvent;

public class CLCopyListener implements CopyTableListener
{

	private boolean copyFinished = false;
	
	public boolean isCopyFinished() {
		return copyFinished;
	}
	
	@Override
	public void analyzingTable(TableEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void copyFinished(int seconds)
	{
		copyFinished = true;
	}

	@Override
	public void copyStarted(CopyEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void handleError(ErrorEvent e)
	{
		System.err.println("Encountered the following exception: "+e.getException().getMessage());
		e.getException().printStackTrace();
		copyFinished = true;
	}

	@Override
	public void recordCopied(RecordEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void statementExecuted(StatementEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void tableAnalysisStarted(AnalysisEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void tableCopyFinished(TableEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void tableCopyStarted(TableEvent e)
	{
		// TODO Auto-generated method stub

	}

}
