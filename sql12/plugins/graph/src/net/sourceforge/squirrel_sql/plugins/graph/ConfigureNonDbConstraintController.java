package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.util.Vector;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class ConfigureNonDbConstraintController
{
   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(ConfigureNonDbConstraintController.class);



   private ConfigureNonDbConstraintDlg _dlg;
   private ConstraintDataSet _constraintDataSet;
   private ConstraintView _constraintView;
   private TableFrameController _fkFrameOriginatingFrom;
   private TableFrameController _pkFramePointingTo;



   public ConfigureNonDbConstraintController(ISession session, ConstraintView constraintView, TableFrameController fkFrameOriginatingFrom, TableFrameController pkFramePointingTo)
   {
      _constraintView = constraintView;
      _fkFrameOriginatingFrom = fkFrameOriginatingFrom;
      _pkFramePointingTo = pkFramePointingTo;
      try
      {
         String fkTableName = fkFrameOriginatingFrom.getTableInfo().getSimpleName();
         String pkTableName = pkFramePointingTo.getTableInfo().getSimpleName();

         _dlg = new ConfigureNonDbConstraintDlg(session.getApplication().getMainFrame(), fkTableName, pkTableName);

         _constraintDataSet = new ConstraintDataSet(constraintView, fkTableName, pkTableName);
         _dlg._table.show(_constraintDataSet);
         _dlg._txtContstrName.setText(constraintView.getData().getConstraintName());

         initCbos(fkFrameOriginatingFrom, pkFramePointingTo, constraintView);

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

      _constraintDataSet.writeConstraintView(_constraintView, _fkFrameOriginatingFrom, _pkFramePointingTo);

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
         ColumnInfo fkColumn = (ColumnInfo) _dlg._cboLocalCol.getSelectedItem();
         ColumnInfo pkColumn = (ColumnInfo) _dlg._cboReferencingCol.getSelectedItem();
         if(_constraintDataSet.addRow(fkColumn, pkColumn))
         {
            _dlg._table.show(_constraintDataSet);
         }
      }
      catch (DataSetException e)
      {
         throw new RuntimeException(e);
      }

   }

   private void onRemoveSelectedRow()
   {
      try
      {
         if (_constraintDataSet.removeRows(_dlg._table.getSeletedRows()))
         {
            _dlg._table.show(_constraintDataSet);
         }
      }
      catch (DataSetException e)
      {
         throw new RuntimeException(e);
      }
   }


   private void initCbos(TableFrameController fkFrameOriginatingFrom, TableFrameController pkFramePointingTo, ConstraintView constraintView)
   {
      _dlg._cboLocalCol.setModel(new DefaultComboBoxModel(getCleanedFkColumnInfos(fkFrameOriginatingFrom, constraintView)));
      _dlg._cboReferencingCol.setModel(new DefaultComboBoxModel(pkFramePointingTo.getColumnInfos()));
   }

   private Vector<ColumnInfo> getCleanedFkColumnInfos(TableFrameController fkFrameOriginatingFrom, ConstraintView constraintView)
   {
      Vector<ColumnInfo> cleanedFkColumnInfos = new Vector<ColumnInfo>();
      ColumnInfo[] fkColumnInfos = fkFrameOriginatingFrom.getColumnInfos();

      for (ColumnInfo fkColumnInfo : fkColumnInfos)
      {
         boolean toAdd = false;

         if(null == fkColumnInfo.getImportedColumnName())
         {
            // We add if the fkColumnInfo is not yet referencing any column.
            toAdd = true;
         }
         else
         {
            // Or we add if the fkColumnInfo is already part of the constraint to configure.
            for (ColumnInfo columnInfo : constraintView.getData().getColumnInfos())
            {
               if(fkColumnInfo.getName().equalsIgnoreCase(columnInfo.getName()))
               {
                  toAdd = true;
                  break;
               }
            }
         }


         if(toAdd)
         {
            cleanedFkColumnInfos.add(fkColumnInfo);
         }
      }
      return cleanedFkColumnInfos;
   }
}
