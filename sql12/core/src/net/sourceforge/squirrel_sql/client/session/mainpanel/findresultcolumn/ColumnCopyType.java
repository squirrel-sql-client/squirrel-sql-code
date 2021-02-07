package net.sourceforge.squirrel_sql.client.session.mainpanel.findresultcolumn;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public enum ColumnCopyType
{
   NAMES(I18nProvider.s_stringMgr.getString("ColumnCopyType.copy.selected.column.names")),
   NAMES_COMMA_SEPARATED(I18nProvider.s_stringMgr.getString("ColumnCopyType.copy.selected.column.names.comma.separated")),
   NAMES_QUALIFIED(I18nProvider.s_stringMgr.getString("ColumnCopyType.copy.selected.qualified.column.names")),
   NAMES_QUALIFIED_COMMA_SEPARATED(I18nProvider.s_stringMgr.getString("ColumnCopyType.copy.selected.qualified.column.names.comma.separated"));

   private interface I18nProvider
   {
      StringManager s_stringMgr = StringManagerFactory.getStringManager(ColumnCopyType.class);
   }


   private String _title;

   ColumnCopyType(String title)
   {

      _title = title;
   }

   public String getTitle()
   {
      return _title;
   }
}
