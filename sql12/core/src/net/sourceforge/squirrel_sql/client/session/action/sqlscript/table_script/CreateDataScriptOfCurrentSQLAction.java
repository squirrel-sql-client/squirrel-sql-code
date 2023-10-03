package net.sourceforge.squirrel_sql.client.session.action.sqlscript.table_script;
/*
 * Copyright (C) 2001 Johan Compagner
 * jcompagner@j-com.nl
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

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ISQLPanelAction;
import net.sourceforge.squirrel_sql.client.session.action.sqlscript.FrameWorkAcessor;
import net.sourceforge.squirrel_sql.client.session.action.sqlscript.table_script.scriptbuilder.StringScriptBuilder;
import net.sourceforge.squirrel_sql.client.session.mainpanel.sqltab.BaseSQLTab;

import java.awt.event.ActionEvent;

public class CreateDataScriptOfCurrentSQLAction extends SquirrelAction implements ISQLPanelAction
{
   private ISession _session;


   public CreateDataScriptOfCurrentSQLAction()
   {
      super(Main.getApplication(), Main.getApplication().getResources());
   }

   public void actionPerformed(ActionEvent evt)
   {
      if (_session != null)
      {
         StringScriptBuilder sbRows = new StringScriptBuilder();
         new CreateInsertScriptOfCurrentSQLCommand(_session).generateInserts(sbRows, () -> onScriptFinished(sbRows));
      }
   }

   private void onScriptFinished(StringScriptBuilder ssb)
   {
      StringBuilder sb = ssb.getStringBuilder();
      if (sb.length() > 0)
      {
         FrameWorkAcessor.getSQLPanelAPI(_session).appendSQLScript(sb.toString(), true);

         if (false == _session.getSelectedMainTab() instanceof BaseSQLTab)
         {
            _session.selectMainTab(ISession.IMainPanelTabIndexes.SQL_TAB);
         }
      }
   }


   public void setSQLPanel(ISQLPanelAPI panel)
   {
      if (null != panel)
      {
         _session = panel.getSession();
      }
      else
      {
         _session = null;
      }
      setEnabled(null != _session);
   }
}
