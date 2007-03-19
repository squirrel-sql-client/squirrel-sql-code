package net.sourceforge.squirrel_sql.client.session.mainpanel;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.gui.SortableTable;
import net.sourceforge.squirrel_sql.fw.gui.SortableTableModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.prefs.Preferences;

public class SQLHistoryDlg extends JDialog
{
   private static final String PREF_KEY_SQL_HISTORY_DLG_WIDTH = "Squirrel.sqlHistoryDlgWidth";
   private static final String PREF_KEY_SQL_HISTORY_DLG_HEIGHT = "Squirrel.sqlHistoryDlgHeight";
   private static final String PREF_KEY_SQL_HISTORY_DLG_DIV_LOC = "Squirrel.sqlHistoryDlgDivLoc";

   /** Internationalized strings for this class. */
   private static final StringManager s_stringMgr =
       StringManagerFactory.getStringManager(SQLHistoryDlg.class);


   SortableTable tblHistoryItems;
   JButton btnClose;
   JTextField txtFilter;
   JButton btnApplyFilter;
   JComboBox cboFilterItems;
   JCheckBox chkFiltered;
   JSplitPane splSpilt;
   JTextArea txtSQL;

   public SQLHistoryDlg(JFrame mainFrame, String sqlPanelParentFrameName)
   {
      // i18n[SQLHistoryDlg.title=SQL History for {0}]
      super(mainFrame, s_stringMgr.getString("SQLHistoryDlg.title", sqlPanelParentFrameName), false);

      setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5),0,0);
      getContentPane().add(createFilterPanel(), gbc);

      gbc = new GridBagConstraints(0,1,1,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0,5,5,5),0,0);
      splSpilt = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
      getContentPane().add(splSpilt, gbc);

      tblHistoryItems = new SortableTable(new SortableTableModel(null));
      tblHistoryItems.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
      tblHistoryItems.getTableHeader().setResizingAllowed(true);
      tblHistoryItems.getTableHeader().setReorderingAllowed(true);
      tblHistoryItems.setAutoCreateColumnsFromModel(false);
      tblHistoryItems.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
      splSpilt.setTopComponent(new JScrollPane(tblHistoryItems));
      
      txtSQL = new JTextArea();
      txtSQL.setEditable(false);
      splSpilt.setBottomComponent(new JScrollPane(txtSQL));

      Dimension size = getDimension(mainFrame);
      setSize(size);

      splSpilt.setDividerLocation(Preferences.userRoot().getInt(PREF_KEY_SQL_HISTORY_DLG_DIV_LOC, size.height / 3));


      addWindowListener(new WindowAdapter()
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
   }

   void close()
   {
      setVisible(false);
      dispose();
   }


   private JPanel createFilterPanel()
   {
      JPanel ret = new JPanel();

      ret.setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,5,0,5),0,0);
      // i18n[SQLHistoryDlg.SQLPref=SQL]
      ret.add(new JLabel(s_stringMgr.getString("SQLHistoryDlg.SQLPref")), gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0,5,0,5),0,0);
      cboFilterItems = new JComboBox();
      ret.add(cboFilterItems, gbc);
      for (FilterCboItems filterCboItem : FilterCboItems.values())
      {
         cboFilterItems.addItem(filterCboItem);
      }


      gbc = new GridBagConstraints(2,0,1,1,1,0,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0,5,0,5),0,0);
      txtFilter = new JTextField();
      ret.add(txtFilter, gbc);
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            txtFilter.requestFocus();
         }
      });


      gbc = new GridBagConstraints(3,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,5,0,5),0,0);
      // i18n[SQLHistoryDlg.applyFilter=Apply]
      btnApplyFilter = new JButton(s_stringMgr.getString("SQLHistoryDlg.applyFilter"));
      ret.add(btnApplyFilter, gbc);
      getRootPane().setDefaultButton(btnApplyFilter);

      gbc = new GridBagConstraints(4,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,5,0,5),0,0);
      // i18n[SQLHistoryDlg.filtered=Filtered]
      chkFiltered = new JCheckBox(s_stringMgr.getString("SQLHistoryDlg.filtered"));
      ret.add(chkFiltered, gbc);


      // i18n[SQLHistoryDlg.close=Close]
      btnClose = new JButton(s_stringMgr.getString("SQLHistoryDlg.close"));
      gbc = new GridBagConstraints(5,0,1,1,0,0,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0,5,0,5),0,0);
      ret.add(btnClose, gbc);


      return ret;
   }

   private Dimension getDimension(JFrame mainFrame)
   {
      int prefWidth = Preferences.userRoot().getInt(PREF_KEY_SQL_HISTORY_DLG_WIDTH, 600);
      int perfHeight = Preferences.userRoot().getInt(PREF_KEY_SQL_HISTORY_DLG_HEIGHT, 600);
      return new Dimension(
         Math.min(prefWidth, mainFrame.getSize().width),
         Math.min(perfHeight, mainFrame.getSize().height)
      );
   }


   private void onWindowClosed()
   {
      Dimension size = getSize();
      Preferences.userRoot().putInt(PREF_KEY_SQL_HISTORY_DLG_WIDTH, size.width);
      Preferences.userRoot().putInt(PREF_KEY_SQL_HISTORY_DLG_HEIGHT, size.height);
      Preferences.userRoot().putInt(PREF_KEY_SQL_HISTORY_DLG_DIV_LOC, splSpilt.getDividerLocation());
   }

   static enum FilterCboItems
   {
      // i18n[SQLHistoryDlg.filterCboContains=contains]
      CONTAINS (s_stringMgr.getString("SQLHistoryDlg.filterCboContains")),

      // i18n[SQLHistoryDlg.filterCboStartsWith=starts with]
      STARTS_WITH (s_stringMgr.getString("SQLHistoryDlg.filterCboStartsWith")),

      // i18n[SQLHistoryDlg.filterCboEndsWith=ends with]
      ENDS_WITH (s_stringMgr.getString("SQLHistoryDlg.filterCboEndsWith")),

      // i18n[SQLHistoryDlg.filterCboRegEx=regular exp]
      REG_EX (s_stringMgr.getString("SQLHistoryDlg.filterCboRegEx"));
      private String _name;


      FilterCboItems(String name)
      {
         _name = name;
      }


      public String toString()
      {
         return _name;
      }
   }


}
