package net.sourceforge.squirrel_sql.fw.datasetviewer;

import java.awt.event.MouseEvent;

public class TableClickPosition
{
   private final boolean _clickedOnTableHeader;
   private final MouseEvent _tableClickEvent;

   public TableClickPosition(boolean clickedOnTableHeader, MouseEvent tableClickEvent)
   {
      _clickedOnTableHeader = clickedOnTableHeader;
      _tableClickEvent = tableClickEvent;
   }

   public boolean isClickedOnTableHeader()
   {
      return _clickedOnTableHeader;
   }

   public MouseEvent getTableClickEvent()
   {
      return _tableClickEvent;
   }

   public int getX()
   {
      return _tableClickEvent.getX();
   }
}
