package net.sourceforge.squirrel_sql.plugins.hibernate.server;

import java.io.Serializable;

public class TypedValuesDisplaySwitch implements Serializable
{
   private TypedValuesDisplayMode typedValuesDisplayMode = TypedValuesDisplayMode.DEFAULT_MODE;

   public TypedValuesDisplayMode getTypedValuesDisplayMode()
   {
      return typedValuesDisplayMode;
   }

   public void setTypedValuesDisplayMode(TypedValuesDisplayMode typedValuesDisplayMode)
   {
      this.typedValuesDisplayMode = typedValuesDisplayMode;
   }
}
