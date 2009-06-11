package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.ColumnInfoXmlBean;


public class ColumnInfo extends Object
{
   private String _columnName;
   private String _columnType;
   private int _columnSize;
   private int _decimalDigits;
   private boolean _isPrimaryKey;
   private boolean _nullable;
   private int _index;

   private String _importedFromTable;
   private String _importedColumn;
   private String _constraintName;
   private boolean _nonDbConstraint;

   private String _toString;


   public ColumnInfo(String columnName, String columnType, int columnSize, int decimalDigits, boolean nullable)
   {
      _columnName = columnName;
      _columnType = columnType;
      _columnSize = columnSize;
      _decimalDigits = decimalDigits;
      _nullable = nullable;

      String decimalDigitsString = 0 == _decimalDigits? "": "," + _decimalDigits;

      _toString = _columnName + "  " + _columnType + "(" + _columnSize + decimalDigitsString + ") " + (_nullable? "NULL": "NOT NULL");
   }

   public ColumnInfo(ColumnInfoXmlBean xmlBean)
   {
      this(xmlBean.getColumnName(), xmlBean.getColumnType(), xmlBean.getColumnSize(), xmlBean.getDecimalDigits(), xmlBean.isNullable());
      _index = xmlBean.getIndex();
      if(xmlBean.isPrimaryKey())
      {
         markPrimaryKey();
      }

      if(null != xmlBean.getImportedFromTable())
      {
         setImportData(xmlBean.getImportedFromTable(), xmlBean.getImportedColumn(), xmlBean.getConstraintName(), xmlBean.isNonDbConstraint());
      }

   }

   public ColumnInfoXmlBean getXmlBean()
   {
      ColumnInfoXmlBean ret = new ColumnInfoXmlBean();
      ret.setColumnName(_columnName);
      ret.setColumnType(_columnType);
      ret.setColumnSize(_columnSize);
      ret.setDecimalDigits(_decimalDigits);
      ret.setNullable(_nullable);
      ret.setPrimaryKey(_isPrimaryKey);
      ret.setIndex(_index);
      ret.setImportedFromTable(_importedFromTable);
      ret.setImportedColumn(_importedColumn);
      ret.setConstraintName(_constraintName);
      ret.setNonDbConstraint(_nonDbConstraint);

      return ret;
   }


   public String toString()
   {
      return _toString;
   }

   public String getName()
   {
      return _columnName;
   }

   public void setImportData(String importedFromTable, String importedColumn, String constraintName, boolean nonDbConstraint)
   {
      _importedFromTable = importedFromTable;
      _importedColumn = importedColumn;
      _constraintName = constraintName;
      _nonDbConstraint = nonDbConstraint;

      String fkString = " (FK)";

      if(null != importedColumn && false == nonDbConstraint && false == _toString.endsWith(fkString))
      {
         _toString += fkString;
      }
   }

   public void clearImportData()
   {
      setImportData(null, null, null, false);
   }


   public boolean isImportedFrom(String tableName)
   {
      return tableName.equals(_importedFromTable);
   }

   public String getConstraintName()
   {
      return _constraintName;
   }

   public int getIndex()
   {
      return _index;
   }

   public String getImportedColumnName()
   {
      return _importedColumn;
   }

   public void markPrimaryKey()
   {
      _isPrimaryKey = true;
      _toString += " (PK)";
   }

   public boolean isPrimaryKey()
   {
      return _isPrimaryKey;
   }

   public void setIndex(int index)
   {
      _index = index;
   }

   public String getConstraintToolTipText()
   {
      if(null == _importedFromTable)
      {
         return null;
      }

      return _importedFromTable + "." + _importedColumn + " (" + _constraintName + ")";
   }

   public String getImportedTableName()
   {
      return _importedFromTable;
   }

   public boolean isNonDbConstraint()
   {
      return _nonDbConstraint;
   }
}
