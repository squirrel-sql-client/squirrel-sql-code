package net.sourceforge.squirrel_sql.plugins.dbcopy.actions;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class EditPasteTableNameDialog extends JDialog
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(EditPasteTableNameDialog.class);


   JTextField txtTableName = new JTextField();
   JTextArea txtWhere = new JTextArea();

   JButton btnOK = new JButton(s_stringMgr.getString("EditPasteTableNameDlg.OK"));
   JButton btnCancel = new JButton(s_stringMgr.getString("EditPasteTableNameDlg.Cancel"));

   public EditPasteTableNameDialog(Frame owner, String destTableName)
   {
      super(owner, s_stringMgr.getString("EditPasteTableNameDlg.title"), true);
      createUI(destTableName);

      GUIUtils.enableCloseByEscape(this);

      getRootPane().setDefaultButton(btnOK);
      setSize(450, 400);

   }

   private void createUI(String destTableName)
   {
      getContentPane().setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      int gridy = 0;

      gbc = new GridBagConstraints(0, gridy,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0,0);
      getContentPane().add(new MultipleLineLabel(s_stringMgr.getString("EditPasteTableNameDlg.text")), gbc);

      gbc = new GridBagConstraints(0,++gridy,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0,0);
      getContentPane().add(txtTableName, gbc);

      gbc = new GridBagConstraints(0,++gridy,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,5,5,5), 0,0);
      getContentPane().add(createTableNameLinkLabel(destTableName), gbc);


      gbc = new GridBagConstraints(0,++gridy,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(20,5,5,5), 0,0);
      getContentPane().add(new JLabel(s_stringMgr.getString("EditPasteTableNameDlg.where.label")), gbc);

      gbc = new GridBagConstraints(0,++gridy,1,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0,5,5,5), 0,0);
      getContentPane().add(txtWhere, gbc);

      gbc = new GridBagConstraints(0,++gridy,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10,5,5,5), 0,0);
      getContentPane().add(createButtonPanel(), gbc);
   }

   private JLabel createTableNameLinkLabel(String destTableName)
   {
      JLabel lblDestNameLink = new JLabel(s_stringMgr.getString("EditPasteTableNameDlg.htmlSetNameTo", destTableName));
      lblDestNameLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      lblDestNameLink.addMouseListener(new MouseAdapter()
      {
         @Override
         public void mouseClicked(MouseEvent e)
         {
            txtTableName.setText(destTableName);
         }
      });
      return lblDestNameLink;
   }

   private JPanel createButtonPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,0,5,5), 0,0);
      ret.add(btnOK, gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      ret.add(btnCancel, gbc);

      return ret;
   }

}
