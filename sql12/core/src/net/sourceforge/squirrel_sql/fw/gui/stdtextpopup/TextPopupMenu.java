package net.sourceforge.squirrel_sql.fw.gui.stdtextpopup;
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

import net.sourceforge.squirrel_sql.fw.gui.BasePopupMenu;

import javax.swing.text.JTextComponent;
import java.awt.Component;
import java.awt.event.MouseEvent;

public class TextPopupMenu extends BasePopupMenu
{
	private JTextComponent _comp;

	private TextCutAction _cut;
	private TextCopyAction _copy;
	private TextPasteAction _paste;
	private TextSelectAllAction _selectAll;

	public TextPopupMenu()
	{
		_cut = new TextCutAction();
		add(_cut);

		_copy = new TextCopyAction();
		add(_copy);

		_paste = new TextPasteAction();
		add(_paste);

		addSeparator();
		_selectAll = new TextSelectAllAction();
		add(_selectAll);

	}

	public void setTextComponent(JTextComponent value)
	{
		_comp = value;
		_cut.setComponent(_comp);
		_copy.setComponent(_comp);
		_paste.setComponent(_comp);
		_selectAll.setComponent(_comp);
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

	public void dispose()
	{
		// Menues that are also shown in the main window Session menu might
		// be in this popup. If we don't remove them, the Session won't be Garbage Collected.
		removeAll();
		setInvoker(null);
		_comp = null;
	}
}
