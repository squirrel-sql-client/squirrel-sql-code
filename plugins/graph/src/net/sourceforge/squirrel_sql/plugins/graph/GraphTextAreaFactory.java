package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.client.session.ISession;

import javax.swing.*;
import java.awt.*;
import java.awt.dnd.DnDConstants;
import java.awt.event.MouseListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.InputEvent;

public class GraphTextAreaFactory
{
   private ColumnTextArea _txtColumns;
   private ZoomableColumnTextArea _txtZoomColumns;

   public GraphTextAreaFactory(TableToolTipProvider toolTipProvider, Zoomer zoomer, DndCallback dndCallback, ISession session)
   {
      _txtColumns = new ColumnTextArea(toolTipProvider, dndCallback, session);
      _txtColumns.setEditable(false);
      _txtColumns.setBackground(new Color(255,255,204));

      _txtZoomColumns = new ZoomableColumnTextArea(toolTipProvider, zoomer, dndCallback, session);
      _txtZoomColumns.setBackground(new Color(255,255,204));

   }


   public JComponent getComponent(boolean zoomEnabled)
   {
      if(zoomEnabled)
      {
         return _txtZoomColumns;
      }
      else
      {
         return _txtColumns;
      }
   }

   /**
    * Really tries it's best to provide the component. That means:
    * 1. If a showing component exists it is returned.
    * 2. If a visible and and non null Graphics providing Component exists it is returned
    *
    * If non of the above is true the method returns null.
    * @return If not null the component is guranteed to provide Graphics
    */
   public JComponent getBestReadyComponent()
   {

      if( getComponent(true).isShowing() ||
          (getComponent(true).isVisible() && null != getComponent(true).getGraphics())  )
      {
         return getComponent(true);
      }
      else if( getComponent(false).isShowing() ||
               (getComponent(false).isVisible() && null != getComponent(false).getGraphics()) )
      {
         return getComponent(false);
      }
      else
      {
         return null;
      }
   }


   public Graphics getGraphics()
   {
      JComponent bestReadyComponent = getBestReadyComponent();
      if(null == bestReadyComponent)
      {
         return null;
      }
      return bestReadyComponent.getGraphics();
   }

   public Font getFont()
   {
      JComponent bestReadyComponent = getBestReadyComponent();
      if(null == bestReadyComponent)
      {
         return null;
      }
      return bestReadyComponent.getGraphics().getFont();
   }

   public void setColumns(ColumnInfo[] columnInfos)
   {
      _txtColumns.setGraphColumns(columnInfos);
      _txtZoomColumns.setGraphColumns(columnInfos);
   }

   public void addMouseListener(MouseListener ml)
   {
      _txtColumns.addMouseListener(ml);
      _txtZoomColumns.addMouseListener(ml);
   }
}
