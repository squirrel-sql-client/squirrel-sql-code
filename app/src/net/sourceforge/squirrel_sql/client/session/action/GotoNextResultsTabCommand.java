package net.sourceforge.squirrel_sql.client.session.action;
/*
 * Copyright (C) 2003 Colin Bell
 * colbell@users.sourceforge.net
 *
 * Modifications Copyright (C) 2003-2004 Jason Height
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

import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
/**
 * This <CODE>ICommand</CODE> refreshes the object tree.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class GotoNextResultsTabCommand implements ICommand
{
	/** The current panel. */
	private final ISQLPanelAPI _panel;

	/**
	 * Ctor.
	 *
	 * @param	panel	The SQL Panel we want to display the next results
	 *					tab for.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if a <TT>null</TT> <TT>ISQLPanelAPI</TT> passed.
	 */
	public GotoNextResultsTabCommand(ISQLPanelAPI panel)
	{
		super();
		if (panel == null)
		{
			throw new IllegalArgumentException("ISQLPanelAPI == null");
		}
		_panel = panel;
	}

	/**
	 * Display the next results tab.
	 */
	public void execute()
	{
		_panel.gotoNextResultsTab();
	}
}