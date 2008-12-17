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

import net.sourceforge.squirrel_sql.fw.util.FileWrapper;

public class InstallFileOperationInfoImpl implements InstallFileOperationInfo {

   private FileWrapper fileToInstall;
   private FileWrapper installDir;
   private boolean isPlugin;
   private String artifactName;
   
   /**
    * @param fileToInstall
    * @param installDir
    */
   public InstallFileOperationInfoImpl(FileWrapper fileToInstall, FileWrapper installDir) {
      super();
      this.fileToInstall = fileToInstall;
      this.installDir = installDir;
   }
   
   /**
    * @see net.sourceforge.squirrel_sql.client.update.gui.installer.util.InstallFileOperationInfo#getFileToInstall()
    */
   public FileWrapper getFileToInstall() {
      return fileToInstall;
   }
   /**
    * @param fileToInstall the fileToInstall to set
    */
   public void setFileToInstall(FileWrapper fileToInstall) {
      this.fileToInstall = fileToInstall;
   }
   /**
    * @see net.sourceforge.squirrel_sql.client.update.gui.installer.util.InstallFileOperationInfo#getInstallDir()
    */
   public FileWrapper getInstallDir() {
      return installDir;
   }
   /**
    * @return the isPlugin
    */
   public boolean isPlugin() {
   	return isPlugin;
   }

	/**
    * @param isPlugin the isPlugin to set
    */
   public void setPlugin(boolean isPlugin) {
   	this.isPlugin = isPlugin;
   }

	/**
    * @param installDir the installDir to set
    */
   public void setInstallDir(FileWrapper installDir) {
      this.installDir = installDir;
   }

	/**
	 * @param artifactName the artifactName to set
	 */
	public void setArtifactName(String artifactName)
	{
		this.artifactName = artifactName;
	}

	/**
	 * @return the artifactName
	 */
	public String getArtifactName()
	{
		return artifactName;
	} 
}
