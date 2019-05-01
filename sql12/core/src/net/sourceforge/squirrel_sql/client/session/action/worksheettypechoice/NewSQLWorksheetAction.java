package net.sourceforge.squirrel_sql.client.session.action.worksheettypechoice;
/*
 * Copyright (C) 2003-2004 Jason Height
 * jmheight@users.sourceforge.net
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
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.SquirrelActionChannel;
import net.sourceforge.squirrel_sql.client.action.ChanneledAction;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;
import net.sourceforge.squirrel_sql.client.session.mainpanel.sqltab.AdditionalSQLTab;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;

/**
 * According to the selection in the main tool bar a new main  window tab
 * or a new SQL tab inside the current Session is opened.
 */
public class NewSQLWorksheetAction extends SquirrelAction implements ISessionAction, ChanneledAction
{
   private ISession _session;

   private SquirrelActionChannel _squirrelActionChannel = new SquirrelActionChannel();
   private EnabledListener _enabledListener;

   public NewSQLWorksheetAction(IApplication app)
   {
      super(app);
      if (app == null)
      {
         throw new IllegalArgumentException("Null IApplication passed");
      }

      setEnabled(false);
   }

   public void actionPerformed(ActionEvent evt)
   {
      switch (SQLWorksheetTypeEnum.getSelectedType())
      {
         case SQL_WORKSHEET:
            getApplication().getWindowManager().createSQLInternalFrame(_session);
            break;
         case SQL_TAB:
            AdditionalSQLTab additionalSQLTab = new AdditionalSQLTab(_session);
            int tabIndex = _session.getSessionPanel().addMainTab(additionalSQLTab);
            _session.getSessionPanel().selectMainTab(tabIndex);
            Main.getApplication().getPluginManager().additionalSQLTabOpened(additionalSQLTab);
            _session.getSessionInternalFrame().moveToFront();
            break;
         default:
            throw new IllegalStateException("Unknown selected SQLWorksheetTypeEnum " + SQLWorksheetTypeEnum.getSelectedType());

      }
   }

   public void setSession(ISession session)
   {
      _session = session;
      GUIUtils.processOnSwingEventThread(() -> onEnabledChanged());
   }

   private void onEnabledChanged()
   {
      boolean enabled = null != _session;
      super.setEnabled(enabled);

      if(null != _enabledListener)
      {
         _enabledListener.enabledChanged(enabled);
      }
   }

   @Override
   public SquirrelActionChannel getActionChannel()
   {
      return _squirrelActionChannel;
   }

   public void setEnabledListener(EnabledListener enabledListener)
   {
      _enabledListener = enabledListener;
   }
}
