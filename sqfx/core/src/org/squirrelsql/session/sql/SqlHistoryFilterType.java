package org.squirrelsql.session.sql;

import org.squirrelsql.services.I18n;

public enum SqlHistoryFilterType
{
   CONTAINS(I18nHelper.i18n.t("SqlHistoryFilterType.CONTAINS")),
   STARTS_WITH(I18nHelper.i18n.t("SqlHistoryFilterType.STARTS_WITH")),
   ENDS_WITH(I18nHelper.i18n.t("SqlHistoryFilterType.ENDS_WITH")),
   REG_EXP(I18nHelper.i18n.t("SqlHistoryFilterType.REG_EXP"));

   private String _toString;

   SqlHistoryFilterType(String toString)
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
      private static I18n i18n = new I18n(SqlHistoryFilterType.class);
   }

}
