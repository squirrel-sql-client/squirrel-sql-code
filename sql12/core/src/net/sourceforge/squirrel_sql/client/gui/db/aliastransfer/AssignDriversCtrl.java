package net.sourceforge.squirrel_sql.client.gui.db.aliastransfer;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTableModel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetUpdateableModel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.SimpleDataSet;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.persist.ValidationException;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriver;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

public class AssignDriversCtrl
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(AssignDriversCtrl.class);

   private final AssignDriversDlg _dlg;
   private boolean _ok;

   /**
    * Will show the driver translation dialog and will update the {@link SQLAlias#_driverId} in parameter sqlAliases
    */
   public AssignDriversCtrl(Properties driverIdentifierToName, List<SQLAlias> sqlAliases, JDialog parent)
   {
      try
      {
         _dlg = new AssignDriversDlg(parent);

         List<SQLDriver> sqlDriversSorted = new ArrayList<>(Main.getApplication().getDataCache().getDriverList());
         sqlDriversSorted.sort(Comparator.comparing(SQLDriver::getName));
         DefaultComboBoxModel<SQLDriver> availableDriversModel = (DefaultComboBoxModel<SQLDriver>) _dlg.cboAvailableDrivers.getModel();
         sqlDriversSorted.forEach(d -> availableDriversModel.addElement(d));


         List<ImportDriver> importDrivers = ImportDriver.create(driverIdentifierToName);
         DefaultComboBoxModel<ImportDriver> importDriversModel = (DefaultComboBoxModel<ImportDriver>) _dlg.cboDriversToAssign.getModel();
         importDrivers.forEach(d -> importDriversModel.addElement(d));


         initializeTableAndSuggestAssignments(sqlDriversSorted, importDrivers, importDriversModel);

         _dlg.btnOk.addActionListener(e -> onOk());
         _dlg.btnCancel.addActionListener(e -> close());

         _dlg.btnAssign.addActionListener(e -> onAssign());
         _dlg.btnUnassign.addActionListener(e -> onUnassign());


         GUIUtils.initLocation(_dlg, 500, 500);
         GUIUtils.enableCloseByEscape(_dlg);
         _dlg.setVisible(true);



      }
      catch (DataSetException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

   private void initializeTableAndSuggestAssignments(List<SQLDriver> sqlDriversSorted, List<ImportDriver> importDrivers, DefaultComboBoxModel<ImportDriver> importDriversModel) throws DataSetException
   {
      ColumnDisplayDefinition[] columnDisplayDefinitions = new ColumnDisplayDefinition[]
            {
                  new ColumnDisplayDefinition(50, s_stringMgr.getString("AssignDriversCtrl.tbl.column.assignment.target")),
                  new ColumnDisplayDefinition(51, s_stringMgr.getString("AssignDriversCtrl.tbl.column.assignment.source"))
            };

      ArrayList<Object[]> allRows = new ArrayList<>();
      SimpleDataSet assignedDriversDataSet = new SimpleDataSet(allRows, columnDisplayDefinitions);

      for (SQLDriver sqlDriver : sqlDriversSorted)
      {
         for (ImportDriver importDriver : importDrivers)
         {
            if(importDriver.getDriverIdentifier().equals(sqlDriver.getIdentifier().toString()))
            {
               allRows.add(createTableRow(sqlDriver, importDriver));
               importDriversModel.removeElement(importDriver);
            }
         }
      }

      _dlg.tblAssignedDrivers.show(assignedDriversDataSet);
   }

   private Object[] createTableRow(SQLDriver sqlDriver, ImportDriver importDriver)
   {
      return new Object[]{sqlDriver, importDriver};
   }

   private ImportDriver getImportDriver(Object[] tableRow)
   {
      return (ImportDriver) tableRow[1];
   }

   private SQLDriver getSqlDriver(Object[] tableRow)
   {
      return (SQLDriver) tableRow[0];
   }


   private void onUnassign()
   {
      ArrayList<ImportDriver> allUnassignedDrivers = new ArrayList<>();

      DefaultComboBoxModel unassignedCboModel = (DefaultComboBoxModel) _dlg.cboDriversToAssign.getModel();

      for (int i = 0; i < unassignedCboModel.getSize(); i++)
      {
         allUnassignedDrivers.add((ImportDriver) unassignedCboModel.getElementAt(i));
      }


      ImportDriver toSelectInCbo = null;
      int[] selectedModelRows = _dlg.tblAssignedDrivers.getSelectedModelRows();
      for (int selectedModelRow : selectedModelRows)
      {
         Object[] row = _dlg.tblAssignedDrivers.getTable().getDataSetViewerTableModel().getRowAt(selectedModelRow);
         allUnassignedDrivers.add(getImportDriver(row));

         toSelectInCbo = getImportDriver(row);
      }

      allUnassignedDrivers.sort(Comparator.comparing(importDriver -> importDriver.getDriverName()));

      unassignedCboModel.removeAllElements();

      allUnassignedDrivers.forEach(importDriver -> unassignedCboModel.addElement(importDriver));

      if(null != toSelectInCbo)
      {
         unassignedCboModel.setSelectedItem(toSelectInCbo);
      }

      _dlg.tblAssignedDrivers.getTable().getDataSetViewerTableModel().deleteRows(selectedModelRows);

      _dlg.tblAssignedDrivers.getTable().getSortableTableModel().fireTableDataChanged();
   }

   private void onAssign()
   {
      ImportDriver selectedImportDriver = (ImportDriver) _dlg.cboDriversToAssign.getSelectedItem();

      SQLDriver targetDriver = (SQLDriver) _dlg.cboAvailableDrivers.getSelectedItem();

      if(null == selectedImportDriver || null == targetDriver)
      {
         return;
      }

      _dlg.cboDriversToAssign.removeItem(selectedImportDriver);

      Object[] newTableRow = createTableRow(targetDriver, selectedImportDriver);

      _dlg.tblAssignedDrivers.getTable().getDataSetViewerTableModel().addRow(newTableRow);
      _dlg.tblAssignedDrivers.getTable().getDataSetViewerTableModel().allRowsAdded();
      _dlg.tblAssignedDrivers.getTable().getSortableTableModel().fireTableDataChanged();
   }

   private void onOk()
   {
      if(0 < _dlg.cboDriversToAssign.getModel().getSize())
      {
         int res = JOptionPane.showConfirmDialog(_dlg, s_stringMgr.getString("AssignDriversCtrl.msg.not.all.drivers.assigned.quest.cancel.import"));

         if(res != JOptionPane.YES_OPTION)
         {
            return;
         }
      }

      _ok = true;

      close();
   }

   private void close()
   {
      _dlg.setVisible(false);
      _dlg.dispose();
   }

   public boolean areAllDriversAssigned()
   {
      return _ok && 0 == _dlg.cboDriversToAssign.getModel().getSize();
   }


   public void updateDriverIdentifiersInAliases(List<SQLAlias> sqlAliases, AssignDriversCtrl assignDriversCtrl) throws ValidationException
   {
      HashMap<String, IIdentifier> oldIdentifierStringToNewIdentifier =  assignDriversCtrl.getOldIdentifierStringToNewIdentifier();

      for (SQLAlias sqlAlias : sqlAliases)
      {
         IIdentifier newIdentifier = oldIdentifierStringToNewIdentifier.get(sqlAlias.getDriverIdentifier().toString());
         sqlAlias.setDriverIdentifier(newIdentifier);
      }
   }

   private HashMap<String, IIdentifier> getOldIdentifierStringToNewIdentifier()
   {
      DataSetViewerTableModel tm = _dlg.tblAssignedDrivers.getTable().getDataSetViewerTableModel();

      HashMap<String, IIdentifier> ret = new HashMap<>();

      for (int i = 0; i < tm.getRowCount(); i++)
      {
         Object[] row =  tm.getRowAt(i);

         ImportDriver importDriver = getImportDriver(row);
         SQLDriver sqlDriver = getSqlDriver(row);

         ret.put(importDriver.getDriverIdentifier(), sqlDriver.getIdentifier());
      }

      return ret;
   }
}
