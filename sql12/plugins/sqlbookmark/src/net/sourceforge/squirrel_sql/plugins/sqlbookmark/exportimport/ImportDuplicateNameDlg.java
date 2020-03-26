package net.sourceforge.squirrel_sql.plugins.sqlbookmark.exportimport;

import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;

public class ImportDuplicateNameDlg extends JDialog
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ImportDuplicateNameDlg.class);

   JRadioButton radIgnore;
   JRadioButton radUpdate;
   JRadioButton radCopy;
   JButton btnOk;
   JButton btnCancel;

   public ImportDuplicateNameDlg(Window owningWindow)
   {
      super(owningWindow, s_stringMgr.getString("ImportDuplicateNameDlg.title"), JDialog.DEFAULT_MODALITY_TYPE);

      getContentPane().setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,1,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,0,5), 0,0);
      getContentPane().add(new MultipleLineLabel(s_stringMgr.getString("ImportDuplicateNameDlg.message")), gbc);

      gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,0,5), 0,0);
      radIgnore = new JRadioButton(s_stringMgr.getString("ImportDuplicateNameDlg.ignore.bookmarks"));
      getContentPane().add(radIgnore, gbc);

      gbc = new GridBagConstraints(0,2,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(2,5,0,5), 0,0);
      radCopy = new JRadioButton(s_stringMgr.getString("ImportDuplicateNameDlg.copy.bookmarks"));
      getContentPane().add(radCopy, gbc);

      gbc = new GridBagConstraints(0,3,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(2,5,0,5), 0,0);
      radUpdate = new JRadioButton(s_stringMgr.getString("ImportDuplicateNameDlg.update.bookmarks"));
      getContentPane().add(radUpdate, gbc);


      gbc = new GridBagConstraints(0,4,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10,5,5,5), 0,0);
      getContentPane().add(createButtonPane(), gbc);

      ButtonGroup buttonGroup = new ButtonGroup();
      buttonGroup.add(radIgnore);
      buttonGroup.add(radCopy);
      buttonGroup.add(radUpdate);
   }

   private JPanel createButtonPane()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0);
      btnOk = new JButton(s_stringMgr.getString("ImportDuplicateNameDlg.ok"));
      ret.add(btnOk, gbc);

      getRootPane().setDefaultButton(btnOk);

      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,5,0,0), 0,0);
      btnCancel = new JButton(s_stringMgr.getString("ImportDuplicateNameDlg.cancel"));
      ret.add(btnCancel, gbc);

      return ret;
   }
}
