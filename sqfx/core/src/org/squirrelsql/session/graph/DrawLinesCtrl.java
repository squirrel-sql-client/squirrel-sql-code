package org.squirrelsql.session.graph;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import org.squirrelsql.Props;
import org.squirrelsql.session.graph.graphdesktop.Window;

import java.util.ArrayList;
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

            List<LineSpec> lineSpecs = fkCtrl.getLineSpecs(pkCtrl);

            for (LineSpec lineSpec : lineSpecs)
            {

               try
               {
                  if(lineSpec.isSelected())
                  {
                     gc.setLineWidth(3d);
                  }

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
               finally
               {
                  gc.setLineWidth(1d);
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

   public void mouseClicked(MouseEvent e)
   {
      LineSpec clickedLineSpec = null;

      ArrayList<LineSpec> allLineSpecs = new ArrayList<>();

      for (Node pkNode : _desktopPane.getChildren())
      {
         TableWindowCtrl pkCtrl = ((Window) pkNode).getCtrl();

         for (Node fkNode : _desktopPane.getChildren())
         {
            TableWindowCtrl fkCtrl = ((Window) fkNode).getCtrl();

            List<LineSpec> lineSpecs = fkCtrl.getLineSpecs(pkCtrl);

            for (LineSpec lineSpec : lineSpecs)
            {
               allLineSpecs.add(lineSpec);

               Polygon polygon = createPolygon(lineSpec.getFkGatherPointX(), lineSpec.getFkGatherPointY(), lineSpec.getPkGatherPointX(), lineSpec.getPkGatherPointY(), 3);

               if (polygon.contains(e.getX(), e.getY()))
               {
                  clickedLineSpec = lineSpec;
               }
            }
         }
      }

      if(null != clickedLineSpec)
      {
         for (LineSpec lineSpec : allLineSpecs)
         {
            if (lineSpec != clickedLineSpec)
            {
               lineSpec.setSelected(false);
            }
         }
         clickedLineSpec.setSelected(!clickedLineSpec.isSelected());

         doDraw();
      }
   }

   private Polygon createPolygon(double x1, double y1, double x2, double y2, double halfThickness)
   {
      Polygon ret = new Polygon();

      if (x1 < x2 && y1 < y2)
      {
         ret.getPoints().addAll(x1 + halfThickness, y1 - halfThickness);
         ret.getPoints().addAll(x1 - halfThickness, y1 + halfThickness);
         ret.getPoints().addAll(x2 - halfThickness, y2 + halfThickness);
         ret.getPoints().addAll(x2 + halfThickness, y2 - halfThickness);
      }
      else if (x1 > x2 && y1 > y2)
      {
         ret.getPoints().addAll(x1 - halfThickness, y1 + halfThickness);
         ret.getPoints().addAll(x1 + halfThickness, y1 - halfThickness);
         ret.getPoints().addAll(x2 + halfThickness, y2 - halfThickness);
         ret.getPoints().addAll(x2 - halfThickness, y2 + halfThickness);
      }
      else
      {
         ret.getPoints().addAll(x1 + halfThickness, y1 + halfThickness);
         ret.getPoints().addAll(x1 - halfThickness, y1 - halfThickness);
         ret.getPoints().addAll(x2 - halfThickness, y2 - halfThickness);
         ret.getPoints().addAll(x2 + halfThickness, y2 + halfThickness);
      }

      //System.out.println("("+ x1 + ", " + y1 + ") - (" + x2 + ", " + y2 +")");

      return ret;
   }


}
