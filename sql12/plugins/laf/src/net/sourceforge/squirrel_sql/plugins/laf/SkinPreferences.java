package net.sourceforge.squirrel_sql.plugins.laf;

import net.sourceforge.squirrel_sql.fw.id.IHasIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IntegerIdentifier;

public final class SkinPreferences implements IHasIdentifier
{
   private String _themePackDir;

   private String _themePackName;

   private IntegerIdentifier _id = new IntegerIdentifier(1);

   public String getThemePackDirectory()
   {
      return _themePackDir;
   }

   public void setThemePackDirectory(String value)
   {
      _themePackDir = value;
   }

   public String getThemePackName()
   {
      return _themePackName;
   }

   public void setThemePackName(String value)
   {
      _themePackName = value;
   }

   /**
    * @return The unique identifier for this object.
    */
   public IIdentifier getIdentifier()
   {
      return _id;
   }
}
