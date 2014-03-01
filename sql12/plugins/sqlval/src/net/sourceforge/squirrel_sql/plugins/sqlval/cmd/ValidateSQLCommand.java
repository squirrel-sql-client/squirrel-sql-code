package net.sourceforge.squirrel_sql.plugins.sqlval.cmd;
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
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.fw.sql.IQueryTokenizer;
import net.sourceforge.squirrel_sql.fw.util.BaseException;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.sqlval.WebServicePreferences;
import net.sourceforge.squirrel_sql.plugins.sqlval.WebServiceSession;
import net.sourceforge.squirrel_sql.plugins.sqlval.WebServiceSessionProperties;
import net.sourceforge.squirrel_sql.plugins.sqlval.WebServiceValidator;

import com.mimer.ws.validateSQL.ValidatorResult;

/**
 * This <CODE>ICommand</CODE> will validate the passed SQL.
 *
 * @author	<A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ValidateSQLCommand implements ICommand
{
   private final WebServicePreferences _prefs;
   private final WebServiceSessionProperties _wsSessionProps;
   private final String _sql;
   private final String _stmtSep;
   private final String _solComment;
   private SessionProperties _sessionProperties;
   private String _results;
   private final ISession _session;
   
   /** Logger for this class. */
   private final static ILogger s_log =
      LoggerController.createLogger(ValidateSQLCommand.class);

   public ValidateSQLCommand(WebServicePreferences prefs,
                             WebServiceSessionProperties wsSessionProps, String sql,
                             String stmtSep, String solComment, 
                             SessionProperties sessionProperties,
                             ISession session)
   {
      super();
      _prefs = prefs;
      _wsSessionProps = wsSessionProps;
      _sql = sql;
      _stmtSep= stmtSep;
      _solComment = solComment;
      _sessionProperties = sessionProperties;
      _session = session;
   }

   public void openSession(WebServiceSession info)
   {
      if (info == null)
      {
         throw new IllegalArgumentException("ValidationInfo == null");
      }
   }

   public String getResults()
   {
      return _results;
   }

   public void execute() throws BaseException
   {
      try
      {
         // Open connection to the webservice.
         WebServiceSession wss = new WebServiceSession(_prefs,_wsSessionProps);
         wss.open();

         final WebServiceValidator val = new WebServiceValidator(wss, _wsSessionProps);
         final IQueryTokenizer qt = _session.getQueryTokenizer();

         qt.setScriptToTokenize(_sql);
         final StringBuffer results = new StringBuffer(1024);
         while (qt.hasQuery())
         {
            // TODO: When message are can have some text in red (error)
            // and some normal then put out errors in red.
            ValidatorResult rc = val.validate(qt.nextQuery());
            results.append(rc.getData());
         }
         _results = results.toString().trim();

      }
      catch (Throwable th)
      {
         throw new BaseException(th);
      }
   }
}

