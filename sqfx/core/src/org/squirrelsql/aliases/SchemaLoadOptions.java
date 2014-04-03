package org.squirrelsql.aliases;

import org.squirrelsql.services.I18n;

public enum SchemaLoadOptions
{
   LOAD_BUT_DONT_CACHE(Helper.i18n.t("alias.load.option.load.no.cache")),
   LOAD_AND_CACHE(Helper.i18n.t("alias.load.option.load.and.cache")),
   DONT_LOAD(Helper.i18n.t("alias.load.option.dont.load"));


   private static class Helper
   {
      private static I18n i18n = new I18n(SchemaLoadOptions.class);
   }


   private String _toString;

   SchemaLoadOptions(String toString)
   {

      _toString = toString;
   }


   @Override
   public String toString()
   {
      return _toString;
   }

}
