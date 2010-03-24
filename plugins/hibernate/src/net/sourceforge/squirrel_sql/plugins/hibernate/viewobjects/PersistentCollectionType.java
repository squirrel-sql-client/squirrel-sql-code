package net.sourceforge.squirrel_sql.plugins.hibernate.viewobjects;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import java.util.ArrayList;

public class PersistentCollectionType implements IType
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(PersistentCollectionType.class);


   private SingleType _singleType;
   private boolean _wasInitalized;
   private String _toString;

   public PersistentCollectionType(SingleType singleType, boolean wasInitalized)
   {
      _singleType = singleType;
      _wasInitalized = wasInitalized;

      if(_wasInitalized)
      {
         _toString = s_stringMgr.getString("PersistentCollectionType.initialized", _singleType.getMappedClassInfo().getClassName());
      }
      else
      {
         _toString = s_stringMgr.getString("PersistentCollectionType.uninitialized", _singleType.getMappedClassInfo().getClassName());
      }
   }

   @Override
   public ArrayList<? extends IType> getKidTypes()
   {
      return _singleType.getKidTypes();
   }

   @Override
   public ArrayList<? extends IResult> getResults()
   {
      return _singleType.getResults();
   }

   @Override
   public String toString()
   {
      return _toString;
   }

   public SingleType getSingleType()
   {
      return _singleType;
   }

   public boolean isInitalized()
   {
      return _wasInitalized;
   }
}
