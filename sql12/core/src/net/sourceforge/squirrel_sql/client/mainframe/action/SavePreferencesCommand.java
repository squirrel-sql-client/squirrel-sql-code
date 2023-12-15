package net.sourceforge.squirrel_sql.client.mainframe.action;
/*
 * Copyright (C) 2007 Rob Manning
 * manningr@users.sourceforge.net
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

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.gui.Dialogs;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import java.awt.*;

/**
 * This is fired to allow the user to save the application state in 
 * terms of preferences and properties. 
 */
public class SavePreferencesCommand implements ICommand
{
   /** Owner of the maintenance dialog. */
   private Frame _frame;

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SavePreferencesCommand.class);
   
   /**
    * Ctor.
    *
    * @param   frame      Owning <TT>Frame</TT>.
    * @param   sqlAlias   <SQLAlias</TT> to be deleted.
    *
    * @throws IllegalArgumentException
    *			Thrown if a <TT>null</TT> <TT>SQLAlias</TT> or
    *			<TT>IApplication</TT> passed.
    */
   public SavePreferencesCommand(Frame frame)
   {
      if (frame == null)
      {
         throw new IllegalArgumentException("frame cannot be null");
      }
      _frame = frame;
   }

   /**
    * Save the application state and let the user know when it's finished.
    */
   public void execute()
   {
      Main.getApplication().saveApplicationState();
      String msg = s_stringMgr.getString("SavePreferencesCommand.allPrefsSavedMsg");
      Dialogs.showOk(_frame, msg);
   }
}
