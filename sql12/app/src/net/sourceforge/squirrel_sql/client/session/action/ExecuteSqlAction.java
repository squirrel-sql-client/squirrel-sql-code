package net.sourceforge.squirrel_sql.client.session.action;
/*
 * Copyright (C) 2001-2002 Colin Bell
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
import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.fw.gui.CursorChanger;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
/**
 * Executes sql from an ISQLPanelAPI.
 *
 */
public class ExecuteSqlAction extends SquirrelAction implements ISQLPanelAction
{
	private ISQLPanelAPI _panel;

	public ExecuteSqlAction(IApplication app)
	{
		super(app);
	}

	public void setSQLPanel(ISQLPanelAPI panel)
	{
		_panel = panel;
      setEnabled(null != _panel);
	}

	public void actionPerformed(ActionEvent evt)
	{
		if (_panel != null)
		{
			CursorChanger cursorChg = new CursorChanger(getApplication().getMainFrame());
			cursorChg.show();
			try
			{
				_panel.executeCurrentSQL();
			}
			finally
			{
				cursorChg.restore();
			}
		}
	}
}
