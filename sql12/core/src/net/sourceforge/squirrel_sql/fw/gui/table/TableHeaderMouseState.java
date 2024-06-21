package net.sourceforge.squirrel_sql.fw.gui.table;

public class TableHeaderMouseState
{
   /** If <TT>true</TT> then the mouse button is currently pressed. */
   private boolean _pressed;

   /**
    * If <TT>true</TT> then the mouse is being dragged. This is only relevant
    * while the mouse is pressed.
    */
   private boolean _dragged;

   /**
    * if <tt>_pressed</tt> is <tt>true</tt> then this is the physical column
    * that the mouse was pressed in.
    */
   private int _pressedViewColumnIdx;

   public TableHeaderMouseState()
   {
      _pressed = false;
      _dragged = false;
      _pressedViewColumnIdx = -1;
   }

   public boolean isPressed()
   {
      return _pressed;
   }

   public void setPressed(boolean pressed)
   {
      _pressed = pressed;
   }

   public boolean isDragged()
   {
      return _dragged;
   }

   public void setDragged(boolean dragged)
   {
      _dragged = dragged;
   }

   public int getPressedViewColumnIdx()
   {
      return _pressedViewColumnIdx;
   }

   public void setPressedViewColumnIdx(int pressedViewColumnIdx)
   {
      _pressedViewColumnIdx = pressedViewColumnIdx;
   }
}
