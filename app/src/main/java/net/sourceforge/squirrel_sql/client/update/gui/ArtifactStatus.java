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
package net.sourceforge.squirrel_sql.client.update.gui;

import java.io.Serializable;

import net.sourceforge.squirrel_sql.client.update.UpdateUtil;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ArtifactXmlBean;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

/**
 * Data object that contains information about available artifact updates.  Each
 * instance describes the status of one single update artifact.
 * 
 * @author manningr
 */
public class ArtifactStatus implements Serializable {

   private transient static final long serialVersionUID = 3902196017013411091L;

   /** Internationalized strings for this class. */
   private transient static final StringManager s_stringMgr = 
      StringManagerFactory.getStringManager(ArtifactStatus.class);
   
   private interface i18n extends Serializable {
      //i18n[ArtifactStatus.translationLabel=translation]
      String TRANSLATION_LABEL = s_stringMgr.getString("ArtifactStatus.translationLabel");
      
      //i18n[ArtifactStatus.coreLabel=core]
      String CORE_LABEL = s_stringMgr.getString("ArtifactStatus.coreLabel");
      
      //i18n[ArtifactStatus.pluginLabel=plugin]
      String PLUGIN_LABEL = s_stringMgr.getString("ArtifactStatus.pluginLabel");
   }
      
   /** the name of the artifact */
   private String name = null;
   
   /** the type of the artifact (e.g. Core, Plugin, Translation (I18n)) */
   private String type;
   
   /** boolean indicating whether or not the artifact is currently installed */
   private boolean installed;
   
   /** The type of this artifact, which is displayed to the user */
   private String displayType;
   
   /** The action to take with this artifact */
   private ArtifactAction artifactAction = ArtifactAction.NONE;

   /** The size of the artifact in bytes */
	private long size;

	/** The checksum of the artifact */
	private long checksum;
   
	public ArtifactStatus() {}
	
	/**
	 * Constructs an ArtifactStatus from the specified ArtifactXmlBean
	 * 
	 * @param artifactXmlBean
	 */
	public ArtifactStatus(ArtifactXmlBean artifactXmlBean) {
		this.name = artifactXmlBean.getName();
		this.installed = artifactXmlBean.isInstalled();
		this.size = artifactXmlBean.getSize();
		this.checksum = artifactXmlBean.getChecksum();
		setType(artifactXmlBean.getType());
	}
	
   /**
    * @return the _name
    */
   public String getName() {
      return name;
   }

   /**
    * @param name the _name to set
    */
   public void setName(String name) {
      this.name = name;
   }

   /**
    * @return the _type
    */
   public String getType() {
      return type;
   }

   /**
    * @param type the _type to set
    */
   public void setType(String type) {
      this.type = type;
   	if (type == null) {
   		return;
   	}
      if (type.equals(UpdateUtil.TRANSLATION_ARTIFACT_ID)) {
         this.displayType = i18n.TRANSLATION_LABEL;
      }
      if (type.equals(UpdateUtil.CORE_ARTIFACT_ID)) {
         this.displayType = i18n.CORE_LABEL;
      }
      if (type.equals(UpdateUtil.PLUGIN_ARTIFACT_ID)) {
         this.displayType = i18n.PLUGIN_LABEL;
      }      
   }

   public boolean isCoreArtifact() {
      return UpdateUtil.CORE_ARTIFACT_ID.equals(this.type);
   }
   
   public boolean isPluginArtifact() {
      return UpdateUtil.PLUGIN_ARTIFACT_ID.equals(this.type);
   }
   
   public boolean isTranslationArtifact() {
      return UpdateUtil.TRANSLATION_ARTIFACT_ID.equals(this.type);
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
    * @return the artifactAction
    */
   public ArtifactAction getArtifactAction() {
      return artifactAction;
   }

   /**
    * @param action the artifactAction to set
    */
   public void setArtifactAction(ArtifactAction artifactAction) {
      this.artifactAction = artifactAction;
   }

   /**
    * @return the displayType
    */
   public String getDisplayType() {
      return displayType;
   }

   /**
    * @param displayType the displayType to set
    */
   public void setDisplayType(String displayType) {
      this.displayType = displayType;
   }

   /**
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((type == null) ? 0 : type.hashCode());
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
      final ArtifactStatus other = (ArtifactStatus) obj;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (type == null) {
         if (other.type != null)
            return false;
      } else if (!type.equals(other.type))
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
	 public String toString()
	 {
	     final String TAB = "    ";
	     
	     String retValue = "";
	     
	     retValue = "ArtifactStatus ( "
	         + super.toString() + TAB
	         + "name = " + this.name + TAB
	         + "type = " + this.type + TAB
	         + "installed = " + this.installed + TAB
	         + "displayType = " + this.displayType + TAB
	         + "artifactAction = " + this.artifactAction + TAB
	         + " )";
	 
	     return retValue;
	 }
	 
	/**
	 * Sets the size (in bytes) of the artifact.
	 * 
	 * @param size the size of the file.
	 */
	public void setSize(long size)
	{
		this.size = size;
	}

	/**
	 * @return the size
	 */
	public long getSize()
	{
		return size;
	}
	
	/**
	 * Sets the checksum of the file.
	 * 
	 * @param checksum the checksum of the file.
	 */
	public void setChecksum(long checksum)
	{
		this.checksum = checksum;
	}

	/**
	 * Returns the checksum of the file.
	 * 
	 * @return the checksum the checksum of the file.
	 */
	public long getChecksum()
	{
		return checksum;
	}

   
   
   
}
