package net.sourceforge.squirrel_sql.plugins.graph;

import javax.swing.*;
import java.awt.event.MouseEvent;


public class ColumnTextArea extends JTextArea
{
   private TableToolTipProvider _toolTipProvider;

   public ColumnTextArea(TableToolTipProvider toolTipProvider)
   {
      _toolTipProvider = toolTipProvider;
      setToolTipText("Just to make getToolTiptext() to be called");
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
}
