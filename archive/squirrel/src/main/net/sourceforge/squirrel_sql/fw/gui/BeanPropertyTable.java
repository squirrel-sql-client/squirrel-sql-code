package net.sourceforge.squirrel_sql.fw.gui;
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
import javax.swing.JTable;
import javax.swing.table.TableColumn;

import net.sourceforge.squirrel_sql.fw.util.BaseException;

public class BeanPropertyTable extends JTable {

    private BeanPropertyTableModel _model;

    private Object _bean;

    public BeanPropertyTable() throws BaseException {
        this(null);
    }

    public BeanPropertyTable(Object bean) throws BaseException {
        super(new BeanPropertyTableModel());
        _model = (BeanPropertyTableModel)getModel();
        _model.setBean(bean);
        getTableHeader().setResizingAllowed(true);
    }

    public void refresh() throws BaseException {
        _model.refresh();
    }

    public void setBean(Object bean) throws BaseException {
        _model.setBean(bean);
    }

    public void setModel(BeanPropertyTableModel model) throws BaseException {
        super.setModel(model);
        _model = model;
        refresh();
    }

}
