package net.sourceforge.squirrel_sql.fw.datasetviewer;
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
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

import javax.swing.JTextArea;

import net.sourceforge.squirrel_sql.fw.gui.TextPopupMenu;
//??RENAME to DataSetViewerTextDestination

public class DataSetViewerTextPanel extends BaseDataSetViewerDestination
{
	private final static int COLUMN_PADDING = 2;
	private MyJTextArea _outText = new MyJTextArea();
	private int _rowCount;

	public DataSetViewerTextPanel()
	{
		super();
		_rowCount = 0;
	}

	public void clear()
	{
		_outText.setText("");
		_rowCount = 0;
	}

	public void setColumnDefinitions(ColumnDisplayDefinition[] colDefs)
	{
		super.setColumnDefinitions(colDefs);
		colDefs = getColumnDefinitions(); // in case superclass modifies them.
		if (getShowHeadings())
		{
			StringBuffer buf = new StringBuffer();
			for (int i = 0; i < colDefs.length; ++i)
			{
				buf.append(format(colDefs[i].getLabel(), colDefs[i].getDisplayWidth(), ' '));
			}
			addLine(buf.toString());
			buf = new StringBuffer();
			for (int i = 0; i < colDefs.length; ++i)
			{
				buf.append(format("", colDefs[i].getDisplayWidth(), '-'));
			}
			addLine(buf.toString());
		}
	}

	protected void addRow(Object[] row)
	{
		_rowCount++;
		ColumnDisplayDefinition[] colDefs = getColumnDefinitions();
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < row.length; ++i)
		{
			Object obj = getColumnRenderer(i).renderObject(row[i], i);
			buf.append(format(obj.toString(), colDefs[i].getDisplayWidth(), ' '));
		}
		addLine(buf.toString());
	}

	public void moveToTop()
	{
		_outText.select(0, 0);
	}

	/*
	 * @see BaseDataSetViewerDestination#allRowsAdded()
	 */
	protected void allRowsAdded()
	{
	}

	/**
	 * Get the component for this viewer.
	 *
	 * @return	The component for this viewer.
	 */
	public Component getComponent()
	{
		return _outText;
	}

	/*
	 * @see IDataSetViewer#getRowCount()
	 */
	public int getRowCount()
	{
		return _rowCount;
	}

	protected void addLine(String line)
	{
		_outText.append(line);
		_outText.append("\n");
	}

	protected String format(String data, int displaySize, char fillChar)
	{
		data = data.replace('\n', ' ');
		data = data.replace('\r', ' ');
		StringBuffer output = new StringBuffer(data);
		if (displaySize > MAX_COLUMN_WIDTH)
		{
			displaySize = MAX_COLUMN_WIDTH;
		}

		if (output.length() > displaySize)
		{
			output.setLength(displaySize);
		}

		displaySize += COLUMN_PADDING;

		int extraPadding = displaySize - output.length();
		if (extraPadding > 0)
		{
			char[] padData = new char[extraPadding];
			Arrays.fill(padData, fillChar);
			output.append(padData);
		}

		return output.toString();
	}

	private static final class MyJTextArea extends JTextArea
	{
		private TextPopupMenu _textPopupMenu;

		MyJTextArea()
		{
			super();
			createUserInterface();
		}

		protected void createUserInterface()
		{
			setEditable(false);
			setLineWrap(false);
			setFont(new Font("Monospaced", Font.PLAIN, 12));

			_textPopupMenu = new TextPopupMenu();
			_textPopupMenu.setTextComponent(this);

			addMouseListener(new MouseAdapter()
			{
				public void mousePressed(MouseEvent evt)
				{
					if (evt.isPopupTrigger())
					{
						MyJTextArea.this.displayPopupMenu(evt);
					}
				}
				public void mouseReleased(MouseEvent evt)
				{
					if (evt.isPopupTrigger())
					{
						MyJTextArea.this.displayPopupMenu(evt);
					}
				}
			});

		}

		void displayPopupMenu(MouseEvent evt)
		{
			_textPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
		}
	}

}
