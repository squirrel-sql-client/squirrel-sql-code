package net.sourceforge.squirrel_sql.fw.gui.action.showdistinctvalues;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTable;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ExtTableColumn;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.props.Props;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JFrame;
import javax.swing.Timer;
import javax.swing.table.TableColumn;

public class ShowDistinctValuesCtrl
{

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ShowDistinctValuesCtrl.class);
   private static final String PREF_DISTINCT_IN_COLUMN = "ShowDistinctValuesCtrl.optDistinctInColumn";
   private static final String PREF_DISTINCT_IN_SELECTION = "ShowDistinctValuesCtrl.optDistinctInSelection";
   private static final String PREF_DISTINCT_IN_SELECTED_ROWS = "ShowDistinctValuesCtrl.optDistinctInSelectedRows";
   private static final String PREF_DISTINCT_IN_TABLE = "ShowDistinctValuesCtrl.optDistinctInTable";

   private static final String PREF_DISTINCT_IN_ROWS = "ShowDistinctValuesCtrl.optDistinctInRows";

   private ShowDistinctValuesDlg _dlg;

   private javax.swing.Timer _distinctTableUpdateTrigger;

   public ShowDistinctValuesCtrl(JFrame owningFrame, DataSetViewerTable sourceTable, ISession session)
   {
      if(0 == sourceTable.getDataSetViewerTableModel().getRowCount())
      {
         Main.getApplication().getMessageHandler().showErrorMessage(s_stringMgr.getString("ShowDistinctValuesCtrl.emptyTable"));
         return;
      }

      int selectedColumnIx = sourceTable.getSelectedColumn();
      if(-1 == selectedColumnIx)
      {
         selectedColumnIx = 0;
      }

      TableColumn selectedColumn = sourceTable.getColumnModel().getColumn(selectedColumnIx);

      if (false == selectedColumn instanceof ExtTableColumn)
      {
         Main.getApplication().getMessageHandler().showErrorMessage(s_stringMgr.getString("ShowDistinctValuesCtrl.unableToShowDistinctValuesForColumn", selectedColumn.getHeaderValue()));
         return;
      }

      _dlg = new ShowDistinctValuesDlg(owningFrame, ((ExtTableColumn) selectedColumn).getColumnDisplayDefinition().getColumnName());

      _dlg.optDistinctInColumn.setSelected(Props.getBoolean(PREF_DISTINCT_IN_COLUMN, true));
      _dlg.optDistinctInSelection.setSelected(Props.getBoolean(PREF_DISTINCT_IN_SELECTION, false));
      _dlg.optDistinctInSelectedRows.setSelected(Props.getBoolean(PREF_DISTINCT_IN_SELECTED_ROWS, false));
      _dlg.optDistinctInTable.setSelected(Props.getBoolean(PREF_DISTINCT_IN_TABLE, false));


      _dlg.optDistinctInRows.setSelected(Props.getBoolean(PREF_DISTINCT_IN_ROWS, false));
      _dlg.optDistinctInColumns.setSelected(false == Props.getBoolean(PREF_DISTINCT_IN_ROWS, false));

      onDistinctInColumnOrSelectionChanged();

      _distinctTableUpdateTrigger = new Timer(300, e -> new DistinctValuesTableUpdater(_dlg).updateTable(sourceTable, (ExtTableColumn)selectedColumn, session));
      _distinctTableUpdateTrigger.setRepeats(false);
      triggerTableUpdate();

      _dlg.optDistinctInColumn.addActionListener(e -> onDistinctInColumnOrSelectionChanged());
      _dlg.optDistinctInSelection.addActionListener(e -> onDistinctInColumnOrSelectionChanged());
      _dlg.optDistinctInSelectedRows.addActionListener(e -> onDistinctInColumnOrSelectionChanged());
      _dlg.optDistinctInTable.addActionListener(e -> onDistinctInColumnOrSelectionChanged());

      _dlg.optDistinctInColumn.addActionListener(e -> triggerTableUpdate());
      _dlg.optDistinctInSelection.addActionListener(e -> triggerTableUpdate());
      _dlg.optDistinctInSelectedRows.addActionListener(e -> triggerTableUpdate());
      _dlg.optDistinctInTable.addActionListener(e -> triggerTableUpdate());

      _dlg.optDistinctInRows.addActionListener(e -> triggerTableUpdate());
      _dlg.optDistinctInColumns.addActionListener(e -> triggerTableUpdate());

      GUIUtils.initLocation(_dlg, 500, 500);
      GUIUtils.enableCloseByEscape(_dlg);

      _dlg.setVisible(true);
   }


   private void triggerTableUpdate()
   {
      Props.putBoolean(PREF_DISTINCT_IN_COLUMN,_dlg.optDistinctInColumn.isSelected());
      Props.putBoolean(PREF_DISTINCT_IN_SELECTION,_dlg.optDistinctInSelection.isSelected());
      Props.putBoolean(PREF_DISTINCT_IN_SELECTED_ROWS,_dlg.optDistinctInSelectedRows.isSelected());
      Props.putBoolean(PREF_DISTINCT_IN_TABLE,_dlg.optDistinctInTable.isSelected());

      Props.putBoolean(PREF_DISTINCT_IN_ROWS, _dlg.optDistinctInRows.isSelected());

      _distinctTableUpdateTrigger.restart();
   }


   private void onDistinctInColumnOrSelectionChanged()
   {
      if(_dlg.optDistinctInColumn.isSelected())
      {
         _dlg.optDistinctInColumns.setSelected(true);
         _dlg.optDistinctInColumns.setEnabled(false);
         _dlg.optDistinctInRows.setEnabled(false);
      }
      else
      {
         _dlg.optDistinctInColumns.setEnabled(true);
         _dlg.optDistinctInRows.setEnabled(true);
      }
   }
}
