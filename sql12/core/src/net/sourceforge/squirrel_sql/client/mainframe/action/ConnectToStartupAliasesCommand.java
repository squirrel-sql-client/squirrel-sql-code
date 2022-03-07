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

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.db.AliasesAndDriversManager;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This <CODE>ICommand</CODE> connects to all aliases specified as "connect
 * at startup.
 *
 * @author	<A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ConnectToStartupAliasesCommand implements ICommand
{
   private static final ILogger s_log = LoggerController.createLogger(ConnectToStartupAliasesCommand.class);

   private IApplication _app;

   public ConnectToStartupAliasesCommand(IApplication app)
   {
      _app = app;
   }

   public void execute()
   {
      final List<ISQLAlias> aliases = new ArrayList<>();
      final AliasesAndDriversManager cache = _app.getAliasesAndDriversManager();

      for (Iterator<? extends ISQLAlias> it = cache.aliases(); it.hasNext();)
      {
         ISQLAlias alias = it.next();
         if (alias.isConnectAtStartup())
         {
            aliases.add(alias);
         }
      }

      final Iterator<ISQLAlias> it = aliases.iterator();
      while (it.hasNext())
      {
         final SQLAlias alias = (SQLAlias) it.next();

         s_log.info("Connecting during Application start to Alias: \"" + alias.getName() + "\" (JDBC-URL: " +  alias.getUrl() + ")");

         new ConnectToAliasCommand(alias).execute();
      }
   }
}
