package net.sourceforge.squirrel_sql.client.session.action;
/*
 * Copyright (C) 2002-2004 Colin Bell
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

import java.awt.event.ActionEvent;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLPanel;
import net.sourceforge.squirrel_sql.client.session.parser.IParserEventsProcessor;
import net.sourceforge.squirrel_sql.client.session.schemainfo.SchemaInfoUpdateListener;
import net.sourceforge.squirrel_sql.fw.gui.CursorChanger;

/**
 * This <CODE>Action</CODE> will refresh the object tree.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class RefreshSchemaInfoAction extends SquirrelAction implements ISessionAction
{
   /** Current Object Tree. */
   private ISession _session;
   private SchemaInfoUpdateListener _schemaInfoUpdateListener;

   /**
    * Ctor specifying application API.
    */
   public RefreshSchemaInfoAction(IApplication app)
   {
      super(app);
   }

   public void setSession(ISession session)
   {
      _session = session;
   }

   public void actionPerformed(ActionEvent evt)
   {

      if (_session != null)
      {
         IObjectTreeAPI objectTreeAPI = _session.getSessionPanel().getObjectTreePanel();
         CursorChanger cursorChg = new CursorChanger(getApplication().getMainFrame());
         cursorChg.show();
         try
         {
            new RefreshObjectTreeCommand(objectTreeAPI).execute();
         }
         finally
         {
            cursorChg.restore();


            if(null != _schemaInfoUpdateListener)
            {
               _session.getSchemaInfo().removeSchemaInfoUpdateListener(_schemaInfoUpdateListener);
            }

            _schemaInfoUpdateListener = () -> onSchemaInfoUpdated(_session);

            _session.getSchemaInfo().addSchemaInfoUpdateListener(_schemaInfoUpdateListener);
         }
      }
   }

   private void onSchemaInfoUpdated(ISession session)
   {
      for (SQLPanel sqlPanel: _session.getSessionPanel().getAllSQLPanels())
      {
         final IParserEventsProcessor parserEventsProcessor = session.getParserEventsProcessor(sqlPanel.getSQLEntryPanel().getIdentifier());
         parserEventsProcessor.triggerParser();
         _session.getSchemaInfo().removeSchemaInfoUpdateListener(_schemaInfoUpdateListener);
         _schemaInfoUpdateListener = null;
      }
   }
}

