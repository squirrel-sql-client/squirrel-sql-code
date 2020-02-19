package net.sourceforge.squirrel_sql.client.gui.db.aliastransfer;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanel;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriver;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class AssignDriversDlg extends JDialog
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(AssignDriversDlg.class);

   JButton btnAssign;
   JButton btnUnassign;

   JComboBox<SQLDriver> cboAvailableDrivers = new JComboBox<>();
   JComboBox<ImportDriver> cboDriversToAssign = new JComboBox<>();

   DataSetViewerTablePanel tblAssignedDrivers;

   JButton btnOk = new JButton(s_stringMgr.getString("AssignDriversDlg.btn.ok"));
   JButton btnCancel = new JButton(s_stringMgr.getString("AssignDriversDlg.btn.cancel"));

   public AssignDriversDlg(JDialog parent)
   {
      super(parent, s_stringMgr.getString("AssignDriversDlg.title"), true);

      getContentPane().setLayout(new GridBagLayout());


      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,0,0), 0,0);
      getContentPane().add(createComboBoxesPanel(), gbc);

      gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,2,5), 0,0);
      getContentPane().add(new JLabel(s_stringMgr.getString("AssignDriversDlg.assigned.drivers")), gbc);


      gbc = new GridBagConstraints(0,2,1,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,5,5,5), 0,0);
      getContentPane().add(createTablePanel(), gbc);

      gbc = new GridBagConstraints(0,3,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,2,5), 0,0);
      getContentPane().add(createButtonPanel(), gbc);
   }

   private JPanel createTablePanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,1,1,GridBagConstraints.NORTHWEST,GridBagConstraints.BOTH, new Insets(0,0,0,0), 0,0);
      tblAssignedDrivers = new DataSetViewerTablePanel();
      tblAssignedDrivers.init(null, null);
      JScrollPane scrollPane = new JScrollPane(tblAssignedDrivers.getTable());
      ret.add(scrollPane, gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,5,0,0), 0,0);
      btnUnassign = new JButton(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.UNLOCK));
      btnUnassign.setToolTipText(s_stringMgr.getString("AssignDriversDlg.unassign.button.tooltip"));
      ret.add(btnUnassign, gbc);

      return ret;
   }

   private JPanel createComboBoxesPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST,GridBagConstraints.NONE, new Insets(0,0,0,5), 0,0);
      ret.add(new JLabel(s_stringMgr.getString("AssignDriversDlg.available.drivers")), gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.NORTHWEST,GridBagConstraints.NONE, new Insets(0,0,0,5), 0,0);
      ret.add(new JLabel(s_stringMgr.getString("AssignDriversDlg.drivers.to.assign")), gbc);



      gbc = new GridBagConstraints(0,1,1,1,1,0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL, new Insets(0,0,0,5), 0,0);
      ret.add(cboAvailableDrivers, gbc);

      gbc = new GridBagConstraints(1,1,1,1,1,0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL, new Insets(0,0,0,5), 0,0);
      ret.add(cboDriversToAssign, gbc);

      gbc = new GridBagConstraints(2,1,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE, new Insets(0,0,0,5), 0,0);
      btnAssign = new JButton(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.LOCK));
      btnAssign.setToolTipText(s_stringMgr.getString("AssignDriversDlg.assign.button.tooltip"));
      ret.add(btnAssign, gbc);

      return ret;
   }

   private JPanel createButtonPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST,GridBagConstraints.NONE, new Insets(0,0,0,5), 0,0);
      ret.add(btnOk, gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.NORTHWEST,GridBagConstraints.NONE, new Insets(0,0,0,5), 0,0);
      ret.add(btnCancel, gbc);

      return ret;
   }
}
