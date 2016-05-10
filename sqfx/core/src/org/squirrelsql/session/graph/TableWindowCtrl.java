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
import java.util.Collections;
import java.util.List;

public class TableWindowCtrl
{
   private Session _session;
   private final Window _window;

   public TableWindowCtrl(Session session, TableInfo tableInfo, double x, double y, DrawLinesListener drawLinesListener)
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

      _window.boundsInParentProperty().addListener((observable, oldValue, newValue) -> drawLinesListener.drawLines(TableWindowCtrl.this));

      _window.setOnClosedAction(e -> drawLinesListener.drawLines(TableWindowCtrl.this));
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

   public List<LineSpec> getLineSpecs(TableWindowCtrl pkCtrl)
   {
      if(pkCtrl == this)
      {
         return new ArrayList<>();
      }


      double d = 20;

      PkPoint pkPoint;
      double pkGatherPointX;
      double pkGatherPointY;
      double fkGatherPointX;
      double fkGatherPointY;
      FkPoint fkPoint;

      if(pkCtrl.getMidX() < getMidX())
      {
         pkPoint = new PkPoint(pkCtrl._window.getBoundsInParent().getMaxX(), pkCtrl._window.getBoundsInParent().getMinY(), Math.PI);

         pkGatherPointX = pkCtrl._window.getBoundsInParent().getMaxX() + d;
         pkGatherPointY = pkCtrl._window.getBoundsInParent().getMinY();

         fkGatherPointX = _window.getBoundsInParent().getMinX() - d;
         fkGatherPointY = _window.getBoundsInParent().getMaxY();

         fkPoint = new FkPoint(_window.getBoundsInParent().getMinX(), _window.getBoundsInParent().getMaxY());
      }
      else
      {
         pkPoint = new PkPoint(pkCtrl._window.getBoundsInParent().getMinX(), pkCtrl._window.getBoundsInParent().getMinY(), 0);

         pkGatherPointX = pkCtrl._window.getBoundsInParent().getMinX() - d;
         pkGatherPointY = pkCtrl._window.getBoundsInParent().getMinY();

         fkGatherPointX = _window.getBoundsInParent().getMaxX() + d;
         fkGatherPointY = _window.getBoundsInParent().getMaxY();
         fkPoint = new FkPoint(_window.getBoundsInParent().getMaxX(), _window.getBoundsInParent().getMaxY());
      }

      LineSpec ret = new LineSpec(Collections.singletonList(pkPoint), pkGatherPointX, pkGatherPointY, fkGatherPointX, fkGatherPointY, Collections.singletonList(fkPoint));


      //ret.add(_window.getTranslateX(), _window.getTranslateY());
      return Collections.singletonList(ret);
   }

   private double getMidX()
   {
      return _window.getBoundsInParent().getMinX() + (_window.getBoundsInParent().getMaxX() - _window.getBoundsInParent().getMinX()) / 2.0;
   }

   public List<Point2D> getFkPointsTo(TableWindowCtrl pkCtrl)
   {
      if(pkCtrl == this)
      {
         return new ArrayList<>();
      }

      Point2D ret;
      if(getMidX() < pkCtrl.getMidX())
      {
         ret = new Point2D(_window.getBoundsInParent().getMaxX(), _window.getBoundsInParent().getMaxY());
      }
      else
      {
         ret = new Point2D(_window.getBoundsInParent().getMinX(), _window.getBoundsInParent().getMaxY());
      }

      //ret.add(_window.getTranslateX(), _window.getTranslateY());
      return Collections.singletonList(ret);

   }
}
