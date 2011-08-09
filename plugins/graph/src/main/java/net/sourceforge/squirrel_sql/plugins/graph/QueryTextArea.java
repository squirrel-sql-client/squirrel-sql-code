package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.plugins.graph.nondbconst.DndCallback;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class QueryTextArea extends JPanel implements IColumnTextArea
{
   private String _tableName;
   private GraphPlugin _plugin;
   private ColumnInfoModel _columnInfoModel;
   private QueryTextAreaMouseListenerProxy _mouseListenerPx = new QueryTextAreaMouseListenerProxy();
   private ArrayList<MouseListener> _mouseListeners = new ArrayList<MouseListener>();

   private DndCallback _dndCallback;
   private ISession _session;
   private int _lastColumnHeightBeforeSetColums;


   public QueryTextArea(String tableName, GraphPlugin plugin, DndCallback dndCallback, ISession session)
   {
      _tableName = tableName;
      _plugin = plugin;
      _dndCallback = dndCallback;
      _session = session;

      setLayout(new GridBagLayout());

      addMouseListener(_mouseListenerPx);

   }

   public void setColumnInfoModel(ColumnInfoModel columnInfoModel)
   {
      _columnInfoModel = columnInfoModel;

      _columnInfoModel.addColumnInfoModelListener(new ColumnInfoModelListener()
      {
         @Override
         public void columnInfosChanged(TableFramesModelChangeType changeType)
         {
            if (TableFramesModelChangeType.COLUMN_SORTING == changeType || TableFramesModelChangeType.COLUMN_SELECT_ALL == changeType || TableFramesModelChangeType.COLUMN_WHERE_ALL == changeType)
            {
               initColumnInfos();
            }
         }
      });
      initColumnInfos();
   }

   private void initColumnInfos()
   {
      if (0 < getComponentCount())
      {
         _lastColumnHeightBeforeSetColums = getColumnHeight();
      }

      ///////////////////////////////////////////////////////
      // Nicely prevents repaint problems when selecting
      // the right mouse menu "Check all columns for SQL-SELECT clause",
      // see also setVisble(true) below
      setVisible(false);
      //
      /////////////////////////////////////////////////////

      removeAll();

      GridBagConstraints gbc;

      setBackground(GraphTextAreaFactory.TEXTAREA_BG);

      for (int i = 0; i < _columnInfoModel.getColCount(); i++)
      {
         ColumnInfo columnInfo = _columnInfoModel.getOrderedColAt(i);

         gbc = new GridBagConstraints(0, i, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);
         add(new QueryColumnPanel(_plugin, _tableName, columnInfo, _dndCallback, _session), gbc);
      }

      gbc = new GridBagConstraints(0, _columnInfoModel.getColCount(), 1, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);


      JPanel dist = new JPanel();
      dist.setBackground(GraphTextAreaFactory.TEXTAREA_BG);
      add(dist, gbc);

      setVisible(true);

      addToAllKidComponents(_mouseListenerPx);

   }

   @Override
   public int getColumnHeight()
   {
      int height = getComponent(0).getHeight();

      if(0 == height)
      {
         // if a setColumnInfoModel() call happend on the same stack before this call
         // components have not been layouted and don't have height 0.
         return _lastColumnHeightBeforeSetColums;
      }

      return height;
   }

   @Override
   public int getMaxWidth()
   {
      return ((QueryColumnPanel) getComponent(0)).getMaxWidth(_columnInfoModel.getAll());
   }


   public void addQueryAreaMouseListener(MouseListener mouseListener)
   {
      _mouseListeners.add(mouseListener);
   }

   private void addToAllKidComponents(MouseListener mouseListener)
   {
      for (Component component : getComponents())
      {
         if(component instanceof QueryColumnPanel)
         {
            ((QueryColumnPanel)component).addColumnMouseListener(mouseListener);
         }
         else
         {
            component.addMouseListener(mouseListener);
         }
      }
   }

   private MouseEvent transformMouseEvent(MouseEvent e)
   {
      if(this == e.getComponent())
      {
         return e;
      }
      else
      {
         Point lp = getLocationInQueryTextArea(e.getComponent());
         e.setSource(this);
         e.translatePoint(lp.x, lp.y);
         return e;
      }
   }

   static Point getLocationInQueryTextArea(Component childComp)
   {
      Point ret = new Point(0,0);

      Component comp = childComp;

      while(false == comp instanceof QueryTextArea)
      {
         ret.translate(comp.getX(), comp.getY());
         comp = comp.getParent();
      }

      return ret;
   }

   private class QueryTextAreaMouseListenerProxy implements MouseListener
   {
      @Override
      public void mouseClicked(MouseEvent e)
      {
         MouseEvent te = transformMouseEvent(e);
         MouseListener[] mouseListenersBuf = _mouseListeners.toArray(new MouseListener[_mouseListeners.size()]);
         for (MouseListener mouseListener : mouseListenersBuf)
         {
            mouseListener.mouseClicked(te);
         }

      }

      @Override
      public void mousePressed(MouseEvent e)
      {
         MouseEvent te = transformMouseEvent(e);
         MouseListener[] mouseListenersBuf = _mouseListeners.toArray(new MouseListener[_mouseListeners.size()]);
         for (MouseListener mouseListener : mouseListenersBuf)
         {
            mouseListener.mousePressed(te);
         }
      }

      @Override
      public void mouseReleased(MouseEvent e)
      {
         MouseEvent te = transformMouseEvent(e);
         MouseListener[] mouseListenersBuf = _mouseListeners.toArray(new MouseListener[_mouseListeners.size()]);
         for (MouseListener mouseListener : mouseListenersBuf)
         {
            mouseListener.mouseReleased(te);
         }
      }

      @Override
      public void mouseEntered(MouseEvent e)
      {
         MouseEvent te = transformMouseEvent(e);
         MouseListener[] mouseListenersBuf = _mouseListeners.toArray(new MouseListener[_mouseListeners.size()]);
         for (MouseListener mouseListener : mouseListenersBuf)
         {
            mouseListener.mouseEntered(te);
         }
      }

      @Override
      public void mouseExited(MouseEvent e)
      {
         MouseEvent te = transformMouseEvent(e);
         MouseListener[] mouseListenersBuf = _mouseListeners.toArray(new MouseListener[_mouseListeners.size()]);
         for (MouseListener mouseListener : mouseListenersBuf)
         {
            mouseListener.mouseExited(te);
         }
      }

   }

}
