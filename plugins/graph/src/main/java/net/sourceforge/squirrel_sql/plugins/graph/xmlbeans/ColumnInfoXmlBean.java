package net.sourceforge.squirrel_sql.plugins.graph.xmlbeans;

public class ColumnInfoXmlBean
{
   private String columnName;
   private String columnType;
   private int columnSize;
   private boolean nullable;
   private int index;
   private boolean isPrimaryKey;
   private String importedFromTable;
   private String importedColumn;
   private boolean _nonDbConstraint;
   private String constraintName;
   private int decimalDigits;

   public String getColumnName()
   {
      return columnName;
   }

   public void setColumnName(String columnName)
   {
      this.columnName = columnName;
   }

   public String getColumnType()
   {
      return columnType;
   }

   public void setColumnType(String columnType)
   {
      this.columnType = columnType;
   }

   public int getColumnSize()
   {
      return columnSize;
   }

   public void setColumnSize(int columnSize)
   {
      this.columnSize = columnSize;
   }

   public boolean isNullable()
   {
      return nullable;
   }

   public void setNullable(boolean nullable)
   {
      this.nullable = nullable;
   }

   public int getIndex()
   {
      return index;
   }

   public void setIndex(int index)
   {
      this.index = index;
   }

   public String getImportedFromTable()
   {
      return importedFromTable;
   }

   public void setImportedFromTable(String importedFromTable)
   {
      this.importedFromTable = importedFromTable;
   }

   public String getImportedColumn()
   {
      return importedColumn;
   }

   public void setImportedColumn(String importedColumn)
   {
      this.importedColumn = importedColumn;
   }

   public String getConstraintName()
   {
      return constraintName;
   }

   public void setConstraintName(String constraintName)
   {
      this.constraintName = constraintName;
   }

   public boolean isPrimaryKey()
   {
      return isPrimaryKey;
   }

   public void setPrimaryKey(boolean primaryKey)
   {
      isPrimaryKey = primaryKey;
   }

   public int getDecimalDigits()
   {
      return decimalDigits;
   }

   public void setDecimalDigits(int decimalDigits)
   {
      this.decimalDigits = decimalDigits;
   }

   public boolean isNonDbConstraint()
   {
      return _nonDbConstraint;
   }

   public void setNonDbConstraint(boolean nonDbConstraint)
   {
      _nonDbConstraint = nonDbConstraint;
   }
}
