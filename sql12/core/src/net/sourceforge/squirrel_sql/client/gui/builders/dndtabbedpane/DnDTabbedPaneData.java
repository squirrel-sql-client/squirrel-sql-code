package net.sourceforge.squirrel_sql.client.gui.builders.dndtabbedpane;

import javax.swing.*;
import java.awt.*;

public class DnDTabbedPaneData
{
   private final Rectangle _targetLineRectBuffer = new Rectangle();
   private int _dragTabIndex = -1;

   private boolean _hasGhost = true;
   private boolean _paintScrollArea = true;

   private JTabbedPane _tabbedPane;


   private Rectangle _rBackward = new Rectangle();
   private Rectangle _rForward = new Rectangle();

   public DnDTabbedPaneData(JTabbedPane tabbedPane)
   {
      _tabbedPane = tabbedPane;
   }

   public Rectangle getrBackward()
   {
      return _rBackward;
   }

   public void setrBackward(Rectangle rBackward)
   {
      _rBackward = rBackward;
   }

   public Rectangle getrForward()
   {
      return _rForward;
   }

   public void setrForward(Rectangle rForward)
   {
      _rForward = rForward;
   }


   public Rectangle getTargetLineRectBuffer()
   {
      return _targetLineRectBuffer;
   }

   public int getDragTabIndex()
   {
      return _dragTabIndex;
   }

   public void setDragTabIndex(int dragTabIndex)
   {
      this._dragTabIndex = dragTabIndex;
   }


   public boolean isHasGhost()
   {
      return _hasGhost;
   }

   public void setHasGhost(boolean hasGhost)
   {
      _hasGhost = hasGhost;
   }

   public boolean isPaintScrollArea()
   {
      return _paintScrollArea;
   }

   public void setPaintScrollArea(boolean paintScrollArea)
   {
      _paintScrollArea = paintScrollArea;
   }

   public JTabbedPane getTabbedPane()
   {
      return _tabbedPane;
   }
}
