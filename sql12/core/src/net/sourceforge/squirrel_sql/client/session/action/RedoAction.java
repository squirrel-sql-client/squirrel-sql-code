package net.sourceforge.squirrel_sql.client.session.action;
/*
 * Copyright (C) 2002-2003 Johan Compagner
 * jcompagner@j-com.nl
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

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;

import javax.swing.Action;
import javax.swing.undo.UndoManager;
import java.awt.event.ActionEvent;

public class RedoAction extends SquirrelAction  implements ISQLPanelAction
{
	private UndoManager _undoManager;
   private Action _delegate;
   private ISQLPanelAPI _sqlPanelAPI;

   public RedoAction()
	{
		super(Main.getApplication());
	}

   @Override
   public void setSQLPanel(ISQLPanelAPI sqlPanelAPI)
   {
      _sqlPanelAPI = sqlPanelAPI;
      setEnabled(null != _sqlPanelAPI);
   }


   public void setUndoManager(UndoManager undoManager)
   {
      _undoManager = undoManager;
      _delegate = null;
   }

   public void setDelegate(Action delegate)
   {
      _delegate = delegate;
      _undoManager = null;
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
         if(_undoManager.canRedo()) _undoManager.redo();
      }
      else
      {
         _delegate.actionPerformed(e);
      }
   }

}
