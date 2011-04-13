package net.sourceforge.squirrel_sql.plugins.graph.xmlbeans;

import net.sourceforge.squirrel_sql.plugins.graph.Mode;

import java.util.ArrayList;

@SuppressWarnings("deprecation")  // This is a version converter so its OK to use deprecated methods here.
public class Version32Converter
{
   public static void convert(GraphControllerXmlBean ret)
   {
      if(ret.is32Converted())
      {
         return;
      }


      if(Mode.DEFAULT == Mode.getForIndex(ret.getModeIndex()) && ret.getZoomerXmlBean().isEnabled())
      {
         ret.setModeIndex(Mode.ZOOM_PRINT.getIndex());
      }

      for (TableFrameControllerXmlBean tfcXml : ret.getTableFrameControllerXmls())
      {

         ArrayList<ConstraintViewXmlBean> remainConstXmls = new ArrayList<ConstraintViewXmlBean>();
         for (ConstraintViewXmlBean constXml : tfcXml.getConstraintViewXmlBeans())
         {
            ConstraintDataXmlBean constDataXml = constXml.getConstraintDataXmlBean();

            ColumnInfoXmlBean[] pkCols = getPkColumnsFor(constDataXml, ret);

            if(null == pkCols)
            {
               continue;
            }


            constDataXml.setPkColumns(cleanColInfos(pkCols));
            constDataXml.setFkColumns(cleanColInfos(constDataXml.getColumnInfoXmlBeans()));
            constDataXml.setColumnInfoXmlBeans(null);
            constXml.setConstraintDataXmlBean(constDataXml);
            remainConstXmls.add(constXml);
         }

         tfcXml.setConstraintViewXmlBeans(remainConstXmls.toArray(new ConstraintViewXmlBean[remainConstXmls.size()]));
      }


      for (TableFrameControllerXmlBean tfcXml : ret.getTableFrameControllerXmls())
      {
         cleanColInfos(tfcXml.getColumnIfoXmlBeans());
      }
   }

   private static ColumnInfoXmlBean[] getPkColumnsFor(ConstraintDataXmlBean constDataXml, GraphControllerXmlBean graphXml)
   {

      ColumnInfoXmlBean[] colXmls = constDataXml.getColumnInfoXmlBeans();

      ColumnInfoXmlBean[] ret = new ColumnInfoXmlBean[colXmls.length];

      for (int i = 0; i < colXmls.length; i++)
      {
         ret[i] = findCol(colXmls[i].getImportedFromTable(), colXmls[i].getImportedColumn(), graphXml.getTableFrameControllerXmls());
         if(null == ret[i])
         {
            return null;
         }
      }

      return ret;
   }

   private static ColumnInfoXmlBean findCol(String importedFromTable, String importedColumn, TableFrameControllerXmlBean[] tableFrameControllerXmls)
   {
      for (TableFrameControllerXmlBean tfcXml : tableFrameControllerXmls)
      {
         if(tfcXml.getTablename().equalsIgnoreCase(importedFromTable))
         {
            for (ColumnInfoXmlBean colXml : tfcXml.getColumnIfoXmlBeans())
            {
               if(colXml.getColumnName().equalsIgnoreCase(importedColumn))
               {
                  return colXml;
               }
            }
         }
      }
      return null;
   }

   private static ColumnInfoXmlBean[] cleanColInfos(ColumnInfoXmlBean[] colXmls)
   {

      for (ColumnInfoXmlBean colXml : colXmls)
      {
         if(colXml.isNonDbConstraint())
         {
            colXml.setImportedColumn(null);
            colXml.setImportedFromTable(null);
            colXml.setConstraintName(null);
         }
      }

      return colXmls;

   }

   public static void markConverted(GraphControllerXmlBean xmlBean)
   {
      xmlBean.set32Converted(true);
   }
}
