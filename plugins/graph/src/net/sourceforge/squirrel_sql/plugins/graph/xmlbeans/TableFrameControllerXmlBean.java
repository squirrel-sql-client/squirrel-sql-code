package net.sourceforge.squirrel_sql.plugins.graph.xmlbeans;


public class TableFrameControllerXmlBean
{
   private String schema;
   private String catalog;
   private String tablename;
   private TableFrameXmlBean tableFrameXmlBean;
   private ColumnInfoXmlBean[] columnIfoXmlBeans;
   private String[] tablesExportedTo;
   private ConstraintViewXmlBean[] constraintViewXmlBeans;
   private int columOrder;

   public String getSchema()
   {
      return schema;
   }

   public void setSchema(String schema)
   {
      this.schema = schema;
   }

   public String getCatalog()
   {
      return catalog;
   }

   public void setCatalog(String catalog)
   {
      this.catalog = catalog;
   }

   public String getTablename()
   {
      return tablename;
   }

   public void setTablename(String tablename)
   {
      this.tablename = tablename;
   }

   public TableFrameXmlBean getTableFrameXmlBean()
   {
      return tableFrameXmlBean;
   }

   public void setTableFrameXmlBean(TableFrameXmlBean tableFrameXmlBean)
   {
      this.tableFrameXmlBean = tableFrameXmlBean;
   }

   public ColumnInfoXmlBean[] getColumnIfoXmlBeans()
   {
      return columnIfoXmlBeans;
   }

   public void setColumnIfoXmlBeans(ColumnInfoXmlBean[] columnIfoXmlBeans)
   {
      this.columnIfoXmlBeans = columnIfoXmlBeans;
   }

   public String[] getTablesExportedTo()
   {
      return tablesExportedTo;
   }

   public void setTablesExportedTo(String[] tablesExportedTo)
   {
      this.tablesExportedTo = tablesExportedTo;
   }

   public ConstraintViewXmlBean[] getConstraintViewXmlBeans()
   {
      return constraintViewXmlBeans;
   }

   public void setConstraintViewXmlBeans(ConstraintViewXmlBean[] constraintViewXmlBeans)
   {
      this.constraintViewXmlBeans = constraintViewXmlBeans;
   }

   public int getColumOrder()
   {
      return columOrder;
   }

   public void setColumOrder(int columOrder)
   {
      this.columOrder = columOrder;
   }
}
