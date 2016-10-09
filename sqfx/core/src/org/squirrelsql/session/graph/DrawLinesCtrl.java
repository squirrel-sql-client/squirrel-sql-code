package org.squirrelsql.session.graph;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Polygon;
import org.squirrelsql.services.I18n;
import org.squirrelsql.services.RightMouseMenuHandler;
import org.squirrelsql.session.graph.graphdesktop.Window;

import java.util.ArrayList;
import java.util.List;

public class DrawLinesCtrl
{
   public static final int FOLDING_POINT_DIAMETER = 10;

   private Canvas _canvas = new Canvas();
   private Pane _desktopPane;
   private ScrollPane _scrollPane;
   private GraphChannel _graphChannel;

   private LineInteractionInfo _currentLineInteractionInfo = new LineInteractionInfo();


   public DrawLinesCtrl(Pane desktopPane, ScrollPane scrollPane, GraphChannel graphChannel)
   {
      _desktopPane = desktopPane;
      _scrollPane = scrollPane;
      _graphChannel = graphChannel;

      SizeBindingHelper.bindLinesCanvasSizeToDesktopPaneSize(desktopPane, _canvas);

      _canvas.widthProperty().addListener((observable, oldValue, newValue) -> onDraw());
      _canvas.heightProperty().addListener((observable, oldValue, newValue) -> onDraw());
   }

   private void onDraw()
   {
      GraphicsContext gc = _canvas.getGraphicsContext2D();

      gc.clearRect(0, 0, _canvas.getWidth(), _canvas.getHeight());

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

                  if (lineSpec.isNonDb())
                  {
                     gc.setStroke(Color.BLUE);
                     gc.setFill(Color.BLUE);
                  }
                  else
                  {
                     gc.setStroke(Color.BLACK);
                     gc.setFill(Color.BLACK);
                  }

                  for (PkPoint pkPoint : lineSpec.getPkPoints())
                  {
                     ArrowDrawer.drawArrow(gc, pkPoint, (Color)gc.getFill());
                     gc.strokeLine(pkPoint.getX(), pkPoint.getY(), lineSpec.getPkGatherPointX(), lineSpec.getPkGatherPointY());
                  }

                  Point2D begin = new Point2D(lineSpec.getPkGatherPointX(), lineSpec.getPkGatherPointY());

                  for (Point2D fp : lineSpec.getFoldingPoints())
                  {
                     gc.strokeLine(begin.getX(), begin.getY(), fp.getX(), fp.getY());

                     gc.fillOval(fp.getX() - FOLDING_POINT_DIAMETER /2d, fp.getY() - FOLDING_POINT_DIAMETER /2.d, FOLDING_POINT_DIAMETER, FOLDING_POINT_DIAMETER);

                     begin = fp;
                  }

                  gc.strokeLine(begin.getX(), begin.getY(), lineSpec.getFkGatherPointX(), lineSpec.getFkGatherPointY());

                  for (FkPoint fkPoint : lineSpec.getFkPoints())
                  {
                     gc.strokeLine(fkPoint.getPoint().getX(), fkPoint.getPoint().getY(), lineSpec.getFkGatherPointX(), lineSpec.getFkGatherPointY());
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

      for (LineSpec lineSpec : _currentLineInteractionInfo.getAllLineSpecsCache())
      {
         if (lineSpec != _currentLineInteractionInfo.getClickedOnLineSpec())
         {
            lineSpec.setSelected(false);
         }
      }


      if(_currentLineInteractionInfo.isClickOnFoldingPoint())
      {
         if(RightMouseMenuHandler.isPopupTrigger(e))
         {
            RightMouseMenuHandler rightMouseMenuHandler = new RightMouseMenuHandler(_canvas, false);
            rightMouseMenuHandler.addMenu(new I18n(getClass()).t("folding.point.remove"), this::onRemoveFoldingPoint);
            rightMouseMenuHandler.show(e);

            _currentLineInteractionInfo.getClickedOnLineSpec().setSelected(true);
         }

         doDraw();
      }
      else if(_currentLineInteractionInfo.isClickOnLineSpec())
      {
         if(RightMouseMenuHandler.isPopupTrigger(e))
         {
            RightMouseMenuHandler rightMouseMenuHandler = new RightMouseMenuHandler(_canvas, false);
            rightMouseMenuHandler.addMenu(new I18n(getClass()).t("folding.point.add"), () -> onAddFoldingPoint(e));
            if (_currentLineInteractionInfo.getClickedOnLineSpec().isNonDb())
            {
               rightMouseMenuHandler.addMenu(new I18n(getClass()).t("configure.nondb.constraint"), () -> onConfigureNonDBConstraint());
               rightMouseMenuHandler.addMenu(new I18n(getClass()).t("remove.nondb.constraint"), () -> onRemoveNonDBConstraint());
            }
            rightMouseMenuHandler.show(e);

            _currentLineInteractionInfo.getClickedOnLineSpec().setSelected(true);

         }
         else
         {
            _currentLineInteractionInfo.getClickedOnLineSpec().setSelected(!_currentLineInteractionInfo.getClickedOnLineSpec().isSelected());
         }

         doDraw();

      }
   }

   private void onRemoveNonDBConstraint()
   {
      String nonDbFkId = _currentLineInteractionInfo.getClickedOnLineSpec().getFkSpec().getFkNameOrId();

      List<FkPoint> fkPoints = _currentLineInteractionInfo.getClickedOnLineSpec().getFkSpec().getFkPoints();

      for (FkPoint fkPoint : fkPoints)
      {
         GraphColumn fkCol = fkPoint.getGraphColumn();
         GraphColumn pkCol = fkCol.getNonDbImportedKeyForId(nonDbFkId).getColumnThisImportedKeyPointsTo();

         pkCol.removeNonDbFkId(nonDbFkId);
         fkCol.removeNonDbFkId(nonDbFkId);

         _graphChannel.getGraphFinder().getTableWindowCtrl(pkCol.getQualifiedTableName()).removeNonDBFkId(nonDbFkId);
         _graphChannel.getGraphFinder().getTableWindowCtrl(fkCol.getQualifiedTableName()).removeNonDBFkId(nonDbFkId);
      }

      doDraw();
   }

   private void onConfigureNonDBConstraint()
   {
      new ConfigureNonDBConstraintCtrl(_currentLineInteractionInfo, new GraphFinder(_desktopPane));
      doDraw();
   }

   private void onRemoveFoldingPoint()
   {
      _currentLineInteractionInfo.removeFoldingPoint();
      doDraw();
   }

   private ArrayList<LineSpec> getAllLineSpecs()
   {
      ArrayList<LineSpec> ret = new ArrayList<>();
      for (Node pkNode : _desktopPane.getChildren())
      {
         TableWindowCtrl pkCtrl = ((Window) pkNode).getCtrl();

         for (Node fkNode : _desktopPane.getChildren())
         {
            TableWindowCtrl fkCtrl = ((Window) fkNode).getCtrl();

            ret.addAll(fkCtrl.getLineSpecs(pkCtrl));
         }
      }

      return ret;
   }

   private void onAddFoldingPoint(MouseEvent e)
   {
      _currentLineInteractionInfo.getClickedOnLineSpec().addFoldingPoint(new Point2D(e.getX(), e.getY()));
      doDraw();
   }

   public void mousePressed(MouseEvent e)
   {
      _currentLineInteractionInfo.clear();

      ArrayList<LineSpec> allLineSpecs = getAllLineSpecs();

      _currentLineInteractionInfo.setAllLineSpecsCache(allLineSpecs);

      for (LineSpec lineSpec : allLineSpecs)
      {
         for (Point2D fp : lineSpec.getFoldingPoints())
         {
            Ellipse ellipse = new Ellipse(fp.getX(), fp.getY(), FOLDING_POINT_DIAMETER / 2d, FOLDING_POINT_DIAMETER / 2d);
            if (ellipse.contains(e.getX(), e.getY()))
            {
               _currentLineInteractionInfo.setClickedOnFoldingPoint(fp, lineSpec);
               break;
            }
         }

         Point2D begin = new Point2D(lineSpec.getPkGatherPointX(), lineSpec.getPkGatherPointY());

         Polygon polygon;

         for (Point2D fp : lineSpec.getFoldingPoints())
         {
            polygon = GraphUtils.createPolygon(begin.getX(), begin.getY(), fp.getX(), fp.getY());
            if (polygon.contains(e.getX(), e.getY()))
            {
               _currentLineInteractionInfo.setClickedOnLineSpec(lineSpec);
               break;
            }

            begin = fp;
         }

         polygon = GraphUtils.createPolygon(begin.getX(), begin.getY(), lineSpec.getFkGatherPointX(), lineSpec.getFkGatherPointY());
         if (polygon.contains(e.getX(), e.getY()))
         {
            _currentLineInteractionInfo.setClickedOnLineSpec(lineSpec);
            break;
         }
      }
   }

   public void mouseDragged(MouseEvent e)
   {
      if (false == _currentLineInteractionInfo.isClickOnFoldingPoint() || RightMouseMenuHandler.isPopupTrigger(e))
      {
         return;
      }

      _currentLineInteractionInfo.moveFoldingPointTo(e.getX(), e.getY());
      doDraw();
   }

   public void mouseReleased(MouseEvent e)
   {
      if (false == _currentLineInteractionInfo.isClickOnFoldingPoint() || RightMouseMenuHandler.isPopupTrigger(e))
      {
         return;
      }

      _currentLineInteractionInfo.moveFoldingPointTo(e.getX(), e.getY());
      _currentLineInteractionInfo.clear();
      doDraw();
   }

}
