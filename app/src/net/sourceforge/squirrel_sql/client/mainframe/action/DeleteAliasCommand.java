package net.sourceforge.squirrel_sql.client.mainframe.action;
/*
 * Copyright (C) 2001-2003 Colin Bell
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
import java.awt.Frame;

import net.sourceforge.squirrel_sql.fw.gui.Dialogs;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;

/**
 * This <CODE>ICommand</CODE> allows the user to delete an existing
 * <TT>ISQLAlias</TT>.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class DeleteAliasCommand implements ICommand
{
   /** Internationalized strings for this class. */
   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(DeleteAliasCommand.class);

   /** Application API. */
   private  IApplication _app;

   /** Owner of the maintenance dialog. */
   private Frame _frame;

   /** <TT>SQLAlias</TT> to be deleted. */
   private SQLAlias _sqlAlias;

   /**
    * Ctor.
    *
    * @param	app			Application API.
    * @param	frame		Owning <TT>Frame</TT>.
    * @param	sqlAlias	<ISQLAlias</TT> to be deleted.
    *
    * @throws	IllegalArgumentException
    *			Thrown if a <TT>null</TT> <TT>ISQLAlias</TT> or
    *			<TT>IApplication</TT> passed.
    */
   public DeleteAliasCommand(IApplication app, Frame frame, SQLAlias sqlAlias)
   {
//      super();
//      if (app == null)
//      {
//         throw new IllegalArgumentException("Null IApplication passed");
//      }
//      if (sqlAlias == null)
//      {
//         throw new IllegalArgumentException("Null ISQLAlias passed");
//      }
//
//      _app = app;
//      _frame = frame;
//      _sqlAlias = sqlAlias;
   }

   /**
    * Delete the current <TT>ISQLAlias</TT> after confirmation.
    */
   public void execute()
   {
//      if (Dialogs.showYesNo(_frame, s_stringMgr.getString("DeleteAliasCommand.confirm", _sqlAlias.getName())))
//      {
//         _app.getDataCache().removeAlias(_sqlAlias);
//      }
   }
}
