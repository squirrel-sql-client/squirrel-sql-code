package net.sourceforge.squirrel_sql.client.gui.db;
/*
 * Copyright (C) 2001-2004 Henner Zeller
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

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JList;

import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
/**
 * A cell renderer, that shows Drivers that could not be loaded with
 * one icon and those can could be loaded with another icon.
 *
 * @author Henner Zeller
 */
class DriverListCellRenderer extends DefaultListCellRenderer
{
	/** Icon for drivers that could be loaded. */
	private final Icon OK_ICON;

	/** Icon for drivers that could not be loaded. */
	private final Icon FAIL_ICON;

	public DriverListCellRenderer(Icon ok, Icon fail)
	{
		OK_ICON = ok;
		FAIL_ICON = fail;
	}

	public Component getListCellRendererComponent(JList list, Object value,
													int index, boolean isSelected,
													boolean cellHasFocus)
	{
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		final ISQLDriver drv = (ISQLDriver)value;
		if (drv == null || !drv.isJDBCDriverClassLoaded())
		{
			setIcon(FAIL_ICON);
		}
		else
		{
			setIcon(OK_ICON);
		}
		return this;
	}
}
