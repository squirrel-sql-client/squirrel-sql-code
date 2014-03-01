package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import static net.sourceforge.squirrel_sql.plugins.graph.GraphPluginResources.IKeys.*;


public enum Sorting
{


   NONE(0, "<Not sorted>", SORT_NONE, SortingI18n.s_stringMgr.getString("Sorting.notSorted"), SortingI18n.s_stringMgr.getString("Sorting.notSortedTT")),
   ASC(1, "ASC", SORT_ASC, SortingI18n.s_stringMgr.getString("Sorting.sortedAsc"), SortingI18n.s_stringMgr.getString("Sorting.sortedAscTT")),
   DESC(2, "DESC", SORT_DESC, SortingI18n.s_stringMgr.getString("Sorting.sortedDesc"), SortingI18n.s_stringMgr.getString("Sorting.sortedDescTT"));

   public static final String CLIENT_PROP_NAME = Sorting.class.getName();


   private int _index;
   private String _sql;
   private String _image;
   private String _name;
   private String _toolTip;

   Sorting(int index, String sql, String image, String name, String toolTip)
   {
      _index = index;
      _sql = sql;
      _image = image;
      _name = name;
      _toolTip = toolTip;
   }

   public int getIndex()
   {
      return _index;
   }

   public static Sorting getByIndex(int id)
   {
      for (Sorting sorting : values())
      {
         if(sorting._index == id)
         {
            return sorting;
         }
      }

      throw new IllegalArgumentException("Unknown sorting ID");
   }

   public String getImage()
   {
      return _image;
   }

   public String getSql()
   {
      return _sql;
   }

   public String getName()
   {
      return _name;
   }

   public String getToolTip()
   {
      return _toolTip;
   }


   @Override
   public String toString()
   {
      return _name;
   }
}

class SortingI18n
{
   static final StringManager s_stringMgr =  StringManagerFactory.getStringManager(SortingI18n.class);

}

