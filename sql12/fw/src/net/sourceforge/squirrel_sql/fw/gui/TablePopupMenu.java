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

import javax.swing.JTable;

import net.sourceforge.squirrel_sql.fw.gui.BasePopupMenu;
import net.sourceforge.squirrel_sql.fw.gui.action.BaseAction;
import net.sourceforge.squirrel_sql.fw.gui.action.TableCopyCommand;
import net.sourceforge.squirrel_sql.fw.gui.action.TableCopyHtmlCommand;
import net.sourceforge.squirrel_sql.fw.gui.action.MakeEditableCommand;
import net.sourceforge.squirrel_sql.fw.gui.action.TableSelectAllCellsCommand;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetUpdateableModel;

public class TablePopupMenu extends BasePopupMenu
{
	private JTable _table;

	private CutAction _cut = new CutAction();
	private CopyAction _copy = new CopyAction();
	private CopyHtmlAction _copyHtml = new CopyHtmlAction();
	private PasteAction _paste = new PasteAction();
	//	private ClearAction _clear = new ClearAction();
	private MakeEditableAction _makeEditable = new MakeEditableAction();
	private SelectAllAction _select = new SelectAllAction();

	// The following pointer is needed to allow the "Make Editable button
	// to tell the application to set up an editable display panel
	private IDataSetUpdateableModel _updateableModel = null;

	/**
	 * Constructor used when caller wants to be able to make table editable.
	 * We need both parameters because there is at least one case where the
	 * underlying data model is updateable, but we do not want to allow the
	 * user to enter editing mode because they are already in edit mode.
	 * The caller needs to determine whether or not to allow a request for edit mode.
	 */
	public TablePopupMenu(boolean allowEditing,
			IDataSetUpdateableModel updateableModel)
	{
		super();
		// save the pointer needed to enable editing of data on-demand
		_updateableModel = updateableModel;

		//add(_cut);
		add(_copy);
		add(_copyHtml);
		//add(_paste);
		if (allowEditing)
		{
			addSeparator();
			add(_makeEditable);
		}
		addSeparator();
		//add(_clear);
		//addSeparator();
		add(_select);
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

	protected void updateActions()
	{
		final boolean isEditable = false;
		_cut.setEnabled(isEditable);
		_paste.setEnabled(isEditable);
	}

	private class ClearAction extends BaseAction
	{
		ClearAction()
		{
			super("Clear");
		}

		public void actionPerformed(ActionEvent evt)
		{
			if (_table != null)
			{
			}
		}
	}

	private class CutAction extends BaseAction
	{
		CutAction()
		{
			super("Cut");
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
			super("Copy");
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
			super("Copy as Html");
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
			super("Paste");
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
			super("Make Editable");
		}

		public void actionPerformed(ActionEvent evt)
		{
			if (_updateableModel != null)
			{
				new MakeEditableCommand(_updateableModel).execute();
			}
		}
	}

	private class SelectAllAction extends BaseAction
	{
		SelectAllAction()
		{
			super("Select All");
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

