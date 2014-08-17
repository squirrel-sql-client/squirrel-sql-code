package org.squirrelsql.table;

import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.*;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Callback;
import org.squirrelsql.AppState;

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

   public static StackPane prepareExtendedSelection(TableView tv)
   {
      return new ExtendedTableSelection(tv).getStackPane();
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
