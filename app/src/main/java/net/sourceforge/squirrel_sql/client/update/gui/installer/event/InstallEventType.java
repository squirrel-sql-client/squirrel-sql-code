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


/**
 * A type-safe enum to describe the type of an install event.
 * 
 * @author manningr
 *
 */
public enum InstallEventType {

	/** change list initialization phase has started */
	INIT_CHANGELIST_STARTED,
	
	FILE_INIT_CHANGELIST_STARTED,
	
	FILE_INIT_CHANGELIST_COMPLETE,
	
	/** change list initialization phase is complete */
	INIT_CHANGELIST_COMPLETE,
	
	/** backup phase has started */
   BACKUP_STARTED,
   
   /** a particular file is about to be backed up */
   FILE_BACKUP_STARTED,
   
   /** a particular file has just been successfully backed up */
   FILE_BACKUP_COMPLETE,
   
   /** backup phase has completed successfully */
   BACKUP_COMPLETE,
   
   /** File removal phase has been started */
   REMOVE_STARTED,
   
   /** file remove has started */
   FILE_REMOVE_STARTED,
   
   /** file remove completed */
   FILE_REMOVE_COMPLETE,
   
   /** File removal phase has completed successfully */
   REMOVE_COMPLETE,
   
   /** install phase has started */
   INSTALL_STARTED,

   /** a particular file is about to be installed */
   FILE_INSTALL_STARTED,
   
   /** a particular file has just been successfully installed */
   FILE_INSTALL_COMPLETE,   
   
   /** install phase has completed successfully */
   INSTALL_COMPLETE
   
}
