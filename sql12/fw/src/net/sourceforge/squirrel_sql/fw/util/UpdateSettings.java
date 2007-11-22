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
package net.sourceforge.squirrel_sql.fw.util;

/**
 * Software update settings.
 */
public class UpdateSettings implements Cloneable
{

	/** Name of software update server. */
	private String updateServer = "http://www.squirrelsql.org";

	/** Port for HTTP Proxy server. */
	private String updateServerPort = "80";

	/** User name for HTTP Proxy server. */
	private String updateServerPath = "releases";

	/** Password for HTTP Proxy server. */
	private String updateServerChannel = "STABLE";

	/** If <TT>true</TT> use a SOCKS proxy server. */
	private boolean enableAutomaticUpdates = true;

	/** How often to auto check - at startup / weekly */
	private String updateCheckFrequency = "WEEKLY";

	/** The last time an update check was made in milliseconds */
	private String lastUpdateCheckTimeMillis = "0";
	
	/**
	 * Return a copy of this object.
	 */
	public Object clone()
	{
		try
		{
			return super.clone();
		}
		catch(CloneNotSupportedException ex)
		{
			throw new InternalError(ex.getMessage()); // Impossible.
		}
	}

   /**
    * @return the updateServer
    */
   public String getUpdateServer() {
      return updateServer;
   }

   /**
    * @param updateServer the updateServer to set
    */
   public void setUpdateServer(String updateServer) {
      this.updateServer = updateServer;
   }

   /**
    * @return the updateServerPort
    */
   public String getUpdateServerPort() {
      return updateServerPort;
   }

   /**
    * @param updateServerPort the updateServerPort to set
    */
   public void setUpdateServerPort(String updateServerPort) {
      this.updateServerPort = updateServerPort;
   }

   /**
    * @return the updateServerPath
    */
   public String getUpdateServerPath() {
      return updateServerPath;
   }

   /**
    * @param updateServerPath the updateServerPath to set
    */
   public void setUpdateServerPath(String updateServerPath) {
      this.updateServerPath = updateServerPath;
   }

   /**
    * @return the updateServerChannel
    */
   public String getUpdateServerChannel() {
      return updateServerChannel;
   }

   /**
    * @param updateServerChannel the updateServerChannel to set
    */
   public void setUpdateServerChannel(String updateServerChannel) {
      this.updateServerChannel = updateServerChannel;
   }

   /**
    * @return the enableAutomaticUpdates
    */
   public boolean isEnableAutomaticUpdates() {
      return enableAutomaticUpdates;
   }

   /**
    * @param enableAutomaticUpdates the enableAutomaticUpdates to set
    */
   public void setEnableAutomaticUpdates(boolean enableAutomaticUpdates) {
      this.enableAutomaticUpdates = enableAutomaticUpdates;
   }

   /**
    * @return the updateCheckFrequency
    */
   public String getUpdateCheckFrequency() {
      return updateCheckFrequency;
   }

   /**
    * @param updateCheckFrequency the updateCheckFrequency to set
    */
   public void setUpdateCheckFrequency(String updateCheckFrequency) {
      this.updateCheckFrequency = updateCheckFrequency;
   }

   /**
    * @return the lastUpdateCheckTimeMillis
    */
   public String getLastUpdateCheckTimeMillis() {
      return lastUpdateCheckTimeMillis;
   }

   /**
    * @param lastUpdateCheckTimeMillis the lastUpdateCheckTimeMillis to set
    */
   public void setLastUpdateCheckTimeMillis(String lastUpdateCheckTimeMillis) {
      this.lastUpdateCheckTimeMillis = lastUpdateCheckTimeMillis;
   }

}