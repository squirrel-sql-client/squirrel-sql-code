package de.ixdb.squirrel_sql.plugins.cache;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.IMainPanelTab;
import net.sourceforge.squirrel_sql.fw.gui.ButtonTableHeader;
import net.sourceforge.squirrel_sql.fw.gui.SortableTableModel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.CellDataPopup;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class ProcessListTab  implements IMainPanelTab
{
   ProcessListPanel _pnlProcessList;
   private ISession _session;
   private ProcessData[] _procData;
   private ProcessListTabListener _processListTabListener;

   public ProcessListTab(ProcessData[] procData, ProcessListTabListener processListTabListener)
   {
      _procData = procData;


      _processListTabListener = processListTabListener;
      _pnlProcessList = new ProcessListPanel();

      DefaultTableModel dtm =
         new DefaultTableModel()
         {
            public boolean isCellEditable(int row, int column)
            {
               return false;
            }

            public Object getValueAt(int row, int column)
            {
               return onGetValueAt(row, column);
            }

            public String getColumnName(int column)
            {
               return ProcessData.getColumns()[column];
            }

            public int getRowCount()
            {
               if(null == ProcessListTab.this)
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
                  return ProcessListTab.this._procData.length;
               }
            }

            public int getColumnCount()
            {
               return ProcessData.getColumns().length;
            }

         };

      SortableTableModel stm = (SortableTableModel) _pnlProcessList.tblProcessList.getModel();
      stm.setActualModel(dtm);

      DefaultTableCellRenderer dtcr =
         new DefaultTableCellRenderer()
         {
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
            {
               Component rend = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
               if(null != value && 0 < value.toString().indexOf('\n'))
               {
                  rend.setBackground(Color.cyan);
               }
               else
               {
                  if (isSelected)
                  {
                     setBackground(table.getSelectionBackground());
                  }
                  else
                  {
                     setBackground(table.getBackground());
                  }
               }
               return rend;
            }
         };


      final TableColumnModel tcm = new DefaultTableColumnModel();
      for (int i = 0; i < ProcessData.getColumns().length; ++i)
      {
         final TableColumn col = new TableColumn(i);
         col.setHeaderValue(ProcessData.getColumns()[i]);
         col.setCellRenderer(dtcr);
         tcm.addColumn(col);
      }


      _pnlProcessList.tblProcessList.setColumnModel(tcm);


      _pnlProcessList.btnRefresh.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onRefresh();
         }
      });

      _pnlProcessList.btnTerminate.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onTerminate();
         }
      });

      _pnlProcessList.btnClose.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onClose();
         }
      });


      _pnlProcessList.tblProcessList.addMouseListener(new MouseAdapter()
      {
         public void mousePressed(MouseEvent evt)
         {
            if (evt.getClickCount() == 2)
            {
               // figure out which column the user clicked on
               // so we can pass in the right column description

               Point pt = evt.getPoint();
               int col = _pnlProcessList.tblProcessList.columnAtPoint(pt);
               ColumnDisplayDefinition dumDef = new ColumnDisplayDefinition(50, "Detail");
               CellDataPopup.showDialog(_pnlProcessList.tblProcessList, dumDef, evt,false);
            }
         }
      });
   }


   private void onClose()
   {
      _processListTabListener.closeRequested(_session);
   }

   private void onTerminate()
   {
      int selectedRow = _pnlProcessList.tblProcessList.getSelectedRow();
      selectedRow = _pnlProcessList.tblProcessList.getSortableTableModel().transfromToModelRow(selectedRow);

      String msg;
      if(-1 == selectedRow)
      {
         msg = "No process selected.\nPlease select the Process you wish to terminate.";
         JOptionPane.showMessageDialog(_session.getApplication().getMainFrame(), msg);
         return;
      }

      msg = "Do you really wish to terminate the selected process?";
      if(JOptionPane.YES_OPTION != JOptionPane.showConfirmDialog(_session.getApplication().getMainFrame(), msg, "Terminate process", JOptionPane.YES_NO_CANCEL_OPTION))
      {
         return;
      }

      int termRet = _processListTabListener.terminateRequested(_session, _procData[selectedRow]);

      if(1 != termRet)
      {
         msg = "Terminate should return 1 but returned " + termRet + ".\nMaybe terminate didn't succeed.";
         JOptionPane.showMessageDialog(_session.getApplication().getMainFrame(), msg);
         return;
      }
   }

   private void onRefresh()
   {
      int selectedRow = _pnlProcessList.tblProcessList.getSelectedRow();

      ProcessData old = null;
      if(-1 != selectedRow)
      {
         old = _procData[selectedRow];
      }

      _procData = _processListTabListener.refreshRequested(_session);

      SortableTableModel stm = (SortableTableModel) _pnlProcessList.tblProcessList.getModel();
      ((DefaultTableModel)stm.getActualModel()).fireTableDataChanged();
      stm.fireTableDataChanged();

      ButtonTableHeader bth = (ButtonTableHeader) _pnlProcessList.tblProcessList.getTableHeader();

      if(-1 != bth.getCurrentlySortedColumnIdx())
      {
         stm.sortByColumn(bth.getCurrentlySortedColumnIdx(), bth.isAscending());
      }


      _pnlProcessList.tblProcessList.repaint();

      if(null != old)
      {
         for (int i = 0; i < _procData.length; i++)
         {
            if(old.job == _procData[i].job)
            {
               _pnlProcessList.tblProcessList.getSelectionModel().setSelectionInterval(i,i);
               break;
            }
         }
      }

   }

   private Object onGetValueAt(int row, int column)
   {
      try
      {
         return ProcessData.class.getFields()[column].get(_procData[row]);
      }
      catch (IllegalAccessException e)
      {
         throw new RuntimeException(e);
      }
   }

   public String getTitle()
   {
      return "Processes";
   }

   public String getHint()
   {
      return getTitle();
   }

   public Component getComponent()
   {
      return _pnlProcessList;
   }

   public void setSession(ISession session)
   {
      _session = session;
   }

   public void sessionClosing(ISession session)
   {
      _session = null;
   }

   public void select()
   {
   }
}
