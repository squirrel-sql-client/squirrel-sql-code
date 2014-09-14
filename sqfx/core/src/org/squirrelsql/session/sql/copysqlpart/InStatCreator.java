package org.squirrelsql.session.sql.copysqlpart;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import org.squirrelsql.session.ColumnInfo;
import org.squirrelsql.table.ColumnHandle;
import org.squirrelsql.table.tableselection.CellItemsWithColumn;
import org.squirrelsql.table.tableselection.ExtendedTableSelectionHandler;

import java.util.ArrayList;
import java.util.List;

public class InStatCreator
{
   public static void onCopyAsInStat(ExtendedTableSelectionHandler extendedTableSelectionHandler)
   {
      ArrayList<InStatColumnInfo> inStatColumnInfos = getInStatColumnInfos(extendedTableSelectionHandler);

      if(0 == inStatColumnInfos.size())
      {
         return;
      }

      StringBuffer allInStats = new StringBuffer();

      for (InStatColumnInfo inStatColumnInfo : inStatColumnInfos)
      {
         allInStats.append(inStatColumnInfo.getInstat()).append("\n");
      }

      final Clipboard clipboard = Clipboard.getSystemClipboard();
      final ClipboardContent content = new ClipboardContent();
      content.putString(allInStats.toString());
      clipboard.setContent(content);
   }


   public static ArrayList<InStatColumnInfo> getInStatColumnInfos(ExtendedTableSelectionHandler extendedTableSelectionHandler)
   {
      List<CellItemsWithColumn> selectedCellItemsByColumn = extendedTableSelectionHandler.getSelectedCellItemsWithColumn();

      ArrayList<InStatColumnInfo> ret = new ArrayList<>();

      for (CellItemsWithColumn cellItemsWithColumn : selectedCellItemsByColumn)
      {
         ColumnInfo col = cellItemsWithColumn.getColumnHandle().getResultColumnInfo();

         InStatColumnInfo infoBuf = new InStatColumnInfo();
         ret.add(infoBuf);

         infoBuf.setColumnInfo(col);

         StringBuffer buf = new StringBuffer();
         int lastLength = buf.length();
         buf.append("(");


         for (int i = 0; i < cellItemsWithColumn.getItems().size(); i++)
         {
            Object cellObj = cellItemsWithColumn.getItems().get(i);
            if (0 < i)
            {
               buf.append(",");
               if (100 < buf.length() - lastLength)
               {
                  lastLength = buf.length();
                  buf.append("\n");
               }
            }

            buf.append(CopySqlPartUtil.getData(col, cellObj, StatementType.IN));
         }
         buf.append(")");
         infoBuf.setInstat(buf);
      }

      return ret;
   }

}
