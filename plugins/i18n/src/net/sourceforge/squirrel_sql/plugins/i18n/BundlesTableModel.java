package net.sourceforge.squirrel_sql.plugins.i18n;

import javax.swing.table.DefaultTableModel;

public class BundlesTableModel extends DefaultTableModel
{
   private I18nBundle[] _bundles;

   public BundlesTableModel()
   {
      addColumn("Bundle");
      addColumn("Translation");
   }

   public int getColumnCount()
   {
      return 2;
   }

   public boolean isCellEditable(int row, int column)
   {
      return false;
   }

   public int getRowCount()
   {
      if(null == _bundles)
      {
         return 0;
      }
      else
      {
         return _bundles.length;
      }
   }

   public Object getValueAt(int row, int column)
   {
      if(0 == column)
      {
         return _bundles[row].getName();
      }
      else if(1 == column)
      {
         return _bundles[row].getTranslationState();
      }
      else
      {
         throw new IllegalArgumentException("Unknown column " + column);
      }

   }

   public void setBundles(I18nBundle[] bundles)
   {
      _bundles = bundles;
      fireTableDataChanged();
   }
}
