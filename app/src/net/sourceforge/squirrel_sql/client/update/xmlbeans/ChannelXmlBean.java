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

public class ChannelXmlBean {

    private String name;
    
    private ReleaseXmlBean currentRelease;

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
     * @return the currentRelease
     */
    public ReleaseXmlBean getCurrentRelease() {
        return currentRelease;
    }

    /**
     * @param currentRelease the currentRelease to set
     */
    public void setCurrentRelease(ReleaseXmlBean currentRelease) {
        this.currentRelease = currentRelease;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((currentRelease == null) ? 0 : currentRelease.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    /**
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
        final ChannelXmlBean other = (ChannelXmlBean) obj;
        if (currentRelease == null) {
            if (other.currentRelease != null)
                return false;
        } else if (!currentRelease.equals(other.currentRelease))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
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
	     
	     retValue.append("ChannelXmlBean ( ")
	         .append(super.toString()).append(TAB)
	         .append("name = ").append(this.name).append(TAB)
	         .append("currentRelease = ").append(this.currentRelease).append(TAB)
	         .append(" )");
	     
	     return retValue.toString();
	 }


    
    
}
