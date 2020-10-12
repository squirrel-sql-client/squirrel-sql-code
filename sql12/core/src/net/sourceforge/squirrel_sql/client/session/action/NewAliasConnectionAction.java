package net.sourceforge.squirrel_sql.client.session.action;

/*
 * Copyright (C) 2003-2004 Jason Height
 * jmheight@users.sourceforge.net
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
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.mainframe.action.CloneAliasCommand;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;

import java.awt.event.ActionEvent;

/**
 * This <CODE>Action</CODE> displays a new tab connected t osame alias.
 *
 * @author jarmolow
 */
public class NewAliasConnectionAction extends SquirrelAction implements ISessionAction
{
   private ISession _session;

   /**
    * Ctor.
    *
    * @param app Application API.
    * @throws IllegalArgumentException Thrown if a <TT>null</TT> <TT>IApplication</TT> passed.
    */
   public NewAliasConnectionAction(IApplication app)
   {
      super(app);
      if (app == null)
      {
         throw new IllegalArgumentException("Null IApplication passed");
      }

      setEnabled(false);
   }

   /**
    * Display the tree.
    *
    * @param evt The event being processed.
    */
   @Override
   public void actionPerformed(ActionEvent evt)
   {
      new CloneAliasCommand(getApplication(), (SQLAlias) _session.getAlias()).execute();
   }

   @Override
   public void setSession(ISession session)
   {
      _session = session;
      GUIUtils.processOnSwingEventThread(() -> setEnabled(null != _session));
   }
}
