package net.sourceforge.squirrel_sql.fw.datasetviewer;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class MoveColumnsToFrontHandler
{
   public static void moveColumnsToFront(JTable table, ArrayList<ExtTableColumn> columnsToMoveToFront)
   {
      int nextIndexToMoveTo = 0;

      for (ExtTableColumn extTableColumn : columnsToMoveToFront)
      {
         for (int i = 0; i < table.getColumnModel().getColumnCount(); i++)
         {
            if(extTableColumn == table.getColumnModel().getColumn(i))
            {
               table.getColumnModel().moveColumn(i, nextIndexToMoveTo++);
               break;
            }
         }
      }


      Rectangle cellRect = table.getCellRect(0, 0, true);

      Rectangle visibleRect = table.getVisibleRect();
      cellRect.y = visibleRect.y;
      cellRect.height = visibleRect.height;

      table.scrollRectToVisible(cellRect);

   }
}
