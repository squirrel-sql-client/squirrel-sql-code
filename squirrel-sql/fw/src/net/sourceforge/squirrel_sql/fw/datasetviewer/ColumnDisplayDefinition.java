package net.sourceforge.squirrel_sql.fw.datasetviewer;
/*
 * Copyright (C) 2001 Colin Bell
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
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * This defines the display information for a column.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ColumnDisplayDefinition {

	/** Number of characters to display. */
	private int _displayWidth;

	/** Column heading. */
	private String _label;

	/**
	 * Ctor.
	 *
	 * @param   displayWidth		Number of characters to display.
	 * @param   label			   Column heading.
	 */
	public ColumnDisplayDefinition(int displayWidth, String label) {
		super();
		init(displayWidth, label);
	}

	/**
	 * Return the number of characters to display.
	 *
	 * @return  The number of characters to display.
	 */
	public int getDisplayWidth() {
		return _displayWidth;
	}

	/**
	 * Return the column heading.
	 *
	 * @return  The column heading.
	 */
	public String getLabel() {
		return _label;
	}

	/**
	 * Private initializer method for ctors. If the display width
	 * is less than the width of the heading then make the display
	 * width the same as the width of the heading.
	 *
	 * @param   displayWidth		Number of characters to display.
	 * @param   label			   Column heading.
	 */
	private void init(int displayWidth, String label) {
		_displayWidth = displayWidth;
		if (_displayWidth < label.length()) {
			_displayWidth = label.length();
		}
		_label = label;
	}
}