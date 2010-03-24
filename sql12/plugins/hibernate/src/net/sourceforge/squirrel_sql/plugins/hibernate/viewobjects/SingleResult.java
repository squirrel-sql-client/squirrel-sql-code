package net.sourceforge.squirrel_sql.plugins.hibernate.viewobjects;

import net.sourceforge.squirrel_sql.plugins.hibernate.mapping.MappedClassInfo;
import net.sourceforge.squirrel_sql.plugins.hibernate.mapping.PropertyInfo;

public class SingleResult implements IResult
{
   private Object _object;
   private MappedClassInfo _mappedClassInfo;
   private String _toString;

   public SingleResult(Object object, MappedClassInfo mappedClassInfo)
   {
      _object = object;
      _mappedClassInfo = mappedClassInfo;


      _toString = _mappedClassInfo.getClassName();

      if (null == _object)
      {
         _toString += " <null>";
         return;
      }


      for (PropertyInfo propertyInfo : _mappedClassInfo.getAttributes())
      {
         if(propertyInfo.getHibernatePropertyInfo().isIdentifier())
         {
            String propertyName = propertyInfo.getHibernatePropertyInfo().getPropertyName();
            HibernatePropertyReader hpr = new HibernatePropertyReader(propertyName, _object);

            _toString += " [" + propertyName + "=" + hpr.getValue() + "; toString=\"" + _object + "\"]";
            break;
         }
      }
   }

   public Object getObject()
   {
      return _object;
   }

   public MappedClassInfo getMappedClassInfo()
   {
      return _mappedClassInfo;
   }

   @Override
   public String toString()
   {
      return _toString;
   }
}
