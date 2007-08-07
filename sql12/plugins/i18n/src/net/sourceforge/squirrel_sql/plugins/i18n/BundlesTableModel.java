package net.sourceforge.squirrel_sql.plugins.i18n;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.Arrays;

public class BundlesTableModel extends DefaultTableModel
{

   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(I18nProps.class);


   private I18nBundle[] _bundles;

   public BundlesTableModel()
   {
      addColumn(s_stringMgr.getString("i18n.bundle"));
      // i18n[i18n.bundle=Bundle]
      addColumn(s_stringMgr.getString("i18n.missingTarnslation"));
      // i18n[i18n.missingTarnslation=Missing translations]

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
         return _bundles[row].getMissingTranslationsCount();
      }
      else
      {
         throw new IllegalArgumentException("Unknown column " + column);
      }

   }

   public void setBundles(I18nBundle[] bundles)
   {
      _bundles = bundles;

      Arrays.sort(_bundles);

      fireTableDataChanged();
   }

   public I18nBundle[] getBundlesForRows(int[] rows)
   {
      ArrayList<I18nBundle> ret = new ArrayList<I18nBundle>(rows.length);

      for (int i = 0; i < rows.length; i++)
      {
         ret.add(_bundles[rows[i]]);
      }

      return ret.toArray(new I18nBundle[ret.size()]);
   }
}
