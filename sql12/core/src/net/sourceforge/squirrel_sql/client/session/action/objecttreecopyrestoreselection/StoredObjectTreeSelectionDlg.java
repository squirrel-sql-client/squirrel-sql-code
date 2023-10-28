package net.sourceforge.squirrel_sql.client.session.action.objecttreecopyrestoreselection;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;

public class StoredObjectTreeSelectionDlg extends JDialog
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(StoredObjectTreeSelectionDlg.class);

   JList<ObjectTreeSelectionStoreItemWrapper> lstObjectTreeSelections = new JList<>();
   JButton btnOk;
   JButton btnCancel;
   JButton btnDeleteSelected;

   public StoredObjectTreeSelectionDlg(Window owner)
   {
      super(owner, s_stringMgr.getString("StoredObjectTreeSelectionDlg.title"), ModalityType.APPLICATION_MODAL);

      getContentPane().setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(3,3,0,3), 0,0);
      getContentPane().add(new JScrollPane(lstObjectTreeSelections), gbc);

      gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(3,3,3,3), 0,0);
      getContentPane().add(createButtonPanel(), gbc);
   }

   private JPanel createButtonPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());
      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0);
      btnOk = new JButton(s_stringMgr.getString("StoredObjectTreeSelectionDlg.ok"));
      ret.add(btnOk, gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,5,0,0), 0,0);
      btnCancel = new JButton(s_stringMgr.getString("StoredObjectTreeSelectionDlg.cancel"));
      ret.add(btnCancel, gbc);

      gbc = new GridBagConstraints(2,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,30,0,0), 0,0);
      btnDeleteSelected = new JButton(s_stringMgr.getString("StoredObjectTreeSelectionDlg.delete.selected"));
      ret.add(btnDeleteSelected, gbc);

      return ret;
   }
}
