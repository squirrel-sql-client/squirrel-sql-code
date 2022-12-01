package net.sourceforge.squirrel_sql.plugins.hibernate.server;

import java.io.Serializable;

public class MappedClassInfoData  implements Serializable
{
   private String _mappedClassName;
   private String _tableName;
   private String _simpleMappedClassName;
   private HibernatePropertyInfo _indentifierHibernatePropertyInfo;
   private HibernatePropertyInfo[] _hibernatePropertyInfos;
   private boolean _plainValueArray;

   public MappedClassInfoData(String mappedClassName, String tableName, HibernatePropertyInfo indentifierHibernatePropertyInfo, HibernatePropertyInfo[] hibernatePropertyInfos)
   {
      _indentifierHibernatePropertyInfo = indentifierHibernatePropertyInfo;
      _hibernatePropertyInfos = hibernatePropertyInfos;

      _mappedClassName = mappedClassName;
      _tableName = tableName;
      _simpleMappedClassName = extracteSimpleClassName(mappedClassName);
   }

   public String getMappedClassName()
   {
      return _mappedClassName;
   }


   public String getSimpleMappedClassName()
   {
      return _simpleMappedClassName;
   }


   public String getTableName()
   {
      return _tableName;
   }

   public HibernatePropertyInfo getIndentifierHibernatePropertyInfo()
   {
      return _indentifierHibernatePropertyInfo;
   }

   public HibernatePropertyInfo[] getHibernatePropertyInfos()
   {
      return _hibernatePropertyInfos;
   }


   private String extracteSimpleClassName(String mappedClassName)
   {
      String[] cpTokens = mappedClassName.split("\\.");
      return cpTokens[cpTokens.length - 1];
   }

   public boolean isPlainValueArray()
   {
      return _plainValueArray;
   }

   public void setPlainValueArray(boolean plainValueArray)
   {
      _plainValueArray = plainValueArray;
   }
}