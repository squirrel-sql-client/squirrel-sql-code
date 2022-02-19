package net.sourceforge.squirrel_sql.fw.util.log;

import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

public enum SQLogLevel
{
   ERROR(4), WARN(2), INFO(1), DEBUG(0);

   private int _height;

   SQLogLevel(int height)
   {
      _height = height;
   }

   public static SQLogLevel getMatchingLevel(String logLevel)
   {
      if(StringUtilities.isEmpty(logLevel, true))
      {
         return INFO;
      }

      for (SQLogLevel level : values())
      {
         if(level.name().equalsIgnoreCase(logLevel.trim()))
         {
            return level;
         }
      }

      return INFO;
   }

   public boolean higherOrEqual(SQLogLevel level)
   {
      return level._height >= this._height;
   }
}
