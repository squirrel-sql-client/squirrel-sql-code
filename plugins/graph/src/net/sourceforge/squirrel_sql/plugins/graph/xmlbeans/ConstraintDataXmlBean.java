package net.sourceforge.squirrel_sql.plugins.graph.xmlbeans;

import net.sourceforge.squirrel_sql.plugins.graph.ColumnInfo;


public class ConstraintDataXmlBean
{
   private String pkTableName;
   private String fkTableName;
   private String constraintName;
   private ColumnInfoXmlBean[] columnInfoXmlBeans;

   public String getPkTableName()
   {
      return pkTableName;
   }

   public void setPkTableName(String pkTableName)
   {
      this.pkTableName = pkTableName;
   }

   public String getFkTableName()
   {
      return fkTableName;
   }

   public void setFkTableName(String fkTableName)
   {
      this.fkTableName = fkTableName;
   }

   public String getConstraintName()
   {
      return constraintName;
   }

   public void setConstraintName(String constraintName)
   {
      this.constraintName = constraintName;
   }

   public ColumnInfoXmlBean[] getColumnInfoXmlBeans()
   {
      return columnInfoXmlBeans;
   }

   public void setColumnInfoXmlBeans(ColumnInfoXmlBean[] columnInfoXmlBeans)
   {
      this.columnInfoXmlBeans = columnInfoXmlBeans;
   }
}
