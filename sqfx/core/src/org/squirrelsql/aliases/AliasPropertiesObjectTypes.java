package org.squirrelsql.aliases;

import org.squirrelsql.services.I18n;

public enum AliasPropertiesObjectTypes
{
   TABLE(I18nHelper.i18n.t("alias.properties.object.type.tables")),
   VIEW(I18nHelper.i18n.t("alias.properties.object.type.views")),
   PROCEDURE(I18nHelper.i18n.t("alias.properties.object.type.procedures")),
   OTHER_TABLE_TYPES(I18nHelper.i18n.t("alias.properties.object.type.othertabletypes"));

   private static class I18nHelper
   {
      private static I18n i18n = new I18n(SchemaLoadOptions.class);
   }

   private String _toString;

   AliasPropertiesObjectTypes(String toString)
   {
      _toString = toString;
   }

   @Override
   public String toString()
   {
      return _toString;
   }


}
