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

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;

import javax.swing.Action;
import javax.swing.undo.UndoManager;
import java.awt.event.ActionEvent;

public class UndoAction extends SquirrelAction implements ISQLPanelAction
{
	private UndoManager _undoManager;
   private Action _delegate;
   private ISQLPanelAPI _sqlPanelAPI;

   public UndoAction()
	{
		super(Main.getApplication());
	}

   public UndoAction(IApplication app, Action delegate)
   {
      super(app);
      _delegate = delegate;
   }

   @Override
   public void setSQLPanel(ISQLPanelAPI sqlPanelAPI)
   {
      _sqlPanelAPI = sqlPanelAPI;
      setEnabled(null != _sqlPanelAPI);

      if(null != _sqlPanelAPI)
      {
         UndoRedoActionContext undoRedoContext = _sqlPanelAPI.getUndoRedoActionContext();

         _undoManager = undoRedoContext.getUndoManager();
         _delegate = undoRedoContext.getUndoActionDelegate();
      }
   }

   /*
   * @see ActionListener#actionPerformed(ActionEvent)
   */
	public void actionPerformed(ActionEvent e)
	{
      if(null == _sqlPanelAPI)
      {
         return;
      }

      if (null == _delegate)
      {
         if (_undoManager.canUndo())
         {
            _undoManager.undo();
         }
      }
      else
      {
         _delegate.actionPerformed(e);
      }
   }
}
