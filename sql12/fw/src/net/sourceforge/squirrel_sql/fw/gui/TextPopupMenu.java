package net.sourceforge.squirrel_sql.fw.gui;
/*
 * Copyright (C) 2001-2003 Colin Bell
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
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

import net.sourceforge.squirrel_sql.fw.gui.action.BaseAction;

public class TextPopupMenu extends BasePopupMenu
{
	public interface IOptionTypes
	{
		int CUT = 0;
		int COPY = 1;
		int PASTE = 2;
		int CLEAR = 3;
		int SELECT_ALL = 4;
		int LAST_ENTRY = 4;
	}

	private JTextComponent _comp;

	private final JMenuItem[] _menuItems = new JMenuItem[IOptionTypes.LAST_ENTRY + 1];

	private CutAction _cut = new CutAction();
	private CopyAction _copy = new CopyAction();
	private PasteAction _paste = new PasteAction();
	private ClearAction _clear = new ClearAction();
	private SelectAllAction _select = new SelectAllAction();

	/**
	 * Default constructor.
	 */
	public TextPopupMenu()
	{
		super();
		addMenuEntries();
	}

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
		_menuItems[optionType] = (JMenuItem) getComponent(idx);
	}

	private void addMenuEntries()
	{
		_menuItems[IOptionTypes.CUT] = add(_cut);
		_menuItems[IOptionTypes.COPY] = add(_copy);
		_menuItems[IOptionTypes.PASTE] = add(_paste);
		addSeparator();
		_menuItems[IOptionTypes.CLEAR] = add(_clear);
		addSeparator();
		_menuItems[IOptionTypes.SELECT_ALL] = add(_select);
	}

	public void setTextComponent(JTextComponent value)
	{
		_comp = value;
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
		final boolean isEditable = _comp != null && _comp.isEditable();
		_cut.setEnabled(isEditable);
		_paste.setEnabled(isEditable);
	}

	protected JTextComponent getTextComponent()
	{
		return _comp;
	}

	protected class ClearAction extends BaseAction
	{
		protected ClearAction()
		{
			super("Clear");
		}

		public void actionPerformed(ActionEvent evt)
		{
			if (_comp != null)
			{
				try
				{
					Document doc = _comp.getDocument();
					doc.remove(0, doc.getLength());
				}
				catch (BadLocationException ignore)
				{
				}
			}
		}
	}

	protected class CutAction extends BaseAction
	{
		CutAction()
		{
			super("Cut");
		}

		public void actionPerformed(ActionEvent evt)
		{
			if (_comp != null)
			{
				_comp.cut();
			}
		}
	}

	protected class CopyAction extends BaseAction
	{
		CopyAction()
		{
			super("Copy");
		}

		public void actionPerformed(ActionEvent evt)
		{
			if (_comp != null)
			{
				_comp.copy();
			}
		}
	}

	protected class PasteAction extends BaseAction
	{
		PasteAction()
		{
			super("Paste");
		}

		public void actionPerformed(ActionEvent evt)
		{
			if (_comp != null)
			{
				_comp.paste();
			}
		}
	}

	protected class SelectAllAction extends BaseAction
	{
		SelectAllAction()
		{
			super("Select All");
		}

		public void actionPerformed(ActionEvent evt)
		{
			if (_comp != null)
			{
				_comp.selectAll();
			}
		}
	}
}
