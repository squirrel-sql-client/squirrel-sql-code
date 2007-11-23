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
      
   /** the value for artifact type that identifies it as a core artifact */
   public static final String CORE_ARTIFACT_ID = "core";

   /** the value for artifact type that identifies it as a plugin artifact */
   public static final String PLUGIN_ARTIFACT_ID = "plugin";

   /** the value for artifact type that identifies it as a plugin artifact */
   public static final String TRANSLATION_ARTIFACT_ID = "i18n";   
   
   private String name = null;
   private String type;
   private boolean installed;
   private String displayType;
   private ArtifactAction artifactAction = ArtifactAction.NONE;
   
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
      if (type.equals("i18n")) {
         this.displayType = i18n.TRANSLATION_LABEL;
      }
      if (type.equals("core")) {
         this.displayType = i18n.CORE_LABEL;
      }
      if (type.equals("plugin")) {
         this.displayType = i18n.PLUGIN_LABEL;
      }      
   }

   public boolean isCoreArtifact() {
      return CORE_ARTIFACT_ID.equals(this.type);
   }
   
   public boolean isPluginArtifact() {
      return PLUGIN_ARTIFACT_ID.equals(this.type);
   }
   
   public boolean isTranslationArtifact() {
      return TRANSLATION_ARTIFACT_ID.equals(this.type);
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
   
   
}
