package net.sourceforge.squirrel_sql.plugins.hibernate.mapping;

public class HibernatePropertyInfo
{
   private String _propertyName;
   private String _className;
   private String _tableName;
   private String[] _columnNames;
   private String _toString;
   private String _collectionClassName;
   private boolean _identifier;
   private String _classNameRegardingCollection;

   public HibernatePropertyInfo(String propertyName, String className, String tableName, String[] columnNames)
   {
      _propertyName = propertyName;
      _className = className;
      _tableName = tableName;
      _columnNames = columnNames;
      initStrings();
   }


   public void setCollectionClassName(String collectionClassName)
   {
      _collectionClassName = collectionClassName;
      initStrings();
   }

   private void initStrings()
   {
      _classNameRegardingCollection = null == _collectionClassName ? _className : _collectionClassName + "<" + _className + ">";

      _toString = _propertyName + " " + _classNameRegardingCollection;
   }


   public String getPropertyName()
   {
      return _propertyName;
   }

   public String getClassName()
   {
      return _className;
   }


   public String toString()
   {
      return _toString;
   }

   public String getCollectionClassName()
   {
      return _collectionClassName;
   }

   public void setIdentifier(boolean identifier)
   {
      _identifier = identifier;
   }

   public boolean isIdentifier()
   {
      return _identifier;
   }


   public String getTableName()
   {
      return _tableName;
   }


   public String[] getColumnNames()
   {
      return _columnNames;
   }


   public String getClassNameRegardingCollection()
   {
      return _classNameRegardingCollection;
   }
}
