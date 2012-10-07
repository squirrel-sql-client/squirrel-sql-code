package net.sourceforge.squirrel_sql.client.session.mainpanel.overview;

import net.sourceforge.squirrel_sql.client.session.mainpanel.overview.datascale.DataScale;
import net.sourceforge.squirrel_sql.client.session.mainpanel.overview.datascale.DataScaleTable;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanel;

import javax.swing.*;
import java.awt.*;

public class OverviewHolder
{
   private DataScaleTable _dataScaleTable;
   private SimpleTable _simpleTable;

   public DataScaleTable getDataScaleTable()
   {
      return _dataScaleTable;
   }

   public boolean isEmpty()
   {
      return null == _dataScaleTable && null == _simpleTable;
   }

   public void setOverview(DataScaleTable dataScaleTable, boolean keepFormerRelations)
   {

      if(null != _dataScaleTable)
      {
         if (keepFormerRelations)
         {
            if (null != _dataScaleTable.getParentScaleTable())
            {
               dataScaleTable.setParentScaleTable(_dataScaleTable.getParentScaleTable());
            }

            if (null != _dataScaleTable.getKidScaleTable())
            {
               dataScaleTable.setKidScaleTable(_dataScaleTable.getKidScaleTable());
            }
         }
         else
         {
            dataScaleTable.setParentScaleTable(_dataScaleTable);
         }
      }
      _dataScaleTable = dataScaleTable;

      _simpleTable = null;
   }


   public void setOverview(DataSetViewerTablePanel simpleTable)
   {
      _simpleTable = new SimpleTable(simpleTable, _dataScaleTable);
      _dataScaleTable = null;
   }

   public void setParent()
   {
      if(null != _simpleTable)
      {
         _dataScaleTable = _simpleTable.getParent();
         _simpleTable = null;
         return;
      }

      if(null != _dataScaleTable)
      {
         _dataScaleTable = _dataScaleTable.getParentScaleTable();
         _simpleTable = null;
         return;
      }
   }

   public void setKid()
   {
      if (null != _dataScaleTable.getKidScaleTable())
      {
         _dataScaleTable = _dataScaleTable.getKidScaleTable();
         _simpleTable = null;
      }
      else
      {
         _simpleTable = new SimpleTable(_dataScaleTable.getKidSimpleTable(), _dataScaleTable);
         _dataScaleTable = null;
      }
   }


   public boolean hasParent()
   {
      if(null != _simpleTable)
      {
         return true;
      }

      if(null != _dataScaleTable)
      {
         return null != _dataScaleTable.getParentScaleTable();
      }

      throw new IllegalStateException("Either _simpleTable or _dataScaleTable must be initialized");
   }

   public boolean hasKid()
   {
      if(null != _simpleTable)
      {
         return false;
      }

      if(null != _dataScaleTable)
      {
         return null != _dataScaleTable.getKidScaleTable() || null != _dataScaleTable.getKidSimpleTable();
      }

      throw new IllegalStateException("Either _simpleTable or _dataScaleTable must be initialized");
   }

   public Component getComponent()
   {
      if(null != _simpleTable)
      {
         return _simpleTable.getComponent();
      }

      if(null != _dataScaleTable)
      {
         return _dataScaleTable;
      }

      throw new IllegalStateException("Either _simpleTable or _dataScaleTable must be initialized");
   }

   public boolean canShowInSimpleTable()
   {
      return null != _dataScaleTable;
   }

   public boolean isScaleTable()
   {
      return null != _dataScaleTable; 
   }

   public void doClickTracing(JButton intervalButtonClicked)
   {
      for (int i = 0; i < _dataScaleTable.getDataScaleTableModel().getDataScaleCount(); i++)
      {
         DataScale dataScale = _dataScaleTable.getDataScaleTableModel().getDataScaleAt(i);
         dataScale.initButtonColor(intervalButtonClicked);
      }
   }


   private static class SimpleTable
   {
      private DataScaleTable _parent;
      private DataSetViewerTablePanel _simpleTable;

      public SimpleTable(DataSetViewerTablePanel simpleTable, DataScaleTable parent)
      {
         _simpleTable = simpleTable;
         _parent = parent;
         _parent.setKidSimpleTable(_simpleTable);
      }

      public DataScaleTable getParent()
      {
         return _parent;
      }

      public Component getComponent()
      {
         return _simpleTable.getComponent();
      }
   }

//   private void stopCellEditing(DataScaleTable dataScaleTable)
//   {
//      TableCellEditor tableCellEditor = dataScaleTable.getCellEditor();
//      if (null != tableCellEditor)
//      {
//         tableCellEditor.stopCellEditing();
//         tableCellEditor.cancelCellEditing();
//      }
//   }
}
