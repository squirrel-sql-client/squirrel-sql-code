package net.sourceforge.squirrel_sql.plugins.hibernate.mapping;

public class DetailAttribute
{
   private PropertyInfo _attribute;
   private String _columnNamesString = "";

   public DetailAttribute(PropertyInfo attribute)
   {
      _attribute = attribute;

      HibernatePropertyInfo hibernatePropertyInfo = attribute.getHibernatePropertyInfo();

      if(0 < hibernatePropertyInfo.getColumnNames().length)
      {
         _columnNamesString = hibernatePropertyInfo.getColumnNames()[0];
         for (int i = 1; i < hibernatePropertyInfo.getColumnNames().length; i++)
         {
            _columnNamesString += "," +  hibernatePropertyInfo.getColumnNames()[i];
         }
      }
   }

   public static DetailAttribute[] createDetailtAttributes(PropertyInfo[] attributes)
   {
      DetailAttribute[] ret = new DetailAttribute[attributes.length];


      for (int i = 0; i < attributes.length; i++)
      {
         ret[i] = new DetailAttribute(attributes[i]);
      }

      return ret;
   }

   public String getAttributeName()
   {
      return _attribute.getHibernatePropertyInfo().getPropertyName();
   }

   public String getClassName()
   {
      return _attribute.getClassName();
   }


   public String getCollectionClassName()
   {
      String ret = _attribute.getHibernatePropertyInfo().getCollectionClassName();

      return null == ret ? "": ret;
   }


   public boolean isIdentifier()
   {
      return _attribute.getHibernatePropertyInfo().isIdentifier();
   }

   public String getTableName()
   {
      return _attribute.getHibernatePropertyInfo().getTableName();
   }


   public String getColumnNamesString()
   {
      return _columnNamesString;
   }

   public String getClassNameRegardingCollection()
   {
      return _attribute.getHibernatePropertyInfo().getClassNameRegardingCollection();
   }
}
