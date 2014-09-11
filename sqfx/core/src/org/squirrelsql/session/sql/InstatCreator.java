package org.squirrelsql.session.sql;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import org.squirrelsql.table.ColumnHandle;
import org.squirrelsql.table.tableselection.CellItemsWithColumn;
import org.squirrelsql.table.tableselection.ExtendedTableSelectionHandler;

import java.util.List;

public class InstatCreator
{
   public static void onCopyAsInStat(ExtendedTableSelectionHandler extendedTableSelectionHandler)
   {
      List<CellItemsWithColumn> selectedCellItemsByColumn = extendedTableSelectionHandler.getSelectedCellItemsWithColumn();

      StringBuffer allInStats = new StringBuffer();
      for (CellItemsWithColumn cellItemsWithColumn : selectedCellItemsByColumn)
      {

         ColumnHandle columnHandle = cellItemsWithColumn.getColumnHandle();
         System.out.println("columnHandle = " + columnHandle); // !!! Hier weiter !!!!

         StringBuffer inStat = new StringBuffer();
         for (Object item : cellItemsWithColumn.getItems())
         {
            if (0 == inStat.length())
            {
               inStat.append("" + item);
            }
            else
            {
               inStat.append(",").append("" + item);
            }
         }
         inStat.insert(0,"(").append(")");

         if (0 == allInStats.length())
         {
            allInStats.append(inStat);
         }
         else
         {
            allInStats.append("\n").append(inStat);
         }
      }

      final Clipboard clipboard = Clipboard.getSystemClipboard();
      final ClipboardContent content = new ClipboardContent();
      content.putString(allInStats.toString());
      clipboard.setContent(content);
   }
}
