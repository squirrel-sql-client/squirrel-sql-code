package net.sourceforge.squirrel_sql.plugins.graph.xmlbeans;


public class GraphControllerXmlBean
{
   private String title;
   private TableFrameControllerXmlBean[] tableFrameControllerXmls;
   private boolean showConstraintNames;

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
}
