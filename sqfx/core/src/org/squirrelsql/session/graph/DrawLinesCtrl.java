package org.squirrelsql.session.graph;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import org.squirrelsql.Props;
import org.squirrelsql.session.graph.graphdesktop.Window;

import java.util.List;

public class DrawLinesCtrl
{
   private Canvas _canvas = new Canvas();
   private Pane _desktopPane;
   private ScrollPane _scrollPane;
   private Props _props = new Props(this.getClass());

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

            List<LineSpec> lineSpecs = fkCtrl.getLineSpecs(pkCtrl);

            for (LineSpec lineSpec : lineSpecs)
            {
               for (PkPoint pkPoint : lineSpec.getPkPoints())
               {
                  ArrowDrawer.drawArrow(gc, pkPoint);
                  gc.strokeLine(pkPoint.getX(), pkPoint.getY(), lineSpec.getPkGatherPointX(), lineSpec.getPkGatherPointY());
               }

               gc.strokeLine(lineSpec.getPkGatherPointX(), lineSpec.getPkGatherPointY(), lineSpec.getFkGatherPointX(), lineSpec.getFkGatherPointY());

               for (Point2D fkPoint : lineSpec.getFkPoints())
               {
                  gc.strokeLine(fkPoint.getX(), fkPoint.getY(), lineSpec.getFkGatherPointX(), lineSpec.getFkGatherPointY());
               }

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
