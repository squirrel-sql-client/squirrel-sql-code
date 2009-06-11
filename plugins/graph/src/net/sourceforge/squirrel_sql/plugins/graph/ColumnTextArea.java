package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.client.session.ISession;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;


public class ColumnTextArea extends JTextArea
{
   private TableToolTipProvider _toolTipProvider;
   private DndHandler _dndHandler;

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
    * @param columnInfos
    */
   public void setGraphColumns(ColumnInfo[] columnInfos)
   {
      StringBuffer sb = new StringBuffer();
      for (int i = 0; i < columnInfos.length; i++)
      {
         columnInfos[i].setIndex(i);
         sb.append(columnInfos[i]).append('\n');
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
}
