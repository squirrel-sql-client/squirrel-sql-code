package net.sourceforge.squirrel_sql.client.mainframe;
/*
 * Copyright (C) 2001 Colin Bell
 * colbell@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
import java.awt.Color;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;

/**
 * A cell renderer, that shows Drivers that could not be loaded in red,
 * and thpse that could be loaded in green..
 *
 * @author  Henner Zeller
 */
public class DriverListCellRenderer extends DefaultListCellRenderer {
	/** Color for drivers that could be loaded. */
	private final static Color OK_COLOR   = new Color(190, 255, 190);

	/** Color for drivers that could not be loaded. */
	private final static Color FAIL_COLOR = new Color(255, 190, 190);

	public Component getListCellRendererComponent(JList list,
												Object value,
												int index,
												boolean isSelected,
												boolean cellHasFocus) {
		super.getListCellRendererComponent(list, value,  index, isSelected,
											cellHasFocus);
		ISQLDriver drv = (ISQLDriver)value;
		setBackground((drv.isJDBCDriverClassLoaded()) ? OK_COLOR : FAIL_COLOR);
		return this;
	}
}
