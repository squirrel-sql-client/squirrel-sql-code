package net.sourceforge.squirrel_sql.fw.sql.tablenamefind;

public class QualifyResult
{
   private final String tableName;
   private final QualifyResultState empty;

   public QualifyResult(String tableName, QualifyResultState empty)
   {
      this.tableName = tableName;
      this.empty = empty;
   }

   public String getTableName()
   {
      return tableName;
   }

   public QualifyResultState getState()
   {
      return empty;
   }
}
