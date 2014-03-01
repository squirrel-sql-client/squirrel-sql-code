package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.plugins.graph.nondbconst.DndCallback;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;

public class GraphTextAreaFactory
{
   public static final Color TEXTAREA_BG = new Color(255,255,204);

   private ColumnTextArea _txtColumns;
   private ZoomableColumnTextArea _txtZoomColumns;
   private QueryTextArea _txtQueryTextArea;

   public GraphTextAreaFactory(String tableName, ISession session, GraphPlugin plugin, TableToolTipProvider toolTipProvider, ModeManager modeManager, DndCallback dndCallback)
   {
      _txtColumns = new ColumnTextArea(toolTipProvider, dndCallback, session);
      _txtColumns.setEditable(false);
      _txtColumns.setBackground(TEXTAREA_BG);

      _txtZoomColumns = new ZoomableColumnTextArea(toolTipProvider, modeManager.getZoomer(), dndCallback, session);
      _txtZoomColumns.setBackground(TEXTAREA_BG);

      _txtQueryTextArea = new QueryTextArea(tableName, plugin, dndCallback, session);
   }

   public int getColumnHeight()
   {
      JComponent bestReadyComponent = getBestReadyComponent();
      if(null == bestReadyComponent)
      {
         return 0;
      }

      return ((IColumnTextArea)bestReadyComponent).getColumnHeight();
   }


   public JComponent getComponent(Mode mode)
   {
      if(Mode.ZOOM_PRINT == mode)
      {
         return _txtZoomColumns;
      }
      else if(Mode.DEFAULT == mode)
      {
         return _txtColumns;
      }
      else if(Mode.QUERY_BUILDER == mode)
      {
         return _txtQueryTextArea;
      }
      else
      {
         throw new IllegalArgumentException("Unknown mode: " + mode);
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

      if( getComponent(Mode.DEFAULT).isShowing() ||
          (getComponent(Mode.DEFAULT).isVisible() && null != getComponent(Mode.DEFAULT).getGraphics())  )
      {
         return getComponent(Mode.DEFAULT);
      }
      else if( getComponent(Mode.ZOOM_PRINT).isShowing() ||
               (getComponent(Mode.ZOOM_PRINT).isVisible() && null != getComponent(Mode.ZOOM_PRINT).getGraphics()) )
      {
         return getComponent(Mode.ZOOM_PRINT);
      }
      else if( getComponent(Mode.QUERY_BUILDER).isShowing() ||
               (getComponent(Mode.QUERY_BUILDER).isVisible() && null != getComponent(Mode.QUERY_BUILDER).getGraphics()) )
      {
         return getComponent(Mode.QUERY_BUILDER);
      }
      else
      {
         return null;
      }
   }


   public void setColumnInfoModel(ColumnInfoModel columnInfoModel)
   {
      _txtColumns.setColumnInfoModel(columnInfoModel);
      _txtZoomColumns.setColumnInfoModel(columnInfoModel);
      _txtQueryTextArea.setColumnInfoModel(columnInfoModel);
   }

   public void addMouseListener(MouseListener ml)
   {
      _txtColumns.addMouseListener(ml);
      _txtZoomColumns.addMouseListener(ml);
      _txtQueryTextArea.addQueryAreaMouseListener(ml);
   }

   public int getMaxWidht()
   {
      JComponent bestReadyComponent = getBestReadyComponent();
      if(null == bestReadyComponent)
      {
         return 0;
      }

      return ((IColumnTextArea)bestReadyComponent).getMaxWidth();
   }
}
