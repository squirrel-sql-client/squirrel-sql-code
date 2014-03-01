package net.sourceforge.squirrel_sql.plugins.hibernate.viewobjects;

import net.sourceforge.squirrel_sql.plugins.hibernate.mapping.MappedClassInfo;
import net.sourceforge.squirrel_sql.plugins.hibernate.server.ObjectSubstitute;
import net.sourceforge.squirrel_sql.plugins.hibernate.server.ObjectSubstituteRoot;

import java.util.ArrayList;
import java.util.List;

public class TupelType implements IType
{
   ArrayList<SingleType> _singleTypes = new ArrayList<SingleType>();

   ArrayList<TupelResult> _tupleResults = new ArrayList<TupelResult>();

   private String _toString = "";


   public TupelType(ArrayList<MappedClassInfo> myMappedClassInfos, ArrayList<MappedClassInfo> allMappedClassInfos, List<ObjectSubstituteRoot> arrays)
   {
      for (int i = 0; i < myMappedClassInfos.size(); i++)
      {
         _singleTypes.add(new SingleType(myMappedClassInfos.get(i), allMappedClassInfos, getAllArrayElementsWithIndex(i, arrays)));

         _toString += myMappedClassInfos.get(i).getClassName();

         if(myMappedClassInfos.size() - 1 > i)
         {
            _toString += ";";
         }
      }

      for (ObjectSubstituteRoot array : arrays)
      {
         _tupleResults.add(new TupelResult(myMappedClassInfos, array));
      }
   }

   private ArrayList<ObjectSubstitute> getAllArrayElementsWithIndex(int index, List<ObjectSubstituteRoot> arrays)
   {
      ArrayList<ObjectSubstitute> ret = new ArrayList<ObjectSubstitute>();

      for (ObjectSubstituteRoot array : arrays)
      {
         ret.add(array.getArrayItemAt(index));
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
