package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import javax.swing.SwingConstants;

public class NumberTypeUtil
{
   public static Integer getHorizontalAlignmentOrNull()
   {
      if(DataTypeGeneral.isRightAlignNumericTypes())
      {
         return SwingConstants.RIGHT;
      }

      return null;
   }
}
