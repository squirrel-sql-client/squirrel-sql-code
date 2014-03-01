package net.sourceforge.squirrel_sql.client.mainframe.action;
/*
 * Copyright (C) 2001-2003 Colin Bell and Johan Compagner
 * colbell@users.sourceforge.net
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.util.ICommand;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.db.DataCache;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;

/**
 * This <CODE>ICommand</CODE> connects to all aliases specified as "connect
 * at startup.
 *
 * @author	<A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ConnectToStartupAliasesCommand implements ICommand
{
   /** Application API. */
   private final IApplication _app;

   /**
    * Ctor.
    *
    * @param	app		Application API
    *
    * @throws	IllegalArgumentException
    * 			Thrown if <TT>null</TT> <TT>IApplication</TT> passed.
    */
   public ConnectToStartupAliasesCommand(IApplication app)
   {
      super();
      if (app == null)
      {
         throw new IllegalArgumentException("IApplication == null");
      }

      _app = app;
   }

   public void execute()
   {
      final List<ISQLAlias> aliases = new ArrayList<ISQLAlias>();
      final DataCache cache = _app.getDataCache();
      synchronized (cache)
      {
         for (Iterator<ISQLAlias> it = cache.aliases(); it.hasNext();)
         {
            ISQLAlias alias = it.next();
            if (alias.isConnectAtStartup())
            {
               aliases.add(alias);
            }
         }
      }
      final Iterator<ISQLAlias> it = aliases.iterator();
      while (it.hasNext())
      {
         final SQLAlias alias = (SQLAlias) it.next();
         new ConnectToAliasCommand(_app, alias).execute();
      }
   }
}
