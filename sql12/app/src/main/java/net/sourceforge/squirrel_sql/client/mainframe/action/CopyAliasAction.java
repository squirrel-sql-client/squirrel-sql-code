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

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.db.IAliasesList;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;

/**
 * This <CODE>Action</CODE> allows the user to copy an <TT>ISQLAlias</TT>
 * and maintain the newly copied one.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class CopyAliasAction extends AliasAction
{
   private static final long serialVersionUID = 1L;

   /**
    * List of all the users aliases.
    */
   private final IAliasesList _aliases;

   /**
    * Ctor specifying the list of aliases.
    *
    * @param	app		Application API.
    * @param	list	List of <TT>ISQLAlias</TT> objects.
    *
    * @throws	IllegalArgumentException
    *			thrown if a <TT>null</TT> <TT>AliasesList</TT> passed.
    */
   public CopyAliasAction(IApplication app, IAliasesList list)
   {
      super(app);
      if (list == null)
      {
         throw new IllegalArgumentException("Null AliasesList passed");
      }
      _aliases = list;
   }

   /**
    * Perform this action. Use the <TT>CopyAliasCommand</TT>.
    *
    * @param	evt	The current event.
    */
   public void actionPerformed(ActionEvent evt)
   {
      moveToFrontAndSelectAliasFrame();
      SQLAlias alias = _aliases.getSelectedAlias(null);
      if (alias != null)
      {
         new CopyAliasCommand(getApplication(), alias).execute();
      }
   }
}
