/*
 * Copyright (C) 2008 Rob Manning
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
package net.sourceforge.squirrel_sql.client.update.gui.installer.util;

import java.io.File;

public class InstallFileOperationInfoImpl implements InstallFileOperationInfo {

   private File fileToInstall;
   private File installDir;
   
   /**
    * @param fileToInstall
    * @param installDir
    */
   public InstallFileOperationInfoImpl(File fileToInstall, File installDir) {
      super();
      this.fileToInstall = fileToInstall;
      this.installDir = installDir;
   }
   
   /**
    * @see net.sourceforge.squirrel_sql.client.update.gui.installer.util.InstallFileOperationInfo#getFileToInstall()
    */
   public File getFileToInstall() {
      return fileToInstall;
   }
   /**
    * @param fileToInstall the fileToInstall to set
    */
   public void setFileToInstall(File fileToInstall) {
      this.fileToInstall = fileToInstall;
   }
   /**
    * @see net.sourceforge.squirrel_sql.client.update.gui.installer.util.InstallFileOperationInfo#getInstallDir()
    */
   public File getInstallDir() {
      return installDir;
   }
   /**
    * @param installDir the installDir to set
    */
   public void setInstallDir(File installDir) {
      this.installDir = installDir;
   } 
}
