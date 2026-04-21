package net.sourceforge.squirrel_sql.client.session.editexternal;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import net.sourceforge.squirrel_sql.fw.gui.IntegerField;
import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class EditFileExternallyInitDlg extends JDialog
{
   private static StringManager s_stringMgr = StringManagerFactory.getStringManager(EditFileExternallyInitDlg.class);

   final IntegerField txtMillis = new IntegerField(6, 0);
   final JTextField txtCommand = new JTextField();
   final JButton btnOk = new JButton(s_stringMgr.getString("EditFileExternallyInitDlg.ok"));
   final JButton btnCancel = new JButton(s_stringMgr.getString("EditFileExternallyInitDlg.cancel"));
   final JRadioButton radStartsAtZero = new JRadioButton(s_stringMgr.getString("EditFileExternallyInitDlg.line_col.numbering.starts.at.zero"));
   final JRadioButton radStartsAtOne = new JRadioButton(s_stringMgr.getString("EditFileExternallyInitDlg.line_col.numbering.starts.at.one"));


   public EditFileExternallyInitDlg(Frame owningFrame)
   {
      super(owningFrame, s_stringMgr.getString("EditFileExternallyInitDlg.title"), true);

      getContentPane().setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,0,0), 0,0);
      getContentPane().add(new JLabel(s_stringMgr.getString("EditFileExternallyInitDlg.editor.refresh.millis")), gbc);

      gbc = new GridBagConstraints(1,0,2,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,0,40), 0,0);
      getContentPane().add(txtMillis, gbc);


      gbc = new GridBagConstraints(0, 1, 2, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 0), 0, 0);
      getContentPane().add(createNumberingPanel(), gbc);

      gbc = new GridBagConstraints(0,2,2,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(15,5,0,5), 0,0);
      getContentPane().add(new MultipleLineLabel(s_stringMgr.getString("EditFileExternallyInitDlg.command")), gbc);

      gbc = new GridBagConstraints(0,3,2,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,0,5), 0,0);
      getContentPane().add(txtCommand, gbc);

      gbc = new GridBagConstraints(0,4,2,1,0,0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(20,0,5,0), 0,0);
      getContentPane().add(createOkCancelPanel(), gbc);

      gbc = new GridBagConstraints(0,5,2,1,1,1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0,0);
      getContentPane().add(new JPanel(), gbc);

      getRootPane().setDefaultButton(btnOk);

   }

   private JPanel createNumberingPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;
      gbc = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
      ret.add(new JLabel(s_stringMgr.getString("EditFileExternallyInitDlg.line_col.numbering.starts.at")), gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,5,0,0), 0,0);
      ret.add(radStartsAtZero, gbc);

      gbc = new GridBagConstraints(2,0,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0);
      ret.add(radStartsAtOne, gbc);

      ButtonGroup bg = new ButtonGroup();
      bg.add(radStartsAtZero);
      bg.add(radStartsAtOne);

      return ret;
   }

   private JPanel createOkCancelPanel()
   {
      JPanel ret = new JPanel(new GridLayout(1,2,10,0));
      ret.add(btnOk);
      ret.add(btnCancel);
      return ret;
   }
}
