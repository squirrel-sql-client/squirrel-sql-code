package net.sourceforge.squirrel_sql.plugins.syntax;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import java.util.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class AutoCorrectController
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(AutoCorrectController.class);


   private AutoCorrectDlg _dlg;
   private SyntaxPugin _syntaxPugin;

   public AutoCorrectController(SyntaxPugin syntaxPugin)
   {
      _syntaxPugin = syntaxPugin;

      _dlg = new AutoCorrectDlg(syntaxPugin.getApplication().getMainFrame());

      AutoCorrectData autoCorrectData = syntaxPugin.getAutoCorrectProviderImpl().getAutoCorrectData();

      Vector<Vector<String>> data = new Vector<Vector<String>>();

      for(Enumeration<String> e=autoCorrectData.getAutoCorrectsHash().keys(); e.hasMoreElements();)
      {
         String error = e.nextElement();
         Vector<String> row = new Vector<String>();
         row.add(error);
         String corr = autoCorrectData.getAutoCorrectsHash().get(error);

         corr = corr.replaceAll("\n","\\\\n");

         row.add(corr);
         data.add(row);
      }

      Collections.sort(data, new Comparator<Vector<String>>()
      {
         public int compare(Vector<String> row1, Vector<String> row2)
         {
            return row1.get(0).compareTo(row2.get(0));
         }
      });



      Vector<String> colHeaders = new Vector<String>();
		// i18n[syntax.errAbrev=error / abreviation]
		colHeaders.add(s_stringMgr.getString("syntax.errAbrev"));
		// i18n[syntax.corExt=correction / extension]
      colHeaders.add(s_stringMgr.getString("syntax.corExt"));


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


      _dlg.btnClose.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onClose();
         }
      });


   }

   private void onClose()
   {
      _dlg.setVisible(false);
      _dlg.dispose();
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

      Vector<Vector<String>> dataVector = dtm.getDataVector();

      Hashtable<String, String> newAutoCorrects = 
          new Hashtable<String, String>();

      for (int i = 0; i < dataVector.size(); i++)
      {
         Vector<String> row = dataVector.get(i);

         String error = row.get(0);
         String corr = row.get(1);

         if(null != error && null != corr && 0 != error.trim().length() && 0 != corr.trim().length() && false == error.equals(corr))
         {
            corr = corr.replaceAll("\\\\n", "\n");
            newAutoCorrects.put(error.trim().toUpperCase(), corr);
         }
      }

      _syntaxPugin.getAutoCorrectProviderImpl().setAutoCorrects(newAutoCorrects, _dlg.chkEnable.isSelected());



   }

   private void onAddRow()
   {
      DefaultTableModel dtm = (DefaultTableModel) _dlg.tblAutoCorrects.getModel();

      Vector<String> newRow = new Vector<String>();
      newRow.add("");
      newRow.add("");

      dtm.addRow(newRow);
   }
}
