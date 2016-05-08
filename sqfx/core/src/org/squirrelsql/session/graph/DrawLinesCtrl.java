package org.squirrelsql.session.graph;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
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
   private Image _arrowLeftImage = _props.getImage("arrow_left.png");
   private Image _arrowRightImage = _props.getImage("arrow_right.png");

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
               double xPk = pkPoints.get(0).getX();
               double yPk = pkPoints.get(0).getY();

               double xFk = fkPoints.get(0).getX();
               double yFk = fkPoints.get(0).getY();

               double xl;
               double yl;

               double xr;
               double yr;

               if(xPk < xFk)
               {
                  xl = xPk;
                  yl = yPk;

                  xr = xFk;
                  yr = yFk;

                  gc.drawImage(_arrowLeftImage, xl, yl - _arrowLeftImage.getHeight() / 2d);

                  //System.out.println("PK left " + pkCtrl.getWindow().getTitle());
               }
               else
               {
                  xl = xFk;
                  yl = yFk;

                  xr = xPk;
                  yr = yPk;

                  gc.drawImage(_arrowRightImage, xr - _arrowRightImage.getWidth(), yr - _arrowRightImage.getHeight() / 2d);

                  //System.out.println("PK right " + pkCtrl.getWindow().getTitle());
               }

               int d = 20;

               gc.strokeLine(xl, yl, xl  + d, yl);
               gc.strokeLine(xl  + d, yl, xr - d, yr);
               gc.strokeLine(xr - d, yr, xr, yr);
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
