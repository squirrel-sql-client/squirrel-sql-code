package net.sourceforge.squirrel_sql.plugins.refactoring.gui;

import net.sourceforge.squirrel_sql.fw.sql.ForeignKeyInfo;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;

public class SimpleNameReader
{
   public static String getSimpleName(IDatabaseObjectInfo info)
   {
      if(info instanceof ForeignKeyInfo)
      {
         return info.getSimpleName() + " (" + ((ForeignKeyInfo) info).getForeignKeyType() + ")";
      }

      return info.getSimpleName();
   }
}
