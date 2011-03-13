package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.client.session.ISession;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;


public class ColumnTextArea extends JTextArea implements DndColumn, IColumnTextArea
{
   private TableToolTipProvider _toolTipProvider;
   private DndHandler _dndHandler;
   private ColumnInfoModel _columnInfoModel;

   public ColumnTextArea(TableToolTipProvider toolTipProvider, DndCallback dndCallback, ISession session)
   {
      _toolTipProvider = toolTipProvider;
      setToolTipText("Just to make getToolTiptext() to be called");
      _dndHandler = new DndHandler(dndCallback, this, session);
   }

   public String getToolTipText(MouseEvent event)
   {
      return _toolTipProvider.getToolTipText(event);
   }

   /**
    * Not named setColumns() because it would be an overload.
    * @param columnInfoModel
    */
   public void setColumnInfoModel(ColumnInfoModel columnInfoModel)
   {
      _columnInfoModel = columnInfoModel;

      _columnInfoModel.addColumnInfoModelListener(new ColumnInfoModelListener()
      {
         @Override
         public void columnInfosChanged(TableFramesModelChangeType changeType)
         {
            if (TableFramesModelChangeType.COLUMN_SORTING == changeType)
            {
               initColumnInfos();
            }
         }
      });

      initColumnInfos();
   }

   private void initColumnInfos()
   {
      StringBuffer sb = new StringBuffer();
      for (int i = 0; i < _columnInfoModel.getColCount(); i++)
      {
         //_columnInfoModel.getOrderedColAt(i).setIndex(i);
         sb.append(_columnInfoModel.getOrderedColAt(i)).append('\n');
      }
      setText(sb.toString());
   }

   public DndEvent getDndEvent()
   {
      return _dndHandler.getDndEvent();
   }

   public void setDndEvent(DndEvent dndEvent)
   {
      _dndHandler.setDndEvent(dndEvent);
   }

   @Override
   public Point getLocationInColumnTextArea()
   {
      return new Point(0,0);
   }

   @Override
   public int getColumnHeight()
   {
      FontMetrics fm = getGraphics().getFontMetrics(getFont());
      return fm.getHeight();
   }

   @Override
   public int getMaxWidth()
   {
      int maxSize = 0;
      FontMetrics fm = getFontMetrics(getFont());

      for (int i = 0; i < _columnInfoModel.getColCount(); i++)
      {
         int buf = fm.stringWidth(_columnInfoModel.getColAt(i).toString());
         if(maxSize < buf)
         {
            maxSize = buf;
         }
      }

      return maxSize;
   }
}
