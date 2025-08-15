package net.sourceforge.squirrel_sql.fw.sql.databasemetadata;

import java.util.List;
import java.util.stream.Stream;

/**
 * Introduced to have a common point for default table types usages.
 * Introduced on account of SourceForge bug. #1534
 */
public enum DefaultTableTypesEnum
{
   TABLE("TABLE"),
   VIEW("VIEW"),
   SYSTEM_TABLE("SYSTEM TABLE");

   private final String _tableTypName;

   DefaultTableTypesEnum(String tableTypName)
   {
      _tableTypName = tableTypName;
   }

   public String getTableTypName()
   {
      return _tableTypName;
   }

   public static List<String> getAllDefaultTableTypeNames()
   {
      return Stream.of(values()).map(v -> v._tableTypName).toList();
   }

   public static List<String> getRealTableDefaultTableTypeNames()
   {
      return Stream.of(TABLE, SYSTEM_TABLE).map(v -> v._tableTypName).toList();
   }

   public static String getDefaultViewTypeName()
   {
      return VIEW._tableTypName;
   }

}
