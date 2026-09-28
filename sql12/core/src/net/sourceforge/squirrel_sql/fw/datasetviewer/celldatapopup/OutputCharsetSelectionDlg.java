package net.sourceforge.squirrel_sql.fw.datasetviewer.celldatapopup;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import net.sourceforge.squirrel_sql.fw.gui.EditableComboBoxHandler;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class OutputCharsetSelectionDlg extends JDialog
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(OutputCharsetSelectionDlg.class);

   EditableComboBoxHandler filterCboHandler;
   JList<String> lstCharSets;
   JLabel lblSelectedCharset = new JLabel();
   JButton btnOk;
   JButton btnCancel;


   public OutputCharsetSelectionDlg(Window owningWindow)
   {
      super(owningWindow, s_stringMgr.getString("OutputCharsetSelectionDlg.title"), ModalityType.APPLICATION_MODAL);

      getContentPane().setLayout(new GridBagLayout());

      GridBagConstraints gbc;
      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,0,0), 0,0);
      getContentPane().add(new JLabel(s_stringMgr.getString("OutputCharsetSelectionDlg.filter")), gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,0,5), 0,0);
      JComboBox cboFilter = new JComboBox();
      filterCboHandler = new EditableComboBoxHandler(cboFilter, this.getClass().getName() + "filter");
      getContentPane().add(cboFilter, gbc);

      gbc = new GridBagConstraints(0,1,2,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,5,0,5), 0,0);
      lstCharSets = new JList<>();
      lstCharSets.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      getContentPane().add(new JScrollPane(lstCharSets), gbc);


      gbc = new GridBagConstraints(0,2,2,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,0,0), 0,0);
      getContentPane().add(createSelectedCharsetPanel(), gbc);

      gbc = new GridBagConstraints(0,3,2,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      getContentPane().add(createOkCancelPanel(), gbc);
   }

   private JPanel createSelectedCharsetPanel()
   {
      JPanel ret = new JPanel(new BorderLayout(5,0));
      ret.add(new JLabel(s_stringMgr.getString("OutputCharsetSelectionDlg.selected.charset")), BorderLayout.WEST);
      ret.add(lblSelectedCharset, BorderLayout.CENTER);
      return ret;
   }

   private JPanel createOkCancelPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,0, 0,0), 0,0);
      btnOk = new JButton(s_stringMgr.getString("OutputCharsetSelectionDlg.ok"));
      ret.add(btnOk, gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,5, 0,0), 0,0);
      btnCancel = new JButton(s_stringMgr.getString("OutputCharsetSelectionDlg.cancel"));
      ret.add(btnCancel, gbc);

      return ret;
   }

}
