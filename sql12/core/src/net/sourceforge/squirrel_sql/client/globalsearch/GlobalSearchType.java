package net.sourceforge.squirrel_sql.client.globalsearch;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public enum GlobalSearchType
{
   CONTAINS (I18n.s_stringMgr.getString("GlobalSearchType.contains")),
   CONTAINS_IGNORE_CASE(I18n.s_stringMgr.getString("GlobalSearchType.contains.ignore.case")),
   EXACT (I18n.s_stringMgr.getString("GlobalSearchType.exact")),
   STARTS_WITH (I18n.s_stringMgr.getString("GlobalSearchType.startsWith")),
   ENDS_WITH (I18n.s_stringMgr.getString("GlobalSearchType.endsWith")),
   REG_EX (I18n.s_stringMgr.getString("GlobalSearchType.regEx"));

   private interface I18n
   {
      StringManager s_stringMgr = StringManagerFactory.getStringManager(GlobalSearchType.class);
   }


   private final String _description;

   GlobalSearchType(String description)
   {
      _description = description;
   }


}
