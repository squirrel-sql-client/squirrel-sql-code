package net.sourceforge.squirrel_sql.fw.gui.action.rowselectionwindow;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.TabButton;
import net.sourceforge.squirrel_sql.client.session.mainpanel.findresultcolumn.FindResultColumnUtil;
import net.sourceforge.squirrel_sql.client.session.mainpanel.rowcolandsum.RowColAndSumController;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTable;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanelUtil;
import net.sourceforge.squirrel_sql.fw.datasetviewer.coloring.markduplicates.MarkDuplicatesChooserController;
import net.sourceforge.squirrel_sql.fw.datasetviewer.tablefind.DataSetViewerFindHandler;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.props.Props;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class RowsWindowFrame extends JDialog
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(RowsWindowFrame.class);

   private static final String PREF_KEY_ROWS_WINDOW_FRAME_WIDTH = "Squirrel.rowselectionwindow.FrameWidth";
   private static final String PREF_KEY_ROWS_WINDOW_FRAME_HIGHT = "Squirrel.rowselectionwindow.FrameHight";

   private TabButton _btnToggleFind;
   private TabButton _btnToggleFindColumn;

   private final JPanel _contentPanel;
   private List<ColumnDisplayDefinition> _columnDisplayDefinitions;
   private ISession _session;
   private int _myCounterId;
   private DataSetViewerTablePanel _dataSetViewerTablePanel;
   private DataSetViewerFindHandler _dataSetViewerFindHandler;

   public RowsWindowFrame(Window parent, List<Object[]> rows, List<ColumnDisplayDefinition> columnDisplayDefinitions, ISession session)
   {
      super(parent);
      _columnDisplayDefinitions = columnDisplayDefinitions;
      _session = session;

      _contentPanel = initContentPanel(rows);

      getContentPane().setLayout(new GridLayout(1,1));
      getContentPane().add(_contentPanel);

      GUIUtils.initLocation(this, 500, 300);
      //GUIUtils.enableCloseByEscape(this, dlg -> onWindowClosing());

      addWindowListener(new WindowAdapter()
      {
         @Override
         public void windowClosing(WindowEvent e)
         {
            onWindowClosing();
         }
      });

      setVisible(true);
   }

   private JPanel initContentPanel(List<Object[]> rows)
   {
      JPanel ret;
      ret = new JPanel(new BorderLayout(0, 3));

      _dataSetViewerTablePanel = DataSetViewerTablePanelUtil.createDataSetViewerTablePanel(rows, _columnDisplayDefinitions, _session);
      _dataSetViewerFindHandler = new DataSetViewerFindHandler(_dataSetViewerTablePanel, _session, this);

      JPanel pnlNorth = createNorthPanel(_dataSetViewerTablePanel);
      ret.add(pnlNorth, BorderLayout.NORTH);



      GUIUtils.forceProperty(() ->
      {
         ret.remove(_dataSetViewerFindHandler.getComponent());
         ret.add(_dataSetViewerFindHandler.getComponent(), BorderLayout.CENTER);
         _dataSetViewerTablePanel.getTable().getDataSetViewerTableModel().fireTableDataChanged();
         _dataSetViewerFindHandler.getComponent().doLayout();

         return _dataSetViewerTablePanel.getTable().getRowCount() == 0 || 0 < _dataSetViewerTablePanel.getTable().getSelectedRows().length;
      });


      return ret;
   }

   private JPanel createNorthPanel(DataSetViewerTablePanel dataSetViewerTablePanel)
   {
      JPanel ret = new JPanel(new BorderLayout());
      ret.add(new JPanel(), BorderLayout.CENTER);

      JPanel pnlEast = new JPanel(new GridBagLayout());
      ret.add(pnlEast, BorderLayout.EAST);

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.VERTICAL, new Insets(3,3,0,0), 0,0);
      RowColAndSumController rowColAndSumController = new RowColAndSumController();
      rowColAndSumController.setDataSetViewer(dataSetViewerTablePanel);
      rowColAndSumController.setRowColSumLayoutListener(() -> onRowColSumLayoutDone(ret));
      pnlEast.add(rowColAndSumController.getPanel(), gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(3,10,0,0), 0,0);
      MarkDuplicatesChooserController markDuplicatesChooserController = new MarkDuplicatesChooserController(dataSetViewerTablePanel);
      pnlEast.add(GUIUtils.setPreferredWidth(GUIUtils.setPreferredHeight(markDuplicatesChooserController.getComponent(), GUIUtils.TAB_BUTTON_SIDE_LENGTH),45), gbc);

      gbc = new GridBagConstraints(2,0,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(3,1,0,3), 0,0);
      ImageIcon iconFindColumn = Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.FIND_COLUMN);
      _btnToggleFindColumn = GUIUtils.styleAsTabButton(new TabButton(iconFindColumn));
      _btnToggleFindColumn.addActionListener(e -> onFindColumn());
      pnlEast.add(_btnToggleFindColumn, gbc);

      gbc = new GridBagConstraints(3,0,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(3,1,0,3), 0,0);
      ImageIcon iconFind = Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.FIND);
      _btnToggleFind = GUIUtils.styleAsTabButton(new TabButton(iconFind));
      _btnToggleFind.addActionListener(e -> onFind());
      pnlEast.add(_btnToggleFind, gbc);

      return ret;
   }

   private void onFindColumn()
   {
      FindResultColumnUtil.findAndShowResultColumns(_dataSetViewerTablePanel, this);
   }

   private void onFind()
   {
      _dataSetViewerFindHandler.toggleShowFindPanel();
   }

   private void onRowColSumLayoutDone(JPanel pnlNorth)
   {
      pnlNorth.revalidate();
   }

   private void onWindowClosing()
   {
      Dimension size = getSize();

      Props.putInt(PREF_KEY_ROWS_WINDOW_FRAME_WIDTH, size.width);
      Props.putInt(PREF_KEY_ROWS_WINDOW_FRAME_HIGHT, size.height);

      Main.getApplication().getRowsWindowFrameRegistry().remove(this);
   }

   public void appendSelectedRows(List<Object[]> rows, ArrayList<ColumnDisplayDefinition> columnDisplayDefinitions)
   {
      HashMap<Integer, Integer> myIndexByOtherIndexMap = getMyIndexByOtherIndexMap(columnDisplayDefinitions);

      if(null == myIndexByOtherIndexMap)
      {
         throw new IllegalStateException("appendSelectedRows() should be called after columnsMatch() was called to check.");
      }

      for (Object[] row : rows)
      {
         Object[] myRow = new Object[row.length];

         for (int i = 0; i < row.length; i++)
         {
            myRow[myIndexByOtherIndexMap.get(i)] = row[i];
         }

         RowSelectionTableUtil.getActualTableModel(_dataSetViewerTablePanel).addRow(myRow);

      }

      RowSelectionTableUtil.getActualTableModel(_dataSetViewerTablePanel).allRowsAdded();


      DataSetViewerTable table = _dataSetViewerTablePanel.getTable();

      table.getDataSetViewerTableModel().fireTableDataChanged();
      table.validate();


      SwingUtilities.invokeLater(() -> scrollTableToBottom(table));

   }

   private void scrollTableToBottom(JTable table)
   {
      int rowCount = RowSelectionTableUtil.getActualTableModel(table).getRowCount();

      Rectangle cellRect = table.getCellRect(rowCount, 0, true);

      Rectangle visibleRect = table.getVisibleRect();
      cellRect.x = visibleRect.x;
      cellRect.height = 1;
      cellRect.width = 1;
      cellRect.y = 1000000;

      table.scrollRectToVisible(cellRect);

      //table.scrollRectToVisible(new Rectangle(0, 1000000, 1,1));
   }

   public void close()
   {
      onWindowClosing();
      setVisible(false);
      dispose();
   }

   public boolean columnsMatch(ArrayList<ColumnDisplayDefinition> otherCols)
   {
      return null != getMyIndexByOtherIndexMap(otherCols);
   }

   private HashMap<Integer, Integer> getMyIndexByOtherIndexMap(ArrayList<ColumnDisplayDefinition> otherCols)
   {
      if(_columnDisplayDefinitions.size() != otherCols.size())
      {
         return null;
      }

      HashMap<Integer, Integer> myIndexByOthersIndex = new HashMap<>();

      ArrayList<ColumnDisplayDefinition> myColsBuf = new ArrayList<>(_columnDisplayDefinitions);


      HashSet<Integer> usedIncicesOfMine = new HashSet<>();
      for (int i = 0; i < otherCols.size(); i++)
      {
         ColumnDisplayDefinition otherCol = otherCols.get(i);

         boolean found = false;
         for (int j = 0; j < myColsBuf.size(); j++)
         {
            ColumnDisplayDefinition myCol = myColsBuf.get(j);

            if(   match(otherCol, myCol)
               && false == usedIncicesOfMine.contains(j) // There may be mor than on column of same name and type
              )
            {
               myIndexByOthersIndex.put(i, j);
               usedIncicesOfMine.add(j);
               found = true;
               break;
            }
         }

         if(false == found)
         {
            myIndexByOthersIndex = null;
            break;
         }
      }

      return myIndexByOthersIndex;
   }

   private boolean match(ColumnDisplayDefinition col1, ColumnDisplayDefinition col2)
   {
      return Utilities.equalsRespectNull(col1.getColumnName(), col2.getColumnName()) && col1.getSqlType() == col2.getSqlType();
   }

   public void setMyCounterId(int myCounterId)
   {
      _myCounterId = myCounterId;
      setTitle(s_stringMgr.getString("RowsWindowFrame.title") + " / " + (null != _session ? _session.getTitle() : "") + " / (" + _myCounterId + ")");
   }


   public void markWindow(boolean b)
   {
      if (b)
      {
         _contentPanel.setBorder(BorderFactory.createLineBorder(Color.red, 3));
      }
      else
      {
         _contentPanel.setBorder(BorderFactory.createEmptyBorder());
      }
   }
}
