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

	public boolean isCopyFinished()
	{
		return copyFinished;
	}

	@Override
	public void analyzingTable(TableEvent e)
	{
	}

	@Override
	public void copyFinished(int seconds)
	{
		copyFinished = true;
	}

	@Override
	public void copyStarted(CopyEvent e)
	{
	}

	@Override
	public void handleError(ErrorEvent e)
	{
		System.err.println("Encountered the following exception: " + e.getException().getMessage());
		e.getException().printStackTrace();
		copyFinished = true;
	}

	@Override
	public void recordCopied(RecordEvent e)
	{
		System.out.println("Copied " + e.toString());
	}

	@Override
	public void statementExecuted(StatementEvent e)
	{
		System.out.println("Executed " + e.toString());
	}

	@Override
	public void tableAnalysisStarted(AnalysisEvent e)
	{
	}

	@Override
	public void tableCopyFinished(TableEvent e)
	{
	}

	@Override
	public void tableCopyStarted(TableEvent e)
	{
	}

}
