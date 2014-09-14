package org.squirrelsql.session.sql;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import org.squirrelsql.table.tableselection.CellItemsWithColumn;
import org.squirrelsql.table.tableselection.ExtendedTableSelectionHandler;

import java.util.List;

public class CopyUtil
{
   public static void copyCells(ExtendedTableSelectionHandler extendedTableSelectionHandler, boolean withHeaders)
   {
      List<CellItemsWithColumn> selectedCellItemsByColumn = extendedTableSelectionHandler.getSelectedCellItemsWithColumn();

      if(0 == selectedCellItemsByColumn.size())
      {
         return;
      }



      StringBuffer buf = new StringBuffer();
      if (withHeaders)
      {
         for (int i = 0; i < selectedCellItemsByColumn.size(); i++)
         {
            CellItemsWithColumn cellItemsWithColumn = selectedCellItemsByColumn.get(i);

            buf.append(cellItemsWithColumn.getColumn().getText());
            if (i < selectedCellItemsByColumn.size() - 1)
            {
               buf.append('\t');
            }
         }

         buf.append('\n');
      }

      int rowCount = selectedCellItemsByColumn.get(0).getItems().size();


      for (int j = 0; j < rowCount; j++)
      {
         for (int i = 0; i < selectedCellItemsByColumn.size(); i++)
         {
            CellItemsWithColumn cellItemsWithColumn = selectedCellItemsByColumn.get(i);

            Object cellObj = cellItemsWithColumn.getItems().get(j);
            buf.append(cellObj);

            if (selectedCellItemsByColumn.size() > 1 && i < selectedCellItemsByColumn.size() - 1)
            {
               buf.append('\t');
            }
         }

         if (rowCount > 1)
         {
            buf.append('\n');
         }
      }


      final Clipboard clipboard = Clipboard.getSystemClipboard();
      final ClipboardContent content = new ClipboardContent();
      content.putString(buf.toString());
      clipboard.setContent(content);
   }
}
