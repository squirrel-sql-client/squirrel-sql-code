package net.sourceforge.squirrel_sql.plugins.graph.xmlbeans;

import net.sourceforge.squirrel_sql.plugins.graph.ColumnInfo;


public class ConstraintViewXmlBean
{
   private ConstraintGraphXmlBean constraintGraphXmlBean;
   private ConstraintDataXmlBean constraintDataXmlBean;

   public ConstraintGraphXmlBean getConstraintGraphXmlBean()
   {
      return constraintGraphXmlBean;
   }

   public void setConstraintGraphXmlBean(ConstraintGraphXmlBean constraintGraphXmlBean)
   {
      this.constraintGraphXmlBean = constraintGraphXmlBean;
   }

   public ConstraintDataXmlBean getConstraintDataXmlBean()
   {
      return constraintDataXmlBean;
   }

   public void setConstraintDataXmlBean(ConstraintDataXmlBean constraintDataXmlBean)
   {
      this.constraintDataXmlBean = constraintDataXmlBean;
   }
}
