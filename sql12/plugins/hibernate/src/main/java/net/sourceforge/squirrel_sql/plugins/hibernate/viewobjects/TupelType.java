package net.sourceforge.squirrel_sql.plugins.hibernate.viewobjects;

import net.sourceforge.squirrel_sql.plugins.hibernate.mapping.MappedClassInfo;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class TupelType implements IType
{
   ArrayList<SingleType> _singleTypes = new ArrayList<SingleType>();

   ArrayList<TupelResult> _tupleResults = new ArrayList<TupelResult>();

   private String _toString = "";


   public TupelType(ArrayList<MappedClassInfo> myMappedClassInfos, ArrayList<MappedClassInfo> allMappedClassInfos, Class persistenCollectionClass, List arrays)
   {
      for (int i = 0; i < myMappedClassInfos.size(); i++)
      {
         _singleTypes.add(new SingleType(myMappedClassInfos.get(i), allMappedClassInfos, persistenCollectionClass, getAllArrayElementsWithIndex(i, arrays)));

         _toString += myMappedClassInfos.get(i).getClassName();

         if(myMappedClassInfos.size() - 1 > i)
         {
            _toString += ";";
         }
      }

      for (Object array : arrays)
      {
         _tupleResults.add(new TupelResult(myMappedClassInfos, array));
      }
   }

   private ArrayList getAllArrayElementsWithIndex(int index, List arrays)
   {
      ArrayList ret = new ArrayList();

      for (Object array : arrays)
      {
         ret.add(Array.get(array, index));
      }

      return ret;
   }


   @Override
   public ArrayList<? extends IType> getKidTypes()
   {
      return _singleTypes;
   }

   @Override
   public ArrayList<TupelResult> getResults()
   {
      return _tupleResults;
   }

   @Override
   public String toString()
   {
      return _toString;
   }
}
