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
 * An abstraction for update channel combobox entries.  This just allows
 * the label to be internationalized, while still allowing for the application
 * to know what the user's preference for update channel is.
 *  
 * @author manningr
 */
public class UpdateChannelComboBoxEntry {

   /**
    * An enumeration that defines the different update check channels that 
    * a user can choose
    */   
   public enum ChannelType {
      STABLE, SNAPSHOT
   }

   /** the channel to use for software updates */
   private ChannelType _channel = null;

   /** the label to show to the user for this software update channel */
   private String _displayName = null;

   /**
    * Construct a new update channel entry
    * 
    * @param channel
    *           the channel to use for software updates
    * @param displayName
    *           the label to show to the user for this software update channel
    */
   public UpdateChannelComboBoxEntry(ChannelType channel, String displayName) {
      _channel = channel;
      _displayName = displayName;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#toString()
    */
   public String toString() {
      return _displayName;
   }

   
   /**
    * @return returns a boolean indicating whether or not the channel is for 
    * "stable" updates.
    */
   public boolean isStable() {
      return _channel == ChannelType.STABLE;
   }

   /**
    * @return returns a boolean indicating whether or not the channel is for 
    * "snapshot" updates.
    */   
   public boolean isSnapshot() {
      return _channel == ChannelType.SNAPSHOT;
   }

}
