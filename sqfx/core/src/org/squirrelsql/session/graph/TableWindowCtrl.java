package org.squirrelsql.session.graph;

import javafx.collections.FXCollections;
import javafx.geometry.Point2D;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import org.squirrelsql.services.CollectionUtil;
import org.squirrelsql.session.ColumnInfo;
import org.squirrelsql.session.Session;
import org.squirrelsql.session.TableInfo;
import org.squirrelsql.session.graph.graphdesktop.Window;

import java.util.ArrayList;
import java.util.List;

public class TableWindowCtrl extends Window
{
   private Session _session;
   private final Window _window;

   public TableWindowCtrl(Session session, TableInfo tableInfo, double x, double y)
   {
      _session = session;
      _window = new Window(tableInfo.getName());

      Pane contentPane = createContentPane(tableInfo);

      _window.setContentPane(contentPane);

      _window.setLayoutX(x);
      _window.setLayoutY(y);

      // define the initial window size
      _window.setPrefSize(300, 200);

      _window.setCtrl(this);
   }

   private Pane createContentPane(TableInfo tableInfo)
   {
      List<ColumnInfo> columns = _session.getSchemaCacheValue().get().getColumns(tableInfo);

      BorderPane contentPane = new BorderPane();
      contentPane.setCenter(new ListView<String>(FXCollections.observableArrayList(CollectionUtil.transform(columns, c -> c.getDescription()))));
      return contentPane;
   }


   public Window getWindow()
   {
      return _window;
   }

   public List<Point2D> getPkPointsTo(TableWindowCtrl fkCtrl)
   {
      return new ArrayList<>();
   }

   public List<Point2D> getFkPointsTo(TableWindowCtrl pkCtrl)
   {
      return new ArrayList<>();
   }
}
