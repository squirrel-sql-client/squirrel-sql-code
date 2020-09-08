package net.sourceforge.squirrel_sql.fw.dialects;

import org.hibernate.dialect.Dialect;

import java.sql.Types;

public class GenericDialectHelper extends Dialect
{
   public GenericDialectHelper()
   {
      registerColumnType(Types.BIGINT, "integer");
      registerColumnType(Types.CHAR, "char($l)");
      registerColumnType(Types.DATE, "date");
      registerColumnType(Types.INTEGER, "integer");
      registerColumnType(Types.LONGVARCHAR, "varchar($l)");
      registerColumnType(Types.SMALLINT, "integer");
      registerColumnType(Types.TIME, "time");
      registerColumnType(Types.TIMESTAMP, "timestamp");
      registerColumnType(Types.TINYINT, "integer");
      registerColumnType(Types.VARCHAR, "varchar($l)");
   }
}
