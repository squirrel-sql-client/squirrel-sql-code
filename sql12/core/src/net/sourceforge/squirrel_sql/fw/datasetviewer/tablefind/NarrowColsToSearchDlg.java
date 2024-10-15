package net.sourceforge.squirrel_sql.fw.datasetviewer.tablefind;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;

public class NarrowColsToSearchDlg extends JDialog
{
   private static StringManager s_stringMgr = StringManagerFactory.getStringManager(NarrowColsToSearchDlg.class);

   JComboBox<String> cboFilter;
   JList<CheckColumnWrapper> lstColumns;

   JButton btnSelectAll;
   JButton btnInvertSelection;
   JButton btnOk;

   public NarrowColsToSearchDlg(Window owner)
   {
      super(owner);
      setModal(true);
      setTitle(s_stringMgr.getString("NarrowColsToSearchDlg.title"));

      getContentPane().setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,0,5), 0,0);
      getContentPane().add(new JLabel(s_stringMgr.getString("NarrowColsToSearchDlg.search.cols")), gbc);

      gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(3,5,0,5), 0,0);
      cboFilter = new JComboBox<>();
      getContentPane().add(cboFilter, gbc);

      gbc = new GridBagConstraints(0,2,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10,5,0,5), 0,0);
      getContentPane().add(new JLabel(s_stringMgr.getString("NarrowColsToSearchDlg.uncheck.cols.to.exclude.from.search")), gbc);

      gbc = new GridBagConstraints(0,3,1,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(3,5,0,5), 0,0);
      lstColumns = new JList<>();
      getContentPane().add(new JScrollPane(lstColumns), gbc);

      gbc = new GridBagConstraints(0,4,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(3,5,0,5),0,0 );
      getContentPane().add(createListControlButtonsPanel(), gbc);

      gbc = new GridBagConstraints(0,5,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(15,5,5,5),0,0 );
      btnOk = new JButton(s_stringMgr.getString("NarrowColsToSearchDlg.ok"));
      getContentPane().add(btnOk, gbc);
   }

   private JPanel createListControlButtonsPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,0,0,0),0,0 );
      btnSelectAll = new JButton(s_stringMgr.getString("NarrowColsToSearchDlg.selectAll"));
      ret.add(btnSelectAll, gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,5,0,0),0,0 );
      btnInvertSelection = new JButton(s_stringMgr.getString("NarrowColsToSearchDlg.invertSelection"));
      ret.add(btnInvertSelection, gbc);

      return ret;
   }

}
