package net.sourceforge.squirrel_sql.client.session.mainpanel;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.regex.Pattern;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SessionUtils;
import net.sourceforge.squirrel_sql.client.session.event.ISQLPanelAdapter;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.table.SortableTableModel;
import net.sourceforge.squirrel_sql.fw.props.Props;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import static java.lang.Math.max;


public class SQLHistoryController
{
   private static final String PREF_KEY_SQL_HISTORY_COL_NAME_PREFIX_ = "Squirrel.sqlHistoryColIxPrefix_";

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SQLHistoryController.class);

   private SQLHistoryDlg _dlg;
   private ArrayList<SQLHistoryItemWrapper> _sqlHistoryItemWrappers;
   private boolean _dontReactToChkFiltered;
   private ISQLPanelAPI _sqlPanelAPI;

   private JPopupMenu _popUp = new JPopupMenu();


   @SuppressWarnings("serial")
   public SQLHistoryController(ISession session, ISQLPanelAPI sqlPanelAPI, ArrayList<SQLHistoryItem> items)
   {
      _sqlPanelAPI = sqlPanelAPI;
      _sqlHistoryItemWrappers = SQLHistoryItemWrapper.wrap(items);
      _dlg = new SQLHistoryDlg((JFrame) SessionUtils.getOwningFrame(session), session.getActiveSessionWindow().getTitle());

      GUIUtils.centerWithinParent(_dlg);

      _dlg.setVisible(true);

      _sqlPanelAPI.addSQLPanelListener(new ISQLPanelAdapter()
      {
         public void panelParentClosing()
         {
            _dlg.close();
         }
      });


      _dlg.addWindowListener(new WindowAdapter()
      {
         boolean onWindowClosedCalled = false;

         public void windowClosed(WindowEvent e)
         {
            if(false == onWindowClosedCalled)
            {
               onWindowClosed();
            }
         }

         public void windowClosing(WindowEvent e)
         {
            onWindowClosed();
            onWindowClosedCalled = true;
         }
      });


      SortableTableModel stm = (SortableTableModel) _dlg.tblHistoryItems.getModel();
      ArrayList<SQLHistoryItemWrapper> copy =  new ArrayList<>(_sqlHistoryItemWrappers);
      SqlHistoryTableModel dtm = new SqlHistoryTableModel(copy, stm);
      stm.setActualModel(dtm);

      final TableColumnModel tcm = new DefaultTableColumnModel();
      _dlg.tblHistoryItems.setColumnModel(tcm);
      for (int i = 0; i < SQLHistoryItemWrapper.getColumns().length; ++i)
      {
         final TableColumn col = new TableColumn(i);
         tcm.addColumn(col);
         String header = SQLHistoryItemWrapper.getColumns()[i];
         col.setHeaderValue(header);
         col.setPreferredWidth(Props.getInt(PREF_KEY_SQL_HISTORY_COL_NAME_PREFIX_ + header, 50));
      }


      // i18n[SQLHistoryController.mnuAppendSelectionToEditor=Append selected statements to SQL editor]
      JMenuItem mnuAppendSelectionToEditor = new JMenuItem(s_stringMgr.getString("SQLHistoryController.mnuAppendSelectionToEditor"));
      mnuAppendSelectionToEditor.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onAppendSelectionToEditor();
         }
      });
      _popUp.add(mnuAppendSelectionToEditor);


      _dlg.tblHistoryItems.getSelectionModel().addListSelectionListener(new ListSelectionListener()
      {
         public void valueChanged(ListSelectionEvent e)
         {
            onTblSelectionChanged(e);
         }
      });

      _dlg.tblHistoryItems.addMouseListener(new MouseAdapter()
      {
         public void mousePressed(MouseEvent evt)
         {
            if (evt.getClickCount() == 2)
            {
               onSQLSelected();
            }
         }
      });

      _dlg.tblHistoryItems.addMouseListener(new MouseAdapter()
      {
         public void mousePressed(MouseEvent evt)
         {
            maybeShowPopup(evt);
         }

         public void mouseReleased(MouseEvent e)
         {
            maybeShowPopup(e);
         }

      });




      _dlg.btnApplyFilter.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onApplyFilter();
         }
      });

      _dlg.chkFiltered.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onCheckFiltered();
         }
      });

      _dlg.btnClose.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            closeAndSetFocus();
         }
      });


      GUIUtils.enableCloseByEscape(_dlg, dw -> _sqlPanelAPI.getSQLEntryPanel().requestFocus());
   }

   private void maybeShowPopup(MouseEvent evt)
   {
      if (evt.isPopupTrigger())
      {
         _popUp.show(evt.getComponent(), evt.getX(), evt.getY());
      }
   }

   private void closeAndSetFocus()
   {
      _dlg.close();
      _sqlPanelAPI.getSQLEntryPanel().requestFocus();
   }

   private void onAppendSelectionToEditor()
   {
      int[] selRows = _dlg.tblHistoryItems.getSelectedRows();

      if (0 < selRows.length)
      {
         for (int i = 0; i < selRows.length; i++)
         {
            _sqlPanelAPI.getSQLEntryPanel().appendText("\n" + getSQLFromRow(selRows[i]) + "\n");
         }
      }
   }

   private void onSQLSelected()
   {
      int[] selRows = _dlg.tblHistoryItems.getSelectedRows();

      if (1 == selRows.length)
      {
         _sqlPanelAPI.getSQLEntryPanel().appendText("\n" + getSQLFromRow(selRows[0]));
         closeAndSetFocus();
      }
   }

   private void onCheckFiltered()
   {
      if(_dontReactToChkFiltered)
      {
         return;
      }


      if(_dlg.chkFiltered.isSelected())
      {
         onApplyFilter();
      }
      else
      {
         SortableTableModel stm = (SortableTableModel) _dlg.tblHistoryItems.getModel();
         SqlHistoryTableModel tm = (SqlHistoryTableModel) stm.getActualModel();

         ArrayList<SQLHistoryItemWrapper> clone = new ArrayList<>(_sqlHistoryItemWrappers);
         tm.setData(clone);
      }
   }


   private void onApplyFilter()
   {
      SortableTableModel stm = (SortableTableModel) _dlg.tblHistoryItems.getModel();
      SqlHistoryTableModel tm = (SqlHistoryTableModel) stm.getActualModel();

      if(_dlg.chkFiltered.isSelected())
      {
          ArrayList<SQLHistoryItemWrapper> clone = 
              new ArrayList<SQLHistoryItemWrapper>(_sqlHistoryItemWrappers);
         tm.setData(clone);
      }


      ArrayList<SQLHistoryItemWrapper> toRemove = new ArrayList<SQLHistoryItemWrapper>();
      for (SQLHistoryItemWrapper itemWrapper : tm.getData())
      {
         if(false == matchesFilter(itemWrapper))
         {
            toRemove.add(itemWrapper);
         }
      }


      boolean filtered;
      if(0 < toRemove.size())
      {
         tm.removeData(toRemove);
         filtered = true;
      }
      else
      {
         filtered = false;
      }

      try
      {
         _dontReactToChkFiltered = true;
         _dlg.chkFiltered.setSelected(filtered);
      }
      finally
      {
         _dontReactToChkFiltered = false;
      }
   }

   private boolean matchesFilter(SQLHistoryItemWrapper itemWrapper)
   {
      String filter = _dlg.txtFilter.getText();

      if(null == filter || 0 == filter.length())
      {
         return true;
      }

      String ucfilter;

      SQLHistoryDlg.FilterCboItems sel = (SQLHistoryDlg.FilterCboItems) _dlg.cboFilterItems.getSelectedItem();
      switch (sel)
      {
         case CONTAINS:
            ucfilter = filter.toUpperCase();
            return -1 < itemWrapper.getUpperCaseSQL().indexOf(ucfilter);
         case STARTS_WITH:
            ucfilter = filter.toUpperCase();
            return itemWrapper.getUpperCaseSQL().startsWith(ucfilter);
         case ENDS_WITH:
            ucfilter = filter.toUpperCase();
            return itemWrapper.getUpperCaseSQL().endsWith(ucfilter);
         case REG_EX:
            return Pattern.compile(filter, Pattern.CASE_INSENSITIVE).matcher(itemWrapper.getUpperCaseSQL()).matches();
      }

      throw new IllegalArgumentException("How can I ever get here?????");
   }

   private void onTblSelectionChanged(ListSelectionEvent e)
   {
      if (e.getValueIsAdjusting())
      {
         return;
      }

      int[] selRows = _dlg.tblHistoryItems.getSelectedRows();

      if (1 == selRows.length)
      {
         _dlg.txtSQL.setText(getSQLFromRow(selRows[0]));

         SwingUtilities.invokeLater(new Runnable()
         {
            public void run()
            {
               _dlg.txtSQL.scrollRectToVisible(new Rectangle(0,0,1,1));
            }
         });

      }
      else
      {
         _dlg.txtSQL.setText(null);
      }
   }

   private String getSQLFromRow(int row)
   {
      TableModel tm = _dlg.tblHistoryItems.getModel();

      return
         (String) tm.getValueAt(row, SQLHistoryItemWrapper.getSQLColIx());

   }

   private void onWindowClosed()
   {
      TableColumnModel tcm = _dlg.tblHistoryItems.getColumnModel();
      for (int i = 0; i < tcm.getColumnCount(); i++)
      {
         TableColumn col = tcm.getColumn(i);
         Props.putInt(PREF_KEY_SQL_HISTORY_COL_NAME_PREFIX_ + col.getHeaderValue(), max(col.getWidth(), 10));
      }
   }

   @SuppressWarnings("serial")
   private static class SqlHistoryTableModel extends DefaultTableModel
   {
      private ArrayList<SQLHistoryItemWrapper> _tempSqlHistoryItemWrappers;
      private SortableTableModel _parent;

      public SqlHistoryTableModel(ArrayList<SQLHistoryItemWrapper> tempsqlHistoryItemWrappers, SortableTableModel parent)
      {
         _tempSqlHistoryItemWrappers = tempsqlHistoryItemWrappers;
         _parent = parent;
      }


      public boolean isCellEditable(int row, int column)
      {
         return false;
      }

      public Object getValueAt(int row, int column)
      {
         return _tempSqlHistoryItemWrappers.get(row).getColum(column);
      }


      public String getColumnName(int column)
      {
         return SQLHistoryItemWrapper.getColumns()[column];
      }

      public int getRowCount()
      {
         if (null == _tempSqlHistoryItemWrappers)
         {
            // I have seen the reference to the outer class being null
            // when this method is called.
            // I have seen it only with the runtime jars
            // and on Linux.
            // I could not reproduce in my IDE.
            return 0;
         }
         else
         {
            return _tempSqlHistoryItemWrappers.size();
         }
      }

      public int getColumnCount()
      {
         return SQLHistoryItemWrapper.getColumns().length;
      }

      ArrayList<SQLHistoryItemWrapper> getData()
      {
         return _tempSqlHistoryItemWrappers;
      }

      public void setData(ArrayList<SQLHistoryItemWrapper> sqlHistoryItemWrappers)
      {
         _tempSqlHistoryItemWrappers = sqlHistoryItemWrappers;
         _parent.tableChanged();
      }

      public void removeData(ArrayList<SQLHistoryItemWrapper> toRemove)
      {
         _tempSqlHistoryItemWrappers.removeAll(toRemove);
         _parent.tableChanged();
      }
   }
}

