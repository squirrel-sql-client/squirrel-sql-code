package net.sourceforge.squirrel_sql.fw.dialects;

import net.sourceforge.squirrel_sql.fw.dialects.fromhibernate3_2_4_sp1.HibernateException;

public class MariaDBDialectExt extends CommonHibernateDialect // Could make sense to derive this from MySQLDialectEx
{
   private GenericDialectHelper _dialect = new GenericDialectHelper();

   public String getDisplayName()
   {
      return "MariaDB";
   }

   public boolean supportsProduct(String databaseProductName, String databaseProductVersion)
   {
      if (databaseProductName == null)
      {
         return false;
      }

      return databaseProductName.trim().startsWith("MariaDB");
   }


   public String getTypeName(int code, int length, int precision, int scale) throws HibernateException
   {
      return _dialect.getTypeName(code, length, precision, scale);
   }

   @Override
   public DialectType getDialectType()
   {
      return DialectType.MARIADB;
   }
}
