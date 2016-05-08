package org.squirrelsql.session.graph;

import javafx.beans.binding.DoubleBinding;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;

public class SizeBindingHelper
{
   private static final int PREVENT_INITIAL_SCROLL_DIST = 2;

   public static void bindLinesCanvasSizeToDesktopPaneSize(final Pane desktopPane, Canvas linesCanvas)
   {
      DoubleBinding dbWidth = new DoubleBinding()
      {
         {
            super.bind(desktopPane.widthProperty());
         }


         @Override
         protected double computeValue()
         {
            return desktopPane.widthProperty().get();
         }
      };

      DoubleBinding dbHeight = new DoubleBinding()
      {
         {
            super.bind(desktopPane.heightProperty());
         }


         @Override
         protected double computeValue()
         {
            return desktopPane.heightProperty().get();
         }
      };


      linesCanvas.widthProperty().bind(dbWidth);
      linesCanvas.heightProperty().bind(dbHeight);
   }

   public static void bindSizingDummyCanvasToScrollPane(ScrollPane scrollPane, Canvas sizingDummyCanvas)
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

      sizingDummyCanvas.widthProperty().bind(dbWidth);
      sizingDummyCanvas.heightProperty().bind(dbHeight);

   }
}
