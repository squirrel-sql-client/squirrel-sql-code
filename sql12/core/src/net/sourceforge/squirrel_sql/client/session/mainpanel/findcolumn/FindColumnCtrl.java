package net.sourceforge.squirrel_sql.client.session.mainpanel.findcolumn;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ExtTableColumn;
import net.sourceforge.squirrel_sql.fw.gui.CloseByEscapeListener;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
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
import java.util.prefs.Preferences;

public class FindColumnCtrl
{
   public static final String PREF_KEY_FIND_COLUMN_SHEET_WIDTH = "Squirrel.findColumnSheet.width";
   public static final String PREF_KEY_FIND_COLUMN_SHEET_HEIGHT = "Squirrel.findColumnSheet.height";



   private final DataSetViewerTablePanel _dataSetViewerTablePanel;
   private final FindColumnDlg _findColumnDlg;
   private final DefaultListModel _leftListModel;
   private final DefaultListModel _rightListModel = new DefaultListModel();

   private ColumnSortingEnum _columnSorting = ColumnSortingEnum.NONE;


   private FindColumnColWrapper _columnToGoTo;
   private ArrayList<ExtTableColumn> _columnsToMoveToFront;
   private final boolean _dataSetContainsdifferentTables;

   public FindColumnCtrl(final Frame owningFrame, DataSetViewerTablePanel dataSetViewerTablePanel)
   {
      _dataSetContainsdifferentTables = dataSetContainsdifferentTables(dataSetViewerTablePanel);


      _dataSetViewerTablePanel = dataSetViewerTablePanel;
      _findColumnDlg = new FindColumnDlg(owningFrame);

      _leftListModel = new DefaultListModel();
      _findColumnDlg.lstLeft.setModel(_leftListModel);
      _findColumnDlg.lstLeft.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent e)
         {
            onMouseClicked(e);
         }
      });

      _findColumnDlg.lstRight.setModel(_rightListModel);

      _findColumnDlg.btnSortAsc.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            _columnSorting = ColumnSortingEnum.ASC;
            onFilterChanged();
         }
      });

      _findColumnDlg.btnSortDesc.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            _columnSorting = ColumnSortingEnum.DESC;
            onFilterChanged();
         }
      });



      _findColumnDlg.txtFilter.getDocument().addDocumentListener(new DocumentListener() {
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


      _findColumnDlg.btnRight.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onMoveRight();
         }
      });

      _findColumnDlg.btnLeft.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onMoveLeft();
         }
      });

      _findColumnDlg.btnUp.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onMoveUp();
         }
      });

      _findColumnDlg.btnDown.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onMoveDown();
         }
      });

      _findColumnDlg.btnToTableBegin.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onMoveToTableBegin();
         }
      });

      onFilterChanged();

      GUIUtils.enableCloseByEscape(_findColumnDlg, new CloseByEscapeListener() {
         @Override
         public void willCloseByEcape(JDialog dialog)
         {
            onClosing();
         }
      });

      _findColumnDlg.addWindowListener(new WindowAdapter() {
         @Override
         public void windowClosing(WindowEvent e)
         {
            onClosing();
         }
      });

      _findColumnDlg.setSize(getDimension());

      _findColumnDlg.showDialog();
   }

   private boolean dataSetContainsdifferentTables(DataSetViewerTablePanel dataSetViewerTablePanel)
   {
      HashSet<String> uniqueTableNamesOrNull = new HashSet<>();

      for (ExtTableColumn extTableColumn : dataSetViewerTablePanel.getTableColumns())
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
      int[] selIx = _findColumnDlg.lstRight.getSelectedIndices();

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
         Object item = _rightListModel.remove(selIx[i]);
         newSelIx[i] = selIx[i] - 1;
         _rightListModel.insertElementAt(item, newSelIx[i]);
      }

      _findColumnDlg.lstRight.setSelectedIndices(newSelIx);

      _findColumnDlg.lstRight.ensureIndexIsVisible(newSelIx[0]);

   }

   private void onMoveDown()
   {
      int[] selIx = _findColumnDlg.lstRight.getSelectedIndices();

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
         Object item = _rightListModel.remove(selIx[i]);
         newSelIx[i] = selIx[i] + 1;
         _rightListModel.insertElementAt(item, newSelIx[i]);
      }

      _findColumnDlg.lstRight.setSelectedIndices(newSelIx);

      _findColumnDlg.lstRight.ensureIndexIsVisible(newSelIx[newSelIx.length - 1]);

   }



   private void onMoveRight()
   {
      _findColumnDlg.lstRight.clearSelection();

      for (Object obj : _findColumnDlg.lstLeft.getSelectedValues())
      {
         FindColumnColWrapper findColumnColWrapper = (FindColumnColWrapper) obj;

         _leftListModel.removeElement(findColumnColWrapper);
         _rightListModel.addElement(findColumnColWrapper);

         _findColumnDlg.lstRight.addSelectionInterval(_rightListModel.size() - 1, _rightListModel.size() - 1);
      }
   }

   private void onMoveLeft()
   {
      _findColumnDlg.lstLeft.clearSelection();

      for (Object obj : _findColumnDlg.lstRight.getSelectedValues())
      {
         FindColumnColWrapper findColumnColWrapper = (FindColumnColWrapper) obj;

         _rightListModel.removeElement(findColumnColWrapper);
         _leftListModel.addElement(findColumnColWrapper);

         _findColumnDlg.lstLeft.addSelectionInterval(_leftListModel.size() - 1, _leftListModel.size() - 1);
      }

      onFilterChanged();
   }


   private void onFilterChanged()
   {
      ArrayList<FindColumnColWrapper> newListContent = new ArrayList<FindColumnColWrapper>();

      Object[] formerSelectedValues = _findColumnDlg.lstLeft.getSelectedValues();

      for (ExtTableColumn extTableColumn : _dataSetViewerTablePanel.getTableColumns())
      {
         FindColumnColWrapper colWrapper = new FindColumnColWrapper(extTableColumn, _dataSetContainsdifferentTables);

         String filterText = _findColumnDlg.txtFilter.getText();

         if(StringUtilities.isEmpty(filterText, true) || -1 < colWrapper.toString().toLowerCase().indexOf(filterText.toLowerCase()))
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
      _findColumnDlg.lstLeft.clearSelection();

      ArrayList<Integer> indicesToSelect = new ArrayList<Integer>();
      for (Object formerSelectedValue : formerSelectedValues)
      {
         indicesToSelect.add(_leftListModel.indexOf(formerSelectedValue));
      }

      for (Integer ix : indicesToSelect)
      {
         _findColumnDlg.lstLeft.addSelectionInterval(ix, ix);
      }
      //
      //////////////////////////////////////////////////////////

   }

   private void onMouseClicked(MouseEvent e)
   {
      if(2 == e.getClickCount())
      {
         _columnToGoTo = (FindColumnColWrapper) _findColumnDlg.lstLeft.getSelectedValue();

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
      _findColumnDlg.setVisible(false);
      _findColumnDlg.dispose();
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
      Preferences.userRoot().putInt(PREF_KEY_FIND_COLUMN_SHEET_WIDTH, _findColumnDlg.getWidth());
      Preferences.userRoot().putInt(PREF_KEY_FIND_COLUMN_SHEET_HEIGHT, _findColumnDlg.getHeight());
   }


   private Dimension getDimension()
   {
      return new Dimension(
            Preferences.userRoot().getInt(PREF_KEY_FIND_COLUMN_SHEET_WIDTH, 600),
            Preferences.userRoot().getInt(PREF_KEY_FIND_COLUMN_SHEET_HEIGHT, 500)
      );
   }

}
