package de.ixdb.squirrel_sql.plugins.cache;

import net.sourceforge.squirrel_sql.fw.gui.SortableTable;
import net.sourceforge.squirrel_sql.fw.gui.SortableTableModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ProcessListPanel extends JPanel
{
   SortableTable tblProcessList;
   JButton btnRefresh;
   JButton btnTerminate;
   JButton btnClose;

   public ProcessListPanel()
   {
      setLayout(new BorderLayout());


      tblProcessList = new SortableTable(new SortableTableModel(null));
      tblProcessList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
      tblProcessList.getTableHeader().setResizingAllowed(true);
      tblProcessList.getTableHeader().setReorderingAllowed(true);
      tblProcessList.setAutoCreateColumnsFromModel(false);
      tblProcessList.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

      add(new JScrollPane(tblProcessList), BorderLayout.CENTER);



      JPanel pnlSouth = new JPanel();
      pnlSouth.setLayout(new GridBagLayout());
      GridBagConstraints gbc;

      btnRefresh = new JButton("Refresh list");
      btnTerminate = new JButton("Terminate selected Process");
      btnClose = new JButton("Close processes tab");

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,0,5), 0,0);
      pnlSouth.add(btnRefresh, gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,0,0,5), 0,0);
      pnlSouth.add(btnTerminate, gbc);

      gbc = new GridBagConstraints(2,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,0,0,5), 0,0);
      pnlSouth.add(btnClose,gbc);

      add(pnlSouth, BorderLayout.SOUTH);
   }

}
