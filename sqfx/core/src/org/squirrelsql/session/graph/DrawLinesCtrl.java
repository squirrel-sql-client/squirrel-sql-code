package org.squirrelsql.session.graph;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import org.squirrelsql.session.graph.graphdesktop.Window;

import java.util.List;

public class DrawLinesCtrl
{
   private Canvas _canvas = new Canvas();
   private Pane _desktopPane;
   private ScrollPane _scrollPane;

   public DrawLinesCtrl(Pane desktopPane, ScrollPane scrollPane)
   {
      _desktopPane = desktopPane;
      _scrollPane = scrollPane;

      SizeBindingHelper.bindLinesCanvasSizeToDesktopPaneSize(desktopPane, _canvas);

      _canvas.widthProperty().addListener((observable, oldValue, newValue) -> onDraw());
      _canvas.heightProperty().addListener((observable, oldValue, newValue) -> onDraw());
   }

   private void onDraw()
   {
      GraphicsContext gc = _canvas.getGraphicsContext2D();

      gc.clearRect(0, 0, _canvas.getWidth(), _canvas.getHeight());

      gc.setStroke(Color.BLACK);
      gc.setLineWidth(1);

      double maxX = 0;
      double maxY = 0;

      for (Node pkNode : _desktopPane.getChildren())
      {
         TableWindowCtrl pkCtrl = ((Window) pkNode).getCtrl();

         for (Node fkNode : _desktopPane.getChildren())
         {
            TableWindowCtrl fkCtrl = ((Window) fkNode).getCtrl();

            List<Point2D> pkPoints = pkCtrl.getPkPointsTo(fkCtrl);
            List<Point2D> fkPoints = fkCtrl.getFkPointsTo(pkCtrl);

            if (0 < pkPoints.size() && 0 < fkPoints.size())
            {
               double x1 = pkPoints.get(0).getX();
               double y1 = pkPoints.get(0).getY();

               double x2 = fkPoints.get(0).getX();
               double y2 = fkPoints.get(0).getY();

               //System.out.println("(" + x1 +"," + y1 + ") - (" + x2 + "," + y2 +")");
               gc.strokeLine(x1, y1, x2, y2);
            }
         }

         maxX = Math.max(pkCtrl.getWindow().getBoundsInParent().getMaxX(), maxX);
         maxY = Math.max(pkCtrl.getWindow().getBoundsInParent().getMaxY(), maxY);
      }

      preventUnnecessaryScrolling(maxX, maxY);
   }

   private void preventUnnecessaryScrolling(double maxX, double maxY)
   {
      _desktopPane.setMaxWidth(Math.max(_scrollPane.getWidth(), maxX));
      _desktopPane.setMaxHeight(Math.max(_scrollPane.getHeight(), maxY));
   }


   public Canvas getCanvas()
   {
      return _canvas;
   }

   public void doDraw()
   {
      onDraw();
   }
}
