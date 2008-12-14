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
package net.sourceforge.squirrel_sql.client.update.gui.installer.event;

import net.sourceforge.squirrel_sql.fw.util.Utilities;


/**
 * An event implementation that informs the installer of key events that are 
 * happening during installation.  The ArtifactInstaller generates these events as
 * it is installing artifacts giving them to any listeners.  The installer UI 
 * registers as a listener and updates the UI based on the events that it receives.
 */
public class InstallStatusEvent {

	/** The name of the artifact being installed; typically a filename (e.g. fw.jar) */
   private String _artifactName;
   
   /** The type of event that has occurred - see InstallEventType for detailed */
   private InstallEventType _type;
   
   /** The number of files that will be added, replaced or removed */
   private int numFilesToUpdate = 0;
   
   public InstallStatusEvent(InstallEventType type) {
   	Utilities.checkNull("InstallStatusEvent.init", "type", type);
      this._type = type;
   }

   /**
    * @return the _artifactName
    */
   public String getArtifactName() {
      return _artifactName;
   }

   /**
    * @param name the _artifactName to set
    */
   public void setArtifactName(String name) {
   	Utilities.checkNull("setArtifactName", "name", name);
      _artifactName = name;
   }

   /**
    * @return the event type
    */
   public InstallEventType getType() {
      return _type;
   }

   /**
    * @param _type the event type to set
    */
   public void setType(InstallEventType type) {
   	Utilities.checkNull("setType", "type", type);
      this._type = type;
   }

	/**
	 * @param numFilesToUpdate the numFilesToUpdate to set
	 */
	public void setNumFilesToUpdate(int numFilesToUpdate)
	{
		this.numFilesToUpdate = numFilesToUpdate;
	}

	/**
	 * @return the numFilesToUpdate
	 */
	public int getNumFilesToUpdate()
	{
		return numFilesToUpdate;
	}

   
}
