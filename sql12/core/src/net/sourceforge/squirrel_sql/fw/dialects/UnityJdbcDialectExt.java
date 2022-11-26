package net.sourceforge.squirrel_sql.fw.dialects;

import net.sourceforge.squirrel_sql.fw.dialects.fromhibernate3_2_4_sp1.HibernateException;

public class UnityJdbcDialectExt extends CommonHibernateDialect
{
   private GenericDialectHelper _dialect = new GenericDialectHelper();

   public String getDisplayName()
   {
      return "UnityJDBC";
   }

   public boolean supportsProduct(String databaseProductName, String databaseProductVersion)
   {
      if (databaseProductName == null)
      {
         return false;
      }

      return databaseProductName.trim().startsWith("UnityJDBC");
   }


   public String getTypeName(int javaSqlTypesConst, int length, int precision, int scale, String typeNameOrNull) throws HibernateException
   {
      return _dialect.getTypeName(javaSqlTypesConst, length, precision, scale);
   }

   @Override
   public DialectType getDialectType()
   {
      return DialectType.UNITY_JDBC;
   }
}
