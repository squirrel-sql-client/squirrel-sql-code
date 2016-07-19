package org.squirrelsql.sqlreformat;

import org.squirrelsql.services.I18n;

public enum ColumnListSpiltMode
{
   ALLOW_SPLIT("ColumnListSpiltMode.i18nkey.allow.spilt"),
   REQUIRE_SPLIT("ColumnListSpiltMode.i18nkey.require.spilt"),
   DISALLOW_SPLIT("ColumnListSpiltMode.i18nkey.disallow.spilt");

   private static final I18n _i18n = new I18n(ColumnListSpiltMode.class);
   private String _i18nKey;


   ColumnListSpiltMode(String i18nKey)
   {
      _i18nKey = i18nKey;
   }


   @Override
   public String toString()
   {
      return _i18n.t(_i18nKey);
   }
}
