package net.sourceforge.squirrel_sql.plugins.graph.xmlbeans;


import net.sourceforge.squirrel_sql.plugins.graph.querybuilder.WhereTreeNodeStructure;

public class GraphControllerXmlBean
{
   private String title;
   private TableFrameControllerXmlBean[] tableFrameControllerXmls;
   private boolean showConstraintNames;
   private ZoomerXmlBean zoomerXmlBean;
   private PrintXmlBean printXmlBean;
   private boolean _showQualifiedTableNames;
   private int _modeIndex;
   private boolean _queryHideNoJoins;
   private boolean _converted32;
   private WhereTreeNodeStructure _whereTreeNodeStructure;
   private OrderStructureXmlBean _orderStructure;
   private SelectStructureXmlBean _selectStructure;


   public String getTitle()
   {
      return title;
   }

   public void setTitle(String title)
   {
      this.title = title;
   }

   public TableFrameControllerXmlBean[] getTableFrameControllerXmls()
   {
      return tableFrameControllerXmls;
   }

   public void setTableFrameControllerXmls(TableFrameControllerXmlBean[] tableFrameControllerXmls)
   {
      this.tableFrameControllerXmls = tableFrameControllerXmls;
   }

   public boolean isShowConstraintNames()
   {
      return showConstraintNames;
   }

   public void setShowConstraintNames(boolean showConstraintNames)
   {
      this.showConstraintNames = showConstraintNames;
   }

   public ZoomerXmlBean getZoomerXmlBean()
   {
      return zoomerXmlBean;
   }

   public void setZoomerXmlBean(ZoomerXmlBean zoomerXmlBean)
   {
      this.zoomerXmlBean = zoomerXmlBean;
   }

   public PrintXmlBean getPrintXmlBean()
   {
      return printXmlBean;
   }

   public void setPrintXmlBean(PrintXmlBean printXmlBean)
   {
      this.printXmlBean = printXmlBean;
   }

   public boolean isShowQualifiedTableNames()
   {
      return _showQualifiedTableNames;
   }

   public void setShowQualifiedTableNames(boolean showQualifiedTableNames)
   {
      _showQualifiedTableNames = showQualifiedTableNames;
   }

   public int getModeIndex()
   {
      return _modeIndex;
   }

   public void setModeIndex(int modeIndex)
   {
      _modeIndex = modeIndex;
   }

   public void setQueryHideNoJoins(boolean queryHideNoJoins)
   {
      _queryHideNoJoins = queryHideNoJoins;
   }

   public boolean isQueryHideNoJoins()
   {
      return _queryHideNoJoins;
   }

   public void setConverted32(boolean converted32)
   {
      _converted32 = converted32;
   }

   public boolean isConverted32()
   {
      return _converted32;
   }

   public WhereTreeNodeStructure getWhereTreeNodeStructure()
   {
      return _whereTreeNodeStructure;
   }

   public void setWhereTreeNodeStructure(WhereTreeNodeStructure whereTreeNodeStructure)
   {
      _whereTreeNodeStructure = whereTreeNodeStructure;
   }

   public void setOrderStructure(OrderStructureXmlBean orderStructure)
   {
      _orderStructure = orderStructure;
   }

   public OrderStructureXmlBean getOrderStructure()
   {
      return _orderStructure;
   }


   public SelectStructureXmlBean getSelectStructure()
   {
      return _selectStructure;
   }

   public void setSelectStructure(SelectStructureXmlBean selectStructure)
   {
      _selectStructure = selectStructure;
   }
}
