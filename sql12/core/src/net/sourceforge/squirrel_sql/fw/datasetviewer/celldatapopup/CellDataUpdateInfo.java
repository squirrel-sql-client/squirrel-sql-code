package net.sourceforge.squirrel_sql.fw.datasetviewer.celldatapopup;

import javax.swing.*;

public class CellDataUpdateInfo
{
   private final int _row;
   private final int _col;
   private final JTable _table;
   private final CellDataTextAreaDialog _parentDialog;

   public CellDataUpdateInfo(int row, int col, JTable table, CellDataTextAreaDialog parentDialog)
   {
      _row = row;
      _col = col;
      _table = table;
      _parentDialog = parentDialog;
   }

   public int getRow()
   {
      return _row;
   }

   public int getCol()
   {
      return _col;
   }

   public JTable getTable()
   {
      return _table;
   }

   public void closeParentDialog()
   {
      _parentDialog.setVisible(false);
      _parentDialog.dispose();
   }
}
