package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;

public class TableColumnHideConfigCtrl
{
   private final TableColumnHideConfigDlg _dlg;
   private final ColumnInfoModel _columnInfoModel;
   private boolean _columnHidingChanged;

   public TableColumnHideConfigCtrl(Window owningWindow, ColumnInfoModel columnInfoModel, String tableName)
   {
      _columnInfoModel = columnInfoModel;
      _dlg = new TableColumnHideConfigDlg(owningWindow, tableName);
      _dlg.lstLeft.setModel(new DefaultListModel<>());
      _dlg.lstRight.setModel(new DefaultListModel<>());

      initLists(_columnInfoModel);

      _dlg.btnRight.addActionListener(e -> onToRight());
      _dlg.btnLeft.addActionListener(e -> onToLeft());

      _dlg.txtFilterLeft.getDocument().addDocumentListener(new DocumentListener() {
         @Override
         public void insertUpdate(DocumentEvent e)
         {
            initListAndApplyFilter();
         }

         @Override
         public void removeUpdate(DocumentEvent e)
         {
            initListAndApplyFilter();
         }

         @Override
         public void changedUpdate(DocumentEvent e)
         {
            initListAndApplyFilter();
         }
      });

      _dlg.txtFilterRight.getDocument().addDocumentListener(new DocumentListener() {
         @Override
         public void insertUpdate(DocumentEvent e)
         {
            initListAndApplyFilter();
         }

         @Override
         public void removeUpdate(DocumentEvent e)
         {
            initListAndApplyFilter();
         }

         @Override
         public void changedUpdate(DocumentEvent e)
         {
            initListAndApplyFilter();
         }
      });



      _dlg.btnApplyHiding.addActionListener(e -> onApplyHiding());

      _dlg.setVisible(true);
   }

   private void initListAndApplyFilter()
   {
      initLists(_columnInfoModel);
      _applyFilter(_dlg.txtFilterLeft, _dlg.lstLeft);
      _applyFilter(_dlg.txtFilterRight, _dlg.lstRight);
   }

   private static void _applyFilter(JTextField txtFilter, JList<ColumnInfo> lst)
   {
      ArrayList<ColumnInfo> toRemove = new ArrayList<>();
      for (int i = 0; i < lst.getModel().getSize(); i++)
      {
         ColumnInfo colInfo = lst.getModel().getElementAt(i);
         if(   false == StringUtilities.isEmpty(txtFilter.getText(), true)
            && false == StringUtils.containsIgnoreCase(colInfo.getColumnName(), txtFilter.getText()))
         {
            toRemove.add(colInfo);
         }
      }
      toRemove.forEach(c -> ((DefaultListModel) lst.getModel()).removeElement(c));
   }

   private void initLists(ColumnInfoModel colInfoModel)
   {
      ArrayList<ColumnInfo> leftCols = new ArrayList<>();
      ArrayList<ColumnInfo> rightCols = new ArrayList<>();
      for (int i = 0; i < colInfoModel.getColCount(); i++)
      {
         if(colInfoModel.getOrderedColAt(i).isHidden())
         {
            rightCols.add(colInfoModel.getOrderedColAt(i));
         }
         else
         {
            leftCols.add(colInfoModel.getOrderedColAt(i));
         }
      }

      ((DefaultListModel<ColumnInfo>)_dlg.lstLeft.getModel()).clear();
      ((DefaultListModel<ColumnInfo>)_dlg.lstRight.getModel()).clear();

      ((DefaultListModel<ColumnInfo>)_dlg.lstLeft.getModel()).addAll(leftCols);
      ((DefaultListModel<ColumnInfo>)_dlg.lstRight.getModel()).addAll(rightCols);
   }

   private void onApplyHiding()
   {
      _dlg.setVisible(false);
      _dlg.dispose();
   }

   private void onToLeft()
   {
      ArrayList<ColumnInfo> rightSelCols = new ArrayList<>(_dlg.lstRight.getSelectedValuesList());
      rightSelCols.forEach(c -> c.setHidden(false));

      initListAndApplyFilter();

      selectColumnsAndScrollToFirst(rightSelCols, _dlg.lstLeft);

      _columnHidingChanged = true;
   }

   private void onToRight()
   {
      ArrayList<ColumnInfo> leftSelCols = new ArrayList<>(_dlg.lstLeft.getSelectedValuesList());
      leftSelCols.forEach(c -> c.setHidden(true));

      initListAndApplyFilter();

      selectColumnsAndScrollToFirst(leftSelCols, _dlg.lstRight);

      _columnHidingChanged = true;
   }

   private void selectColumnsAndScrollToFirst(ArrayList<ColumnInfo> colsToSelect, JList<ColumnInfo> lstToSelectIn)
   {
      ArrayList<Integer> indexes = new ArrayList<>();
      for (ColumnInfo leftSelCol : colsToSelect)
      {
         for (int i = 0; i < lstToSelectIn.getModel().getSize(); i++)
         {
            if(lstToSelectIn.getModel().getElementAt(i) == leftSelCol)
            {
               indexes.add(i);
            }
         }
      }

      lstToSelectIn.setSelectedIndices(indexes.stream().mapToInt(i -> i).toArray());

      if (false == indexes.isEmpty())
      {
         lstToSelectIn.ensureIndexIsVisible(indexes.get(0));
      }
   }

   public boolean columnHidingChanged()
   {
      return _columnHidingChanged;
   }
}
