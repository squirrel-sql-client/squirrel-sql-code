package org.squirrelsql.table;

import javafx.scene.control.skin.VirtualFlow;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.TableView;

import java.util.ArrayList;
import java.util.List;

public class TableUtil
{
   public static List<SimpleObjectProperty> createSimpleObjectPropertyRow(Object[] row)
   {
      List<SimpleObjectProperty> buf = new ArrayList<>();

      for (Object o : row)
      {
         buf.add(new SimpleObjectProperty(o));
      }
      return buf;
   }

   public static List<SimpleObjectProperty> createSimpleObjectPropertyRow(List row)
   {
      return createSimpleObjectPropertyRow(row.toArray(new Object[row.size()]));
   }

   public static int getFirstVisibleRow(TableView tableView)
   {
      VirtualFlow virtualFlow = (VirtualFlow) tableView.lookup(".virtual-flow");

      IndexedCell firstVisibleCell = virtualFlow.getFirstVisibleCell();

      if(null == firstVisibleCell)
      {
         // Happens when table is empty.
         return -1;
      }

      int rowIndex = firstVisibleCell.getIndex();

      return rowIndex;

   }

   public static void scrollTo(TableView tableView, int firstVisibleRow)
   {
      if(firstVisibleRow < 0 || tableView.getItems().size() <= firstVisibleRow)
      {
         return;
      }



      Platform.runLater(() -> doScrollTo(tableView, firstVisibleRow));
   }

   private static void doScrollTo(TableView tableView, int firstVisibleRow)
   {
      VirtualFlow virtualFlow = (VirtualFlow) tableView.lookup(".virtual-flow");

      if(null == virtualFlow)
      {
         scrollTo(tableView, firstVisibleRow);
         return;
      }

      virtualFlow.scrollTo(firstVisibleRow);
   }

   //   public static void __prepareExtendedSelection(TableView tv)
//   {
//      tv.setRowFactory(new Callback<TableView, TableRow>()
//      {
//         @Override
//         public TableRow call(TableView param)
//         {
//            return onCreateRow(tv);
//         }
//      });
//   }
//
//   private static TableRow onCreateRow(TableView tv)
//   {
//      TableRow row = new TableRow();
//      row.setOnDragEntered(new EventHandler<DragEvent>()
//      {
//         @Override
//         public void handle(DragEvent t)
//         {
//            setSelection(tv, row);
//         }
//      });
//
//      row.setOnDragDetected(new EventHandler<MouseEvent>()
//      {
//         @Override
//         public void handle(MouseEvent t)
//         {
//
//            Dragboard db = row.getTableView().startDragAndDrop(TransferMode.COPY);
//            ClipboardContent content = new ClipboardContent();
//            content.put(DataFormat.PLAIN_TEXT, "XData");
//            db.setContent(content);
//            setSelection(tv, row);
////            tv.setCursor(Cursor.DEFAULT);
////             t.consume();
//         }
//      });
//
//
//      return row;
//   }
//
//   private static void setSelection(TableView tv, IndexedCell cell)
//   {
//      if (cell.isSelected())
//      {
//         System.out.println("False enter");
//         tv.getSelectionModel().clearSelection(cell.getIndex());
//      }
//      else
//      {
//         System.out.println("Select");
//         tv.getSelectionModel().select(cell.getIndex());
//      }
//   }
}
