package org.squirrelsql.aliases.dnd;

import javafx.scene.Node;
import javafx.scene.control.TreeCell;
import javafx.scene.input.DragEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import org.squirrelsql.aliases.AliasFolder;
import org.squirrelsql.aliases.MovePosition;

public class DndDragPositionMarker<T>
{
   public static final Color STROKE_COLOR = Color.BLACK;
   private TreeCell<T> _treeCell;
   private ModifyableChildrensAccessor _accessor;

   private Path _upperLinePath = new Path();
   private Path _rightLinePath = new Path();
   private Path _lowerLinePath = new Path();
   public static final int STROKE_WIDTH = 3;

   public DndDragPositionMarker(TreeCell<T> treeCell, ModifyableChildrensAccessor accessor)
   {
      _treeCell = treeCell;
      _accessor = accessor;
   }

   public void onDragExit(DragEvent dragEvent)
   {
      clearMarks();
   }

   private void clearMarks()
   {
      _accessor.getChildrenModifiable().remove(_upperLinePath);
      _accessor.getChildrenModifiable().remove(_lowerLinePath);
      _accessor.getChildrenModifiable().remove(_rightLinePath);
   }


   public void onDragOver(DragEvent dragEvent)
   {
      if(_treeCell.isEmpty())
      {
         clearMarks();
         return;
      }

      initSiblingPathLine(true, _upperLinePath);
      initSiblingPathLine(false, _lowerLinePath);
      initRightPathLine(_rightLinePath);

      if(allowsChildren(_treeCell.getItem()) && isOnTheRight(dragEvent))
      {
         _accessor.getChildrenModifiable().remove(_upperLinePath);
         _accessor.getChildrenModifiable().remove(_lowerLinePath);
         if (false == _accessor.getChildrenModifiable().contains(_rightLinePath))
         {
            _accessor.getChildrenModifiable().add(_rightLinePath);
         }
      }
      else if(isOnTheUpper(dragEvent))
      {
         _accessor.getChildrenModifiable().remove(_rightLinePath);
         _accessor.getChildrenModifiable().remove(_lowerLinePath);
         if (false == _accessor.getChildrenModifiable().contains(_upperLinePath))
         {
            _accessor.getChildrenModifiable().add(_upperLinePath);
         }
      }
      else if(isOnTheLower(dragEvent))
      {
         _accessor.getChildrenModifiable().remove(_rightLinePath);
         _accessor.getChildrenModifiable().remove(_upperLinePath);
         if (false == _accessor.getChildrenModifiable().contains(_lowerLinePath))
         {
            _accessor.getChildrenModifiable().add(_lowerLinePath);
         }
      }
   }

   private boolean allowsChildren(T item)
   {
      return item instanceof AliasFolder;
   }

   private boolean isOnTheRight(DragEvent dragEvent)
   {
      //if(getNodeXBegin() + (getNodeXEnd() - getNodeXBegin()) / 2 < dragEvent.getX())
      if(Math.max(getNodeXBegin() + (getNodeXEnd() - getNodeXBegin()) / 2 , getNodeXEnd() - 20) < dragEvent.getX())
      {
         return true;
      }
      else
      {
         return false;
      }
   }

   private boolean isOnTheLower(DragEvent dragEvent)
   {
      return dragEvent.getY() > _treeCell.getHeight() / 2;
   }

   private boolean isOnTheUpper(DragEvent dragEvent)
   {
      return dragEvent.getY() < _treeCell.getHeight() / 2;
   }

   private void initRightPathLine(Path path)
   {
      path.getElements().clear();

      double rightLineX = Math.min(getNodeXEnd(), _treeCell.getWidth());

      MoveTo moveTo = new MoveTo();
      moveTo.setX(rightLineX + STROKE_WIDTH);
      moveTo.setY(_treeCell.getHeight() / 2);

      LineTo lineTo = new LineTo();
      lineTo.setX(rightLineX + 10);
      lineTo.setY(_treeCell.getHeight() / 2);

      path.getElements().add(moveTo);
      path.getElements().add(lineTo);
      path.setStrokeWidth(STROKE_WIDTH);
      path.setStroke(STROKE_COLOR);
   }

   private void initSiblingPathLine(boolean upper, Path path)
   {
      path.getElements().clear();

      MoveTo moveTo = new MoveTo();
      moveTo.setX(getNodeXBegin());

      if (upper)
      {
         moveTo.setY(0 + STROKE_WIDTH);
      }
      else
      {
         moveTo.setY(_treeCell.getHeight() - STROKE_WIDTH);
      }

      LineTo lineTo = new LineTo();
      lineTo.setX(getNodeXEnd());
      if (upper)
      {
         lineTo.setY(0 + STROKE_WIDTH);
      }
      else
      {
         lineTo.setY(_treeCell.getHeight() - STROKE_WIDTH);
      }

      path.getElements().add(moveTo);
      path.getElements().add(lineTo);
      path.setStrokeWidth(STROKE_WIDTH);
      path.setStroke(STROKE_COLOR);
   }

   private double getNodeXEnd()
   {
      double ret = Double.MIN_VALUE;

      for (Node node : _accessor.getChildrenModifiable())
      {
         if(false == node instanceof Path)
         {
            ret = Math.max(ret, node.getLayoutX() + node.getLayoutBounds().getWidth());
         }
      }

      return ret;
   }

   private double getNodeXBegin()
   {
      double ret = Double.MAX_VALUE;

      for (Node node : _accessor.getChildrenModifiable())
      {
         if(false == node instanceof Path)
         {
            ret = Math.min(ret, node.getLayoutX());
         }
      }

      return ret;


   }


   public MovePosition getMovePosition()
   {
      if(_accessor.getChildrenModifiable().contains(_upperLinePath))
      {
         return MovePosition.UPPER_SIBLING;
      }
      else if(_accessor.getChildrenModifiable().contains(_lowerLinePath))
      {
         return MovePosition.LOWER_SIBLING;
      }
      else
      {
         return MovePosition.CHILD;
      }
   }
}
