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
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

import javax.swing.JTextArea;

import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.DataTypeGeneral;
import net.sourceforge.squirrel_sql.fw.gui.TextPopupMenu;
import net.sourceforge.squirrel_sql.fw.gui.action.BaseAction;
import net.sourceforge.squirrel_sql.fw.gui.action.MakeEditableCommand;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.CellComponentFactory;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
//??RENAME to DataSetViewerTextDestination

public class DataSetViewerTextPanel extends BaseDataSetViewerDestination
{

	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(DataSetViewerTextPanel.class);


	private final static int COLUMN_PADDING = 2;

	private MyJTextArea _outText = null;
	private int _rowCount;

	public DataSetViewerTextPanel()
	{
      init(null);
		_rowCount = 0;
	}

	public void init(IDataSetUpdateableModel updateableObject)
	{
		_outText = new MyJTextArea(updateableObject);
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
            String headerValue = colDefs[i].getColumnHeading();

            buf.append(format(headerValue, colDefs[i].getDisplayWidth(), ' '));
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
			String cellValue = CellComponentFactory.renderObject(row[i], colDefs[i]);
			buf.append(format(cellValue, colDefs[i].getDisplayWidth(), ' '));
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

	private final class MyJTextArea extends JTextArea
	{
		private static final long serialVersionUID = 1L;
		
		private TextPopupMenu _textPopupMenu;

		MyJTextArea(IDataSetUpdateableModel updateableObject)
		{
			super();
			boolean allowUpdate = false;
			if (updateableObject != null)
				allowUpdate = true;
			createUserInterface(allowUpdate, updateableObject);
		}

		protected void createUserInterface(boolean allowUpdate, 
			IDataSetUpdateableModel updateableObject)
		{
			setEditable(false);
			setLineWrap(false);
			setFont(new Font("Monospaced", Font.PLAIN, 12));

			_textPopupMenu = new MyJTextAreaPopupMenu(allowUpdate, updateableObject);
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

	private static class MyJTextAreaPopupMenu extends TextPopupMenu
	{
		private static final long serialVersionUID = 1L;

		private MakeEditableAction _makeEditable = new MakeEditableAction();

		// The following pointer is needed to allow the "Make Editable button
		// to tell the application to set up an editable display panel
		private IDataSetUpdateableModel _updateableModel = null;

		MyJTextAreaPopupMenu(boolean allowUpdate, 
					IDataSetUpdateableModel updateableObject)
		{
			super();
			// save the pointer needed to enable editing of data on-demand
			_updateableModel = updateableObject;

			if (allowUpdate)
			{
				addSeparator();
				add(_makeEditable);
				addSeparator();
			}
		}

		private class MakeEditableAction extends BaseAction
		{
			private static final long serialVersionUID = 1L;

			MakeEditableAction()
			{
				// i18n[dataSetViewerTablePanel.makeEditable=Make Editable]
 				super(s_stringMgr.getString("dataSetViewerTablePanel.makeEditable"));
			}

			public void actionPerformed(ActionEvent evt)
			{
				if (_updateableModel != null)
				{
					new MakeEditableCommand(_updateableModel).execute();
				}
			}
		}
	}

}
