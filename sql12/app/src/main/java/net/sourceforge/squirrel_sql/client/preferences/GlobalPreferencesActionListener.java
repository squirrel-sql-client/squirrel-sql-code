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
package net.sourceforge.squirrel_sql.client.preferences;

/**
 * An interface to register as a listener for GlobalPreferences actions.
 * 
 * @author manningr
 */
public interface GlobalPreferencesActionListener {

   /**
    * This is called whenever global preferences are displayed.
    */
   public void onDisplayGlobalPreferences();
   
   /**
    * This is called when global preferences that were previously displayed are
    * saved by clicking the OK button
    */
   public void onPerformOk();
   
   /**
    * This is called when global preferences that were previously displayed are
    * reverted by clicking the Close button 
    */
   public void onPerformClose();
}
