package org.squirrelsql.session.graph;

public class ColumnPersistenceId
{
   public static String createId(ColumnPersistence columnPersistence)
   {
      return columnPersistence.getTableName() + "." + columnPersistence.getColName();
   }
}
