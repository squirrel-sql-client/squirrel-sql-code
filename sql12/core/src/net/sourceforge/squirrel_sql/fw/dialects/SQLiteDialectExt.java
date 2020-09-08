package net.sourceforge.squirrel_sql.fw.dialects;

import org.hibernate.HibernateException;

public class SQLiteDialectExt extends CommonHibernateDialect
{
   private GenericDialectHelper _dialect = new GenericDialectHelper();

   public String getDisplayName()
   {
      return "SQLite";
   }

   public boolean supportsProduct(String databaseProductName, String databaseProductVersion)
   {
      if (databaseProductName == null)
      {
         return false;
      }

      return databaseProductName.trim().startsWith("SQLite");
   }


   public String getTypeName(int code, int length, int precision, int scale) throws HibernateException
   {
      return _dialect.getTypeName(code, length, precision, scale);
   }

   @Override
   public DialectType getDialectType()
   {
      return DialectType.SQLLITE;
   }
}
