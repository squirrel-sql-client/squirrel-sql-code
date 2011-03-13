package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public enum Mode
{
   DEFAULT (0, ModeEnumI18nSupport.s_stringMgr.getString("ModeMenuItem.mode.default")),
   ZOOM_PRINT(1, ModeEnumI18nSupport.s_stringMgr.getString("ModeMenuItem.mode.zoomPrint")),
   QUERY_BUILDER(2, ModeEnumI18nSupport.s_stringMgr.getString("ModeMenuItem.mode.queryBuilder"));


   private int _index;
   private String _toString;

   Mode(int index, String toString)
   {
      _index = index;
      _toString = toString;
   }


   @Override
   public String toString()
   {
      return _toString;
   }

   public static Mode getForIndex(int modeIndex)
   {
      for (Mode mode : values())
      {
         if(mode._index == modeIndex)
         {
            return mode;
         }
      }

      throw new IllegalStateException("Unknown mode index: " + modeIndex);
   }

   public int getIndex()
   {
      return _index;
   }

   boolean isQueryBuilder()
   {
      return QUERY_BUILDER == this;
   }
}

class ModeEnumI18nSupport
{
    static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(ModeEnumI18nSupport.class);
}
