package net.sourceforge.squirrel_sql.fw.gui.action;
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
import java.beans.PropertyVetoException;

import javax.swing.JInternalFrame;

import net.sourceforge.squirrel_sql.fw.util.ICommand;

public class SelectInternalFrameCommand implements ICommand
{
	private JInternalFrame _child;

	public SelectInternalFrameCommand(JInternalFrame child)
	{
		super();
		if (child == null)
		{
			throw new IllegalArgumentException("Null JInternalFrame passed");
		}
		_child = child;
	}

	public void execute()
	{
		try
		{
			if (!_child.isSelected())
			{
				if (!_child.isVisible())
				{
					_child.setVisible(true);
				}
				_child.setSelected(true);
			}
		}
		catch (PropertyVetoException ignore)
		{
		}
	}
}