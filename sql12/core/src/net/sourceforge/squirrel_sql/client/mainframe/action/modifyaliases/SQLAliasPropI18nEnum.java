package net.sourceforge.squirrel_sql.client.mainframe.action.modifyaliases;

import net.sourceforge.squirrel_sql.client.gui.db.AliasInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.db.aliasproperties.DriverPropertiesPanel;
import net.sourceforge.squirrel_sql.client.gui.db.aliasproperties.SchemaPropertiesPanel;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public enum SQLAliasPropI18nEnum
{
   aliasName("AliasInternalFrame.name", AliasInternalFrame.class),
   jdbcUrl("AliasInternalFrame.url", AliasInternalFrame.class),
   driverIdentifier("AliasInternalFrame.driver", AliasInternalFrame.class),
   userName("AliasInternalFrame.username", AliasInternalFrame.class),
   password("AliasInternalFrame.password", AliasInternalFrame.class),
   encryptPassword("AliasInternalFrame.password.encrypted", AliasInternalFrame.class),
   autoLogon("AliasInternalFrame.autologon", AliasInternalFrame.class),
   connectAtStartup("AliasInternalFrame.connectatstartup", AliasInternalFrame.class),
   useDriverProperties("DriverPropertiesPanel.useDriverProperties", DriverPropertiesPanel.class),
   driverPropertyCollection,
   schemaProp_schemaDetails,
   schemaProp_globalState,
   schemaProp_byLikeStringInclude("SchemaPropertiesPanel.specifySchemasByLikeString.include", SchemaPropertiesPanel.class),
   schemaProp_byLikeStringExclude("SchemaPropertiesPanel.specifySchemasByLikeString.exclude", SchemaPropertiesPanel.class),
   schemaProp_cacheSchemaIndependentMetaData("SchemaPropertiesPanel.CacheSchemaIndependentMetaData", SchemaPropertiesPanel.class),
   colorProp_overrideToolbarBackgroundColor,
   colorProp_toolbarBackgroundColor,
   colorProp_overrideObjectTreeBackgroundColor,
   colorProp_objectTreeBackgroundColor,
   colorProp_overrideStatusBarBackgroundColor,
   colorProp_statusBarBackgroundColor,
   colorProp_overrideAliasBackgroundColor,
   colorProp_aliasBackgroundColor;


   private final String _i18nKey;
   private final Class _i18nSourceClass;

   SQLAliasPropI18nEnum(String i18nKey, Class i18nSourceClass)
   {
      _i18nKey = i18nKey;
      _i18nSourceClass = i18nSourceClass;
   }

   SQLAliasPropI18nEnum()
   {
      this(null, null);
   }

   public String getString()
   {
      return StringManagerFactory.getStringManager(_i18nSourceClass).getString(_i18nKey);
   }

}
