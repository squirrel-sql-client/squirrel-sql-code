package net.sourceforge.squirrel_sql.fw.gui;
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
import java.awt.Component;
import java.awt.Cursor;

public class CursorChanger
{
	private final Component _comp;
	private final Cursor _newCursor;

	public CursorChanger(Component comp)
	{
		this(comp, Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	}

	public CursorChanger(Component comp, Cursor newCursor)
	{
		super();

		if (newCursor == null)
		{
			throw new IllegalArgumentException("null Cursor passed");
		}
		if (comp == null)
		{
			throw new IllegalArgumentException("null Component passed");
		}

		_comp = comp;
		_newCursor = newCursor;
	}

	public void show()
	{
		_comp.setCursor(_newCursor);
	}

	public void restore()
	{
		_comp.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

}
