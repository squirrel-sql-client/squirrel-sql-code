package net.sourceforge.squirrel_sql.plugins.jedit;
/*
 * Copyright (C) 2001-2002 Colin Bell
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
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DefaultEditorKit.PasteAction;

import net.sourceforge.squirrel_sql.fw.gui.BasePopupMenu;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;
import net.sourceforge.squirrel_sql.client.session.ISession;

import net.sourceforge.squirrel_sql.plugins.jedit.textarea.InputHandler;
import net.sourceforge.squirrel_sql.plugins.jedit.textarea.JEditTextArea;

class JeditPopupMenu extends BasePopupMenu
{
	/** Logger for this class. */
	private static final ILogger s_log =
		LoggerController.createLogger(JeditPopupMenu.class);

	private JeditPlugin _plugin;

	private CutAction _cut;
	private CopyAction _copy;
	private PasteAction _paste;
	private ClearAction _clear;
	private SelectAllAction _select;

	private JEditTextArea _comp;

	JeditPopupMenu(ISession session, JeditPlugin plugin, JEditTextArea ta)
	{
		super();
		_plugin = plugin;
		_comp = ta;

		IApplication app = session.getApplication();
		PluginResources rsrc = plugin.getResources();

		add(_cut = new CutAction(app, rsrc));
		add(_copy = new CopyAction(app, rsrc));
		add(_paste = new PasteAction(app, rsrc));
		addSeparator();
		add(_clear = new ClearAction(app, rsrc));
		addSeparator();
		add(_select = new SelectAllAction(app, rsrc));
	}

	public JMenuItem add(Action action)
	{
		JMenuItem mi = super.add(action);
		InputHandler ih = _comp.getInputHandler();
		PluginResources rsrc = _plugin.getResources();
		String rsrcKey = "jeditshortcut." + rsrc.getClassName(action.getClass());
		String binding = rsrc.getString(rsrcKey);
		if (binding != null && binding.length() > 0)
		{
			ih.addKeyBinding(binding, action);
			s_log.debug("Adding binding: " + binding);
		}
		return mi;
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

	private class ClearAction extends SquirrelAction
	{
		ClearAction(IApplication app, PluginResources rsrc)
		{
			super(app, rsrc);
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

	private class CutAction extends SquirrelAction
	{
		CutAction(IApplication app, PluginResources rsrc)
		{
			super(app, rsrc);
		}

		public void actionPerformed(ActionEvent evt)
		{
			if (_comp != null)
			{
				_comp.cut();
			}
		}
	}

	private class CopyAction extends SquirrelAction
	{
		CopyAction(IApplication app, PluginResources rsrc)
		{
			super(app, rsrc);
		}

		public void actionPerformed(ActionEvent evt)
		{
			if (_comp != null)
			{
				_comp.copy();
			}
		}
	}

	private class PasteAction extends SquirrelAction
	{
		PasteAction(IApplication app, PluginResources rsrc)
		{
			super(app, rsrc);
		}

		public void actionPerformed(ActionEvent evt)
		{
			if (_comp != null)
			{
				_comp.paste();
			}
		}
	}

	private class SelectAllAction extends SquirrelAction
	{
		SelectAllAction(IApplication app, PluginResources rsrc)
		{
			super(app, rsrc);
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