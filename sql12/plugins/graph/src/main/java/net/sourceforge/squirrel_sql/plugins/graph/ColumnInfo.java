package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.ColumnInfoXmlBean;
import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.QueryDataXmlBean;


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

   private String _toString;

   private QueryData _queryData = new QueryData();
   private ColumnInfoModelEventDispatcher _columnInfoModelEventDispatcher;

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
      _queryData = new QueryData(xmlBean.getQueryDataXmlBean());
      _index = xmlBean.getIndex();
      if(xmlBean.isPrimaryKey())
      {
         markPrimaryKey();
      }

      if(null != xmlBean.getImportedFromTable())
      {
         setDBImportData(xmlBean.getImportedFromTable(), xmlBean.getImportedColumn(), xmlBean.getConstraintName());
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

      QueryDataXmlBean queryDataXmlBean = new QueryDataXmlBean();
      queryDataXmlBean.setOperatorIndex(_queryData.getOperator().getIndex());
      queryDataXmlBean.setAggregateFunctionIndex(_queryData.getAggregateFunction().getIndex());
      queryDataXmlBean.setFilterValue(_queryData.getFilterValue());
      queryDataXmlBean.setInSelectClause(_queryData.isInSelectClause());

      ret.setQueryDataXmlBean(queryDataXmlBean);


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

   public void setDBImportData(String importedFromTable, String importedColumn, String constraintName)
   {
      _importedFromTable = importedFromTable;
      _importedColumn = importedColumn;
      _constraintName = constraintName;

      String fkString = " (FK)";

      if(null != importedColumn && false == _toString.endsWith(fkString))
      {
         _toString += fkString;
      }
   }

   public String getDBConstraintName()
   {
      return _constraintName;
   }

   public int getIndex()
   {
      return _index;
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

   public String getDBImportedTableName()
   {
      return _importedFromTable;
   }


   public QueryData getQueryData()
   {
      return _queryData;
   }

   public String getColumnName()
   {
      return _columnName;
   }

   public void setQueryData(QueryData queryData)
   {
      _queryData = queryData;
   }

   public void setColumnInfoModelEventDispatcher(ColumnInfoModelEventDispatcher columnInfoModelEventDispatcher)
   {
      _columnInfoModelEventDispatcher = columnInfoModelEventDispatcher;
   }

   public ColumnInfoModelEventDispatcher getColumnInfoModelEventDispatcher()
   {
      return _columnInfoModelEventDispatcher;
   }

   @Override
   public boolean equals(Object obj)
   {
      if(false == obj instanceof ColumnInfo)
      {
         return false;
      }

      return _columnName.equalsIgnoreCase(((ColumnInfo)obj)._columnName);
   }

   @Override
   public int hashCode()
   {
      return _columnName.hashCode();
   }
}
