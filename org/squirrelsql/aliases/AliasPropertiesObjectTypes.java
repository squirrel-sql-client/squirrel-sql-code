package org.squirrelsql.aliases;

import org.squirrelsql.services.I18n;

public enum AliasPropertiesObjectTypes
{
   TABLE(Helper.i18n.t("alias.properties.object.type.tables"), 1),
   VIEW(Helper.i18n.t("alias.properties.object.type.views"), 2),
   PROCEDURE(Helper.i18n.t("alias.properties.object.type.procedures"), 3);

   private static class Helper
   {
      private static I18n i18n = new I18n(SchemaLoadOptions.class);
   }

   private String _toString;
   private int _colIx;

   AliasPropertiesObjectTypes(String toString, int colIx)
   {
      _toString = toString;
      _colIx = colIx;
   }

   public int getColIx()
   {
      return _colIx;
   }

   @Override
   public String toString()
   {
      return _toString;
   }


}
