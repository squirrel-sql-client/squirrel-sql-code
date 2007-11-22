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
 * An abstraction for update check frequency combobox entries.  This just allows
 * the label to be internationalized, while still allowing for the application
 * to know what the user's preference for update check frequency is.
 * 
 * @author manningr
 */
public class UpdateCheckFrequencyComboBoxEntry {

   /**
    * An enumeration that defines the different update check frequencies that 
    * a user can choose
    */
   public enum Frequency {
      AT_STARTUP, WEEKLY
   }
   
   /** the frequency with which to check the site for software updates */
   private Frequency _frequency = null;

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
   public UpdateCheckFrequencyComboBoxEntry(Frequency frequency, String displayName) {
      _frequency = frequency;
      _displayName = displayName;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#toString()
    */
   public String toString() {
      return _displayName;
   }

   /**
    * @return whether or not this frequency indicates that update checks should
    *         happen at application startup
    */
   public boolean isStartup() {
      return _frequency == Frequency.AT_STARTUP;
   }

   /**
    * @return whether or not this frequency indicates that update checks should
    *         happen approximately weekly
    */
   public boolean isWeekly() {
      return _frequency == Frequency.WEEKLY;
   }

}
