package net.sourceforge.squirrel_sql.fw.datasetviewer;

import java.beans.PropertyDescriptor;

/**
 * Created by IntelliJ IDEA.
 * User: gerd
 * Date: 01.12.11
 * Time: 21:04
 */
public class BeanPorpertyColumnDisplayDefinition
{
   private ColumnDisplayDefinition _colDef;
   private PropertyDescriptor _propDesc;

   public BeanPorpertyColumnDisplayDefinition(ColumnDisplayDefinition colDef, PropertyDescriptor propDesc)
   {
      _colDef = colDef;
      _propDesc = propDesc;
   }

   public ColumnDisplayDefinition getColDef()
   {
      return _colDef;
   }

   public PropertyDescriptor getPropDesc()
   {
      return _propDesc;
   }

   public static ColumnDisplayDefinition[] getColDefs(BeanPorpertyColumnDisplayDefinition[] beanPorpertyColumnDisplayDefinitions)
   {
      ColumnDisplayDefinition[] ret = new ColumnDisplayDefinition[beanPorpertyColumnDisplayDefinitions.length];

      for (int i = 0; i < beanPorpertyColumnDisplayDefinitions.length; i++)
      {
         ret[i] = beanPorpertyColumnDisplayDefinitions[i].getColDef();
      }

      return ret;
   }
}
