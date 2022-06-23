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

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;

public class RedoAction extends SquirrelAction  implements ISQLPanelAction
{
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

   /*
     * @see ActionListener#actionPerformed(ActionEvent)
     */
	public void actionPerformed(ActionEvent e)
	{
      if(null == _sqlPanelAPI)
      {
         return;
      }


      UndoRedoActionContext undoRedoContext = _sqlPanelAPI.getUndoRedoActionContext();

      if ( null != undoRedoContext.getUndoManager() )
      {
         if( undoRedoContext.getUndoManager().canRedo())
         {
            undoRedoContext.getUndoManager().redo();
         }
      }
      else
      {
         undoRedoContext.getRedoActionDelegate().actionPerformed(e);
      }
   }

}
