package net.sourceforge.squirrel_sql.client.session.action.reconnect;
/*
 * Copyright (C) 2002-2003 Colin Bell
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

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.Dialogs;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class ReconnectAction extends SquirrelAction
{
   public ReconnectAction(IApplication app)
   {
      super(app);
   }

   /**
    * Perform this action. Prompt the user and if they confirm then close the
    * current connection to the database and open a new one.
    *
    * @param   evt    The current event.
    */
   public void actionPerformed(ActionEvent evt)
   {
      ISession activeSession = getApplication().getSessionManager().getActiveSession();

      ReconnectController reconnectController = new ReconnectController(activeSession);

      ReconnectInfo reconnectInfo = reconnectController.getReconnectInfo();

      if (reconnectInfo.isReconnectRequested())
      {
         // Can't work with ISessionAction because if a result window is on top
         // the session in a ISessionAction is null.
         activeSession.reconnect(reconnectInfo);
      }
   }
}
