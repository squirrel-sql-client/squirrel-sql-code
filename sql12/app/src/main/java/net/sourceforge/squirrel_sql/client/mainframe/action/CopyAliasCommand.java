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
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.fw.util.ICommand;

/**
 * This <CODE>ICommand</CODE> allows the user to copy an existing
 * <TT>ISQLAlias</TT> to a new one and then maintain the new one.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class CopyAliasCommand implements ICommand
{
   /** Application API. */
   private final IApplication _app;

   /** <TT>ISQLAlias</TT> to be copied. */
   private final SQLAlias _sqlAlias;

   /**
    * Ctor.
    *
    * @param	app			Application API.
    * @param	sqlAlias	<TT>ISQLAlias</TT> to be copied.
    *
    * @throws	IllegalArgumentException
    *			Thrown if a <TT>null</TT> <TT>ISQLAlias</TT> or
    *			<tt>IApplication</tt> passed.
    */
   public CopyAliasCommand(IApplication app, SQLAlias sqlAlias)
   {
      super();
      if (app == null)
      {
         throw new IllegalArgumentException("IApplication == null");
      }
      if (sqlAlias == null)
      {
         throw new IllegalArgumentException("Null ISQLAlias passed");
      }

      _app = app;
      _sqlAlias = sqlAlias;
   }

   public void execute()
   {
      _app.getWindowManager().showCopyAliasInternalFrame(_sqlAlias);
   }
}
