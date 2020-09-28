package net.sourceforge.squirrel_sql.plugins.laf.flatlaf;

import net.sourceforge.squirrel_sql.fw.id.IHasIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IntegerIdentifier;

public class FlatThemePreference implements IHasIdentifier
{
   private static IIdentifier id = new IntegerIdentifier(1);

   private String name;

   @Override
   public IIdentifier getIdentifier()
   {
      return id;
   }

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }
}
