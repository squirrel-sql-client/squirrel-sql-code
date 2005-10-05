package net.sourceforge.squirrel_sql.plugins.syntax;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import java.util.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class AutoCorrectController
{
   private AutoCorrectDlg _dlg;
   private SyntaxPugin _syntaxPugin;

   public AutoCorrectController(SyntaxPugin syntaxPugin)
   {
      _syntaxPugin = syntaxPugin;

      _dlg = new AutoCorrectDlg(syntaxPugin.getApplication().getMainFrame());

      AutoCorrectData autoCorrectData = syntaxPugin.getAutoCorrectProviderImpl().getAutoCorrectData();

      Vector data = new Vector();

      for(Enumeration e=autoCorrectData.getAutoCorrectsHash().keys(); e.hasMoreElements();)
      {
         String error = (String) e.nextElement();
         Vector row = new Vector();
         row.add(error);
         row.add(autoCorrectData.getAutoCorrectsHash().get(error));
         data.add(row);
      }

      Collections.sort(data, new Comparator()
      {
         public int compare(Object o1, Object o2)
         {
            Vector row1 = (Vector) o1;
            Vector row2 = (Vector) o2;

            return ((String)row1.get(0)).compareTo((String)row2.get(0));
         }
      });



      Vector colHeaders = new Vector();
      colHeaders.add("error / abreviation");
      colHeaders.add("correction / extension");


      DefaultTableModel dtm = new DefaultTableModel();


      dtm.setDataVector(data, colHeaders);

      _dlg.tblAutoCorrects.setModel(dtm);

      _dlg.chkEnable.setSelected(autoCorrectData.isEnableAutoCorrects());

      _dlg.setSize(550, 280);

      GUIUtils.centerWithinParent(_dlg);

      _dlg.setVisible(true);

      _dlg.btnApply.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onApply();
         }
      });


      _dlg.btnAddRow.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onAddRow();
         }
      });

      _dlg.btnRemoveRows.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onRemoveRows();
         }
      });


   }

   private void onRemoveRows()
   {
      DefaultTableModel dtm = (DefaultTableModel) _dlg.tblAutoCorrects.getModel();

      int selRow = _dlg.tblAutoCorrects.getSelectedRow();

      while(-1 != selRow)
      {
         dtm.removeRow(selRow);
         selRow = _dlg.tblAutoCorrects.getSelectedRow();
      }
   }

   private void onApply()
   {
      TableCellEditor cellEditor = _dlg.tblAutoCorrects.getCellEditor();
      if(null != cellEditor)
      {
         cellEditor.stopCellEditing();
      }


      DefaultTableModel dtm = (DefaultTableModel) _dlg.tblAutoCorrects.getModel();

      Vector dataVector = dtm.getDataVector();

      Hashtable newAutoCorrects = new Hashtable();

      for (int i = 0; i < dataVector.size(); i++)
      {
         Vector row = (Vector) dataVector.get(i);

         String error = (String) row.get(0);
         String corr = (String) row.get(1);

         if(null != error && null != corr && 0 != error.trim().length() && 0 != corr.trim().length() && false == error.equals(corr))
         {
            newAutoCorrects.put(error.trim().toUpperCase(), corr);
         }
      }

      _syntaxPugin.getAutoCorrectProviderImpl().setAutoCorrects(newAutoCorrects, _dlg.chkEnable.isSelected());

      System.out.println(_dlg.getSize());


   }

   private void onAddRow()
   {
      DefaultTableModel dtm = (DefaultTableModel) _dlg.tblAutoCorrects.getModel();

      Vector newRow = new Vector();
      newRow.add("");
      newRow.add("");

      dtm.addRow(newRow);
   }
}
