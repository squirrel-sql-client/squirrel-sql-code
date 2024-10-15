package net.sourceforge.squirrel_sql.fw.datasetviewer.tablefind;

import net.sourceforge.squirrel_sql.fw.gui.EditableComboBoxHandler;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.checkedlistbox.CheckedListBoxHandler;
import net.sourceforge.squirrel_sql.fw.gui.checkedlistbox.CheckedListBoxListener;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import org.apache.commons.lang3.StringUtils;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NarrowColsToSearchCtrl
{
   private static StringManager s_stringMgr = StringManagerFactory.getStringManager(NarrowColsToSearchCtrl.class);

   private static final String NARROW_COLS_TO_SEARCH_CTRL_FILTER_STRINGS_PREFIX = "NarrowColsToSearchCtrl.filter.strings_";
   private final EditableComboBoxHandler _editableFilterCboHandler;
   private final CheckedListBoxHandler<CheckColumnWrapper> _columnCheckedListBoxHandler;

   private NarrowColsToSearchDlg _dlg;
   private final FindService _findService;

   private ColsToSearchHolder _colsToSearchHolder = ColsToSearchHolder.UNFILTERED;

   public NarrowColsToSearchCtrl(ColsToSearchHolder curColsToSearchHolder, FindService findService)
   {
      _dlg = new NarrowColsToSearchDlg(findService.getParentWindow());
      _findService = findService;

      _columnCheckedListBoxHandler = new CheckedListBoxHandler<>(_dlg.lstColumns, new CheckedListBoxListener<>()
      {
         @Override
         public void listBoxItemToInvert(CheckColumnWrapper item)
         {
            item.setToSearch(!item.isToSearch());
         }

         @Override
         public void listBoxItemToRender(CheckColumnWrapper item, JCheckBox renderer)
         {
            renderer.setSelected(item.isToSearch());
            renderer.setText(item.getColumnName());
         }
      });

      initColumns(curColsToSearchHolder);

      _editableFilterCboHandler = new EditableComboBoxHandler(_dlg.cboFilter, NARROW_COLS_TO_SEARCH_CTRL_FILTER_STRINGS_PREFIX);
      _editableFilterCboHandler.addDocumentListener(new DocumentListener()
      {
         @Override
         public void insertUpdate(DocumentEvent e)
         {
            onApplyFilter();
         }

         @Override
         public void removeUpdate(DocumentEvent e)
         {
            onApplyFilter();
         }

         @Override
         public void changedUpdate(DocumentEvent e)
         {
            onApplyFilter();
         }
      });


      _dlg.btnInvertSelection.addActionListener(e -> onInvertSelection());
      _dlg.btnSelectAll.addActionListener(e -> onSelectAll());

      _dlg.btnOk.addActionListener(e -> onOk());

      GUIUtils.initLocation(_dlg, 300, 500);
      GUIUtils.enableCloseByEscape(_dlg, d -> _editableFilterCboHandler.saveCurrentItem());

      _dlg.getRootPane().setDefaultButton(_dlg.btnOk);
      _editableFilterCboHandler.focus();

      _dlg.setVisible(true);
   }

   private void initColumns(ColsToSearchHolder previousColsToSearchHolder)
   {
      List<CheckColumnWrapper> availableColumns;
      if(previousColsToSearchHolder == ColsToSearchHolder.UNFILTERED)
      {
         availableColumns = Stream.of(_findService.getColumnDisplayDefinitions()).map(cdd -> new CheckColumnWrapper(cdd)).collect(Collectors.toList());
         _colsToSearchHolder = new ColsToSearchHolder(availableColumns, _findService);
      }
      else
      {
         _colsToSearchHolder = previousColsToSearchHolder;
         availableColumns = _colsToSearchHolder.getCheckColumnWrappers();
      }
      _columnCheckedListBoxHandler.setItems(availableColumns);
   }

   private void onApplyFilter()
   {
      String filterText = _editableFilterCboHandler.getItem();

      _columnCheckedListBoxHandler.setItems(_colsToSearchHolder.getCheckColumnWrappers());

      if(StringUtilities.isEmpty(filterText, true))
      {
         return;
      }

      ArrayList<CheckColumnWrapper> toDisplay = new ArrayList<>();
      for(CheckColumnWrapper checkColumnWrapper : _colsToSearchHolder.getCheckColumnWrappers())
      {
         if(StringUtils.containsIgnoreCase(checkColumnWrapper.getColumnName(), filterText))
         {
            toDisplay.add(checkColumnWrapper);
         }
      }
      _columnCheckedListBoxHandler.setItems(toDisplay);
   }

   private void onInvertSelection()
   {
      _columnCheckedListBoxHandler.getAllItems().forEach(i -> i.setToSearch(!i.isToSearch()));
      _columnCheckedListBoxHandler.repaint();
   }

   private void onSelectAll()
   {
      _columnCheckedListBoxHandler.getAllItems().forEach(i -> i.setToSearch(true));
      _columnCheckedListBoxHandler.repaint();
   }

   private void onOk()
   {
      if(_columnCheckedListBoxHandler.getAllItems().stream().noneMatch(c -> c.isToSearch()))
      {
         JOptionPane.showMessageDialog(_dlg, s_stringMgr.getString("NarrowColsToSearchCtrl.no.columns.to.search"));
         return;
      }

      List<CheckColumnWrapper> checkColumnWrappers = _columnCheckedListBoxHandler.getAllItems();
      _colsToSearchHolder = new ColsToSearchHolder(checkColumnWrappers, _findService);

      close();
   }

   private void close()
   {
      _editableFilterCboHandler.saveCurrentItem();
      _dlg.setVisible(false);
      _dlg.dispose();
   }

   public ColsToSearchHolder getColsToSearchFilter()
   {
      return _colsToSearchHolder;
   }
}
