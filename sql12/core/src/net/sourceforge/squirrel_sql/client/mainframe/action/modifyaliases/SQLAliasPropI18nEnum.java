package net.sourceforge.squirrel_sql.client.mainframe.action.modifyaliases;

import net.sourceforge.squirrel_sql.client.gui.db.AliasInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.db.aliasproperties.SchemaPropertiesPanel;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public enum SQLAliasPropI18nEnum
{
   aliasName("AliasInternalFrame.name", AliasInternalFrame.class),
   schemaProp_byLikeStringInclude("SchemaPropertiesPanel.specifySchemasByLikeString.include", SchemaPropertiesPanel.class),
   schemaProp_byLikeStringExclude("SchemaPropertiesPanel.specifySchemasByLikeString.exclude", SchemaPropertiesPanel.class);

   private final String _i18nKey;
   private final Class _i18nSourceClass;

   SQLAliasPropI18nEnum(String i18nKey, Class i18nSourceClass)
   {
      _i18nKey = i18nKey;
      _i18nSourceClass = i18nSourceClass;
   }

   public String getString()
   {
      return StringManagerFactory.getStringManager(_i18nSourceClass).getString(_i18nKey);
   }
}
