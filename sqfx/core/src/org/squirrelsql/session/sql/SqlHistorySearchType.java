package org.squirrelsql.session.sql;

import org.squirrelsql.services.I18n;

public enum SqlHistorySearchType
{
   CONTAINS(I18nHelper.i18n.t("SqlHistorySearchType.CONTAINS")),
   STARTS_WITH(I18nHelper.i18n.t("SqlHistorySearchType.STARTS_WITH")),
   ENDS_WITH(I18nHelper.i18n.t("SqlHistorySearchType.ENDS_WITH")),
   REG_EXP(I18nHelper.i18n.t("SqlHistorySearchType.REG_EXP"));

   private String _toString;

   SqlHistorySearchType(String toString)
   {
      _toString = toString;
   }

   @Override
   public String toString()
   {
      return _toString;
   }

   private static class I18nHelper
   {
      private static I18n i18n = new I18n(SqlHistorySearchType.class);
   }

}
