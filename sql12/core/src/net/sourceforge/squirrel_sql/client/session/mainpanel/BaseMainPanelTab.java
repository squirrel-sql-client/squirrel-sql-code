package net.sourceforge.squirrel_sql.client.session.mainpanel;
/*
 * Copyright (C) 2001-2003 Colin Bell
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
import net.sourceforge.squirrel_sql.client.session.filemanager.IFileEditorAPI;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import java.awt.*;

/**
 * Base class for tabs to the added to the main tabbed panel.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public abstract class BaseMainPanelTab implements IMainPanelTab
{
   private static ILogger s_log =LoggerController.createLogger(BaseMainPanelTab.class);

   private ISession _session;

   /**
    * Set to <TT>true</TT> once this tab has been displayed.
    */
   private boolean _hasBeenDisplayed;

   /**
    * Set the current session.
    *
    * @param   session   Current session.
    * @throws IllegalArgumentException Thrown if a <TT>null</TT> ISession</TT> passed.
    */
   public void setSession(ISession session)
   {
      if (session == null)
      {
         throw new IllegalArgumentException("Null ISession passed");
      }
      _session = session;
   }

   /**
    * The current session is closing.
    *
    * @param   session      Current session.
    */
   public void sessionClosing(ISession session)
   {
      // Empty method.
   }

   /**
    * Retrieve the current session.
    *
    * @return Current session.
    */
   public final ISession getSession()
   {
      return _session;
   }

   /**
    * This tab has been selected. This will call <TT>refreshComponent()</TT>
    * only if it hasn't been called.
    */
   public synchronized void select()
   {
      if (!_hasBeenDisplayed)
      {
         s_log.debug("Refreshing " + getTitle() + " main tab.");
         refreshComponent();
         _hasBeenDisplayed = true;
      }
   }

   @Override
   public void mouseWheelClickedOnTabComponent()
   {
   }

   @Override
   public void rightMouseClickedOnTabComponent(int clickPosX, int clickPosY)
   {
   }

   @Override
   public IFileEditorAPI getActiveFileEditorAPIOrNull()
   {
      return null;
   }

   @Override
   public Component getTabComponent()
   {
      return null;
   }

   @Override
   public String getTitle()
   {
      return "";
   }

   /**
    * @see IMainPanelTab#select()
    */
   protected void refreshComponent()
   {
   }

   /**
    * When a session is ending, if it happens to be the one this class is using,
    * then set our reference to null to allow it to be GC'd.
    *
    * @param session the session that is ending.
    */
   public void sessionEnding(ISession session)
   {
      if (_session == session)
      {
         _session = null;
      }
   }




}
