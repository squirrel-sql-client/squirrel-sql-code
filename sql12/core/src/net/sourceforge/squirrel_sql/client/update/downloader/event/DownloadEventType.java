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
package net.sourceforge.squirrel_sql.client.update.downloader.event;

/**
 * An enumeration of the types of download events that can occur.
 * 
 * @author manningr
 */
public enum DownloadEventType {
   /** Started the process of downloading one or more files */
   DOWNLOAD_STARTED,
   /** User chose to stop the process of downloading one or more files */
   DOWNLOAD_STOPPED,
   /** The process of downloading one or more files failed for some reason */
   DOWNLOAD_FAILED,
   /** The process of downloading one or more files completed without error */
   DOWNLOAD_COMPLETED,
   /** The download of a single file started */
   DOWNLOAD_FILE_STARTED,
   /** The download of a single file completed */
   DOWNLOAD_FILE_COMPLETED
}