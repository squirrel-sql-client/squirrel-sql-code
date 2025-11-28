package net.sourceforge.squirrel_sql.client.mainframe.action;
/*
 * Copyright (C) 2001-2004 Colin Bell
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
import java.util.List;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.db.IAliasesList;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;

public class ConnectToAliasAction extends AliasAction
{
   /**
    * List of all the users aliases.
    */
   private final IAliasesList _aliases;

   /**
    * Ctor specifying the list of aliases.
    *
    * @param	app		Application API.
    * @param	list	List of <TT>SQLAlias</TT> objects.
    */
   public ConnectToAliasAction(IApplication app, IAliasesList list)
   {
      super(app);
      _aliases = list;
   }

   /**
    * Perform this action. Retrieve the current alias from this list and
    * connect to it.
    *
    * @param	evt		The current event.
    */
   public void actionPerformed(ActionEvent evt)
   {
      moveToFrontAndSelectAliasFrame();
      List<SQLAlias> selectedAliases = _aliases.getAllSelectedAliases();

      selectedAliases.forEach(a -> new ConnectToAliasCommand(a).executeConnect());
   }
}
