package net.sourceforge.squirrel_sql.client.session.action;
/*
 * Copyright (C) 2006 Rob Manning
 * manningr@users.sourceforge.net
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

import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;

import java.awt.event.ActionEvent;


public class FileNewAction extends SquirrelAction  implements ISQLPanelAction
{
   private ISQLPanelAPI _panel;

   public FileNewAction(IApplication app)
   {
      super(app);
   }

   public void actionPerformed(ActionEvent e)
   {
      _panel.fileNew();
   }

   public void setSQLPanel(ISQLPanelAPI panel)
   {
      _panel = panel;
      setEnabled(null != _panel);
   }
}