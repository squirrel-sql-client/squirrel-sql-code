package net.sourceforge.squirrel_sql.client.session.mainpanel.findresultcolumn;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanelUtil;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ExtTableColumn;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.props.Props;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import javax.swing.DefaultListModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

public class FindResultColumnCtrl
{
   public static final String PREF_KEY_FIND_COLUMN_SHEET_WIDTH = "Squirrel.findColumnSheet.width";
   public static final String PREF_KEY_FIND_COLUMN_SHEET_HEIGHT = "Squirrel.findColumnSheet.height";

   public static final String PREF_KEY_FIND_IN_TABLE_NAME = "Squirrel.findColumnSheet.find.in.table.name";

   private final DataSetViewerTablePanel _dataSetViewerTablePanel;
   private final FindResultColumnDlg _findResultColumnDlg;

   private final DefaultListModel<FindColumnColWrapper> _leftListModel = new DefaultListModel<>();
   private final DefaultListModel<FindColumnColWrapper> _rightListModel = new DefaultListModel<>();

   private ColumnSortingEnum _columnSorting = ColumnSortingEnum.NONE;


   private FindColumnColWrapper _columnToGoTo;
   private ArrayList<ExtTableColumn> _columnsToMoveToFront;
   private final boolean _dataSetContainsdifferentTables;

   public FindResultColumnCtrl(final Frame owningFrame, DataSetViewerTablePanel dataSetViewerTablePanel)
   {
      _dataSetContainsdifferentTables = dataSetContainsdifferentTables(dataSetViewerTablePanel);


      _dataSetViewerTablePanel = dataSetViewerTablePanel;
      _findResultColumnDlg = new FindResultColumnDlg(owningFrame);

      _findResultColumnDlg.chkFindInTableNames.setSelected(Props.getBoolean(PREF_KEY_FIND_IN_TABLE_NAME, false));
      _findResultColumnDlg.chkFindInTableNames.setEnabled(_dataSetContainsdifferentTables);
      _findResultColumnDlg.chkFindInTableNames.addActionListener(e -> onFindInTableNames());


      _findResultColumnDlg.lstLeft.setModel(_leftListModel);
      _findResultColumnDlg.lstLeft.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent e)
         {
            onMouseClicked(e);
         }
      });

      _findResultColumnDlg.lstRight.setModel(_rightListModel);



      _findResultColumnDlg.lstLeft.addMouseListener(ColumnCopyHandler.getListPopupListener(_findResultColumnDlg.lstLeft));
      _findResultColumnDlg.lstRight.addMouseListener(ColumnCopyHandler.getListPopupListener(_findResultColumnDlg.lstRight));


      _findResultColumnDlg.btnSortAsc.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            _columnSorting = ColumnSortingEnum.ASC;
            onFilterChanged();
         }
      });

      _findResultColumnDlg.btnSortDesc.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            _columnSorting = ColumnSortingEnum.DESC;
            onFilterChanged();
         }
      });



      _findResultColumnDlg.txtFilter.getDocument().addDocumentListener(new DocumentListener() {
         @Override
         public void insertUpdate(DocumentEvent e)
         {
            onFilterChanged();
         }

         @Override
         public void removeUpdate(DocumentEvent e)
         {
            onFilterChanged();
         }

         @Override
         public void changedUpdate(DocumentEvent e)
         {
            onFilterChanged();
         }
      });


      _findResultColumnDlg.btnRight.addActionListener(e -> onMoveRight());

      _findResultColumnDlg.btnLeft.addActionListener(e -> onMoveLeft());

      _findResultColumnDlg.btnUp.addActionListener(e -> onMoveUp());

      _findResultColumnDlg.btnDown.addActionListener(e -> onMoveDown());

      _findResultColumnDlg.btnToTableBegin.addActionListener(e -> onMoveToTableBegin());

      onFilterChanged();

      GUIUtils.enableCloseByEscape(_findResultColumnDlg, dialog -> onClosing());

      _findResultColumnDlg.addWindowListener(new WindowAdapter() {
         @Override
         public void windowClosing(WindowEvent e)
         {
            onClosing();
         }
      });

      _findResultColumnDlg.setSize(getDimension());

      _findResultColumnDlg.showDialog();
   }

   private void onFindInTableNames()
   {
      Props.putBoolean(PREF_KEY_FIND_IN_TABLE_NAME, _findResultColumnDlg.chkFindInTableNames.isSelected());
      onFilterChanged();
   }

   private boolean dataSetContainsdifferentTables(DataSetViewerTablePanel dataSetViewerTablePanel)
   {
      HashSet<String> uniqueTableNamesOrNull = new HashSet<>();

      for (ExtTableColumn extTableColumn : DataSetViewerTablePanelUtil.getTableColumns(dataSetViewerTablePanel.getTable()))
      {
         uniqueTableNamesOrNull.add(TableNameAccess.getTableName(extTableColumn));
      }
      return uniqueTableNamesOrNull.size() > 1;
   }

   private void onMoveToTableBegin()
   {
      _columnsToMoveToFront = new ArrayList<>();

      for (int i = 0; i < _rightListModel.size(); i++)
      {
         FindColumnColWrapper wrapper = (FindColumnColWrapper) _rightListModel.get(i);

         _columnsToMoveToFront.add(wrapper.getExtTableColumn());
      }

      close();
   }


   private void onMoveUp()
   {
      int[] selIx = _findResultColumnDlg.lstRight.getSelectedIndices();

      if (null == selIx || 0 == selIx.length)
      {
         return;
      }


      for (int i : selIx)
      {
         if (0 == i)
         {
            return;
         }
      }

      int[] newSelIx = new int[selIx.length];
      for (int i = 0; i < selIx.length; ++i)
      {
         FindColumnColWrapper item = _rightListModel.remove(selIx[i]);
         newSelIx[i] = selIx[i] - 1;
         _rightListModel.insertElementAt(item, newSelIx[i]);
      }

      _findResultColumnDlg.lstRight.setSelectedIndices(newSelIx);

      _findResultColumnDlg.lstRight.ensureIndexIsVisible(newSelIx[0]);

   }

   private void onMoveDown()
   {
      int[] selIx = _findResultColumnDlg.lstRight.getSelectedIndices();

      if (null == selIx || 0 == selIx.length)
      {
         return;
      }


      for (int i : selIx)
      {
         if (_rightListModel.getSize() - 1 == i)
         {
            return;
         }
      }

      int[] newSelIx = new int[selIx.length];
      for (int i = selIx.length - 1; i >= 0; --i)
      {
         FindColumnColWrapper item = _rightListModel.remove(selIx[i]);
         newSelIx[i] = selIx[i] + 1;
         _rightListModel.insertElementAt(item, newSelIx[i]);
      }

      _findResultColumnDlg.lstRight.setSelectedIndices(newSelIx);

      _findResultColumnDlg.lstRight.ensureIndexIsVisible(newSelIx[newSelIx.length - 1]);

   }



   private void onMoveRight()
   {
      _findResultColumnDlg.lstRight.clearSelection();

      for (Object obj : _findResultColumnDlg.lstLeft.getSelectedValues())
      {
         FindColumnColWrapper findColumnColWrapper = (FindColumnColWrapper) obj;

         _leftListModel.removeElement(findColumnColWrapper);
         _rightListModel.addElement(findColumnColWrapper);

         _findResultColumnDlg.lstRight.addSelectionInterval(_rightListModel.size() - 1, _rightListModel.size() - 1);
      }
   }

   private void onMoveLeft()
   {
      _findResultColumnDlg.lstLeft.clearSelection();

      for (Object obj : _findResultColumnDlg.lstRight.getSelectedValues())
      {
         FindColumnColWrapper findColumnColWrapper = (FindColumnColWrapper) obj;

         _rightListModel.removeElement(findColumnColWrapper);
         _leftListModel.addElement(findColumnColWrapper);

         _findResultColumnDlg.lstLeft.addSelectionInterval(_leftListModel.size() - 1, _leftListModel.size() - 1);
      }

      onFilterChanged();
   }


   private void onFilterChanged()
   {
      ArrayList<FindColumnColWrapper> newListContent = new ArrayList<FindColumnColWrapper>();

      List<FindColumnColWrapper> formerSelectedValues = _findResultColumnDlg.lstLeft.getSelectedValuesList();

      for (ExtTableColumn extTableColumn : DataSetViewerTablePanelUtil.getTableColumns(_dataSetViewerTablePanel.getTable()))
      {
         FindColumnColWrapper colWrapper = new FindColumnColWrapper(extTableColumn, _dataSetContainsdifferentTables);

         String filterText = _findResultColumnDlg.txtFilter.getText();

         if(StringUtilities.isEmpty(filterText, true) || -1 < colWrapper.getMatchString(_findResultColumnDlg.chkFindInTableNames.isSelected()).toLowerCase().indexOf(filterText.toLowerCase()))
         {
            if(false == _rightListModel.contains(colWrapper))
            {
               newListContent.add(colWrapper);
            }
         }
      }

      if(_columnSorting == ColumnSortingEnum.ASC)
      {
         Collections.sort(newListContent, new Comparator<FindColumnColWrapper>() {
            @Override
            public int compare(FindColumnColWrapper o1, FindColumnColWrapper o2)
            {
               return o1.toString().compareTo(o2.toString());
            }
         });
      }
      else if(_columnSorting == ColumnSortingEnum.DESC)
      {
         Collections.sort(newListContent, new Comparator<FindColumnColWrapper>() {
            @Override
            public int compare(FindColumnColWrapper o1, FindColumnColWrapper o2)
            {
               return -o1.toString().compareTo(o2.toString());
            }
         });
      }


      _leftListModel.clear();

      for (FindColumnColWrapper findColumnColWrapper : newListContent)
      {
         _leftListModel.addElement(findColumnColWrapper);
      }


      ////////////////////////////////////////////////////////
      // restore selection
      _findResultColumnDlg.lstLeft.clearSelection();

      ArrayList<Integer> indicesToSelect = new ArrayList<Integer>();
      for (FindColumnColWrapper formerSelectedValue : formerSelectedValues)
      {
         indicesToSelect.add(_leftListModel.indexOf(formerSelectedValue));
      }

      for (Integer ix : indicesToSelect)
      {
         _findResultColumnDlg.lstLeft.addSelectionInterval(ix, ix);
      }
      //
      //////////////////////////////////////////////////////////

   }

   private void onMouseClicked(MouseEvent e)
   {
      if(2 == e.getClickCount())
      {
         _columnToGoTo = (FindColumnColWrapper) _findResultColumnDlg.lstLeft.getSelectedValue();

         if(null == _columnToGoTo)
         {
            return;
         }

         close();
      }
   }

   private void close()
   {
      onClosing();
      _findResultColumnDlg.setVisible(false);
      _findResultColumnDlg.dispose();
   }

   public FindColumnColWrapper getColumnToGoTo()
   {
      return _columnToGoTo;
   }

   public ArrayList<ExtTableColumn> getColumnsToMoveToFront()
   {
      return _columnsToMoveToFront;
   }

   private void onClosing()
   {
      Props.putInt(PREF_KEY_FIND_COLUMN_SHEET_WIDTH, _findResultColumnDlg.getWidth());
      Props.putInt(PREF_KEY_FIND_COLUMN_SHEET_HEIGHT, _findResultColumnDlg.getHeight());
   }


   private Dimension getDimension()
   {
      return new Dimension(
            Props.getInt(PREF_KEY_FIND_COLUMN_SHEET_WIDTH, 600),
            Props.getInt(PREF_KEY_FIND_COLUMN_SHEET_HEIGHT, 500)
      );
   }

}
