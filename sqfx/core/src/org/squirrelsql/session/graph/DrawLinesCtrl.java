package org.squirrelsql.session.graph;

import javafx.beans.binding.DoubleBinding;
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
   private static final int PREVENT_INITIAL_SCROLL_DIST = 2;


   private Canvas _canvas = new Canvas();
   private Pane _desktopPane;

   public DrawLinesCtrl(Pane desktopPane, ScrollPane scrollPane)
   {
      DoubleBinding dbWidth = new DoubleBinding()
      {
         {
            super.bind(scrollPane.widthProperty());
         }


         @Override
         protected double computeValue()
         {
            return scrollPane.widthProperty().get() - PREVENT_INITIAL_SCROLL_DIST;
         }
      };

      DoubleBinding dbHeight = new DoubleBinding()
      {
         {
            super.bind(scrollPane.heightProperty());
         }


         @Override
         protected double computeValue()
         {
            return scrollPane.heightProperty().get() - PREVENT_INITIAL_SCROLL_DIST;
         }
      };


      _desktopPane = desktopPane;
      _canvas.widthProperty().bind(dbWidth);
      _canvas.heightProperty().bind(dbHeight);

      _canvas.widthProperty().addListener((observable, oldValue, newValue) -> onDraw());
      _canvas.heightProperty().addListener((observable, oldValue, newValue) -> onDraw());
   }

   private void onDraw()
   {
      GraphicsContext gc = _canvas.getGraphicsContext2D();

      gc.clearRect(0, 0, _canvas.getWidth(), _canvas.getHeight());

      gc.setStroke(Color.BLACK);
      gc.setLineWidth(1);
      //gc.strokeLine(0, 0, _canvas.getWidth(), _canvas.getHeight());

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
      }
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
