package net.sourceforge.squirrel_sql.plugins.hibernate.server;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class PlainValueRepresentation implements Serializable
{
   private Object standardJdkTypeValue;
   private ProjectionFieldValueList projectionFieldValueList;

   /**
    * Use the factory methods below.
    */
   private PlainValueRepresentation()
   {
   }

   public static PlainValueRepresentation ofStandardJdkType(Object standardJdkTypeValue)
   {
      PlainValueRepresentation ret = new PlainValueRepresentation();
      ret.standardJdkTypeValue = standardJdkTypeValue;
      return ret;
   }

   public static PlainValueRepresentation ofProjectionFieldValue(ProjectionFieldValueList projectionFieldValueList)
   {
      PlainValueRepresentation ret = new PlainValueRepresentation();
      ret.projectionFieldValueList = projectionFieldValueList;
      return ret;
   }

   public static boolean containsTypedValueLists(List<ObjectSubstituteRoot> objects)
   {
      AtomicBoolean found = new AtomicBoolean(false);

      ProjectionFieldValueVisitor visitor = tv -> {
         found.set(true);
         return false;
      };

      visitTypedValues(objects, visitor);
      return found.get();
   }

   public static void distributeProjectionDisplaySwitch(List<ObjectSubstituteRoot> objects, ProjectionDisplaySwitch projectionDisplaySwitch)
   {
      ProjectionFieldValueVisitor visitor = pfv -> {
         pfv.setProjectionDisplaySwitch(projectionDisplaySwitch);
         return true;
      };

      visitTypedValues(objects, visitor);
   }

   public static void visitTypedValues(List<ObjectSubstituteRoot> objects, ProjectionFieldValueVisitor visitor)
   {
      for( ObjectSubstituteRoot object : objects )
      {
         if(object.isArray())
         {
            for(int i=0; i < object.getArraySize(); ++i)
            {
               for( PlainValue pv : object.getArrayItemAt(i).getPlainValues() )
               {
                  if(null != pv.getValue().projectionFieldValueList )
                  {
                     if(false == pv.getValue().projectionFieldValueList.visitValues(visitor))
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
               if(null != pv.getValue().projectionFieldValueList )
               {
                  if(false == pv.getValue().projectionFieldValueList.visitValues(visitor))
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
      else if( null != projectionFieldValueList )
      {
         return projectionFieldValueList.toUiRepresentationString();
      }

      return ret;
   }
}
