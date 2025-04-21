package net.sourceforge.squirrel_sql.fw.datasetviewer.tablefind;

import net.sourceforge.squirrel_sql.client.globalsearch.GlobalSearchType;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

enum DataSetSearchMatchType
{
   CONTAINS(I18n.s_stringMgr.getString("DataSetFindPanel.filterCboContains")),
   EXACT(I18n.s_stringMgr.getString("DataSetFindPanel.exact")),
   STARTS_WITH(I18n.s_stringMgr.getString("DataSetFindPanel.filterCboStartsWith")),
   ENDS_WITH(I18n.s_stringMgr.getString("DataSetFindPanel.filterCboEndsWith")),
   REG_EX(I18n.s_stringMgr.getString("DataSetFindPanel.filterCboRegEx"));
   private String _name;

   public static DataSetSearchMatchType ofGlobalSearchType(GlobalSearchType globalSearchType)
   {
      switch(globalSearchType)
      {
         case CONTAINS: return CONTAINS;
         case CONTAINS_IGNORE_CASE: return CONTAINS;
         case EXACT: return DataSetSearchMatchType.EXACT;
         case REG_EX: return DataSetSearchMatchType.REG_EX;
         case STARTS_WITH: return DataSetSearchMatchType.STARTS_WITH;
         case ENDS_WITH: return DataSetSearchMatchType.ENDS_WITH;
         default: throw new IllegalStateException("Unknown GlobalSearchType " + globalSearchType.name());
      }
   }

   interface I18n
   {
      StringManager s_stringMgr = StringManagerFactory.getStringManager(DataSetFindPanel.class);
   }

   DataSetSearchMatchType(String name)
   {
      _name = name;
   }


   public String toString()
   {
      return _name;
   }

   public GlobalSearchType getGlobalType()
   {
      switch(this)
      {
         case CONTAINS:
            return GlobalSearchType.CONTAINS_IGNORE_CASE;
         case EXACT:
            return GlobalSearchType.EXACT;
         case STARTS_WITH:
            return GlobalSearchType.STARTS_WITH;
         case ENDS_WITH:
            return GlobalSearchType.ENDS_WITH;
         case REG_EX:
            return GlobalSearchType.REG_EX;
         default:
            throw new IllegalStateException("Unknown search type " + name());
      }
   }
}
