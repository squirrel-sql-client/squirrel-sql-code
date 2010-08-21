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
package net.sourceforge.squirrel_sql.client.update.xmlbeans;

import java.io.Serializable;

/**
 * This describes a single artifact.  An artifact is simply a file resource 
 * required in a given release. 
 * 
 * @author manningr
 */
public class ArtifactXmlBean implements Serializable {

    private static final long serialVersionUID = -6653935534454353144L;

    private String name;

    private String type;

    private String version;

    private long size;

    private long checksum;

    private boolean installed = false;
    
    public ArtifactXmlBean() {

    }

    public ArtifactXmlBean(String name, String type, String version, long size,
            long checksum) {

        this.name = name;
        this.type = type;
        this.version = version;
        this.size = size;
        this.checksum = checksum;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @return the size
     */
    public long getSize() {
        return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(long size) {
        this.size = size;
    }

    /**
     * @return the checksum
     */
    public long getChecksum() {
        return checksum;
    }

    /**
     * @param checksum the checksum to set
     */
    public void setChecksum(long checksum) {
        this.checksum = checksum;
    }

   /**
    * @return the installed
    */
   public boolean isInstalled() {
      return installed;
   }

   /**
    * @param installed the installed to set
    */
   public void setInstalled(boolean installed) {
      this.installed = installed;
   }

   /**
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (int) (checksum ^ (checksum >>> 32));
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + (int) (size ^ (size >>> 32));
      result = prime * result + ((type == null) ? 0 : type.hashCode());
      result = prime * result + ((version == null) ? 0 : version.hashCode());
      return result;
   }

   /**
    * Artifacts that differ only by whether or not they are installed are still
    * considered equal for the purpose of figuring out whether the user has the
    * latest software.
    * 
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      final ArtifactXmlBean other = (ArtifactXmlBean) obj;
      if (checksum != other.checksum)
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (size != other.size)
         return false;
      if (type == null) {
         if (other.type != null)
            return false;
      } else if (!type.equals(other.type))
         return false;
      if (version == null) {
         if (other.version != null)
            return false;
      } else if (!version.equals(other.version))
         return false;
      return true;
   }

	/**
	  * Constructs a <code>String</code> with all attributes
	  * in name = value format.
	  *
	  * @return a <code>String</code> representation 
	  * of this object.
	  */
	 @Override
	 public String toString()
	 {
	     final String TAB = "    ";
	 
	     StringBuilder retValue = new StringBuilder();
	     
	     retValue.append("ArtifactXmlBean ( ")
	         .append(super.toString()).append(TAB)
	         .append("name = ").append(this.name).append(TAB)
	         .append("type = ").append(this.type).append(TAB)
	         .append("version = ").append(this.version).append(TAB)
	         .append("size = ").append(this.size).append(TAB)
	         .append("checksum = ").append(this.checksum).append(TAB)
	         .append("installed = ").append(this.installed).append(TAB)
	         .append(" )");
	     
	     return retValue.toString();
	 }

   
}
