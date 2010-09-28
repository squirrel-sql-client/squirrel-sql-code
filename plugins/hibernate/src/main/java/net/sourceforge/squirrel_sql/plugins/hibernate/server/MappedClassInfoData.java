package net.sourceforge.squirrel_sql.plugins.hibernate.server;

import java.io.Serializable;

public class MappedClassInfoData  implements Serializable
{
   private String _mappedClassName;
   private String _tableName;
   private String _simpleMappedClassName;
   private HibernatePropertyInfo _indentifierHibernatePropertyInfo;
   private HibernatePropertyInfo[] _hibernatePropertyInfos;

   public MappedClassInfoData(String mappedClassName, String tableName, HibernatePropertyInfo indentifierHibernatePropertyInfo, HibernatePropertyInfo[] hibernatePropertyInfos)
   {
      _indentifierHibernatePropertyInfo = indentifierHibernatePropertyInfo;
      _hibernatePropertyInfos = hibernatePropertyInfos;

      setMappedClassName(mappedClassName);
      setTableName(tableName);
      setSimpleMappedClassName(extracteSimpleClassName(mappedClassName));
   }


   public String getMappedClassName()
   {
      return _mappedClassName;
   }

   public void setMappedClassName(String mappedClassName)
   {
      _mappedClassName = mappedClassName;
   }


   public String getSimpleMappedClassName()
   {
      return _simpleMappedClassName;
   }

   public void setSimpleMappedClassName(String simpleMappedClassName)
   {
      _simpleMappedClassName = simpleMappedClassName;
   }


   public void setTableName(String tableName)
   {
      _tableName = tableName;
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

}