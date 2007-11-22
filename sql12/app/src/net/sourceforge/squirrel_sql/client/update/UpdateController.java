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
package net.sourceforge.squirrel_sql.client.update;

public interface UpdateController {

   /**
    * Returns a boolean value indicating whether or not there are updates
    * available to be installed. The sequence of steps involved is :
    * 
    * 1. Find the local release.xml file 2. Load the local release.xml file as a
    * ChannelXmlBean. 3. Determine the channel that the user has (stable or
    * snapshot) 4. Get the release.xml file as a ChannelXmlBean from the server
    * 5. Determine if it is the same as the local copy, which was placed either
    * by the installer or the last update?
    * 
    * @return true if the installed software is latest; false otherwise.
    */
   boolean isUpToDate() throws Exception;

   String getUpdateServerName();

   String getUpdateServerPort();

   String getUpdateServerPath();

   String getUpdateServerChannel();

   int getUpdateServerPortAsInt();

   void showMessage(String title, String msg);
   
   void showUpdateDialog();
   
   void showErrorMessage(String title, String msg);
   
   void checkUpToDate();
}
