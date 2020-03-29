package net.sourceforge.squirrel_sql.plugins.sqlparam;
/*
 * Copyright (C) 2007 Thorsten Mürell
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.SwingUtilities;

import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.SelectWidgetCommand;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.event.SQLExecutionAdapter;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.QueryHolder;
import net.sourceforge.squirrel_sql.fw.sql.commentandliteral.SQLCommentRemover;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.sqlparam.gui.AskParamValueDialog;

/**
 * This listener listens for SQL execution.
 *
 * @author Thorsten Mürell
 */
public class SQLParamExecutionListener extends SQLExecutionAdapter
{

   private final static ILogger s_log = LoggerController.createLogger(SQLParamPlugin.class);

   private SQLParamPlugin _plugin;
   private ISQLPanelAPI _sqlPanelAPI;

   /**
    * The constructor
    * @param plugin
    * @param sqlPanelAPI
    */
   public SQLParamExecutionListener(SQLParamPlugin plugin, ISQLPanelAPI sqlPanelAPI)
   {
      _plugin = plugin;
      _sqlPanelAPI = sqlPanelAPI;
   }

   /**
    * This method is called when the SQL was executed.
    *
    * @param sql
    */
   @Override
   public void statementExecuted(QueryHolder sql)
   {
      // log.info("SQL executed: " + sql);
   }

   /**
    * Called prior to an individual statement being executed. If you modify the
    * script remember to return it so that the caller knows about the
    * modifications.
    *
    * @param   sql   The SQL to be executed.
    * @return The SQL to be executed. If <TT>null</TT> returned then the
    * statement will not be executed.
    */
   @Override
   public String statementExecuting(String sql)
   {
//      // log.info("SQL starting to execute: " + sql);
//
//      // Removes -- comments
//      sql = removeComments(sql);
//
//      //log.info("Removed comments: " + sql);
//
//      // Removes /*  */ comments
//      sql = sql.replaceAll("\\/\\*(.|\\s)*?\\*\\/", "");

      sql = SQLCommentRemover.removeComments(sql);

      StringBuffer buffer = new StringBuffer(sql);
      Map<String, String> cache = _plugin.getCache();
      Map<String, String> currentCache = new HashMap<>();
      Pattern p = Pattern.compile("[\\ \\(]:[a-zA-Z]\\w+");

      Matcher m = p.matcher(buffer);


      boolean parametersWhereReplaced = false;
      while (m.find())
      {
         if (isQuoted(buffer, m.start()))
         {
            continue;
         }

         final String var = m.group();
         String value;
         if (currentCache.containsKey(var))
         {
            value = currentCache.get(var);
         }
         else
         {
            final String oldValue = cache.get(var);
            AskParamValueDialog dialog = createParameterDialog(var, oldValue);

            if (dialog.isCancelled())
            {
               return null;
            }
            value = sanitizeValue(dialog.getValue(), dialog.isQuotingNeeded());
            cache.put(var, dialog.getValue());
            currentCache.put(var, value);
         }
         buffer.replace(m.start(), m.end(), value);
         parametersWhereReplaced = true;
         m.reset();
      }

      GUIUtils.processOnSwingEventThread(() -> new SelectWidgetCommand(_sqlPanelAPI.getSession().getActiveSessionWindow()).execute());
      // log.info("SQL passing to execute: " + buffer.toString());

      //////////////////////////////////////////////////////////////////
      // This is a workaround to avoid bug #1206 "SQuirrel detects single line comment inside string literals"
      // That means at least when no parameters are used bug #1206 is avoided.
      // The right way would be to do parsing like in QueryTokenizer instead of using Regular Expressions.
      // Regular Expressions is not able to really cope with literals.
      if (parametersWhereReplaced)
      {
         return buffer.toString();
      }
      else
      {
         return sql;
      }
      //
      //////////////////////////////////////////////////////////////////
   }


   private AskParamValueDialog createParameterDialog(String parameter, String oldValue)
   {
      AskParamValueDialog dialog = new AskParamValueDialog(_sqlPanelAPI.getOwningFrame(), parameter, oldValue);
      SwingUtilities.invokeLater(() -> dialog.requestFocusForInputField());
      dialog.setVisible(true);
      return dialog;
   }

   private String sanitizeValue(String value, boolean quoting)
   {
      String retValue = value;
      boolean quotesNeeded = quoting;

      try
      {
         Float.parseFloat(value);
      }
      catch (NumberFormatException nfe)
      {
         quotesNeeded = true;
      }

      if (quotesNeeded)
      {
         retValue = "'" + value + "'";
      }
      return retValue;
   }

   private boolean isQuoted(StringBuffer buffer, int position)
   {
      String part = buffer.substring(0, position);
      if (searchAllOccurences(part, "\"") % 2 != 0)
         return true;
      if (searchAllOccurences(part, "'") % 2 != 0)
         return true;
      return false;
   }

   private int searchAllOccurences(String haystack, String needle)
   {
      int i = 0;
      int pos = 0;
      while ((pos = haystack.indexOf(needle, pos + 1)) > -1)
      {
         i++;
      }
      return i;
   }

}
