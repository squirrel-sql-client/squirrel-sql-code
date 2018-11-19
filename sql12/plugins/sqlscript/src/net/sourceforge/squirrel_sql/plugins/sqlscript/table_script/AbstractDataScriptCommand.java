/*
 * Copyright (C) 2011 Stefan Willinger
 * wis775@users.sourceforge.net
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
package net.sourceforge.squirrel_sql.plugins.sqlscript.table_script;

import java.awt.event.WindowAdapter;

import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.IQueryTokenizer;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.sqlscript.FrameWorkAcessor;
import net.sourceforge.squirrel_sql.plugins.sqlscript.SQLScriptPlugin;

import org.apache.commons.lang.StringUtils;

/**
 * The base class for data script commands, they depends on the current selected SQL statement.
 *
 * @author Stefan Willinger
 */
public abstract class AbstractDataScriptCommand extends WindowAdapter
{

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(AbstractDataScriptCommand.class);

   private final ISession session;

   private final SQLScriptPlugin plugin;

   /**
    * Ctor specifying the current session and IAbortController.
    */
   public AbstractDataScriptCommand(ISession session, SQLScriptPlugin plugin)
   {
      super();
      this.session = session;
      this.plugin = plugin;
   }

   /**
    * @return the _session
    */
   public ISession getSession()
   {
      return session;
   }

   /**
    * @return the _plugin
    */
   public SQLScriptPlugin getPlugin()
   {
      return plugin;
   }

   /**
    * Looks for the current selected SQL statement in the editor pane.
    * This ensures, that the selected statement is realy a SELECT statement.
    * These errors can occurs,
    * <li>no query selected</li>
    * <li>more than one query selected</li>
    * <li>not a SELECT statement selected</li>
    * In all these cases, the user will get a message and <code>null</code> will be returned.
    *
    * @return the selected SELECT statement or null, if not exactly one SELECT statement is selected.
    */
   protected String getSelectedSelectStatement()
   {
      ISQLPanelAPI api = FrameWorkAcessor.getSQLPanelAPI(getSession(), getPlugin());

      String script = api.getSQLScriptToBeExecuted();

      IQueryTokenizer qt = getSession().getQueryTokenizer();
      qt.setScriptToTokenize(script);

      if (false == qt.hasQuery())
      {
         // i18n[CreateFileOfCurrentSQLCommand.noQuery=No query found to
         // create the script from.]
         getSession().showErrorMessage(s_stringMgr.getString("AbstractDataScriptCommand.noQuery"));
         return null;
      }

      if (qt.getQueryCount() > 1)
      {
         // i18n[CreateFileOfCurrentSQLCommand.moreThanOnQuery=There are more than one query selected. Only the first statement will be used.]
         getSession().showWarningMessage(s_stringMgr.getString("AbstractDataScriptCommand.moreThanOnQuery"));
      }

      String currentSQL = qt.nextQuery().getQuery();

      if (StringUtils.startsWithIgnoreCase(currentSQL, "select") == false)
      {
         // i18n[CreateFileOfCurrentSQLCommand.notASelect=The selected SQL is not a SELECT statement.]
         getSession().showErrorMessage(s_stringMgr.getString("AbstractDataScriptCommand.notASelect"));
         return null;
      }
      return currentSQL;
   }


}
