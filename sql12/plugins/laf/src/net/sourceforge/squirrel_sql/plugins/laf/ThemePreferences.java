package net.sourceforge.squirrel_sql.plugins.laf;

import net.sourceforge.squirrel_sql.fw.id.IHasIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IntegerIdentifier;

public abstract class ThemePreferences implements IHasIdentifier
{
   private String _themeName;
   private IntegerIdentifier _id = new IntegerIdentifier(1);

   public String getThemeName()
   {
      return _themeName;
   }

   public void setThemeName(String value)
   {
      _themeName = value;
   }

   /**
    * @return The unique identifier for this object.
    */
   public IIdentifier getIdentifier()
   {
      return _id;
   }
}
