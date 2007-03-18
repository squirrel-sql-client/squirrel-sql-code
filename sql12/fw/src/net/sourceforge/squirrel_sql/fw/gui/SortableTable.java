package net.sourceforge.squirrel_sql.fw.gui;
/*
 * Copyright (C) 2001-2004 Colin Bell
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
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

public class SortableTable extends JTable
{
	public SortableTable(TableModel model)
	{
		super(new SortableTableModel(model));
		init();
	}

	public SortableTable(TableModel model, TableColumnModel colModel)
	{
		super(new SortableTableModel(model), colModel);
		init();
	}

	public SortableTable(SortableTableModel model)
	{
		super(model);
		init();
	}

	public SortableTable(SortableTableModel model, TableColumnModel colModel)
	{
		super(model, colModel);
		init();
	}

	public SortableTableModel getSortableTableModel()
	{
		return (SortableTableModel)getModel();
	}

	public void setModel(TableModel model)
	{
		super.setModel(new SortableTableModel(model));
	}

	public void setSortableTableModel(SortableTableModel model)
	{
		super.setModel(model);
	}

	private void init()
	{
		setTableHeader(new ButtonTableHeader());
	}
}

