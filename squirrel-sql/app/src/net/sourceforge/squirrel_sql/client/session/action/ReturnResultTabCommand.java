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
import net.sourceforge.squirrel_sql.fw.util.ICommand;

import net.sourceforge.squirrel_sql.client.session.mainpanel.ResultFrame;
/**
 * This <CODE>ICommand</CODE> returns the specifed SQL results
 * frame back where it was torn off from.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ReturnResultTabCommand implements ICommand
{
	/** Frame to be returned. */
	private ResultFrame _resultFrame;

	/**
	 * Ctor.
	 *
	 * @param	resultFrame   Frame to be returned to tabbed folder.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if a <TT>null</TT> <TT>ResultFrame</TT> passed.
	 */
	public ReturnResultTabCommand(ResultFrame resultFrame)
	{
		super();
		if (resultFrame == null)
		{
			throw new IllegalArgumentException("Null ResultFrame passed");
		}

		_resultFrame = resultFrame;
	}

	public void execute()
	{
		_resultFrame.returnToTabbedPane();
	}
}