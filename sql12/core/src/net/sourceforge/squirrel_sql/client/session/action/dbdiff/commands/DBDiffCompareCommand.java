/*
 * Copyright (C) 2005 Rob Manning
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

package net.sourceforge.squirrel_sql.client.session.action.dbdiff.commands;

import net.sourceforge.squirrel_sql.client.session.action.dbdiff.gui.IDiffPresentation;
import net.sourceforge.squirrel_sql.client.session.action.dbdiff.gui.IDiffPresentationFactory;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

/**
 * This class represents the command that gets executed when the user clicks compare in a schema after
 * selecting one or more tables.
 */
public class DBDiffCompareCommand implements ICommand
{

	/** the class that does the work of copying */
	private IDiffPresentationFactory diffPresentationFactory = null;



	/**
	 * Kicks off the diff operation. All pieces of information are provided by the SessionInfoProvider and have
	 * been verified in the action prior to this point. Nothing left to do except start the copy operation.
    */
	public void execute()
	{
		final IDiffPresentation diffPresentation = diffPresentationFactory.createDiffPresentation();
		diffPresentation.execute();
	}

	/**
	 * @param diffPresentationFactory
	 *           the diffPresentationFactory to set
	 */
	public void setDiffPresentationFactory(IDiffPresentationFactory diffPresentationFactory)
	{
		Utilities.checkNull("setDiffPresentationFactory", "diffPresentationFactory", diffPresentationFactory);
		this.diffPresentationFactory = diffPresentationFactory;
	}

}
