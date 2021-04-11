package net.sourceforge.squirrel_sql.plugins.syntax;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;


public class AutoCorrectController
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(AutoCorrectController.class);


   private AutoCorrectDlg _dlg;
   private SyntaxPlugin _syntaxPugin;

   public AutoCorrectController(SyntaxPlugin syntaxPugin, Frame parent)
   {
      _syntaxPugin = syntaxPugin;

      _dlg = new AutoCorrectDlg(parent);

      AutoCorrectData autoCorrectData = syntaxPugin.getAutoCorrectProviderImpl().getAutoCorrectData();

      Vector<Vector<String>> data = new Vector<>();

      for(Enumeration<String> e=autoCorrectData.getAutoCorrectsHash().keys(); e.hasMoreElements();)
      {
         String error = e.nextElement();
         Vector<String> row = new Vector<>();
         row.add(error);
         String corr = autoCorrectData.getAutoCorrectsHash().get(error);

         row.add(corr);
         data.add(row);
      }

      data.sort(Comparator.comparing(row -> row.get(0)));



      Vector<String> colHeaders = new Vector<String>();
		// i18n[syntax.errAbrev=error / abreviation]
		colHeaders.add(s_stringMgr.getString("syntax.errAbrev"));
		// i18n[syntax.corExt=correction / extension]
      colHeaders.add(s_stringMgr.getString("syntax.corExt"));


      DefaultTableModel dtm = new DefaultTableModel();


      dtm.setDataVector(data, colHeaders);

      _dlg.tblAutoCorrects.setModel(dtm);

      _dlg.chkEnable.setSelected(autoCorrectData.isEnableAutoCorrects());

      _dlg.setSize(550, 500);

      GUIUtils.centerWithinParent(_dlg);

      _dlg.setVisible(true);

      _dlg.btnApply.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onApply();
         }
      });


      _dlg.btnNew.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onNew();
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


      _dlg.tblAutoCorrects.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
         @Override
         public void valueChanged(ListSelectionEvent e)
         {
            onSelectionChanged(e);
         }
      });

      _dlg.tblAutoCorrects.getSelectionModel().setSelectionInterval(0,0);

   }

   private void onSelectionChanged(ListSelectionEvent e)
   {
      if(1 == _dlg.tblAutoCorrects.getSelectedRows().length)
      {
         int selRow = _dlg.tblAutoCorrects.getSelectedRows()[0];

         DefaultTableModel dtm = (DefaultTableModel) _dlg.tblAutoCorrects.getModel();

         Vector<Vector> dataVector = dtm.getDataVector();

         Vector<String> row = dataVector.get(selRow);

         _dlg._txtAbreviation.setText(row.get(0));

         String correction = row.get(1);
         _dlg._txtCorrection.setText(correction);

      }
      else
      {
         _dlg._txtAbreviation.setText(null);
         _dlg._txtCorrection.setText(null);
      }
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

      save();
   }

   private void onApply()
   {
      if(    StringUtilities.isEmpty(_dlg._txtAbreviation.getText(), true)
          || StringUtilities.isEmpty(_dlg._txtCorrection.getText(), true))
      {
         return;
      }

      DefaultTableModel dtm = (DefaultTableModel) _dlg.tblAutoCorrects.getModel();

      Vector<Vector> dataVector = dtm.getDataVector();

      boolean found = false;
      for (int i = 0; i < dataVector.size(); i++)
      {
         Vector<String> row = dataVector.get(i);
         if (_dlg._txtAbreviation.getText().equalsIgnoreCase(row.get(0)))
         {
            row.set(1, _dlg._txtCorrection.getText());
            dtm.fireTableRowsUpdated(i,i);
            found = true;
            break;
         }
      }

      if(false == found)
      {
         Vector<String> row = new Vector<>();

         row.add(_dlg._txtAbreviation.getText());
         row.add(_dlg._txtCorrection.getText());
         dtm.addRow(row);

         _dlg.tblAutoCorrects.getSelectionModel().setSelectionInterval(dtm.getRowCount()-1, dtm.getRowCount()-1);

         Rectangle cellRect = _dlg.tblAutoCorrects.getCellRect(dtm.getRowCount() - 1, 0, true);

         _dlg.tblAutoCorrects.scrollRectToVisible(cellRect);
      }

      save();

   }

   private void save()
   {
      DefaultTableModel dtm = (DefaultTableModel) _dlg.tblAutoCorrects.getModel();

      Vector<Vector> dataVector = dtm.getDataVector();


      Hashtable<String, String> newAutoCorrects = new Hashtable<>();

      for (int i = 0; i < dataVector.size(); i++)
      {
         Vector<String> row = dataVector.get(i);

         String error = row.get(0);
         String corr = row.get(1);

         if(null != error && null != corr && 0 != error.trim().length() && 0 != corr.trim().length() && false == error.equals(corr))
         {
            newAutoCorrects.put(error.trim().toUpperCase(), corr);
         }
      }

      _syntaxPugin.getAutoCorrectProviderImpl().setAutoCorrects(newAutoCorrects, _dlg.chkEnable.isSelected());
   }

   private void onNew()
   {
      _dlg.tblAutoCorrects.clearSelection();
   }
}
