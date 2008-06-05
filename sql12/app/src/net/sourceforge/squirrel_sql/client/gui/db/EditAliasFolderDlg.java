package net.sourceforge.squirrel_sql.client.gui.db;

import net.sourceforge.squirrel_sql.client.gui.mainframe.MainFrame;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class EditAliasFolderDlg extends JDialog
{
   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(EditAliasFolderDlg.class);



   private JTextField _txtFolderName = new JTextField();

   private JButton _btnOK = new JButton(s_stringMgr.getString("EditAliasFolderDlg.OK"));
   private JButton _btnCancel = new JButton(s_stringMgr.getString("EditAliasFolderDlg.Cancel"));

   private String _folderName;


   public EditAliasFolderDlg(MainFrame mainFrame, String title, String text)
   {
      super(mainFrame, title, true);
      createUI(text);

      _btnOK.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onOK();
         }
      });

      _btnCancel.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onCancel();
         }
      });

      setSize(400, 150);

   }

   private void onCancel()
   {
      close();
   }

   private void close()
   {
      setVisible(false);
      dispose();
   }

   private void onOK()
   {
      if(null == _txtFolderName.getText() || 0 == _txtFolderName.getText().trim().length())
      {
         JOptionPane.showConfirmDialog(this,s_stringMgr.getString("EditAliasFolderDlg.FolderNameEmpty"));
         return;
      }

      _folderName = _txtFolderName.getText();

      close();
   }


   private void createUI(String text)
   {
      getContentPane().setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      getContentPane().add(new JLabel(text), gbc);

      gbc = new GridBagConstraints(0,1,1,1,1,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0,0);
      getContentPane().add(_txtFolderName, gbc);

      gbc = new GridBagConstraints(0,2,1,1,1,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      getContentPane().add(createButtonPanel(), gbc);
   }

   private JPanel createButtonPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      ret.add(_btnOK, gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      ret.add(_btnCancel, gbc);

      return ret;
   }

   public String getNewFolderName()
   {
      return _folderName;
   }
}
