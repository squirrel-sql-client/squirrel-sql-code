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

      Vector data = new Vector();

      for(Enumeration e=autoCorrectData.getAutoCorrectsHash().keys(); e.hasMoreElements();)
      {
         String error = (String) e.nextElement();
         Vector row = new Vector();
         row.add(error);
         String corr = (String) autoCorrectData.getAutoCorrectsHash().get(error);

         corr = corr.replaceAll("\n","\\\\n");

         row.add(corr);
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

      Vector dataVector = dtm.getDataVector();

      Hashtable newAutoCorrects = new Hashtable();

      for (int i = 0; i < dataVector.size(); i++)
      {
         Vector row = (Vector) dataVector.get(i);

         String error = (String) row.get(0);
         String corr = (String) row.get(1);

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

      Vector newRow = new Vector();
      newRow.add("");
      newRow.add("");

      dtm.addRow(newRow);
   }
}
