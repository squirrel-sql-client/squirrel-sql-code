package net.sourceforge.squirrel_sql.plugins.hibernate.viewobjects;

import net.sourceforge.squirrel_sql.plugins.hibernate.mapping.MappedClassInfo;
import net.sourceforge.squirrel_sql.plugins.hibernate.server.ObjectSubstitute;
import net.sourceforge.squirrel_sql.plugins.hibernate.server.ObjectSubstituteRoot;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class TupelResult implements IResult
{
   private ArrayList<SingleResult> _singleResults = new ArrayList<SingleResult>();

   private String _toString = "";

   public TupelResult(ArrayList<MappedClassInfo> mappedClassInfos, ObjectSubstituteRoot array)
   {
      for (int i = 0; i < mappedClassInfos.size(); i++)
      {
         MappedClassInfo mappedClassInfo = mappedClassInfos.get(i);
         ObjectSubstitute obj = array.getArrayItemAt(i);
         _singleResults.add(new SingleResult(obj, mappedClassInfo));

         _toString += mappedClassInfo.getClassName();

         if(i < mappedClassInfos.size() - 1)
         {
            _toString += ";";
         }
      }

   }

   public ArrayList<SingleResult> getSingleResults()
   {
      return _singleResults;
   }

   @Override
   public String toString()
   {
      return _toString;
   }
}
