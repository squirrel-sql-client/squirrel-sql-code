package net.sourceforge.squirrel_sql.plugins.graph.xmlbeans;


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
   private boolean _32Converted;


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

   public void set32Converted(boolean a32Converted)
   {
      _32Converted = a32Converted;
   }

   public boolean is32Converted()
   {
      return _32Converted;
   }
}
