package net.sourceforge.squirrel_sql.fw.gui.copyseparatedby;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.IntegerField;
import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class CopySeparatedByDlg extends JDialog
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(CopySeparatedByDlg.class);

   JTextField txtCellSeparator = new JTextField();
   JLabel _lblRowSeparator = new JLabel(s_stringMgr.getString("CopySeparatedByDlg.row.separator"));
   JTextField txtRowSeparator = new JTextField();
   IntegerField txtLineLength = new IntegerField(6);
   JButton btnOk = new JButton(s_stringMgr.getString("CopySeparatedByDlg.cell.ok"));
   JButton btnCancel = new JButton(s_stringMgr.getString("CopySeparatedByDlg.cell.cancel"));


   public CopySeparatedByDlg(Frame owningFrame)
   {
      super(owningFrame, s_stringMgr.getString("CopySeparatedByDlg.title"), true);

      getContentPane().setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0, 0 , 1, 1, 1,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,5,5,5), 0,0);
      getContentPane().add(new MultipleLineLabel(s_stringMgr.getString("CopySeparatedByDlg.description")), gbc);

      gbc = new GridBagConstraints(0, 1 , 1, 1, 0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,5,5,5), 0,0);
      getContentPane().add(createControlsPanel(), gbc);

      gbc = new GridBagConstraints(0, 2 , 1, 1, 0,1, GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE, new Insets(0,5,5,5), 0,0);
      getContentPane().add(createButtoPanel(), gbc);

      setSize(350, 250);

      GUIUtils.centerWithinParent(this);

      GUIUtils.enableCloseByEscape(this);

      getRootPane().setDefaultButton(btnOk);
   }

   private JPanel createButtoPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0, 0 , 1, 1, 0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      ret.add(btnOk, gbc);

      gbc = new GridBagConstraints(1, 0 , 1, 1, 0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      ret.add(btnCancel, gbc);


      return ret;
   }

   private JPanel createControlsPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0, 0 , 1, 1, 0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      ret.add(new JLabel(s_stringMgr.getString("CopySeparatedByDlg.cell.separator")), gbc);

      gbc = new GridBagConstraints(1, 0 , 1, 1, 1,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0,0);
      //txtCellSeparator.setPreferredSize(new Dimension(70, txtCellSeparator.getPreferredSize().height));
      ret.add(txtCellSeparator, gbc);


      gbc = new GridBagConstraints(0, 1 , 1, 1, 0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      ret.add(_lblRowSeparator, gbc);

      gbc = new GridBagConstraints(1, 1 , 1, 1, 1,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0,0);
      //txtRowSeparator.setPreferredSize(new Dimension(70, txtRowSeparator.getPreferredSize().height));
      ret.add(txtRowSeparator, gbc);

      gbc = new GridBagConstraints(0, 2 , 1, 1, 0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      ret.add(new JLabel(s_stringMgr.getString("CopySeparatedByDlg.prefered.line.length")), gbc);

      gbc = new GridBagConstraints(1, 2 , 1, 1, 0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      txtLineLength.setPreferredSize(new Dimension(70, txtLineLength.getPreferredSize().height));
      ret.add(txtLineLength, gbc);


//      gbc = new GridBagConstraints(2, 2 , 1, 1, 1,1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0);
//      ret.add(new JPanel(), gbc);


      return ret;
   }
}
