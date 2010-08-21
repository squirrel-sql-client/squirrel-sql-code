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

import net.sourceforge.squirrel_sql.client.update.UpdateCheckFrequency;

/**
 * An abstraction for update check frequency combo-box entries.  This just allows
 * the label to be internationalized, while still allowing for the application
 * to know what the user's preference for update check frequency is.
 */
public class UpdateCheckFrequencyComboBoxEntry {
   
   /** the frequency with which to check the site for software updates */
   private UpdateCheckFrequency _frequency = null;

   /** the label to show to the user for this software update frequency */
   private String _displayName = null;

   /**
    * Construct a new update check frequency entry
    * 
    * @param frequency
    *           the frequency with which to check the site for software updates
    * @param displayName
    *           the label to show to the user for this software update frequency
    */
   public UpdateCheckFrequencyComboBoxEntry(UpdateCheckFrequency frequency, String displayName) {
      _frequency = frequency;
      _displayName = displayName;
   }

   /**
    * @see java.lang.Object#toString()
    */
   public String toString() {
      return _displayName;
   }

   /**
    * @return the UpdateCheckFrequency Enum that is associated with this combo box entry.
    */
   public UpdateCheckFrequency getUpdateCheckFrequencyEnum() {
   	return _frequency;
   }
   
}
