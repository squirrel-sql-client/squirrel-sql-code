package net.sourceforge.squirrel_sql.fw.datasetviewer.columndisplaychoice;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public enum DisplayMode
{

   DEFAULT(I18n.s_stringMgr.getString("DisplayMode.default")),
   IMAGE(I18n.s_stringMgr.getString("DisplayMode.image"));

   interface I18n
   {
      StringManager s_stringMgr = StringManagerFactory.getStringManager(DisplayMode.class);
   }

   private final String _toString;


   DisplayMode(String toString)
   {
      _toString = toString;
   }

   @Override
   public String toString()
   {
      return _toString;
   }
}
