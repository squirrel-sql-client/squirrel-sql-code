package net.sourceforge.squirrel_sql.fw.gui.table;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

class TableHeaderButtonRenderer implements TableCellRenderer
{
   private final TableHeaderMouseState _mouseState;
   private final TableAccessForHeader _tableAccess;
   private JButton _buttonRaised;
   private JButton _buttonLowered;

   TableHeaderButtonRenderer(Font font, TableHeaderMouseState mouseState, TableAccessForHeader tableAccess)
   {
      _mouseState = mouseState;
      _tableAccess = tableAccess;

      _buttonRaised = new JButton();
      _buttonRaised.putClientProperty("JButton.buttonType", "gradient"); // Added by Patch 2856103 for Apple/Mac
      _buttonRaised.setMargin(new Insets(0, 0, 0, 0));
      _buttonRaised.setFont(font);
      _buttonLowered = new JButton();
      _buttonLowered.putClientProperty("JButton.buttonType", "gradient"); // Added by Patch 2856103 for Apple/Mac
      _buttonLowered.setMargin(new Insets(0, 0, 0, 0));
      _buttonLowered.setFont(font);
      _buttonLowered.getModel().setArmed(true);
      _buttonLowered.getModel().setPressed(true);

      _buttonLowered.setMinimumSize(new Dimension(50, 25));
      _buttonRaised.setMinimumSize(new Dimension(50, 25));
   }

   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
   {
      if(value == null)
      {
         value = "";
      }

      // Rendering the column that the mouse has been pressed in.
      if(_mouseState.getPressedViewColumnIdx() == column && _mouseState.isPressed())
      {
         _buttonLowered.setText(value.toString());

         // If this is the column that the table is currently
         // sorted by then display the sort icon.
         // if (    column == getViewColumnIndex(getTableSortingAdmin().getSortedColumn())
         //         && getTableSortingAdmin().getSortedColumnIcon() != null)
         // {
         //    _buttonLowered.setIcon(getTableSortingAdmin().getSortedColumnIcon());
         // }
         // else
         // {
         //    _buttonLowered.setIcon(null);
         // }

         _buttonLowered.setIcon(null);
         TableSortingItem tableSortingItem = _tableAccess.getTableSortingAdmin().getTableSortingItem(_tableAccess.getTable().convertColumnIndexToModel(column));
         if(null != tableSortingItem && null != tableSortingItem.getSortedColumnIcon())
         {
            _buttonLowered.setIcon(tableSortingItem.getSortedColumnIcon());
         }

         return _buttonLowered;
      }

      // This is not the column that the mouse has been pressed in.
      _buttonRaised.setText(value.toString());

      // if (getTableSortingAdmin().getSortedColumnIcon() != null
      //      && column == getViewColumnIndex(getTableSortingAdmin().getSortedColumn()))
      // {
      //    _buttonRaised.setIcon(getTableSortingAdmin().getSortedColumnIcon());
      // }
      // else
      // {
      //    _buttonRaised.setIcon(null);
      // }

      _buttonRaised.setIcon(null);
      TableSortingItem tableSortingItem = _tableAccess.getTableSortingAdmin().getTableSortingItem(_tableAccess.getTable().convertColumnIndexToModel(column));
      if(null != tableSortingItem && null != tableSortingItem.getSortedColumnIcon())
      {
         _buttonRaised.setIcon(tableSortingItem.getSortedColumnIcon());
      }

      return _buttonRaised;
   }
}
