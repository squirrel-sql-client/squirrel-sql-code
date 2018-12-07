package net.sourceforge.squirrel_sql.plugins.graph.nondbconst;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.graph.ColumnInfo;
import net.sourceforge.squirrel_sql.plugins.graph.ConstraintView;
import net.sourceforge.squirrel_sql.plugins.graph.TableFrameController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;


public class ConfigureNonDbConstraintController
{
   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(ConfigureNonDbConstraintController.class);



   private ConfigureNonDbConstraintDlg _dlg;
   private ConstraintDataSet _constraintDataSet;
   private ConstraintView _constraintView;
   private TableFrameController _fkTable;
   private TableFrameController _pkTable;



   public ConfigureNonDbConstraintController(ConstraintView constraintView, TableFrameController fkTable, TableFrameController pkTable)
   {
      _constraintView = constraintView;
      _fkTable = fkTable;
      _pkTable = pkTable;
      try
      {
         String fkTableName = fkTable.getTableInfo().getSimpleName();
         String pkTableName = pkTable.getTableInfo().getSimpleName();

         Window parent = SwingUtilities.windowForComponent(fkTable.getFrame());
         _dlg = new ConfigureNonDbConstraintDlg(parent, fkTableName, pkTableName);

         _constraintDataSet = new ConstraintDataSet(constraintView, fkTableName, pkTableName);
         _dlg._table.show(_constraintDataSet);
         _dlg._txtContstrName.setText(constraintView.getData().getConstraintName());

         initCbos(fkTable, pkTable, constraintView);

         _dlg._btnRemove.addActionListener(new ActionListener()
         {
            @Override
            public void actionPerformed(ActionEvent e)
            {
               onRemoveSelectedRow();
            }
         });

         _dlg._btnAdd.addActionListener(new ActionListener()
         {
            @Override
            public void actionPerformed(ActionEvent e)
            {
               onAddColumns();
            }
         });

         _dlg._btnOk.addActionListener(new ActionListener()
         {
            @Override
            public void actionPerformed(ActionEvent e)
            {
               onOk();
            }
         });

         _dlg._btnCancel.addActionListener(new ActionListener()
         {
            @Override
            public void actionPerformed(ActionEvent e)
            {
               close();
            }
         });

         _dlg.setVisible(true);
      }
      catch (DataSetException e)
      {
         throw new RuntimeException(e);
      }
   }

   private void onOk()
   {
      if(_constraintDataSet.isEmpty())
      {
         JOptionPane.showMessageDialog(_dlg, s_stringMgr.getString("graph.ConfigureNonDbConstraintController.emptyMsg"));
         return;
      }

      if(null == _dlg._txtContstrName.getText() || 0 == _dlg._txtContstrName.getText().trim().length())
      {
         JOptionPane.showMessageDialog(_dlg, s_stringMgr.getString("graph.ConfigureNonDbConstraintController.noNameMsg"));
         return;
      }

      _constraintDataSet.writeConstraintView(_constraintView, _fkTable, _pkTable);

      _constraintView.getData().setConstraintName(_dlg._txtContstrName.getText().trim());
      close();
   }

   private void close()
   {
      _dlg.setVisible(false);
      _dlg.dispose();
   }

   private void onAddColumns()
   {
      try
      {
         ColumnInfo fkColumn = (ColumnInfo) _dlg._cboFkCol.getSelectedItem();
         ColumnInfo pkColumn = (ColumnInfo) _dlg._cboPkCol.getSelectedItem();

         if(null == fkColumn || null == pkColumn)
         {
            return;
         }

         removeFromCbo(fkColumn, _dlg._cboFkCol);
         removeFromCbo(pkColumn, _dlg._cboPkCol);

         _constraintDataSet.addRow(fkColumn, pkColumn);
         _dlg._table.show(_constraintDataSet);
      }
      catch (DataSetException e)
      {
         throw new RuntimeException(e);
      }

   }

   private void removeFromCbo(ColumnInfo col, JComboBox cbo)
   {
      if(null == col)
      {
         return;
      }

      ((DefaultComboBoxModel) cbo.getModel()).removeElement(col);
      if(0 < cbo.getItemCount())
      {
         cbo.setSelectedIndex(0);
      }
   }

   private void onRemoveSelectedRow()
   {
      try
      {
         ArrayList<ContraintDisplayData> displayDatas = _constraintDataSet.removeRows(_dlg._table.getSelectedModelRows());
         if (0 < displayDatas.size())
         {
            _dlg._table.show(_constraintDataSet);
         }


         for (ContraintDisplayData displayData : displayDatas)
         {
            ((DefaultComboBoxModel)_dlg._cboFkCol.getModel()).addElement(displayData.getFkCol());
            ((DefaultComboBoxModel)_dlg._cboPkCol.getModel()).addElement(displayData.getPkCol());
         }


      }
      catch (DataSetException e)
      {
         throw new RuntimeException(e);
      }
   }


   private void initCbos(TableFrameController fkTable, TableFrameController pkTable, ConstraintView constraintView)
   {
      _dlg._cboFkCol.setModel(new DefaultComboBoxModel(getUnused(fkTable.getColumnInfos(), constraintView.getData().getFkColumnInfos())));
      _dlg._cboPkCol.setModel(new DefaultComboBoxModel(getUnused(pkTable.getColumnInfos(), constraintView.getData().getPkColumnInfos())));
   }

   private ColumnInfo[] getUnused(ColumnInfo[] all, ColumnInfo[] used)
   {
      ArrayList<ColumnInfo> ret = new ArrayList<ColumnInfo>();
      ret.addAll(Arrays.asList(all));
      ret.removeAll(Arrays.asList(used));
      return ret.toArray(new ColumnInfo[ret.size()]);
   }
}
