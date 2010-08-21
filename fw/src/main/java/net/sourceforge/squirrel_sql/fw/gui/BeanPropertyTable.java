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

import net.sourceforge.squirrel_sql.fw.util.BaseException;

public class BeanPropertyTable extends JTable
{
	private BeanPropertyTableModel _model;

	public BeanPropertyTable() throws BaseException
	{
		this(null);
	}

	public BeanPropertyTable(Object bean) throws BaseException
	{
		super(new BeanPropertyTableModel());
		_model = (BeanPropertyTableModel) getModel();
		_model.setBean(bean);
		getTableHeader().setResizingAllowed(true);
	}

	public void refresh() throws BaseException
	{
		_model.refresh();
	}

	public void setBean(Object bean) throws BaseException
	{
		_model.setBean(bean);
	}

	public void setModel(BeanPropertyTableModel model) throws BaseException
	{
		super.setModel(model);
		_model = model;
		refresh();
	}
}
