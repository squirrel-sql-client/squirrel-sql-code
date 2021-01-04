package net.sourceforge.squirrel_sql.client.session.mainpanel.rowcolandsum;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTable;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetViewer;
import net.sourceforge.squirrel_sql.fw.datasetviewer.RowColSelectedCountListener;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.buttonchooser.ComboButton;
import net.sourceforge.squirrel_sql.fw.props.Props;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

public class RowColAndSumController
{
   private static final String PREF_KEY_ROW_COL_AND_SUM_DISPLAY = "RowColAndSumController.RowColAndSumDisplay";

   private final JPanel _panel;

   private final JPanel _containerPanel = new JPanel();

   private RowColLabelController _selectRowColLabelController = new RowColLabelController();
   private SumFunctionController _sumFunctionController = new SumFunctionController();
   private IDataSetViewer _dataSetViewer;
   private RowColAndSumDisplay _rowColAndSumDisplay;
   private RowColSumLayoutListener _rowColSumLayoutListener;

   public RowColAndSumController()
   {
      _panel = new JPanel(new BorderLayout(0,0));

      ComboButton btnCombo = new ComboButton();
      GUIUtils.styleAsToolbarButton(btnCombo);

      btnCombo.setPreferredSize(new Dimension(12, btnCombo.getPreferredSize().height));
      btnCombo.setMinimumSize(new Dimension(12, btnCombo.getMinimumSize().height));

      onDisplaySelected(RowColAndSumDisplay.valueOf(Props.getString(PREF_KEY_ROW_COL_AND_SUM_DISPLAY, RowColAndSumDisplay.ROW_COLS.name())));
      btnCombo.addActionListener(e -> onComboSelectDisplay(btnCombo));

      _panel.add(btnCombo, BorderLayout.WEST);
      _panel.add(_containerPanel, BorderLayout.CENTER);
   }

   public void setRowColSumLayoutListener(RowColSumLayoutListener rowColSumLayoutListener)
   {
      _rowColSumLayoutListener = rowColSumLayoutListener;
      _sumFunctionController.setRowColSumLayoutListener(rowColSumLayoutListener);
   }

   private void onComboSelectDisplay(ComboButton comboButton)
   {
      JPopupMenu popupMenu = new JPopupMenu();

      for (RowColAndSumDisplay value : RowColAndSumDisplay.values())
      {
         JMenuItem menuItem = new JMenuItem(value.toString());
         menuItem.addActionListener(e -> onDisplaySelected(value));
         popupMenu.add(menuItem);
      }

      popupMenu.addPopupMenuListener(comboButton.getPopupMenuListener());
      popupMenu.show(comboButton, 0, comboButton.getHeight());
   }

   private void onDisplaySelected(RowColAndSumDisplay rowColAndSumDisplay)
   {
      _rowColAndSumDisplay = rowColAndSumDisplay;
      Props.putString(PREF_KEY_ROW_COL_AND_SUM_DISPLAY, rowColAndSumDisplay.name());

      _containerPanel.removeAll();

      switch (rowColAndSumDisplay)
      {
         case ROW_COLS:
            _containerPanel.setLayout(new GridLayout(1,1));
            _containerPanel.add(_selectRowColLabelController.getPanel());
            break;
         case SUM_FUNCTIONS:
            _containerPanel.setLayout(new GridLayout(1,1));
            _containerPanel.add(_sumFunctionController.getPanel());
            break;
         case BOTH:
            _containerPanel.setLayout(new BorderLayout(3,3));

            _containerPanel.add(_selectRowColLabelController.getPanel(), BorderLayout.WEST);

            JPanel separator = GUIUtils.createVerticalSeparatorPanel();

            _containerPanel.add(separator);

            _containerPanel.add(_sumFunctionController.getPanel(), BorderLayout.EAST);
            break;
      }

      callSelectionChanged();

      if(null != _rowColSumLayoutListener)
      {
         _rowColSumLayoutListener.rowColSumLayoutDone();
      }
   }

   private void callSelectionChanged()
   {
      if (_dataSetViewer instanceof DataSetViewerTablePanel)
      {
         DataSetViewerTable table = ((DataSetViewerTablePanel) _dataSetViewer).getTable();
         onRowColSelectionChanged(table.getSelectedRowCount(), table.getSelectedColumnCount(), table.getSelectedRow(), table.getSelectedColumn());
      }
      else
      {
         onRowColSelectionChanged(0, 0, -1, -1);
      }
   }


   public JPanel getPanel()
   {
      return _panel;
   }

   public void setDataSetViewer(IDataSetViewer dataSetViewer)
   {
      _dataSetViewer = dataSetViewer;
      _dataSetViewer.setRowColSelectedCountListener(new RowColSelectedCountListener(){
         @Override
         public void rowColSelectedCountOrPosChanged(int selectedRowCount, int selectedColumnCount, int selectedRow, int selectedColumn)
         {
            onRowColSelectionChanged(selectedRowCount, selectedColumnCount, selectedRow, selectedColumn);
         }
      });

      callSelectionChanged();
   }

   private void onRowColSelectionChanged(int selectedRowCount, int selectedColumnCount, int selectedRow, int selectedColumn)
   {
      if(_rowColAndSumDisplay == RowColAndSumDisplay.ROW_COLS || _rowColAndSumDisplay == RowColAndSumDisplay.BOTH)
      {
         if(null != _dataSetViewer && 0 == _dataSetViewer.getRowCount())
         {
            _selectRowColLabelController.onRowColSelectionChanged(0, 0, -1, -1);
         }
         else
         {
            _selectRowColLabelController.onRowColSelectionChanged(selectedRowCount, selectedColumnCount, selectedRow, selectedColumn);
         }
      }

      if(_rowColAndSumDisplay == RowColAndSumDisplay.SUM_FUNCTIONS || _rowColAndSumDisplay == RowColAndSumDisplay.BOTH)
      {
         if (_dataSetViewer instanceof DataSetViewerTablePanel)
         {
            _sumFunctionController.onSelectionChanged(((DataSetViewerTablePanel)_dataSetViewer).getTable());
         }
      }
   }
}
