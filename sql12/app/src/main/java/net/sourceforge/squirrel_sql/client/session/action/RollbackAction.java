package net.sourceforge.squirrel_sql.client.session.action;
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
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import net.sourceforge.squirrel_sql.fw.gui.CursorChanger;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;

public class RollbackAction extends SquirrelAction implements ISessionAction
{
   private ISession _session;
   private PropertyChangeListener _propertyListener;

   public RollbackAction(IApplication app)
   {
      super(app);

      _propertyListener = new PropertyChangeListener()
      {
         public void propertyChange(PropertyChangeEvent evt)
         {
            if (SessionProperties.IPropertyNames.AUTO_COMMIT.equals(evt.getPropertyName()))
            {
               Boolean autoCom = (Boolean) evt.getNewValue();
               setEnabled(false == autoCom.booleanValue());
            }
         }
      };

      setEnabled(false);

   }


   public void setSession(ISession session)
   {
      _session = session;

      if(null != _session)
      {
         _session.getProperties().removePropertyChangeListener(_propertyListener);
      }
      _session = session;

      if (session == null)
      {
         setEnabled(false);
      }
      else
      {
         setEnabled(false == _session.getProperties().getAutoCommit());
         _session.getProperties().addPropertyChangeListener(_propertyListener);
      }
   }

   public void actionPerformed(ActionEvent evt)
   {
      CursorChanger cursorChg = new CursorChanger(getApplication().getMainFrame());
      cursorChg.show();
      try
      {
         IPlugin plugin = _session.getApplication().getDummyAppPlugin();
         _session.rollback();
      }
      finally
      {
         cursorChg.restore();
      }
   }
}
