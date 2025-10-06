package net.sourceforge.squirrel_sql.plugins.sqlbookmark;

import java.awt.Color;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;

public class AddBookmarkDialog extends JDialog
{
   private static final String BM_TITLE = "dialog.add.title";
   private static final String BM_NAME = "dialog.add.name";
   private static final String BM_DESCRIPTION = "dialog.add.description";
   private static final String BM_ENTER_NAME = "dialog.add.entername";
   private static final String BM_ENTER_DESCRIPTION = "dialog.add.enterdescription";
   public static final String BM_ACCESS_HINT = "dialog.add.accesshint";

   private JTextField txtName = new JTextField();
   private JTextField txtDescription = new JTextField();
   private JButton btnOK;
   private JButton btnCancel;
   private static final String BM_CANCEL = "dialog.add.cancel";
   private static final String BM_OK = "dialog.add.ok";
   private SQLBookmarkPlugin plugin;
   private boolean ok;


   public AddBookmarkDialog(Frame frame, SQLBookmarkPlugin plugin)
   {
      super(frame, plugin.getResourceString(BM_TITLE),true);
      this.plugin = plugin;

      createUI();

      btnOK.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onOK();
         }
      });

      btnCancel.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onCancel();
         }
      });
   }

   private void onCancel()
   {
      closeDialog();
   }

   private void onOK()
   {
      String name = txtName.getText();
      if(null == name || 0 == txtName.getText().trim().length() || containsWhiteSpaces(name))
      {
         JOptionPane.showMessageDialog(this, plugin.getResourceString(BM_ENTER_NAME));
         return;
      }

      String description = txtDescription.getText();
      if(null == description || 0 == txtDescription.getText().trim().length())
      {
         JOptionPane.showMessageDialog(this, plugin.getResourceString(BM_ENTER_DESCRIPTION));
         return;
      }


      ok = true;

      closeDialog();
   }

   private boolean containsWhiteSpaces(String name)
   {
      for (int i = 0;  i < name.length(); ++i)
      {
         if(Character.isWhitespace(name.charAt(i)))
         {
            return true;
         }
      }
      return false;

   }


   private void closeDialog()
   {
      setVisible(false);
      dispose();
   }

   private void createUI()
   {
      getContentPane().setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0.0,0.0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,0,0),0,0);
      getContentPane().add(new JLabel(plugin.getResourceString(BM_NAME)), gbc);

      gbc = new GridBagConstraints(1,0,1,1,1.0,0.0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,0,5),0,0);
      getContentPane().add(txtName, gbc);

      gbc = new GridBagConstraints(0,1,1,1,0.0,0.0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,0,0),0,0);
      getContentPane().add(new JLabel(plugin.getResourceString(BM_DESCRIPTION)), gbc);

      gbc = new GridBagConstraints(1,1,1,1,1.0,0.0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,0,5),0,0);
      getContentPane().add(txtDescription, gbc);


      gbc = new GridBagConstraints(0,2,2,1,1.0,0.0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,0,5),0,0);
      JLabel lblAccesshint = new JLabel(plugin.getResourceString(BM_ACCESS_HINT));
      lblAccesshint.setForeground(Color.red);
      getContentPane().add(lblAccesshint, gbc);

      gbc = new GridBagConstraints(1,3,1,1,1.0,0.0,GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(10,5,5,5),0,0);
      getContentPane().add(createLowerButtonsPanel(), gbc);

      getRootPane().setDefaultButton(btnOK);

      txtName.requestFocus();

      GUIUtils.enableCloseByEscape(this);

      setSize(430, 150);
   }

   private JPanel createLowerButtonsPanel()
   {
      GridBagConstraints gbc;
      JPanel pnlButtons = new JPanel(new GridBagLayout());

      btnOK = new JButton(plugin.getResourceString(BM_OK));
      gbc = new GridBagConstraints(0,0,1,1,0.0,0.0,GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0,0,0,5),0,0);
      pnlButtons.add(btnOK, gbc);

      btnCancel = new JButton(plugin.getResourceString(BM_CANCEL));
      gbc = new GridBagConstraints(1,0,1,1,0.0,0.0,GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0,0,0,0),0,0);
      pnlButtons.add(btnCancel, gbc);
      return pnlButtons;
   }


   public boolean isOK()
   {
      return ok;
   }

   public String getDescription()
   {
      return txtDescription.getText().trim();
   }

   public String getBookmarkName()
   {
      return txtName.getText().trim();
   }

   public void requestFocusLater()
   {
      SwingUtilities.invokeLater(() -> txtName.requestFocus());
   }
}
