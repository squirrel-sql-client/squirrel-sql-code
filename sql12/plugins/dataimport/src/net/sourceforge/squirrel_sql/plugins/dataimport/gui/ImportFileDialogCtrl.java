package net.sourceforge.squirrel_sql.plugins.dataimport.gui;
/*
 * Copyright (C) 2007 Thorsten Mürell
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.IOkClosePanelListener;
import net.sourceforge.squirrel_sql.client.gui.OkClosePanelEvent;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.DialogWidget;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.WidgetAdapter;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.WidgetEvent;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.plugins.dataimport.EDTMessageBoxUtil;
import net.sourceforge.squirrel_sql.plugins.dataimport.ImportDataIntoTableExecutor;

import static net.sourceforge.squirrel_sql.plugins.dataimport.gui.SpecialColumnMapping.*;

import net.sourceforge.squirrel_sql.plugins.dataimport.importer.IFileImporter;
import net.sourceforge.squirrel_sql.plugins.dataimport.importer.csv.CSVFileImporter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * This dialog has some options to specify how the file is imported into
 * the database.
 *
 * @author Thorsten Mürell
 */
public class ImportFileDialogCtrl
{

   private static final StringManager stringMgr = StringManagerFactory.getStringManager(ImportFileDialogCtrl.class);


   private final ImportFileDialog _importFileDialog;

   private String[][] previewData = null;
   private List<String> _importerColumns = new ArrayList<>();

   private ISession _session;
   private File _importFile;
   private IFileImporter _importer;
   private ITableInfo _table;
   private TableColumnInfo[] _columns;

   /**
    * @param importer   The file importer
    * @param table      The table to import into
    * @param columns    The columns of the import table
    */
   public ImportFileDialogCtrl(ISession session, File importFile, IFileImporter importer, ITableInfo table, TableColumnInfo[] columns)
   {
      _session = session;
      _importFile = importFile;
      _importer = importer;
      _table = table;
      _columns = columns;


      _importFileDialog = new ImportFileDialog(_importFile, importer.getImportFileTypeDescription(), _table);

      _importFileDialog.setSize(getDimension());

      _importFileDialog.addWidgetListener(new WidgetAdapter(){
         @Override
         public boolean widgetClosing(WidgetEvent evt)
         {
            onClosing();
            return true;
         }
      });

      _importFileDialog.btnsPnl.addListener(new IOkClosePanelListener()
      {
         public void okPressed(OkClosePanelEvent evt)
         {
            onOk();
         }

         public void closePressed(OkClosePanelEvent evt)
         {
            close();
         }

         public void cancelPressed(OkClosePanelEvent evt)
         {
            close();
         }
      });

      _importFileDialog.tblMapping.setModel(new ColumnMappingTableModel(columns));

      _importFileDialog.chkHeadersIncluded.setSelected(ImportFileDialogProps.isHeadersIncluded());
      _importFileDialog.chkHeadersIncluded.addActionListener(e -> onHeadersIncluded());
      onHeadersIncluded();

      _importFileDialog.btnSuggestColumns.addActionListener(e -> suggestColumns());
      _importFileDialog.btnOneToOneMapping.addActionListener(e -> mapOneToOne());


      _importFileDialog.txtCommitAfterInserts.setText("" + ImportFileDialogProps.getCommitAfterInsertsCount());
      _importFileDialog.chkSingleTransaction.setSelected(ImportFileDialogProps.isSingleTransaction());
      _importFileDialog.chkSingleTransaction.addActionListener(e -> updateTransactionPanel());
      updateTransactionPanel();

      _importFileDialog.chkEmptyTableBeforeImport.setSelected(ImportFileDialogProps.isEmptyTableOnImport());
      _importFileDialog.chkEmptyTableBeforeImport.addActionListener(e -> onEmptyTableBeforeImport());
      onEmptyTableBeforeImport();

      _importFileDialog.chkTrimValues.setSelected(ImportFileDialogProps.isTrimValues());

      _importFileDialog.chkSafeMode.setSelected(ImportFileDialogProps.isSaveMode());

      GUIUtils.enableCloseByEscape(_importFileDialog);
   }


   private void onEmptyTableBeforeImport()
   {
      if(_importFileDialog.chkEmptyTableBeforeImport.isSelected())
      {
         _importFileDialog.lblEmptyTableWarning.setText(stringMgr.getString("ImportFileDialogCtrl.all.data.will.be.lost.warning"));
      }
      else
      {
         _importFileDialog.lblEmptyTableWarning.setText(null);
      }
   }

   private void onHeadersIncluded()
   {
      _importFileDialog.btnSuggestColumns.setSelected(false);
      updatePreviewData();
   }

   private Dimension getDimension()
   {
      return new Dimension(ImportFileDialogProps.getDialogWidth(),ImportFileDialogProps.getDialogHeight());
   }

   public void onClosing()
   {
      Dimension size = _importFileDialog.getSize();

      ImportFileDialogProps.setDialogWidth(size.width);
      ImportFileDialogProps.setDialogHeight(size.height);

      ImportFileDialogProps.setCommitAfterInsertsCount(getCommitAfterEveryInsertsCount());
      ImportFileDialogProps.setEmptyTableOnImport(_importFileDialog.chkEmptyTableBeforeImport.isSelected());
      ImportFileDialogProps.setHeadersIncluded(_importFileDialog.chkHeadersIncluded.isSelected());
      ImportFileDialogProps.setSingleTransaction(_importFileDialog.chkSingleTransaction.isSelected());
      ImportFileDialogProps.setTrimValues(_importFileDialog.chkTrimValues.isSelected());
      ImportFileDialogProps.setSaveMode(_importFileDialog.chkSafeMode.isSelected());
   }



   private void updateTransactionPanel()
   {
      boolean intermediateCommits = (false == _importFileDialog.chkSingleTransaction.isSelected());

      _importFileDialog.txtCommitAfterInserts.setEnabled(intermediateCommits);
      _importFileDialog.lblCommitAfterInsertBegin.setEnabled(intermediateCommits);
      _importFileDialog.lblCommitAfterInsertEnd.setEnabled(intermediateCommits);
   }


   /**
    * Sets the preview data for the dialog
    *
    * @param data
    */
   public void setPreviewData(String[][] data)
   {
      previewData = data;
      updatePreviewData();
   }

   private void updatePreviewData()
   {
      JComboBox cboColumnMapping = new JComboBox();
      cboColumnMapping.addItem(SKIP.getVisibleString());
      cboColumnMapping.addItem(FIXED_VALUE.getVisibleString());
      cboColumnMapping.addItem(AUTO_INCREMENT.getVisibleString());
      cboColumnMapping.addItem(NULL.getVisibleString());

      cboColumnMapping.addActionListener(e -> onColumnMappingSelected(e));

      if (previewData != null && previewData.length > 0)
      {
         String[] headers = new String[previewData[0].length];
         String[][] data = previewData;

         if (_importFileDialog.chkHeadersIncluded.isSelected())
         {
            for (int i = 0; i < headers.length; i++)
            {
               headers[i] = data[0][i];
            }
            data = new String[previewData.length - 1][];
            for (int i = 1; i < previewData.length; i++)
            {
               data[i - 1] = previewData[i];
            }
         }
         else
         {
            for (int i = 0; i < headers.length; i++)
            {
               //i18n[ImportFileDialogCtrl.column=Column]
               headers[i] = stringMgr.getString("ImportFileDialog.column") + i;
            }
         }

         _importerColumns.clear();
         for (int i = 0; i < headers.length; i++)
         {
            _importerColumns.add(headers[i]);
         }

         for (String header : headers)
         {
            cboColumnMapping.addItem(header);
         }

         DefaultTableModel dataModel = new DefaultTableModel(data, headers)
         {
            @Override
            public boolean isCellEditable(int row, int column)
            {
               return false;
            }
         };

         _importFileDialog.tblPreview.setModel(dataModel);

      }

      if (null != _importFileDialog.tblMapping.getColumnModel().getColumn(1).getCellEditor())
      {
         // Makes sure the new cboColumnMapping is properly repainted
         _importFileDialog.tblMapping.getColumnModel().getColumn(1).getCellEditor().cancelCellEditing();
      }

      _importFileDialog.tblMapping.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(cboColumnMapping));
      ((ColumnMappingTableModel) _importFileDialog.tblMapping.getModel()).resetMappings();
      if (_importFileDialog.btnSuggestColumns.isSelected())
      {
         suggestColumns();
      }
      else if (_importFileDialog.btnOneToOneMapping.isSelected())
      {
         mapOneToOne();
      }
   }

   private void onColumnMappingSelected(ActionEvent e)
   {
      int selectedRow = _importFileDialog.tblMapping.getSelectedRow();
      if (selectedRow == -1)
      {
         return;
      }
      TableModel model = _importFileDialog.tblMapping.getModel();
      String comboValue = ((JComboBox) e.getSource()).getSelectedItem().toString();
      int fixedValueColumnIdx = ColumnMappingConstants.INDEX_FIXEDVALUE_COLUMN;
      if (comboValue.equals(AUTO_INCREMENT.getVisibleString()))
      {
         // If the user picks auto-increment, auto-fill the "Fixed value" column with 0 for the start
         // value if it is currently empty.
         if (model.getValueAt(selectedRow, fixedValueColumnIdx) == null ||
               "".equals(model.getValueAt(selectedRow, fixedValueColumnIdx)))
         {
            model.setValueAt("0", selectedRow, fixedValueColumnIdx);
         }

      }
      else if (!comboValue.equals(FIXED_VALUE.getVisibleString()))
      {
         // If the user chooses neither Fixed value nor Auto-Increment, then clear the "Fixed value"
         // field if it has a value.
         model.setValueAt("", selectedRow, fixedValueColumnIdx);
      }
      _importFileDialog.tblMapping.clearSelection();
   }

   private void mapOneToOne()
   {
      if (_importFileDialog.btnSuggestColumns.isSelected())
      {
         _importFileDialog.btnSuggestColumns.setSelected(false);
      }

      final ColumnMappingTableModel columnMappingTableModel = ((ColumnMappingTableModel) _importFileDialog.tblMapping.getModel());


      if (_importFileDialog.btnOneToOneMapping.isSelected())
      {
         columnMappingTableModel.resetMappings();

         for (int i = 0; i < Math.min(_importerColumns.size(), _columns.length); i++)
         {
            String importerColumn = _importerColumns.get(i);
            if (false == StringUtilities.isEmpty(importerColumn, true))
            {
               columnMappingTableModel.setValueAt(importerColumn, i, ColumnMappingConstants.INDEX_IMPORTFILE_COLUMN);
            }
         }
      }
      else
      {
         columnMappingTableModel.resetMappings();
      }

   }


   public void suggestColumns()
   {
      if(_importFileDialog.btnOneToOneMapping.isSelected())
      {
         _importFileDialog.btnOneToOneMapping.setSelected(false);
      }



      final ColumnMappingTableModel columnMappingTableModel = ((ColumnMappingTableModel) _importFileDialog.tblMapping.getModel());

      if (_importFileDialog.btnSuggestColumns.isSelected())
      {
         columnMappingTableModel.resetMappings();

         for (String importerColumn : _importerColumns)
         {
            if (null != importerColumn && !importerColumn.isEmpty())
            {
               final TableColumnInfo suggestedColumn = suggestColumn(importerColumn);
               if (suggestedColumn != null)
               {
                  final String suggestedColumnName = suggestedColumn.getColumnName();
                  int row = columnMappingTableModel.findTableColumn(suggestedColumnName);
                  columnMappingTableModel.setValueAt(importerColumn, row, ColumnMappingConstants.INDEX_IMPORTFILE_COLUMN);
               }
            }
         }
      }
      else
      {
         columnMappingTableModel.resetMappings();
      }
   }

   private TableColumnInfo suggestColumn(final String importerColumn)
   {
      for (TableColumnInfo colInfo : _columns)
      {
         if (colInfo.getColumnName().equalsIgnoreCase(importerColumn))
         {
            return colInfo;
         }
      }
      return null;
   }

   public void onOk()
   {
      ColumnMappingTableModel columnMappingModel = (ColumnMappingTableModel) _importFileDialog.tblMapping.getModel();


      if(0 == columnMappingModel.getColumnCountExcludingSkipped(_columns))
      {
         String msg = stringMgr.getString("ImportFileDialogCtrl.all.columns.skipped.msg");
         String title = stringMgr.getString("ImportFileDialogCtrl.all.columns.skipped.title");
         EDTMessageBoxUtil.showMessageDialogOnEDT(msg, title);
         return;
      }


      close();

      //Set the SafetySwitch of the CSVReader
      if (_importer instanceof CSVFileImporter)
      {
         ((CSVFileImporter) _importer).setSafetySwitch(_importFileDialog.chkSafeMode.isSelected());
      }

      _importer.setTrimValues(_importFileDialog.chkTrimValues.isSelected());


      int commitAfterEveryInsertsCount = getCommitAfterEveryInsertsCount();


      ImportDataIntoTableExecutor executor =
            new ImportDataIntoTableExecutor(
                  _session,
                  _table,
                  _columns,
                  _importerColumns,
                  columnMappingModel,
                  _importer,
                  _importFileDialog.chkSingleTransaction.isSelected(),
                  commitAfterEveryInsertsCount,
                  _importFileDialog.chkEmptyTableBeforeImport.isSelected());

      executor.setSkipHeader(_importFileDialog.chkHeadersIncluded.isSelected());
      executor.execute();
   }

   private int getCommitAfterEveryInsertsCount()
   {
      int commitAfterEveryInsertsCount = ImportFileDialogProps.getCommitAfterInsertsCount();
      if (null != _importFileDialog.txtCommitAfterInserts.getText())
      {
         try
         {
            int buf = Integer.valueOf(_importFileDialog.txtCommitAfterInserts.getText());
            if (commitAfterEveryInsertsCount >= 0)
            {
               commitAfterEveryInsertsCount = buf;
            }
         }
         catch (NumberFormatException e)
         {
            // ignore
         }
      }
      return commitAfterEveryInsertsCount;
   }

   private void close()
   {
      onClosing();
      _importFileDialog.setVisible(false);
      _importFileDialog.dispose();
   }

   public void show()
   {
      Main.getApplication().getMainFrame().addWidget(_importFileDialog);
      _importFileDialog.moveToFront();
      DialogWidget.centerWithinDesktop(_importFileDialog);
      _importFileDialog.setVisible(true);
   }
}



