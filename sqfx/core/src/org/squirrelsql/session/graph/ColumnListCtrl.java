package org.squirrelsql.session.graph;

import javafx.collections.FXCollections;
import javafx.geometry.Point2D;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.*;
import org.squirrelsql.services.CollectionUtil;
import org.squirrelsql.session.ColumnInfo;
import org.squirrelsql.session.Session;
import org.squirrelsql.session.TableInfo;
import org.squirrelsql.session.graph.graphdesktop.Window;
import org.squirrelsql.session.objecttree.TableDetailsReader;
import org.squirrelsql.workaround.ListViewScrollEventWA;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class ColumnListCtrl
{

   public static final String DRAGGING_COLUMS = "DRAGGING_COLUMS";
   private final ListView<GraphColumn> _listView;
   private final ColumnPositionHelper _columnPositionHelper;
   private GraphChannel _graphChannel;
   private TableInfo _tableInfo;
   private Window _window;

   public ColumnListCtrl(Session session, GraphChannel graphChannel, TableInfo tableInfo, Window window, Runnable scrollListener)
   {
      _graphChannel = graphChannel;
      _tableInfo = tableInfo;
      _window = window;

      List<ColumnInfo> columns = session.getSchemaCacheValue().get().getColumns(_tableInfo);

      PrimaryKeyInfo pkInfo = new PrimaryKeyInfo(TableDetailsReader.readPrimaryKey(session, _tableInfo));
      ImportedKeysInfo impKeysInfo = new ImportedKeysInfo(TableDetailsReader.readImportedKeys(session, _tableInfo));
      //ExportedKeysInfo expKeysInfo = new ExportedKeysInfo(TableDetailsReader.readExportedKeys(_session, tableInfo));

      _listView = new ListView<>(FXCollections.observableArrayList(CollectionUtil.transform(columns, c -> new GraphColumn(c, pkInfo, impKeysInfo))));

      _columnPositionHelper = new ColumnPositionHelper(_listView, _window);

      ListViewScrollEventWA listViewScrollEventWA = new ListViewScrollEventWA(scrollListener);
      _listView.setCellFactory(p -> listViewScrollEventWA.registerCell(_columnPositionHelper.registerCell(new ColumnListCell())));

      _listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

      _listView.setOnDragDetected(e -> onDragDetected(e));
      _listView.setOnDragOver(e -> onDragOver(e));
      _listView.setOnDragDropped(e -> onDragDropped(e));
   }

   private void onDragDropped(DragEvent e)
   {
      if( DRAGGING_COLUMS.equals(e.getDragboard().getString()) )
      {
         ColumnListCtrl source = _graphChannel.getLastDraggingColumnListCtrl();

         System.out.println("ColumnListCtrl.onDragDropped TableInfo: " + source._tableInfo.getQualifiedName());

         for (GraphColumn graphColumn : source._listView.getSelectionModel().getSelectedItems())
         {
            System.out.println("  ColumnListCtrl.onDragDropped Col: " + graphColumn.getDescription());

         }

         if(1 == source._listView.getSelectionModel().getSelectedItems().size())
         {
            GraphColumn sourceCol = source._listView.getSelectionModel().getSelectedItems().get(0);
            GraphColumn dropCol = getDropColumn(e);

            dropCol.addNonDbImportedKey(new NonDbImportedKey(sourceCol, source._tableInfo));
         }
      }
      e.consume();

   }

   private GraphColumn getDropColumn(DragEvent e)
   {
      // TODO Implement
      return _listView.getItems().get(0);
   }

   private void onDragOver(DragEvent e)
   {
      if (DRAGGING_COLUMS.equals(e.getDragboard().getString()))
      {
         e.acceptTransferModes(TransferMode.MOVE);
      }
      e.consume();

   }

   private void onDragDetected(MouseEvent e)
   {
      if (0 < _listView.getSelectionModel().getSelectedItems().size())
      {
         Dragboard dragBoard = _listView.startDragAndDrop(TransferMode.MOVE);
         ClipboardContent content = new ClipboardContent();
         content.put(DataFormat.PLAIN_TEXT, DRAGGING_COLUMS);
         dragBoard.setContent(content);
         _graphChannel.setLastDraggingColumnListCtrl(this);
      }

      e.consume();

   }

   public ListView<GraphColumn> getColumnListView()
   {
      return _listView;
   }

   public PkSpec getPkSpec(TableWindowSide windowSide)
   {

      ArrayList<Point2D> pkPoints = new ArrayList<>();

      for (GraphColumn graphColumn : _listView.getItems())
      {
         if(graphColumn.belongsToPk())
         {
            double pkPointX;
            if(TableWindowSide.LEFT == windowSide)
            {
               pkPointX = _window.getBoundsInParent().getMinX();
            }
            else
            {
               pkPointX = _window.getBoundsInParent().getMaxX();
            }

            double pkPointY = _columnPositionHelper.getMiddleYOfColumn(graphColumn);
            pkPoints.add(new Point2D(pkPointX, pkPointY));

         }
      }

      if (0 < pkPoints.size())
      {
         return new PkSpec(pkPoints, windowSide);
      }
      else
      {
         return null;
      }
   }


   public List<FkSpec> getFkSpecsTo(TableInfo toPkTable, TableWindowSide windowSide)
   {
      List<FkSpec> ret = new ArrayList<>();

      List<String> fkNames = getFkNamesBelongingToPk(toPkTable);

      for (String fkName : fkNames)
      {
         ArrayList<Point2D> fkPoints = new ArrayList<>();

         for (GraphColumn graphColumn : _listView.getItems())
         {
            if(graphColumn.belongsToFk(fkName))
            {
               double fkPointX;
               if(TableWindowSide.LEFT == windowSide)
               {
                  fkPointX = _window.getBoundsInParent().getMaxX();
               }
               else
               {
                  fkPointX = _window.getBoundsInParent().getMinX();
               }

               double pkPointY = _columnPositionHelper.getMiddleYOfColumn(graphColumn);
               fkPoints.add(new Point2D(fkPointX, pkPointY));
            }
         }

         if(0 < fkPoints.size())
         {
            ret.add(new FkSpec(fkName, fkPoints, windowSide));
         }
      }

      return ret;
   }

   private List<String> getFkNamesBelongingToPk(TableInfo toPkTable)
   {
      HashSet<String> ret = new HashSet<>();

      for (GraphColumn graphColumn : _listView.getItems())
      {
         String fkName = graphColumn.getFkNameTo(toPkTable);

         if(null != fkName)
         {
            ret.add(fkName);
         }
      }

      return Arrays.asList(ret.toArray(new String[0]));
   }
}
