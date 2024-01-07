package net.sourceforge.squirrel_sql.client.gui.db.modifyaliases;

import net.sourceforge.squirrel_sql.client.gui.db.AliasInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAliasSchemaDetailProperties;
import net.sourceforge.squirrel_sql.client.gui.db.aliasproperties.ConnectionPropertiesPanel;
import net.sourceforge.squirrel_sql.client.gui.db.aliasproperties.DriverPropertiesPanel;
import net.sourceforge.squirrel_sql.client.gui.db.aliasproperties.SchemaPropertiesPanel;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverPropertyCollection;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import java.util.Objects;

public enum SQLAliasPropType
{
   aliasName("AliasInternalFrame.name", AliasInternalFrame.class),
   jdbcUrl("AliasInternalFrame.url", AliasInternalFrame.class),
   driverIdentifier("AliasInternalFrame.driver", AliasInternalFrame.class),
   userName("AliasInternalFrame.username", AliasInternalFrame.class),
   password("AliasInternalFrame.password", AliasInternalFrame.class),
   encryptPassword("AliasInternalFrame.password.encrypted", AliasInternalFrame.class),
   autoLogon("AliasInternalFrame.autologon", AliasInternalFrame.class),
   connectAtStartup("AliasInternalFrame.connectatstartup", AliasInternalFrame.class),
   driverProp_useDriverProperties("DriverPropertiesPanel.useDriverProperties", DriverPropertiesPanel.class),
   driverProp_driverPropertyCollection,
   schemaProp_schemaDetails,
   schemaProp_globalState,
   schemaProp_byLikeStringInclude("SchemaPropertiesPanel.specifySchemasByLikeString.include", SchemaPropertiesPanel.class),
   schemaProp_byLikeStringExclude("SchemaPropertiesPanel.specifySchemasByLikeString.exclude", SchemaPropertiesPanel.class),
   schemaProp_cacheSchemaIndependentMetaData("SchemaPropertiesPanel.CacheSchemaIndependentMetaData", SchemaPropertiesPanel.class),
   schemaProp_schemaTableWasCleared_transientForMultiAliasModificationOnly,
   connectionProp_keepAliveSqlStatement,
   colorProp_overrideToolbarBackgroundColor,
   colorProp_toolbarBackgroundColor,
   colorProp_overrideObjectTreeBackgroundColor,
   colorProp_objectTreeBackgroundColor,
   colorProp_overrideStatusBarBackgroundColor,
   colorProp_statusBarBackgroundColor,
   colorProp_overrideAliasBackgroundColor,
   colorProp_aliasBackgroundColor,
   connectionProp_keepAlive("ConnectionPropertiesPanel.enableKeepAliveMsg", ConnectionPropertiesPanel.class),
   connectionProp_keepAliveSleepSeconds("ConnectionPropertiesPanel.sleepForLabel", ConnectionPropertiesPanel.class);


   private final String _i18nKey;
   private final Class _i18nSourceClass;

   SQLAliasPropType(String i18nKey, Class i18nSourceClass)
   {
      _i18nKey = i18nKey;
      _i18nSourceClass = i18nSourceClass;
   }

   SQLAliasPropType()
   {
      this(null, null);
   }

   public String getI18nString()
   {
      return StringManagerFactory.getStringManager(_i18nSourceClass).getString(_i18nKey);
   }

   public boolean isSchemaProp()
   {
      return this == schemaProp_schemaDetails ||
            this == schemaProp_globalState ||
            this == schemaProp_byLikeStringInclude ||
            this == schemaProp_byLikeStringExclude ||
            this == schemaProp_cacheSchemaIndependentMetaData ||
            this == schemaProp_schemaTableWasCleared_transientForMultiAliasModificationOnly;
   }

   public boolean isDriverProp()
   {
      return this == driverProp_useDriverProperties ||
            this == driverProp_driverPropertyCollection;
   }

   public boolean isColorProp()
   {
      return this == colorProp_overrideToolbarBackgroundColor ||
            this == colorProp_toolbarBackgroundColor ||
            this == colorProp_overrideObjectTreeBackgroundColor ||
            this == colorProp_objectTreeBackgroundColor ||
            this == colorProp_overrideStatusBarBackgroundColor ||
            this == colorProp_statusBarBackgroundColor ||
            this == colorProp_overrideAliasBackgroundColor ||
            this == colorProp_aliasBackgroundColor;
   }

   public boolean isConnectionProp()
   {
      return this == connectionProp_keepAliveSqlStatement ||
            this == connectionProp_keepAlive ||
            this == connectionProp_keepAliveSleepSeconds;
   }

   public boolean equals(Object previousAliasPropValue, Object editedAliasPropValue)
   {
      if(this == schemaProp_schemaDetails)
      {
         ChangeReport changeReport = new ChangeReport();
         AliasChangesUtil.compareSchemaDetailProperties((SQLAliasSchemaDetailProperties[]) editedAliasPropValue, (SQLAliasSchemaDetailProperties[]) previousAliasPropValue, changeReport);

         return 0 == changeReport.length();

      }
      else if(this == driverProp_driverPropertyCollection)
      {
         ChangeReport changeReport = new ChangeReport();
         AliasChangesUtil.compareSQLDriverPropertyCollection((SQLDriverPropertyCollection) editedAliasPropValue, (SQLDriverPropertyCollection) previousAliasPropValue, changeReport);

         return 0 == changeReport.length();
      }
      else
      {
         return Objects.equals(previousAliasPropValue, editedAliasPropValue);
      }
   }

   public boolean isNested()
   {
      return isColorProp() || isConnectionProp() || isSchemaProp() || isDriverProp();
   }
}
