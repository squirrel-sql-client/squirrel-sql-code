package net.sourceforge.squirrel_sql.plugins.graph.xmlbeans;


public class ConstraintDataXmlBean
{
   private String pkTableName;
   private String fkTableName;
   private String constraintName;
   private boolean nonDbConstraint;

   /**
    * @deprecated Should be handled as _fkColumns if these don't already exist.
    */
   private ColumnInfoXmlBean[] columnInfoXmlBeans;

   private boolean _showThisConstraintName;
   private ConstraintQueryDataXmlBean _constraintQueryDataXmlBean;
   private ColumnInfoXmlBean[] _pkColumns;
   private ColumnInfoXmlBean[] _fkColumns;

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

   /**
    * @deprecated Use getFkColumns() instead
    */
   public ColumnInfoXmlBean[] getColumnInfoXmlBeans()
   {
      return columnInfoXmlBeans;
   }

   /**
    * @deprecated Use setFkColumns() instead
    */
   public void setColumnInfoXmlBeans(ColumnInfoXmlBean[] columnInfoXmlBeans)
   {
      this.columnInfoXmlBeans = columnInfoXmlBeans;
   }

   public boolean isNonDbConstraint()
   {
      return nonDbConstraint;
   }

   public void setNonDbConstraint(boolean dbConstraint)
   {
      this.nonDbConstraint = dbConstraint;
   }

   public boolean isShowThisConstraintName()
   {
      return _showThisConstraintName;
   }

   public void setShowThisConstraintName(boolean showThisConstraintName)
   {
      _showThisConstraintName = showThisConstraintName;
   }

   public void setConstraintQueryDataXmlBean(ConstraintQueryDataXmlBean constraintQueryDataXmlBean)
   {
      _constraintQueryDataXmlBean = constraintQueryDataXmlBean;
   }

   public ConstraintQueryDataXmlBean getConstraintQueryDataXmlBean()
   {
      return _constraintQueryDataXmlBean;
   }

   public ColumnInfoXmlBean[] getPkColumns()
   {
      return _pkColumns;
   }

   public void setPkColumns(ColumnInfoXmlBean[] pkColumns)
   {
      _pkColumns = pkColumns;
   }

   public ColumnInfoXmlBean[] getFkColumns()
   {
      return _fkColumns;
   }

   public void setFkColumns(ColumnInfoXmlBean[] fkColumns)
   {
      _fkColumns = fkColumns;
   }
}
