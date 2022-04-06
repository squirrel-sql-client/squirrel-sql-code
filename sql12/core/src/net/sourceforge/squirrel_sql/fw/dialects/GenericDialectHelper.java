package net.sourceforge.squirrel_sql.fw.dialects;

import java.sql.Types;

import net.sourceforge.squirrel_sql.fw.dialects.fromhibernate3_2_4_sp1.dialect.Dialect;

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

      // Originally introduced for Maria-DB
      registerColumnType(Types.NUMERIC, "numeric($p,$s)");
      registerColumnType(Types.DECIMAL, "decimal($p,$s)");
   }
}
