package net.sourceforge.squirrel_sql.plugins.hibernate.server;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class PlainValueRepresentation implements Serializable
{
   private Object standardJdkTypeValue;
   private TypedValueList typedValueList;

   private PlainValueRepresentation()
   {
   }

   public static PlainValueRepresentation ofStandardJdkType(Object standardJdkTypeValue)
   {
      PlainValueRepresentation ret = new PlainValueRepresentation();
      ret.standardJdkTypeValue = standardJdkTypeValue;
      return ret;
   }

   public static PlainValueRepresentation ofTypedValueList(TypedValueList typedValueList)
   {
      PlainValueRepresentation ret = new PlainValueRepresentation();
      ret.typedValueList = typedValueList;
      return ret;
   }

   public static boolean containsTypedValueLists(List<ObjectSubstituteRoot> objects)
   {
      AtomicBoolean found = new AtomicBoolean(false);

      TypedValueVisitor visitor = tv -> {
         found.set(true);
         return false;
      };

      visitTypedValues(objects, visitor);
      return found.get();
   }

   public static void distributeTypedValuesDisplaySwitch(List<ObjectSubstituteRoot> objects, TypedValuesDisplaySwitch typedValuesDisplaySwitch)
   {
      TypedValueVisitor visitor = tv -> {
         tv.setTypedValuesDisplaySwitch(typedValuesDisplaySwitch);
         return true;
      };

      visitTypedValues(objects, visitor);
   }

   public static void visitTypedValues(List<ObjectSubstituteRoot> objects, TypedValueVisitor visitor)
   {
      for( ObjectSubstituteRoot object : objects )
      {
         if(object.isArray())
         {
            for(int i=0; i < object.getArraySize(); ++i)
            {
               for( PlainValue pv : object.getArrayItemAt(i).getPlainValues() )
               {
                  if(null != pv.getValue().typedValueList)
                  {
                     if(false == pv.getValue().typedValueList.visitValues(visitor))
                     {
                        return;
                     }
                  }
               }
            }
         }
         else
         {
            for( PlainValue pv : object.getObjectSubstitute().getPlainValues() )
            {
               if(null != pv.getValue().typedValueList)
               {
                  if(false == pv.getValue().typedValueList.visitValues(visitor))
                  {
                     return;
                  }
               }
            }
         }
      }
   }


   @Override
   public String toString()
   {
      String ret = "<null>";

      if(null != standardJdkTypeValue)
      {
         return "" + standardJdkTypeValue;
      }
      else if( null != typedValueList )
      {
         return typedValueList.asString();
      }

      return ret;
   }
}
