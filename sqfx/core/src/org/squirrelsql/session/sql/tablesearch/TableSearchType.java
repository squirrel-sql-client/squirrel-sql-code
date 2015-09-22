package org.squirrelsql.session.sql.tablesearch;

import org.squirrelsql.services.I18n;

public enum TableSearchType
{
   CONTAINS(I18nHelper.i18n.t("search.type.contains")),
   STARTS_WITH(I18nHelper.i18n.t("search.type.starts.with")),
   ENDS_WITH(I18nHelper.i18n.t("search.type.ends.with")),
   REG_EX(I18nHelper.i18n.t("search.type.regExp"));


   private static class I18nHelper
   {
      private static I18n i18n = new I18n(TableSearchType.class);
   }


   private String _toString;

   TableSearchType(String toString)
   {
      _toString = toString;
   }

   @Override
   public String toString()
   {
      return _toString;
   }
}
