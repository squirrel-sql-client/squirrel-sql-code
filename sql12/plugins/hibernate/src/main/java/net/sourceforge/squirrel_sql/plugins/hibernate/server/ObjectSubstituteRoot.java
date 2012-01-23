package net.sourceforge.squirrel_sql.plugins.hibernate.server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ObjectSubstituteRoot implements Serializable
{
   private ArrayList<ObjectSubstitute> _objectSubstituteArray;
   private ObjectSubstitute _objectSubstitute;

   public ObjectSubstituteRoot(ArrayList<ObjectSubstitute> objectSubstituteArray)
   {
      _objectSubstituteArray = objectSubstituteArray;
   }

   public ObjectSubstituteRoot(ObjectSubstitute objectSubstitute)
   {
      _objectSubstitute = objectSubstitute;
   }

   public boolean isArray()
   {
      return null != _objectSubstituteArray;
   }

   public int getArraySize()
   {
      return _objectSubstituteArray.size();
   }

   public ObjectSubstitute getArrayItemAt(int i)
   {
      return _objectSubstituteArray.get(i);
   }

   public ObjectSubstitute getObjectSubstitute()
   {
      return _objectSubstitute;
   }

   public static List<ObjectSubstitute> toObjectSubstitutes(List<ObjectSubstituteRoot> nonArrayRoots)
   {
      ArrayList<ObjectSubstitute> ret = new ArrayList<ObjectSubstitute>();

      for (ObjectSubstituteRoot nonArrayRoot : nonArrayRoots)
      {
         if(nonArrayRoot.isArray())
         {
            throw new IllegalArgumentException("Should only be called for non array");
         }
         
         ret.add(nonArrayRoot._objectSubstitute);
      }

      return ret;
   }
}
