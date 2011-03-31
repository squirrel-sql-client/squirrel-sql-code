package net.sourceforge.squirrel_sql.plugins.graph;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Vector;

public class TableFramesModel
{
   private Vector<TableFrameController> _openTableFrameCtrls = new Vector<TableFrameController>();

   private TableFrameControllerListener _tableFrameControllerListener;
   private ConstraintViewsModelListener _constraintViewsModelListener;

   private ArrayList<TableFramesModelListener> _listeners = new ArrayList<TableFramesModelListener>();
   private ColumnInfoModelListener _columnInfoModelListener;

   private TableFrameController _lastRoot;
   private Timer _calcQueryRootTimer;

   public TableFramesModel()
   {
      _tableFrameControllerListener = new TableFrameControllerListener()
      {
         public void closed(TableFrameController tfc)
         {
            onTableFrameControllerClosed(tfc);
         }
      };

      _constraintViewsModelListener = new ConstraintViewsModelListener()
      {
         @Override
         public void constraintsChanged()
         {
            fireListeners(TableFramesModelChangeType.CONSTRAINT);
         }
      };

      _columnInfoModelListener = new ColumnInfoModelListener()
      {
         @Override
         public void columnInfosChanged(TableFramesModelChangeType changeType)
         {
            fireListeners(changeType);
         }
      };

      _calcQueryRootTimer = new Timer(300, new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            calculateQueryRoot();
         }
      });

      _calcQueryRootTimer.setRepeats(false);
      _calcQueryRootTimer.stop();

   }

   public Vector<TableFrameController> getTblCtrls()
   {
      return _openTableFrameCtrls;
   }

   void refreshTableNames()
   {
      for (TableFrameController openTableFrameCtrl : _openTableFrameCtrls)
      {
         openTableFrameCtrl.refreshTableName();
      }
   }

   void allTablesDbOrder()
   {
      for (TableFrameController openTableFrameCtrl : _openTableFrameCtrls)
      {
         openTableFrameCtrl.dbOrder();
      }
   }

   void allTablesByNameOrder()
   {
      for (TableFrameController openTableFrameCtrl : _openTableFrameCtrls)
      {
         openTableFrameCtrl.nameOrder();
      }
   }

   void allTablesPkConstOrder()
   {
      for (TableFrameController openTableFrameCtrl : _openTableFrameCtrls)
      {
         openTableFrameCtrl.pkConstraintOrder();
      }
   }

   void allTablesFilteredSelectedOrder()
   {
      for (TableFrameController openTableFrameCtrl : _openTableFrameCtrls)
      {
         openTableFrameCtrl.filteredSelectedOrder();
      }
   }

   private void onTableFrameControllerClosed(TableFrameController tfc)
   {
      _openTableFrameCtrls.remove(tfc);

      for (int i = 0; i < _openTableFrameCtrls.size(); i++)
      {
         TableFrameController tableFrameController = _openTableFrameCtrls.get(i);
         tableFrameController.tableFrameRemoved(tfc);
      }

      fireListeners(TableFramesModelChangeType.TABLE);
   }

   private void fireListeners(TableFramesModelChangeType changeType)
   {
      TableFramesModelListener[] listeners = _listeners.toArray(new TableFramesModelListener[_listeners.size()]);

      for (TableFramesModelListener listener : listeners)
      {
         listener.modelChanged(changeType);
      }
   }


   void refreshAllTables()
   {

      for (int i = 0; i < _openTableFrameCtrls.size(); i++)
      {
         TableFrameController tableFrameController = _openTableFrameCtrls.get(i);
         tableFrameController.refresh();
      }

      fireListeners(TableFramesModelChangeType.TABLE);
   }


   public boolean containsTable(TableFrameController tfc)
   {
      return _openTableFrameCtrls.contains(tfc);
   }

   public void addTable(TableFrameController tfc)
   {
      tfc.addTableFrameControllerListener(_tableFrameControllerListener);
      tfc.getConstraintViewsModel().addListener(_constraintViewsModelListener);
      tfc.getColumnInfoModel().addColumnInfoModelListener(_columnInfoModelListener);

      tfc.getFrame().addComponentListener(new ComponentAdapter()
      {
         @Override
         public void componentResized(ComponentEvent e)
         {
            onTableFrameMoved();
         }

         @Override
         public void componentMoved(ComponentEvent e)
         {
            onTableFrameMoved();
         }
      });


      _openTableFrameCtrls.add(tfc);
      fireListeners(TableFramesModelChangeType.TABLE);
   }

   private void onTableFrameMoved()
   {
      if(0 == _openTableFrameCtrls.size())
      {
         return;
      }
      _calcQueryRootTimer.restart();
   }

   private void calculateQueryRoot()
   {
      TableFrameController queryRoot = _openTableFrameCtrls.get(0);

      for (int i = 1; i < _openTableFrameCtrls.size(); i++)
      {
         Point loc = _openTableFrameCtrls.get(i).getFrame().getLocation();
         Point rootLoc = queryRoot.getFrame().getLocation();

         if(loc.x * loc.x + loc.y * loc.y <  rootLoc.x * rootLoc.x + rootLoc.y * rootLoc.y )
         {
            queryRoot = _openTableFrameCtrls.get(i);
         }

         if(_lastRoot != queryRoot)
         {
            _lastRoot = queryRoot;
            fireListeners(TableFramesModelChangeType.ROOT_TABLE);
         }
      }
   }

   public void addTableFramesModelListener(TableFramesModelListener tableFramesModelListener)
   {
      _listeners.remove(tableFramesModelListener);
      _listeners.add(tableFramesModelListener);
   }

   public void hideNoJoins(boolean b)
   {
      for (TableFrameController openTableFrameCtrl : _openTableFrameCtrls)
      {
         openTableFrameCtrl.getConstraintViewsModel().hideNoJoins(b);
      }


   }

   public boolean containsUniddenNoJoins()
   {
      for (TableFrameController openTableFrameCtrl : _openTableFrameCtrls)
      {
         if(openTableFrameCtrl.getConstraintViewsModel().containsUniddenNoJoins())
         {
            return true;
         }
      }

      return false;
   }
}
