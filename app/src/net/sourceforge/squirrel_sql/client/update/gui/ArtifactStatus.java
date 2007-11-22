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

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

/**
 * Data object that contains information about available artifact updates.  Each
 * instance describes the status of one single update artifact.
 * 
 * @author manningr
 */
public class ArtifactStatus {

   /** Internationalized strings for this class. */
   private static final StringManager s_stringMgr = 
      StringManagerFactory.getStringManager(ArtifactStatus.class);
   
   private interface i18n {
      //i18n[ArtifactStatus.translationLabel=translation]
      String TRANSLATION_LABEL = s_stringMgr.getString("ArtifactStatus.translationLabel");
      
      //i18n[ArtifactStatus.coreLabel=core]
      String CORE_LABEL = s_stringMgr.getString("ArtifactStatus.coreLabel");
      
      //i18n[ArtifactStatus.pluginLabel=plugin]
      String PLUGIN_LABEL = s_stringMgr.getString("ArtifactStatus.pluginLabel");
   }
   
   public enum Action {
      NONE,
      INSTALL,
      REMOVE
   }
   
   /** the value for artifact type that identifies it as a core artifact */
   public static final String CORE_ARTIFACT_ID = "core";

   /** the value for artifact type that identifies it as a plugin artifact */
   public static final String PLUGIN_ARTIFACT_ID = "plugin";
   
   private String _name;
   private String _type;
   private boolean _installed;
   private String _displayType;
   
   private Action _action = null;
   
   public ArtifactStatus(String name, String type, boolean installed) {
      _name = name;
      setType(type);
      _installed = installed;
      _action = Action.NONE;
   }

   /**
    * @return the _name
    */
   public String getName() {
      return _name;
   }

   /**
    * @param _name the _name to set
    */
   public void setName(String _name) {
      this._name = _name;
   }

   /**
    * @return the _type
    */
   public String getType() {
      return _type;
   }

   /**
    * @param _type the _type to set
    */
   public void setType(String _type) {
      this._type = _type;
      if (_type.equals("i18n")) {
         _displayType = i18n.TRANSLATION_LABEL;
      }
      if (_type.equals("core")) {
         _displayType = i18n.CORE_LABEL;
      }
      if (_type.equals("plugin")) {
         _displayType = i18n.PLUGIN_LABEL;
      }      
   }

   public boolean isCoreArtifact() {
      return CORE_ARTIFACT_ID.equals(this._type);
   }
   
   public boolean isPluginArtifact() {
      return PLUGIN_ARTIFACT_ID.equals(this._type);
   }
   
   /**
    * @return the _installed
    */
   public boolean isInstalled() {
      return _installed;
   }

   /**
    * @param _installed the _installed to set
    */
   public void setInstalled(boolean _installed) {
      this._installed = _installed;
   }

   /**
    * @return the action
    */
   public Action getAction() {
      return _action;
   }

   /**
    * @param action the action to set
    */
   public void setAction(Action action) {
      this._action = action;
   }

   /**
    * @return the _displayType
    */
   public String get_displayType() {
      return _displayType;
   }

   /**
    * @param type the _displayType to set
    */
   public void set_displayType(String type) {
      _displayType = type;
   }
}
