package net.sourceforge.squirrel_sql.client.session.action;
/*
 * Copyright (C) 2001-2003 Johan Compagner
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
import java.awt.event.ActionEvent;

import javax.swing.undo.UndoManager;
import javax.swing.*;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;

public class UndoAction extends SquirrelAction
{
	private UndoManager _undo;
   private Action _delegate;

   public UndoAction(IApplication app, UndoManager undo)
	{
		super(app);
		if (undo == null)
		{
			throw new IllegalArgumentException("UndoManager == null");
		}
		_undo = undo;
	}

   public UndoAction(IApplication app, Action delegate)
   {
      super(app);
      _delegate = delegate;
   }


   /*
   * @see ActionListener#actionPerformed(ActionEvent)
   */
	public void actionPerformed(ActionEvent e)
	{
      if (null == _delegate)
      {
         if (_undo.canUndo())
         {
            _undo.undo();
         }
      }
      else
      {
         _delegate.actionPerformed(e);
      }
   }
}
