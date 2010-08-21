package net.sourceforge.squirrel_sql.client.mainframe.action;
/*
 * Copyright (C) 2001-2003 Colin Bell
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

import net.sourceforge.squirrel_sql.fw.gui.CursorChanger;
import net.sourceforge.squirrel_sql.client.mainframe.action.TileInternalFramesAction;

import net.sourceforge.squirrel_sql.client.IApplication;
/**
 * This <CODE>Action</CODE> vertically tiles across the internal frames.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class TileVerticalAction extends TileInternalFramesAction
{
	/** Application API. */
	private IApplication _app;

	/**
	 * Ctor.
	 *
	 * @param	app	Application API.
	 */
	public TileVerticalAction(IApplication app)
	{
		super(app);
		_app = app;
		app.getResources().setupAction(
			this,
			_app.getSquirrelPreferences().getShowColoriconsInToolbar());
	}

	public void actionPerformed(ActionEvent evt)
	{
		CursorChanger cursorChg = new CursorChanger(_app.getMainFrame());
		cursorChg.show();
		try
		{
			super.actionPerformed(evt);
		}
		finally
		{
			cursorChg.restore();
		}
	}

	/**
	 * Retrieve the number of rows and columns that the internal frames
	 * should be rearranged into.
	 *
	 * @param	internalFrameCount	Number of internal frames to be rearranged.
	 */
	protected RowColumnCount getRowColumnCount(int internalFrameCount)
	{
		int rows = 0;
		int cols = 0;
		if (internalFrameCount > 0)
		{
			rows = 1;
			cols = internalFrameCount;
		}
		return new RowColumnCount(rows, cols);
	}
}
