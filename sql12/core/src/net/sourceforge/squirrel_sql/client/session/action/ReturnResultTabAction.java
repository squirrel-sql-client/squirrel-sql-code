package net.sourceforge.squirrel_sql.client.session.action;
/*
 * Copyright (C) 2001-2002 Colin Bell
 * colbell@users.sourceforge.net
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
import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.mainpanel.ResultFrame;

public class ReturnResultTabAction extends SquirrelAction
{
	/** Frame to be returned. */
	private ResultFrame _resultFrame;

	/**
	 * Ctor.
	 *
	 * @param	app			Application API.
	 * @param	resultFrame	Results frame to be returned.
	 */
	public ReturnResultTabAction(IApplication app, ResultFrame resultFrame)
		throws IllegalArgumentException
	{
		super(app);
		if (resultFrame == null)
		{
			throw new IllegalArgumentException("Null ResultFrame passed");
		}

		_resultFrame = resultFrame;
	}

	public void actionPerformed(ActionEvent evt)
	{
		new ReturnResultTabCommand(_resultFrame).execute();
	}

}