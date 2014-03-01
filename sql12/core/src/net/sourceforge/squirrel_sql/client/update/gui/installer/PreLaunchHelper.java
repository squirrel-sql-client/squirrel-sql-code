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
package net.sourceforge.squirrel_sql.client.update.gui.installer;

import java.io.IOException;

/**
 * Interface for the class that does the heavy lifting with regard to checking for and installing any updates
 * prior to launching the application.
 */
public interface PreLaunchHelper
{
	/**
	 * Installs updates that have been downloaded previously.
	 * 
	 * @param prompt if true, this will present the user with a dialog asking them whether or not updates 
	 * should be applied. 
	 */
	void installUpdates(boolean prompt);

	/**
	 * This will prompt the user to be sure they want to revert to the previous version and restore from 
	 * backup if that is the case.
	 */
	void restoreFromBackup();

	/**
	 * Updates the launch script with changes made necessary by the new release.
	 *   
	 * @throws IOException if an I/O error occurs
	 */
	public void updateLaunchScript() throws IOException;
	
	/**
	 * Copies the splash image from 
	 * @throws IOException
	 */
	public void copySplashImage() throws IOException;

}