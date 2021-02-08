package net.sourceforge.squirrel_sql.client.session.action.findcolums;

public class FindColumnsResultBean
{
   private String catalogName;
   private String schemaName;
   private String objectName;
   private String objectTypeName;
   private String columnName;
   private String columnTypeName;
   private Integer nullable;
   private Integer size;
   private Integer precision;
   private Integer decimalDigits;
   private Integer ordinalPosition;
   private String remarks;
   private Integer javaSqlType;

   public String getCatalogName()
   {
      return catalogName;
   }

   public void setCatalogName(String catalogName)
   {
      this.catalogName = catalogName;
   }

   public String getSchemaName()
   {
      return schemaName;
   }

   public void setSchemaName(String schemaName)
   {
      this.schemaName = schemaName;
   }

   public String getObjectName()
   {
      return objectName;
   }

   public void setObjectName(String objectName)
   {
      this.objectName = objectName;
   }

   public String getObjectTypeName()
   {
      return objectTypeName;
   }

   public void setObjectTypeName(String objectTypeName)
   {
      this.objectTypeName = objectTypeName;
   }

   public String getColumnName()
   {
      return columnName;
   }

   public void setColumnName(String columnName)
   {
      this.columnName = columnName;
   }

   public String getColumnTypeName()
   {
      return columnTypeName;
   }

   public void setColumnTypeName(String columnTypeName)
   {
      this.columnTypeName = columnTypeName;
   }

   public Integer getNullable()
   {
      return nullable;
   }

   public void setNullable(Integer nullable)
   {
      this.nullable = nullable;
   }

   public Integer getSize()
   {
      return size;
   }

   public void setSize(Integer size)
   {
      this.size = size;
   }

   public Integer getPrecision()
   {
      return precision;
   }

   public void setPrecision(Integer precision)
   {
      this.precision = precision;
   }

   public Integer getDecimalDigits()
   {
      return decimalDigits;
   }

   public void setDecimalDigits(Integer decimalDigits)
   {
      this.decimalDigits = decimalDigits;
   }

   public Integer getOrdinalPosition()
   {
      return ordinalPosition;
   }

   public void setOrdinalPosition(Integer ordinalPosition)
   {
      this.ordinalPosition = ordinalPosition;
   }

   public String getRemarks()
   {
      return remarks;
   }

   public void setRemarks(String remarks)
   {
      this.remarks = remarks;
   }

   public Integer getJavaSqlType()
   {
      return javaSqlType;
   }

   public void setJavaSqlType(Integer javaSqlType)
   {
      this.javaSqlType = javaSqlType;
   }
}
