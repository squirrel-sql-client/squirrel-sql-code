package net.sourceforge.squirrel_sql.fw.gui;
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
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JTable;

import net.sourceforge.squirrel_sql.fw.gui.action.BaseAction;
import net.sourceforge.squirrel_sql.fw.gui.action.TableCopyCommand;
import net.sourceforge.squirrel_sql.fw.gui.action.TableCopyHtmlCommand;
import net.sourceforge.squirrel_sql.fw.gui.action.MakeEditableCommand;
import net.sourceforge.squirrel_sql.fw.gui.action.TableSelectAllCellsCommand;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetUpdateableModel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanel;

//?????
import net.sourceforge.squirrel_sql.fw.datasetviewer.MyTableModel;

public class TablePopupMenu extends BasePopupMenu
{
	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(TablePopupMenu.class);

	public interface IOptionTypes
	{
		int COPY = 0;
		int COPY_HTML = 1;
		int SELECT_ALL = 2;
		int LAST_ENTRY = 2;
	}

	private final JMenuItem[] _menuItems = new JMenuItem[IOptionTypes.LAST_ENTRY + 1];

	private JTable _table;

	private CutAction _cut = new CutAction();
	private CopyAction _copy = new CopyAction();
	private CopyHtmlAction _copyHtml = new CopyHtmlAction();
	private PasteAction _paste = new PasteAction();
	//	private ClearAction _clear = new ClearAction();
	private MakeEditableAction _makeEditable = new MakeEditableAction();
	private DeleteRowsAction _deleteRows = new DeleteRowsAction();
	private InsertRowAction _insertRow = new InsertRowAction();
	private SelectAllAction _select = new SelectAllAction();

	// The following pointer is needed to allow the "Make Editable button
	// to tell the application to set up an editable display panel
	private IDataSetUpdateableModel _updateableModel = null;
	
	// pointer to the viewer
	// This is needed for insert and delete operations
	private DataSetViewerTablePanel _viewer = null;

	/**
	 * Constructor used when caller wants to be able to make table editable.
	 * We need both parameters because there is at least one case where the
	 * underlying data model is updateable, but we do not want to allow the
	 * user to enter editing mode because they are already in edit mode.
	 * The caller needs to determine whether or not to allow a request for edit mode.
	 */
	public TablePopupMenu(boolean allowEditing,
			IDataSetUpdateableModel updateableModel,
			DataSetViewerTablePanel viewer)
	{
		super();
		// save the pointer needed to enable editing of data on-demand
		_updateableModel = updateableModel;
		
		// save the pointer needed for insert and delete operations
		_viewer = viewer;

		addMenuItems(allowEditing);
	}
	
	/**
	 * Constructor used when creating menu for use in cell editor.
	 */
	public TablePopupMenu(IDataSetUpdateableModel updateableModel,
			DataSetViewerTablePanel viewer, JTable table)
	{
		super();
		// save the pointer needed to enable editing of data on-demand
		_updateableModel = updateableModel;
		
		// save the pointer needed for insert and delete operations
		_viewer = viewer;
		
		_table = table;

// Cut and Paste need to be worked on, so for now do not include them
//		add(_cut);
		add(_copy);
		add(_copyHtml);
//		add(_paste);
		addSeparator();
		add(_insertRow);
		add(_deleteRows);
	}

	public void setTable(JTable value)
	{
		_table = value;
	}

	/**
	 * Show the menu.
	 */
	public void show(Component invoker, int x, int y)
	{
		updateActions();
		super.show(invoker, x, y);
	}

	public void show(MouseEvent evt)
	{
		updateActions();
		super.show(evt);
	}

/*
	protected void setItemAction(int optionType, Action action)
	{
		if (optionType < 0 || optionType > IOptionTypes.LAST_ENTRY)
		{
			throw new IllegalArgumentException("Invalid option type: " + optionType);
		}
		if (action == null)
		{
			throw new IllegalArgumentException("Action == null");
		}

		final int idx = getComponentIndex(_menuItems[optionType]);
		remove(idx);
		insert(action, idx);
		_menuItems[optionType] = (JMenuItem)getComponent(idx);
	}
*/

	protected void updateActions()
	{
		final boolean isEditable = false;
		_cut.setEnabled(isEditable);
		_paste.setEnabled(isEditable);
	}

	private void addMenuItems(boolean allowEditing)
	{
		_menuItems[IOptionTypes.COPY] = add(_copy);
		_menuItems[IOptionTypes.COPY_HTML] = add(_copyHtml);
		if (allowEditing)
		{
			addSeparator();
			add(_makeEditable);
		}
		addSeparator();
		_menuItems[IOptionTypes.SELECT_ALL] = add(_select);

		// add entries for insert and delete rows
		// only if table is updateable and already editable (ie. allowEditing is false)
		if (_updateableModel != null && allowEditing==false) {
			addSeparator();
			add(_insertRow);
			add(_deleteRows);
		}
	}

//	private class ClearAction extends BaseAction
//	{
//		ClearAction()
//		{
//			super(s_stringMgr.getString("TablePopupMenu.clear"));
//		}
//
//		public void actionPerformed(ActionEvent evt)
//		{
//			if (_table != null)
//			{
//			}
//		}
//	}

	private class CutAction extends BaseAction
	{
		CutAction()
		{
			super(s_stringMgr.getString("TablePopupMenu.cut"));
		}

		public void actionPerformed(ActionEvent evt)
		{
			if (_table != null)
			{
			}
		}
	}

	private class CopyAction extends BaseAction
	{
		CopyAction()
		{
			super(s_stringMgr.getString("TablePopupMenu.copy"));
		}

		public void actionPerformed(ActionEvent evt)
		{
			if (_table != null)
			{
				new TableCopyCommand(_table).execute();
			}
		}
	}

	private class CopyHtmlAction extends BaseAction
	{
		CopyHtmlAction()
		{
			super(s_stringMgr.getString("TablePopupMenu.copyashtml"));
		}

		public void actionPerformed(ActionEvent evt)
		{
			if (_table != null)
			{
				new TableCopyHtmlCommand(_table).execute();
			}
		}
	}

	private class PasteAction extends BaseAction
	{
		PasteAction()
		{
			super(s_stringMgr.getString("TablePopupMenu.paste"));
		}

		public void actionPerformed(ActionEvent evt)
		{
			if (_table != null)
			{
			}
		}
	}

	private class MakeEditableAction extends BaseAction
	{
		MakeEditableAction()
		{
			super(s_stringMgr.getString("TablePopupMenu.makeeditable"));
		}

		public void actionPerformed(ActionEvent evt)
		{
			if (_updateableModel != null)
			{
				new MakeEditableCommand(_updateableModel).execute();
			}
		}
	}
	
	private class DeleteRowsAction extends BaseAction
	{
		DeleteRowsAction()
		{
			super(s_stringMgr.getString("TablePopupMenu.deleterows"));
		}

		public void actionPerformed(ActionEvent evt)
		{
			if (_table != null)
			{
				int selectedRows[] = _table.getSelectedRows();
			
				// Tell the DataSetViewer to delete the rows
				// Note: rows are indexes in the SORTABLE model, not the ACTUAL model
				_viewer.deleteRows(selectedRows);
			}
		}
	}
	
	private class InsertRowAction extends BaseAction
	{
		InsertRowAction()
		{
			super(s_stringMgr.getString("TablePopupMenu.insertrow"));
		}

		public void actionPerformed(ActionEvent evt)
		{
			_viewer.insertRow();
		}
	}

	private class SelectAllAction extends BaseAction
	{
		SelectAllAction()
		{
			super(s_stringMgr.getString("TablePopupMenu.selectall"));
		}

		public void actionPerformed(ActionEvent evt)
		{
			if (_table != null)
			{
				new TableSelectAllCellsCommand(_table).execute();
			}
		}
	}
}

