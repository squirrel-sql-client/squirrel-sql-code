package net.sourceforge.squirrel_sql.client.session.mainpanel.sqltab;
/*
 * Copyright (C) 2001-2004 Colin Bell
 * colbell@users.sourceforge.net
 *
 * Modifications Copyright (C) 2003-2004 Jason Height
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

import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.mainpanel.BaseMainPanelTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.IMainPanelTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLPanel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.SwingUtilities;
import java.awt.Component;

public abstract class BaseSQLTab extends BaseMainPanelTab
{

   /**
    * Component to be displayed.
    */
   private SQLPanel _sqlPanel;

   public BaseSQLTab(ISession session)
   {
      super.setSession(session);
   }


   /**
    * Return the component to be displayed in the panel.
    *
    * @return The component to be displayed in the panel.
    */
   public Component getComponent()
   {
      if(null == _sqlPanel)
      {
         _sqlPanel = createSqlPanel();
      }
      return _sqlPanel;
   }

   protected abstract SQLPanel createSqlPanel();

   /**
    * @see IMainPanelTab#setSession(ISession)
    */
   public void setSession(ISession session)
   {
      super.setSession(session);
      if (null != _sqlPanel)
      {
         _sqlPanel.setSession(session);
      }
   }


   /**
    * Sesssion is ending.
    */
   public void sessionClosing(ISession session)
   {
      _sqlPanel.sessionClosing();
   }

   /**
    * This tab has been selected. Set focus to the SQL entry area.
    */
   public void select()
   {
      super.select();
      SwingUtilities.invokeLater(() -> _sqlPanel.getSQLEntryPanel().requestFocus());
   }

   public SQLPanel getSQLPanel()
   {
      return _sqlPanel;
   }

   public ISQLPanelAPI getSQLPanelAPI()
   {
      return _sqlPanel.getSQLPanelAPI();
   }
}
